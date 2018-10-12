package com.white.whitemusic.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.white.whitemusic.Adapter.WhiteMusicScannerAdapter;
import com.white.whitemusic.R;
import com.white.whitemusic.fragment.WhiteMusicScannerFragment;

import java.util.ArrayList;
import java.util.List;

// 扫描本地数据
public class WhiteMusicScannerActivity extends FragmentActivity {

    public ViewPager mViewPager;
    public WhiteMusicScannerAdapter mWhiteMusicScannerAdapter;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_white_music_scanner);
        initView();
    }

    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        Fragment fragment = new WhiteMusicScannerFragment();
        mListFragment.add(fragment);
        mWhiteMusicScannerAdapter = new WhiteMusicScannerAdapter(getSupportFragmentManager(), mListFragment);
        mViewPager.setAdapter(mWhiteMusicScannerAdapter);
        // setOnPageChangeListener方法使用addOnPageChangeListener替代
        mViewPager.addOnPageChangeListener(new OnPageChangeListener());
        // 通过index制定页面
        mViewPager.setCurrentItem(1, true);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.isShown()) {
            mViewPager.setCurrentItem(0, true);
        }
    }

    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {
        int onPageScrolled = -1;

        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int arg0) {
            if ((onPageScrolled == 0 || onPageScrolled == 2) && arg0 == 0) {
                setResult(1);
                finish();
            }
        }

        // 当前页面被滑动时调用
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            onPageScrolled = arg0;
        }

        // 当新的页面被选中时调用
        @Override
        public void onPageSelected(int arg0) {

        }
    }
}
