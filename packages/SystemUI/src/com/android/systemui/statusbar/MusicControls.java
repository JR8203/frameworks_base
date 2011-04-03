package com.android.systemui.statusbar;

import com.android.systemui.R;
import com.android.systemui.statusbar.powerwidget.MediaKeyEventButton;

import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

public class MusicControls extends MediaKeyEventButton {

    private static final int MEDIA_STATE_UNKNOWN  = -1;
    private static final int MEDIA_STATE_INACTIVE =  0;
    private static final int MEDIA_STATE_ACTIVE   =  1;

    private int mCurrentState = MEDIA_STATE_UNKNOWN;

    @Override
    public void updateMusicControls() {

        View controlsView = mInflater.inflate(R.layout.music_controls);

        mState = STATE_DISABLED;

        if(isMusicActive()) {
	    setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    private boolean isMusicActive() {
        if(mCurrentState == MEDIA_STATE_UNKNOWN) {
            mCurrentState = MEDIA_STATE_INACTIVE;
            AudioManager am = getAudioManager(mView.getContext());
            if(am != null) {
                mCurrentState = (am.isMusicActive() ? MEDIA_STATE_ACTIVE : MEDIA_STATE_INACTIVE);
            }

            return (mCurrentState == MEDIA_STATE_ACTIVE);
        } else {
            boolean active = (mCurrentState == MEDIA_STATE_ACTIVE);
            mCurrentState = MEDIA_STATE_UNKNOWN;
            return active;
        }
    }
}
