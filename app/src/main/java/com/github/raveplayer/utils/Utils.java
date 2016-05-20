package com.github.raveplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.raveplayer.MediaController;
import com.github.raveplayer.PlayList;
import com.github.raveplayer.R;
import com.github.raveplayer.models.SongDetails;
import com.squareup.picasso.Picasso;


public class Utils {

    private final static String albumArtUrl = "content://media/external/audio/albumart/";

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void applyKitKatToolbarPadding(Toolbar toolbar){
        final int statusBarHeight = getStatusBarHeight(toolbar.getContext());
        toolbar.setPadding(0, statusBarHeight, 0, 0);
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height = params.height + statusBarHeight;
        toolbar.setLayoutParams(params);
    }

    public static void loadSmallCoverOrDefaultArt(final ImageView imageView, final SongDetails song){
        Uri albumArtUri = Uri.parse(albumArtUrl+song.getAlbumId());

        loadCoverOrDefaultArt(albumArtUri, imageView);
    }

    public static void loadLargeCoverOrDefaultArt(final ImageView imageView, final SongDetails song) {
        Uri albumArtUri = Uri.parse(albumArtUrl + song.getAlbumId());

        loadCoverOrDefaultArt(albumArtUri, imageView);
    }

    private static void loadCoverOrDefaultArt(Uri uri, ImageView imageView){
        Picasso.with(imageView.getContext())
                .load(uri)
                .placeholder(R.drawable.default_cover_image)
                .error(R.drawable.default_cover_image)
                .into(imageView);
    }

    public static Bitmap getCoverImage(Context context, SongDetails song){
        Bitmap bitmap = BitmapFactory.decodeFile(albumArtUrl+song.getAlbumId());
        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_cover_image);
        }

        return bitmap;
    }

    public static void autoPlayNext() {
        final PlayList playlist = PlayList.getInstance();
        final MediaController mediaController = MediaController.getInstance();

        switch (playlist.getLoopStyle()) {
            case LOOP_CURRENT: {
                mediaController.play(playlist.current());
            } break;
            case LOOP_LIST: {
                if (!playlist.hasNext()) {
                    playlist.setCurrent(-1);
                }
                mediaController.play(playlist.next());
            } break;
            default: {
                if (playlist.hasNext()) {
                    mediaController.play(playlist.next());
                }
            } break;
        }
    }
}
