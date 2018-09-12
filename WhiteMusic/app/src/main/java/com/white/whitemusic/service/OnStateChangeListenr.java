package com.white.whitemusic.service;


import com.white.whitemusic.bean.WhiteMusicInfoBean;

public interface OnStateChangeListenr {
    //用来通知播放进度
    void onPlayProgressChange(WhiteMusicInfoBean musicInformationBean);
    //用来通知当前处于播放状态
    void onPlay(WhiteMusicInfoBean musicInformationBean);
    //用来通知当前处于暂停或停止状态
    void onPause(WhiteMusicInfoBean musicInformationBean);
}
