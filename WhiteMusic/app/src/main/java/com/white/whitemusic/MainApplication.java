package com.white.whitemusic;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.white.whitemusic.activity.WhiteMusicMainActivity;
import com.white.whitemusic.manager.WhiteMusicServiceManager;
import com.white.whitemusic.service.WhiteMusicPermissionService;

public class MainApplication extends Application {

    public static WhiteMusicServiceManager mWhiteMusicServiceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mWhiteMusicServiceManager = new WhiteMusicServiceManager(this);
    }
}
