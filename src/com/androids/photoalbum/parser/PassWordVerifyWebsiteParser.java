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
import com.androids.photoalbum.netinfo.LocalSiteInfo;
import com.androids.photoalbum.netinfo.MusicWebsiteInfo;
import com.androids.photoalbum.netinfo.PassWordVerifyLocalSiteInfo;
import com.androids.photoalbum.netinfo.PassWordVerifyWebsiteInfo;
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

public class PassWordVerifyWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar) {
		PassWordVerifyWebsiteInfo webinfo = new PassWordVerifyWebsiteInfo();
    	
        try {
            URL url = new URL(urlPar);
            Log.d("zheng", "getBlogInfoList url:" + url.toString());
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                Log.d("zheng", "====parseWebsiteInfo: " + in.toString());
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();
                
                WebsiteInfo result = getResult(docEle);
                if (!ServerInfo.SUCCESS.equals(result.result)) {
                	return result;
                }
                
                NodeList nl = docEle.getElementsByTagName("userinfo");
                if (nl != null && nl.getLength() > 0) {
                    Log.d("zheng", "nl.getLength():" + nl.getLength());
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        webinfo = (PassWordVerifyWebsiteInfo)getProductInfo(entry);
                    }
                }
                
                webinfo.result = result.result;
                webinfo.resultdesc = result.resultdesc;
                return webinfo;
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
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar, LocalSiteInfo localInfo) {
		PassWordVerifyWebsiteInfo webinfo = new PassWordVerifyWebsiteInfo();
    	PassWordVerifyLocalSiteInfo localSiteInfo = (PassWordVerifyLocalSiteInfo)localInfo;
		String param = "?username=" + localSiteInfo.username + "&pwd=" + localSiteInfo.pwd;
    	
        try {
            URL url = new URL(NetWorkUtils.BASE_URI + urlPar + param);
            Log.d("zheng", "getBlogInfoList url:" + url.toString());
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                Log.d("zheng", "====parseWebsiteInfo: " + in.toString());
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();
                
                WebsiteInfo result = getResult(docEle);
                if (!ServerInfo.SUCCESS.equals(result.result)) {
                	return result;
                }
                
                NodeList nl = docEle.getElementsByTagName("userinfo");
                if (nl != null && nl.getLength() > 0) {
                    Log.d("zheng", "nl.getLength():" + nl.getLength());
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        webinfo = (PassWordVerifyWebsiteInfo)getProductInfo(entry);
                    }
                }
                
                webinfo.result = result.result;
                webinfo.resultdesc = result.resultdesc;
                return webinfo;
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
	
	private WebsiteInfo getProductInfo(Element entry) {
        Element userid = (Element)entry.getElementsByTagName("userid").item(0);
        Element username = (Element)entry.getElementsByTagName("username").item(0);
		
        PassWordVerifyWebsiteInfo nodeinfo = new PassWordVerifyWebsiteInfo();
        nodeinfo.userid = getWebSiteNodeValue(userid);
        nodeinfo.username = getWebSiteNodeValue(username);
        return nodeinfo;
	}
}
