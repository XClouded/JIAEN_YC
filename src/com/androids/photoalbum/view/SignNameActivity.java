package com.androids.photoalbum.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.parser.SignNameWebsiteParser;
import com.androids.photoalbum.parser.WordsAddWebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.TabActivityGroup;
import com.androids.photoalbum.utils.ListViewWebsiteParserTask;
import com.androids.photoalbum.utils.NetWorkUtils;

public class SignNameActivity extends BaseActivity implements
		OnItemClickListener {
	private TextView mUpLoad;
	private AutoCompleteTextView mAutoText;
	// private ListView mListView;
	// private AlbumAdapter mAdapter;
	public static String mPhotoaddress;
	public static Bitmap mBitmap;

	private Button mReviewButton;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("show_save_dialog".equals(intent.getAction())) {
				byte[] bytes = intent.getByteArrayExtra("photo_bitmap");
				mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						null);
				mPhotoaddress = intent.getStringExtra("photo_address");
				MainTabActivity.getCurrentActivityGroup().showDialog(
						DIALOG_SAVE_IMAGE);
			}
		}
	};

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.sign_name_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, null,
				null, "返回");
		mUpLoad = (TextView) findViewById(R.id.search_button);
		mUpLoad.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.list_view);
		// mListView.setOnItemClickListener(this);
		// registerForContextMenu(mListView);
		mAutoText = (AutoCompleteTextView) findViewById(R.id.search_field);
		mAutoText.setOnTouchListener(this);
		mLineCount = 3;
		mImageWidth = (int) mResources
				.getDimension(R.dimen.signname_image_with);
		mImageHeigth = (int) mResources
				.getDimension(R.dimen.signname_image_height);
		// IntentFilter filter = new
		// IntentFilter(PictureWorkingActivity.ACTION_NOTIFY_DATA_SET_CHANGED);
		// registerReceiver(mReceiver, filter);
		String text = "伤不起";
		try {
			text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		IntentFilter filter = new IntentFilter("show_save_dialog");
		registerReceiver(mReceiver, filter);
		// comment out because the new requirement
		// new ListViewWebsiteParserTask(SignNameActivity.this, new
		// WordsAddWebsiteParser(), mListView, mLineCount, mImageWidth,
		// mImageHeigth, false).execute(NetWorkUtils.GET_NAME + "?Name=" +
		// text);
		File sdPhoto = new File("/sdcard/优彩/");
		if (!sdPhoto.exists()) {
			sdPhoto.mkdirs();
		}

		mReviewButton = (Button) findViewById(R.id.review);
		mReviewButton.setOnTouchListener(this);
		mReviewButton.setOnClickListener(this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("保存艺术签名");
		getMenuInflater().inflate(R.menu.save_img, menu);
	}

	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// AdapterView.AdapterContextMenuInfo menuInfo =
	// (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	// final ImageLayout image =
	// (ImageLayout)mListView.getItemAtPosition(menuInfo.position);
	// String photoaddress = image.getPhotoAddress();
	// switch (item.getItemId()) {
	// case R.id.save:
	// saveImage(mBitmap, mPhotoaddress);
	// break;
	// case R.id.cancel:
	// break;
	// }
	//
	// return true;
	// }

	private File sdCard = null;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		// finish();
		MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
	}

	@Override
	public void onClick(View view) {
		String text;
		switch (view.getId()) {
		case R.id.search_button:
			text = mAutoText.getText().toString();
			if (text == null || text.length() == 0) {
				notifyToUser("上传签名不能为空!");
				return;
			}

			try {
				text = URLEncoder.encode(text, "UTF-8");
				Log.d("peter", "nickname: " + text);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}

			// new SignNameTask().execute(text);
			new ListViewWebsiteParserTask(SignNameActivity.this,
					new WordsAddWebsiteParser(), mListView, mLineCount,
					mImageWidth, mImageHeigth, false)
					.execute(NetWorkUtils.GET_NAME + "?Name=" + text);
			break;
		case R.id.review:
			text = mAutoText.getText().toString();
			if (text == null || text.length() == 0) {
				notifyToUser("您还未输入内容，无法查看效果图");
				return;
			}

			if (text.length() > 90) {
				notifyToUser("您输入的文字太多");
				return;
			}
			try {
				text = URLEncoder.encode(text, "UTF-8");
				Log.d("peter", "nickname: " + text);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
			Intent intent = new Intent(this, SignNameDownloadActivity.class);
			intent.putExtra("words", text);
			startActivity(intent);
			mAutoText.setText("");
			// MainTabActivity.getCurrentActivityGroup().setNextActivity(intent,
			// "SignNameDownloadActivity");
			break;
		default:
			break;
		}
		super.onClick(view);
	}

	class SignNameTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String name = params[0];
			String localInfo = "?Name=" + name;
			mWebsiteInfo = new SignNameWebsiteParser()
					.parseWebsiteInfo(NetWorkUtils.GET_NAME + localInfo);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (ServerInfo.FAIL.equals(mWebsiteInfo.result)) {
				notifyToUser("获取签名失败，请重试.");
				return;
			}
		}
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int arg2,
			long arg3) {
		// view.performLongClick();
	}
}