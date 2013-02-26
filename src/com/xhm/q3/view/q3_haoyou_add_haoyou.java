package com.xhm.q3.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.xhm.q3.view.q3_login_activity.LoginTask;

public class q3_haoyou_add_haoyou extends Activity implements OnClickListener {
	public ImageView mBack;
	public EditText mName, mPhone;
	public Button mFanhuihaoyou, mWandcheng;
	public String mID, mName_str, mPhone_str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_haoyou_add_shoudong);
		mID = getIntent().getStringExtra("id");
		mName_str = getIntent().getStringExtra("name");
		mPhone_str = getIntent().getStringExtra("phone");
		initView();
		if (mName_str != null && mName_str.length() != 0) {
			mName.setText(mName_str);
		}
		if (mPhone_str != null && mPhone_str.length() != 0) {
			mPhone.setText(mPhone_str);
		}
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
			if ((mName.getText().toString() == null || mName.getText()
					.toString().length() == 0)
					&& ((mPhone.getText().toString() == null) || (mPhone
							.getText().toString().length() == 0))) {
				Toast.makeText(this, "请输入用户名和号码！", Toast.LENGTH_LONG).show();

			} else if (mName.getText().toString() == null
					|| mName.getText().toString().length() == 0) {
				Toast.makeText(this, "请输入用户名！", Toast.LENGTH_LONG).show();

			} else if (mPhone.getText().toString() == null
					|| mPhone.getText().toString().length() == 0) {
				Toast.makeText(this, "请输入号码！", Toast.LENGTH_LONG).show();

			} else if (mPhone.getText().toString().length() == 11
					&& mPhone.getText().toString().substring(0, 1).equals("1")) {
				intent = new Intent(this, q3_youcai_haoyou_add.class);
				intent.putExtra("name", mName.getText().toString());
				intent.putExtra("phone", mPhone.getText().toString());
				setResult(10, intent);
				finish();
			} else {
				Toast.makeText(this, "请输入的号码有误，请重新输入！", Toast.LENGTH_LONG)
						.show();
				break;
			}
		default:
			break;
		}
	}
}
