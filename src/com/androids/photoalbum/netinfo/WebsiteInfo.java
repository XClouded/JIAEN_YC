package com.androids.photoalbum.netinfo;

import android.graphics.Bitmap;

import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.BaseInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ParentClassInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ProductInfo;


public class WebsiteInfo {
	public String result = null;
	public String resultdesc = null;
	public String workingphotoaddress = null;
//	public Bitmap picture = null;
//	public String classid = null;
//	public String classname = null;
	public BaseInfo mBaseInfo = new BaseInfo();
	
    public void newEntry(WebsiteInfo info) {
    	
    }
    
    public void newEntry(String info) {
    }
    
    public int getClassCount() {
    	return 0;
    }
    
    public WebsiteInfo getClassInfo(int position) {
    	return null;
    }
    
    public int getProductsCount() {
    	return 0;
    }
    
    public WebsiteInfo getProductInfo(int position) {
    	return null;
    }
    
    public WebsiteInfo getClassInfo(String classname) {
		int count = getClassCount();
		WebsiteInfo websiteInfo = null;
		for (int i = 0; i < count; i++) {
			websiteInfo = getClassInfo(i);
			if (websiteInfo.mBaseInfo.classname.equals(classname)) {
				return websiteInfo;
			}
		}
		
		return null;
    }
    
	public String getPictureAddress() {
		return null;
	}
	
	public Bitmap getPictureBitmap() {
		return null;
	}
	
	public void setPictureBitmap(Bitmap bitmap) {
		
	}
}

