package com.white.whitemusic.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.white.whitemusic.R;
import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.service.MusicService;
import com.white.whitemusic.task.WhiteMusicScannerTask;

import java.util.ArrayList;
import java.util.List;

public class WhiteMusicListActivity extends AppCompatActivity {

    private WhiteMusicScannerTask whiteMusicScannerTask;
    public static List<WhiteMusicInfoBean> lsWhiteMusicInfoBean = new ArrayList<WhiteMusicInfoBean>();;
    private ListView listView;
    private MusicService.MusicServiceIBinder mMusicService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //绑定成功后，取得MusicSercice提供的接口
            mMusicService = (MusicService.MusicServiceIBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_music_list);

        Intent i = new Intent(this, MusicService.class);
        //启动MusicService
        startService(i);
        //实现绑定操作
        bindService(i, mServiceConnection, BIND_AUTO_CREATE);


//        listView = (ListView) findViewById(R.id.music_list);
//        WhiteMusicListAdapter musicScannerAdapter = new WhiteMusicListAdapter(this, R.layout.activity_white_music_listview, lsWhiteMusicInfoBean);
//        listView.setAdapter(musicScannerAdapter);
//
//        whiteMusicScannerTask = new WhiteMusicScannerTask();
//        whiteMusicScannerTask.listView = listView;
//        whiteMusicScannerTask.context = this;
//        whiteMusicScannerTask.execute();
//
//        // 设置监听事件
//        listView.setOnItemClickListener(mOnMusicItemClickListener);
    }

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
            if( whiteMusicInfoBean.getThumb() != null ) {
                whiteMusicInfoBean.getThumb().recycle();
                whiteMusicInfoBean.setThumb(null);
            }
        }
        lsWhiteMusicInfoBean.clear();

        unbindService(mServiceConnection);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.play_list_menu: {
//                //响应用户对菜单的点击，显示播放列表
//                showPlayList();
//            }
//            break;
//
//        }
//
//        return true;
//    }
//
//    private void showPlayList() {
//
//        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        //设置对话框的图标
//        builder.setIcon(R.mipmap.ic_playlist);
//        //设计对话框的显示标题
//        builder.setTitle(R.string.play_list);
//
//        //获取播放列表，把播放列表中歌曲的名字取出组成新的列表
//        List<WhiteMusicInfoBean> playList = mMusicService.getPlayList();
//        ArrayList<String> data = new ArrayList<String>();
//        for(WhiteMusicInfoBean music : playList) {
//            data.add(music.getName());
//        }
//        if(data.size() > 0) {
//            //播放列表有曲目，显示音乐的名称
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
//            builder.setAdapter(adapter, null);
//        }
//        else {
//            //播放列表没有曲目，显示没有音乐
//            builder.setMessage(getString(R.string.no_song));
//        }
//
//        //设置该对话框是可以自动取消的，例如当用户在空白处随便点击一下，对话框就会关闭消失
//        builder.setCancelable(true);
//
//        //创建并显示对话框
//        builder.create().show();
//    }
}
