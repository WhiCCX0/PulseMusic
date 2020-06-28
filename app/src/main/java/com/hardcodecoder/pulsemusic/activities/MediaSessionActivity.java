package com.hardcodecoder.pulsemusic.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.PMS.ServiceBinder;

public abstract class MediaSessionActivity extends PMBActivity {

    private MediaController mController = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            ServiceBinder serviceBinder = (ServiceBinder) binder;
            mController = serviceBinder.getMediaController();
            setMediaController(mController);
            onMediaServiceConnected(mController);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToService();
    }

    public void connectToService() {
        Intent intent = new Intent(this, PMS.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void playMedia() {
        if (null != mController) mController.getTransportControls().play();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    public abstract void onMediaServiceConnected(MediaController controller);
}
