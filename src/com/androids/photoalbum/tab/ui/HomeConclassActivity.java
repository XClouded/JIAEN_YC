package com.androids.photoalbum.tab.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ProductInfo;
import com.androids.photoalbum.netinfo.SearchWebsiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.SearchWebsiteParser;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.utils.ListViewAdapter;
import com.androids.photoalbum.utils.LoadImageTask;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.utils.Utils;
import com.androids.photoalbum.view.HomeDownloadTopActivity;
import com.androids.photoalbum.view.ImageLayout;
import com.androids.photoalbum.view.MainTabActivity;

public class HomeConclassActivity extends BaseActivity {
	private String classid;
	private LinearLayout mDownloadList;
	private final static int TYPE_DOWNLOAD_LIST = 2;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.product_list);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "",
				"", "返回");
		mProductList = (LinearLayout) findViewById(R.id.product_list);
		mDownloadList = (LinearLayout) findViewById(R.id.download_list);
		mListView = (ListView) findViewById(R.id.list_view);
		// mWebsiteParser = new ClassificationWebsiteParser();
		mImageWidth = (int) mResources.getDimension(R.dimen.image_with);
		mImageHeigth = (int) mResources.getDimension(R.dimen.image_height);
		mLineCount = 4;
		// loadServerInfo(WebsiteParser.ALL, null, null);
		Intent intent = getIntent();
		if (intent != null) {
			classid = intent.getStringExtra("classid");
			Log.d("zheng", "mConclassId: " + classid);
			String optype = intent.getStringExtra("optype");
			String parentid = intent.getStringExtra("parentid");
			String urlParam = "?productid=&parentclass=&sonclass=" + classid
					+ "&addtime=&pageindex=0&pagecount=20&ordertype=";
			new ProductWebsiteTask(this).execute(urlParam);

			new DownloadTopWebsiteTask(this).execute();
		}
	}

	public class ProductWebsiteTask extends
			AsyncTask<String, Void, WebsiteInfo> {
		private Context mContext;

		public ProductWebsiteTask(Context context) {
			mContext = context;
		}

		@Override
		protected WebsiteInfo doInBackground(String... params) {
			showProgessBarDialog("Loading...");
			WebsiteInfo webinfo = new SearchWebsiteParser()
					.parseWebsiteInfo(NetWorkUtils.GET_PRODUCTS + params[0]);
			notifyToUser(webinfo.resultdesc);
			if (ServerInfo.FAIL.equals(webinfo.result)) {
				return null;
			}

			return webinfo;
		}

		@Override
		protected void onPostExecute(WebsiteInfo websiteinfo) {
			if (websiteinfo == null) {
				return;
			}

			pd.dismiss();
			SearchWebsiteInfo productWebsiteInfo = (SearchWebsiteInfo) websiteinfo;
			mAlbumAdapter = new ListViewAdapter(mContext, productWebsiteInfo,
					4, mImageWidth, mImageHeigth);
			mListView.setAdapter(mAlbumAdapter);
		}
	}

	public class DownloadTopWebsiteTask extends AsyncTask<Void, Void, Void> {
		private Context mContext;
		private SearchWebsiteInfo mDownloadWebsiteInfo = null;

		public DownloadTopWebsiteTask(Context context) {
			mContext = context;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.d("zheng", "calling parseClassificationWebsiteInfo");
			showProgessBarDialog("Loading...");

			// get download list
			String urlParam = "?productid=&parentclass=&sonclass=" + classid
					+ "&addtime=&pageindex=0&pagecount=4&ordertype=DownHits";
			WebsiteInfo downloadlist = new SearchWebsiteParser()
					.parseWebsiteInfo(NetWorkUtils.GET_PRODUCTS + urlParam);
			if (ServerInfo.FAIL.equals(downloadlist.result)) {
				notifyToUser(downloadlist.resultdesc);
				mDownloadWebsiteInfo = null;
			} else {
				mDownloadWebsiteInfo = (SearchWebsiteInfo) downloadlist;
			}

			if (mDownloadWebsiteInfo != null) {
				getAndSavePhotoBitmap(mDownloadWebsiteInfo);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd != null) {
				pd.dismiss();
			}

			if (mDownloadWebsiteInfo != null) {
				displayProductList(mDownloadWebsiteInfo);
			}
		}

		private void displayProductList(WebsiteInfo websiteinfo) {
			int productCount = websiteinfo.getProductsCount();
			ImageLayout imageLayout = null;
			ImageView imageView = null;
			ProgressBar probar = null;
			boolean needDownload = false;
			mDownloadList.removeAllViewsInLayout();
			Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.default_image);
			bmp = Utils.getResizedBitmap(bmp, mImageWidth, mImageHeigth);

			TextView tv = (TextView) mInflater.inflate(R.layout.text_vertical,
					null);
			tv.setText("栏目下载排行");
			tv.setOnClickListener(HomeConclassActivity.this);
			mDownloadList.addView(tv, 0);

			for (int productIndex = 0; productIndex < productCount; productIndex++) {
				ProductInfo productinfo = (ProductInfo) websiteinfo
						.getProductInfo(productIndex);
				imageLayout = new ImageLayout(mContext, null, productinfo,
						false);

				imageView = (ImageView) imageLayout.findViewById(R.id.image);
				probar = (ProgressBar) imageLayout
						.findViewById(R.id.progressbar);
				if (productinfo.photobitmap != null) {
					probar.setVisibility(View.GONE);
					imageView.setImageBitmap(productinfo.photobitmap);
				} else {
					imageView.setImageBitmap(bmp);
					probar.setVisibility(View.VISIBLE);
					needDownload = true;
				}

				mDownloadList.addView(imageLayout, productIndex + 1);
			}

			mDownloadList.invalidate();
			if (needDownload && mLoadImageTask == null) {
				mLoadImageTask = new LoadImageTask(mContext, websiteinfo);
				mLoadImageTask.execute();
			}
		}

		private void getAndSavePhotoBitmap(WebsiteInfo websiteInfo) {
			int productCount = websiteInfo.getProductsCount();
			for (int j = 0; j < productCount; j++) {
				ClassificationWebSiteInfo.ProductInfo productinfo = (ClassificationWebSiteInfo.ProductInfo) websiteInfo
						.getProductInfo(j);
				if (productinfo.photobitmap == null) {

					// query the photo from database firstly
					productinfo.photobitmap = Utils.getPhotoFromDatabase(
							mContext, productinfo);
					if (productinfo.photobitmap != null) {
						Log.d("zheng", "getPhotoFromDatabase:"
								+ productinfo.photoaddress);
					}

					// if database doesn't store this photo, get it from network
					if (productinfo.photobitmap == null) {
						try {
							productinfo.photobitmap = Utils
									.getBitMapFromNetwork(
											productinfo.photoaddress, mContext);
							if (productinfo.photobitmap != null) {
								Log.d("zheng", "getBitMapFromNetwork:"
										+ productinfo.photoaddress);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (productinfo.photobitmap != null) {
							// restore this photo into database
							// Utils.savePhotoIntoDatabase(mResolver,
							// productinfo);

							productinfo.photobitmap = Utils.getResizedBitmap(
									productinfo.photobitmap, mImageWidth,
									mImageHeigth);
							Log.d("zheng", "the bitmap with:"
									+ productinfo.photobitmap.getWidth());
							Log.d("zheng", "the bitmap height:"
									+ productinfo.photobitmap.getHeight());
						}
					}
				}
			}
		}
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.festival:

			break;
		case R.id.title:
			Intent intent = new Intent(this, HomeDownloadTopActivity.class);
			intent.putExtra("classid", classid);
			intent.putExtra("optype", WebsiteParser.SINGLE);
			this.startActivity(intent);
			break;
		default:
			break;
		}
		super.onClick(view);
	}
}