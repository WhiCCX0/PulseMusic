package com.hardcodecoder.pulsemusic.activities.nowplaying;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.PMBActivity;
import com.hardcodecoder.pulsemusic.activities.nowplaying.screens.LandscapeModeNowPlayingScreen;
import com.hardcodecoder.pulsemusic.activities.nowplaying.screens.ModernNowPlayingScreen;
import com.hardcodecoder.pulsemusic.activities.nowplaying.screens.StylishNowPlayingScreen;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class NowPlayingScreenActivity extends PMBActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_open_exit);
        Fragment screenFragment;
        String tag;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            super.onCreate(null);
            screenFragment = LandscapeModeNowPlayingScreen.getInstance();
            tag = LandscapeModeNowPlayingScreen.TAG;
        } else {
            super.onCreate(savedInstanceState);
            int id = AppSettings.getNowPlayingScreenStyle(this);
            switch (id) {
                case Preferences.NOW_PLAYING_SCREEN_STYLISH:
                    screenFragment = StylishNowPlayingScreen.getInstance();
                    tag = StylishNowPlayingScreen.TAG;
                    break;
                case Preferences.NOW_PLAYING_SCREEN_MODERN:
                default:
                    screenFragment = ModernNowPlayingScreen.getInstance();
                    tag = ModernNowPlayingScreen.TAG;
            }
        }
        setContentView(R.layout.activity_now_playing_screen);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.now_playing_screen_container, screenFragment, tag)
                .commit();
    }

    private void overrideExitTransition() {
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_slide_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overrideExitTransition();
    }

    @Override
    public void finish() {
        super.finish();
        overrideExitTransition();
    }
}
