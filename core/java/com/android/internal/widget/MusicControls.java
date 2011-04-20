package com.android.internal.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MusicControls extends LinearLayout{

	
	 public static final int HORIZONTAL = 0;
	    public static final int VERTICAL = 1;

		private String TAG = "LockMusicControls";
		private static final boolean DBG = true;
	    private static final boolean VISUAL_DEBUG = true;
		
	
	onMusicControlsListener mMusicControlsListener;
	    
	boolean mWasMusicActive = false;    
	private Context mContext;
	
    // UI elements
    private MusicControls mControls;
    private ImageView mHandle;
    private ImageView mAlbumArt;
    private ImageView mPlayButton;
    private ImageView mPauseButton;
    private ImageView mSkipButton;
    private ImageView mSeekButton;
    

	// Albums stats
	private static String mArtist = "";
	private static String mTrack = "";
	private static Boolean mPlaying = false;
	private static long mSongId = 0;
	private static long mAlbumId = 0;
	
	
	public MusicControls(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public MusicControls(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        
        
        mContext = this.getContext();
        
        mControls = (MusicControls) findViewById(R.id.music_controls);
        mHandle = new ImageView(mContext);
        mAlbumArt = new ImageView(mContext);
        mPlayButton = new ImageView(mContext);
        mPauseButton = new ImageView(mContext);
        mSkipButton = new ImageView(mContext);
        mSeekButton = new ImageView(mContext);
       
        setListners();
        
        
    }
	
	private void setListners() {
		// TODO Auto-generated method stub
		
		 mHandle.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {	                
	                // Send animation event to remove view
	                mWasMusicActive = true;
	            }
	        });
		
		 mAlbumArt.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {	                
	                sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
	                mWasMusicActive = true;
	            }
	        });
		 
		
		 mPlayButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
	                mWasMusicActive = false;
	                                
	            }
	        });
		 
		 
		 
		 mPauseButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {	                
	                sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
	                mWasMusicActive = true;
	            }
	        });
		 
		 mSkipButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {	                
	                sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
	                mWasMusicActive = true;
	            }
	        });
		 
		 mSeekButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {	                
	                sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
	                mWasMusicActive = true;
	            }
	        });
		 
		 
		
	}

	public void setOnMusicControlsListner( onMusicControlsListener l){
		 mMusicControlsListener = l;
	
	
	}
	
	 // Broadcast receiver to determine if the music state has changed
	 // 
	 private BroadcastReceiver mMusicReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				         String action = intent.getAction();
		            mArtist = intent.getStringExtra("artist");
		            mTrack = intent.getStringExtra("track");
		            mPlaying = intent.getBooleanExtra("playing", false);
		            mSongId = intent.getLongExtra("songid", 0);
		            mAlbumId = intent.getLongExtra("albumid", 0);
		            
		            // Update the lock screen music controls here
		            //intent = new Intent("internal.policy.impl.updateSongStatus");
		            
		            // Send the broadcast signaling that the lockscreen should update the controls
		            //context.sendBroadcast(intent);
			}

	    };
	    
	    public interface onMusicControlsListener{
	    	
	    
	    	void setMusicControl(View v, int control);
	    
	    
	    }
	  

	    
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
	    
	    
		   public void sendMediaButtonEvent(int code) {
			   

				  if (DBG) log("sending media button event");
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
	
		   
		// Debugging / testing code

			private void log(String msg) {
			    Log.d(TAG, msg);
			}
			    
		   

}
