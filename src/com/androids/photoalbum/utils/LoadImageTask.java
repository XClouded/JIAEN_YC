package com.androids.photoalbum.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.CollectionsWebsiteInfo;
import com.androids.photoalbum.netinfo.SearchWebsiteInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ProductInfo;
import com.androids.photoalbum.tab.ui.BaseActivity;



public class LoadImageTask extends AsyncTask<Void, Void, Void> {
//        	private ClassificationWebSiteInfo mClassificationInfo;
//        	private WebsiteInfo classWebsiteInfo;
//        	private PhotoListAdapter mAdapter;
	private Context mContext;
	private WebsiteInfo mWebsiteInfo = null;
	private ContentResolver mResolver;
	
	public LoadImageTask(Context context, WebsiteInfo websiteInfo) {
//        		mClassificationInfo = (ClassificationWebSiteInfo)mWebsiteInfo;
//        		classWebsiteInfo = parentInfo;
//        		mAdapter = adapter;
		mContext = context;
		mWebsiteInfo = websiteInfo;
		mResolver = context.getContentResolver();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
//    			int parentClassCount = mClassificationInfo.getParentClassCount();
		if (mWebsiteInfo instanceof ClassificationWebSiteInfo
				|| mWebsiteInfo instanceof ClassificationWebSiteInfo.ParentClassInfo) {
			loadClassificationProductImage();
		} else {
			loadProductImage();
		}
		
//		else if (mWebsiteInfo instanceof CollectionsWebsiteInfo) {
//			loadCollectionProductImage();
//		} else if (mWebsiteInfo instanceof SearchWebsiteInfo) {
//			loadProductImage();
//		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
//    			PhotoListAdapter.this.notifyDataSetChanged();
	}
	
	private void loadCollectionProductImage() {
		int count = mWebsiteInfo.getProductsCount();
		CollectionsWebsiteInfo.ProductInfo productinfo = null;
		for (int i = 0; i < count; i++) {
			productinfo = (CollectionsWebsiteInfo.ProductInfo)mWebsiteInfo.getProductInfo(i);
			loadProductImage(productinfo);
		}
	}
	
	private void loadProductImage() {
		int count = mWebsiteInfo.getProductsCount();
		ClassificationWebSiteInfo.ProductInfo productinfo = null;
		for (int i = 0; i < count; i++) {
			productinfo = (ClassificationWebSiteInfo.ProductInfo)mWebsiteInfo.getProductInfo(i);
			loadProductImage(productinfo);
		}
	}
	
	private void loadClassificationProductImage() {
		int count = mWebsiteInfo.getClassCount();
		WebsiteInfo classWebsiteInfo = null;

		for (int i = 0; i < count; i++) {
			classWebsiteInfo = mWebsiteInfo.getClassInfo(i);
//    				ParentClassInfo mParentInfo = mClassificationInfo.getParentClassInfo(i);
    		int productCount = classWebsiteInfo.getProductsCount();
    		for (int j = 0; j < productCount; j++) {
    			ProductInfo productinfo = (ProductInfo)classWebsiteInfo.getProductInfo(j);
    			loadProductImage(productinfo);
    		}
		}
	}
	
	private void loadProductImage(ProductInfo productinfo) {
		if (productinfo.photobitmap == null) {
			
			// query the photo from database firstly
			productinfo.photobitmap = Utils.getPhotoFromDatabase(mContext, productinfo);
			if (productinfo.photobitmap != null) {
				Log.d("zheng", "getPhotoFromDatabase:" + productinfo.photoaddress);
			}
			
			// if database doesn't store this photo, get it from network
			if (productinfo.photobitmap == null) {
				try {
					productinfo.photobitmap = Utils.getBitMapFromNetwork(productinfo.photoaddress, mContext);
    				if (productinfo.photobitmap != null) {
    					Log.d("zheng", "getBitMapFromNetwork:" + productinfo.photoaddress);
    				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (productinfo.photobitmap != null) {
					// restore this photo into database
//					Utils.savePhotoIntoDatabase(mResolver, productinfo);
					
					productinfo.photobitmap = Utils.getResizedBitmap(productinfo.photobitmap, ((BaseActivity)mContext).mImageWidth, ((BaseActivity)mContext).mImageHeigth);
					Log.d("zheng", "the bitmap with:" + productinfo.photobitmap.getWidth());
					Log.d("zheng", "the bitmap height:" + productinfo.photobitmap.getHeight());
				}
			}
			
  			if (productinfo.photobitmap != null) {
				Intent intent = new Intent(BaseActivity.ACTION_NOTIFY_DATA_SET_CHANGED);
				mContext.sendBroadcast(intent);
			}
		}
	}
}