package com.white.whitemusic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.white.whitemusic.activity.WhiteMusicListActivity;
import com.white.whitemusic.service.WhiteMusicPermissionService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WhiteMusicPermissionService.modifyAppPermission(this);
        // TODO
        Intent intent = new Intent(MainActivity.this, WhiteMusicListActivity.class);
        startActivity(intent);
    }
}
