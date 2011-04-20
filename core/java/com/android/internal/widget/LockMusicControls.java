package com.android.internal.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;

import com.android.internal.R;

public class LockMusicControls extends View {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

	private String TAG = "LockMusicControls";
	private static final boolean DBG = true;
    private static final boolean VISUAL_DEBUG = true;
	
	
	// Listener for onMusic*Listeners() callbacks.
    private OnMusicTriggerListener mOnMusicTriggerListener; 
    
    final Matrix mBgMatrix = new Matrix();
    private float mDensity;
    
    // UI elements
    private Bitmap mBackground;
    private Bitmap mAlbumArt;
    private Bitmap mPlayButton;
    private Bitmap mPauseButton;
    private Bitmap mSkipButton;
    private Bitmap mSeekButton;
    

    private int mBackgroundWidth;
    private int mBackgroundHeight;
    
	// Albums stats
	private static String mArtist = "";
	private static String mTrack = "";
	private static Boolean mPlaying = false;
	private static long mSongId = 0;
	private static long mAlbumId = 0;
	
	private static Context mContext;
	  
	/**
     * Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */	
	 private int mOrientation;
	 
	// true if the music controls are hidden
	private boolean mHidden = false;
	
	 private Paint mPaint = new Paint();
	
	 /**
     * If the user is currently dragging something.
     */
    private int mGrabbedState = NOTHING_GRABBED;
    public static final int NOTHING_GRABBED = 0;
    public static final int LEFT_HANDLE_GRABBED = 1;
    public static final int RIGHT_HANDLE_GRABBED = 2;
    
  	/**
	 * If the user selected a music control
	 */
	public static final int PLAY_PRESSED = 10;
	public static final int PAUSE_PRESSED = 11;
	public static final int SKIP_PRESSED = 12;
	public static final int SEEK_PRESSED = 13;

    
	 // positions of the left and right handle
    private int mLeftHandleX;
    private int mRightHandleX;

    
    
    
    
	
    private Vibrator mVibrator;
    
    /**
     * Whether the user has triggered something (e.g dragging the left handle all the way over to
     * the right).
     */
    private boolean mTriggered = false;


    /**
     * How far from the edge of the screen the user must drag to trigger the event.
     */
    private static final int EDGE_TRIGGER_DIP = 100;
    
    
	
	public LockMusicControls(Context context){
	     this(context, null);
	}
	
	
	/**
     * Constructor used when this widget is created from a layout file.
     */
	public LockMusicControls(Context context, AttributeSet attrs)  {
		 super(context,attrs);
		 
		 // Set the widget layout structure
		 
		 TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MusicControls);
	     mOrientation = a.getInt(R.styleable.MusicControls_orientation, HORIZONTAL);
	     a.recycle();
	     
	     Resources r = getResources();
	        mDensity = r.getDisplayMetrics().density;
	        if (DBG) log("- Density: " + mDensity);
	     
	     // Set the background of the music widget
	     // This should not be completely transparent
	     // And should be a .9 to stretch
	        
	     mBackground = this.getBitmapFor(R.drawable.lock_ic_media_bg);
	     mAlbumArt = this.getBitmapFor(R.drawable.lock_ic_default_artwork);
	     mPlayButton = this.getBitmapFor(R.drawable.lock_ic_media_play);
	     mPauseButton = this.getBitmapFor(R.drawable.lock_ic_media_pause);
	     mSkipButton = this.getBitmapFor(R.drawable.lock_ic_media_next);
	     mSeekButton = this.getBitmapFor(R.drawable.lock_ic_media_previous);
		 
	     mBackgroundWidth = mBackground.getWidth();
	        mBackgroundHeight = mBackground.getHeight();
		 
	}
	
    private Bitmap getBitmapFor(int resId) {
        return BitmapFactory.decodeResource(getContext().getResources(), resId);
    }
    
	
    private boolean isHoriz() {
        return mOrientation == HORIZONTAL;
    }
	  @Override
	    protected void onFinishInflate() {
	        super.onFinishInflate();
	        // Set the default inflation space
	   
	    }

	  
	  @Override
	    protected void onAttachedToWindow() {     
		  
		  	 mContext = this.getContext();
			
			 IntentFilter iF = new IntentFilter();
			 iF.addAction("com.android.music.metachanged");
			 iF.addAction("com.android.music.playstatechanged");
			 
			 // Register if the music play state has change
			 mContext.registerReceiver(mMusicReceiver, iF);
	  }
	
	 // Broadcast receiver to determine if the music state has changed
	 // 
	 private BroadcastReceiver mMusicReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				         String action = intent.getAction();
		            mArtist = intent.getStringExtra("artist");
		            mTrack = intent.getStringExtra("track");
		            mPlaying = intent.getBooleanExtra("playing", false);
		            mSongId = intent.getLongExtra("songid", 0);
		            mAlbumId = intent.getLongExtra("albumid", 0);
		            
		            // Update the lock screen music controls here
		            intent = new Intent("internal.policy.impl.updateSongStatus");
		            
		            // Send the broadcast signaling that the lockscreen should update the controls
		            context.sendBroadcast(intent);
			}

	    };
	

	    private static String NowPlayingArtist() {
	        if (mArtist != null && mPlaying) {
	            return (mArtist);
	        } else {
	            return "";
	        }
	    }

	    private static String NowPlayingAlbum() {
	        if (mArtist != null && mPlaying) {
	            return (mTrack);
	        } else {
	            return "";
	        }
	    }

	    private static long SongId() {
	        return mSongId;
	    }

	    private static long AlbumId() {
	        return mAlbumId;
	    }
	
	    /**
	     * Sets the current button pressed state, and dispatches a pressed button state change
	     * event to our listener.
	     */
	    private void setMusicButtonStateChanged(int musicstate){
	    	
	    	if (mOnMusicTriggerListener != null) {
	    		mOnMusicTriggerListener.onMusicButtonStateChange(this, musicstate);
	    	}
	    	
	    }
	    
	    /**
	     * Sets the current grabbed state, and dispatches a grabbed state change
	     * event to our listener.
	     */
	    private void setGrabbedState(int newState) {
	        if (newState != mGrabbedState) {
	            mGrabbedState = newState;
	            if (mOnMusicTriggerListener != null) {
	            	mOnMusicTriggerListener.onMusicGrabbedStateChange(this, mGrabbedState);
	            }
	        }
	    }
	    
	    public interface OnMusicTriggerListener{
	    	
	 	   /**
	         * The music widget was triggered because the user grabbed the left handle,
	         * and moved the handle to the right.
	         */
	        public static final int LEFT_HANDLE = 1;

	        /**
	         * The music widget was triggered because the user grabbed the right handle,
	         * and moved the handle to the left.
	         */
	        public static final int RIGHT_HANDLE = 2;
	    
	    	
	    	/**
	    	 * The music controls play button was pressed 
	    	 */
	    	public static final int PLAY = 10;
	    	/**
	    	 * The music controls pause button was pressed 
	    	 */
	    	public static final int PAUSE = 11;
	    	/**
	    	 * The music controls play button was pressed 
	    	 */
	    	public static final int SKIP = 12;
	    	/**
	    	 * The music controls play button was pressed 
	    	 */
	    	public static final int SEEK = 13;
	    	
	    	
	    	
	        /**
	         * Called when one of the music button changes (i.e. when
	         * the user either plays, pauses, seeks or skips a track.)
	         *
	         * @param v the view that was triggered
	         * @param grabbedState the new state: either {@link #PLAY_PRESSED},
	         * {@link #PUASE_PRESSED}, {@link #SKIP_PRESSED}, or {@link #SEEK_PRESSED}.
	         */
	    	  void onMusicButtonStateChange(View v, int musicstate);
	    	   
	    	  /**
		         * Called when the dial is triggered.
		         *
		         * @param v The view that was triggered
		         * @param whichHandle  Which "dial handle" the user grabbed,
		         *        either {@link #LEFT_HANDLE}, {@link #RIGHT_HANDLE}.
		         */
		        void onMusicHandleTrigger(View v, int whichHandle);

		        /**
		         * Called when the "grabbed state" changes (i.e. when
		         * the user either grabs or releases one of the handles.)
		         *
		         * @param v the view that was triggered
		         * @param grabbedState the new state: either {@link #NOTHING_GRABBED},
		         * {@link #LEFT_HANDLE_GRABBED}, or {@link #RIGHT_HANDLE_GRABBED}.
		         */
		        void onMusicGrabbedStateChange(View v, int grabbedState);
		    
		        /**
		         * Called when the music control is triggered.
		         *
		         * @param v The view that was triggered
		         * @param whichControl  Which "music control" the user pressed,
		         * either {@link #PLAY}, {@link #PUASE}, 
		         * {@link #SKIP}, or {@link #SEEK}.
		         */
			   void onMusicControlTrigger(View v, int whichControl);
	    	
	    }
	    
	   public void sendMediaButtonEvent(int code) {
	        long eventtime = SystemClock.uptimeMillis();

	        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
	        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, code, 0);
	        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
	        getContext().sendOrderedBroadcast(downIntent, null);

	        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
	        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, code, 0);
	        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
	        getContext().sendOrderedBroadcast(upIntent, null);
	    }
	
	    
	 
		
        @Override 
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        	   
        	final int length = isHoriz() ?
                    MeasureSpec.getSize(widthMeasureSpec) :
                    MeasureSpec.getSize(heightMeasureSpec);
                    
        	final int height = mBackgroundHeight;

               if (isHoriz()) {
                   setMeasuredDimension(length, height);
               } else {
                   setMeasuredDimension(height, length);
               }
           }
    	
	 
	    
	    @Override
	    protected void onDraw(Canvas canvas){
	    	  super.onDraw(canvas);

	          final int width = getWidth();

	          if (VISUAL_DEBUG) {
	              // draw bounding box around widget
	              mPaint.setColor(0xffff0000);
	              mPaint.setStyle(Paint.Style.STROKE);
	              canvas.drawRect(0, 0, width, getHeight(), mPaint);
	          }
	          
	          // Background: this should be a .9 to stretch
	          //canvas.drawBitmap(mBackground, mBgMatrix, mPaint);
	          
	          // Draw music album
	          
	          // Draw music buttons
	          
	          
	          
	    	
	    	
	    }
	    /**
	     * Handle touch screen events.
	     *
	     * @param event The motion event.
	     * @return True if the event was handled, false otherwise.
	     */
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	    	
	    	 final int action = event.getAction();
	         switch (action) {
	         	case MotionEvent.ACTION_DOWN:
	         		if (DBG) log("touch-down");
	         		  mTriggered = false;
	                  if (mGrabbedState != NOTHING_GRABBED) {
	                      reset();
	                      invalidate();
	                      
	                  }
	                  if (mGrabbedState == PLAY_PRESSED){
	                	  // Send play broadcasts
	                	  setGrabbedState(LEFT_HANDLE_GRABBED);
	                	  
	                  }
	                  if (mGrabbedState == PAUSE_PRESSED){
	                	  // Send pause broadcast
	                	  
	                  }
	                  if (mGrabbedState == SKIP_PRESSED){
	                	  // Send skip  broadcast
	                	  
	                  }
	                  if (mGrabbedState == SEEK_PRESSED){
	                	  // Send seek broadcast
	                	  
	                  }
	                break;
	            case MotionEvent.ACTION_MOVE:
	                if (DBG) log("touch-move");
	                // This is where the slide animation to the left or right would occur
	            	invalidate();
	                break;
	            case MotionEvent.ACTION_UP:
	                if (DBG) log("touch-up");
	                // This is where the animation to "snap back" the left or right would occur
	            	invalidate();
	                break;
	            case MotionEvent.ACTION_CANCEL:
	            	 if (DBG) log("touch-cancel");
	                 reset();
	                 invalidate();
	            	break;
	         }
	         return true;
	    
	    
	    }
	    		
	    /**
	     * Registers a callback to be invoked when the music controls
	     * are "triggered" by sliding the view one way or the other
	     * or pressing the music control buttons.
	     *
	     * @param l the OnDialTriggerListener to attach to this view
	     */
	    public void setOnMusicTriggerListener(OnMusicTriggerListener l) {
	    	mOnMusicTriggerListener = l;
	    }
	    
	    /**
	     * Dispatches a trigger event to our listener.
	     */
	    private void dispatchTriggerEvent(int whichHandle) {
	        //vibrate(VIBRATE_LONG);
	        if (mOnMusicTriggerListener != null) {
	            
	        	if(whichHandle < OnMusicTriggerListener.PLAY)
	        		mOnMusicTriggerListener.onMusicControlTrigger(this, whichHandle);
	        	else	            
	        		mOnMusicTriggerListener.onMusicHandleTrigger(this, whichHandle);
	            
	        }
	    }
	    
	    /**
	     * Triggers haptic feedback.
	     */
	    private synchronized void vibrate(long duration) {
	        if (mVibrator == null) {
	            mVibrator = (android.os.Vibrator)
	                    getContext().getSystemService(Context.VIBRATOR_SERVICE);
	        }
	        mVibrator.vibrate(duration);
	    }
	    	
	    private void reset() {
	    	
	        //mAnimating = false;
	        setGrabbedState(NOTHING_GRABBED);
	        mTriggered = false;
	    }
	    
	    public void changeVisiblity(){
	    	
	    	if(!mHidden){
	    		setVisibility(VISIBLE);
	    		mHidden = false;
	    	}
	    	else {
	    		setVisibility(GONE);
	    		mHidden = true;
	    	}
	    	
	    }


// Debugging / testing code

	private void log(String msg) {
	    Log.d(TAG, msg);
	}
	    
	
}
