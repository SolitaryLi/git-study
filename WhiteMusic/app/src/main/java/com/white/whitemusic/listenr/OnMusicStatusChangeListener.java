package com.white.whitemusic.listenr;

import com.white.whitemusic.bean.WhiteMusicInfoBean;

// Music 状态变更监听
public interface OnMusicStatusChangeListener {
    void onMusicPlayProgressChange(WhiteMusicInfoBean whiteMusicInfoBean);
    void onMusicPlay(WhiteMusicInfoBean whiteMusicInfoBean);
    void onMusicPause(WhiteMusicInfoBean whiteMusicInfoBean);
}
