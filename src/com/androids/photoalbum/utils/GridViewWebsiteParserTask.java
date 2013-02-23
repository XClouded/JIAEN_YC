package com.androids.photoalbum.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.tab.ui.TabActivityGroup;
import com.androids.photoalbum.view.MainTabActivity;

public class GridViewWebsiteParserTask extends
		AsyncTask<String, Void, WebsiteInfo> {
	public WebsiteParser mWebsiteParser = null;

	private GridView mGallery;
	private Context mContext;
	private boolean showed;

	// private static ProgressDialog pd;

	public GridViewWebsiteParserTask(Context context, WebsiteParser parser,
			GridView gallery) {
		mWebsiteParser = parser;
		mGallery = gallery;
		mContext = context;
		// pd = new ProgressDialog(context);
	}

	@Override
	protected WebsiteInfo doInBackground(String... params) {
		// pd = new ProgressDialog(MainTabActivity.getCurrentActivityGroup());
		TabActivityGroup currentGroup = MainTabActivity
				.getCurrentActivityGroup();
		if (currentGroup != null) {
			showed = currentGroup.showProgessBarDialog("加载中...");
		}
		// pd = Utils.showProgessBarDialog("Loading...", mContext);
		WebsiteInfo websiteInfo = mWebsiteParser.parseWebsiteInfo(params[0]);
		return websiteInfo;
	}

	@Override
	protected void onPostExecute(WebsiteInfo result) {
		// Q_3
		// MainTabActivity.getCurrentActivityGroup().dismissProgressBarDialog();

		postExecute(result);
	}

	public void postExecute(WebsiteInfo result) {
		Utils.notifyToUser(result.resultdesc, mContext);
		if (ServerInfo.FAIL.equals(result.result)) {
			return;
		}
		// PictureWorkingActivity.mFrameWebView.needSelfLayout = true;
		GridViewAdapter adapter = new GridViewAdapter(mContext, result);
		mGallery.setAdapter(adapter);
		int cWidth = (int) mContext.getResources().getDimension(
				R.dimen.gallery_height);
		int cHeight = (int) mContext.getResources().getDimension(
				R.dimen.grid_height);
		LayoutParams params = new LayoutParams(mGallery.getCount()
				* (cWidth + 5), cHeight);
		mGallery.setLayoutParams(params);
		mGallery.setNumColumns(adapter.getCount());
	}
}