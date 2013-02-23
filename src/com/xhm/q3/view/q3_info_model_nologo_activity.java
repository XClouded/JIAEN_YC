package com.xhm.q3.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

import com.android.photoalbum.R;

public class q3_info_model_nologo_activity extends Activity implements
		OnTouchListener {
	private ImageView mButton_finsh;
	private ImageView mImageView_fanhui;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_info_modle_nologo_layout);
		initView();
	}

	private void initView() {
		mButton_finsh = (ImageView) findViewById(R.id.q3_info_model_finsh);
		mImageView_fanhui = (ImageView) findViewById(R.id.q3_fanhui);
		mButton_finsh.setOnTouchListener(this);
		mImageView_fanhui.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("info_down");
			mButton_finsh.setAlpha(100);
			break;
		case MotionEvent.ACTION_UP:
			mButton_finsh.setAlpha(255);
			switch (v.getId()) {
			case R.id.q3_fanhui:
				finish();
				break;
			case R.id.q3_info_model_finsh:
				System.out.println("nologo");
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}

		return true;
	}
}
