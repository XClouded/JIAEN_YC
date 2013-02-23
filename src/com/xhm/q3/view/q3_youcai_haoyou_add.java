package com.xhm.q3.view;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.androids.photoalbum.tab.ui.MultiMediaMessageActivity;
import com.androids.photoalbum.view.MainTabActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class q3_youcai_haoyou_add extends Activity implements OnClickListener {
	private Button mButton_add_no_0, mButton_add_yes_0, mButton_add_no_1,
			mButton_add_yes_1;
	private ImageView mImageView, mRentou, mChazhao;
	private AutoCompleteTextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_haoyou_add);
		initView();
	}

	private void initView() {
		mImageView = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mImageView.setOnClickListener(this);
		mRentou = (ImageView) findViewById(R.id.q3_youcai_haoyou_add_local_rentou);
		mRentou.setOnClickListener(this);
		mChazhao = (ImageView) findViewById(R.id.q3_youcai_haoyou_add_local_chazhao);
		mChazhao.setOnClickListener(this);
		mButton_add_no_0 = (Button) findViewById(R.id.q3_youcai_haoyou_add_no_0);
		mButton_add_no_0.setOnClickListener(this);
		mButton_add_no_1 = (Button) findViewById(R.id.q3_youcai_haoyou_add_no_1);
		mButton_add_no_1.setOnClickListener(this);
		mButton_add_yes_0 = (Button) findViewById(R.id.q3_youcai_haoyou_add_yes_0);
		mButton_add_yes_0.setOnClickListener(this);
		mButton_add_yes_1 = (Button) findViewById(R.id.q3_youcai_haoyou_add_yes_1);
		mButton_add_yes_1.setOnClickListener(this);
		mTextView = (AutoCompleteTextView) findViewById(R.id.q3_youcai_haoyou_add_local);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		case R.id.q3_youcai_haoyou_add_no_0:

			break;
		case R.id.q3_youcai_haoyou_add_no_1:

			break;
		case R.id.q3_youcai_haoyou_add_yes_0:

			break;
		case R.id.q3_youcai_haoyou_add_yes_1:

			break;
		case R.id.q3_youcai_haoyou_add_local_rentou:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
			startActivityForResult(intent, 138);
			break;
		case R.id.q3_youcai_haoyou_add_local_chazhao:

			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 138) {
			if (data != null) {
				Uri uri = data.getData();
				Log.d("zheng", "uri:" + uri);
				if (uri != null) {
					Cursor cursor = null;
					try {
						cursor = this.getContentResolver().query(uri, null,
								null, null, null);
						if (cursor != null && cursor.moveToFirst()) {
							String contactId = cursor
									.getString(cursor
											.getColumnIndex(ContactsContract.Contacts._ID));
							String hasPhone = cursor
									.getString(cursor
											.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

							if (hasPhone.compareTo("1") == 0) {
								Cursor phones = getContentResolver()
										.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
												null,
												ContactsContract.CommonDataKinds.Phone.CONTACT_ID
														+ " = " + contactId,
												null, null);
								while (phones.moveToNext()) {
									String phoneNumber = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									String phoneTpye = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

									if (mTextView.getEditableText().toString() == null
											|| mTextView.getEditableText()
													.toString().length() == 0) {
										mTextView.setText(phoneNumber);
									} else {
										mTextView.setText(mTextView
												.getEditableText().toString()
												+ "," + phoneNumber);
									}
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
	}
}
