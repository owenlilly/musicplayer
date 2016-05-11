package com.coderave.raveplayer.services;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.coderave.raveplayer.MediaController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlayerService extends Service {

    private EventBus bus = EventBus.getDefault();

    @Override
    public void onCreate() {
        super.onCreate();
        bus.register(this);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe
    public void onPlayerStateChanged(MediaController.OnPlayerStateChangeEvent evt){

    }

    private void release(){
        MediaController.getInstance().stop();
        bus.unregister(this);
    }
}
