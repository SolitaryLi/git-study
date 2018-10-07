/**
 * Copyright (c) www.longdw.com
 */
package com.white.whitemusic.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.white.whitemusic.R;
import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.utils.Utils;
import com.white.whitemusic.view.AlwaysMarqueeTextView;


@SuppressLint("HandlerLeak")
public class WhiteMusicLocalListUIManager implements OnClickListener {

	private Activity mActivity;
	private View mView;
	private WhiteMusicServiceManager mWhiteMusicServiceManager;
	private AlwaysMarqueeTextView mMusicNameTv, mArtistTv;
	private TextView mPositionTv, mDurationTv;
	private ProgressBar mPlaybackProgress;
	public Handler mHandler;
	private ImageView mHeadIcon;

	private Button mPlayBtn;
    private Button mPauseBtn;
	private Button mNextBtn;
	private Button mSearchBtn, mBackBtn;
	private WhiteMusicUIManager mWhiteMusicUIManager;


	public WhiteMusicLocalListUIManager(Activity pActivity, WhiteMusicServiceManager pWhiteMusicServiceManager, View pView, WhiteMusicUIManager pWhiteMusicUIManager) {
		this.mActivity = pActivity;
		this.mView = pView;
		this.mWhiteMusicUIManager = pWhiteMusicUIManager;
		this.mWhiteMusicServiceManager = pWhiteMusicServiceManager;
		initView();

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				refreshSeekProgress(mWhiteMusicServiceManager.getCurrentMusicPosition(),
						mWhiteMusicServiceManager.getCurrentMusicDuration());
			}
		};
	}

	// 页面初始化
	private void initView() {

		mSearchBtn = (Button) findViewById(R.id.searchBtn);
		mBackBtn = (Button) findViewById(R.id.backBtn);

		mMusicNameTv = (AlwaysMarqueeTextView) findViewById(R.id.musicname_tv2);
		mArtistTv = (AlwaysMarqueeTextView) findViewById(R.id.artist_tv2);

		mPositionTv = (TextView) findViewById(R.id.position_tv2);
		mDurationTv = (TextView) findViewById(R.id.duration_tv2);

		mPlayBtn = (Button) findViewById(R.id.play_btn);
        mPauseBtn = (Button) findViewById(R.id.pause_btn);
		mNextBtn = (Button) findViewById(R.id.next_btn);

		mSearchBtn.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
		mPlayBtn.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);

		mPlaybackProgress = (ProgressBar) findViewById(R.id.playback_seekbar2);

		mHeadIcon = (ImageView) findViewById(R.id.headicon_iv);
	}

	// 根据ID取View
	public View findViewById(int id) {
		return mView.findViewById(id);
	}

	// 音乐播放进度条
	public void refreshSeekProgress(int curTime, int totalTime) {

		curTime /= 1000;
		totalTime /= 1000;
		int curminute = curTime / 60;
		int cursecond = curTime % 60;

		String curTimeString = String.format("%02d:%02d", curminute, cursecond);
		mPositionTv.setText(curTimeString);

		int rate = 0;
		if (totalTime != 0) {
			rate = (int) ((float) curTime / totalTime * 100);
		}
		mPlaybackProgress.setProgress(rate);
	}

	public void refreshUI(int curTime, int totalTime, WhiteMusicInfoBean music) {

		int tempCurTime = curTime;
		int tempTotalTime = totalTime;

		totalTime /= 1000;
		int totalminute = totalTime / 60;
		int totalsecond = totalTime % 60;
		String totalTimeString = String.format("%02d:%02d", totalminute,
				totalsecond);

		mDurationTv.setText(totalTimeString);

		mMusicNameTv.setText(music.getMusicName());
		mArtistTv.setText(music.getArtistName());

		ContentResolver res = mActivity.getContentResolver();
		Bitmap bitmap = Utils.createThumbFromUir(res, music.getMusicAlbumUri());

		mHeadIcon.setBackgroundDrawable(new BitmapDrawable(mActivity
				.getResources(), bitmap));
		refreshSeekProgress(tempCurTime, tempTotalTime);
	}

	public void showPlay(boolean flag) {
		if (flag) {
			mPlayBtn.setVisibility(View.VISIBLE);
			mPauseBtn.setVisibility(View.GONE);
		} else {
			mPlayBtn.setVisibility(View.GONE);
			mPauseBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.play_btn:
				mWhiteMusicServiceManager.musicPlay();
				break;
			case R.id.pause_btn:
				mWhiteMusicServiceManager.musicPlayPause();
				break;
			case R.id.next_btn:
				mWhiteMusicServiceManager.musicPlayNext();
				break;
			case R.id.searchBtn:
//				mActivity.startActivity(new Intent(mActivity,
//						MusicListSearchActivity.class));
				break;
			case R.id.backBtn:
				mWhiteMusicUIManager.setCurrentItem();
				break;
//			case R.id.play_btn:
////				((MainContentActivity)mActivity).mSlidingMenu.showMenu(true);
//				break;
		}
	}
}
