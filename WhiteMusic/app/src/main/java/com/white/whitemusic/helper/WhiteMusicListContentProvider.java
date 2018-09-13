package com.white.whitemusic.helper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

// ContentProvider 进程间进行数据交互&共享，即跨进程通信
// 1.ContentProvider 相当于中间者，真正存储&操作数据的数据源还是原来
// 存储数据的方式（数据库，文件，XML或网络）
// 2.数据源：数据库（如SqlLite），文件，XML，网络等
public class WhiteMusicListContentProvider extends ContentProvider {

    // 主题名
    // ContentProvider 的URI前缀（Android规定）
    private static final String SCHEMA = "content://";
    // 授权信息
    // ContentProvider 的唯一标识符
    public static final String AUTHORITY = "com.white.whitemusic.helper.WhiteMusicListContentProvider";
    // 表名
    // ContentProvider 指向数据库中的某个表名
    private static final String PATH_MUSIC = "/white_music_list_table";

    // URI 统一资源标识符
    // URI 作用：唯一标识 ContentProvider & 其中的数据
    // "content://com.white.whitemusic.helper.WhiteMusicListContentProvider/white_music_list_table"
    public static final Uri CONTENT_SONGS_URI = Uri.parse(SCHEMA + AUTHORITY + PATH_MUSIC);

    // 声明成员变量，mDBHelper将帮助我们操作SQLite数据库
    private WhiteMusicDBHelper whiteMusicDBHelper;

    // ContentProvider创建后 或 打开系统后其它进程第一次访问该ContentProvider时 由系统进行调用
    // 注：运行在ContentProvider进程的主线程，故不能做耗时操作
    @Override
    public boolean onCreate() {
        // ContentProvider被创建的时候，获取WhiteMusicDBHelper对象
        whiteMusicDBHelper = new WhiteMusicDBHelper(getContext());
        return true;
    }

    // 得到数据类型，即返回当前 Url 所代表数据的MIME类型
    @Override
    public String getType(Uri uri) {
        return null;
    }

    // 外部进程向 ContentProvider 中添加数据
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri resUri = null;
        // 通过WhiteMusicDBHelper获取写数据库的方法
        SQLiteDatabase sqlLiteDatabase = whiteMusicDBHelper.getWritableDatabase();
        // 将要数据ContentValues插入到数据库中
        long resId = sqlLiteDatabase.insert(WhiteMusicDBHelper.MUSIC_LIST_TABLE_NAME, null, values);

        if(resId > 0) {
            // 根据返回到id值组合成该数据项对应的Uri地址,
            // 假设id为8，那么这个Uri地址类似于content://com.white.whitemusic.helper.WhiteMusicListContentProvider/white_music_list_table
            resUri = ContentUris.withAppendedId(CONTENT_SONGS_URI, resId);
        }
        return resUri;
    }

    // 外部进程 删除 ContentProvider 中的数据
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // 通过DBHelper获取写数据库的方法
        SQLiteDatabase sqlLiteDatabase = whiteMusicDBHelper.getWritableDatabase();
        // 清空white_music_list_table表，并将删除的数据条数返回
        int count = sqlLiteDatabase.delete(WhiteMusicDBHelper.MUSIC_LIST_TABLE_NAME, selection, selectionArgs);
        return count;
    }

    // 外部进程更新 ContentProvider 中的数据
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // 通过DBHelper获取写数据库的方法
        SQLiteDatabase sqlLiteDatabase = whiteMusicDBHelper.getWritableDatabase();
        // 更新数据库的指定项
        int count = sqlLiteDatabase.update(WhiteMusicDBHelper.MUSIC_LIST_TABLE_NAME, values, selection, selectionArgs);
        return count;
    }

    // 外部应用 获取 ContentProvider 中的数据
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // 通过DBHelper获取读数据库的方法
        SQLiteDatabase sqlLiteDatabase = whiteMusicDBHelper.getReadableDatabase();
        //查询数据库中的数据项
        Cursor cursor = sqlLiteDatabase.query(WhiteMusicDBHelper.MUSIC_LIST_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }
}
