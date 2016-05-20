package com.github.raveplayer.utils;


import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.github.raveplayer.ui.adapters.SimpleTabPagerAdapter;
import com.github.raveplayer.ui.fragments.BaseTabFragment;

import java.util.List;

public class TabBuilder {

    private final FragmentManager fm;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<BaseTabFragment> tabs;

    private TabBuilder(FragmentManager fm) {
        this.fm = fm;
    }

    public static TabBuilder with(FragmentManager fm){
        return new TabBuilder(fm);
    }

    public TabBuilder setTabLayout(TabLayout tabLayout){
        this.tabLayout = tabLayout;
        return this;
    }

    public TabBuilder setViewPager(ViewPager viewPager){
        this.viewPager = viewPager;
        return this;
    }

    public TabBuilder setTabList(List<BaseTabFragment> tabs){
        this.tabs = tabs;
        return this;
    }

    public void build(){
        for(BaseTabFragment tab : tabs){
            tabLayout.addTab(tabLayout.newTab().setText(tab.getTitle()));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setAdapter(new SimpleTabPagerAdapter(fm, tabs));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
