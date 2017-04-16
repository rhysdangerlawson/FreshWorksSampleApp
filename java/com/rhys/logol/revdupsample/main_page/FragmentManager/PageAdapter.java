package com.rhys.logol.revdupsample.main_page.FragmentManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rhys.logol.revdupsample.main_page.Feed.ShowImagesActivity;

import java.util.List;

class PageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    PageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override public Fragment getItem(int position) {
        switch(position) {
            case 0: return ShowImagesActivity.newInstance("ShowImagesFragment, Instance 1");
            default: return VinFragment.newInstance("VinFragment, Default");
        }
    }

    @Override public int getCount() {
        return this.fragments.size();
    }
}
