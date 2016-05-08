package com.coderave.raveplayer.observers.song;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import rx.Observable;
import rx.Subscriber;


public abstract class BaseSongObserver<T> implements Observable.OnSubscribe<T> {
    private final ContentResolver contentResolver;

    public BaseSongObserver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        Cursor cursor = contentResolver.query(getGetMediaUri(), getProjection(), getQuery(), null, getSortOrder());
        if(cursor == null) {
            subscriber.onCompleted();
            return;
        }

        while(cursor.moveToNext()){
            subscriber.onNext(mapFromCursor(cursor));
        }

        cursor.close();
        subscriber.onCompleted();
    }

    public abstract T mapFromCursor(Cursor cursor);

    public abstract String getQuery();

    public String[] getProjection(){
        return new String[] {
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION
        };
    }

    public Uri getGetMediaUri(){
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    public String getSortOrder(){
        return MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
    }
}
