package com.androids.photoalbum.tab.ui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

public class SignNameMessageActivity extends BaseActivity implements OnItemSelectedListener {
//	private Bitmap mBitmap;
	private GifView mGifView;
	private ImageView mImageView;
	private MediaPlayer mMediaPlayer = null;
	private Button mFavorite;
	private int mPosition = -1;
	private String musicid = "";
	private String productid = "";
	private String id = null;
	private Button mBack;
	private Button mFinish;
	private EditText mSubject;
	private EditText mContent;
	public static AutoCompleteTextView mReceiver;
	private Context mContext;
	public static SignNameMessageActivity mActivity;
	private TextView mAddContact;
	
	private byte[] bytes;
	private String photoaddress;
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.signname_message_layout); 
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_GONE, null, null, null);
		mGifView = (GifView)findViewById(R.id.image);
		mImageView = (ImageView)findViewById(R.id.imageview);
//		mListView = (ListView)findViewById(R.id.list_view);
//		mListView.setOnItemClickListener(this);
//		setTitleBar(com.android.photoalbum.view.BaseLayout.TYPE_NORMAL, "返回", "", "刷新");
//		mImageWidth = (int)mResources.getDimension(R.dimen.gif_with);
//	    mImageHeigth = (int)mResources.getDimension(R.dimen.gif_height);
	       mImageWidth = (int)mResources.getDimension(R.dimen.msg_gif_with);
	        mImageHeigth = (int)mResources.getDimension(R.dimen.msg_gif_height);
		new MusicParserTask().execute("all", null);
//		onNewIntent(getIntent());
		mMediaPlayer = new MediaPlayer();
		mFavorite = (Button)findViewById(R.id.favorite);
		mFavorite.setOnClickListener(this);
		mFavorite.setOnTouchListener(this);
		
		mFinish = (Button)findViewById(R.id.finish);
		mFinish.setOnClickListener(this);
		
		mBack = (Button)findViewById(R.id.back);
		mBack.setOnClickListener(this);
		
		mSpinner = (Spinner)findViewById(R.id.music);
		mSpinner.setOnItemSelectedListener(this);
		
		mSubject = (EditText)findViewById(R.id.subject);
		mContent = (EditText)findViewById(R.id.content);
		
		mReceiver = (AutoCompleteTextView)findViewById(R.id.receiver);
		
		getIntentInfo(getIntent());
		
		mContext = this;
		mActivity = this;
		isActivity = true;
		
		mAddContact = (TextView)findViewById(R.id.add_contact);
	    mAddContact.setOnClickListener(
                new ResultDisplayer("Selected contact",
                        ContactsContract.Contacts.CONTENT_ITEM_TYPE));
	    
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
	
	int mCurDrawFrame = 0;
	int mTotalFrames = 0;
	String fromActivity = null;
	ArrayList<Bitmap> gifBitmaps = new ArrayList<Bitmap>();
	private void getIntentInfo(Intent intent) {
		if (intent == null) {
			return;
		}
		
	    if (intent != null) {
	    	bytes = intent.getByteArrayExtra("photo_bitmap");
	    	Log.d("zheng", "peter bytes:" + bytes + "length:" + bytes.length);
	    	
	    	photoaddress = intent.getStringExtra("photoaddress");
	    	Log.d("zheng", "peter photoaddress:" + photoaddress);
	    }
		
		isInTabGroup = intent.getBooleanExtra("in_tab_group", true);
		fromActivity = intent.getStringExtra("from_activity");
		
		productid = intent.getStringExtra("productid");
		id = intent.getStringExtra("id");
//		final String photoaddress = intent.getStringExtra("photoaddress");
//		if (photoaddress == null) {
//			return;
//		}
		
		
        if ("SignNameActivity".equals(fromActivity)) {
        	Runnable run = new Runnable() {
				
				public void run() {
					final Bitmap bitmap = Utils.getResizedBitmap(Utils.getBitMapFromNetwork(photoaddress, SignNameMessageActivity.this), mImageWidth, mImageHeigth);
					
					Runnable action = new Runnable() {
						
						public void run() {
							mImageView.setImageBitmap(bitmap);
						}
					};
					
					runOnUiThread(action);
				}
			};
			
			new Thread(run).start();
			mFavorite.setVisibility(View.GONE);
			mGifView.setVisibility(View.GONE);
			
			mImageView.setVisibility(View.VISIBLE);
        	
        } else {
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
		            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
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
						}						
				};
				runOnUiThread(action);
			}
		};
		new Thread(run).start();
	}
	
	@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == 10) {
					MultiMediaMessageActivity.contactActivityResult(requestCode, resultCode, data, SignNameMessageActivity.mReceiver, mResolver);
				return;
			}
		}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.favorite:
				if (productid == null || productid.length() == 0) {
					return;
				}
				
				if (StaticInfo.mPassword == null || StaticInfo.mPassword.length() == 0) {
					Intent intent = new Intent(this, LoginActivity.class);
					intent.putExtra("isactivity", true);
					startActivity(intent);
					return;
				}
				new CommonWebsiteTask(new AddCollectionWebsiteParser(), this)
						.execute(NetWorkUtils.ADD_COLLECTION + "?Proid=" + productid + "&userid=" + StaticInfo.userid);
				break;
			case R.id.back:
//				if (isInTabGroup) {
//					MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
//				} else {
					finish();
//				}
//				if (MainTabActivity.mCurrentPage == 0) {
//					MainTabActivity.mHomeActivityGroup.setPreviousActivity();
//				} else {
//					finish();
//				}
				break;
			case R.id.finish:
//				String text = mReceiver.getEditableText().toString();
//				if (text == null || text.length() == 0) {
//					notifyToUser("请输入收件人信息!");
//					return;
//				}
				
//				if (StaticInfo.mPassword == null || StaticInfo.mPassword.length() == 0) {
//					Intent intent = new Intent(this, LoginActivity.class);
//					intent.putExtra("isactivity", true);
//					startActivity(intent);
//					return;
//				}
				
				Runnable run = new Runnable() {
					
					public void run() {
						showProgessBarDialog("Loading...");
//						postProductDown();
						sendBySlideShow();
						pd.dismiss();
					}
				};
				
				new Thread(run).start();
				break;
//			case R.id.music:
//				selectMusicFromList();
//				break;
			default:
				break;
		}
		super.onClick(view);
	}
	
	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {
			
			public void run() {
			    if(pd==null) {
			    	pd=new ProgressDialog(SignNameMessageActivity.this);
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
//		mImageView.setGifImage((InputStream)null);
		super.onDestroy();
	}
	
	public MusicInfo selectItem(ArrayList musicList, int position) {
		MusicInfo musicInfo = (MusicInfo)musicList.get(position);
		if (mMediaPlayer.isPlaying()) {
			if (mPosition == position) {
				return musicInfo;
			}
			mMediaPlayer.pause();
		}
		
		mPosition = position;
//		MusicInfo musicInfo = (MusicInfo)parent.getItemAtPosition(position);
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
	
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
		Spinner spinner = (Spinner)parent;
		final ArrayList musicList = ((MusicWebsiteInfo)mWebsiteInfo).mMusicList;
		MusicInfo musicInfo = (MusicInfo)musicList.get(position - 1);
//		MusicInfo musicInfo = (MusicInfo)spinner.getSelectedItem();
//		MusicInfo musicInfo = (MusicInfo)spinner.getItemAtPosition(position);
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
	}
	
    public Bitmap getPhotoFromDatabase(String productid) {
		Cursor cursor = null;
		String selection = AlbumColumns.PRODUCT_ID + "=?";
		
		try {
			cursor = mResolver.query(Album.ALBUM_CONTENT_URI, Album.CONTENT_PROJECTION, selection,
					new String[] { productid }, null);
			
			if (cursor != null && cursor.moveToFirst()) {
	            byte[] data = cursor.getBlob(AlbumColumns.CONTENT_PHOTO_BITMAP_COLUMN);
	            if (data != null && data.length != 0) {
	            	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
	            	bitmap = Utils.getResizedBitmap(bitmap, mImageWidth, mImageHeigth);
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
//		consignid.setAttribute("attr", "kehuduan");
		consignid.setAttribute("attr", StaticInfo.mUsername);
		root.appendChild(consignid);// 添加属性
		
		Element password = doc.createElement("password");
		password.setAttribute("attr", StaticInfo.mPassword);
		root.appendChild(password);// 添加属性
		
		Element desttermid = null;
		String[] numbers = getReceivers();
		int size = numbers.length;
		for(int i = 0; i < size; i++) {
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
		
			Element MMSContent = doc.createElement("MMSContent");
			root.appendChild(MMSContent); // 将根元素添加到文档上
			
			Element contentType = doc.createElement("contentType");
			contentType.setAttribute("attr", "image/jpeg");
			MMSContent.appendChild(contentType);// 添加属性
			
			Element imagecontent = doc.createElement("content");
			Text content = doc.createTextNode(new BASE64Encoder().encode(bytes));
			imagecontent.appendChild(content);
			MMSContent.appendChild(imagecontent);
		
		Element musicid = doc.createElement("musicid");
		musicid.setAttribute("attr", SignNameMessageActivity.this.musicid);
		root.appendChild(musicid);// 添加属性
		
		Element textcontent = doc.createElement("textcontent");
		String usercontent = mContent.getText().toString();
		usercontent = usercontent == null ? "" : usercontent;
		usercontent = new BASE64Encoder().encode(usercontent.getBytes());
		content = doc.createTextNode(usercontent);
		textcontent.appendChild(content);
		root.appendChild(textcontent);// 添加属性
		
		return toStringFromDoc(doc);
	} 
	
//	public String getProductDownAPIXMLString() {
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = null;
//		Text content ;
//		try {
//			builder = dbf.newDocumentBuilder();
//		} catch (Exception e) {
//		}
//		Document doc = builder.newDocument();
//
//		Element root = doc.createElement("MMessage");
//		doc.appendChild(root); // 将根元素添加到文档上
//
//		Element consignid = doc.createElement("consignid");
////		consignid.setAttribute("attr", "kehuduan");
//		consignid.setAttribute("attr", StaticInfo.mUsername);
//		root.appendChild(consignid);// 添加属性
//		
//		Element password = doc.createElement("password");
//		password.setAttribute("attr", StaticInfo.mPassword);
//		root.appendChild(password);// 添加属性
//		
//		Element desttermid = null;
//		String[] numbers = getReceivers();
//		int size = numbers.length;
//		for(int i = 0; i < size; i++) {
//			desttermid = doc.createElement("desttermid");
//			desttermid.setAttribute("attr", numbers[i]);
//			root.appendChild(desttermid);// 添加属性
//		}
//		
//		long timestamp = System.currentTimeMillis();
//		Element linkid = doc.createElement("linkid");
//		linkid.setAttribute("attr", String.valueOf(timestamp));
//		root.appendChild(linkid);// 添加属性
//		
////		Element subject = doc.createElement("subject");
////		String usersubject = mSubject.getText().toString();
////		usersubject = usersubject == null ? "" : usersubject;
////		usersubject = new BASE64Encoder().encode(usersubject.getBytes());
////		subject.setAttribute("attr", usersubject);
////		root.appendChild(subject);// 添加属性
//		
////		Element productid = doc.createElement("productid");
////		productid.setAttribute("attr", SignNameMessageActivity.this.productid);
////		root.appendChild(productid);// 添加属性
//		
////		final ByteArrayOutputStream os = new ByteArrayOutputStream();
////		photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
////		byte[] imgbytes = os.toByteArray();
//		
//		Element MMSContent = doc.createElement("MMSContent");
//		root.appendChild(MMSContent); // 将根元素添加到文档上
//		
//		Element contentType = doc.createElement("contentType");
//		contentType.setAttribute("attr", "image/jpeg");
//		MMSContent.appendChild(contentType);// 添加属性
//		
//		Element imagecontent = doc.createElement("content");
//		content = doc.createTextNode(new BASE64Encoder().encode(bytes));
//		imagecontent.appendChild(content);
//		MMSContent.appendChild(imagecontent);
//		
////		Element musicid = doc.createElement("musicid");
////		musicid.setAttribute("attr", SignNameMessageActivity.this.musicid);
////		root.appendChild(musicid);// 添加属性
//		
////		Element textcontent = doc.createElement("textcontent");
////		String usercontent = mContent.getText().toString();
////		usercontent = usercontent == null ? "" : usercontent;
////		usercontent = new BASE64Encoder().encode(usercontent.getBytes());
////		Text content = doc.createTextNode(usercontent);
////		textcontent.appendChild(content);
////		root.appendChild(textcontent);// 添加属性
//		
//		return toStringFromDoc(doc);
//	} 
	
	
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
        final ArrayList musicList = ((MusicWebsiteInfo)mWebsiteInfo).mMusicList;
        if (musicList == null) {
        	return;
        }
        
        String items[] = new String[]{};
        int size = musicList.size() + 1;
        for (int i = 0; i < size; i++) {
        	if (i == 0) {
        		items[0] = "点击下拉列表选择";
        	} else {
        		items[i] = ((MusicInfo)musicList.get(i - 1)).hname;
        	}
        }
        
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	musicInfo = selectItem(musicList, which);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
        HttpPost httppost = new HttpPost("http://www.uuunm.com/ProductDownNew.jsp");

        try {
        	Log.d("zheng", getProductDownAPIXMLString());
            StringEntity se = new StringEntity(getProductDownAPIXMLString(), HTTP.UTF_8);
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
//            tv.setText("Status NOT OK: \n" + e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            notifyToUser(e.getMessage());
//            tv.setText("Status NOT OK: \n" + e.getMessage());
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
//			if (cah != 200)
//				throw new RuntimeException("请求url失败: " + cah);

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
	
	private File sdCard = null; 
	private String saveImage() {
		if (photoaddress == null) {
			return null;
		}
	    
		try {
			String filename = "/mnt/sdcard/";
			int index = photoaddress.lastIndexOf("/");
			filename += photoaddress.substring(index+1);
			sdCard = new File(filename);
			if (sdCard.exists()) {
			    return filename;
			}
			
			Bitmap bitmap = Utils.getBitMapFromNetwork(photoaddress, SignNameMessageActivity.this);
			if (bitmap == null) {
			    return null;
			}
			FileOutputStream outStreamz = new FileOutputStream(sdCard);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStreamz);
			outStreamz.close();
			return filename;
		} catch (Exception e) {
			e.printStackTrace();
			
			if (photoaddress == null) {
				return null;
			}
		    
			try {
				String filename = "/sdcard/";
				int index = photoaddress.lastIndexOf("/");
				filename += photoaddress.substring(index+1);
				sdCard = new File(filename);
				if (sdCard.exists()) {
				    return filename;
				}
				
				Bitmap bitmap = Utils.getBitMapFromNetwork(photoaddress, SignNameMessageActivity.this);
				if (bitmap == null) {
					notifyToUser("获取图片失败, 请检查网络!");
				    return null;
				}
				FileOutputStream outStreamz = new FileOutputStream(sdCard);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStreamz);
				outStreamz.close();
				return filename;
			} catch (Exception e1) {
			    notifyToUser("保存失败，请检查SD卡是否可用!");
				e1.printStackTrace();
			}
		}
		
		return null;
	}
	
    private void sendBySlideShow() {
        Log.d("zheng", "sendBySlideShow");
        String imagePath = saveImage();
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
    
	//htc
	private void combineMMS(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_SEND); 
		intent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
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
	
    private void sendBySlideShow_old() {
        Log.d("zheng", "sendBySlideShow");
        String imagePath = saveImage();
        if (imagePath == null || imagePath.length() == 0) {
            Log.d("zheng", "imagePath null");
            if (pd != null) {
                pd.dismiss();
            }
            return;
        }
        
        Log.d("zheng", "imagePath:" + imagePath);
        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(Uri.parse("file://" + imagePath));
        
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
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + imagePath));
        }
        
        intent.setType("image/*");
//      i.setType("*/*");
//      intent.setClassName("com.sonyericsson.conversations", "com.sonyericsson.conversations.ui.ConversationActivity");
      startActivity(intent);
      if (pd != null) {
          pd.dismiss();
      }
  }
}