package com.androids.photoalbum.utils;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ProductInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.view.ImageLayout;
import com.androids.photoalbum.view.MainTabActivity;

public class ListViewAdapter extends BaseAdapter implements
		View.OnClickListener {
	private Context mContext;
	public WebsiteInfo mWebsiteInfo = null;
	private LayoutInflater mInflater;
	private LoadImageTask mLoadImageTask = null;
	private ContentResolver mResolver;
	private int mImageWidth;
	private int mImageHeigth;
	private Resources mResources;
	private int mLineCount;
	private boolean mShowButton = false;

	public ListViewAdapter(Context a, WebsiteInfo websiteinfo, int linecount,
			int width, int length) {
		mContext = a;
		mWebsiteInfo = websiteinfo;
		mInflater = (LayoutInflater) mContext
				.getSystemService("layout_inflater");
		mResolver = mContext.getContentResolver();

		mResources = mContext.getResources();
		mImageWidth = width;
		mImageHeigth = length;

		mLineCount = linecount;
	}

	public int getCount() {
		return mWebsiteInfo.getProductsCount() / mLineCount + 1;
	}

	public Object getItem(int position) {
		ArrayList<String> list = new ArrayList<String>();
		int productCount = mWebsiteInfo.getProductsCount();
		for (int index = 0; index < productCount; index++) {
			ProductInfo productInfo = (ProductInfo) mWebsiteInfo
					.getProductInfo(index);
			list.add(productInfo.photoaddress);
		}
		return list;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (MainTabActivity.mCurrentPage == 4) {
			mShowButton = true;
		} else {
			mShowButton = false;
		}

		LinearLayout lineLayout = (LinearLayout) mInflater.inflate(
				R.layout.linear_layout, null);
		ProductInfo productInfo = null;
		int productCount = mWebsiteInfo.getProductsCount();
		int index = position * mLineCount;
		ImageLayout imageLayout = null;
		ImageView imageView = null;
		ProgressBar probar = null;
		boolean needDownload = false;
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.default_image);
		if (mLineCount > 1) {
			bmp = Utils.getResizedBitmap(bmp, mImageWidth, mImageHeigth);
		}
		productCount = index + mLineCount < productCount ? index + mLineCount
				: productCount;

		for (; index < productCount; index++) {
			productInfo = (ProductInfo) mWebsiteInfo.getProductInfo(index);

			imageLayout = new ImageLayout(mContext, null, productInfo,
					mShowButton);
			imageView = (ImageView) imageLayout.findViewById(R.id.image);
			probar = (ProgressBar) imageLayout.findViewById(R.id.progressbar);
			if (productInfo.photobitmap != null) {
				probar.setVisibility(View.GONE);
				imageView.setImageBitmap(productInfo.photobitmap);
			} else {
				imageView.setImageBitmap(bmp);
				probar.setVisibility(View.VISIBLE);
				needDownload = true;
			}

			((LinearLayout) lineLayout).addView(imageLayout);
		}

		if (needDownload && mLoadImageTask == null) {
			mLoadImageTask = new LoadImageTask(mContext, mWebsiteInfo);
			mLoadImageTask.execute();
		}

		if (true) {
			return lineLayout;
		}

		convertView = mInflater.inflate(R.layout.list_item, null);
		WebsiteInfo websiteInfo = (WebsiteInfo) getItem(position);

		TextView tv = (TextView) convertView.findViewById(R.id.title);
		tv.setOnClickListener(this);
		tv.setText(websiteInfo.mBaseInfo.classname);
		bmp = Utils.getResizedBitmap(bmp, mImageWidth, mImageHeigth);
		productCount = websiteInfo.getProductsCount();

		for (int i = 0; i < productCount; i++) {
			productInfo = (ProductInfo) websiteInfo.getProductInfo(i);
			imageLayout = new ImageLayout(mContext, null, productInfo, false);
			imageView = (ImageView) imageLayout.findViewById(R.id.image);
			probar = (ProgressBar) imageLayout.findViewById(R.id.progressbar);
			if (productInfo.photobitmap != null) {
				probar.setVisibility(View.GONE);
				imageView.setImageBitmap(productInfo.photobitmap);
			} else {
				imageView.setImageBitmap(bmp);
				probar.setVisibility(View.VISIBLE);
				needDownload = true;
			}

			((LinearLayout) convertView).addView(imageLayout, i + 1);
		}

		if (needDownload && mLoadImageTask == null) {
			mLoadImageTask = new LoadImageTask(mContext, mWebsiteInfo);
			mLoadImageTask.execute();
		}

		return convertView;
	}

	public void onClick(View v) {
		Log.d("zheng", "onClick");
	}
}
