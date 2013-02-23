package com.androids.photoalbum.netinfo;

import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MusicWebsiteInfo extends WebsiteInfo {
	public ArrayList mMusicList = new ArrayList<MusicInfo>();
		 
	@Override
    public void newEntry(WebsiteInfo info) {
    	mMusicList.add(info);
    }
    
	@Override
    public int getProductsCount() {
    	return mMusicList.size();
    }
    
	@Override
    public WebsiteInfo getProductInfo(int position) {
    	return (MusicInfo)mMusicList.get(position);
    }
	    
	public static class MusicInfo extends WebsiteInfo {
		public String id = null;
		public String pname = null;
		public String hname = null;
		public String amrurl = null;
	}
}


