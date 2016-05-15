package com.coderave.raveplayer.utils;

import com.coderave.raveplayer.MediaController;
import com.coderave.raveplayer.PlayList;

/**
 * Created by user on 5/15/16.
 */
public class PlayerUtils {

    public static void togglePlayPause(){
        final MediaController mediaController = MediaController.getInstance();

        if (mediaController.isPlaying()){
            mediaController.pause();
        } else if (mediaController.isPaused()){
            mediaController.resume();
        } else if(!PlayList.getInstance().isEmpty()){
            mediaController.play(PlayList.getInstance().current());
        }
    }
}
