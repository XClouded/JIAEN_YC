package com.androids.photoalbum.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.photoalbum.R;
import com.androids.photoalbum.parser.WorkingPhotoWebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.MultiMediaMessageActivity;
import com.androids.photoalbum.tab.ui.TabActivityGroup;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.BASE64Encoder;
import com.androids.photoalbum.utils.Utils;

public class PictureCombineActivity extends BaseActivity implements OnItemClickListener {
	private Button mCombine;
	private Button mCamera;
	private GridView mGridView;
	private GridAdapter mAdapter;
	int i = 0;
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("zheng", "" + i);
		i++;
	    mGridView = (GridView)findViewById(R.id.grid_list);
	    mAdapter = new GridAdapter(this);
	    mGridView.setAdapter(mAdapter);
	    mGridView.setOnItemClickListener(this);
	}
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.pic_combine_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "", "", "返回");
		mCombine = (Button)findViewById(R.id.combine);
		mCombine.setOnClickListener(this);
		mCombine.setOnTouchListener(this);
		
		mCamera = (Button)findViewById(R.id.camera);
		mCamera.setOnClickListener(this);
		mCamera.setOnTouchListener(this);
		
		mImageWidth = (int)mResources.getDimension(R.dimen.favorite_image_with);
	    mImageHeigth = (int)mResources.getDimension(R.dimen.favorite_image_height);
	    
	    mGridView = (GridView)findViewById(R.id.grid_list);
	    mAdapter = new GridAdapter(this);
	    mGridView.setAdapter(mAdapter);
	    mGridView.setOnItemClickListener(this);
	    
	    IntentFilter filter = new IntentFilter(MultiMediaMessageActivity.ACTION_PICTURE_SELECTED);
	    registerReceiver(mBroadReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadReceiver);
		super.onDestroy();
	}
	
	@Override
	protected void handleTitleBarEvent(int paramInt) {
		MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
	}
	
	int picCount = 0;
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.combine:
//				if (StaticInfo.mPassword == null || StaticInfo.mPassword.length() == 0) {
//					Intent intent = new Intent(this, LoginActivity.class);
//					intent.putExtra("isactivity", true);
//					startActivity(intent);
//					return;
//				}
				
				if (mPhotoArrayList.size() == 0) {
				    Utils.notifyToUser("请选择图片", MainTabActivity.getCurrentActivityGroup());
//					MainTabActivity.getCurrentActivityGroup().showDialog(DIALOG_CONFIRM_ADD_PHOTO);
					return;
				}
				complete();
				break;
			case R.id.camera:
				Intent intent = new Intent(this, SelectPictureActivity.class);
				MainTabActivity.getCurrentActivityGroup().setNextActivity(intent, "SelectPictureActivity");
				break;
			default:
				super.onClick(view);
				break;
		}
	}
	
	private ArrayList mBase64Pictures = new ArrayList();
	private BroadcastReceiver mBroadReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MultiMediaMessageActivity.ACTION_PICTURE_SELECTED.equals(intent.getAction())) {
				Bundle extras = intent.getExtras();
//				if (extras != null) {
					Bitmap photo = TabActivityGroup.bmp;//extras.getParcelable("data");

					
//					int height = mPicutureList.getHeight();
					int height = mGridView.getHeight();
					height = (int)getResources().getDimension(R.dimen.multi_msg_height);
					FrameLayout image = (FrameLayout)mInflater.inflate(R.layout.gridview_item, null);
					photo = Utils.getResizedBitmap(photo, mImageWidth, mImageHeigth);
					ImageView imageView = (ImageView)image.findViewById(R.id.image);
					imageView.setImageBitmap(photo);
					ImageView delIcon = (ImageView)image.findViewById(R.id.mText);
					delIcon.setVisibility(View.VISIBLE);
					
//					mPicutureList.addView(image);
//					mPicutureList.addView(image);
					mPhotoArrayList.add(image);
					
					final ByteArrayOutputStream os = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
					byte[] imgbytes = os.toByteArray();
					String content = new BASE64Encoder().encode(imgbytes);
					mBase64Pictures.add(content);
					
					mAdapter.notifyDataSetChanged();
//				}
			}
		}
	};
	
	private ArrayList mPhotoArrayList = new ArrayList();
    class GridAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater mInflater;

        public GridAdapter(Context c) {
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
        	return (View)mPhotoArrayList.get(position);
        }
    }
    
	
	private void complete() {
		Runnable run = new Runnable() {
			
			public void run() {
				showProgessBarDialog("Loading...");
				postGetCombination();
				pd.dismiss();
			}
		};
		
		new Thread(run).start();
	}
	
//	private Bitmap mCombinedBitmap = null;
	public static String mCombinedAddress = null;
	private void postGetCombination() {
	       HttpClient httpclient = new DefaultHttpClient();
	       HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 5000);
	        HttpPost httppost = new HttpPost("http://www.uuunm.com/getMultiImg.jsp");

	        try {
	        	Log.d("zheng", getProductDownAPIXMLString());
	            StringEntity se = new StringEntity(getProductDownAPIXMLString(), HTTP.UTF_8);
	            se.setContentType("text/xml");
	            httppost.setEntity(se);

	            HttpResponse httpresponse = httpclient.execute(httppost);
	            HttpEntity resEntity = httpresponse.getEntity();
	            InputStream is = resEntity.getContent();
	            String address = new WorkingPhotoWebsiteParser().parseWebsiteInfo(is);
	            if (address == null) {
	            	Utils.notifyToUser("合成图片超过90K了, 无法下载", PictureCombineActivity.this);
//	            	mCombinedBitmap = null;
	            	mCombinedAddress = null;
	            } else {
//	            	mCombinedBitmap = Utils.getBitMapFromNetwork(address, this);
	            	mCombinedAddress = address;
	            	
	            	Runnable run = new Runnable() {
						
						public void run() {
						    Log.d("zheng", "图片合成成功");
//							MainTabActivity.getCurrentActivityGroup().showDialog(DIALOG_COMBINE_SUCESS);
							MainTabActivity.getCurrentActivityGroup().notifyToUser("图片合成成功！");
							Intent intent = new Intent(PictureCombineActivity.this, PictureReviewActivity.class);
							MainTabActivity.getCurrentActivityGroup().setNextActivity(intent, "PictureReviewActivity");
						}
					};
					
					MainTabActivity.getCurrentActivityGroup().runOnUiThread(run);
	            }
	            Log.d("zheng", "the final working address is:" + address);
	            
//	            Utils.notifyToUser(mCombinedAddress, MainTabActivity.getCurrentActivityGroup());
	            Log.d("zheng", "Status OK: \n" + mCombinedAddress);

	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	            notifyToUser(e.getMessage());
//		            tv.setText("Status NOT OK: \n" + e.getMessage());
	        } catch (IOException e) {
	            e.printStackTrace();
	            notifyToUser(e.getMessage());
//		            tv.setText("Status NOT OK: \n" + e.getMessage());
	        }
		}
	
	public String getProductDownAPIXMLString() {
		Text content ;
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
		consignid.setAttribute("attr", StaticInfo.mUsername);
		root.appendChild(consignid);// 添加属性
		
		Element password = doc.createElement("password");
		password.setAttribute("attr", StaticInfo.mPassword);
		root.appendChild(password);// 添加属性
		
		long timestamp = System.currentTimeMillis();
		Element linkid = doc.createElement("linkid");
		linkid.setAttribute("attr", String.valueOf(timestamp));
		root.appendChild(linkid);// 添加属性
		
		Element productInfo = doc.createElement("productInfo");
		root.appendChild(productInfo);// 添加属性
		for (int i = 0; i < mBase64Pictures.size(); i++) {
			
			Element PicInfo = doc.createElement("PicInfo");
			productInfo.appendChild(PicInfo);// 添加属性
			Element imagecontent = doc.createElement("content");
			content = doc.createTextNode((String)mBase64Pictures.get(i));
			imagecontent.appendChild(content);
			PicInfo.appendChild(imagecontent);
		}
		
		return toStringFromDoc(doc);
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
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mBase64Pictures.remove(position);
		mPhotoArrayList.remove(position);
		mAdapter.notifyDataSetChanged();
		Log.d("zheng", "onItemClick");
	}
}