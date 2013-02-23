package com.androids.photoalbum.tab.ui;

import com.android.photoalbum.R;
import com.androids.photoalbum.parser.CollectionsWebsiteParser;
import com.androids.photoalbum.parser.WordsAddWebsiteParser;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.ListViewWebsiteParserTask;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.view.LoginActivity;
import com.androids.photoalbum.view.MainTabActivity;
import com.androids.photoalbum.view.SignNameActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FavoriteActivity extends BaseActivity implements
		OnItemClickListener {
	public static boolean notLoaded = true;
	public static FavoriteActivity mThis;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		mThis = this;
		setView(R.layout.favorite_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_GONE, null,
				null, null);
		// mFavoriteText = (TextView)findViewById(R.id.my_favorite);
		mProductList = (LinearLayout) findViewById(R.id.product_list);

		mImageWidth = (int) mResources
				.getDimension(R.dimen.favorite_image_with);
		mImageHeigth = (int) mResources
				.getDimension(R.dimen.favorite_image_height);
		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setOnItemClickListener(this);
		mLineCount = 3;
		reloadFavoriteList();
	}

	private void login() {
		if (StaticInfo.mPassword == null || StaticInfo.mPassword.length() == 0) {
			Intent intent = new Intent(this, LoginActivity.class);
			MainTabActivity.getCurrentActivityGroup().setNextActivity(intent,
					"LoginActivity");
			// startActivityForResult(intent, LoginActivity.REQUEST_LOGIN_CODE);
			return;
		} else if (notLoaded) {
			reloadFavoriteList();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
	}

	public void reloadFavoriteList() {
		String param = NetWorkUtils.COLLECTION + "?userid=" + StaticInfo.userid
				+ "&pageindex=" + 0 + "&pagecount=" + 30;
		new ListViewWebsiteParserTask(this, new CollectionsWebsiteParser(),
				mListView, mLineCount, mImageWidth, mImageHeigth, true)
				.execute(param);
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (notLoaded) {
			reloadFavoriteList();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			reloadFavoriteList();
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}
}