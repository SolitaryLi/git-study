package com.white.whitemusic.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.white.whitemusic.R;
import com.white.whitemusic.activity.WhiteMusicContentActivity;
import com.white.whitemusic.constant.WhiteMusicConstant;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WhiteMusicUIManager implements WhiteMusicContentActivity.OnBackListener{

    private Activity mActivity;
    private View mView;
    private LayoutInflater mLayoutInflater;
    /** mViewPager为第一层 mViewPagerSub为第二层（例如从文件夹或歌手进入列表，点击列表会进入第二层） */
    private ViewPager mViewPager, mViewPagerSub;
    private List<View> mListViews, mListViewsSub;
    private WhiteMusicContentActivity mWhiteMusicContentActivity;
    private WhiteMusicMainUIManager mWhiteMusicMainUIManager;
    private RelativeLayout mRelativeLayout;
    private ChangeBgReceiver mChangeBgReceiver;
    private OnRefreshListener mRefreshListener;


    public WhiteMusicUIManager(Activity pActivity, View pView) {
        this.mActivity = pActivity;
        this.mView = pView;
        mWhiteMusicContentActivity = (WhiteMusicContentActivity)pActivity;
        this.mLayoutInflater = LayoutInflater.from(pActivity);
        initUIManager();
    }

    private void initUIManager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPagerSub = (ViewPager) findViewById(R.id.viewPagerSub);

        mListViews = new ArrayList<View>();
        mListViewsSub = new ArrayList<View>();
        // 注册监听
        mViewPager.addOnPageChangeListener(new UIOnPageChangeListener());
        mViewPagerSub.addOnPageChangeListener(new UIOnPageChangeListener());
    }

    public void setContentType(int type) {
        // 此处可以根据传递过来的view和type分开来处理
        setContentType(type, null);
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    private class UIOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int onPageScrolled = -1;
        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int arg0) {
            System.out.println("onPageScrollStateChanged--->" + arg0);
            if (onPageScrolled == 0 && arg0 == 0) {
                mWhiteMusicContentActivity.unRegisterBackListener(WhiteMusicUIManager.this);
                mViewPager.removeAllViews();
                mViewPager.setVisibility(View.INVISIBLE);
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }
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

    public void setContentType(int type, Object obj) {
        // 注册监听返回按钮
        mWhiteMusicContentActivity.registerBackListener(this);
        switch (type) {
            case WhiteMusicConstant.START_FROM_LOCAL:
                mWhiteMusicMainUIManager = new WhiteMusicLocalListManager(mActivity, this);
                View transView1 = mLayoutInflater.inflate(
                        R.layout.viewpager_trans_layout, null);
                View contentView1 = mWhiteMusicMainUIManager.getView(WhiteMusicConstant.START_FROM_LOCAL);
                mViewPager.setVisibility(View.VISIBLE);
                mListViews.clear();
                mViewPager.removeAllViews();

                mListViews.add(transView1);
                mListViews.add(contentView1);
                mViewPager.setAdapter(new MyPagerAdapter(mListViews));
                mViewPager.setCurrentItem(1, true);
                break;
            case WhiteMusicConstant.START_FROM_FAVORITE:
//                mMainUIManager = new WhiteMusicLocalListManager(mActivity, this);
//                View transView2 = mInflater.inflate(
//                        R.layout.viewpager_trans_layout, null);
//                View contentView2 = mMainUIManager.getView(START_FROM_FAVORITE);
//                mViewPager.setVisibility(View.VISIBLE);
//                mListViews.clear();
//                mViewPager.removeAllViews();
//
//                mListViews.add(transView2);
//                mListViews.add(contentView2);
//                mViewPager.setAdapter(new MyPagerAdapter(mListViews));
//                mViewPager.setCurrentItem(1, true);
                break;
            case WhiteMusicConstant.START_FROM_FOLDER:
//                mMainUIManager = new FolderBrowserManager(
//                        mActivity, this);
//                View transView3 = mInflater.inflate(
//                        R.layout.viewpager_trans_layout, null);
//                View folderView = mMainUIManager.getView();
//                mViewPager.setVisibility(View.VISIBLE);
//                mListViews.clear();
//                mViewPager.removeAllViews();
//
//                mListViews.add(transView3);
//                mListViews.add(folderView);
//                mViewPager.setAdapter(new MyPagerAdapter(mListViews));
//                mViewPager.setCurrentItem(1, true);
                break;
            case WhiteMusicConstant.START_FROM_ARTIST:
//                mMainUIManager = new ArtistBrowserManager(
//                        mActivity, this);
//                View transView4 = mInflater.inflate(
//                        R.layout.viewpager_trans_layout, null);
//                View artistView = mMainUIManager.getView();
//                mViewPager.setVisibility(View.VISIBLE);
//                mListViews.clear();
//                mViewPager.removeAllViews();
//
//                mListViews.add(transView4);
//                mListViews.add(artistView);
//                mViewPager.setAdapter(new MyPagerAdapter(mListViews));
//                mViewPager.setCurrentItem(1, true);
                break;
            case WhiteMusicConstant.START_FROM_ALBUM:
//                mMainUIManager = new AlbumBrowserManager(
//                        mActivity, this);
//                View transView5 = mInflater.inflate(
//                        R.layout.viewpager_trans_layout, null);
//                View albumView = mMainUIManager.getView();
//                mViewPager.setVisibility(View.VISIBLE);
//                mListViews.clear();
//                mViewPager.removeAllViews();
//
//                mListViews.add(transView5);
//                mListViews.add(albumView);
//                mViewPager.setAdapter(new MyPagerAdapter(mListViews));
//                mViewPager.setCurrentItem(1, true);
                break;
            case WhiteMusicConstant.FOLDER_TO_MYMUSIC:
//                mMainUIManager = new WhiteMusicLocalListManager(mActivity, this);
//                View transViewSub1 = mInflater.inflate(
//                        R.layout.viewpager_trans_layout, null);
//                View contentViewSub1 = mMainUIManager.getView(START_FROM_FOLDER, obj);
//                mViewPagerSub.setVisibility(View.VISIBLE);
//                mListViewsSub.clear();
//                mViewPagerSub.removeAllViews();
//
//                mListViewsSub.add(transViewSub1);
//                mListViewsSub.add(contentViewSub1);
//                mViewPagerSub.setAdapter(new MyPagerAdapter(mListViewsSub));
//                mViewPagerSub.setCurrentItem(1, true);
                break;
            case WhiteMusicConstant.ARTIST_TO_MYMUSIC:
//                mMainUIManager = new WhiteMusicLocalListManager(mActivity, this);
//                View transViewSub2 = mInflater.inflate(
//                        R.layout.viewpager_trans_layout, null);
//                View contentViewSub2 = mMainUIManager.getView(START_FROM_ARTIST, obj);
//                mViewPagerSub.setVisibility(View.VISIBLE);
//                mListViewsSub.clear();
//                mViewPagerSub.removeAllViews();
//
//                mListViewsSub.add(transViewSub2);
//                mListViewsSub.add(contentViewSub2);
//                mViewPagerSub.setAdapter(new MyPagerAdapter(mListViewsSub));
//                mViewPagerSub.setCurrentItem(1, true);
                break;
            case WhiteMusicConstant.ALBUM_TO_MYMUSIC:
//                mMainUIManager = new WhiteMusicLocalListManager(mActivity, this);
//                View transViewSub3 = mInflater.inflate(
//                        R.layout.viewpager_trans_layout, null);
//                View contentViewSub3 = mMainUIManager.getView(START_FROM_ALBUM, obj);
//                mViewPagerSub.setVisibility(View.VISIBLE);
//                mListViewsSub.clear();
//                mViewPagerSub.removeAllViews();
//
//                mListViewsSub.add(transViewSub3);
//                mListViewsSub.add(contentViewSub3);
//                mViewPagerSub.setAdapter(new MyPagerAdapter(mListViewsSub));
//                mViewPagerSub.setCurrentItem(1, true);
                break;
        }
    }

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

    private class MyPagerAdapter extends PagerAdapter {

        private List<View> listViews;

        public MyPagerAdapter(List<View> views) {
            this.listViews = views;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(listViews.get(position));// 删除页卡
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {// 这个方法用来实例化页卡
            container.addView(listViews.get(position));// 添加页卡
            return listViews.get(position);
        }

        @Override
        public int getCount() {
            return listViews.size();// 返回页卡的数量
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;// 官方提示这样写
        }
    }

    @Override
    public void onBack() {

    }

    private class ChangeBgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("path");
            Bitmap bitmap = getBitmapByPath(path);
            if(bitmap != null) {
                mRelativeLayout.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), bitmap));
            }
            if(mWhiteMusicMainUIManager != null) {
                mWhiteMusicMainUIManager.setBgByPath(path);
            }
        }
    }

    public Bitmap getBitmapByPath(String path) {
        AssetManager am = mActivity.getAssets();
        Bitmap bitmap = null;
        try {
            InputStream is = am.open("bkgs/" + path);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void setCurrentItem() {
        if (mViewPagerSub.getChildCount() > 0) {
            mViewPagerSub.setCurrentItem(0, true);
        } else {
            mViewPager.setCurrentItem(0, true);
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }
}
