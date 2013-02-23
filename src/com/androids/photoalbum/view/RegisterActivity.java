package com.androids.photoalbum.view;

import android.os.Bundle;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.BaseActivity;

public class RegisterActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.regist_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "返回", null, null);
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		finish();
	}
}