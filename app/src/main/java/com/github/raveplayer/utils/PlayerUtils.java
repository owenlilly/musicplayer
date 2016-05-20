package com.github.raveplayer.utils;

import com.github.raveplayer.MediaController;
import com.github.raveplayer.PlayList;


public class PlayerUtils {

    private static PlayList playList = PlayList.getInstance();
    private static MediaController mediaController = MediaController.getInstance();

    public static void prev(){
        if(playList.hasPrev()){
            mediaController.play(playList.prev());
        }
    }

    public static void next(){
        if(playList.hasNext()){
            mediaController.play(playList.next());
        }
    }

    public static void togglePlayPause(){
        if (mediaController.isPlaying()){
            mediaController.pause();
        } else if (mediaController.isPaused()){
            mediaController.resume();
        } else if(!playList.isEmpty()){
            mediaController.play(playList.current());
        }
    }
}
