package com.white.whitemusic.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.white.whitemusic.R;
import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.service.MusicService;
import com.white.whitemusic.task.WhiteMusicScannerTask;
import com.white.whitemusic.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WhiteMusicListActivity extends AppCompatActivity {

    private WhiteMusicScannerTask whiteMusicScannerTask;
    public static List<WhiteMusicInfoBean> lsWhiteMusicInfoBean = new ArrayList<WhiteMusicInfoBean>();;
    private ListView listView;
    private MusicService.MusicServiceIBinder mMusicService;

    private Button mPlayBtn;
    private Button mPreBtn;
    private Button mNextBtn;
    private TextView mMusicTitle;
    private TextView mPlayedTime;
    private TextView mDurationTime;
    private SeekBar mMusicSeekBar;
//    private MusicUpdateTask mMusicUpdateTask;


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //绑定成功后，取得MusicSercice提供的接口
            mMusicService = (MusicService.MusicServiceIBinder) service;
            mMusicService.registerOnStateChangeListener(mStateChangeListenr);

            WhiteMusicInfoBean item = mMusicService.getCurrentMusic();
            if(item == null) {
                enableControlPanel(false);
                return;
            }
            else {
                updatePlayingInfo(item);
            }
            if(mMusicService.isPlaying()) {
                mPlayBtn.setBackgroundResource(R.mipmap.ic_pause);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_music_list);

        listView = (ListView) findViewById(R.id.music_list);
        WhiteMusicListAdapter musicScannerAdapter = new WhiteMusicListAdapter(this, R.layout.activity_white_music_listview, lsWhiteMusicInfoBean);
        listView.setAdapter(musicScannerAdapter);

        listView.setOnItemClickListener(mOnMusicItemClickListener);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
//        listView.setMultiChoiceModeListener(mMultiChoiceListener);

        mPlayBtn = (Button) findViewById(R.id.play_btn);
        mPreBtn = (Button) findViewById(R.id.pre_btn);
        mNextBtn = (Button) findViewById(R.id.next_btn);

        mMusicTitle = (TextView) findViewById(R.id.music_title);

        mDurationTime = (TextView) findViewById(R.id.duration_time);
        mPlayedTime = (TextView) findViewById(R.id.played_time);
        mMusicSeekBar = (SeekBar) findViewById(R.id.seek_music);
        mMusicSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);


        whiteMusicScannerTask = new WhiteMusicScannerTask();
        whiteMusicScannerTask.listView = listView;
        whiteMusicScannerTask.context = this;
        whiteMusicScannerTask.execute();

        Intent i = new Intent(this, MusicService.class);
        //启动MusicService
        startService(i);
        //实现绑定操作
        bindService(i, mServiceConnection, BIND_AUTO_CREATE);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            if(mMusicService != null) {
                mMusicService.seekTo(seekBar.getProgress());
            }
        }
    };

    //定义监听器
    private AdapterView.OnItemClickListener mOnMusicItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WhiteMusicInfoBean item = lsWhiteMusicInfoBean.get(position);
            if(mMusicService != null) {
                //通过MusicService提供的接口，把要添加的音乐交给MusicService处理
                mMusicService.addPlayList(lsWhiteMusicInfoBean.get(position));
            }

            //添加播放音乐的代码
//            MusicInformationBean musicInformationBean = lsMusicInformationBean.get(position);
//            Intent intent = new Intent( MusicScannerActivity.this, MusicActionActivity.class);
//            intent.putExtra("musicUri", musicInformationBean.getSongUri().toString());
//            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(whiteMusicScannerTask != null && whiteMusicScannerTask.getStatus() == AsyncTask.Status.RUNNING) {
            whiteMusicScannerTask.cancel(true);
        }
        whiteMusicScannerTask = null;

        //手动回收使用的图片资源
        for(WhiteMusicInfoBean whiteMusicInfoBean : lsWhiteMusicInfoBean) {
            if( whiteMusicInfoBean.getMusicThumb() != null ) {
                whiteMusicInfoBean.getMusicThumb().recycle();
                whiteMusicInfoBean.setMusicThumb(null);
            }
        }
        lsWhiteMusicInfoBean.clear();

        unbindService(mServiceConnection);
    }

//    private ListView.MultiChoiceModeListener mMultiChoiceListener = new AbsListView.MultiChoiceModeListener() {
//
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            getMenuInflater().inflate(R.menu.music_choice_actionbar, menu);
//            enableControlPanel(false);
//            return true;
//        }
//
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return true;
//        }
//
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch(item.getItemId()) {
//                case R.id.menu_play: {
//                    List musicList = new ArrayList<MusicItem>();
//                    SparseBooleanArray checkedResult = mMusicListView.getCheckedItemPositions();
//                    for (int i = 0; i < checkedResult.size(); i++) {
//                        if(checkedResult.valueAt(i)) {
//                            int pos = checkedResult.keyAt(i);
//                            MusicItem music = mMusicList.get(pos);
//                            musicList.add(music);
//                        }
//                    }
//
//                    mMusicService.addPlayList(musicList);
//
//                    mode.finish();
//                }
//                break;
//            }
//            return true;
//        }
//
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            enableControlPanel(true);
//        }
//
//        @Override
//        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//
//        }
//    };

    private void enableControlPanel(boolean enabled) {
        mPlayBtn.setEnabled(enabled);
        mPreBtn.setEnabled(enabled);
        mNextBtn.setEnabled(enabled);
        mMusicSeekBar.setEnabled(enabled);
    }

    private void updatePlayingInfo(WhiteMusicInfoBean item) {
        String times = Utils.convertMSecendToTime(item.getMusicDuration());
        mDurationTime.setText(times);

        times = Utils.convertMSecendToTime(item.getMusicPlayTime());
        mPlayedTime.setText(times);

        mMusicSeekBar.setMax((int) item.getMusicDuration());
        mMusicSeekBar.setProgress((int) item.getMusicPlayTime());

        mMusicTitle.setText(item.getMusicName());
    }

    private MusicService.OnStateChangeListenr mStateChangeListenr = new MusicService.OnStateChangeListenr() {

        @Override
        public void onPlayProgressChange(WhiteMusicInfoBean item) {

            updatePlayingInfo(item);
        }

        @Override
        public void onPlay(WhiteMusicInfoBean item) {
            mPlayBtn.setBackgroundResource(R.mipmap.ic_pause);
            updatePlayingInfo(item);
            enableControlPanel(true);
        }

        @Override
        public void onPause(WhiteMusicInfoBean item) {
            mPlayBtn.setBackgroundResource(R.mipmap.ic_play);
            enableControlPanel(true);
        }
    };


    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.play_btn: {
                if(mMusicService != null) {
                    if(!mMusicService.isPlaying()) {
                        mMusicService.play();
                    }
                    else {
                        mMusicService.pause();
                    }
                }
            }
            break;

            case R.id.next_btn: {
                if(mMusicService != null) {
                    mMusicService.playNext();
                }
            }
            break;

            case R.id.pre_btn: {
                if(mMusicService != null) {
                    mMusicService.playPre();
                }
            }
            break;
        }
    }

    private void showPlayList() {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_playlist);
        builder.setTitle(R.string.play_list);

        List<WhiteMusicInfoBean> playList = mMusicService.getPlayList();
        ArrayList<String> data = new ArrayList<String>();
        for(WhiteMusicInfoBean music : playList) {
            data.add(music.getMusicName());
        }
        if(data.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
            builder.setAdapter(adapter, null);
        }
        else {
            builder.setMessage(getString(R.string.no_song));
        }

        builder.setCancelable(true);
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.play_list_menu: {

                showPlayList();
            }
            break;

        }

        return true;
    }
}
