package com.androids.photoalbum.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.androids.photoalbum.netinfo.ServerInfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.util.List;  
 
  
public class NetWorkUtils {
	//测试账户用户名15647424490 密码111111 
	public final static String BASE_URI = "http://www.uuunm.com/";
	public final static String LOGO_URI = "http://www.uuunm.com/Product/logo.png";
    public final static String TAG = "NetWorkUtils";
    public final static String IS_EXIST_USER = "IsExistUsert.jsp";
    public final static String GET_CLASS   = "getClass.jsp";
    public final static String GET_PRODUCT_COUNT = "getProductCount.jsp";
    public final static String IS_EXIST_NEW_PRODUCT  = "IsExistNewProduct.jsp";
    public final static String GET_PRODUCTS  = "getProducts.jsp";
    public final static String PRODUCT_DOWN = "ProductDown.jsp";
    public final static String GET_COLOR_WORDS = "getColorWords.jsp";
    public final static String GET_PRODUCTS_SELECT = "getProductsSelect.jsp";
    public final static String GET_MUSIC = "getMusic.jsp";
    public final static String COLLECTION = "Collection.jsp";
    public final static String GET_NAME = "getName.jsp";
    public final static String GET_STENCIL = "getStencil.jsp";
    public final static String GET_FRAME = "getFrame.jsp";
    public final static String GET_EFFECTS = "getEffects.jsp";
    public final static String DELLCOLLECTION = "Dellcollection.jsp";
    public final static String ADD_COLLECTION = "AddCollection.jsp";
    public final static String GET_COMBINATION = "getCombination.jsp";
    public final static String FORCE_PUSH = "http://www.uuunm.com/PushContent.jsp?time=";
    
    private static ServerInfo post(String uri, List<NameValuePair> nameValuePairs) {
        ServerInfo info = new ServerInfo();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpRequest = new HttpPost(MicroBlogUtils.BASE_URI + uri);
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpRequest);

            // 连接服务器
            info.mServerInputStream = httpResponse.getEntity().getContent();
            info.mStatusCode = httpResponse.getStatusLine().getStatusCode();
//            String result = EntityUtils.toString(httpResponse.getEntity());
            int code = info.mStatusCode;
            Log.d("zheng", "statusCode:" + code);
//            info.mServerResponse = result;
//            Log.d("zheng", "the buffer" + result);

            // 读取所有头数据
            Header[] header = httpResponse.getAllHeaders();
            HashMap<String, String> hm = new HashMap<String, String>();
            for (int i = 0; i < header.length; i++) {
                hm.put(header[i].getName(), header[i].getValue());
                Log.d("zheng", "header " + i + " name:" + header[i].getName());
                Log.d("zheng", "header " + i + " value:" + header[i].getValue());
            }

            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            Log.d(TAG, "NetWorkUtils push()" + e.getMessage().toString());
            e.printStackTrace();
        }

        return info;
    }
    
	public static boolean checkNet(Context context) {// ��ȡ�ֻ�����l�ӹ�����󣨰�(��wi-fi,net��l�ӵĹ��?
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// ��ȡ����l�ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// �жϵ�ǰ�����Ƿ��Ѿ�l��
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
