package com.cs.mms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.net.wifi.WifiManager;  
public class MMSSend {
    /** Called when the activity is first created. */
    public static String mmscUrl = "http://mmsc.monternet.com";
    private Context mContext;
    public void sendMms(Context context, String[] phonenumbers) {
    	mContext = context;
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        //�ر�WIFI
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {  

        	wifiManager.setWifiEnabled(false);  
        	
        	} 
        //��ȡ��ǰ����
        ConnectivityManager conManager= (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo info = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
        String currentAPN = info.getExtraInfo(); 
        conManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "mms");
        currentAPN = info.getExtraInfo(); 
        //ֻ��CMWAP���ܷ��Ͳ���
        if("cmwap".equals(currentAPN))
        {
        	for (int i = 0; i < phonenumbers.length; i++) {
        		sendMMS(phonenumbers[i]);
        	}
        }
    }
    void sendMMS(String phonenumber)
    {
    	final MMSInfo mms = new MMSInfo(mContext, phonenumber);//���͵��ֻ��
        String path = "file://mnt/sdcard//1.jpg";//�跢�͵�ͼƬ
        System.out.println("--->" + path);
        mms.addImagePart(path);// file://mnt/sdcard//1.jpg
        final MMSSender sender = new MMSSender();
        new Thread() {
                public void run() {
                        try {
                                byte[] res = sender.sendMMS(mContext,
                                                mms.getMMSBytes());
                                System.out.println("-==-=-=>>> " + res.toString());
                        } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                };
        }.start();
    }
}