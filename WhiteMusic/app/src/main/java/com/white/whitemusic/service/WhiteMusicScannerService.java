package com.white.whitemusic.service;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.white.whitemusic.MainApplication;
import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.manager.WhiteMusicServiceManager;
import com.white.whitemusic.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WhiteMusicScannerService {

    private Context mContext;
    private WhiteMusicServiceManager mWhiteMusicServiceManager;
    private List<WhiteMusicInfoBean> mLsWhiteMusicInfoBean;

    public WhiteMusicScannerService(Context context) {
        this.mContext = context;
        mWhiteMusicServiceManager = MainApplication.mWhiteMusicServiceManager;
        mLsWhiteMusicInfoBean = new ArrayList<WhiteMusicInfoBean>();
    }

    public void scannerLocalMusic() {
        // 媒体库查询
        // 外部存储
        // URI
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] searchKey = new String[] {
                // 对应文件在数据库中的检索ID
                MediaStore.Audio.Media._ID,
                // 对应文件的标题
                MediaStore.Audio.Media.TITLE,
                // 对应文件的艺术家
                MediaStore.Audio.Media.ARTIST,
                // 对应文件所在的专辑ID，在后面获取封面图片时会用到
                MediaStore.Audio.Media.ALBUM_ID,
                // 对应文件的存放位置
                MediaStore.Audio.Media.DATA,
                // 对应文件的播放时长
                MediaStore.Audio.Media.DURATION
        };
        String where = MediaStore.Audio.Media.DATA + " like \"%"+"/music"+"%\"";
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(uri, searchKey, where, null, sortOrder);

        if (null != cursor) {
            while (cursor.moveToNext()) {
                // 获取音乐的路径，这个参数我们实际上不会用到，不过在调试程序的时候可以方便我们看到音乐的真实路径，确定寻找的文件的确就在我们规定的目录当中
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                // 获取音乐的ID
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //通过URI和ID，组合出改音乐特有的Uri地址
                Uri musicUri = Uri.withAppendedPath(uri, id);
                //获取音乐的名称
                String musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                //获取音乐的艺术家
                String musicArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                //获取音乐的时长，单位是毫秒
                long musicDuration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //获取该音乐所在专辑的id
                int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID));
                //再通过AlbumId组合出专辑的Uri地址
                Uri musicAlbumUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);

                WhiteMusicInfoBean whiteMusicInfoBean = new WhiteMusicInfoBean(
                        musicName, musicArtist, musicUri, musicAlbumUri, null, musicDuration, 0);

                if (uri != null) {
                    ContentResolver res = mContext.getContentResolver();
                    whiteMusicInfoBean.setMusicThumb(Utils.createThumbFromUir(res, musicAlbumUri));
                }
                mLsWhiteMusicInfoBean.add(whiteMusicInfoBean);
            }
            // 资源释放
            cursor.close();

            if (null != mLsWhiteMusicInfoBean && !mLsWhiteMusicInfoBean.isEmpty()) {
                mWhiteMusicServiceManager.addMusicPlayList(mLsWhiteMusicInfoBean);
            }
        }
    }
}
