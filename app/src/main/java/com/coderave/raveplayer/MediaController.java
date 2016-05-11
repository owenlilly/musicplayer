package com.coderave.raveplayer;


import android.media.AudioManager;
import android.media.MediaPlayer;

import com.coderave.raveplayer.models.SongDetails;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;


public class MediaController {

    private static MediaController instance;

    private PlayerState playerState = PlayerState.Stopped;
    private MediaPlayer mediaPlayer;
    private SongDetails currentSong;
    private EventBus bus = EventBus.getDefault();

    public static MediaController getInstance() {
        if(instance == null){
            synchronized (MediaController.class){
                instance = new MediaController();
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
        setPlayerState(PlayerState.Stopped);
        if(mediaPlayer == null){
            return;
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public PlayerState getPlayerState(){
        return playerState;
    }

    public void resume(){
        getMediaPlayer().start();
        setPlayerState(PlayerState.Playing);
    }

    public void pause(){
        getMediaPlayer().pause();
        setPlayerState(PlayerState.Paused);
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
                mediaPlayer = new MediaPlayer();
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
            mediaPlayer.setOnCompletionListener(mp -> {
                stop();
                bus.post(new OnPlayCompletedEvent(currentSong));
            });
            mediaPlayer.setOnPreparedListener(mp -> {
                setPlayerState(PlayerState.Playing);
                bus.post(new OnSongChangedEvent(song));
                mp.start();
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPlayerState(PlayerState newState){
        playerState = newState;
        bus.post(new OnPlayerStateChangeEvent(newState));
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
}
