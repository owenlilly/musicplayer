package com.github.raveplayer;


import android.media.AudioManager;
import android.media.MediaPlayer;

import com.github.raveplayer.models.SongDetails;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MediaController {

    private static volatile MediaController instance;

    private PlayerState playerState = PlayerState.Stopped;
    private MediaPlayer mediaPlayer;
    private SongDetails currentSong;
    private boolean shouldPublishProgress = false;
    private boolean canSeek = false;
    private EventBus bus = EventBus.getDefault();

    public MediaController() { }

    public static MediaController getInstance() {
        if(instance == null){
            synchronized (MediaController.class){
                if(instance == null) {
                    instance = new MediaController();
                }
            }
        }

        return instance;
    }

    public void play(SongDetails song) {
        switch (playerState){
            case Playing: {
                if(currentSong == song){
                    pause();
                    break;
                } else {
                    stop();
                }
            }
            case Stopped: {
                playSong(song);
            } break;
            case Paused: {
                resume();
            } break;
        }

        currentSong = song;
    }

    public void stop() {
        canSeek = false;
        setPlayerState(PlayerState.Stopped);
        if(mediaPlayer == null){
            return;
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void resume(){
        getMediaPlayer().start();
        setPlayerState(PlayerState.Playing);
    }

    public void pause(){
        getMediaPlayer().pause();
        setPlayerState(PlayerState.Paused);
    }

    public int getProgress(){
        int pos = -1;
        if(mediaPlayer != null){
            pos = mediaPlayer.getCurrentPosition();
        }

        return pos;
    }

    public void prepareToSeek(){
        shouldPublishProgress = false;
    }

    public void seekTo(int millisecond){
        if(!canSeek){
            return;
        }

        mediaPlayer.seekTo(millisecond);
        if(isPlaying()){
           startProgressPublisher();
        }
    }

    public SongDetails getCurrentSong(){
        return currentSong;
    }

    public boolean isPlaying(){
        return playerState == PlayerState.Playing;
    }

    public boolean isPaused(){
        return playerState == PlayerState.Paused;
    }

    private MediaPlayer getMediaPlayer(){
        if(mediaPlayer == null){
            synchronized (MediaController.class){
                if(mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
            }
        }

        return mediaPlayer;
    }

    private void playSong(SongDetails song){
        setPlayerState(PlayerState.Starting);
        MediaPlayer mediaPlayer = getMediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(song.getPath());
            initOnCompletionListener(mediaPlayer);
            initOnPrepareListener(mediaPlayer, song);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initOnCompletionListener(MediaPlayer mediaPlayer){
        mediaPlayer.setOnCompletionListener(mp -> {
            stop();
            bus.post(new OnPlayCompletedEvent(currentSong));
        });
    }

    private void initOnPrepareListener(MediaPlayer mediaPlayer, SongDetails song){
        mediaPlayer.setOnPreparedListener(mp -> {
            canSeek = true;
            setPlayerState(PlayerState.Playing);
            bus.post(new OnSongChangedEvent(song));
            mp.start();
        });
    }

    private void setPlayerState(PlayerState newState){
        playerState = newState;
        if(!isPlaying()){
            shouldPublishProgress = false;
        } else {
            startProgressPublisher();
        }
        bus.post(new OnPlayerStateChangeEvent(newState));
    }

    private void startProgressPublisher(){
        shouldPublishProgress = true;

        Observable<Integer> o = Observable.create(subscriber ->
                Schedulers.newThread()
                        .createWorker()
                        .schedulePeriodically(() -> publishNextOrComplete(subscriber), 0, 100, TimeUnit.MILLISECONDS)
        );

        o.observeOn(AndroidSchedulers.mainThread())
         .onBackpressureDrop() // discard unconsumed events
         .onErrorResumeNext(o) // resubscribe on error...
         .subscribe(pos -> bus.post(new OnProgressUpdateEvent(pos)), Throwable::printStackTrace);
    }

    private void publishNextOrComplete(Subscriber<? super Integer> s) {
        if(shouldPublishProgress) {
            s.onNext(mediaPlayer.getCurrentPosition());
            return;
        }

        s.onCompleted();
    }

    public enum PlayerState {
        Starting,
        Playing,
        Paused,
        Stopped
    }

    public static class OnPlayCompletedEvent {
        private final SongDetails song;

        public OnPlayCompletedEvent(SongDetails song) {
            this.song = song;
        }

        public SongDetails getCompletedSong(){
            return song;
        }
    }

    public static class OnSongChangedEvent {
        private final SongDetails songDetails;

        public OnSongChangedEvent(SongDetails songDetails) {
            this.songDetails = songDetails;
        }

        public SongDetails getSongDetails() {
            return songDetails;
        }
    }

    public static class OnPlayerStateChangeEvent {
        private final PlayerState playerState;

        public OnPlayerStateChangeEvent(PlayerState playerState) {
            this.playerState = playerState;
        }

        public PlayerState getPlayerState() {
            return playerState;
        }
    }

    public static class OnProgressUpdateEvent {
        private final int pos;

        public OnProgressUpdateEvent(int pos) {
            this.pos = pos;
        }

        public int getProgress(){
            return pos;
        }
    }
}
