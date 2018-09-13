package com.white.whitemusic.bean;

import android.graphics.Bitmap;
import android.net.Uri;

// 存储Music信息
public class WhiteMusicInfoBean {
    // 存储音乐的名字
    String musicName;
     // 存储音乐的Uri地址
    Uri musicUri;
    // 存储音乐封面的Uri地址
    Uri musicAlbumUri;
    // 存储封面图片
    Bitmap musicThumb;
    // 存储音乐的播放时长，单位是毫秒
    long musicDuration;

    public WhiteMusicInfoBean(String musicName, Uri musicUri, Uri musicAlbumUri, Bitmap musicThumb, long musicDuration) {
        this.musicName = musicName;
        this.musicUri = musicUri;
        this.musicAlbumUri = musicAlbumUri;
        this.musicThumb = musicThumb;
        this.musicDuration = musicDuration;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public Uri getMusicUri() {
        return musicUri;
    }

    public void setMusicUri(Uri musicUri) {
        this.musicUri = musicUri;
    }

    public Uri getMusicAlbumUri() {
        return musicAlbumUri;
    }

    public void setMusicAlbumUri(Uri musicAlbumUri) {
        this.musicAlbumUri = musicAlbumUri;
    }

    public Bitmap getMusicThumb() {
        return musicThumb;
    }

    public void setMusicThumb(Bitmap musicThumb) {
        this.musicThumb = musicThumb;
    }

    public long getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(long musicDuration) {
        this.musicDuration = musicDuration;
    }

    //重写WhiteMusicInfoBean的equals()方法
    @Override
    public boolean equals(Object object) {
        WhiteMusicInfoBean whiteMusicInfoBean = (WhiteMusicInfoBean) object;
        //音乐的Uri相同，则说明两者相同
        return whiteMusicInfoBean.getMusicUri().equals(this.musicUri);
    }
}
