package com.androids.photoalbum.utils;

import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.DelCollectionWebsiteParser;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.tab.ui.FavoriteActivity;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.androids.photoalbum.view.MainTabActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CommonWebsiteTask extends AsyncTask<String, Void, WebsiteInfo> {
	public static WebsiteParser mWebsiteParser;
	private Context mContext;
	private boolean showed = false;
//	private static ProgressDialog pd;
	
	public CommonWebsiteTask(WebsiteParser websiteParser, Context context) {
		mWebsiteParser = websiteParser;
		mContext = context;
//		pd = new ProgressDialog(context);
	}
	
	public ProgressDialog pd;
	protected void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {
			
			public void run() {
			    if(pd==null) {
			        if (((Activity)mContext) instanceof MessageActivity) {
			            pd=new ProgressDialog(mContext);
			        } else {
			            pd=new ProgressDialog(MainTabActivity.getCurrentActivityGroup());
			        }
//			    	pd.setTitle("加载图片");
			    }
			    pd.setMessage(" 加载中... ");
			    pd.show();	
			}
		};

	    
		((Activity)mContext).runOnUiThread(action);
	}
	
	@Override
	protected WebsiteInfo doInBackground(String... params) {
		showProgessBarDialog("");
//		showed = MainTabActivity.getCurrentActivityGroup().showProgessBarDialog("加载中...");
		WebsiteInfo websiteInfo = mWebsiteParser.parseWebsiteInfo(params[0]);
		return websiteInfo;
	}
	
	@Override
	protected void onPostExecute(WebsiteInfo result) {
//		if (showed) {
//			MainTabActivity.getCurrentActivityGroup().dismissProgressBarDialog();
//		}
		pd.dismiss();
		if (mWebsiteParser instanceof DelCollectionWebsiteParser) {
//			((FavoriteActivity)mContext).reloadFavoriteList();
			FavoriteActivity.mThis.reloadFavoriteList();
		}
		Utils.notifyToUser(result.resultdesc, mContext);
		if (ServerInfo.FAIL.equals(result.result)) {
			return;
		}
//		super.onPostExecute(result);
	}
}