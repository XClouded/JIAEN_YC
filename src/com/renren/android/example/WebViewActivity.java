package com.renren.android.example;

import com.renren.api.connect.android.Renren;

import android.app.Activity;
import android.os.Bundle;

public class WebViewActivity extends Activity {

	private static final String API_KEY = "87fd38d177cd49a9957a8b0f2e9865f6";

	private static final String SECRET_KEY = "6bcf98cacdb9459182e60fa67e410760";

	private static final String APP_ID = "209200";

	private Renren renren;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		renren = new Renren(API_KEY, SECRET_KEY, APP_ID, this);
		PhotoDemo.uploadPhotoWithActivity(this, renren);
		finish();	
}}
