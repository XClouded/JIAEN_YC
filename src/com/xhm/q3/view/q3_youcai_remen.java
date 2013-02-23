package com.xhm.q3.view;

import java.util.ArrayList;

import com.android.photoalbum.R;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class q3_youcai_remen extends Activity implements OnClickListener {
	private ImageView mBack;
	private ListView mListView;
	private ArrayList<q3_Video_Info> mQ3_Video_Infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_remen);
		initVar();
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.q3_youcai_remen_list);
		TuiJianAdapter adapter = new TuiJianAdapter(this, mQ3_Video_Infos);
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
		default:
			break;
		}
	}
}
