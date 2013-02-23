package com.androids.photoalbum.netinfo;

import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class FrameWebsiteInfo extends WebsiteInfo {
	public ArrayList mFrameList = new ArrayList<FrameInfo>();
		 
	@Override
    public void newEntry(WebsiteInfo info) {
    	mFrameList.add(info);
    }
    
	@Override
    public int getProductsCount() {
    	return mFrameList.size();
    }
    
	@Override
    public WebsiteInfo getProductInfo(int position) {
    	return (FrameInfo)mFrameList.get(position);
    }
	    
	public static class FrameInfo extends WebsiteInfo {
		public String frameaddress = null;
		public Bitmap framebitmap = null;
		
		public String getPictureAddress() {
			return frameaddress;
		}
		
		public Bitmap getPictureBitmap() {
			return framebitmap;
		}
		
		public void setPictureBitmap(Bitmap bitmap) {
			framebitmap = bitmap;
		}
	}
}


