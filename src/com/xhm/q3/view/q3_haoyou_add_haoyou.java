package com.xhm.q3.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.photoalbum.R;

public class q3_haoyou_add_haoyou extends Activity implements OnClickListener {
	private ImageView mBack;
	private EditText mName, mPhone;
	private Button mFanhuihaoyou, mWandcheng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_haoyou_add_shoudong);
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mName = (EditText) findViewById(R.id.q3_haoyou_add_xingming);
		mPhone = (EditText) findViewById(R.id.q3_haoyou_add_dianhua);
		mFanhuihaoyou = (Button) findViewById(R.id.q3_haoyou_fanhuitohaoyou);
		mFanhuihaoyou.setOnClickListener(this);
		mWandcheng = (Button) findViewById(R.id.q3_haoyou_wancheng);
		mWandcheng.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		case R.id.q3_haoyou_fanhuitohaoyou:
			intent = new Intent(this, q3_youcai_haoyou.class);
			startActivity(intent);
			break;
		case R.id.q3_haoyou_wancheng:
			intent = new Intent(this, q3_youcai_haoyou_add.class);
			intent.putExtra("name", mName.getText().toString());
			intent.putExtra("phone", mPhone.getText().toString());
			setResult(100, intent);
			finish();
			break;
		default:
			break;
		}
	}

}
