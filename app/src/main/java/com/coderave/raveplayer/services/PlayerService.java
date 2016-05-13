package com.coderave.raveplayer.services;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;

import com.coderave.raveplayer.Constants;
import com.coderave.raveplayer.MediaController;
import com.coderave.raveplayer.PlayList;
import com.coderave.raveplayer.R;
import com.coderave.raveplayer.models.SongDetails;
import com.coderave.raveplayer.ui.MainActivity;
import com.coderave.raveplayer.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlayerService extends Service {

    private final PlayList playlist = PlayList.getInstance();
    private final MediaController mediaController = MediaController.getInstance();
    private final PhoneStateListener mPhoneStateListener = new PlayerPhoneStateListener();
    private final static int NotificationId = 8763326;
    private NotificationManager mNotificationManager;
    private EventBus bus = EventBus.getDefault();
    private boolean shouldResumePlay = false;


    @Override
    public void onCreate() {
        super.onCreate();
        bus.register(this);
        registerCallListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null || intent.getAction() == null)
            return super.onStartCommand(intent, flags, startId);

        switch (intent.getAction()){
            case Constants.ACTION.PREV_ACTION: {
                if(playlist.hasPrev()){
                    mediaController.play(playlist.prev());
                }
            } break;
            case Constants.ACTION.PLAY_ACTION: {
                if (mediaController.isPlaying()) {
                    mediaController.pause();
                } else if (mediaController.isPaused()) {
                    mediaController.resume();
                }
            } break;
            case Constants.ACTION.NEXT_ACTION: {
                if (playlist.hasNext()) {
                    mediaController.play(playlist.next());
                }
            } break;
            case Constants.ACTION.CLOSE_ACTION: {
                stopSelf();
                sendBroadcast(new Intent(Constants.ACTION.CLOSE_ACTION));
            } break;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        release();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        release();
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe
    public void onPlayerStateChanged(MediaController.OnPlayerStateChangeEvent event){
        createOrUpdateNotification();
    }

    @Subscribe
    public void onSongChanged(MediaController.OnSongChangedEvent event){
        createOrUpdateNotification();
    }

    @Subscribe
    public void onSongPlayCompleted(MediaController.OnPlayCompletedEvent event){
        Utils.autoPlayNext();
        createOrUpdateNotification();
    }

    private void registerCallListener(){
        TelephonyManager phoneManager = getPhoneManager();
        if(phoneManager != null) {
            phoneManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void unregisterCallListener(){
        TelephonyManager phoneManager = getPhoneManager();
        if(phoneManager != null) {
            phoneManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    private TelephonyManager getPhoneManager(){
        return (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    private void release(){
        MediaController.getInstance().stop();
        unregisterCallListener();
        bus.unregister(this);
        if(mNotificationManager != null){
            mNotificationManager.cancel(NotificationId);
        }
    }

    private void createOrUpdateNotification(){
        if(mediaController.getCurrentSong() == null) {
            return;
        }

        if(mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Notification notification = createNotification();
        updateNotification(notification);
        mNotificationManager.notify(NotificationId, notification);
    }

    private Notification createNotification(){
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);
        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.status_bar_expanded);

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art, Constants.getDefaultAlbumArt(this));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NotificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, NotificationId, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, NotificationId, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, NotificationId, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeIntent = new Intent(this, PlayerService.class);
        closeIntent.setAction(Constants.ACTION.CLOSE_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, NotificationId, closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        final SongDetails song = mediaController.getCurrentSong();

        views.setImageViewResource(R.id.status_bar_play, getPlayOrPauseIcon());
        bigViews.setImageViewResource(R.id.status_bar_play, getPlayOrPauseIcon());

        views.setTextViewText(R.id.status_bar_track_name, song.getTitle());
        bigViews.setTextViewText(R.id.status_bar_track_name, song.getTitle());

        views.setTextViewText(R.id.status_bar_artist_name, song.getArtist());
        bigViews.setTextViewText(R.id.status_bar_artist_name, song.getArtist());

        Notification notification = new Notification.Builder(this)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .build();

        notification.contentView    = views;
        notification.bigContentView = bigViews;
        notification.flags          = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notification.contentIntent  = pendingIntent;

        return notification;
    }

    private void updateNotification(Notification notification){
        if(mediaController.getCurrentSong() == null)
            return;

        updateNotificationArtist(notification);
        updateNotificationTitle(notification);
        updateNotificationPlayPauseIcon(notification);
        updateNotificationCoverArt();
    }

    private void updateNotificationArtist(Notification notification){
        notification.bigContentView.setTextViewText(R.id.status_bar_artist_name, mediaController.getCurrentSong().getArtist());
    }

    private void updateNotificationTitle(Notification notification){
        notification.bigContentView.setTextViewText(R.id.status_bar_track_name, mediaController.getCurrentSong().getTitle());
    }

    private void updateNotificationPlayPauseIcon(Notification notification){
        notification.bigContentView.setImageViewResource(R.id.btn_play_pause, getPlayOrPauseIcon());
    }

    private void updateNotificationCoverArt(){
        //mNotification.bigContentView.setImageViewBitmap(R.id.status_bar_album_art, Utils.getCoverImage(this, mediaController.getCurrentSong()));
    }

    private int getPlayOrPauseIcon(){
        return mediaController.isPlaying() ? R.drawable.apollo_holo_dark_pause : R.drawable.apollo_holo_dark_play;
    }

    private class PlayerPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //Incoming call: Pause music
                if(mediaController.isPlaying()) {
                    mediaController.pause();
                    shouldResumePlay = true;
                }
            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music
                if(shouldResumePlay) {
                    mediaController.resume();
                    shouldResumePlay = false;
                }
            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
                if(mediaController.isPlaying()) {
                    mediaController.pause();
                    shouldResumePlay = true;
                }
            }
        }
    }
}
