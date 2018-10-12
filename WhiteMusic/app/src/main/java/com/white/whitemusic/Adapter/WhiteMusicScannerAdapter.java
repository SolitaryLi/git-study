package com.white.whitemusic.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class WhiteMusicScannerAdapter extends FragmentPagerAdapter {

    List<Fragment> mListFragment;

    public WhiteMusicScannerAdapter(FragmentManager fm) {
        super(fm);
    }

    public WhiteMusicScannerAdapter(FragmentManager fm, List<Fragment> pListFragment) {
        super(fm);
        this.mListFragment = pListFragment;
    }

    @Override
    public Fragment getItem(int i) {
        return mListFragment.get(i);
    }

    @Override
    public int getCount() {
        return mListFragment.size();
    }
}
