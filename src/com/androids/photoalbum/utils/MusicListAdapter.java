package com.androids.photoalbum.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androids.photoalbum.netinfo.MusicWebsiteInfo.MusicInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;

    public class MusicListAdapter extends ArrayAdapter<String> {  
    	public MusicListAdapter(Context context, int textViewResourceId, WebsiteInfo websiteinfo) {
			super(context, textViewResourceId);
			
        	mContext = context;  
        	mWebsiteInfo = websiteinfo;
        	mInflater = (LayoutInflater)mContext.getSystemService("layout_inflater");
        	mResolver = mContext.getContentResolver();
		}

		private Context mContext;  
    	private WebsiteInfo mWebsiteInfo = null;
    	private LayoutInflater mInflater;
    	private ContentResolver mResolver;
//    	private static int mImageWidth; 
//    	private static int mImageHeigth; 
//    	private Resources mResources;
          
//        public MusicListAdapter(Context a, WebsiteInfo websiteinfo) {  
//        	mContext = a;  
//        	mWebsiteInfo = websiteinfo;
//        	mInflater = (LayoutInflater)mContext.getSystemService("layout_inflater");
//        	mResolver = mContext.getContentResolver();
//        }  
          
        public int getCount() {
    		return mWebsiteInfo.getProductsCount() + 1;
        }  
  
        public String getItem(int position) { 
        	if (position == 0) {
        		return "点击下拉列表选择";
        	}
    		MusicInfo musicInfo = (MusicInfo)mWebsiteInfo.getProductInfo(position - 1);
    		return musicInfo.hname;
        }  
  
        public long getItemId(int position) {  
            return position;  
        }  
  
        public View getView(int position, View convertView, ViewGroup parent) {
        	if (convertView == null) {
        		convertView = new TextView(mContext);
        	}
        	
        	String hname = getItem(position);
        	((TextView)convertView).setTextColor(Color.BLACK);
        	((TextView)convertView).setText(hname);

            return convertView;  
        }  
        
//		public void onClick(View v) {
//			Log.d("zheng", "onClick");
//			if (v instanceof TextView) {
//				Log.d("zheng", "get class:" + ((TextView) v).getText().toString());
//				WebsiteInfo websiteInfo = mWebsiteInfo.getClassInfo(((TextView) v).getText().toString());
//		    	if (websiteInfo instanceof ParentClassInfo) {
//		    		Intent intent = new Intent(mContext, HomeActivity.class);
//		    		intent.putExtra("classid", websiteInfo.mBaseInfo.classid);
//		    		intent.putExtra("optype", WebsiteParser.PARENTCLASS);
//		    		mContext.startActivity(intent);
//		    	} else if (websiteInfo instanceof ConclassInfo) {
//		    		Intent intent = new Intent(mContext, HomeConclassActivity.class);
//		    		intent.putExtra("classid", websiteInfo.mBaseInfo.classid);
//		    		intent.putExtra("optype", WebsiteParser.SINGLE);
//		    		intent.putExtra("parentid", mWebsiteInfo.mBaseInfo.classid);
//		    		mContext.startActivity(intent);
//		    	}
//			} else if (v instanceof ImageView) {
//				
//			}
//		}
    }  
//}