package com.androids.photoalbum.netinfo;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ClassificationWebSiteInfo extends WebsiteInfo {
	public ArrayList mParentInfoList = new ArrayList<ParentClassInfo>();
	
	@Override
    public void newEntry(WebsiteInfo info) {
    	mParentInfoList.add(info);
    }
    
	@Override
    public int getClassCount() {
    	return mParentInfoList.size();
    }
    
	@Override
    public WebsiteInfo getClassInfo(int position) {
    	return (ParentClassInfo) mParentInfoList.get(position);
    }
    
	public static class ParentClassInfo extends WebsiteInfo {
		public ArrayList mConclassInfoList = new ArrayList<ConclassInfo>();
		
		@Override
	    public void newEntry(WebsiteInfo info) {
	    	mConclassInfoList.add(info);
	    }
	    
		@Override
	    public int getClassCount() {
	    	return mConclassInfoList.size();
	    }
	    
		@Override
	    public WebsiteInfo getClassInfo(int position) {
	    	return (ConclassInfo)mConclassInfoList.get(position);
	    }
	    
		@Override
        public WebsiteInfo getProductInfo(int position) {  
        	int conclassCount = getClassCount();
        	int count = 0;
        	for (int i = 0; i < conclassCount; i++) {
        		if (count + getClassInfo(i).getClassCount() <= position) {
        			count += getClassInfo(i).getClassCount();
        		} else {
        			return getClassInfo(i).getProductInfo(position - count);
        		}
        	}
        	
            return null;  
        }  
	    
		@Override
	    public int getProductsCount() {
        	int conclassCount = getClassCount();
        	int count = 0;
        	for (int i = 0; i < conclassCount; i++) {
        		count += getClassInfo(i).getProductsCount();
        	}
        	
            return count > 4 ? 4 : count;  
	    }
	}
	
	public static class ConclassInfo extends WebsiteInfo {
		public ArrayList mProductInfoList = new ArrayList<ProductInfo>();
		
		@Override
	    public void newEntry(WebsiteInfo info) {
	    	mProductInfoList.add(info);
	    }
		
		@Override
	    public int getClassCount() {
	    	return mProductInfoList.size();
	    }
//	    
//		@Override
//	    public WebsiteInfo getClassInfo(int position) {
//	    	return (ConclassInfo)mProductInfoList.get(position);
//	    }
		
	    
		@Override
	    public int getProductsCount() {
	    	int size = mProductInfoList.size();
	    	return size > 4 ? 4:size;
	    }
	    
		@Override
	    public ProductInfo getProductInfo(int position) {
	    	return (ProductInfo)mProductInfoList.get(position);
	    }
	}
	
	public static class ProductInfo extends WebsiteInfo {
		public String id = null;
		public String productid = null;
		public String proname = null;
		public String productnum = null;
		public String photoaddress = null;
		public Bitmap photobitmap = null;
		public byte[] photobyte = null;
	}
	
	public static class BaseInfo {
		public String classid = null;
		public String classname = null;
	}
}


