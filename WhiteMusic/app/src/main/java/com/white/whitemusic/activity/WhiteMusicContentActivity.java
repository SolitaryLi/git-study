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
 */
@SuppressLint("HandlerLeak")
public class WhiteMusicContentActivity extends FragmentActivity {

    private WhiteMusicMainActivity mWhiteMusicMainActivity;

    private List<OnBackListener> mlsOnBackListener = new ArrayList<OnBackListener>();

    public interface OnBackListener {
        public abstract void onBack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_white_music_content);

        WhiteMusicPermissionService.modifyAppPermission(this);

        // 页面替换成主页页面
        mWhiteMusicMainActivity = new WhiteMusicMainActivity();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.white_contentFrame_main, mWhiteMusicMainActivity).commit();

    }

    public void registerBackListener(OnBackListener onBackListener) {
        if (!mlsOnBackListener.contains(onBackListener)) {
            mlsOnBackListener.add(onBackListener);
        }
    }

    public void unRegisterBackListener(OnBackListener listener) {
        mlsOnBackListener.remove(listener);
    }
}
