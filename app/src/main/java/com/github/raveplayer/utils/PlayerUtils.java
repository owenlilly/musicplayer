package com.github.raveplayer.utils;

import com.github.raveplayer.MediaController;
import com.github.raveplayer.PlayingQueue;


public class PlayerUtils {

    private static PlayingQueue playingList = PlayingQueue.getInstance();
    private static MediaController mediaController = MediaController.getInstance();

    public static void prev(){
        if(!playingList.isEmpty()){
            mediaController.play(playingList.prev());
        }
    }

    public static void next(){
        if(!playingList.isEmpty()){
            mediaController.play(playingList.next());
        }
    }

    public static void togglePlayPause(){
        if (mediaController.isPlaying()){
            mediaController.pause();
        } else if (mediaController.isPaused()){
            mediaController.resume();
        } else if(!playingList.isEmpty()){
            mediaController.play(playingList.current());
        }
    }
}
