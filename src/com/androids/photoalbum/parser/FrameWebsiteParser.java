package com.androids.photoalbum.parser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.util.Log;

import com.androids.photoalbum.netinfo.ClassificationLocalSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.FrameWebsiteInfo;
import com.androids.photoalbum.netinfo.LocalSiteInfo;
import com.androids.photoalbum.netinfo.MusicLocalSiteInfo;
import com.androids.photoalbum.netinfo.MusicWebsiteInfo;
import com.androids.photoalbum.netinfo.SearchLocalSiteInfo;
import com.androids.photoalbum.netinfo.SearchWebsiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.utils.NetWorkUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FrameWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar) {
		FrameWebsiteInfo FrameWebsiteInfo = null;
		FrameWebsiteInfo.FrameInfo FrameInfo = null;
    	WebsiteInfo webinfo = new WebsiteInfo();
    	 
        try {
            URL url = new URL(NetWorkUtils.BASE_URI + urlPar);
            Log.d("zheng", "parseWebsiteInfo url:" + url.toString());
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                WebsiteInfo result = getResult(docEle);
                if (!ServerInfo.SUCCESS.equals(result.result)) {
                	return result;
                }
                
                NodeList nl = docEle.getElementsByTagName("frameaddress");
                if (nl != null && nl.getLength() > 0) {
                	FrameWebsiteInfo = new FrameWebsiteInfo();
                    Log.d("zheng", "nl.getLength():" + nl.getLength());
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        FrameInfo = new FrameWebsiteInfo.FrameInfo();
//                    	entry = (Element)nl.item(i);
                    	FrameInfo = (FrameWebsiteInfo.FrameInfo)getProductInfo(entry);
                        
                        FrameWebsiteInfo.newEntry(FrameInfo);
                    }
                }
                
                return FrameWebsiteInfo;
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
//        Element frameaddress = (Element)entry.getElementsByTagName("frameaddress").item(0);
		
        FrameWebsiteInfo.FrameInfo nodeinfo = new FrameWebsiteInfo.FrameInfo();
        nodeinfo.frameaddress = getWebSiteNodeValue(entry);
        return nodeinfo;
	}
}
