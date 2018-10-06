package com.white.whitemusic.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQLite是Android内置的小型，关系型，属于文本型的数据库
public class WhiteMusicDBHelper extends SQLiteOpenHelper {

    // Music列表存储数据库名
    private final static String DB_NAME = "whiteMusicBase.db";
    // Music列表存储数据库版本
    private final static int DB_VERSION = 1;
    // Music列表存储数据库表名
    public final static String MUSIC_LIST_TABLE_NAME = "white_music_list_table";
    // 列表中主键ID（自增）
    public final static String MUSIC_LIST_ID = "music_id";
    // 列表中音乐的名字
    public final static String MUSIC_LIST_NAME = "music_name";
    // 列表中音乐的艺术家
    public final static String MUSIC_LIST_ARTIST = "music_artist";
    // 音乐上次播放时间
    public final static String LAST_PLAY_TIME = "last_play_time";
    // 音乐实际物理地址
    public final static String MUSIC_LIST_URI = "music_uri";
    // 音乐封面物理地址
    public final static String MUSIC_LIST_ALBUM_URI = "music_album_uri";
    // 音乐播放时长
    public final static String MUSIC_LIST_DURATION = "music_duration";

    public WhiteMusicDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 创建数据库
    // 第一次创建数据库的时候回调该方法
    // 当调用getReadableDatabase方法的时候，如果数据库不存在，就会调用该方法进行数据库创建
    // 作用：创建数据库表：将创建数据库表的execSql方法和初始化数据的insert方法写在该方法内
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建播放列表的存储表项
        String PLAYLIST_TABLE_CMD = "CREATE TABLE " + MUSIC_LIST_TABLE_NAME
                + "("
                + MUSIC_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MUSIC_LIST_NAME +" VARCHAR(256),"
                + MUSIC_LIST_ARTIST +" VARCHAR(256),"
                + LAST_PLAY_TIME +" LONG,"
                + MUSIC_LIST_URI +" VARCHAR(128),"
                + MUSIC_LIST_ALBUM_URI +" VARCHAR(128),"
                + MUSIC_LIST_DURATION + " LONG"
                + ");" ;
        db.execSQL(PLAYLIST_TABLE_CMD);
    }

    // 修改数据库，更改数据表结构
    // 调用时机：数据库版本发生变化的时候回调（取决于数据库版本）
    // 创建SQLiteOpenHelper子类对象的时候，必须传入一个version参数
    // 只要新版本高于原版本，就会出发该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果遇到数据库更新，我们简单的处理为删除以前的表，重新创建一张
        db.execSQL("DROP TABLE IF EXISTS "+ MUSIC_LIST_TABLE_NAME);
        onCreate(db);
    }
}
