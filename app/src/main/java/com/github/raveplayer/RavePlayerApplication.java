package com.github.raveplayer;

import android.app.Application;

import com.facebook.stetho.Stetho;


public class RavePlayerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
