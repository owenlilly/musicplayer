package com.coderave.raveplayer.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderave.raveplayer.MediaController;
import com.coderave.raveplayer.PlayList;
import com.coderave.raveplayer.R;
import com.coderave.raveplayer.ui.fragments.BaseTabFragment;
import com.coderave.raveplayer.ui.fragments.main.AlbumsFragment;
import com.coderave.raveplayer.ui.fragments.main.AllTracksFragment;
import com.coderave.raveplayer.ui.widgets.MaterialPlayPauseButton;
import com.coderave.raveplayer.utils.TabBuilder;
import com.coderave.raveplayer.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.txt_song_title)  TextView txtSongTitle;
    @Bind(R.id.txt_artist)      TextView txtArtist;
    @Bind(R.id.btn_play_pause)  MaterialPlayPauseButton btnPlayPause;
    @Bind(R.id.tabs)            TabLayout tabLayout;
    @Bind(R.id.toolbar)         Toolbar toolbar;
    @Bind(R.id.viewpager)       ViewPager viewPager;
    @Bind(R.id.cover_image)     ImageView coverImage;

    public static Context appContext;

    private final MediaController mediaController = MediaController.getInstance();
    private final PlayList playlist = PlayList.getInstance();
    private final EventBus mBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initBuildSpecificEnhancements();

        appContext = getApplicationContext();

        initTabLayout();
        initPlayPauseButton();

        updatePlayPauseButton();

        coverImage.setOnClickListener(v -> {
            if(mediaController.getCurrentSong() != null){
                Intent i = new Intent(this, NowPlayingActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayPauseButton();
    }

    @Subscribe
    public void onSongChanged(MediaController.OnSongChangedEvent event) {
        txtSongTitle.setText(event.getSongDetails().getTitle());
        txtArtist.setText(event.getSongDetails().getArtist());

        Bitmap coverArt = Utils.getSmallCover(this, event.getSongDetails().getId());
        if(coverArt != null) {
            coverImage.setImageBitmap(coverArt);
        } else {
            coverImage.setImageResource(R.drawable.default_cover_image);
        }
    }

    @Subscribe
    public void onPlayCompleted(MediaController.OnPlayCompletedEvent event) {
        updatePlayPauseButton();
        autoPlayNext();
    }

    @Subscribe
    public void onPlayerStateChange(MediaController.OnPlayerStateChangeEvent event) {
        updatePlayPauseButton();
    }

    private void initBuildSpecificEnhancements() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            updateKitKatToolbarPaddingAndHeight();
        }
    }

    private void initTabLayout() {
        final List<BaseTabFragment> tabList = Arrays.<BaseTabFragment>asList(new AllTracksFragment(), new AlbumsFragment());

        TabBuilder.with(getSupportFragmentManager())
                .setTabLayout(tabLayout)
                .setViewPager(viewPager)
                .setTabList(tabList)
                .build();
    }

    private void initPlayPauseButton() {
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPlayPause.setColor(Color.WHITE);
        btnPlayPause.setAnimDuration(100);
    }

    private void togglePlayPause() {
        if (mediaController.isPlaying()) {
            MediaController.getInstance().pause();
        } else if (mediaController.isPaused()) {
            MediaController.getInstance().resume();
        }
    }

    private void updatePlayPauseButton() {
        if (mediaController.isPlaying()) {
            btnPlayPause.setToPause();
        } else {
            btnPlayPause.setToPlay();
        }
    }

    private void updateKitKatToolbarPaddingAndHeight(){
        final int statusBarHeight = Utils.getStatusBarHeight(this);
        toolbar.setPadding(0, statusBarHeight, 0, 0);
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height = params.height + statusBarHeight;
        toolbar.setLayoutParams(params);
    }

    private void autoPlayNext() {
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
