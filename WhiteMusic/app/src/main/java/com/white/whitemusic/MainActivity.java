package com.white.whitemusic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.white.whitemusic.activity.WhiteMusicListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO
        Intent intent = new Intent(MainActivity.this, WhiteMusicListActivity.class);
        startActivity(intent);
    }
}
