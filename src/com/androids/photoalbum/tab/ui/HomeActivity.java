package com.androids.photoalbum.tab.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.view.MainTabActivity;

public class HomeActivity extends BaseActivity {
	public final static int LEVEL_1 = 1;
	public final static int LEVEL_2 = 2;
	private TextView mFestival;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.home_main_layout);
		titleBar = (RelativeLayout) findViewById(R.layout.titlebar);
		// 节目专区
		mFestival = (TextView) findViewById(R.id.festival);
		mFestival.setOnClickListener(this);
		mGridView = (GridView) findViewById(R.id.list_view);
		mGridView.setNumColumns(2);
		Intent intent = getIntent();
		if (intent != null) {
			String classid = intent.getStringExtra("classid");
			String optype = intent.getStringExtra("optype");
			boolean showTitleBar = intent.getBooleanExtra("show_titlebar",
					false);
			if (showTitleBar) {
				setTitleBar(
						com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL,
						"", "", "返回");
			} else {
				setTitleBar(
						com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL,
						"", "", "");
			}

			String id = intent.getStringExtra("id");
			// 加载图片
			if (classid != null && optype != null) {
				new ClassficationParserTask().execute(optype, classid, null);
				return;
			}
		}
		// 加载所有图片
		new ClassficationParserTask().execute(WebsiteParser.ALL, null, null);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("zheng", "HomeActivity onResume");
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		switch (paramInt) {
		case BaseActivity.RIGHT_BUTTON:
			String info = (String) ly.rightButton.getText();
			if ("返回".equals(info)) {
				MainTabActivity.mHomeActivityGroup.setPreviousActivity();
			} else if ("刷新".equals(info)) {
				new ClassficationParserTask().execute(WebsiteParser.ALL, null,
						null);
			}
			break;
		case BaseActivity.MID_BUTTON:

			break;
		default:
			break;
		}
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data != null) {
			Uri uri = data.getData();
			Log.d("zheng", "uri:" + uri);
			if (uri != null) {
				Cursor cursor = null;
				try {
					cursor = mResolver.query(uri, null, null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						String contactId = cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts._ID));
						String hasPhone = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

						if (hasPhone.compareTo("1") == 0) {
							Cursor phones = MainTabActivity
									.getCurrentActivityGroup()
									.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = " + contactId, null,
											null);
							while (phones.moveToNext()) {
								String phoneNumber = phones
										.getString(phones
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								String phoneTpye = phones
										.getString(phones
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
								sendEmail();
								Log.d("zheng", "testNum=" + phoneNumber
										+ "type:" + phoneTpye);
							}
							phones.close();
						}
					}
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}
			}
		}
	}

	private void sendEmail() {
		// Intent intent = new Intent(Intent.ACTION_SEND);
		// intent.setType("text/plain");
		// intent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String body = "更多精彩，更多快乐尽在优彩，'加链接'";
		Uri data = Uri.parse("mailto:?subject=" + "分享微博" + "&body=" + body);
		intent.setData(data);
		startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.festival:

			break;

		default:
			break;
		}
		super.onClick(view);
	}

	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {

			public void run() {
				if (pd == null) {
					pd = new ProgressDialog(MainTabActivity.mHomeActivityGroup);
				}
				pd.setMessage(" 加载中 ");
				pd.show();
			}
		};

		runOnUiThread(action);
	}
}