package com.android.internal.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LockMusicControls extends View{

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

	private String TAG = "LockMusicControls";
	private static final boolean DBG = false;
	
	
	// Listener for onDialTrigger() callbacks.
    private OnMusicVisibleListener mOnMusicVisibleListener;
		
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
	public static final int PUASE_PRESSED = 11;
	public static final int SKIP_PRESSED = 12;
	public static final int SEEK_PRESSED = 13;

    
    
    /**
     * Whether the user has triggered something (e.g dragging the left handle all the way over to
     * the right).
     */
    private boolean mTriggered = false;


	
	
	public LockMusicControls(Context context){
	     this(context, null);
	     
	     
	
		
		
	}
	
	public LockMusicControls(Context context, AttributeSet attrs)  {
		 super(context,attrs);
	}
	
	  @Override
	    protected void onFinishInflate() {
	        super.onFinishInflate();
	        // Set the deafault inflation space
	   
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
	

	    public static String NowPlayingArtist() {
	        if (mArtist != null && mPlaying) {
	            return (mArtist);
	        } else {
	            return "";
	        }
	    }

	    public static String NowPlayingAlbum() {
	        if (mArtist != null && mPlaying) {
	            return (mTrack);
	        } else {
	            return "";
	        }
	    }

	    public static long SongId() {
	        return mSongId;
	    }

	    public static long AlbumId() {
	        return mAlbumId;
	    }
	
	    /**
	     * Sets the current grabbed state, and dispatches a grabbed state change
	     * event to our listener.
	     */
	    private void setGrabbedState(int newState) {
	        if (newState != mGrabbedState) {
	            mGrabbedState = newState;
	            if (mOnMusicVisibleListener != null) {
	            	mOnMusicVisibleListener.onGrabbedStateChange(this, mGrabbedState);
	            }
	        }
	    }
	    
	    public interface OnMusicControlsListener{
	    	
	    	/**
	    	 * The music controls play button was pressed 
	    	 */
	    	public static final int PLAY = 10;
	    	/**
	    	 * The music controls pause button was pressed 
	    	 */
	    	public static final int PUASE = 11;
	    	/**
	    	 * The music controls play button was pressed 
	    	 */
	    	public static final int SKIP = 12;
	    	/**
	    	 * The music controls play button was pressed 
	    	 */
	    	public static final int SEEK = 13;
	    	
	    	
	    	/**
	         * Called when the music control is triggered.
	         *
	         * @param v The view that was triggered
	         * @param whichControl  Which "music control" the user pressed,
	         * either {@link #PLAY}, {@link #PUASE}, 
	         * {@link #SKIP}, or {@link #SEEK}.
	         */
	        void onControlTrigger(View v, int whichControl);

	    	
	        /**
	         * Called when one of the music button changes (i.e. when
	         * the user either plays, pauses, seeks or skips a track.)
	         *
	         * @param v the view that was triggered
	         * @param grabbedState the new state: either {@link #PLAY_PRESSED},
	         * {@link #PUASE_PRESSED}, {@link #SKIP_PRESSED}, or {@link #SEEK_PRESSED}.
	         */
	    	  void onMusicButtonStateChange(View v, int musicstate);
	    	
	    	
	    	
	    }
	    
	    public interface OnMusicVisibleListener{
	    
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
	         * Called when the dial is triggered.
	         *
	         * @param v The view that was triggered
	         * @param whichHandle  Which "dial handle" the user grabbed,
	         *        either {@link #LEFT_HANDLE}, {@link #RIGHT_HANDLE}.
	         */
	        void onHandleTrigger(View v, int whichHandle);

	        /**
	         * Called when the "grabbed state" changes (i.e. when
	         * the user either grabs or releases one of the handles.)
	         *
	         * @param v the view that was triggered
	         * @param grabbedState the new state: either {@link #NOTHING_GRABBED},
	         * {@link #LEFT_HANDLE_GRABBED}, or {@link #RIGHT_HANDLE_GRABBED}.
	         */
	        void onGrabbedStateChange(View v, int grabbedState);
	    
	    
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
	                break;
	            case MotionEvent.ACTION_MOVE:
	                break;
	            case MotionEvent.ACTION_UP:
	                break;
	            case MotionEvent.ACTION_CANCEL:
	            	break;
	         }
	         return true;
	    
	    
	    }
	    		
	    	
	    
	    

// Debugging / testing code

	private void log(String msg) {
	    Log.d(TAG, msg);
	}
	    
	
}
