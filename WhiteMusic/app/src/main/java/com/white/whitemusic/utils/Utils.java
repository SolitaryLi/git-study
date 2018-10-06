package com.white.whitemusic.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.white.whitemusic.bean.WhiteMusicInfoBean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

    // 音乐List排序
    public static List<WhiteMusicInfoBean> sortMusicInfoList(List<WhiteMusicInfoBean> pLsWhiteMusicInfoBean) {
        Collections.sort(pLsWhiteMusicInfoBean, comparator);
        return pLsWhiteMusicInfoBean;
    }

    static Comparator<WhiteMusicInfoBean> comparator = new Comparator<WhiteMusicInfoBean>() {
        char first_l, first_r;
        @Override
        public int compare(WhiteMusicInfoBean lhs, WhiteMusicInfoBean rhs) {
            first_l = lhs.getMusicName().charAt(0);
            first_r = rhs.getMusicName().charAt(0);
            if (StringHelper.checkType(first_l) == StringHelper.CharType.CHINESE) {
                first_l = StringHelper.getPinyinFirstLetter(first_l);
            }
            if (StringHelper.checkType(first_r) == StringHelper.CharType.CHINESE) {
                first_r = StringHelper.getPinyinFirstLetter(first_r);
            }
            if (first_l > first_r) {
                return 1;
            } else if (first_l < first_r) {
                return -1;
            } else {
                return 0;
            }
        }
    };
}
