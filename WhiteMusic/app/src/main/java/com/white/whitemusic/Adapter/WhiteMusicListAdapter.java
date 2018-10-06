package com.white.whitemusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.white.whitemusic.MainApplication;
import com.white.whitemusic.R;
import com.white.whitemusic.bean.WhiteMusicInfoBean;
import com.white.whitemusic.manager.WhiteMusicServiceManager;
import com.white.whitemusic.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WhiteMusicListAdapter extends BaseAdapter {

    private Context mContext;
    private List<WhiteMusicInfoBean> mLsWhiteMusicInfoBean;
    // 具体作用：
    // 1、对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater.inflate()来载入；
    // 2、对于一个已经载入的界面，就可以使用Activity.findViewById()方法来获得其中的界面元素。
    private final LayoutInflater mLayoutInflater;
    private WhiteMusicServiceManager mWhiteMusicServiceManager;

    class ViewHolder {
        TextView musicNameTv, artistTv, durationTv;
        ImageView playStateIconIv, favoriteIv;
    }

    public WhiteMusicListAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        // 实例化
        mLsWhiteMusicInfoBean = new ArrayList<WhiteMusicInfoBean>();
        mWhiteMusicServiceManager = MainApplication.mWhiteMusicServiceManager;
    }

    // 注：如getCount为0时，GetView执行失败
    @Override
    public int getCount() {
        return mLsWhiteMusicInfoBean != null ? mLsWhiteMusicInfoBean.size() : 0;
    }

    // 根据坐标ID获取对应对象
    @Override
    public Object getItem(int position) {
        return mLsWhiteMusicInfoBean != null ? mLsWhiteMusicInfoBean.get(position): null ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 重构画面时调用
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final WhiteMusicInfoBean whiteMusicInfoBean = (WhiteMusicInfoBean)getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater
                    .inflate(R.layout.adapter_musiclist_item, null);
            // 音乐名字
            viewHolder.musicNameTv = (TextView) convertView
                    .findViewById(R.id.musicname_tv);
            // 音乐艺术家
            viewHolder.artistTv = (TextView) convertView
                    .findViewById(R.id.artist_tv);
            // 音乐时长
            viewHolder.durationTv = (TextView) convertView
                    .findViewById(R.id.duration_tv);
            // 音乐播放状态
            viewHolder.playStateIconIv = (ImageView) convertView
                    .findViewById(R.id.playstate_iv);
            // 音乐是否最爱
            viewHolder.favoriteIv = (ImageView) convertView
                    .findViewById(R.id.favorite_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if (position != mCurPlayMusicIndex) {
//            viewHolder.playStateIconIv.setVisibility(View.GONE);
//        } else {
//            viewHolder.playStateIconIv.setVisibility(View.VISIBLE);
//            if (mPlayState == MPS_PAUSE) {
//                viewHolder.playStateIconIv
//                        .setBackgroundResource(R.drawable.list_pause_state);
//            } else {
//                viewHolder.playStateIconIv
//                        .setBackgroundResource(R.drawable.list_play_state);
//            }
//        }

//        viewHolder.favoriteIv.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if(music.favorite == 1) {
//                    if(mFrom == START_FROM_FAVORITE) {
//                        mMusicList.remove(position);
//                        notifyDataSetChanged();
//                    }
////					music.favorite = 0;
//                    mFavoriteDao.deleteById(music._id);
//                    mMusicDao.setFavoriteStateById(music._id, 0);
//                    viewHolder.favoriteIv.setImageResource(R.drawable.icon_favourite_normal);
//                    mMusicList.get(position).favorite = 0;
//                    mSdm.refreshFavorite(0);
//                } else {
////					music.favorite = 1;
//                    mFavoriteDao.saveMusicInfo(music);
//                    mMusicDao.setFavoriteStateById(music._id, 1);
//                    viewHolder.favoriteIv.setImageResource(R.drawable.icon_favourite_checked);
//                    mMusicList.get(position).favorite = 1;
//                    mSdm.refreshFavorite(1);
//                }
//            }
//        });

//        if(music.favorite == 1) {
//            viewHolder.favoriteIv.setImageResource(R.drawable.icon_favourite_checked);
//        } else {
//            viewHolder.favoriteIv.setImageResource(R.drawable.icon_favourite_normal);
//        }

        viewHolder.musicNameTv.setText((position + 1) + "." + whiteMusicInfoBean.getMusicName());
        viewHolder.artistTv.setText(whiteMusicInfoBean.getArtistName());
        viewHolder.durationTv.setText(Utils.convertMSecendToTime(whiteMusicInfoBean.getMusicDuration()));
        return convertView;

    }

    public void setData(List<WhiteMusicInfoBean> pLsWhiteMusicInfoBean, boolean pDbFlag) {
//        mLsWhiteMusicInfoBean.clear();
        if (pLsWhiteMusicInfoBean != null && !pLsWhiteMusicInfoBean.isEmpty()) {
            for(WhiteMusicInfoBean pWhiteMusicInfoBean : pLsWhiteMusicInfoBean) {
                int orExist = 0;
                for(WhiteMusicInfoBean mWhiteMusicInfoBean : mLsWhiteMusicInfoBean) {
                    if (mWhiteMusicInfoBean.equals(pWhiteMusicInfoBean)) {
                        orExist = 1;
                        break;
                    }
                }
                if (orExist == 0) {
                    mLsWhiteMusicInfoBean.add(pWhiteMusicInfoBean);
                }
            }
//            mLsWhiteMusicInfoBean.addAll(pLsWhiteMusicInfoBean);

            // 为list排序
            mLsWhiteMusicInfoBean = Utils.sortMusicInfoList(mLsWhiteMusicInfoBean);
            // 保存到数据库中
            if (pDbFlag) {
                mWhiteMusicServiceManager.addMusicPlayList(mLsWhiteMusicInfoBean);
            }
            // 数据有变化的场合，调用GetView方法，刷新UI
            notifyDataSetChanged();
        }
    }

    public List<WhiteMusicInfoBean> getData() {
        return mLsWhiteMusicInfoBean;
    }


}
