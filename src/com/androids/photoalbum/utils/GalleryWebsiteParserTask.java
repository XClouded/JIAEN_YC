package com.androids.photoalbum.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Gallery;
import android.widget.Toast;

import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.view.MainTabActivity;
import com.androids.photoalbum.view.PictureWorkingActivity;

public class GalleryWebsiteParserTask extends AsyncTask<String, Void, WebsiteInfo> {
    public WebsiteParser mWebsiteParser = null;

    private Gallery mGallery;
    private Context mContext;
    private boolean showed;
//    private static ProgressDialog pd;
    
    public GalleryWebsiteParserTask(Context context, WebsiteParser parser, Gallery gallery) {
    	mWebsiteParser = parser;
    	mGallery = gallery;
    	mContext = context;
//    	pd = new ProgressDialog(context);
    }
    
	@Override
	protected WebsiteInfo doInBackground(String... params) {
//		pd = new ProgressDialog(MainTabActivity.getCurrentActivityGroup());
		showed = MainTabActivity.getCurrentActivityGroup().showProgessBarDialog("加载中...");
//		pd = Utils.showProgessBarDialog("Loading...", mContext);
		WebsiteInfo websiteInfo = mWebsiteParser.parseWebsiteInfo(params[0]);
		return websiteInfo;
	} 
	
	@Override
	protected void onPostExecute(WebsiteInfo result) {
//		if (showed) {
			MainTabActivity.getCurrentActivityGroup().dismissProgressBarDialog();
//		}
		
		postExecute(result);
	}
	
	public void postExecute(WebsiteInfo result) {
		Utils.notifyToUser(result.resultdesc, mContext);
		if (ServerInfo.FAIL.equals(result.result)) {
			return;
		}
		
		GalleryAdapter adapter = new GalleryAdapter(mContext, result);
		mGallery.setAdapter(adapter);
		mGallery.setSelection(2);
	}
}