package com.androids.photoalbum.tab.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.BASE64Encoder;
import com.androids.photoalbum.utils.Utils;
import com.androids.photoalbum.view.MainTabActivity;
import com.androids.photoalbum.view.SelectPictureActivity;

public class MultiMediaMessageActivity extends BaseActivity {
	private Button mCameraButton;
	private Button mSendButton;
	private final static int PICURE_REQUEST_CODE = 200;
	public final static int RESULT_CODE_OK = 100;
	// private Gallery mPicutureList;
	// private PictureGalleryAdapter mAdapter;
	public static AutoCompleteTextView mReceiver;
	private EditText mSubject;
	private EditText mTextContent;
	public final static String ACTION_PICTURE_SELECTED = "picture_selected";
	private TextView mAddContact;
	private BroadcastReceiver mBroadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_PICTURE_SELECTED.equals(intent.getAction())) {
				Bundle extras = intent.getExtras();
				// if (extras != null) {
				Bitmap photo = TabActivityGroup.bmp;// extras.getParcelable("data");

				// int height = mPicutureList.getHeight();
				mPhotoArrayList.clear();
				int height = mGridView.getHeight();
				height = (int) getResources().getDimension(
						R.dimen.multi_msg_height);
				photo = Utils.getResizedBitmap(photo, cWidth, cHeight);
				FrameLayout image = (FrameLayout) mInflater.inflate(
						R.layout.multi_gridview_item, null);
				ImageView imageView = (ImageView) image
						.findViewById(R.id.image);
				ImageView delicon = (ImageView) image.findViewById(R.id.mText);
				delicon.setVisibility(View.VISIBLE);
				registerForContextMenu(delicon);
				delicon.setTag((Integer) mPhotoArrayList.size());
				delicon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						view.performLongClick();
						pos = (Integer) view.getTag();
						// MultiMediaMessageActivity.this.view =
						// view.getRootView();
					}
				});

				imageView.setImageBitmap(photo);
				// mPicutureList.addView(image);
				// mPicutureList.addView(image);
				mPhotoArrayList.add(image);

				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
				byte[] imgbytes = os.toByteArray();
				String content = new BASE64Encoder().encode(imgbytes);
				mBase64Pictures.add(content);

				findView();
				setValue();
				setListener();
				// }
			}
		}
	};

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.multimedia_message_layout);
		// LinearLayout functionbar =
		// (LinearLayout)findViewById(R.id.function_bar);
		// functionbar.setVisibility(View.GONE);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_GONE, null,
				null, null);
		mCameraButton = (Button) findViewById(R.id.camera);
		mCameraButton.setOnClickListener(this);
		mSendButton = (Button) findViewById(R.id.send);
		mSendButton.setOnClickListener(this);
		// mPicutureList = (Gallery)findViewById(R.id.picture_list);
		mImageWidth = (int) mResources.getDimension(R.dimen.mms_image_with);
		mImageHeigth = (int) mResources.getDimension(R.dimen.mms_image_height);
		mCameraButton.setOnTouchListener(this);
		mSendButton.setOnTouchListener(this);
		// mAdapter = new PictureGalleryAdapter();
		// mPicutureList.setAdapter(mAdapter);

		mReceiver = (AutoCompleteTextView) findViewById(R.id.receiver);
		mSubject = (EditText) findViewById(R.id.subject);
		mTextContent = (EditText) findViewById(R.id.content);

		mAddContact = (TextView) findViewById(R.id.add_contact);
		mAddContact.setOnClickListener(new ResultDisplayer("Selected contact",
				ContactsContract.Contacts.CONTENT_ITEM_TYPE));
		// mAddContact.setOnTouchListener(this);

		IntentFilter filter = new IntentFilter(ACTION_PICTURE_SELECTED);
		registerReceiver(mBroadReceiver, filter);

		findView();
		setValue();
		setListener();

		// mSubject.setOnTouchListener(new OnTouchListener() {
		//
		// public boolean onTouch(View v, MotionEvent event) {
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// mSubject.setBackgroundDrawable(null);
		// }
		//
		// return true;
		// }
		// });
	}

	Toast mToast;

	protected static void contactActivityResult(int requestCode,
			int resultCode, Intent data, AutoCompleteTextView receiver,
			ContentResolver resolver) {
		if (data != null) {
			Uri uri = data.getData();
			Log.d("zheng", "uri:" + uri);
			if (uri != null) {
				Cursor cursor = null;
				try {
					cursor = resolver.query(uri, null, null, null, null);
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

								if (receiver.getEditableText().toString() == null
										|| receiver.getEditableText()
												.toString().length() == 0) {
									receiver.setText(phoneNumber);
								} else {
									receiver.setText(receiver.getEditableText()
											.toString() + "," + phoneNumber);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadReceiver);
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
	}

	@Override
	public void onClick(View view) {
		Intent intent = null;
		switch (view.getId()) {
		case R.id.camera:
			// intent = new Intent(this, SelectPictureActivity.class);
			// startActivityForResult(intent, PICURE_REQUEST_CODE);

			intent = new Intent(this, SelectPictureActivity.class);
			MainTabActivity.getCurrentActivityGroup().setNextActivity(intent,
					"SelectPictureActivity");
			return;
		case R.id.send:
			// if (StaticInfo.mPassword == null || StaticInfo.mPassword.length()
			// == 0) {
			// intent = new Intent(this, LoginActivity.class);
			// intent.putExtra("isactivity", true);
			// startActivityForResult(intent, LoginActivity.REQUEST_LOGIN_CODE);
			// return;
			// }

			// String text = mReceiver.getEditableText().toString();
			// if (text == null || text.length() == 0) {
			// notifyToUser("请输入收件人信息!");
			// return;
			// }

			// if (mPhotoArrayList.size() <= 0) {
			// return;
			// }

			// MainTabActivity.getCurrentActivityGroup().showProgessBarDialog("Loading");
			// showProgessBarDialog("Loading...");
			Runnable run = new Runnable() {

				public void run() {
					// postProductDown();
					sendBySlideShow();
					MainTabActivity.getCurrentActivityGroup()
							.dismissProgressBarDialog();
				}
			};

			new Thread(run).start();
			return;
			// case R.id.add_contact:
			// addContact();
			// break;
		default:
			super.onClick(view);
			;
		}
	}

	private void addContact() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setData(Contacts.People.CONTENT_URI);
		startActivity(intent);
	}

	private View view;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CODE_OK) {
			if (requestCode == 10) {
				MultiMediaMessageActivity.contactActivityResult(requestCode,
						resultCode, data, MultiMediaMessageActivity.mReceiver,
						mResolver);
				return;
			}
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
				byte[] imgbytes = os.toByteArray();
				String content = new BASE64Encoder().encode(imgbytes);
				mBase64Pictures.add(content);

				// int height = mPicutureList.getHeight();
				// int height = mGridView.getHeight();
				int height = (int) getResources().getDimension(
						R.dimen.multi_msg_height);
				photo = Utils.getResizedBitmap(photo, cWidth, cHeight);
				FrameLayout image = (FrameLayout) mInflater.inflate(
						R.layout.gridview_item, null);
				ImageView imageView = (ImageView) image
						.findViewById(R.id.image);
				ImageView delicon = (ImageView) image.findViewById(R.id.mText);
				delicon.setVisibility(View.VISIBLE);
				registerForContextMenu(delicon);
				delicon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						view.performLongClick();
						MultiMediaMessageActivity.this.view = view;
					}
				});

				imageView.setImageBitmap(photo);
				// mPicutureList.addView(image);
				// mPicutureList.addView(image);
				mPhotoArrayList.add(image);
				mAdapter.notifyDataSetChanged();

				findView();
				setValue();
				setListener();
			}
		}
	}

	private ArrayList mBase64Pictures = new ArrayList();
	private ArrayList<FrameLayout> mPhotoArrayList = new ArrayList<FrameLayout>();

	private class PictureGalleryAdapter extends BaseAdapter {

		public int getCount() {
			return mPhotoArrayList.size();
		}

		public Object getItem(int position) {
			return mPhotoArrayList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return (View) mPhotoArrayList.get(position);
		}

	}

	private void postProductDown() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 5000);
		HttpPost httppost = new HttpPost(
				"http://www.uuunm.com/ProductDownNew.jsp");

		try {
			Log.d("zheng", getProductDownAPIXMLString());
			StringEntity se = new StringEntity(getProductDownAPIXMLString(),
					HTTP.UTF_8);
			se.setContentType("text/xml");
			httppost.setEntity(se);

			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity resEntity = httpresponse.getEntity();
			String result = EntityUtils.toString(resEntity);
			if (result != null && result.contains("成功")) {
				notifyToUser("发送成功");
			} else {
				notifyToUser("发生未知错误, 发送失败.");
			}
			// notifyToUser(result);
			Log.d("zheng", "Status OK: \n" + result);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			notifyToUser(e.getMessage());
			// tv.setText("Status NOT OK: \n" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			notifyToUser(e.getMessage());
			// tv.setText("Status NOT OK: \n" + e.getMessage());
		}
	}

	public String getProductDownAPIXMLString() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();

		Element root = doc.createElement("MMessage");
		doc.appendChild(root); // 将根元素添加到文档上

		Element consignid = doc.createElement("consignid");
		// consignid.setAttribute("attr", "kehuduan");
		consignid.setAttribute("attr", StaticInfo.mUsername);
		root.appendChild(consignid);// 添加属性

		Element password = doc.createElement("password");
		password.setAttribute("attr", StaticInfo.mPassword);
		root.appendChild(password);// 添加属性

		Element desttermid = null;
		String[] numbers = getReceivers();
		int size = numbers.length;
		for (int i = 0; i < size; i++) {
			desttermid = doc.createElement("desttermid");
			desttermid.setAttribute("attr", numbers[i]);
			root.appendChild(desttermid);// 添加属性
		}

		long timestamp = System.currentTimeMillis();
		Element linkid = doc.createElement("linkid");
		linkid.setAttribute("attr", String.valueOf(timestamp));
		root.appendChild(linkid);// 添加属性

		Element subject = doc.createElement("subject");
		String usersubject = mSubject.getText().toString();
		usersubject = usersubject == null ? "" : usersubject;
		usersubject = new BASE64Encoder().encode(usersubject.getBytes());
		subject.setAttribute("attr", usersubject);
		root.appendChild(subject);// 添加属性

		// Element musicid = doc.createElement("musicid");
		// musicid.setAttribute("attr", MessageActivity.this.musicid);
		// root.appendChild(musicid);// 添加属性
		Element textroot = doc.createElement("MMSContent");
		root.appendChild(textroot); // 将根元素添加到文档上

		Element contentType = doc.createElement("contentType");
		contentType.setAttribute("attr", "text/plain");
		textroot.appendChild(contentType);// 添加属性

		Element textcontent = doc.createElement("content");
		String usercontent = mTextContent.getText().toString();
		usercontent = usercontent == null ? "" : usercontent;
		usercontent = new BASE64Encoder().encode(usercontent.getBytes());
		Text content = doc.createTextNode(usercontent);
		textcontent.appendChild(content);
		textroot.appendChild(textcontent);// 添加属性

		int count = mBase64Pictures.size();
		for (int i = 0; i < count; i++) {
			Element imageroot = doc.createElement("MMSContent");
			root.appendChild(imageroot); // 将根元素添加到文档上

			contentType = doc.createElement("contentType");
			contentType.setAttribute("attr", "image/jpeg");
			imageroot.appendChild(contentType);

			Element imagecontent = doc.createElement("content");
			content = doc.createTextNode((String) mBase64Pictures.get(i));
			imagecontent.appendChild(content);
			imageroot.appendChild(imagecontent);
		}

		return toStringFromDoc(doc);
	}

	private String[] getReceivers() {
		String phonenumbers = mReceiver.getEditableText().toString();
		String[] numbers = phonenumbers.split(",");
		return numbers;
	}

	/*
	 * 把dom文件转换为xml字符串
	 */
	public static String toStringFromDoc(Document document) {
		String result = null;

		if (document != null) {
			StringWriter strWtr = new StringWriter();
			StreamResult strResult = new StreamResult(strWtr);
			TransformerFactory tfac = TransformerFactory.newInstance();
			try {
				javax.xml.transform.Transformer t = tfac.newTransformer();
				t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty(OutputKeys.METHOD, "xml"); // xml, html,
																// text
				t.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "4");
				t.transform(new DOMSource(document.getDocumentElement()),
						strResult);
			} catch (Exception e) {
				System.err.println("XML.toString(Document): " + e);
			}
			result = strResult.getWriter().toString();
			try {
				strWtr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	static class ViewHolder {
		ImageView mImg;
		ImageView mTxt;
	}

	private GridView mGridView;
	private HorizontalScrollView mScrollView;
	private int cWidth = 120;
	public int cHeight = 120;
	private int hSpacing = 5;

	private void findView() {
		mGridView = (GridView) findViewById(R.id.mGridView);
		mScrollView = (HorizontalScrollView) findViewById(R.id.mScrollView);
		mScrollView.setHorizontalScrollBarEnabled(false);// ���ع���

		cWidth = (int) getResources().getDimension(R.dimen.multi_msg_with);
		cHeight = (int) getResources().getDimension(R.dimen.multi_msg_height);
	}

	MAdapter mAdapter;

	private void setValue() {
		// cWidth = (int)getResources().getDimension(R.dimen.multi_msg_with);
		mAdapter = new MAdapter(this);
		mGridView.setAdapter(mAdapter);
		LayoutParams params = new LayoutParams(mGridView.getCount()
				* (cWidth + hSpacing), LayoutParams.WRAP_CONTENT);
		mGridView.setLayoutParams(params);
		mGridView.setColumnWidth(cWidth);
		mGridView.setHorizontalSpacing(hSpacing);
		mGridView.setStretchMode(GridView.NO_STRETCH);
		mGridView.setNumColumns(mAdapter.getCount());
		// registerForContextMenu(mGridView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("删除图片？");
		getMenuInflater().inflate(R.menu.delete_pic_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.cancel) {
			return true;
		}

		mPhotoArrayList.remove(pos);
		findView();
		setValue();
		setListener();
		return true;
	}

	int pos = 0;

	private void setListener() {
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				view.performLongClick();
				pos = position;
			}
		});
	}

	class MAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater mInflater;

		public MAdapter(Context c) {
			mContext = c;
			mInflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			Log.d("zheng", "getCount:" + mPhotoArrayList.size());
			return mPhotoArrayList.size();
		}

		public Object getItem(int position) {
			return mPhotoArrayList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View contentView, ViewGroup arg2) {
			return (View) mPhotoArrayList.get(position);
			// return (FrameLayout)mInflater.inflate(R.layout.gridview_item,
			// null);
		}

		// public View getView(int position, View arg1, ViewGroup arg2) {
		// LinearLayout view = (LinearLayout)mPhotoArrayList.get(position);
		// return view;
		// };
	}

	private String saveImage(Bitmap bitmap, int index) {
		File sdCard = null;
		if (bitmap == null) {
			return null;
		}

		try {
			String filename = "/sdcard/";
			filename = filename + index + ".jpg";
			sdCard = new File(filename);
			if (sdCard.exists()) {
				sdCard.delete();
			}

			FileOutputStream outStreamz = new FileOutputStream(sdCard);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStreamz);
			outStreamz.close();
			return filename;
		} catch (Exception e) {
			sdCard = null;
			if (bitmap == null) {
				return null;
			}

			try {
				String filename = "/mnt/sdcard/";
				filename = filename + index + ".jpg";
				sdCard = new File(filename);
				if (sdCard.exists()) {
					sdCard.delete();
				}

				FileOutputStream outStreamz = new FileOutputStream(sdCard);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStreamz);
				outStreamz.close();
				return filename;
			} catch (Exception e1) {
				notifyToUser("保存失败，请检查SD卡是否可用!");
				e.printStackTrace();
			}
			e.printStackTrace();
		}

		return null;
	}

	private void sendSlideShow() {
		int i = 0;
		String imagePath = null;
		Uri uri = null;
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (FrameLayout image : mPhotoArrayList) {
			i++;
			ImageView imageView = (ImageView) image.findViewById(R.id.image);
			Drawable d = imageView.getDrawable();
			Bitmap photo = ((BitmapDrawable) d).getBitmap();
			if (photo == null) {
				continue;
			}

			Log.d("zheng", "sendBySlideShow");
			imagePath = saveImage(photo, i);
			if (imagePath == null || imagePath.length() == 0) {
				Log.d("zheng", "imagePath null");
				continue;
			}
			Log.d("zheng", "imagePath:" + imagePath);
			uri = Uri.parse("file://" + imagePath);
			uris.add(uri);
		}

		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_STREAM, uris);

		String content = mSubject.getEditableText().toString();
		if (content != null && content.length() != 0) {
			content += "\n";
		}
		content += mTextContent.getEditableText().toString();
		if (content != null && content.length() != 0) {
			intent.putExtra(Intent.EXTRA_TEXT, content);
		}

		intent.setType("image/*");
		// intent.setClassName("com.sonyericsson.conversations",
		// "com.sonyericsson.conversations.ui.ConversationActivity");
		startActivity(intent);
		if (pd != null) {
			pd.dismiss();
		}

	}

	private void sendBySlideShow_old() {
		int i = 0;
		String imagePath = null;
		Uri uri = null;
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (FrameLayout image : mPhotoArrayList) {
			i++;
			ImageView imageView = (ImageView) image.findViewById(R.id.image);
			Drawable d = imageView.getDrawable();
			Bitmap photo = ((BitmapDrawable) d).getBitmap();
			if (photo == null) {
				continue;
			}

			Log.d("zheng", "sendBySlideShow");
			imagePath = saveImage(photo, i);
			if (imagePath == null || imagePath.length() == 0) {
				Log.d("zheng", "imagePath null");
				continue;
			}
			Log.d("zheng", "imagePath:" + imagePath);
			uri = Uri.parse("file://" + imagePath);
			uris.add(uri);
			break;
		}

		Intent intent = null;
		if (uris.size() > 1) {
			intent = new Intent(Intent.ACTION_SEND_MULTIPLE,
					Uri.parse("mmsto:"));
		} else {
			intent = new Intent(Intent.ACTION_SEND, Uri.parse("mmsto:"));
		}
		// intent.setClassName("com.android.mms",
		// "com.android.mms.ui.ComposeMessageActivity");
		String content = mSubject.getEditableText().toString();
		if (content != null && content.length() != 0) {
			content += "\n";
		}
		content += mTextContent.getEditableText().toString();
		if (content != null && content.length() != 0) {
			intent.putExtra(Intent.EXTRA_TEXT, content);
			// intent.putExtra("sms_body", content);
		} else {
			// intent.putExtra(Intent.EXTRA_TEXT, "");
		}

		if (uris.size() > 1) {
			intent.putExtra(Intent.EXTRA_STREAM, uris);
		} else if (uris.size() > 0) {
			intent.putExtra(Intent.EXTRA_STREAM, uri);
		}
		intent.setType("image/*");
		startActivity(intent);
		// if (pd != null) {
		// pd.dismiss();
		// }
	}

	private void sendBySlideShow() {
		int i = 0;
		String imagePath = null;
		Uri uri = null;
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (FrameLayout image : mPhotoArrayList) {
			i++;
			ImageView imageView = (ImageView) image.findViewById(R.id.image);
			Drawable d = imageView.getDrawable();
			Bitmap photo = ((BitmapDrawable) d).getBitmap();
			if (photo == null) {
				continue;
			}

			Log.d("zheng", "sendBySlideShow");
			imagePath = saveImage(photo, i);
			if (imagePath == null || imagePath.length() == 0) {
				Log.d("zheng", "imagePath null");
				continue;
			}
			Log.d("zheng", "imagePath:" + imagePath);
			uri = Uri.parse("file://" + imagePath);
			uris.add(uri);
			break;
		}

		try {
			combineMMS(uri);
		} catch (Exception e) {
			sendBySlideShow_old();
		}
		// if (pd != null) {
		// pd.dismiss();
		// }
	}

	// htc
	private void combineMMS(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setClassName("com.android.mms",
				"com.android.mms.ui.ComposeMessageActivity");
		intent.putExtra(Intent.EXTRA_STREAM, uri);

		String body = mTextContent.getEditableText().toString();
		if (body != null) {
			intent.putExtra("sms_body", body);
		}

		String subject = mSubject.getEditableText().toString();
		if (subject != null) {
			intent.putExtra("subject", subject);
		}
		intent.setType("image/jpeg");
		startActivity(intent);
	}
}