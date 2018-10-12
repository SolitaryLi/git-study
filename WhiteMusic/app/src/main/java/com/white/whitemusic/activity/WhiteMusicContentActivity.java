package com.white.whitemusic.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.white.whitemusic.R;
import com.white.whitemusic.service.WhiteMusicPermissionService;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放器主入口函数
 *
 * 1. support v4 FragmentActivity 兼容2.x模式下使用Fragment
 * 2. support v7 AppCompatActivity 兼容2.x模式下使用Fragment和
 * ActionBar, ActionBarActivity是AppCompatActivity过时的产物
 * 如果3.0以上直接继承Activity,便可使用Fragment和ActionBar
 */
@SuppressLint("HandlerLeak")
public class WhiteMusicContentActivity extends FragmentActivity {
    // 主页面
    private WhiteMusicMainActivity mWhiteMusicMainActivity;
    // TODO
    private List<OnBackListener> mlsOnBackListener = new ArrayList<OnBackListener>();

    public interface OnBackListener {
        public abstract void onBack();
    }
    // 在Activity被创建时被系统调用，是Activity生命周期的开始
    // Bundle类型的数据和Map类型的数据相似,都是以Key-Value的形式存储数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置Activity的显示页面
        setContentView(R.layout.activity_white_music_content);
        // TODO 执行欢迎页面
        // 获取存储权限
        WhiteMusicPermissionService.modifyAppPermission(this);
        // 页面替换成主页页面
        mWhiteMusicMainActivity = new WhiteMusicMainActivity();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.white_contentFrame_main, mWhiteMusicMainActivity).commit();

    }
    // 注册返回事件
    public void registerBackListener(OnBackListener onBackListener) {
        if (!mlsOnBackListener.contains(onBackListener)) {
            mlsOnBackListener.add(onBackListener);
        }
    }
    // 解绑返回事件
    public void unRegisterBackListener(OnBackListener listener) {
        mlsOnBackListener.remove(listener);
    }
}
