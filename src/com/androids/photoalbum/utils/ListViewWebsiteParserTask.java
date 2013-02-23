package com.androids.photoalbum.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.FavoriteActivity;
import com.androids.photoalbum.view.MainTabActivity;

public class ListViewWebsiteParserTask extends
		AsyncTask<String, Void, WebsiteInfo> {
	private Context mContext;
	private WebsiteParser mWebsiteParser;

	private ListView mListView;
	private int mLineCount;
	private int mImageWidth;
	private int mImageHeigth;
	private boolean showed;
	private boolean showFooter = false;

	public ListViewWebsiteParserTask(Context context,
			WebsiteParser websiteParser, ListView listView, int linecount,
			int with, int height, boolean showfooter) {
		mContext = context;
		mWebsiteParser = websiteParser;
		mListView = listView;
		mLineCount = linecount;
		mImageWidth = with;
		mImageHeigth = height;

		// showFooter = showfooter;
	}

	@Override
	protected WebsiteInfo doInBackground(String... params) {
		// pd = new ProgressDialog(MainTabActivity.getCurrentActivityGroup());
		((BaseActivity) mContext).showProgessBarDialog("");
		// MainTabActivity.getCurrentActivityGroup().showProgessBarDialog("加载中...");
		// pd = Utils.showProgessBarDialog("Loading...",
		// MainTabActivity.getCurrentActivityGroup());
		WebsiteInfo webinfo = mWebsiteParser.parseWebsiteInfo(params[0]);

		return webinfo;
	}

	@Override
	protected void onPostExecute(WebsiteInfo websiteinfo) {
		Utils.notifyToUser(websiteinfo.resultdesc, mContext);

		// MainTabActivity.getCurrentActivityGroup().dismissProgressBarDialog();
		((BaseActivity) mContext).pd.dismiss();

		if (ServerInfo.FAIL.equals(websiteinfo.result)) {
			return;
		}

		if (websiteinfo.getProductsCount() >= 30 && showFooter) {
			((BaseActivity) mContext).mFooterView.setVisibility(View.VISIBLE);
		} else {
			((BaseActivity) mContext).mFooterView.setVisibility(View.GONE);
		}

		WebsiteInfo listwebsiteinfo;
		if (mListView.getAdapter() != null && showFooter) {
			listwebsiteinfo = ((ListViewAdapter) mListView.getAdapter()).mWebsiteInfo;
			int count = websiteinfo.getProductsCount();
			for (int i = 0; i < count; i++) {
				listwebsiteinfo.newEntry(websiteinfo.getProductInfo(i));
			}
		} else {
			listwebsiteinfo = websiteinfo;
		}

		int startIndex = (Integer) ((BaseActivity) mContext).mFooterView
				.getTag();
		((BaseActivity) mContext).mFooterView.setTag(++startIndex);
		ListViewAdapter albumAdapter = new ListViewAdapter(mContext,
				listwebsiteinfo, mLineCount, mImageWidth, mImageHeigth);
		// mListView.removeAllViews();
		mListView.setAdapter(albumAdapter);
		albumAdapter.notifyDataSetChanged();
		// mListView.addFooterView(((BaseActivity)mContext).mFooterView);

		if (((BaseActivity) mContext) instanceof FavoriteActivity
				|| MainTabActivity.mCurrentPage == 4) {
			FavoriteActivity.notLoaded = false;
		}
	}
}