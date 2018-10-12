package com.white.whitemusic.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.white.whitemusic.Adapter.WhiteMusicMainAdapter;
import com.white.whitemusic.MainApplication;
import com.white.whitemusic.R;
import com.white.whitemusic.manager.WhiteMusicPlayManager;
import com.white.whitemusic.manager.WhiteMusicServiceManager;
import com.white.whitemusic.manager.WhiteMusicUIManager;

/**
 * 主页显示内容
 * 时间：2018年10月5日16:48:10
 * 创建者：white
 */
public class WhiteMusicMainActivity extends Fragment implements View.OnTouchListener, WhiteMusicUIManager.OnRefreshListener {

    // GridView 控件用于把一系列的空间组织成一个二维的网格显示出来
    private GridView mGridView;
    private WhiteMusicMainAdapter mWhiteMusicMainAdapter;
    private WhiteMusicUIManager mWhiteMusicUIManager;
    // 主播放控制页面
    private WhiteMusicPlayManager mWhiteMusicPlayManager;
    // 相对布局控件,它包含的子控件将以控件之间的相对位置或者子类控件相对父类容器的位置方式排序
    private RelativeLayout mWhiteMusicMain, mMainBottomLayout;
    // 数据交互类
    private WhiteMusicServiceManager mWhiteMusicServiceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取数据交互类
        mWhiteMusicServiceManager = MainApplication.mWhiteMusicServiceManager;
    }

    // 创建主页视图
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 获取主页视图
        View view = inflater.inflate(R.layout.activity_white_music_main, container, false);
        // 获取主页中的Grid View
        mGridView = (GridView)view.findViewById(R.id.gridview);
        // 获取主页中的Relative Layout
        mWhiteMusicMain = (RelativeLayout)view.findViewById(R.id.white_music_main);
        // 添加监听事件
        mWhiteMusicMain.setOnTouchListener(this);
        // 获取主页中的Main Bottom Layout模块
        mMainBottomLayout = (RelativeLayout)view.findViewById(R.id.main_bottom_layout);
        // 绑定Service
        MainApplication.mWhiteMusicServiceManager.connectService();
        // 绑定UI Manager
        mWhiteMusicUIManager = new WhiteMusicUIManager(getActivity(), view);
        // 刷新主页内容
        mWhiteMusicUIManager.setOnRefreshListener(this);

        mWhiteMusicPlayManager = new WhiteMusicPlayManager(getActivity(), mWhiteMusicServiceManager,view);
        // TODO 绑定Main Adapter(适配器)
        mWhiteMusicMainAdapter = new WhiteMusicMainAdapter(this, mWhiteMusicUIManager);
        // Grid View绑定适配器内容
        mGridView.setAdapter(mWhiteMusicMainAdapter);
        // 返回视图
        return view;
    }

    // MotionEvent.ACTION_DOWN是指触摸起始
    // MotionEvent.ACTION_MOVE是指触摸滑动
    // MotionEvent.ACTION_UP是指触摸拿起
    int oldY = 0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int bottomTop = mMainBottomLayout.getTop();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            oldY = (int) event.getY();
            if (oldY > bottomTop) {
                // 打开播放主页面
                mWhiteMusicPlayManager.openPlayView();
            }
        }
        return true;
    }

    @Override
    public void onRefresh() {
//        refreshNum();
    }

    public void refreshNum() {
//        int musicCount = mMusicDao.getDataCount();
//        int artistCount = mArtistDao.getDataCount();
//        int albumCount = mAlbumDao.getDataCount();
//        int folderCount = mFolderDao.getDataCount();
//        int favoriteCount = mFavoriteDao.getDataCount();
//
//        mAdapter.setNum(musicCount, artistCount, albumCount, folderCount, favoriteCount);
    }
}
