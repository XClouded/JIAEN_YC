package com.androids.photoalbum.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ConclassInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ParentClassInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ProductInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.tab.ui.HomeActivity;
import com.androids.photoalbum.tab.ui.HomeConclassActivity;
import com.androids.photoalbum.view.HomeDownloadTopActivity;
import com.androids.photoalbum.view.ImageLayout;
import com.androids.photoalbum.view.MainTabActivity;

public class PhotoListAdapter extends BaseAdapter implements
		View.OnClickListener {
	private Context mContext;
	private WebsiteInfo mWebsiteInfo = null;
	private LayoutInflater mInflater;
	private LoadImageTask mLoadImageTask = null;
	private ContentResolver mResolver;
	private int mImageWidth;
	private int mImageHeigth;
	private Resources mResources;

	public PhotoListAdapter(Context a, WebsiteInfo websiteinfo) {
		mContext = a;
		mWebsiteInfo = websiteinfo;
		mInflater = (LayoutInflater) mContext
				.getSystemService("layout_inflater");
		mResolver = mContext.getContentResolver();

		mResources = mContext.getResources();
		mImageWidth = (int) mResources.getDimension(R.dimen.image_with);
		mImageHeigth = (int) mResources.getDimension(R.dimen.image_height);
	}

	public int getCount() {
		return mWebsiteInfo.getClassCount();
	}

	public Object getItem(int position) {
		return mWebsiteInfo.getClassInfo(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// 彩信浏览分类图片适配
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.q3_liulan_layout, null);
		WebsiteInfo websiteInfo = (WebsiteInfo) getItem(position);
		ViewHolder holder = new ViewHolder();
		holder.textView = (TextView) convertView
				.findViewById(R.id.q3_item_text_layout);
		holder.textView.setOnClickListener(this);
		holder.textView.setText(websiteInfo.mBaseInfo.classname);
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.default_image);
		bmp = Utils.getResizedBitmap(bmp, mImageWidth, mImageHeigth);
		int productCount = websiteInfo.getProductsCount();
		boolean needDownload = false;
		ProductInfo productInfo = null;
		ImageLayout imageLayout = null;

		holder.imageView = (ImageView) convertView
				.findViewById(R.id.q3_item_image_layout);
		holder.imageView.setOnClickListener(this);
		ProgressBar probar = null;
		System.out.println("productCount=" + productCount);
		for (int i = 0; i < 1; i++) {
			productInfo = (ProductInfo) websiteInfo.getProductInfo(i);
			imageLayout = new ImageLayout(mContext, null, productInfo, false);
			// imageLayout.setProductInfo(productInfo);

			// probar = (ProgressBar)
			// imageLayout.findViewById(R.id.progressbar);
			if (productInfo.photobitmap != null) {
				// probar.setVisibility(View.GONE);
				holder.imageView.setImageBitmap(productInfo.photobitmap);
			} else {
				holder.imageView.setImageBitmap(bmp);
				// probar.setVisibility(View.VISIBLE);
				needDownload = true;

			}
			// 图片添加到彩信浏览中，四张图片
			((ViewGroup) convertView).addView(imageLayout, i + 1);
		}

		if (needDownload && mLoadImageTask == null) {
			mLoadImageTask = new LoadImageTask(mContext, mWebsiteInfo);
			mLoadImageTask.execute();
		}

		return convertView;
	}

	public void onClick(View v) {
		if (v instanceof TextView) {
			Log.d("zheng", "get class:" + ((TextView) v).getText().toString());
			WebsiteInfo websiteInfo = mWebsiteInfo.getClassInfo(((TextView) v)
					.getText().toString());
			if (websiteInfo instanceof ParentClassInfo) {
				if ("下载排行".equals(((TextView) v).getText().toString())) {
					Intent intent = new Intent(mContext,
							HomeDownloadTopActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("optype", WebsiteParser.ALL);
					intent.putExtra("is_latest_update", false);
					// mContext.startActivity(intent);
					MainTabActivity.mHomeActivityGroup.setNextActivity(intent,
							"HomeDownloadTopActivity");
					return;
				} else if ("最近更新".equals(((TextView) v).getText().toString())) {
					Intent intent = new Intent(mContext,
							HomeDownloadTopActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("optype", WebsiteParser.ALL);
					intent.putExtra("is_latest_update", true);
					MainTabActivity.mHomeActivityGroup.setNextActivity(intent,
							"HomeDownloadTopActivity");
					return;
				}

				Intent intent = new Intent(mContext, HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("classid", websiteInfo.mBaseInfo.classid);
				intent.putExtra("optype", WebsiteParser.PARENTCLASS);
				intent.putExtra("show_titlebar", true);
				// mContext.startActivity(intent);
				MainTabActivity.mHomeActivityGroup.setNextActivity(intent,
						"HomeActivity_Parent");
			} else if (websiteInfo instanceof ConclassInfo) {
				if ("下载排行".equals(((TextView) v).getText().toString())) {
					Intent intent = new Intent(mContext,
							HomeDownloadTopActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("classid", mWebsiteInfo.mBaseInfo.classid);
					intent.putExtra("optype", WebsiteParser.PARENTCLASS);
					// mContext.startActivity(intent);
					MainTabActivity.mHomeActivityGroup.setNextActivity(intent,
							"HomeDownloadTopActivity");
					return;
				}

				Intent intent = new Intent(mContext, HomeConclassActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("classid", websiteInfo.mBaseInfo.classid);
				intent.putExtra("optype", WebsiteParser.SINGLE);
				intent.putExtra("parentid", mWebsiteInfo.mBaseInfo.classid);
				// mContext.startActivity(intent);
				MainTabActivity.mHomeActivityGroup.setNextActivity(intent,
						"HomeConclassActivity");
			}
		} else if (v instanceof ImageView) {

		}
	}

	private void startDownloadTopActivity() {
		Intent intent = new Intent(mContext, HomeDownloadTopActivity.class);
		if (mWebsiteInfo instanceof ClassificationWebSiteInfo) {
			// intent.putExtra("classid", websiteInfo.mBaseInfo.classid);
			intent.putExtra("optype", WebsiteParser.ALL);
		} else if (mWebsiteInfo instanceof ClassificationWebSiteInfo.ParentClassInfo) {
			intent.putExtra("classid", mWebsiteInfo.mBaseInfo.classid);
			intent.putExtra("optype", WebsiteParser.PARENTCLASS);
		} else if (mWebsiteInfo instanceof ClassificationWebSiteInfo.ConclassInfo) {

		}

		mContext.startActivity(intent);
	}

	class ViewHolder {
		TextView textView;
		ImageView imageView;
	}
}
// }