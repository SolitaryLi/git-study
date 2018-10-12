/**
 * Copyright (c) www.longdw.com
 */
package com.white.whitemusic.manager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.white.whitemusic.Adapter.WhiteMusicListAdapter;
import com.white.whitemusic.MainApplication;
import com.white.whitemusic.R;
import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.listenr.OnMusicStatusChangeListener;
import com.white.whitemusic.task.WhiteMusicScannerTask;

import java.util.ArrayList;
import java.util.List;


public class WhiteMusicLocalListManager extends WhiteMusicMainUIManager implements OnTouchListener {

	private LayoutInflater mLayoutInflater;
	private Activity mActivity;
	private RelativeLayout mMainLayout, mBottomLayout;
	private ListView mListView;

	private WhiteMusicListAdapter mWhiteMusicListAdapter;
	private WhiteMusicServiceManager mWhiteMusicServiceManager;
	private WhiteMusicLocalListUIManager mWhiteMusicLocalListUIManager;
	private WhiteMusicUIManager mWhiteMusicUIManager;
	private WhiteMusicScannerTask mWhiteMusicScannerTask;
	private WhiteMusicPlayManager mWhiteMusicPlayManager;

	// 初期化
	public WhiteMusicLocalListManager(Activity activity, WhiteMusicUIManager whiteMusicUIManager) {
		this.mActivity = activity;
		mLayoutInflater = LayoutInflater.from(activity);
		this.mWhiteMusicUIManager = whiteMusicUIManager;
	}

	// 监听音乐播放状态
	private OnMusicStatusChangeListener onMusicStatusChangeListener = new OnMusicStatusChangeListener() {
		@Override
		public void onMusicPlayProgressChange(WhiteMusicInfoBean whiteMusicInfoBean) {
			mWhiteMusicPlayManager.refreshUI(new Long(whiteMusicInfoBean.getMusicPlayTime()).intValue(), new Long(whiteMusicInfoBean.getMusicDuration()).intValue(), whiteMusicInfoBean);
			mWhiteMusicLocalListUIManager.refreshUI(new Long(whiteMusicInfoBean.getMusicPlayTime()).intValue(), new Long(whiteMusicInfoBean.getMusicDuration()).intValue(), whiteMusicInfoBean);
		}
		@Override
		public void onMusicPlay(WhiteMusicInfoBean whiteMusicInfoBean) {
			mWhiteMusicPlayManager.showPlay(false);
			mWhiteMusicLocalListUIManager.showPlay(false);
		}
		@Override
		public void onMusicPause(WhiteMusicInfoBean whiteMusicInfoBean) {
			mWhiteMusicPlayManager.showPlay(true);
			mWhiteMusicLocalListUIManager.showPlay(true);
		}
	};

	public View getView(int from) {
		return getView(from, null);
	}

	public View getView(int from, Object object) {
		View view = mLayoutInflater.inflate(R.layout.activity_white_music_local_list, null);
		initViewBackGround(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		mWhiteMusicServiceManager = MainApplication.mWhiteMusicServiceManager;
		mBottomLayout = (RelativeLayout) view.findViewById(R.id.main_bottom_layout);

		mListView = (ListView) view.findViewById(R.id.music_listview);
		mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// 注册监听
		mWhiteMusicServiceManager.registerOnMusicStataChangeListener(onMusicStatusChangeListener);

		mWhiteMusicLocalListUIManager = new WhiteMusicLocalListUIManager(mActivity, mWhiteMusicServiceManager, view,
				mWhiteMusicUIManager);

		mWhiteMusicPlayManager = new WhiteMusicPlayManager(mActivity, mWhiteMusicServiceManager, view);

		initListView();
		initListViewStatus();
	}

	private void initViewBackGround(View view) {
		mMainLayout = (RelativeLayout) view
				.findViewById(R.id.main_mymusic_layout);
		mMainLayout.setOnTouchListener(this);
	}

	private void initListView() {
		mWhiteMusicListAdapter = new WhiteMusicListAdapter(mActivity);
		mListView.setAdapter(mWhiteMusicListAdapter);
		// 注册ListView中选中Click
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				mWhiteMusicServiceManager.musicPlay(position);
			}
		});

		// DB中如存在获取DB中的数据显示到UI中，如不存在检索CD卡中的数据显示到UI中并保存到数据库中
		// TODO
//        if (null == mWhiteMusicServiceManager.getMusicPlayList() || mWhiteMusicServiceManager.getMusicPlayList().isEmpty()) {
//            mWhiteMusicScannerTask = new WhiteMusicScannerTask();
//            mWhiteMusicScannerTask.context = mActivity;
//            mWhiteMusicScannerTask.whiteMusicListAdapter = mWhiteMusicListAdapter;
//            mWhiteMusicScannerTask.execute();
//        } else {
			mWhiteMusicListAdapter.setData(mWhiteMusicServiceManager.getMusicPlayList(), false);
//		}
	}

	private void initListViewStatus() {
		mWhiteMusicPlayManager.setListViewAdapter(mWhiteMusicListAdapter);
		mWhiteMusicPlayManager.showPlay(true);
		mWhiteMusicLocalListUIManager.showPlay(true);
	}

	int oldY = 0;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int bottomTop = mBottomLayout.getTop();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			oldY = (int) event.getY();
			if (oldY > bottomTop) {
				mWhiteMusicPlayManager.openPlayView();
			}
		}
		return true;
	}

	@Override
	protected void setBgByPath(String path) {

	}

	@Override
	public View getView() {
		return null;
	}

}
