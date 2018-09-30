package com.white.whitemusic.listenr;

import com.white.whitemusic.bean.WhiteMusicInfoBean;

// Music 状态变更监听
public interface OnMusicStatusChangeListener {
    //用来通知播放进度
    void onMusicPlayProgressChange(WhiteMusicInfoBean whiteMusicInfoBean);
    //用来通知当前处于播放状态
    void onMusicPlay(WhiteMusicInfoBean whiteMusicInfoBean);
    //用来通知当前处于暂停或停止状态
    void onMusicPause(WhiteMusicInfoBean whiteMusicInfoBean);
}
