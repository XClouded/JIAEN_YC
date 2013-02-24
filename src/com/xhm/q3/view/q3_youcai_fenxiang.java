package com.xhm.q3.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.photoalbum.R;
import com.androids.photoalbum.view.MainTabActivity;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;

public class q3_youcai_fenxiang extends Activity implements OnClickListener {
	private ImageView mBack;
	private ListView mListView;
	private ArrayList<q3_Video_Info> mQ3_Video_Infos;
	private Button mButton_wdfx, mButton_hyfx;
	private String mName;
	private TuiJianAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_fenxiang);
		SharedPreferences editor = getSharedPreferences(
				MainTabActivity.USER_INFO, MODE_WORLD_READABLE);
		mName = editor.getString("username", "-1");
		System.out.println("name=="+mName);
		mQ3_Video_Infos = q3_GetVideo_Info.getShareVideoInfo(
				"http://www.uuunm.com/getshareVideo.jsp", null, null, mName);
		System.out.println("size==" + mQ3_Video_Infos.size());
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mButton_wdfx = (Button) findViewById(R.id.q3_youcai_fenxiang_wdfx);
		mButton_wdfx.setOnClickListener(this);
		mButton_hyfx = (Button) findViewById(R.id.q3_youcai_fenxiang_hyfx);
		mButton_hyfx.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.q3_youcai_fenxiang_list);
		mAdapter = new TuiJianAdapter(this, mQ3_Video_Infos);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		case R.id.q3_youcai_fenxiang_wdfx:
			mButton_wdfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_wdfx_focus);
			mButton_hyfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_hyfx);
			mQ3_Video_Infos = q3_GetVideo_Info
					.getShareVideoInfo(
							"http://www.uuunm.com/getshareVideo.jsp", null,
							null, mName);
			mAdapter.notifyDataChanged(mQ3_Video_Infos);
			break;
		case R.id.q3_youcai_fenxiang_hyfx:
			mButton_wdfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_wdfx);
			mButton_hyfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_hyfx_focus);
			mQ3_Video_Infos = q3_GetVideo_Info.getShareVideoInfo(
					"http://www.uuunm.com/getfriendsshareVideo.jsp", null,
					null, mName);
			mAdapter.notifyDataChanged(mQ3_Video_Infos);
			break;
		default:
			break;
		}
	}
}
