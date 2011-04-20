package com.android.internal.widget;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.internal.R;

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
    
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    
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
        updateVisibility();
        
        
    }
	
	private void updateVisibility() {
		// TODO Auto-generated method stub

			if (mWasMusicActive) {
				//mHandle
				
      
            }
			else{

                // Set album art
                Uri uri = getArtworkUri(getContext(), SongId(),
           AlbumId());
                if (uri != null) {
                    mAlbumArt.setImageURI(uri);
                }
				
				
			}
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
	
	@Override
    protected void onAttachedToWindow() {     
	  if (DBG) log("Attching " + TAG + " to the window");
	  
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
	    	
	    
	    	void setPokeWakeLock(View v, int control);
	    
	    
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
	    
	    public static Uri getArtworkUri(Context context, long song_id, long album_id) {

	        if (album_id < 0) {
	            // This is something that is not in the database, so get the album art directly
	            // from the file.
	            if (song_id >= 0) {
	                return getArtworkUriFromFile(context, song_id, -1);
	            }
	            return null;
	        }

	       ContentResolver res = context.getContentResolver();
	        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
	        if (uri != null) {
	            InputStream in = null;
	            try {
	                in = res.openInputStream(uri);
	                return uri;
	            } catch (FileNotFoundException ex) {
	                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
	                // maybe it never existed to begin with.
	                return getArtworkUriFromFile(context, song_id, album_id);
	            } finally {
	                try {
	                    if (in != null) {
	                        in.close();
	                    }
	                } catch (IOException ex) {
	                }
	            }
	        }
	        return null;
	    }
	    private static Uri getArtworkUriFromFile(Context context, long songid, long albumid) {

	        if (albumid < 0 && songid < 0) {
	            return null;
	        }

	        try {
	            if (albumid < 0) {
	                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
	                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
	                if (pfd != null) {
	                    return uri;
	               }
	            } else {
	                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
	                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
	                if (pfd != null) {
	                    return uri;
	                }
	            }
	        } catch (FileNotFoundException ex) {
	        }
	        return null;
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
