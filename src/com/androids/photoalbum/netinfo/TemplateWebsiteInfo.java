package com.androids.photoalbum.netinfo;

import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class TemplateWebsiteInfo extends WebsiteInfo {
	public ArrayList mTemplateList = new ArrayList<TemplateInfo>();
		 
	@Override
    public void newEntry(WebsiteInfo info) {
    	mTemplateList.add(info);
    }
    
	@Override
    public int getProductsCount() {
    	return mTemplateList.size();
    }
    
	@Override
    public WebsiteInfo getProductInfo(int position) {
    	return (TemplateInfo)mTemplateList.get(position);
    }
	    
	public static class TemplateInfo extends WebsiteInfo {
		public String stenciladdress = null;
		public Bitmap stencilbitmap = null;
		
		public String getPictureAddress() {
			return stenciladdress;
		}
		
		public Bitmap getPictureBitmap() {
			return stencilbitmap;
		}
		
		public void setPictureBitmap(Bitmap bitmap) {
			stencilbitmap = bitmap;
		}
	}
}


