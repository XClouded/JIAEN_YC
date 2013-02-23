package com.androids.photoalbum.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.androids.photoalbum.utils.ListViewWebsiteParserTask;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.utils.Utils;

public class SignNameDownloadActivity extends BaseActivity implements
		OnItemClickListener {
	private TextView mUpLoad;
	private AutoCompleteTextView mAutoText;
	// private ListView mListView;
	// private AlbumAdapter mAdapter;
	public static String mPhotoaddress;
	public static Bitmap mBitmap;

	private Button mDownloadButton;
	private Button mReIButtonButton;
	private String text = "";
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
		setView(R.layout.sign_name_download_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, null,
				null, "返回");
		mUpLoad = (TextView) findViewById(R.id.search_button);
		mUpLoad.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.list_view);
		// mListView.setOnItemClickListener(this);
		// registerForContextMenu(mListView);
		mAutoText = (AutoCompleteTextView) findViewById(R.id.search_field);
		mAutoText.setOnTouchListener(this);
		mLineCount = 1;
		mImageWidth = (int) mResources
				.getDimension(R.dimen.signname_image_download_with);
		mImageHeigth = (int) mResources
				.getDimension(R.dimen.signname_image_download_height);
		// String text = "伤不起";
		// try {
		// text = URLEncoder.encode(text, "UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// return;
		// }

		IntentFilter filter = new IntentFilter("show_save_dialog");
		registerReceiver(mReceiver, filter);

		downloadSignName(getIntent());
		File sdPhoto = new File("/sdcard/优彩/");
		if (!sdPhoto.exists()) {
			sdPhoto.mkdirs();
		}

		mDownloadButton = (Button) findViewById(R.id.download);
		mDownloadButton.setOnTouchListener(this);
		mDownloadButton.setOnClickListener(this);

		mReIButtonButton = (Button) findViewById(R.id.reinput);
		mReIButtonButton.setOnTouchListener(this);
		mReIButtonButton.setOnClickListener(this);
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

	private void downloadSignName(Intent intent) {
		Boolean started = false;
		if (intent != null) {
			String text = intent.getStringExtra("words");
			if (this.text != null && this.text.equals(text)) {
				return;
			}
			if (text != null && text.length() > 0) {
				started = true;
				new ListViewWebsiteParserTask(SignNameDownloadActivity.this,
						new WordsAddWebsiteParser(), mListView, mLineCount,
						mImageWidth, mImageHeigth, false)
						.execute(NetWorkUtils.GET_NAME + "?Name=" + text);
			}
		}

		if (!started) {
			finish();
		}
	}

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
	protected void onNewIntent(Intent intent) {
		// super.onNewIntent(intent);
		downloadSignName(intent);
		Log.d("zheng", "onNewIntent");
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
			new ListViewWebsiteParserTask(SignNameDownloadActivity.this,
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

			// new SignNameTask().execute(text);
			new ListViewWebsiteParserTask(SignNameDownloadActivity.this,
					new WordsAddWebsiteParser(), mListView, mLineCount,
					mImageWidth, mImageHeigth, false)
					.execute(NetWorkUtils.GET_NAME + "?Name=" + text);
			break;

		case R.id.download:
			imagePath = null;
			if (mListView.getCount() > 0) {
				ArrayList list = (ArrayList) mListView.getItemAtPosition(0);
				for (int i = 0; i < list.size(); i++) {
					String photoaddress = (String) list.get(i);
					String path = saveImage(SignNameDownloadActivity.this,
							photoaddress);
					if (imagePath == null) {
						imagePath = path;
					}

				}
			} else {
				Utils.notifyToUser("获取签名失败，请返回上页重新下载",
						SignNameDownloadActivity.this);
				return;
			}

			showProgessBarDialog("Loading...");
			Runnable run = new Runnable() {
				public void run() {
					sendBySlideShow();
				}
			};
			new Thread(run).start();
			break;
		case R.id.reinput:
			// MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
			finish();
			break;
		default:
			break;
		}
		super.onClick(view);
	}

	private String imagePath = null;

	private void sendBySlideShow() {
		Log.d("zheng", "sendBySlideShow");
		// String imagePath = saveImage(SignNameDownloadActivity.this,
		// photoaddress);
		if (imagePath == null || imagePath.length() == 0) {
			Log.d("zheng", "imagePath null");
			if (pd != null) {
				pd.dismiss();
			}
			return;
		}

		Log.d("zheng", "imagePath:" + imagePath);

		try {
			combineMMS(Uri.parse("file://" + imagePath));
		} catch (Exception e) {
			sendBySlideShow_old();
		}
		if (pd != null) {
			pd.dismiss();
		}
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

	// htc
	private void combineMMS(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setClassName("com.android.mms",
				"com.android.mms.ui.ComposeMessageActivity");
		intent.putExtra(Intent.EXTRA_STREAM, uri);

		// String body = mContent.getEditableText().toString();
		// if (body != null) {
		// intent.putExtra("sms_body", body);
		// }

		// String subject = mSubject.getEditableText().toString();
		// if (subject != null) {
		// intent.putExtra("subject", subject);
		// }
		intent.setType("image/jpeg");
		startActivity(intent);
	}

	private void sendBySlideShow_old() {
		Log.d("zheng", "sendBySlideShow");
		// String imagePath = saveImage(MessageActivity.this, photoaddress);
		if (imagePath == null || imagePath.length() == 0) {
			Log.d("zheng", "imagePath null");
			if (pd != null) {
				pd.dismiss();
			}
			return;
		}

		// if (audioPath == null) {
		// audioPath = saveAudio();
		// }

		Log.d("zheng", "imagePath:" + imagePath);
		ArrayList<Uri> uris = new ArrayList<Uri>();
		uris.add(Uri.parse("file://" + imagePath));

		// if (audioPath != null) {
		// Log.d("zheng", "audioPath:" + audioPath);
		// uris.add(Uri.parse("file://" + audioPath));
		// }
		Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mmsto:"));
		// String content = mSubject.getEditableText().toString();
		// if (content != null && content.length() != 0) {
		// content += "\n";
		// }
		// content += mContent.getEditableText().toString();
		// if (content != null && content.length() != 0) {
		// intent.putExtra(Intent.EXTRA_TEXT, content);
		// intent.putExtra("sms_body", content);
		// } else {
		// intent.putExtra(Intent.EXTRA_TEXT, "");
		// }

		if (uris.size() > 0) {
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file://" + imagePath));
		}

		intent.setType("image/*");
		// i.setType("*/*");
		// intent.setClassName("com.sonyericsson.conversations",
		// "com.sonyericsson.conversations.ui.ConversationActivity");
		startActivity(intent);
		if (pd != null) {
			pd.dismiss();
		}
	}
}