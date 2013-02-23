package com.androids.photoalbum.tab.ui;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.MusicWebsiteInfo;
import com.androids.photoalbum.netinfo.MusicWebsiteInfo.MusicInfo;
import com.androids.photoalbum.parser.AddCollectionWebsiteParser;
import com.androids.photoalbum.provider.AlbumContent.Album;
import com.androids.photoalbum.provider.AlbumContent.AlbumColumns;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.BASE64Encoder;
import com.androids.photoalbum.utils.CommonWebsiteTask;
import com.androids.photoalbum.utils.FormFile;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.utils.Utils;
import com.androids.photoalbum.view.LoginActivity;
import com.androids.photoalbum.view.MainTabActivity;
import com.androids.photoalbum.view.PictureCombineActivity;
import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.cs.mms.MMSSender;
import com.googles.android.mms.pdu.CharacterSets;
import com.googles.android.mms.pdu.EncodedStringValue;
import com.googles.android.mms.pdu.PduBody;
import com.googles.android.mms.pdu.PduComposer;
import com.googles.android.mms.pdu.PduPart;
import com.googles.android.mms.pdu.SendReq;
import com.xhm.get_post.Http_get_post;

public class MessageActivity extends BaseActivity implements
		OnItemSelectedListener {
	// private Bitmap mBitmap;
	private String name, pwd, isejia, userid;
	public static SharedPreferences share;
	private GifView mGifView;
	private ImageView mImageView;
	private MediaPlayer mMediaPlayer = null;
	private Button mFavorite;
	private int mPosition = -1;
	private String musicid = "";
	private String productid = "";
	private String id = null;
	private Button mBack;
	private Button mShare;
	private Button myijiaShare;
	private Button mFinish;
	private EditText mSubject;
	private EditText mContent;
	public static AutoCompleteTextView mReceiver;
	private static Context mContext;
	public static MessageActivity mActivity;
	private TextView mAddContact;
	private boolean mIsCombinedImg;
	public static boolean collected = false;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.message_layout);
		share = getSharedPreferences("user_info", MODE_WORLD_READABLE);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_GONE, null,
				null, null);
		mGifView = (GifView) findViewById(R.id.image);
		mImageView = (ImageView) findViewById(R.id.imageview);
		mImageWidth = (int) mResources.getDimension(R.dimen.msg_gif_with);
		mImageHeigth = (int) mResources.getDimension(R.dimen.msg_gif_height);

		mMediaPlayer = new MediaPlayer();
		mFavorite = (Button) findViewById(R.id.favorite);
		mFavorite.setOnClickListener(this);
		mFavorite.setOnTouchListener(this);
		// Q3_分享改为了彩信发送
		mFinish = (Button) findViewById(R.id.share);
		mFinish.setOnTouchListener(this);
		mFinish.setOnClickListener(this);

		mBack = (Button) findViewById(R.id.back);
		mBack.setOnTouchListener(this);
		mBack.setOnClickListener(this);

		myijiaShare = (Button) findViewById(R.id.yijiashare);
		myijiaShare.setOnClickListener(this);
		myijiaShare.setOnTouchListener(this);

		mShare = (Button) findViewById(R.id.share);
		mShare.setOnTouchListener(this);
		mShare.setOnClickListener(this);

		mSpinner = (Spinner) findViewById(R.id.music);
		mSpinner.setOnItemSelectedListener(this);

		mSubject = (EditText) findViewById(R.id.subject);
		mContent = (EditText) findViewById(R.id.content);

		mReceiver = (AutoCompleteTextView) findViewById(R.id.receiver);

		getIntentInfo(getIntent());

		mContext = this;
		mActivity = this;
		isActivity = true;

		mAddContact = (TextView) findViewById(R.id.add_contact);
		mAddContact.setOnClickListener(new ResultDisplayer("Selected contact",
				ContactsContract.Contacts.CONTENT_ITEM_TYPE));

		// Intent intent = new
		// Intent("com.sonyericsson.contacts.PICK_CONVERSATIONS_TARGET");
		// Allow the contact picker to determine if MMS is disabled,
		// so that it can adjust the picker accordingly, i.e. hide email
		// addresses
		// intent.putExtra("mmsEnabled", true);
		// startActivityForResult(intent, 101);

		conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		info = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		currentAPN = info.getExtraInfo();
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		switch (paramInt) {
		case BaseActivity.LEFT_BUTTON:
			MainTabActivity.mHomeActivityGroup.setPreviousActivity();
			break;

		default:
			break;
		}
	}

	private boolean isInTabGroup = true;

	@Override
	protected void onNewIntent(Intent intent) {
		getIntentInfo(intent);
	}

	private void rearrangeShareButton() {
		LinearLayout parent = (LinearLayout) findViewById(R.id.func_button);
		LinearLayout favShare = (LinearLayout) findViewById(R.id.fav_share);
		LinearLayout backFini = (LinearLayout) findViewById(R.id.back_fini);

		parent.removeView(favShare);
		favShare.removeView(mShare);
		backFini.addView(mShare, 1);
		mShare.setBackgroundResource(R.drawable.color_msg_middle);
	}

	int mCurDrawFrame = 0;
	int mTotalFrames = 0;
	private Intent mIntent;
	String fromActivity = null;
	ArrayList<Bitmap> gifBitmaps = new ArrayList<Bitmap>();
	public static String photoaddress;

	private void getIntentInfo(Intent intent) {
		if (intent == null) {
			return;
		}
		mIntent = intent;
		isInTabGroup = intent.getBooleanExtra("in_tab_group", true);
		fromActivity = intent.getStringExtra("from_activity");

		productid = intent.getStringExtra("productid");
		mIsCombinedImg = intent.getBooleanExtra("is_combined_image", false);
		// notifyToUser("productid:" + productid);
		id = intent.getStringExtra("id");
		photoaddress = intent.getStringExtra("photoaddress");
		if (photoaddress == null) {
			return;
		}
		bitmap = Utils.getResizedBitmap(
				Utils.getBitMapFromNetwork(photoaddress, MessageActivity.this),
				mImageWidth, mImageHeigth);
		showProgessBarDialog("Loading...");
		if ("SignNameActivity".equals(fromActivity)
				|| "PictureReviewActivity".equals(fromActivity)) {
			Runnable run = new Runnable() {

				public void run() {
					bitmap = Utils.getResizedBitmap(Utils.getBitMapFromNetwork(
							photoaddress, MessageActivity.this), mImageWidth,
							mImageHeigth);

					Runnable action = new Runnable() {

						public void run() {
							System.out.println("bitmap_init=" + bitmap);
							mImageView.setImageBitmap(bitmap);
							if (pd != null) {
								pd.dismiss();
							}
						}
					};

					runOnUiThread(action);
				}
			};

			new Thread(run).start();
			mFavorite.setVisibility(View.INVISIBLE);
			rearrangeShareButton();
			mGifView.setVisibility(View.GONE);

			mImageView.setVisibility(View.VISIBLE);

		} else {
			mFavorite.setVisibility(View.VISIBLE);
			if ("PictureWorkingActivity".equals(fromActivity)) {
				mFavorite.setVisibility(View.INVISIBLE);
				rearrangeShareButton();
			}
			mGifView.setVisibility(View.VISIBLE);

			mImageView.setVisibility(View.GONE);

			getGifViewFromNetWork(photoaddress);
		}
	}

	private void getGifViewFromNetWork(final String photoaddress) {
		Runnable run = new Runnable() {

			public void run() {
				URL myFileUrl = null;
				try {
					myFileUrl = new URL(photoaddress);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				InputStream is = null;
				try {
					HttpURLConnection conn = (HttpURLConnection) myFileUrl
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					is = conn.getInputStream();
				} catch (Exception e) {
					e.printStackTrace();
				}

				mGifView.setShowDimension(mImageWidth, mImageHeigth);
				mGifView.setGifImageType(GifImageType.SYNC_DECODER);
				mGifView.setGifImage(is);
				Runnable action = new Runnable() {
					public void run() {
						mGifView.setVisibility(View.VISIBLE);
						if (pd != null) {
							pd.dismiss();
						}
					}
				};
				runOnUiThread(action);
			}
		};
		new Thread(run).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10) {
			MultiMediaMessageActivity.contactActivityResult(requestCode,
					resultCode, data, MessageActivity.mReceiver, mResolver);
			return;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.favorite:
			// Q3___注解
			// if (productid == null || productid.length() == 0) {
			// return;
			// }
			//
			// if (StaticInfo.mPassword == null
			// || StaticInfo.mPassword.length() == 0) {
			// Intent intent = new Intent(this, LoginActivity.class);
			// intent.putExtra("isactivity", true);
			// startActivity(intent);
			// return;
			// }
			// new CommonWebsiteTask(new AddCollectionWebsiteParser(), this)
			// .execute(NetWorkUtils.ADD_COLLECTION + "?Proid="
			// + productid + "&userid=" + StaticInfo.userid);
			finish();
			break;
		case R.id.back:
			// if (isInTabGroup) {
			// MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
			// } else {
			finish();
			// }
			// if (MainTabActivity.mCurrentPage == 0) {
			// MainTabActivity.mHomeActivityGroup.setPreviousActivity();
			// } else {
			// finish();
			// }
			break;
		case R.id.finish:
			name = share.getString("name", "-1");
			pwd = share.getString("pwd", "-1");
			isejia = share.getString("isejia", "-1");
			userid = share.getString("userid", "-1");
			System.out.println("name=" + name + "pwd=" + pwd);
			// 未登录，请先登录
			if (name == "-1" & pwd == "-1") {
				Http_get_post.Http_get(MessageActivity.this);
				// 已登录，发送分享图片
			} else {
				showProgessBarDialog("Loading...");
				Runnable run = new Runnable() {
					public void run() {
						// sendMms(MessageActivity.this);
						sendBySlideShow();
						// postProductDown();
					}
				};

				new Thread(run).start();
			}

			break;
		case R.id.share:
			// share();
			Intent intent = new Intent(Intent.ACTION_SEND);

			intent.putExtra("subject", mSubject.getText().toString());

			intent.putExtra("sms_body", mContent.getText().toString());

			intent.putExtra("address", mReceiver.getEditableText().toString());
			System.out.println("photoaddress=="
					+ saveImage(MessageActivity.this, photoaddress));
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file://"+saveImage(MessageActivity.this, photoaddress)));

			intent.setType("image/*");
			startActivity(intent);
			break;
		case R.id.yijiashare:
			name = share.getString("name", "-1");
			pwd = share.getString("pwd", "-1");
			isejia = share.getString("isejia", "-1");
			userid = share.getString("userid", "-1");
			System.out.println("name=" + name + "pwd=" + pwd);
			// 未登录，请先登录
			if (name == "-1" & pwd == "-1") {
				Http_get_post.Http_get(MessageActivity.this);
				// 已登录，发送分享图片
			} else {
				// 将图片转换为字节
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				System.out.println("bitmap=" + bitmap);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				final byte[] bitmap = baos.toByteArray();
				Http_get_post.Http_post(name, pwd, isejia, userid, bitmap,
						MessageActivity.this);
			}
		default:
			break;
		}
		super.onClick(view);
	}

	public static String imagePath = null;

	private void share() {
		showProgessBarDialog("Loading...");
		Runnable action = new Runnable() {

			public void run() {
				imagePath = saveImage(MessageActivity.this, photoaddress);
				if (imagePath == null) {
					return;
				}
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
				startActivity(Intent.createChooser(intent, "分享"));
				Runnable run = new Runnable() {

					public void run() {
						if (pd != null) {
							pd.dismiss();
						}
					}
				};

				runOnUiThread(run);
			}
		};

		new Thread(action).start();
	}

	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {

			public void run() {
				if (pd == null) {
					pd = new ProgressDialog(MessageActivity.this);
				}
				pd.setMessage(" 加载中... ");
				pd.show();
			}
		};

		runOnUiThread(action);
	}

	private String[] getReceivers() {
		String phonenumbers = mReceiver.getEditableText().toString();
		String[] numbers = phonenumbers.split(",");
		return numbers;
	}

	@Override
	protected void onDestroy() {
		if (mNChangeReceiver != null) {
			unregisterReceiver(mNChangeReceiver);
			mNChangeReceiver = null;
		}
		// mImageView.setGifImage((InputStream)null);
		super.onDestroy();
	}

	public MusicInfo selectItem(ArrayList musicList, int position) {
		MusicInfo musicInfo = (MusicInfo) musicList.get(position);
		if (mMediaPlayer.isPlaying()) {
			if (mPosition == position) {
				return musicInfo;
			}
			mMediaPlayer.pause();
		}

		mPosition = position;
		// MusicInfo musicInfo = (MusicInfo)parent.getItemAtPosition(position);
		musicid = musicInfo.id;
		final String amrurl = musicInfo.amrurl;
		Runnable run = new Runnable() {

			public void run() {
				mMediaPlayer.reset();
				try {
					mMediaPlayer.setDataSource(amrurl);
					mMediaPlayer.prepare();
					mMediaPlayer.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		new Thread(run).start();

		return musicInfo;
	}

	String amrurl;
	String audioPath = null;

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		audioPath = null;
		if (position == 0) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			}
			return;
		}
		if (mMediaPlayer.isPlaying()) {
			if (mPosition == position) {
				return;
			}
			mMediaPlayer.pause();
		}

		mPosition = position;
		Spinner spinner = (Spinner) parent;
		final ArrayList musicList = ((MusicWebsiteInfo) mWebsiteInfo).mMusicList;
		MusicInfo musicInfo = (MusicInfo) musicList.get(position - 1);
		// MusicInfo musicInfo = (MusicInfo)spinner.getSelectedItem();
		// MusicInfo musicInfo = (MusicInfo)spinner.getItemAtPosition(position);
		musicid = musicInfo.id;
		amrurl = musicInfo.amrurl;

		Runnable action = new Runnable() {

			public void run() {
				audioPath = saveAudio();
			}
		};
		new Thread(action).start();
		Log.d("zheng", "amrurl:" + amrurl);
		Runnable run = new Runnable() {

			public void run() {
				mMediaPlayer.reset();
				try {
					mMediaPlayer.setDataSource(amrurl);
					mMediaPlayer.prepare();
					mMediaPlayer.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		new Thread(run).start();
	}

	public Bitmap getPhotoFromDatabase(String productid) {
		Cursor cursor = null;
		String selection = AlbumColumns.PRODUCT_ID + "=?";

		try {
			cursor = mResolver.query(Album.ALBUM_CONTENT_URI,
					Album.CONTENT_PROJECTION, selection,
					new String[] { productid }, null);

			if (cursor != null && cursor.moveToFirst()) {
				byte[] data = cursor
						.getBlob(AlbumColumns.CONTENT_PHOTO_BITMAP_COLUMN);
				if (data != null && data.length != 0) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length, null);
					bitmap = Utils.getResizedBitmap(bitmap, mImageWidth,
							mImageHeigth);
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
		// usersubject = new BASE64Encoder().encode(usersubject.getBytes());
		subject.setAttribute("attr", usersubject);
		root.appendChild(subject);// 添加属性

		if (mIsCombinedImg) {
			Element MMSContent = doc.createElement("MMSContent");
			root.appendChild(MMSContent); // 将根元素添加到文档上

			Element contentType = doc.createElement("contentType");
			contentType.setAttribute("attr", "image/jpeg");
			MMSContent.appendChild(contentType);// 添加属性

			Element imagecontent = doc.createElement("content");
			Text content = null;
			if ("PictureReviewActivity".equals(fromActivity)) {
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				Bitmap bitmap = Utils.getBitMapFromNetwork(
						PictureCombineActivity.mCombinedAddress,
						MessageActivity.this);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
				bitmap.recycle();
				byte[] imgByte = os.toByteArray();
				content = doc.createTextNode(new BASE64Encoder()
						.encode(imgByte));
			} else {
				content = doc.createTextNode(new BASE64Encoder().encode(mIntent
						.getByteArrayExtra("photo_bitmap")));
			}
			imagecontent.appendChild(content);
			MMSContent.appendChild(imagecontent);
		} else {
			Element productid = doc.createElement("productid");
			productid.setAttribute("attr", MessageActivity.this.productid);
			root.appendChild(productid);// 添加属性
		}

		Element musicid = doc.createElement("musicid");
		musicid.setAttribute("attr", MessageActivity.this.musicid);
		root.appendChild(musicid);// 添加属性

		Element textcontent = doc.createElement("textcontent");
		String usercontent = mContent.getText().toString();
		usercontent = usercontent == null ? "" : usercontent;
		usercontent = new BASE64Encoder().encode(usercontent.getBytes());
		Text content = doc.createTextNode(usercontent);
		textcontent.appendChild(content);
		root.appendChild(textcontent);// 添加属性

		return toStringFromDoc(doc);
	}

	private MusicInfo musicInfo;

	/**
	 * Ask the user to select the split screen mode
	 */
	private void selectMusicFromList() {
		Resources res = getResources();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择音乐");
		if (mWebsiteInfo == null) {
			return;
		}
		final ArrayList musicList = ((MusicWebsiteInfo) mWebsiteInfo).mMusicList;
		if (musicList == null) {
			return;
		}

		String items[] = new String[] {};
		int size = musicList.size() + 1;
		for (int i = 0; i < size; i++) {
			if (i == 0) {

				items[0] = "点击下拉列表选择";
			} else {
				items[i] = ((MusicInfo) musicList.get(i - 1)).hname;
			}
		}

		builder.setSingleChoiceItems(items, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						musicInfo = selectItem(musicList, which);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void postProductDown() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 5000);
		HttpPost httppost = null;
		if (mIsCombinedImg) {
			httppost = new HttpPost("http://www.uuunm.com/ProductDownNew.jsp");
		} else {
			httppost = new HttpPost("http://www.uuunm.com/ProductDown.jsp");
		}

		try {
			Log.d("zheng", getProductDownAPIXMLString());
			// notifyToUser(getProductDownAPIXMLString());
			StringEntity se = new StringEntity(getProductDownAPIXMLString(),
					HTTP.UTF_8);
			se.setContentType("text/xml");
			httppost.setEntity(se);

			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity resEntity = httpresponse.getEntity();
			String result = EntityUtils.toString(resEntity);
			if (result != null && result.contains("成功")) {
				notifyToUser("发送成功!");
			} else {
				notifyToUser("发生未知错误, 发送失败.");
			}
			Log.d("zheng", "Status OK: \n" + result);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			notifyToUser(e.getMessage());
			// tv.setText("Status NOT OK: \n" + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			notifyToUser(e.getMessage());
			// tv.setText("Status NOT OK: \n" + e.getMessage());
		}
	}

	public static String post(String actionUrl, Map<String, String> params,
			FormFile file) {
		try {

			String BOUNDARY = "---------7d4a6d158c9";// 数据分隔线
			String MULTIPART_FORM_DATA = "text/xml";
			URL url = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ ";boundary=" + BOUNDARY);
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append("\r\n");
			}
			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// 发送表单字段数据
			if (file != null) {// 发送文件数据
				StringBuilder split = new StringBuilder();
				split.append("--");
				split.append(BOUNDARY);
				split.append("\r\n");
				split.append("Content-Disposition: form-data;name=\""
						+ file.getFormname() + "\";filename=\""
						+ file.getFilname() + "\"\r\n");

				split.append("Content-Type:" + file.getContentType()
						+ "\r\n\r\n");

				outStream.write(split.toString().getBytes());

				if (file.getInStream() != null) {
					byte[] buffer = new byte[1024];
					int len = 0;

					while ((len = file.getInStream().read(buffer)) != -1) {

						outStream.write(buffer, 0, len);

					}

					file.getInStream().close();

				} else {

					outStream.write(file.getData(), 0, file.getData().length);

				}

				outStream.write("\r\n".getBytes());

			}

			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志

			outStream.write(end_data);

			outStream.flush();

			int cah = conn.getResponseCode();
			Log.d("zheng", "response return code: " + cah);
			// if (cah != 200)
			// throw new RuntimeException("请求url失败: " + cah);

			InputStream is = conn.getInputStream();

			int ch;

			StringBuilder b = new StringBuilder();

			while ((ch = is.read()) != -1) {

				b.append((char) ch);
			}

			Log.d("zheng", "return: " + b.toString());
			Log.d("zheng", "result code:" + cah);
			outStream.close();
			conn.disconnect();
			if (cah == 200) {
				return "发布成功";
			} else {
				return "发布失败";
			}

		} catch (ConnectException e) {
			return "发布失败， 请检查网络";
		} catch (Exception e) {
			e.printStackTrace();
			return "发布失败";
		}
	}

	private byte[] constructMMS(EncodedStringValue phoneNumber) {
		final SendReq sendRequest = new SendReq();
		// 设置主题
		String subject = mSubject.getEditableText().toString();
		if (subject != null && subject.length() != 0) {
			EncodedStringValue[] sub = EncodedStringValue.extract(subject);
			if (sub != null && sub.length > 0) {
				sendRequest.setSubject(sub[0]);
			}
		}
		// 设置接收号码
		// EncodedStringValue[] phoneNumbers =
		// EncodedStringValue.extract(mReceiver.getEditableText().toString());
		// if (phoneNumbers != null && phoneNumbers.length > 0) {
		sendRequest.addTo(phoneNumber);
		// }
		final PduBody pduBody = new PduBody();
		// PduPart存放附件，每个附件是一个part，如果添加多个附件，就想body中add多个part。
		// 文本
		String text = mContent.getEditableText().toString();
		if (!TextUtils.isEmpty(text)) {
			PduPart partPdu3 = new PduPart();
			partPdu3.setCharset(CharacterSets.UTF_8);
			partPdu3.setName("mms_text.txt".getBytes());
			partPdu3.setContentType("text/plain".getBytes());
			partPdu3.setData(text.getBytes());// 对于文本直接保存数据在Part中。
			pduBody.addPart(partPdu3);
		}
		// 图片
		String imagePath = saveImage(MessageActivity.this, photoaddress);
		if (!TextUtils.isEmpty(imagePath)) {
			PduPart partPdu = new PduPart();
			partPdu.setCharset(CharacterSets.UTF_8);// 编码格式
			partPdu.setName("aa.jpg".getBytes());
			partPdu.setContentType("image/jpeg".getBytes());
			// partPdu.setDataUri(Uri.parse("file:///sdcard/aa.bmp"));
			partPdu.setDataUri(Uri.fromFile(new File(imagePath)));// 对于图片、音频、视频、PduPart渲染器支持以URI作为数据源保存在Part中。
			pduBody.addPart(partPdu);
		}
		// 音频
		String audioPath = saveAudio();
		if (!TextUtils.isEmpty(audioPath)) {

		}
		if (!TextUtils.isEmpty(audioPath)) {
			PduPart partPdu2 = new PduPart();
			partPdu2.setCharset(CharacterSets.ISO_8859_1);
			partPdu2.setName("speech_test.amr".getBytes());
			partPdu2.setContentType("audio/amr".getBytes());
			// partPdu2.setContentType("audio/amr-wb".getBytes());
			// partPdu2.setDataUri(Uri.parse("file://mnt//sdcard//.lv//audio//1326786209801.amr"));
			partPdu2.setDataUri(Uri.fromFile(new File(audioPath)));
			pduBody.addPart(partPdu2);
		}
		sendRequest.setBody(pduBody);
		final PduComposer composer = new PduComposer(MessageActivity.this,
				sendRequest);
		return composer.make(); // 将彩信的内容以及主题等信息转化成byte数组，准备通过http协议发送到
								// "http://mmsc.monternet.com";
	}

	private String saveAudio() {
		if (amrurl == null) {
			return null;
		}
		File sdCard = null;
		try {
			String filename = "/sdcard/";
			int index = amrurl.lastIndexOf("/");
			filename += amrurl.substring(index + 1);
			sdCard = new File(filename);
			if (sdCard.exists()) {
				return filename;
			}

			byte[] bytes = Utils.getByteArrayFromNetwork(amrurl,
					MessageActivity.this);
			if (bytes == null) {
				return null;
			}
			FileOutputStream outStreamz = new FileOutputStream(sdCard);
			outStreamz.write(bytes);
			outStreamz.close();
			return filename;
		} catch (Exception e) {
			e.printStackTrace();

			if (amrurl == null) {
				return null;
			}

			try {
				String filename = "/mnt/sdcard/";
				int index = amrurl.lastIndexOf("/");
				filename += amrurl.substring(index + 1);
				sdCard = new File(filename);
				if (sdCard.exists()) {
					return filename;
				}

				byte[] bytes = Utils.getByteArrayFromNetwork(amrurl,
						MessageActivity.this);
				if (bytes == null) {
					notifyToUser("获取音乐失败, 请检查网络!");
					return null;
				}
				FileOutputStream outStreamz = new FileOutputStream(sdCard);
				outStreamz.write(bytes);
				outStreamz.close();
				return filename;
			} catch (Exception e1) {
				notifyToUser("保存失败，请检查SD卡是否可用!");
				e1.printStackTrace();
			}
		}

		return null;
	}

	public static String mmscUrl = "http://mmsc.monternet.com";

	public void sendMms(Context context) {
		mContext = context;
		// super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		// �ر�WIFI
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {

			wifiManager.setWifiEnabled(false);

		}
		// ��ȡ��ǰ����
		ConnectivityManager conManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		String currentAPN = info.getExtraInfo();
		conManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE,
				"mms");
		currentAPN = info.getExtraInfo();
		// ֻ��CMWAP���ܷ��Ͳ���
		if ("cmwap".equals(currentAPN)) {
			Log.d("zheng", "already cmwap now");
			EncodedStringValue[] phoneNumbers = EncodedStringValue
					.extract(mReceiver.getEditableText().toString());
			if (phoneNumbers != null && phoneNumbers.length > 0) {
				int len = phoneNumbers.length;
				for (int i = 0; i < len; i++) {
					byte[] bytesToSend = constructMMS(phoneNumbers[i]);
					sendMMS(bytesToSend);
				}
			}
		} else {
			forceCMWapConnection();
		}

		Runnable action = new Runnable() {

			public void run() {
				if (pd != null) {
					pd.dismiss();
				}
			}
		};

		runOnUiThread(action);
		notifyToUser("彩信已发送");
		finish();
	}

	void sendMMS(byte[] bytesToSend) {
		MMSSender sender = new MMSSender();
		// new Thread() {
		// public void run() {
		try {
			byte[] res = sender.sendMMS(mContext, bytesToSend);
			// byte[] res = sendMMS(mContext, bytesToSend);
			System.out.println("-==-=-=>>> " + res.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// };
		// }.start();
	}

	// 发送pdu到彩信中心的代码：
	// public static String mmscUrl = "http://mmsc.monternet.com";//彩信中心地址

	public static String mmsProxy = "10.0.0.172"; // 代理主机
	public static String mmsProt = "80"; // 端口

	private static String HDR_VALUE_ACCEPT_LANGUAGE = "";
	// Definition for necessary HTTP headers.
	private static final String HDR_KEY_ACCEPT = "Accept";
	private static final String HDR_KEY_ACCEPT_LANGUAGE = "Accept-Language";
	private static final String HDR_VALUE_ACCEPT = "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic";

	public static byte[] sendMMS(Context context, byte[] pdu)
			throws IOException {

		// HDR_VALUE_ACCEPT_LANGUAGE = getHttpAcceptLanguage();

		if (mmscUrl == null) {
			throw new IllegalArgumentException("URL must not be null.");
		}

		AndroidHttpClient client = null;
		try {
			// Make sure to use a proxy which supports CONNECT.
			client = AndroidHttpClient.newInstance("Android-Mms/2.0");
			;
			HttpPost post = new HttpPost(mmscUrl);
			// HttpHost target = new HttpHost(hostUrl.getHost(),
			// hostUrl.getPort(), HttpHost.DEFAULT_SCHEME_NAME);
			// mms PUD START
			ByteArrayEntity entity = new ByteArrayEntity(pdu);
			entity.setContentType("application/vnd.wap.mms-message");
			post.setEntity(entity);
			post.addHeader(HDR_KEY_ACCEPT, HDR_VALUE_ACCEPT);
			post.addHeader(HDR_KEY_ACCEPT_LANGUAGE, HDR_VALUE_ACCEPT_LANGUAGE);
			// mms PUD END
			HttpParams params = client.getParams();
			HttpProtocolParams.setContentCharset(params, "UTF-8");
			HttpResponse response = client.execute(post);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != 200) { // HTTP 200 is not success.
				Log.d("zheng", "!200");
				throw new IOException("HTTP error: " + status.getReasonPhrase());
			}
			HttpEntity resentity = response.getEntity();
			byte[] body = null;
			if (resentity != null) {
				try {
					if (resentity.getContentLength() > 0) {
						body = new byte[(int) resentity.getContentLength()];
						DataInputStream dis = new DataInputStream(
								resentity.getContent());
						try {
							dis.readFully(body);
						} finally {
							try {
								dis.close();
							} catch (IOException e) {
								Log.e("zheng", "Error closing input stream: "
										+ e.getMessage());
							}
						}
					}
				} finally {
					if (entity != null) {
						entity.consumeContent();
					}
				}
			}
			Log.d("zheng", "result:" + new String(body));
			return body;
		} catch (IllegalStateException e) {
			Log.d("zheng", "", e);
			// handleHttpConnectionException(e, mmscUrl);
		} catch (IllegalArgumentException e) {
			Log.d("zheng", "", e);
			// handleHttpConnectionException(e, mmscUrl);
		} catch (SocketException e) {
			Log.d("zheng", "", e);
			// handleHttpConnectionException(e, mmscUrl);
		} catch (Exception e) {
			Log.d("zheng", "", e);
			// handleHttpConnectionException(e, mmscUrl);
		} finally {
			if (client != null) {
				// client.;
			}
		}
		return new byte[0];
	}

	public static final Uri CURRENT_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");
	public static final Uri APN_LIST_URI = Uri
			.parse("content://telephony/carriers");
	ConnectivityManager conManager;
	NetworkInfo info;
	String currentAPN;
	NetworkChangeReceiver mNChangeReceiver;

	// 获取到APN之后，需要判断是否是cmwap, 如果不是需要更改当前APN为cmwap. 可以通过改数据库的方式：
	public static int updateCurrentAPN(ContentResolver resolver, String newAPN) {
		Cursor cursor = null;
		try {
			// get new apn id from list
			cursor = resolver.query(APN_LIST_URI, null,
					" apn = ? and current = 1",
					new String[] { newAPN.toLowerCase() }, null);
			String apnId = null;
			if (cursor != null && cursor.moveToFirst()) {
				apnId = cursor.getString(cursor.getColumnIndex("_id"));
			}
			cursor.close();

			// set new apn id as chosen one
			if (apnId != null) {
				ContentValues values = new ContentValues();
				values.put("apn_id", apnId);
				resolver.update(CURRENT_APN_URI, values, null, null);
			} else {
				// apn id not found, return 0.
				return 0;
			}
		} catch (SQLException e) {
			Log.d("zheng", e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		// update success
		return 1;
	}

	// 在更改数据库之后，网络并不是立刻就能连接上的，我们需要加一个网络连接的消息监听器，发现切换成cmwap之后才开始连接服务器。NetworkChangeReceiver如下：
	/*
	 * receiver for observing network connection change
	 */
	public class NetworkChangeReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent
					.getAction())) {
				NetworkInfo info = conManager
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				String apn = info.getExtraInfo();
				if ("cmwap".equals(apn)) {
					/*
					 * apn change message is sent out more than once during a
					 * second, but it only happens once practically.
					 */
					if (mNChangeReceiver != null) {
						// updateDcdContent(mManRequest);
						Log.d("zheng", "already cmwap now");
						sendMms(MessageActivity.this);

						// EncodedStringValue[] phoneNumbers =
						// EncodedStringValue.extract(mReceiver.getEditableText().toString());
						// if (phoneNumbers != null && phoneNumbers.length > 0)
						// {
						// int len = phoneNumbers.length;
						// for (int i = 0; i < len; i++) {
						// byte[] bytesToSend = constructMMS(phoneNumbers[i]);
						// sendMMS(bytesToSend);
						// }
						// }

						unregisterReceiver(mNChangeReceiver);
						mNChangeReceiver = null;
					}
				}
			}
		}
	}

	// 切换apn总入口如下，包括了检测当前apn, 切换，注册网络监听器：
	/*
	 * change current network connection to cmwap if it's not.
	 */
	public int forceCMWapConnection() {

		NetworkInfo info = conManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		String oldAPN = info.getExtraInfo();

		// if current apn is not cmwap, we have to switch to cmwap.
		if (!"cmwap".equals(oldAPN)) {
			mNChangeReceiver = new NetworkChangeReceiver();
			// register receiver for wap network connection.
			IntentFilter upIntentFilter = new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION);
			registerReceiver(mNChangeReceiver, upIntentFilter);
			updateCurrentAPN(getContentResolver(), "cmwap");
			return 1;
		}
		return 0;
	}

	private void more() {
		HttpParams baseParams = new BasicHttpParams();
		baseParams.setParameter("http.route.default-proxy", new HttpHost(
				"10.0.0.172", 80));
		HttpConnectionParams.setConnectionTimeout(baseParams, 30 * 1000);
		HttpConnectionParams.setSoTimeout(baseParams, 45 * 1000);
		// establish HttpClient
		DefaultHttpClient client = new DefaultHttpClient(baseParams);
	}

	private void sendMultiMessage() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		// Intent intent = new Intent(Intent.ACTION_SENDTO,
		// Uri.fromParts("mmsto", "13810891274", null));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_STREAM,
				Uri.parse("file:///sdcard/temp.jpg"));// uri为你的附件的uri
		intent.putExtra("subject", "subject");
		intent.putExtra("sms_body", "body");
		intent.putExtra(Intent.EXTRA_TEXT, "sdfsdf");
		intent.setType("image/audio");// 彩信附件类型
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + audioPath));// uri为你的附件的uri
		// intent.setClassName("com.android.mms",
		// "com.android.mms.ui.SlideEditorActivity");
		startActivity(intent);
	}

	private void sendBySlideShow_old() {
		Log.d("zheng", "sendBySlideShow");
		String imagePath = saveImage(MessageActivity.this, photoaddress);
		if (imagePath == null || imagePath.length() == 0) {
			Log.d("zheng", "imagePath null");
			if (pd != null) {
				pd.dismiss();
			}
			return;
		}

		if (audioPath == null) {
			// audioPath = saveAudio();
		}

		Log.d("zheng", "imagePath:" + imagePath);
		ArrayList<Uri> uris = new ArrayList<Uri>();
		uris.add(Uri.parse("file://" + imagePath));

		if (audioPath != null) {
			Log.d("zheng", "audioPath:" + audioPath);
			uris.add(Uri.parse("file://" + audioPath));
		}
		Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mmsto:"));
		String content = mSubject.getEditableText().toString();
		if (content != null && content.length() != 0) {
			content += "\n";
		}
		content += mContent.getEditableText().toString();
		if (content != null && content.length() != 0) {
			intent.putExtra(Intent.EXTRA_TEXT, content);
			intent.putExtra("sms_body", content);
		} else {
			intent.putExtra(Intent.EXTRA_TEXT, "");
		}

		if (uris.size() > 0) {
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file://" + imagePath));
		}

		intent.setType("image/gif");
		// i.setType("*/*");
		// intent.setClassName("com.sonyericsson.conversations",
		// "com.sonyericsson.conversations.ui.ConversationActivity");
		startActivity(intent);
		if (pd != null) {
			pd.dismiss();
		}
	}

	private void sendBySlideShow() {
		Log.d("zheng", "sendBySlideShow");
		String imagePath = saveImage(MessageActivity.this, photoaddress);
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

	// htc
	private void combineMMS(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setClassName("com.android.mms",
				"com.android.mms.ui.ComposeMessageActivity");
		intent.putExtra(Intent.EXTRA_STREAM, uri);

		String body = mContent.getEditableText().toString();
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

	public static String getWeiboSubject() {
		EditText mSubject = (EditText) ((Activity) mContext)
				.findViewById(R.id.subject);
		return mSubject.getEditableText().toString();
	}

	public static String getWeiboBody() {
		EditText mSubject = (EditText) ((Activity) mContext)
				.findViewById(R.id.subject);
		EditText mContent = (EditText) ((Activity) mContext)
				.findViewById(R.id.content);

		String content = mSubject.getEditableText().toString();
		if (content != null && content.length() != 0) {
			content = "#" + content + "# ";
			// content += "\n";
		}
		content += mContent.getEditableText().toString();
		if (content != null && content.length() != 0) {
			return content;
		}

		return "";
	}
}