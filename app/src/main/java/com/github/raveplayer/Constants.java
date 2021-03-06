package com.github.raveplayer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class Constants {

    public interface ACTION {
        String MAIN_ACTION = "com.coderave.ravelayer.action.main";
        String PREV_ACTION = "com.coderave.raveplayer.action.prev";
        String PLAY_ACTION = "com.coderave.raveplayer.action.play";
        String NEXT_ACTION = "com.coderave.raveplayer.action.next";
        String CLOSE_ACTION = "com.coderave.raveplayer.action.CLOSE_ACTION";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_cover_image, options);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
        return bm;
    }
}
