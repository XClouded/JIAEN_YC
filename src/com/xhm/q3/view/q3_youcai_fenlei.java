package com.xhm.q3.view;

import com.android.photoalbum.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

public class q3_youcai_fenlei extends Activity implements OnClickListener,
		OnItemClickListener {
	private GridView mGridView;
	private ImageView mBack;
	private int PIC[] = { R.drawable.q3_youcai_fenlei_zongyi,
			R.drawable.q3_youcai_fenlei_shishiang,
			R.drawable.q3_youcai_fenlei_yinyue,
			R.drawable.q3_youcai_fenlei_tiyu, R.drawable.q3_youcai_fenlei_yule,
			R.drawable.q3_youcai_fenlei_gaoxiao,
			R.drawable.q3_youcai_fenlei_qita };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_fenlei);
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mGridView = (GridView) findViewById(R.id.q3_youcai_fenlei_grid);
		YouCai_Adapter adapter = new YouCai_Adapter(this, PIC, null);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, q3_youcai_fenlei_item.class);
		q3_youcai_activity.pd = new ProgressDialog(this);
		q3_youcai_activity.pd.setMessage("加载中...");
		q3_youcai_activity.pd.show();
		switch (position) {
		case 0:
			intent.setFlags(0);
			break;
		case 1:
			intent.setFlags(1);
			break;
		case 2:
			intent.setFlags(2);
			break;
		case 3:
			intent.setFlags(3);
			break;
		case 4:
			intent.setFlags(4);
			break;
		case 5:
			intent.setFlags(55);

			break;
		case 6:
			intent.setFlags(6);
			break;
		default:
			break;
		}
		startActivity(intent);
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
