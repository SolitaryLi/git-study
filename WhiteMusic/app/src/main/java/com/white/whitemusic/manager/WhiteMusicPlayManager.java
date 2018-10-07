package com.white.whitemusic.manager;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.white.whitemusic.Adapter.WhiteMusicListAdapter;
import com.white.whitemusic.R;
import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.utils.Utils;
import com.white.whitemusic.view.WhiteMusicSlidingDrawer;

/**
 * 主播放页面
 */
public class WhiteMusicPlayManager implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, SlidingDrawer.OnDrawerOpenListener,
        SlidingDrawer.OnDrawerCloseListener{

    private Activity mActivity;
    private WhiteMusicServiceManager mWhiteMusicServiceManager;
    private WhiteMusicSlidingDrawer mWhiteMusicSlidingDrawer;
    private View mView;
    public Handler mHandler;
    // List 页面
    private ListView mListView;
    // Grid 页面
    private GridView mGridView;
    private SeekBar mPlaybackSeekBar;
    private ImageView mPlayHeadiconIv;
    private Button mPlayPreBtn, mPlayNextBtn, mPlayPlayBtn, mPlayPauseBtn;
    private TextView mPlayMusicNameTv, mPlayArtistTv, mPlayCurTimeTv, mPlayTotalTimeTv;
    private WhiteMusicInfoBean mCurrentWhiteMusicInfoBean;
    private WhiteMusicListAdapter mWhiteMusicListAdapter;


    public WhiteMusicPlayManager(Activity pActivity, WhiteMusicServiceManager pWhiteMusicServiceManager, View pView) {
        this.mActivity = pActivity;
        this.mWhiteMusicServiceManager = pWhiteMusicServiceManager;
        this.mView = pView;

        // 初始化页面
        initPlayView();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                refreshSeekProgress(mWhiteMusicServiceManager.getCurrentMusicPosition(),
                        mWhiteMusicServiceManager.getCurrentMusicDuration());
            }
        };
    }

    private void initPlayView() {
        mListView = (ListView) findViewById(R.id.music_listview);
        mGridView = (GridView) findViewById(R.id.gridview);
        mWhiteMusicSlidingDrawer = (WhiteMusicSlidingDrawer) findViewById(R.id.slidingDrawer);

        mPlayHeadiconIv = (ImageView) findViewById(R.id.play_headicon_iv);

        mPlayPreBtn = (Button) findViewById(R.id.play_pre_btn);
        mPlayNextBtn = (Button) findViewById(R.id.play_next_btn);
        mPlayPlayBtn = (Button) findViewById(R.id.play_play_btn);
        mPlayPauseBtn = (Button) findViewById(R.id.play_pause_btn);

        mPlayMusicNameTv = (TextView) findViewById(R.id.play_musicname_tv);
        mPlayArtistTv = (TextView) findViewById(R.id.play_artist_tv);
        mPlayCurTimeTv = (TextView) findViewById(R.id.play_currentTime_tv);
        mPlayTotalTimeTv = (TextView) findViewById(R.id.play_totalTime_tv);

        mWhiteMusicSlidingDrawer.setOnDrawerCloseListener(this);
        mWhiteMusicSlidingDrawer.setOnDrawerOpenListener(this);

        mPlayPreBtn.setOnClickListener(this);
        mPlayNextBtn.setOnClickListener(this);
        mPlayPlayBtn.setOnClickListener(this);
        mPlayPauseBtn.setOnClickListener(this);

        mPlaybackSeekBar = (SeekBar) findViewById(R.id.play_playback_seekbar);
        mPlaybackSeekBar.setOnSeekBarChangeListener(this);

    }

    public void refreshSeekProgress(int curTime, int totalTime) {

        int tempCurTime = curTime;
        curTime /= 1000;
        totalTime /= 1000;
        int curminute = curTime / 60;
        int cursecond = curTime % 60;

        String curTimeString = String.format("%02d:%02d", curminute, cursecond);
        mPlayCurTimeTv.setText(curTimeString);

        int rate = 0;
        if (totalTime != 0) {
            rate = (int) ((float) curTime / totalTime * 100);
        }
        mPlaybackSeekBar.setProgress(rate);
    }

    public void refreshUI(int curTime, int totalTime, WhiteMusicInfoBean pWhiteMusicInfoBean) {

        mCurrentWhiteMusicInfoBean = pWhiteMusicInfoBean;

        int tempCurTime = curTime;
        int tempTotalTime = totalTime;

        totalTime /= 1000;
        int totalminute = totalTime / 60;
        int totalsecond = totalTime % 60;
        String totalTimeString = String.format("%02d:%02d", totalminute,
                totalsecond);

        mPlayTotalTimeTv.setText(totalTimeString);
        mPlayMusicNameTv.setText(pWhiteMusicInfoBean.getMusicName());
        mPlayArtistTv.setText(pWhiteMusicInfoBean.getArtistName());

        ContentResolver res = mActivity.getContentResolver();
        Bitmap bitmap = Utils.createThumbFromUir(res, pWhiteMusicInfoBean.getMusicAlbumUri());
        mPlayHeadiconIv.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), bitmap));

        refreshSeekProgress(tempCurTime, tempTotalTime);
    }

    public void showPlay(boolean flag) {
        if (flag) {
            mPlayPlayBtn.setVisibility(View.VISIBLE);
            mPlayPauseBtn.setVisibility(View.GONE);
        } else {
            mPlayPlayBtn.setVisibility(View.GONE);
            mPlayPauseBtn.setVisibility(View.VISIBLE);
        }
    }

    private View findViewById(int id) {
        return mView.findViewById(id);
    }

    public void openPlayView() {
        mWhiteMusicSlidingDrawer.setVisibility(View.VISIBLE);
        mWhiteMusicSlidingDrawer.animateOpen();
    }

    public void closePlayView() {
        mWhiteMusicSlidingDrawer.animateClose();
    }

    public boolean isOpenedPlayView() {
        return mWhiteMusicSlidingDrawer.isOpened();
    }

    // View中按钮监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_play_btn:
                if(null == mCurrentWhiteMusicInfoBean) {
                    return;
                }
                mWhiteMusicServiceManager.musicPlay();
                break;
            case R.id.play_pause_btn:
                if(null == mCurrentWhiteMusicInfoBean) {
                    return;
                }
                mWhiteMusicServiceManager.musicPlayPause();
                break;
            case R.id.play_pre_btn:
                mWhiteMusicServiceManager.musicPlayPre();
                break;
            case R.id.play_next_btn:
                mWhiteMusicServiceManager.musicPlayNext();
                break;
        }
    }
    public void setListViewAdapter(WhiteMusicListAdapter pWhiteMusicListAdapter) {
        mWhiteMusicListAdapter = pWhiteMusicListAdapter;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onDrawerClosed() {
        if (mListView != null) {
            mListView.setVisibility(View.VISIBLE);
        }
        if (mGridView != null) {
            mGridView.setVisibility(View.VISIBLE);
        }
        mWhiteMusicSlidingDrawer.setVisibility(View.GONE);
//        if (mListNeedRefresh) {
//			if (mIsFavorite) {
//				mAdapter.refreshFavoriteById(mCurrentMusicInfo.songId, 1);
//			} else {
//				mAdapter.refreshFavoriteById(mCurrentMusicInfo.songId, 0);
//			}
//        }
    }

    @Override
    public void onDrawerOpened() {
        if (mListView != null) {
            mListView.setVisibility(View.INVISIBLE);
        }
        if (mGridView != null) {
            mGridView.setVisibility(View.INVISIBLE);
        }
//        if (!mIsLyricDownloading) {
            // 读取歌词文件
//			loadLyric(mCurrentMusicInfo);
//        }
    }
}
