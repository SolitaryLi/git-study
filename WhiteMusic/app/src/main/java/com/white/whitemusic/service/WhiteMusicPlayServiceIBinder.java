package com.white.whitemusic.service;

import android.os.Binder;

import com.white.whitemusic.bean.WhiteMusicInfoBean;

import java.util.List;

public class WhiteMusicPlayServiceIBinder extends Binder {

    // 添加
    public void addMusicPlayList(List<WhiteMusicInfoBean> lsWhiteMusicInfoBean) {

    }

    public void addMusicPlayList(WhiteMusicInfoBean whiteMusicInfoBean) {

    }

    // 播放当前
    public void musicPlay() {

    }
    // 播放下一曲
    public void musicPlayNext() {

    }
    // 播放上一曲
    public void musicPlayPre() {

    }
    // 播放暂停
    public void musicPlayPause() {

    }
    //
    public void musicSeekTo() {

    }
    // 注册监听事件
    public void registerOnMusicStataChangeListener() {

    }
    // 释放监听事件
    public void unregisterOnMusicStataChangeListener() {

    }
    // 获取当前播放的音乐
    public WhiteMusicInfoBean getCurrentMusic() {

    }
    // 判断当前播放器是否正在运行
    public boolean isMusicPlaying() {

    }
    // 获取播放列表
    public List<WhiteMusicInfoBean> getMusicPlayList() {

    }
}
