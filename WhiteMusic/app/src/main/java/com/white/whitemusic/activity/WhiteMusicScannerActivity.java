package com.white.whitemusic.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.white.whitemusic.Adapter.WhiteMusicScannerAdapter;
import com.white.whitemusic.R;

public class WhiteMusicScannerActivity extends FragmentActivity {

    public ViewPager mViewPager;
    public WhiteMusicScannerAdapter mWhiteMusicScannerAdapter;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_white_music_scanner);
        initView();
    }

    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mWhiteMusicScannerAdapter = new WhiteMusicScannerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mWhiteMusicScannerAdapter);
    }
}
