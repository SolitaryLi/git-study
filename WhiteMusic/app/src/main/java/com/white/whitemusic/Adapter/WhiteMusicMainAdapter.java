package com.white.whitemusic.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.white.whitemusic.R;
import com.white.whitemusic.activity.WhiteMusicMainActivity;
import com.white.whitemusic.constant.WhiteMusicConstant;
import com.white.whitemusic.manager.WhiteMusicUIManager;

/**
 * 主页显示内容
 * 时间：2018年10月5日16:48:10
 * 创建者：white
 */
public class WhiteMusicMainAdapter extends BaseAdapter {

    private int[] mDrawable = new int[]{
            R.mipmap.icon_local_music,
            R.mipmap.icon_favorites,
            R.mipmap.icon_folder_plus,
            R.mipmap.icon_artist_plus,
            R.mipmap.icon_album_plus
    };

    private String[] mName = new String[] {
            "本地音乐",
            "我的最爱",
            "文件夹",
            "歌手",
            "专辑"
    };

    private int mMusicNum = 0,
                  mArtistNum = 0,
                  mAlbumNum = 0,
                  mFolderNum = 0,
                  mFavoriteNum = 0;

    private WhiteMusicMainActivity mWhiteMusicMainActivity;
    private WhiteMusicUIManager mWhiteMusicUIManager;

    public WhiteMusicMainAdapter(WhiteMusicMainActivity pWhiteMusicMainActivity, WhiteMusicUIManager pWhiteMusicUIManager) {
        this.mWhiteMusicMainActivity = pWhiteMusicMainActivity;
        this.mWhiteMusicUIManager = pWhiteMusicUIManager;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setNum(int pMusicNum, int pArtistNum, int pAlbumNum,
                       int pFolderNum, int pFavoriteNum) {
        mMusicNum = pMusicNum;
        mArtistNum = pArtistNum;
        mAlbumNum = pAlbumNum;
        mFolderNum = pFolderNum;
        mFavoriteNum = pFavoriteNum;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mWhiteMusicMainActivity.getActivity().getLayoutInflater().inflate(
                    R.layout.adapter_main_gridview_item, null);
            viewHolder.mIv = (ImageView) convertView
                    .findViewById(R.id.gridview_item_iv);
            viewHolder.mNameTv = (TextView) convertView
                    .findViewById(R.id.gridview_item_name);
            viewHolder.mNumTv = (TextView) convertView
                    .findViewById(R.id.gridview_item_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        switch (position) {
            case 0:// 我的音乐
                viewHolder.mNumTv.setText(mMusicNum + "");
                break;
            case 1:// 我的最爱
                viewHolder.mNumTv.setText(mFavoriteNum + "");
                break;
            case 2:// 文件夹
                viewHolder.mNumTv.setText(mFolderNum + "");
                break;
            case 3:// 歌手
                viewHolder.mNumTv.setText(mArtistNum + "");
                break;
            case 4:// 专辑
                viewHolder.mNumTv.setText(mAlbumNum + "");
                break;
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int from = -1;
                switch (position) {
                    case 0:// 我的音乐
                        from = WhiteMusicConstant.START_FROM_LOCAL;
                        break;
                    case 1:// 我的最爱
                        from = WhiteMusicConstant.START_FROM_FAVORITE;
                        break;
                    case 2:// 文件夹
                        from = WhiteMusicConstant.START_FROM_FOLDER;
                        break;
                    case 3:// 歌手
                        from = WhiteMusicConstant.START_FROM_ARTIST;
                        break;
                    case 4:// 专辑
                        from = WhiteMusicConstant.START_FROM_ALBUM;
                        break;
                }
                mWhiteMusicUIManager.setContentType(from);
            }
        });

        viewHolder.mIv.setImageResource(mDrawable[position]);
        viewHolder.mNameTv.setText(mName[position]);

        return convertView;
    }

    private class ViewHolder {
        ImageView mIv;
        TextView mNameTv, mNumTv;
    }
}
