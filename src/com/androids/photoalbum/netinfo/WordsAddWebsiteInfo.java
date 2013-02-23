package com.androids.photoalbum.netinfo;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class WordsAddWebsiteInfo extends WebsiteInfo {
	public ArrayList mNameList = new ArrayList<ProductInfo>();
		 
	@Override
    public void newEntry(WebsiteInfo info) {
    	mNameList.add(info);
    }
    
	@Override
    public int getProductsCount() {
    	return mNameList.size();
    }
    
	@Override
    public WebsiteInfo getProductInfo(int position) {
    	return (ProductInfo)mNameList.get(position);
    }
	    
	public static class ProductInfo extends ClassificationWebSiteInfo.ProductInfo {
		
		public String getPictureAddress() {
			return photoaddress;
		}
		
		public Bitmap getPictureBitmap() {
			return photobitmap;
		}
		
		public void setPictureBitmap(Bitmap bitmap) {
			photobitmap = bitmap;
		}
	}
}


