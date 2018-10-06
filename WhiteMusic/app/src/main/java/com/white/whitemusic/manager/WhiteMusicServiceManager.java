package com.white.whitemusic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.listenr.OnMusicStatusChangeListener;
import com.white.whitemusic.service.WhiteMusicPlayService;

import java.util.List;

public class WhiteMusicServiceManager {

    private Context mContext;
    private ServiceConnection mServiceConnection;
    private WhiteMusicPlayService.WhiteMusicPlayServiceIBinder whiteMusicPlayService;

    public WhiteMusicServiceManager(Context context) {
        this.mContext = context;
        initConn();
    }

    private void initConn() {
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //绑定成功后，取得MusicSercice提供的接口
                whiteMusicPlayService = (WhiteMusicPlayService.WhiteMusicPlayServiceIBinder) service;
            }
        };
    }

    public void connectService() {
        // Android 5.0 之后版本禁止改种写法
//        Intent intent = new Intent("com.white.whitemusic.service.WhiteMusicPlayService");
        Intent intent = new Intent(mContext, WhiteMusicPlayService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void disConnectService() {
        mContext.unbindService(mServiceConnection);
        // Android 5.0 之后版本禁止改种写法
//        mContext.stopService(new Intent("com.white.whitemusic.service.WhiteMusicPlayService"));
        mContext.stopService(new Intent(mContext, WhiteMusicPlayService.class));
    }

    // 添加
    public void addMusicPlayList(List<WhiteMusicInfoBean> lsWhiteMusicInfoBean) {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.addMusicPlayList(lsWhiteMusicInfoBean);
        }
    }

    public void addMusicPlayList(WhiteMusicInfoBean whiteMusicInfoBean) {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.addMusicPlayList(whiteMusicInfoBean);
        }
    }

    // 播放当前
    public void musicPlay() {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.musicPlay();
        }
    }

    // 播放指定音乐
    public void musicPlay(int position) {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.musicPlay(position);
        }
    }
    // 播放下一曲
    public void musicPlayNext() {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.musicPlayNext();
        }
    }
    // 播放上一曲
    public void musicPlayPre() {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.musicPlayPre();
        }
    }
    // 播放暂停
    public void musicPlayPause() {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.musicPlayPause();
        }
    }
    //
    public void musicSeekTo(int pos) {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.musicSeekTo(pos);
        }
    }
    // 注册监听事件
    public void registerOnMusicStataChangeListener(OnMusicStatusChangeListener onMusicStatusChangeListener) {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.registerOnMusicStataChangeListener(onMusicStatusChangeListener);
        }
    }
    // 释放监听事件
    public void unregisterOnMusicStataChangeListener(OnMusicStatusChangeListener onMusicStatusChangeListener) {
        if (null != whiteMusicPlayService) {
            whiteMusicPlayService.unregisterOnMusicStataChangeListener(onMusicStatusChangeListener);
        }
    }
    // 获取当前播放的音乐
    public WhiteMusicInfoBean getCurrentMusic() {
        if (null != whiteMusicPlayService) {
            return whiteMusicPlayService.getCurrentMusic();
        }
        return null;
    }
    // 判断当前播放器是否正在运行
    public boolean isMusicPlaying() {
        if (null != whiteMusicPlayService) {
            return whiteMusicPlayService.isMusicPlaying();
        }
        return false;
    }
    // 获取播放列表
    public List<WhiteMusicInfoBean> getMusicPlayList() {
        if (null != whiteMusicPlayService) {
            return whiteMusicPlayService.getMusicPlayList();
        }
        return null;
    }

    public int getCurrentMusicPosition() {
        if (null != whiteMusicPlayService) {
            return whiteMusicPlayService.getCurrentMusicPosition();
        }
        return 0;
    }

    public int getCurrentMusicDuration() {
        if (null != whiteMusicPlayService) {
            return whiteMusicPlayService.getCurrentMusicDuration();
        }
        return 0;
    }
}
