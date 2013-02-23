package com.androids.photoalbum.netinfo;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class EffectWebsiteInfo extends WebsiteInfo {
	public ArrayList mEffectList = new ArrayList<EffectInfo>();
		 
	@Override
    public void newEntry(WebsiteInfo info) {
    	mEffectList.add(info);
    }
    
	@Override
    public int getProductsCount() {
    	return mEffectList.size();
    }
    
	@Override
    public WebsiteInfo getProductInfo(int position) {
    	return (EffectInfo)mEffectList.get(position);
    }
	    
	public static class EffectInfo extends WebsiteInfo {
		public String effectsaddress = null;
		public Bitmap effectsbitmap = null;
		
		public String getPictureAddress() {
			return effectsaddress;
		}
		
		public Bitmap getPictureBitmap() {
			return effectsbitmap;
		}
		
		public void setPictureBitmap(Bitmap bitmap) {
			effectsbitmap = bitmap;
		}
	}
}


