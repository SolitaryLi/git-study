package com.white.whitemusic.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.helper.WhiteMusicDBHelper;
import com.white.whitemusic.helper.WhiteMusicListContentProvider;
import com.white.whitemusic.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service{

    public interface OnStateChangeListenr {

        void onPlayProgressChange(WhiteMusicInfoBean item);
        void onPlay(WhiteMusicInfoBean item);
        void onPause(WhiteMusicInfoBean item);
    }

    private final int MSG_PROGRESS_UPDATE = 0;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_UPDATE: {
                    mCurrentMusicItem.setMusicPlayTime(mMusicPlayer.getCurrentPosition());
                    mCurrentMusicItem.setMusicDuration(mMusicPlayer.getDuration());

                    for(OnStateChangeListenr l : mListenerList) {
                        l.onPlayProgressChange(mCurrentMusicItem);
                    }

                    updateMusicItem(mCurrentMusicItem);

                    sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 1000);
                }
                break;
            }
        }
    };

    public static final String ACTION_PLAY_MUSIC_PRE = "com.anddle.anddlemusic.playpre";
    public static final String ACTION_PLAY_MUSIC_NEXT = "com.anddle.anddlemusic.playnext";
    public static final String ACTION_PLAY_MUSIC_TOGGLE = "com.anddle.anddlemusic.playtoggle";
    public static final String ACTION_PLAY_MUSIC_UPDATE = "com.anddle.anddlemusic.playupdate";

    private List<OnStateChangeListenr> mListenerList = new ArrayList<OnStateChangeListenr>();
    private List<WhiteMusicInfoBean> mPlayList;

    private WhiteMusicInfoBean mCurrentMusicItem;
    private MediaPlayer mMusicPlayer;
    private ContentResolver mResolver;
    private boolean mPaused;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ACTION_PLAY_MUSIC_UPDATE.equals(action)) {
                updateAppWidget(mCurrentMusicItem);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (ACTION_PLAY_MUSIC_PRE.equals(action)) {
                    playPreInner();
                } else if (ACTION_PLAY_MUSIC_NEXT.equals(action)) {
                    playNextInner();
                } else if (ACTION_PLAY_MUSIC_TOGGLE.equals(action)) {
                    if (isPlayingInner()) {
                        pauseInner();
                    } else {
                        playInner();
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMusicPlayer = new MediaPlayer();
        mResolver = getContentResolver();
        mPlayList = new ArrayList<WhiteMusicInfoBean>();
        mPaused = false;
        // TODO
        mMusicPlayer.setOnCompletionListener(mOnCompletionListener);

        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(ACTION_PLAY_MUSIC_UPDATE);
        registerReceiver(mIntentReceiver, commandFilter);

        initPlayList();

        if(mCurrentMusicItem != null) {

            prepareToPlay(mCurrentMusicItem);
        }

        updateAppWidget(mCurrentMusicItem);

    }

    private void prepareToPlay(WhiteMusicInfoBean item) {
        try {
            mMusicPlayer.reset();
            mMusicPlayer.setDataSource(MusicService.this, item.getMusicUri());
            mMusicPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mMusicPlayer.isPlaying()) {
            mMusicPlayer.stop();
        }
        mMusicPlayer.release();

        unregisterReceiver(mIntentReceiver);
        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
        mListenerList.clear();
        for(WhiteMusicInfoBean item : mPlayList) {
            if(item.getMusicThumb() != null) {
                item.getMusicThumb().recycle();
            }
        }

        mPlayList.clear();
    }

    public class MusicServiceIBinder extends Binder {

        public void addPlayList(List<WhiteMusicInfoBean> items) {
            addPlayListInner(items);
        }

        public void addPlayList(WhiteMusicInfoBean item) {
            addPlayListInner(item, true);
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
            registerOnStateChangeListenerInner(l);

        }

        public void unregisterOnStateChangeListener(OnStateChangeListenr l) {
            unregisterOnStateChangeListenerInner(l);
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

    private final IBinder mBinder = new MusicServiceIBinder();

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    private void addPlayListInner(List<WhiteMusicInfoBean> items) {

        mResolver.delete(WhiteMusicListContentProvider.CONTENT_SONGS_URI, null, null);
        mPlayList.clear();

        for (WhiteMusicInfoBean item : items) {
            addPlayListInner(item, false);
        }

        mCurrentMusicItem = mPlayList.get(0);
        playInner();
    }

    private void addPlayListInner(WhiteMusicInfoBean item, boolean needPlay) {

        if(mPlayList.contains(item)) {
            return;
        }

        mPlayList.add(0, item);

        insertMusicItemToContentProvider(item);

        if(needPlay) {
            mCurrentMusicItem = mPlayList.get(0);
            playInner();
        }
    }

    private void playNextInner() {
        int currentIndex = mPlayList.indexOf(mCurrentMusicItem);
        if(currentIndex < mPlayList.size() -1 ) {

            mCurrentMusicItem = mPlayList.get(currentIndex + 1);
            playMusicItem(mCurrentMusicItem, true);
        }
    }

    private void playInner() {
        if(mCurrentMusicItem == null && mPlayList.size() > 0) {
            mCurrentMusicItem = mPlayList.get(0);
        }

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

            mCurrentMusicItem = mPlayList.get(currentIndex - 1);
            playMusicItem(mCurrentMusicItem, true);
        }
    }

    private void pauseInner() {

        mPaused = true;
        mMusicPlayer.pause();
        for(OnStateChangeListenr l : mListenerList) {
            l.onPause(mCurrentMusicItem);
        }
        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
        updateAppWidget(mCurrentMusicItem);
    }

    private void seekToInner(int pos) {
        mMusicPlayer.seekTo(pos);
    }

    private void registerOnStateChangeListenerInner(OnStateChangeListenr l) {
        mListenerList.add(l);
    }

    private void unregisterOnStateChangeListenerInner(OnStateChangeListenr l) {
        mListenerList.remove(l);
    }

    private WhiteMusicInfoBean getCurrentMusicInner() {
        return mCurrentMusicItem;
    }

    private boolean isPlayingInner() {
        return mMusicPlayer.isPlaying();
    }

    private void initPlayList() {
        mPlayList.clear();

        Cursor cursor = mResolver.query(
                WhiteMusicListContentProvider.CONTENT_SONGS_URI,
                null,
                null,
                null,
                null);

        while(cursor.moveToNext())
        {
            String songUri = cursor.getString(cursor.getColumnIndexOrThrow(WhiteMusicDBHelper.MUSIC_LIST_URI));
            String albumUri = cursor.getString(cursor.getColumnIndexOrThrow(WhiteMusicDBHelper.MUSIC_LIST_ALBUM_URI));
            String name = cursor.getString(cursor.getColumnIndex(WhiteMusicDBHelper.MUSIC_LIST_NAME));
            long playedTime = cursor.getLong(cursor.getColumnIndexOrThrow(WhiteMusicDBHelper.LAST_PLAY_TIME));
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(WhiteMusicDBHelper.MUSIC_LIST_DURATION));

            WhiteMusicInfoBean item = new WhiteMusicInfoBean(
                    name, Uri.parse(songUri), Uri.parse(albumUri),  null, duration, playedTime/*, isLastPlaying*/);
            mPlayList.add(item);
        }

        cursor.close();

        if( mPlayList.size() > 0) {
            mCurrentMusicItem = mPlayList.get(0);
        }
    }

    private void playMusicItem(WhiteMusicInfoBean item, boolean reload) {
        if(item == null) {
            return;
        }

        if(reload) {
            prepareToPlay(item);
        }

        mMusicPlayer.start();
        seekToInner((int)item.getMusicPlayTime());
        for(OnStateChangeListenr l : mListenerList) {
            l.onPlay(item);
        }
        mPaused = false;

        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);

        updateAppWidget(mCurrentMusicItem);
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {

            mCurrentMusicItem.setMusicPlayTime(0);
            updateMusicItem(mCurrentMusicItem);
            playNextInner();
        }
    };

    private void insertMusicItemToContentProvider(WhiteMusicInfoBean item) {

        ContentValues cv = new ContentValues();
        cv.put(WhiteMusicDBHelper.MUSIC_LIST_NAME, item.getMusicName());
        cv.put(WhiteMusicDBHelper.MUSIC_LIST_DURATION, item.getMusicDuration());
        cv.put(WhiteMusicDBHelper.LAST_PLAY_TIME, item.getMusicPlayTime());
        cv.put(WhiteMusicDBHelper.MUSIC_LIST_URI, item.getMusicUri().toString());
        cv.put(WhiteMusicDBHelper.MUSIC_LIST_ALBUM_URI, item.getMusicAlbumUri().toString());
        Uri uri = mResolver.insert(WhiteMusicListContentProvider.CONTENT_SONGS_URI, cv);
    }

    private void updateMusicItem(WhiteMusicInfoBean item) {

        ContentValues cv = new ContentValues();
        cv.put(WhiteMusicDBHelper.MUSIC_LIST_DURATION, item.getMusicDuration());
        cv.put(WhiteMusicDBHelper.LAST_PLAY_TIME, item.getMusicPlayTime());

        String strUri = item.getMusicUri().toString();
        mResolver.update(WhiteMusicListContentProvider.CONTENT_SONGS_URI, cv, WhiteMusicDBHelper.MUSIC_LIST_URI + "=\"" + strUri + "\"", null);
    }

    private void updateAppWidget(WhiteMusicInfoBean item) {
        if (item != null) {
            if(item.getMusicThumb() == null) {
                ContentResolver res = getContentResolver();
                item.setMusicThumb(Utils.createThumbFromUir(res, item.getMusicAlbumUri()));
            }
//            AnddleMusicAppWidget.performUpdates(MusicService.this, item.name, isPlayingInner(), item.thumb);
        }
    }
}
