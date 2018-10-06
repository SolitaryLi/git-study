package com.white.whitemusic.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
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
import android.support.annotation.Nullable;

import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.constant.WhiteMusicConstant;
import com.white.whitemusic.helper.WhiteMusicDBHelper;
import com.white.whitemusic.helper.WhiteMusicListContentProvider;
import com.white.whitemusic.listenr.OnMusicStatusChangeListener;
import com.white.whitemusic.utils.Utils;

import java.util.ArrayList;
import java.util.List;

// Music 控制详细处理
public class WhiteMusicPlayService extends Service {

    private List<OnMusicStatusChangeListener> lsMusicStatusChangeListener = new ArrayList<OnMusicStatusChangeListener>();
    // 播放列表
    private List<WhiteMusicInfoBean> lsWhiteMusicInfoBean;
    // 当前播放的音乐
    private WhiteMusicInfoBean currentMusicInfoBean;
    // 播放组件
    private MediaPlayer mediaPlayer;

    private ContentResolver contentRrovider;
    // 音乐是否正在播放
    private boolean musicPaused;

    private final IBinder iBinder = new WhiteMusicPlayServiceIBinder();

    private final int MSG_PROGRESS_UPDATE = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_UPDATE: {
                    //
                    currentMusicInfoBean.setMusicPlayTime(mediaPlayer.getCurrentPosition());
                    currentMusicInfoBean.setMusicDuration(mediaPlayer.getDuration());

                    for (OnMusicStatusChangeListener onMusicStatusChangeListener : lsMusicStatusChangeListener) {
                        onMusicStatusChangeListener.onMusicPlayProgressChange(currentMusicInfoBean);
                    }

                    updateMusicItemInfo(currentMusicInfoBean);
                    sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 1000);
                }
                break;
            }
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            currentMusicInfoBean.setMusicPlayTime(0);
            updateMusicItemInfo(currentMusicInfoBean);
            musicPlayNextInner();
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WhiteMusicConstant.ACTION_PLAY_MUSIC_UPDATE.equals(action)) {
                // TODO
                updateAppWidget(currentMusicInfoBean);
            }
        }
    };

    public class WhiteMusicPlayServiceIBinder extends Binder {
        // 添加
        public void addMusicPlayList(List<WhiteMusicInfoBean> lsWhiteMusicInfoBean) {
            addMusicPlayListInner(lsWhiteMusicInfoBean);
        }

        public void addMusicPlayList(WhiteMusicInfoBean whiteMusicInfoBean) {
            addMusicPlayListInner(whiteMusicInfoBean, true);
        }

        // 播放当前
        public void musicPlay() {
            musicPlayInner();
        }
        // 播放指定音乐
        public void musicPlay(int position) { musicPlayInner(position); };
        // 播放下一曲
        public void musicPlayNext() {
            musicPlayNextInner();
        }
        // 播放上一曲
        public void musicPlayPre() {
            musicPlayPreInner();
        }
        // 播放暂停
        public void musicPlayPause() {
            musicPauseCurrentInner();
        }
        //
        public void musicSeekTo(int pos) {
            musicSeekToInner(pos);
        }
        // 注册监听事件
        public void registerOnMusicStataChangeListener(OnMusicStatusChangeListener onMusicStatusChangeListener) {
            setOnMusicStatusChangeInner(onMusicStatusChangeListener);
        }
        // 释放监听事件
        public void unregisterOnMusicStataChangeListener(OnMusicStatusChangeListener onMusicStatusChangeListener) {
            removeOnMusicStatusChangeInner(onMusicStatusChangeListener);
        }
        // 获取当前播放的音乐
        public WhiteMusicInfoBean getCurrentMusic() {
            return getCurrentMusicInfoBean();
        }
        // 判断当前播放器是否正在运行
        public boolean isMusicPlaying() {
            return getMediaPlayerPlaying();
        }
        // 获取播放列表
        public List<WhiteMusicInfoBean> getMusicPlayList() {
            return  lsWhiteMusicInfoBean;
        }

        public int getCurrentMusicPosition() {
            return getCurrentMusicPositionInner();
        }

        public int getCurrentMusicDuration() {
            return getCurrentMusicDurationInner();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        mediaPlayer = new MediaPlayer();
        contentRrovider = getContentResolver();
        lsWhiteMusicInfoBean = new ArrayList<WhiteMusicInfoBean>();
        musicPaused = false;

        mediaPlayer.setOnCompletionListener(onCompletionListener);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WhiteMusicConstant.ACTION_PLAY_MUSIC_UPDATE);
        registerReceiver(broadcastReceiver, intentFilter);

        // List 一栏初始化
        initMusicPlayList();

        if (null != currentMusicInfoBean) {
            musicPreparePlay(currentMusicInfoBean);
        }
        // TODO
        updateAppWidget(currentMusicInfoBean);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (null != intent) {
            String action = intent.getAction();
            if (null != action) {
                if (WhiteMusicConstant.ACTION_PLAY_MUSIC_PRE.equals(action)) {
                    musicPlayPreInner();
                } else if (WhiteMusicConstant.ACTION_PLAY_MUSIC_NEXT.equals(action)) {
                    musicPlayNextInner();
                } else if (WhiteMusicConstant.ACTION_PLAY_MUSIC_TOGGLE.equals(action)) {
                    if (getMediaPlayerPlaying()) {
                        musicPauseCurrentInner();
                    } else {
                        musicPlayInner();
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();

        // 监听清空
        unregisterReceiver(broadcastReceiver);
        handler.removeMessages(MSG_PROGRESS_UPDATE);
        lsMusicStatusChangeListener.clear();

        for (WhiteMusicInfoBean whiteMusicInfoBean : lsWhiteMusicInfoBean) {
            if (whiteMusicInfoBean.getMusicThumb() != null) {
                whiteMusicInfoBean.getMusicThumb().recycle();
            }
        }

        // 播放列表清空
        lsWhiteMusicInfoBean.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    // 初始化列表List
    private void initMusicPlayList() {
        // 清空音乐一栏
        lsWhiteMusicInfoBean.clear();

        // 取得现存DB中的音乐一栏
        Cursor cursor = contentRrovider.query(
                WhiteMusicListContentProvider.CONTENT_SONGS_URI,
                null,
                null,
                null,
                null
        );

        if (null != cursor) {
            while (cursor.moveToNext()) {
                String musicUri = cursor.getString(cursor.getColumnIndexOrThrow(WhiteMusicDBHelper.MUSIC_LIST_URI));
                String albumUri = cursor.getString(cursor.getColumnIndexOrThrow(WhiteMusicDBHelper.MUSIC_LIST_ALBUM_URI));
                String musicName = cursor.getString(cursor.getColumnIndex(WhiteMusicDBHelper.MUSIC_LIST_NAME));
                String musicArtist = cursor.getString(cursor.getColumnIndex(WhiteMusicDBHelper.MUSIC_LIST_ARTIST));
                long playedTime = cursor.getLong(cursor.getColumnIndexOrThrow(WhiteMusicDBHelper.LAST_PLAY_TIME));
                long musicDuration = cursor.getLong(cursor.getColumnIndexOrThrow(WhiteMusicDBHelper.MUSIC_LIST_DURATION));

                WhiteMusicInfoBean item = new WhiteMusicInfoBean(
                        musicName,
                        musicArtist,
                        Uri.parse(musicUri),
                        Uri.parse(albumUri),
                        null,
                        musicDuration,
                        playedTime/*, isLastPlaying*/);

                lsWhiteMusicInfoBean.add(item);
            }
            cursor.close();
        }

        if (null != lsWhiteMusicInfoBean && !lsWhiteMusicInfoBean.isEmpty()) {
            lsWhiteMusicInfoBean = Utils.sortMusicInfoList(lsWhiteMusicInfoBean);
            currentMusicInfoBean = lsWhiteMusicInfoBean.get(0);
        }
    }

    // 播放器中音乐等待播放
    private void musicPreparePlay(WhiteMusicInfoBean whiteMusicInfoBean) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(WhiteMusicPlayService.this, whiteMusicInfoBean.getMusicUri());
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 添加音乐到音乐一览中
    private void addMusicPlayListInner(List<WhiteMusicInfoBean> lsAddWhiteMusicInfo) {
        // 移除当前数据库所存
        contentRrovider.delete(WhiteMusicListContentProvider.CONTENT_SONGS_URI,
                null,
                null);
        // 清空当前音乐一览
        lsWhiteMusicInfoBean.clear();

        for(WhiteMusicInfoBean whiteMusicInfoBean : lsAddWhiteMusicInfo) {
            addMusicPlayListInner(whiteMusicInfoBean, false);
        }

        currentMusicInfoBean = lsWhiteMusicInfoBean.get(0);
        // 添加完播放第一首歌
//        musicPlayInner();
    }

    // 添加音乐到音乐一览中
    private void addMusicPlayListInner(WhiteMusicInfoBean whiteMusicInfoBean, boolean needPlay) {
        if (lsWhiteMusicInfoBean.contains(whiteMusicInfoBean)) {
            if (needPlay) {
                currentMusicInfoBean = whiteMusicInfoBean;
                musicPlayInner();
            }
            return;
        }
        // 新添加音乐，添加到List最开始位置
        lsWhiteMusicInfoBean.add(whiteMusicInfoBean);

        insertMusicItemInfo(whiteMusicInfoBean);

        if (needPlay) {
            currentMusicInfoBean = whiteMusicInfoBean;
            musicPlayInner();
        }
    }

    private void musicPlayInner() {
        if (null == currentMusicInfoBean && !lsWhiteMusicInfoBean.isEmpty()) {
            currentMusicInfoBean = lsWhiteMusicInfoBean.get(0);
        }
        if (musicPaused) {
            musicPlayCurrentInner(currentMusicInfoBean, false);
        } else {
            musicPlayCurrentInner(currentMusicInfoBean, true);
        }
    }

    private void musicPlayInner(int position) {
        if (null != lsWhiteMusicInfoBean && !lsWhiteMusicInfoBean.isEmpty()) {
            currentMusicInfoBean = lsWhiteMusicInfoBean.get(position);
        }
        if (musicPaused) {
            musicPlayCurrentInner(currentMusicInfoBean, false);
        } else {
            musicPlayCurrentInner(currentMusicInfoBean, true);
        }
    }

    // 播放下一首歌曲
    // TODO 循环播放, 随机播放，单曲循环
    private void musicPlayNextInner() {
        // 当前播放音乐坐标位置
        int currentIndex = lsWhiteMusicInfoBean.indexOf(currentMusicInfoBean);
        if (currentIndex < lsWhiteMusicInfoBean.size() - 1) {
            currentMusicInfoBean = lsWhiteMusicInfoBean.get(currentIndex + 1);
        } else {
            currentMusicInfoBean = lsWhiteMusicInfoBean.get(0);
        }
        musicPlayCurrentInner(currentMusicInfoBean, true);
    }

    // 播放上一首歌曲
    // TODO 循环播放, 随机播放，单曲循环
    private void musicPlayPreInner() {
        // 当前播放音乐坐标位置
        int currentIndex = lsWhiteMusicInfoBean.indexOf(currentMusicInfoBean);
        if (currentIndex - 1 <= 0) {
            currentMusicInfoBean = lsWhiteMusicInfoBean.get(currentIndex - 1);
        } else {
            currentMusicInfoBean = lsWhiteMusicInfoBean.get(lsWhiteMusicInfoBean.size() - 1);
        }
        musicPlayCurrentInner(currentMusicInfoBean, true);
    }

    // 播放当前音乐
    private void musicPlayCurrentInner(WhiteMusicInfoBean whiteMusicInfoBean, boolean needPlay) {
        if (null == whiteMusicInfoBean) {
            return;
        }
        if (needPlay) {
            musicPreparePlay(whiteMusicInfoBean);
        }
        // 播放器启动
        mediaPlayer.start();

        musicSeekToInner((int)whiteMusicInfoBean.getMusicPlayTime());
        for (OnMusicStatusChangeListener onMusicStatusChangeListener : lsMusicStatusChangeListener) {
            onMusicStatusChangeListener.onMusicPlay(whiteMusicInfoBean);
        }
        musicPaused = false;

        handler.removeMessages(MSG_PROGRESS_UPDATE);
        handler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
        // TODO
        updateAppWidget(currentMusicInfoBean);
    }
    //
    private void musicPauseCurrentInner() {
        musicPaused = true;
        mediaPlayer.pause();
        for (OnMusicStatusChangeListener onMusicStatusChangeListener : lsMusicStatusChangeListener) {
            onMusicStatusChangeListener.onMusicPause(currentMusicInfoBean);
        }
        handler.removeMessages(MSG_PROGRESS_UPDATE);
        // TODO
        updateAppWidget(currentMusicInfoBean);
    }

    // 获取当前播放音乐
    private WhiteMusicInfoBean getCurrentMusicInfoBean() {
        return currentMusicInfoBean;
    }

    // 获取当前播放器状态
    private boolean getMediaPlayerPlaying() {
        return mediaPlayer.isPlaying();
    }

    // TODO
    private void musicSeekToInner(int mesc) {
        mediaPlayer.seekTo(mesc);
    }

    // 添加Music状态监听
    private void setOnMusicStatusChangeInner(OnMusicStatusChangeListener onMusicStatusChangeListener) {
        lsMusicStatusChangeListener.add(onMusicStatusChangeListener);
    }

    // 移除Music状态监听
    private void removeOnMusicStatusChangeInner(OnMusicStatusChangeListener onMusicStatusChangeListener) {
        lsMusicStatusChangeListener.remove(onMusicStatusChangeListener);
    }

    private int getCurrentMusicPositionInner() {
        return mediaPlayer.getCurrentPosition();
    }

    private int getCurrentMusicDurationInner() {
        return mediaPlayer.getDuration();
    }

    //
    private void updateMusicItemInfo(WhiteMusicInfoBean whiteMusicInfoBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WhiteMusicDBHelper.MUSIC_LIST_DURATION, whiteMusicInfoBean.getMusicDuration());
        contentValues.put(WhiteMusicDBHelper.LAST_PLAY_TIME, whiteMusicInfoBean.getMusicPlayTime());

        String musicUri = whiteMusicInfoBean.getMusicUri().toString();
        contentRrovider.update(WhiteMusicListContentProvider.CONTENT_SONGS_URI,
                contentValues,
                WhiteMusicDBHelper.MUSIC_LIST_URI + "=\"" + musicUri + "\"",
                null);
    }

    private void insertMusicItemInfo(WhiteMusicInfoBean whiteMusicInfoBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WhiteMusicDBHelper.MUSIC_LIST_NAME, whiteMusicInfoBean.getMusicName());
        contentValues.put(WhiteMusicDBHelper.MUSIC_LIST_DURATION, whiteMusicInfoBean.getMusicDuration());
        contentValues.put(WhiteMusicDBHelper.LAST_PLAY_TIME, whiteMusicInfoBean.getMusicPlayTime());
        contentValues.put(WhiteMusicDBHelper.MUSIC_LIST_URI, whiteMusicInfoBean.getMusicUri().toString());
        contentValues.put(WhiteMusicDBHelper.MUSIC_LIST_ALBUM_URI, whiteMusicInfoBean.getMusicAlbumUri().toString());
        Uri uri = contentRrovider.insert(WhiteMusicListContentProvider.CONTENT_SONGS_URI, contentValues);
    }

    private void updateAppWidget(WhiteMusicInfoBean whiteMusicInfoBean) {
        if (whiteMusicInfoBean != null) {
            if(whiteMusicInfoBean.getMusicThumb() == null) {
                ContentResolver res = getContentResolver();
                whiteMusicInfoBean.setMusicThumb(Utils.createThumbFromUir(res, whiteMusicInfoBean.getMusicAlbumUri()));
            }
//            WhiteMusicAppWidget.performUpdates(WhiteMusicPlayService.this,
//                    whiteMusicInfoBean.getMusicName(),
//                    getMediaPlayerPlaying(),
//                    whiteMusicInfoBean.getMusicThumb());
        }
    }
}
