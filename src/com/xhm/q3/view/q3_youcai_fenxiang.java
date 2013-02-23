package com.xhm.q3.view;

import java.util.ArrayList;

import com.android.photoalbum.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class q3_youcai_fenxiang extends Activity implements OnClickListener {
	private ImageView mBack;
	private ListView mListView;
	private ArrayList<q3_Video_Info> mQ3_Video_Infos;
	private Button mButton_wdfx, mButton_hyfx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_fenxiang);
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
		TuiJianAdapter adapter = new TuiJianAdapter(this, null);
		mListView.setAdapter(adapter);
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
			break;
		case R.id.q3_youcai_fenxiang_hyfx:
			mButton_wdfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_wdfx);
			mButton_hyfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_hyfx_focus);
			break;
		default:
			break;
		}
	}
}
