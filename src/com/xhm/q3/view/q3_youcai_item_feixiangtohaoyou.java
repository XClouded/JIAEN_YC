package com.xhm.q3.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.xhm.q3.GetVideo_info.AsynImageLoader;

public class q3_youcai_item_feixiangtohaoyou extends Activity implements
		OnClickListener {
	private ImageView mImageView, mBendi, mFeixiang, mBack;
	private TextView mName, mShow, mKeep, mShare, mTime, mSize;
	private Button mShare_button, mExit_button;
	private EditText mContact;
	private q3_Video_Info mInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_feixiangtohaoyou);
		mInfo = (q3_Video_Info) getIntent().getSerializableExtra("video_info");
		initView();
	}

	private void initView() {
		mContact = (EditText) findViewById(R.id.q3_youcai_feixiangtohaoyou_phone);
		mImageView = (ImageView) findViewById(R.id.q3_youcai_feixiangtohaoyou_item_pic);
		AsynImageLoader loader = new AsynImageLoader(this);
		loader.showImageAsyn(mImageView, mInfo.getmPic_Path(), R.drawable.icon);
		mBendi = (ImageView) findViewById(R.id.q3_youcai_feixiangtohaoyou_phone_bendi);
		mBendi.setOnClickListener(this);
		mFeixiang = (ImageView) findViewById(R.id.q3_youcai_feixiangtohaoyou_phone_feixiang);
		mFeixiang.setOnClickListener(this);
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mName = (TextView) findViewById(R.id.q3_youcai_feixiangtohaoyou_item_name);
		mName.setText(mInfo.getmName());
		mShow = (TextView) findViewById(R.id.q3_youcai_feixiangtohaoyou_item_dianji);
		mShow.setText(Html.fromHtml("播放：<font color=red>" + mInfo.getmShow()
				+ "</font> 次"));
		mKeep = (TextView) findViewById(R.id.q3_youcai_feixiangtohaoyou_item_keep);
		mKeep.setText(Html.fromHtml("收藏：<font color=red>" + mInfo.getmKeep()
				+ "</font> 次"));
		mShare = (TextView) findViewById(R.id.q3_youcai_feixiangtohaoyou_item_show);
		mShare.setText(Html.fromHtml("分享：<font color=red>" + mInfo.getmShare()
				+ "</font> 次"));
		mTime = (TextView) findViewById(R.id.q3_youcai_feixiangtohaoyou_item_time);
		mTime.setText(Html.fromHtml("时长：<font color=red>" + mInfo.getmTime()
				+ "</font> 分"));
		mSize = (TextView) findViewById(R.id.q3_youcai_feixiangtohaoyou_item_size);
		mSize.setText(Html.fromHtml("大小：<font color=red>" + mInfo.getmSize()
				+ "</font> M"));
		mShare_button = (Button) findViewById(R.id.q3_youcai_feixiangtohaoyou_share);
		mShare_button.setOnClickListener(this);
		mExit_button = (Button) findViewById(R.id.q3_youcai_feixiangtohaoyou_exit);
		mExit_button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcai_feixiangtohaoyou_phone_bendi:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
			startActivityForResult(intent, 136);
			break;
		case R.id.q3_youcai_feixiangtohaoyou_phone_feixiang:
			Intent Intent = new Intent(this,
					q3_youcai_feixiangtohaoyou_add_feixiang.class);
			startActivity(Intent);
			break;
		case R.id.q3_youcai_feixiangtohaoyou_exit:
			mContact.setText("");
			break;
		case R.id.q3_youcai_feixiangtohaoyou_share:
			System.out.println("phone nul==" + mContact.getText().toString());
			intent = new Intent(android.content.Intent.ACTION_SEND, Uri.parse("smsto:" +null));
			intent.putExtra("address", mContact.getText().toString());
			intent.putExtra("sms_body", mInfo.getmDescrib() + mInfo.getmPath());
			intent.setType("image/*");
			startActivity(intent);
			break;
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 136) {
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

									if (mContact.getEditableText().toString() == null
											|| mContact.getEditableText()
													.toString().length() == 0) {
										mContact.setText(phoneNumber);
									} else {
										mContact.setText(mContact
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
