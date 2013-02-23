package com.androids.photoalbum.netinfo;

import java.util.ArrayList;

public class CollectionsWebsiteInfo extends WebsiteInfo {
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
//		public String id = null;
//		public String productid = null;
		public String photocount = null;
//		public String photoaddress = null;
//		public Bitmap photobitmap = null;
	}
	
//	public static class BaseInfo {
//		public String classid = null;
//		public String classname = null;
//	}
}


