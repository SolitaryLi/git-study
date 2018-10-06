package com.white.whitemusic.bean;

import android.graphics.Bitmap;
import android.net.Uri;

// 存储Music信息
public class WhiteMusicInfoBean {
    // 存储音乐的名字
    String musicName;
    // 存储艺术家的名字
    String artistName;
     // 存储音乐的Uri地址
    Uri musicUri;
    // 存储音乐封面的Uri地址
    Uri musicAlbumUri;
    // 存储封面图片
    Bitmap musicThumb;
    // 存储音乐的播放时长，单位是毫秒
    long musicDuration;

    long musicPlayTime;

    public WhiteMusicInfoBean(String musicName, String artistName, Uri musicUri, Uri musicAlbumUri, Bitmap musicThumb, long musicDuration, long musicPlayTime) {
        this.musicName = musicName;
        this.artistName = artistName;
        this.musicUri = musicUri;
        this.musicAlbumUri = musicAlbumUri;
        this.musicThumb = musicThumb;
        this.musicDuration = musicDuration;
        this.musicPlayTime = musicPlayTime;
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

    public long getMusicPlayTime() {
        return musicPlayTime;
    }

    public void setMusicPlayTime(long musicPlayTime) {
        this.musicPlayTime = musicPlayTime;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    //重写WhiteMusicInfoBean的equals()方法
    @Override
    public boolean equals(Object object) {
        WhiteMusicInfoBean whiteMusicInfoBean = (WhiteMusicInfoBean) object;
        //音乐的Uri相同，则说明两者相同
        return whiteMusicInfoBean.getMusicUri().equals(this.musicUri);
    }
}
