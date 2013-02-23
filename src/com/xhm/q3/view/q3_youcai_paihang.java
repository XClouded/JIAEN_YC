package com.xhm.q3.view;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.photoalbum.R;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;

public class q3_youcai_paihang extends Activity implements OnClickListener {
	private ImageView mBack;
	private Button mDianjia, mSouchang, mBofang;
	private ArrayList<q3_Video_Info> mQ3_Video_Infos;
	private ListView mListView;
	private TuiJianAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_paihang);
		initVar();
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mDianjia = (Button) findViewById(R.id.q3_youcai_paihang_dianjipaihang);
		mDianjia.setOnClickListener(this);
		mSouchang = (Button) findViewById(R.id.q3_youcai_paihang_souchangpaihang);
		mSouchang.setOnClickListener(this);
		mBofang = (Button) findViewById(R.id.q3_youcai_paihang_bofangpaihang);
		mBofang.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.q3_youcai_paihang_list);
		adapter = new TuiJianAdapter(this, mQ3_Video_Infos);
		mListView.setAdapter(adapter);
	}

	private void initVar() {
		mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo(null, "15", null,
				"playnum%20desc", null, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		case R.id.q3_youcai_paihang_dianjipaihang:
			mDianjia.setBackgroundResource(R.drawable.q3_youcai_paihang_bofangpaihang_focus);
			mSouchang
					.setBackgroundResource(R.drawable.q3_youcai_paihang_souchangpaihang);
			mBofang.setBackgroundResource(R.drawable.q3_youcai_paihang_dianjipaihang);
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo(null, "15", null,
					"playnum%20desc", null, null);
			break;
		case R.id.q3_youcai_paihang_souchangpaihang:
			mDianjia.setBackgroundResource(R.drawable.q3_youcai_paihang_bofangpaihang);
			mSouchang
					.setBackgroundResource(R.drawable.q3_youcai_paihang_souchangpaihang_focus);
			mBofang.setBackgroundResource(R.drawable.q3_youcai_paihang_dianjipaihang);
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo(null, "15", null,
					"collectiontimes%20desc", null, null);
			break;
		case R.id.q3_youcai_paihang_bofangpaihang:
			mDianjia.setBackgroundResource(R.drawable.q3_youcai_paihang_bofangpaihang);
			mSouchang
					.setBackgroundResource(R.drawable.q3_youcai_paihang_souchangpaihang);
			mBofang.setBackgroundResource(R.drawable.q3_youcai_paihang_dianjipaihang_focus);
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo(null, "15", null,
					"shareViews%20desc", null, null);
			break;

		default:
			break;
		}
		adapter.notifyDataChanged(mQ3_Video_Infos);
		adapter.notifyDataSetChanged();
	}
}
