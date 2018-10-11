package com.white.whitemusic.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.white.whitemusic.R;
import com.white.whitemusic.activity.WhiteMusicScannerActivity;

public class WhiteMusicScannerFragment extends Fragment implements View.OnClickListener{

    private ImageButton mBackBtn;
    private Button mScanBtn;
    private ProgressDialog mProgressDialog;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_scanner, container, false);

        mBackBtn = (ImageButton) view.findViewById(R.id.backBtn);
        mScanBtn = (Button) view.findViewById(R.id.scanBtn);
        mBackBtn.setOnClickListener(this);
        mScanBtn.setOnClickListener(this);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mProgressDialog.dismiss();
                ((WhiteMusicScannerActivity)getActivity()).mViewPager.setCurrentItem(0, true);
            }
        };
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == mScanBtn) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在扫描歌曲，请勿退出软件！");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

        } else if(view == mBackBtn) {
            ((WhiteMusicScannerActivity)getActivity()).mViewPager.setCurrentItem(0, true);
        }
    }
}
