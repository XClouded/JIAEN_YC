package com.androids.photoalbum.parser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androids.photoalbum.netinfo.ClassificationLocalSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.ExistNewProductLocalSiteInfo;
import com.androids.photoalbum.netinfo.LocalSiteInfo;
import com.androids.photoalbum.netinfo.PassWordVerifyLocalSiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.utils.MicroBlogUtils;
import com.androids.photoalbum.utils.NetWorkUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ExistNewProductWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar, LocalSiteInfo localInfo) {
    	WebsiteInfo webinfo = new WebsiteInfo();
    	ExistNewProductLocalSiteInfo localSiteInfo = (ExistNewProductLocalSiteInfo)localInfo;
		String param = "?addtime=" + localSiteInfo.addtime;
    	
        try {
            URL url = new URL(NetWorkUtils.BASE_URI + urlPar + param);
            Log.d("zheng", "getBlogInfoList url:" + url.toString());
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();
                
                return getResult(docEle);
            } else {
            	webinfo.result = ServerInfo.FAIL;
            	webinfo.resultdesc = ServerInfo.CONNECT_FAIL + " 返回码:" + responseCode;
            	return webinfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    	webinfo.result = ServerInfo.FAIL;
    	webinfo.resultdesc = ServerInfo.UNKNOWN_FAIL;
		return webinfo;
    }
}
