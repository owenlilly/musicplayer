package com.coderave.raveplayer.ui;

import android.support.v7.widget.RecyclerView;

import com.coderave.raveplayer.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    @Test
    public void songListView_shouldNotBeNull(){
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);

        RecyclerView songListView = mainActivity.songListView;

        assertThat(songListView, not(equalTo(null)));
    }
}
