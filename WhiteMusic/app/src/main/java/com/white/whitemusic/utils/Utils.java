package com.white.whitemusic.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// 工具类
public class Utils {

    // 创建图片
    public static Bitmap createThumbFromUir(ContentResolver contentResolver, Uri uri) {
        InputStream inputStream = null;
        Bitmap bitmap = null;

        try {
            inputStream = contentResolver.openInputStream(uri);
            BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeStream(inputStream, null, sBitmapOptions);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    // 时间转换
    public static String convertMSecendToTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Date date = new Date(time);
        String times = simpleDateFormat.format(date);
        return times;
    }
}
