package com.white.whitemusic.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.helper.DBHelper;
import com.white.whitemusic.helper.PlayListContentProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class MusicService extends Service{

    private List<WhiteMusicInfoBean> mPlayList;
    private ContentResolver mResolver;
    private MediaPlayer mMusicPlayer;

    //当前是否为播放暂停状态
    private boolean mPaused;
    //存放当前要播放的音乐
    private WhiteMusicInfoBean mCurrentMusicItem;

    //定义循环发送的消息
    private final int MSG_PROGRESS_UPDATE = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        //创建
        mMusicPlayer = new MediaPlayer();

        initPlayList();
        //获取ContentProvider的解析器，避免以后每次使用的时候都要重新获取
        mResolver = getContentResolver();
        //保存播放列表
        mPlayList = new ArrayList<WhiteMusicInfoBean>();

        mMusicPlayer = new MediaPlayer();
        mMusicPlayer.setOnCompletionListener(mOnCompletionListener);
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //将当前播放的音乐记录时间重置为0，更新到数据库
            //下次播放就可以从头开始
//            mCurrentMusicItem.playedTime = 0;
            updateMusicItem(mCurrentMusicItem);
            //播放下一首音乐
            playNextInner();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        //释放
        mMusicPlayer.release();

        //当MusicService销毁的时候，清空监听器列表
        mListenerList.clear();

        mPaused = false;

        //停止更新
        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
    }

    //将要播放的音乐载入MediaPlayer，但是并不播放
    private void prepareToPlay(WhiteMusicInfoBean item) {
        try {
            //重置播放器状态
            mMusicPlayer.reset();
            //设置播放音乐的地址
            mMusicPlayer.setDataSource(MusicService.this, item.getSongUri());
            //准备播放音乐
            mMusicPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放音乐，根据reload标志位判断是非需要重新加载音乐
    private void playMusicItem(WhiteMusicInfoBean item, boolean reload) {
        //如果这里传入的是空值，就什么也不做
        if(item == null) {
            return;
        }

        //移除现有的更新消息，重新启动更新
        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);

        if(reload) {
            //需要重新加载音乐
            prepareToPlay(item);
        }
        //开始播放，如果之前只是暂停播放，那么音乐将继续播放
        mMusicPlayer.start();
        //将音乐设置到指定时间开始播放，时间单位为毫秒
//        seekToInner((int)item.playedTime);
        //将播放的状态通过监听器通知给监听者
        for(OnStateChangeListenr l : mListenerList) {
            l.onPlay(item);
        }
        //设置为非暂停播放状态
        mPaused = false;
    }

    //播放播放列表中，当前音乐的下一首音乐
    private void playNextInner() {
        int currentIndex = mPlayList.indexOf(mCurrentMusicItem);
        if(currentIndex < mPlayList.size() -1 ) {
            //获取当前播放（或者被加载）音乐的下一首音乐
            //如果后面有要播放的音乐，把那首音乐设置成要播放的音乐
            //并重新加载该音乐，开始播放
            mCurrentMusicItem = mPlayList.get(currentIndex + 1);
            playMusicItem(mCurrentMusicItem, true);
        }
    }

    private void playInner() {
        //如果之前没有选定要播放的音乐，就选列表中的第一首音乐开始播放
        if(mCurrentMusicItem == null && mPlayList.size() > 0) {
            mCurrentMusicItem = mPlayList.get(0);
        }

        //如果是从暂停状态恢复播放音乐，那么不需要重新加载音乐；
        //如果是从完全没有播放过的状态开始播放音乐，那么就需要重新加载音乐
        if(mPaused) {
            playMusicItem(mCurrentMusicItem, false);
        }
        else {
            playMusicItem(mCurrentMusicItem, true);
        }
    }

    private void playPreInner() {
        int currentIndex = mPlayList.indexOf(mCurrentMusicItem);
        if(currentIndex - 1 >= 0 ) {
            //获取当前播放（或者被加载）音乐的上一首音乐
            //如果前面有要播放的音乐，把那首音乐设置成要播放的音乐
            //并重新加载该音乐，开始播放
            mCurrentMusicItem = mPlayList.get(currentIndex - 1);
            playMusicItem(mCurrentMusicItem, true);
        }
    }

    private void pauseInner() {
        //暂停当前正在播放的音乐
        mMusicPlayer.pause();
        //将播放状态的改变通知给监听者
        for(OnStateChangeListenr l : mListenerList) {
            l.onPause(mCurrentMusicItem);
        }
        //停止更新
        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
        //设置为暂停播放状态
        mPaused = true;
    }

    private void seekToInner(int pos) {
        //将音乐拖动到指定的时间
        mMusicPlayer.seekTo(pos);
    }

    private WhiteMusicInfoBean getCurrentMusicInner() {
        //返回当前正加载好的音乐
        return mCurrentMusicItem;
    }

    private boolean isPlayingInner() {
        //返回当前的播放器是非正在播放音乐
        return mMusicPlayer.isPlaying();
    }

    public interface OnStateChangeListenr {

        void onPlayProgressChange(WhiteMusicInfoBean item);
        void onPlay(WhiteMusicInfoBean item);
        void onPause(WhiteMusicInfoBean item);
    }

    //创建存储监听器的列表
    private List<OnStateChangeListenr> mListenerList = new ArrayList<OnStateChangeListenr>();


    private void initPlayList() {
        mPlayList.clear();

        Cursor cursor = mResolver.query(
                PlayListContentProvider.CONTENT_SONGS_URI,
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {
            String songUri = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.SONG_URI));
            String albumUri = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALBUM_URI));
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
            long playedTime = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.LAST_PLAY_TIME));
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.DURATION));
            WhiteMusicInfoBean item = new WhiteMusicInfoBean(name, Uri.parse(songUri), Uri.parse(albumUri), duration);
            mPlayList.add(item);
        }

        cursor.close();

        if( mPlayList.size() > 0) {
            mCurrentMusicItem = mPlayList.get(0);
        }
    }

    public class MusicServiceIBinder extends Binder {

        public void addPlayList(List<WhiteMusicInfoBean> items) {
            addPlayListInner(items);
        }

        public void addPlayList(WhiteMusicInfoBean item) {
            addPlayListInner(item);
        }

        public void play() {
            playInner();

        }

        public void playNext() {
            playNextInner();

        }

        public void playPre() {
            playPreInner();
        }

        public void pause() {
            pauseInner();
        }

        public void seekTo(int pos) {
            seekToInner(pos);
        }

        public void registerOnStateChangeListener(OnStateChangeListenr l) {
            //将监听器添加到列表
            mListenerList.add(l);
        }

        public void unregisterOnStateChangeListener(OnStateChangeListenr l) {
            //将监听器从列表中移除
            mListenerList.remove(l);
        }

        public WhiteMusicInfoBean getCurrentMusic() {
            return getCurrentMusicInner();
        }

        public boolean isPlaying() {
            return isPlayingInner();
        }

        public List<WhiteMusicInfoBean> getPlayList() {
            return mPlayList;
        }

    }

    //真正实现功能的方法
    public void addPlayListInner(List<WhiteMusicInfoBean> items) {
        //清空数据库中的playlist_table
        mResolver.delete(PlayListContentProvider.CONTENT_SONGS_URI, null, null);
        //清空缓存的播放列表
        mPlayList.clear();

//        //将每首音乐添加到播放列表的缓存和数据库中
//        for (WhiteMusicInfoBean item : items) {
//            //利用现成的代码，便于代码的维护
//            addPlayListInner(item);
//        }

        for (WhiteMusicInfoBean item : items) {
            //逐个添加到播放列表，不要求添加后播放
            addPlayListInner(item, false);
        }

        //添加完成后，开始播放
        mCurrentMusicItem = mPlayList.get(0);
        playInner();
    }

    public void addPlayListInner(WhiteMusicInfoBean item, boolean needPlay) {
        if(mPlayList.contains(item)) {
            return;
        }

        mPlayList.add(0, item);

        insertMusicItemToContentProvider(item);

        if(needPlay) {
            //添加完成后，开始播放
            mCurrentMusicItem = mPlayList.get(0);
            playInner();
        };
    }

    //访问ContentProvider，保存一条数据
    private void insertMusicItemToContentProvider(WhiteMusicInfoBean item) {

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.NAME, item.getName());
        cv.put(DBHelper.DURATION, item.getDuration());
//        cv.put(DBHelper.LAST_PLAY_TIME, item.get.playedTime);
        cv.put(DBHelper.SONG_URI, item.getSongUri().toString());
//        cv.put(DBHelper.ALBUM_URI, item.albumUri.toString());
        Uri uri = mResolver.insert(PlayListContentProvider.CONTENT_SONGS_URI, cv);
    }



    public void registerOnStateChangeListenerInner(OnStateChangeListenr l) {

    }

    public void unregisterOnStateChangeListenerInner(OnStateChangeListenr l) {

    }


    //创建Binder实例
    private final IBinder mBinder = new MusicServiceIBinder();

    @Override
    public IBinder onBind(Intent intent) {
        //当组件bindService()之后，将这个Binder返回给组件使用
        return mBinder;
    }

    //定义处理消息的Handler
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_UPDATE: {
                    //将音乐的时长和当前播放的进度保存到MusicItem数据结构中，
//                    mCurrentMusicItem.playedTime = mMusicPlayer.getCurrentPosition();
                    mCurrentMusicItem.setDuration(mMusicPlayer.getDuration());

                    //通知监听者当前的播放进度
                    for(OnStateChangeListenr l : mListenerList) {
                        l.onPlayProgressChange(mCurrentMusicItem);
                    }

                    //将当前的播放进度保存到数据库中
                    updateMusicItem(mCurrentMusicItem);

                    //间隔一秒发送一次更新播放进度的消息
                    sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 1000);
                }
                break;
            }
        }
    };

    //将播放时间更新到ContentProvider中
    private void updateMusicItem(WhiteMusicInfoBean item) {

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.DURATION, item.getDuration());
//        cv.put(DBHelper.LAST_PLAY_TIME, item.playedTime);

        String strUri = item.getSongUri().toString();
        mResolver.update(PlayListContentProvider.CONTENT_SONGS_URI, cv, DBHelper.SONG_URI + "=\"" + strUri + "\"", null);
    }
}
