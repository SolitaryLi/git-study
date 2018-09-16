package com.white.whitemusic.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.white.whitemusic.R;
import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.utils.Utils;

import java.util.List;

public class WhiteMusicListAdapter extends BaseAdapter {

    private Context context;
    private List<WhiteMusicInfoBean> lsWhiteMusicInfoBean;
    private final LayoutInflater layoutInflater;
    private final int mResource;

    public WhiteMusicListAdapter(Context context, int resId, List<WhiteMusicInfoBean> lsWhiteMusicInfoBean) {
        this.context = context;
        this.lsWhiteMusicInfoBean = lsWhiteMusicInfoBean;
        layoutInflater = LayoutInflater.from(context);
        mResource = resId;
    }

    @Override
    public int getCount() {
        return lsWhiteMusicInfoBean != null ? lsWhiteMusicInfoBean.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return lsWhiteMusicInfoBean != null ? lsWhiteMusicInfoBean.get(position): null ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(mResource, parent, false);
        }
        WhiteMusicInfoBean whiteMusicInfoBean = lsWhiteMusicInfoBean.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.music_title);
        title.setText(whiteMusicInfoBean.getMusicName());
        // TODO
        TextView createTime = (TextView) convertView.findViewById(R.id.music_duration);

        // 调用辅助函数转换时间格式
        String times = Utils.convertMSecendToTime(whiteMusicInfoBean.getMusicDuration());
        times = String.format(context.getString(R.string.duration), times);
        createTime.setText(times);

        ImageView thumb = (ImageView) convertView.findViewById(R.id.music_thumb);
        if(thumb != null) {
            if (whiteMusicInfoBean.getMusicThumb() != null) {
                thumb.setImageBitmap(whiteMusicInfoBean.getMusicThumb());
            } else {
                thumb.setImageResource(R.mipmap.default_cover);
            }
        }
        return convertView;

    }

}
