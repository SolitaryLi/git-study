package com.white.whitemusic.service;

import android.app.Service;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.listenr.OnMusicStatusChangeListener;

import java.util.ArrayList;
import java.util.List;

// Music 控制详细处理
public class WhiteMusicPlayService extends Service {

    private List<OnMusicStatusChangeListener> lsMusicStatusChangeListener = new ArrayList<OnMusicStatusChangeListener>();
    // 已播放列表
    private List<WhiteMusicInfoBean> lsWhiteMusicInfoBean;
    // 当前播放的音乐
    private WhiteMusicInfoBean currentMusicInfoBean;
    // 播放组件
    private MediaPlayer mediaPlayer;

    private ContentResolver contentRrovider;
    // 音乐是否正在播放
    private boolean musicPaused;

    private final IBinder iBinder = new WhiteMusicPlayServiceIBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        mediaPlayer = new MediaPlayer();
        contentRrovider = getContentResolver();
        lsWhiteMusicInfoBean = new ArrayList<WhiteMusicInfoBean>();
        musicPaused = false;



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();

        // 监听清空
        lsMusicStatusChangeListener.clear();

        for (WhiteMusicInfoBean whiteMusicInfoBean : lsWhiteMusicInfoBean) {
            if (whiteMusicInfoBean.getMusicThumb() != null) {
                whiteMusicInfoBean.getMusicThumb().recycle();
            }
        }

        // 播放列表清空
        lsWhiteMusicInfoBean.clear();


    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }



}
