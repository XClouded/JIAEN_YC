package com.androids.photoalbum.netinfo;

import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class SearchWebsiteInfo extends WebsiteInfo {
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
//		public String productid = null;
		public String parentclass = null;
		public String sonclass = null;
//		public String proname = null;
//		public String productnum = null;
//		public String photoaddress = null;
//		public Bitmap photoBitmap = null;
		public String istj = null;
		public String ishot = null;
		public String isyn = null;
		public String hits = null;
		public String downhits = null;
		public String addtime = null;
	}
	
	public static class BaseInfo {
		public String classid = null;
		public String classname = null;
	}
}


