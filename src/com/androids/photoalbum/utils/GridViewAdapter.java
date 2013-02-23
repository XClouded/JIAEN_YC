package com.androids.photoalbum.utils;

import java.io.ByteArrayOutputStream;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.EffectWebsiteInfo;
import com.androids.photoalbum.netinfo.FrameWebsiteInfo;
import com.androids.photoalbum.netinfo.TemplateWebsiteInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.provider.AlbumContent.Album;
import com.androids.photoalbum.provider.AlbumContent.AlbumColumns;
import com.androids.photoalbum.view.PictureWorkingActivity;


public class GridViewAdapter extends BaseAdapter {
	private WebsiteInfo mWebsiteInfo;
	private Context mContext;
	private ContentResolver mResolver;
	private Resources mResources;
	private static int mImageWidth; 
	private static int mImageHeigth; 
	private LayoutInflater mInflater;
	private AsyncTask<Void, Void, Void> mLoadPicTask;
	private ImageView mImageView;
	
	public GridViewAdapter(Context context, WebsiteInfo websiteInfo) {
		mWebsiteInfo = websiteInfo;
		mContext = context;
		mResolver = mContext.getContentResolver();
		mResources = mContext.getResources();
		mInflater = (LayoutInflater)mContext.getSystemService("layout_inflater");
//		mImageView = (ImageView)mInflater.inflate(R.layout.image, null);
	    mImageWidth = (int)mResources.getDimension(R.dimen.image_with);
	    mImageHeigth = (int)mResources.getDimension(R.dimen.image_height);
//		mImageWidth = mImageView.getWidth();
//		mImageHeigth = mImageView.getHeight();
	}
	
	public WebsiteInfo getWebsiteInfo() {
		return mWebsiteInfo;
	}
	
	public int getGalleryType() {
		if (mWebsiteInfo instanceof EffectWebsiteInfo) {
			return PictureWorkingActivity.GALLERY_EFFECT_TYPE;
		} else if (mWebsiteInfo instanceof FrameWebsiteInfo) {
			return PictureWorkingActivity.GALLERY_FRAME_TYPE;
		} else if (mWebsiteInfo instanceof TemplateWebsiteInfo) {
			return PictureWorkingActivity.GALLERY_TEMPLATE_TYPE;
		}
		
		return -1;
	}


	public int getCount() {
		return mWebsiteInfo.getProductsCount();
	}

	public Object getItem(int position) {
		return mWebsiteInfo.getProductInfo(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imgView = (ImageView)convertView;
		if (imgView == null) {
			imgView = (ImageView)mInflater.inflate(R.layout.image, null);
//			imgView = new ImageView(mContext);
		}
		
		WebsiteInfo info = (WebsiteInfo)getItem(position);
		if (info.getPictureBitmap() == null) {
			if (mLoadPicTask == null) {
				mLoadPicTask = new LoadPictureTask(mWebsiteInfo).execute();
			}
		} else {
			imgView.setImageBitmap(info.getPictureBitmap());
		}
		
		return imgView;
	}
	
    public static Bitmap getPhotoFromDatabase(ContentResolver resolver, String picpath) {
		Cursor cursor = null;
		String selection = AlbumColumns.PHOTO_ADDRESS + "=?";
		
		try {
			cursor = resolver.query(Album.ALBUM_CONTENT_URI, Album.CONTENT_PROJECTION, selection,
					new String[] { picpath }, null);
			
			if (cursor != null && cursor.moveToFirst()) {
	            byte[] data = cursor.getBlob(AlbumColumns.CONTENT_PHOTO_BITMAP_COLUMN);
	            if (data != null && data.length != 0) {
	            	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
	            	bitmap = Utils.getResizedBitmap(bitmap, mImageWidth, mImageHeigth);
	            	return bitmap;
	            }
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return null;
    }
    
    public static void savePhotoIntoDatabase(ContentResolver resolver, WebsiteInfo websiteInfo) {

        // 最终图标要保存到浏览器的内部数据库中，系统程序均保存为SQLite格式，Browser也不例外，因为图片是二进制的所以使用字节数组存储数据库的
        // BLOB类型
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 将Bitmap压缩成PNG编码，质量为100%存储
        websiteInfo.getPictureBitmap().compress(Bitmap.CompressFormat.PNG, 100, os);
        // 构造SQLite的Content对象，这里也可以使用raw
        ContentValues values = new ContentValues();
        byte[] imgByte = os.toByteArray();
        // 写入数据库的Browser.BookmarkColumns.TOUCH_ICON字段
        values.put(AlbumColumns.PHOTO_BITMAP, imgByte);
    	values.put(AlbumColumns.PHOTO_ADDRESS, websiteInfo.getPictureAddress());
    	resolver.insert(Album.ALBUM_CONTENT_URI, values);;
    }
    
	public class LoadPictureTask extends AsyncTask<Void, Void, Void> {
		private WebsiteInfo mWebsiteInfo;
		
		public LoadPictureTask(WebsiteInfo websiteInfo) {
			mWebsiteInfo = websiteInfo;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			int count = mWebsiteInfo.getProductsCount();
			WebsiteInfo websiteInfo = null;
			Bitmap bitmap = null;
			for (int i = 0; i < count; i++) {
				websiteInfo = mWebsiteInfo.getProductInfo(i);
				bitmap = websiteInfo.getPictureBitmap();
				String photoaddress = websiteInfo.getPictureAddress();
				if (bitmap == null) {
					bitmap = getPhotoFromDatabase(mResolver, websiteInfo.getPictureAddress());
					if (bitmap == null) {
    					try {
    						bitmap = Utils.getBitMapFromNetwork(websiteInfo.getPictureAddress(), mContext);
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
					}
					
					if (bitmap != null) {
						bitmap = Utils.getResizedBitmap(bitmap, mImageWidth, mImageHeigth);
						websiteInfo.setPictureBitmap(bitmap);
						
//						GalleryAdapter.this.notifyDataSetChanged();
						Intent intent = new Intent(PictureWorkingActivity.ACTION_GALLERY_NOTIFY_DATA_SET_CHANGED);
						mContext.sendBroadcast(intent);
					}
				}
			}
			return null;
		}
	}
}