package com.androids.photoalbum.tab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.photoalbum.R;
import com.androids.photoalbum.view.MainTabActivity;
import com.androids.photoalbum.view.PictureCombineActivity;
import com.androids.photoalbum.view.PictureWorkingActivity;
import com.androids.photoalbum.view.SignNameActivity;

public class WorkingActivity extends BaseActivity {
	private Button mPicWork;
	private Button mNameSign;
	private Button mPicCombine;
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		setView(R.layout.working_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_GONE, null, null, null);
		mPicWork = (Button)findViewById(R.id.pic_work);
		mPicWork.setOnClickListener(this);
		mPicWork.setOnTouchListener(this);
		
		mNameSign = (Button)findViewById(R.id.name_sign);
		mNameSign.setOnClickListener(this);
		mNameSign.setOnTouchListener(this);
		
		mPicCombine = (Button)findViewById(R.id.pic_conbime);
		mPicCombine.setOnClickListener(this);
		mPicCombine.setOnTouchListener(this);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.pic_work:
				picWork();
				break;
			case R.id.name_sign:
				nameSign();
				break;
			case R.id.pic_conbime:
				picCombine();
				break;
			default:
				super.onClick(view);
		}
		
	}
	
	private void picCombine() {
		Intent intent = new Intent(this, PictureCombineActivity.class);
		MainTabActivity.getCurrentActivityGroup().setNextActivity(intent, "PictureCombineActivity");
//		startActivity(intent);
	}
	
	private void picWork() {
		Intent intent = new Intent(this, PictureWorkingActivity.class);
		startActivity(intent);
//		MainTabActivity.getCurrentActivityGroup().setNextActivity(intent, "PictureWorkingActivity");
		
//		Intent intent = new Intent(this, PictureWorkingActivity.class);
//    	WorkingActivityGroup.setNextActivity(intent, "PictureWorkingActivity");
	}
	
	private void nameSign() {
		Intent intent  = new Intent(this, SignNameActivity.class);
//		startActivity(intent);
		MainTabActivity.getCurrentActivityGroup().setNextActivity(intent, "SignNameActivity");
		
//		Intent intent = new Intent(this, SignNameActivity.class);
//    	WorkingActivityGroup.setNextActivity(intent, "SignNameActivity");

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		
	}
	
}