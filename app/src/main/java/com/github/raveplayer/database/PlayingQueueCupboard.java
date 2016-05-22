package com.github.raveplayer.database;


import android.content.Context;

import com.github.raveplayer.models.SongDetails;

import java.util.Collection;

import rx.Observable;

public class PlayingQueueCupboard {

    private final Context context;

    public PlayingQueueCupboard(Context context){
        this.context = context.getApplicationContext();
    }
}
