package com.androids.photoalbum.netinfo;

import java.util.ArrayList;

public class ForcePushWebsiteInfo extends WebsiteInfo {
	public ArrayList mProductList = new ArrayList<ProductInfo>();
		 
	@Override
    public void newEntry(WebsiteInfo info) {
    	mProductList.add(info);
    }
    
	@Override
    public int getProductsCount() {
    	return mProductList.size();
    }
    
	@Override
    public WebsiteInfo getProductInfo(int position) {
    	return (ProductInfo)mProductList.get(position);
    }
	    
	public static class ProductInfo extends ClassificationWebSiteInfo.ProductInfo {
		public String time = null;
		public ArrayList mContents = new ArrayList<String>();
	}
	
}


