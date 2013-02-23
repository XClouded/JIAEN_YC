package com.xhm.q3.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.photoalbum.R;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;

public class q3_youcai_fenlei_item extends Activity implements OnClickListener {
	private ImageView mImageView;
	private ArrayList<q3_Video_Info> mQ3_Video_Infos;
	private TuiJianAdapter mAdapter;
	private ListView mListView;
	private RelativeLayout mRelative;
	private Intent index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_fenlei_item);
		initView();
		index = getIntent();
		switch (index.getFlags()) {
		case 0:
			mRelative
					.setBackgroundResource(R.drawable.q3_youcai_fenlei_item_zonghe);
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo("1", "15", null,
					null, null, null);
			break;
		case 1:
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo("2", "15", null,
					null, null, null);
			mRelative
					.setBackgroundResource(R.drawable.q3_youcai_fenlei_item_shishang);
			break;
		case 2:
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo("3", "15", null,
					null, null, null);
			mRelative
					.setBackgroundResource(R.drawable.q3_youcai_fenlei_item_yinyue);
			break;
		case 3:
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo("4", "15", null,
					null, null, null);
			mRelative
					.setBackgroundResource(R.drawable.q3_youcai_fenlei_item_tiyu);
			break;
		case 4:
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo("5", "15", null,
					null, null, null);
			mRelative
					.setBackgroundResource(R.drawable.q3_youcai_fenlei_item_yule);
			break;
		case 5:
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo("6", "15", null,
					null, null, null);
			mRelative
					.setBackgroundResource(R.drawable.q3_youcai_fenlei_item_gaoxiao);
			break;
		case 6:
			mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo("7", "15", null,
					null, null, null);
			mRelative
					.setBackgroundResource(R.drawable.q3_youcai_fenlei_item_qita);
			break;
		default:
			break;
		}
		mAdapter = new TuiJianAdapter(this, mQ3_Video_Infos);
		mListView.setAdapter(mAdapter);
	}

	private void initView() {
		mRelative = (RelativeLayout) findViewById(R.id.q3_youcai_fenlei_item_bg);
		mImageView = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mImageView.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.q3_youcai_fenlei_list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;

		default:
			break;
		}
	}
}
