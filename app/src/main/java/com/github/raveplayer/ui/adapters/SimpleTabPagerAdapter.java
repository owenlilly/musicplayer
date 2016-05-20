package com.github.raveplayer.ui.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.raveplayer.ui.fragments.BaseTabFragment;

import java.util.List;


public class SimpleTabPagerAdapter extends FragmentStatePagerAdapter {

    private final List<BaseTabFragment> tabs;

    public SimpleTabPagerAdapter(FragmentManager fm, List<BaseTabFragment> tabFragments) {
        super(fm);
        this.tabs = tabFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }
}
