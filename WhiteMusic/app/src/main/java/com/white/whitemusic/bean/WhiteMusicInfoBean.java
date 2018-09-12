package com.white.whitemusic.bean;

import android.graphics.Bitmap;
import android.net.Uri;

public class WhiteMusicInfoBean {
    // 存储音乐的名字
    String name;
     // 存储音乐的Uri地址
    Uri songUri;
    // 存储音乐封面的Uri地址
    Uri albumUri;
    // 存储封面图片
    Bitmap thumb;
    // 存储音乐的播放时长，单位是毫秒
    long duration;

    public WhiteMusicInfoBean(String name, Uri songUri, Uri albumUri, long duration) {
        this.name = name;
        this.songUri = songUri;
        this.albumUri = albumUri;
        this.thumb = thumb;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getSongUri() {
        return songUri;
    }

    public void setSongUri(Uri songUri) {
        this.songUri = songUri;
    }

    public Uri getAlbumUri() {
        return albumUri;
    }

    public void setAlbumUri(Uri albumUri) {
        this.albumUri = albumUri;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    //重写MusicItem的equals()方法
    @Override
    public boolean equals(Object o) {
        WhiteMusicInfoBean another = (WhiteMusicInfoBean) o;

        //音乐的Uri相同，则说明两者相同
        return another.songUri.equals(this.songUri);
    }
}
