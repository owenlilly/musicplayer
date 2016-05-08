package com.coderave.raveplayer;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MediaControllerTest {

    @Test
    public void getInstance_shouldReturnInstanceOfMediaController(){
        MediaController mediaController = MediaController.getInstance();

        assertThat(mediaController, not(equalTo(null)));
    }

    @Test
    public void getInstance_shouldReturnSameInstanceOfMediaController(){
        MediaController mediaController1 = MediaController.getInstance();
        MediaController mediaController2 = MediaController.getInstance();
        
        assertThat(mediaController1, is(equalTo(mediaController2)));
    }

    @Test
    public void play_shouldCallMediaPlayerPlay(){

    }
}
