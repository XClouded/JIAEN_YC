package com.xhm.q3.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.WorkingPhotoWebsiteParser;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.view.MainTabActivity;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;

public class q3_youcai_haoyou_add extends Activity implements OnClickListener {
	private Button mButton_add_no_1, mButton_add_yes_1;
	private ImageView mImageView, mRentou, mChazhao;
	private AutoCompleteTextView mBenDi, mShouDong;
	private ArrayList<q3_Haoyou_Info> mHaoyou_Infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_haoyou_add);
		initView();
		initVar();
	}

	private void initVar() {
		mHaoyou_Infos = new ArrayList<q3_Haoyou_Info>();

	}

	private void initView() {
		mImageView = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mImageView.setOnClickListener(this);
		mRentou = (ImageView) findViewById(R.id.q3_youcai_haoyou_add_local_rentou);
		mRentou.setOnClickListener(this);
		mChazhao = (ImageView) findViewById(R.id.q3_youcai_haoyou_add_shoudong);
		mChazhao.setOnClickListener(this);
		mButton_add_no_1 = (Button) findViewById(R.id.q3_youcai_haoyou_add_no_1);
		mButton_add_no_1.setOnClickListener(this);
		mButton_add_yes_1 = (Button) findViewById(R.id.q3_youcai_haoyou_add_yes_1);
		mButton_add_yes_1.setOnClickListener(this);
		mBenDi = (AutoCompleteTextView) findViewById(R.id.q3_youcai_haoyou_add_local);
		mShouDong = (AutoCompleteTextView) findViewById(R.id.q3_youcai_haoyou_add_phone);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;

		case R.id.q3_youcai_haoyou_add_no_1:
			finish();
			break;
		case R.id.q3_youcai_haoyou_add_yes_1:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("添加中...");
			dialog.show();
			String result = PostFriendsInfo();
			dialog.dismiss();
			System.out.println("shuliang" + mHaoyou_Infos.size());
			break;
		case R.id.q3_youcai_haoyou_add_local_rentou:
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
			startActivityForResult(intent, 138);
			break;
		case R.id.q3_youcai_haoyou_add_shoudong:
			intent = new Intent(this, q3_haoyou_add_haoyou.class);
			startActivityForResult(intent, 100);
			break;
		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			System.out.println("100");
			if (data.getStringExtra("name") != null) {
				if (mShouDong.getText() == null
						|| mShouDong.getText().length() == 0) {
					mShouDong.setText(mShouDong.getText().toString()
							+ data.getStringExtra("name"));
				} else {
					mShouDong.setText(mShouDong.getText() + ","
							+ data.getStringExtra("name"));
				}
				q3_Haoyou_Info info = new q3_Haoyou_Info();
				info.setmName(data.getStringExtra("name"));
				info.setmPhone_num(data.getStringExtra("phone"));
				System.out.println("phone==" + data.getStringExtra("phone"));
				mHaoyou_Infos.add(info);
			}
		}
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
									String name = phones
											.getString(phones
													.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
									String phoneNumber = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									String phoneTpye = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
									q3_Haoyou_Info info = new q3_Haoyou_Info();
									info.setmName(name);
									info.setmPhone_num(phoneNumber);
									mHaoyou_Infos.add(info);
									if (mBenDi.getEditableText().toString() == null
											|| mBenDi.getEditableText()
													.toString().length() == 0) {
										mBenDi.setText(name);
									} else {
										mBenDi.setText(mBenDi.getEditableText()
												.toString() + "," + name);
									}
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

	// 获得好友信息的xml
	public String getProductDownAPIXMLString() {
		SharedPreferences editor = getSharedPreferences(
				MainTabActivity.USER_INFO, MODE_WORLD_READABLE);
		String name = editor.getString("username", "-1");
		String pwd = editor.getString("password", "-1");
		System.out.println("pwd===" + pwd);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();

		Element root = doc.createElement("friendsMessage");
		doc.appendChild(root); // 将根元素添加到文档上

		Element consignid = doc.createElement("consignid");
		consignid.setAttribute("attr", name);
		root.appendChild(consignid);// 添加属性

		Element password = doc.createElement("password");
		password.setAttribute("attr", pwd);
		root.appendChild(password);// 添加属性

		long timestamp = System.currentTimeMillis();
		Element linkid = doc.createElement("username");
		linkid.setAttribute("attr", name);
		root.appendChild(linkid);// 添加属性

		Element productInfo = doc.createElement("friendsinfo");
		root.appendChild(productInfo);// 添加属性
		for (int i = 0; i < mHaoyou_Infos.size(); i++) {

			Element PicInfo = doc.createElement("friends");
			productInfo.appendChild(PicInfo);
			// 添加属性
			Element friend_name = doc.createElement("friendname");
			friend_name.setAttribute("attr", mHaoyou_Infos.get(i).getmName());
			PicInfo.appendChild(friend_name);

			Element friend_phone = doc.createElement("friendnum");
			friend_phone.setAttribute("attr", mHaoyou_Infos.get(i)
					.getmPhone_num());

			PicInfo.appendChild(friend_phone);
		}

		return toStringFromDoc(doc);
	}

	/*
	 * 把dom文件转换为xml字符串
	 */
	@SuppressLint("NewApi")
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

	// 添加好友
	public String PostFriendsInfo() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 5000);
		HttpPost httppost = new HttpPost("http://www.uuunm.com/addfriend.jsp");
		try {
			StringEntity se = new StringEntity(getProductDownAPIXMLString(),
					HTTP.UTF_8);
			se.setContentType("text/xml");
			httppost.setEntity(se);
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity resEntity = httpresponse.getEntity();
			InputStream is = resEntity.getContent();
			String address = new WorkingPhotoWebsiteParser()
					.parseWebsiteInfo(is);
			System.out.println("address===="+address);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	protected WebsiteInfo getResult(Element docEle) {
		WebsiteInfo result = null;
		result = new WebsiteInfo();
		NodeList nl = docEle.getElementsByTagName("opresult");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element entry = (Element) nl.item(i);
				Element resultElement = (Element) entry.getElementsByTagName(
						"result").item(0);
				result.result = getWebSiteNodeValue(resultElement);

				Element resultdescElement = (Element) entry
						.getElementsByTagName("resultdesc").item(0);
				result.resultdesc = getWebSiteNodeValue(resultdescElement);
			}
		}
		Log.d("zheng", "===getResult: " + result);
		return result;
	}

	protected String getWebSiteNodeValue(Element element) {
		String value = null;
		if (element != null) {
			String name = element.getNodeName();
			value = element.getAttribute("attr");
		}
		return value;
	}
}
