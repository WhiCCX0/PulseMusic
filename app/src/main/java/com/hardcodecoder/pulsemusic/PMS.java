package com.hardcodecoder.pulsemusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.media.MediaBrowserService;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import com.hardcodecoder.pulsemusic.playback.LocalPlayback;
import com.hardcodecoder.pulsemusic.playback.MediaNotificationManager;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.List;

public class PMS extends MediaBrowserService implements PlaybackManager.PlaybackServiceCallback, MediaNotificationManager.NotificationCallback {

    private static final String TAG = "PMS";
    private MediaSession mMediaSession;
    private MediaNotificationManager mNotificationManager;
    private HandlerThread mServiceThread = null;
    private Handler mWorkerHandler;
    private boolean isServiceRunning = false;
    private boolean isReceiverRegistered = false;

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(getString(R.string.app_name), /* Name visible in Android Auto*/null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowser.MediaItem>> result) {
        result.sendResult(null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServiceThread = new HandlerThread("PMS Thread", Thread.NORM_PRIORITY);
        mServiceThread.start();
        mWorkerHandler = new Handler(mServiceThread.getLooper());
        mWorkerHandler.post(this::runOnServiceThread);
    }

    private void runOnServiceThread() {
        TrackManager mTrackManager = TrackManager.getInstance();
        LocalPlayback playback = new LocalPlayback(this, mTrackManager, mWorkerHandler);
        PlaybackManager mPlaybackManager = new PlaybackManager(this.getApplicationContext(), playback, mTrackManager, this/*, mWorkerHandler*/);

        mMediaSession = new MediaSession(this.getApplicationContext(), TAG);
        setSessionToken(mMediaSession.getSessionToken());
        mMediaSession.setCallback(mPlaybackManager.getSessionCallbacks(), mWorkerHandler);
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(getApplicationContext(), MediaButtonReceiver.class);
        PendingIntent mbrIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSession.setMediaButtonReceiver(mbrIntent);
        mNotificationManager = new MediaNotificationManager(this, this);
    }

    /* Never use START_STICKY here
     * The use case was -
     * --> open the app
     * --> pLay a song
     * --> remove the app from recent
     * --> relaunch the app by clicking on notification
     * --> pause the playback
     * --> remove the app from recent
     * --> Notification was started again and the service runs in the background, tapping on notification causes the app to crash
     *Reason --> The system was recreating the service {verified by logs in on create and onStartCommand)
     * and thus notification was getting posted with the old metadata
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(MediaSessionCompat.fromMediaSession(this, mMediaSession), intent);
        isServiceRunning = true;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isReceiverRegistered)
            mNotificationManager.unregisterControlsReceiver();
        mMediaSession.release();
        mServiceThread.quit();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!mMediaSession.isActive()) stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onPlaybackStart() {
        mMediaSession.setActive(true);
        if (!isServiceRunning)
            startService(new Intent(this.getApplicationContext(), PMS.class));
    }

    @Override
    public void onPlaybackStopped() {
        mMediaSession.setActive(false);
        isServiceRunning = false;
    }

    @Override
    public void onStartNotification() {
        mNotificationManager.startNotification();
    }

    @Override
    public void onStopNotification() {
        mNotificationManager.stopNotification();
    }

    @Override
    public void onPlaybackStateChanged(PlaybackState newState) {
        mMediaSession.setPlaybackState(newState);
    }

    @Override
    public void onMetaDataChanged(MediaMetadata newMetaData) {
        mMediaSession.setMetadata(newMetaData);
    }

    @Override
    public MediaSession.Token getMediaSessionToken() {
        return getSessionToken();
    }

    @Override
    public void onNotificationStarted(int notificationId, Notification notification) {
        startForeground(notificationId, notification);
    }

    @Override
    public void onStopNotification(boolean removeNotification) {
        stopForeground(removeNotification);
    }

    @Override
    public void registerControlsReceiver(BroadcastReceiver controlsReceiver, IntentFilter filter) {
        registerReceiver(controlsReceiver, filter);
        isReceiverRegistered = true;
    }

    @Override
    public void unregisterControlsReceiver(BroadcastReceiver controlsReceiver) {
        unregisterReceiver(controlsReceiver);
        isReceiverRegistered = false;
    }
}
