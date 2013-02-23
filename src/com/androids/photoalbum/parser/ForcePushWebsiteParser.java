package com.androids.photoalbum.parser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.util.Log;

import com.androids.photoalbum.netinfo.ClassificationLocalSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.CollectionsLocalSiteInfo;
import com.androids.photoalbum.netinfo.CollectionsWebsiteInfo;
import com.androids.photoalbum.netinfo.ForcePushWebsiteInfo;
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

public class ForcePushWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar) {
		ForcePushWebsiteInfo forcePushWebsiteInfo = null;
		ForcePushWebsiteInfo.ProductInfo productInfo = null;
    	WebsiteInfo webinfo = new WebsiteInfo();
    	 
        try {
            URL url = new URL(urlPar);
            Log.d("peter", "parseWebsiteInfo url:" + url.toString());
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
                    Log.d("peter", "!SUCCESS");
                	return result;
                } else {
                    Log.d("peter", "SUCCESS");
                }
                NodeList nl = docEle.getElementsByTagName("PushContent");
                if (nl != null && nl.getLength() > 0) {
                	forcePushWebsiteInfo = new ForcePushWebsiteInfo();
                	forcePushWebsiteInfo.result = ServerInfo.SUCCESS;
                    Log.d("zheng", "nl.getLength():" + nl.getLength());
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        productInfo = new ForcePushWebsiteInfo.ProductInfo();
                        	entry = (Element)nl.item(i);
                        	productInfo = (ForcePushWebsiteInfo.ProductInfo)getProductInfo(entry);
                        
                        forcePushWebsiteInfo.newEntry(productInfo);
                    }
                }
                
                return forcePushWebsiteInfo;
            } else {
                Log.d("peter", "!HTTP_OK");
            	webinfo.result = ServerInfo.FAIL;
            	webinfo.resultdesc = ServerInfo.CONNECT_FAIL + " 返回码:" + responseCode;
            	return webinfo;
            }
        } catch (Exception e) {
            Log.d("peter", "Exceptions");
            e.printStackTrace();
        }

    	webinfo.result = ServerInfo.FAIL;
    	webinfo.resultdesc = ServerInfo.UNKNOWN_FAIL;
		return webinfo;
    }

	private WebsiteInfo getProductInfo(Element entry) {
		 ForcePushWebsiteInfo.ProductInfo nodeinfo = new ForcePushWebsiteInfo.ProductInfo();
        Element time = (Element)entry.getElementsByTagName("time").item(0);
        nodeinfo.time = getWebSiteNodeValue(time);
        
        NodeList nl = entry.getElementsByTagName("content");
        Element content = null;
        for (int i = 0; i < nl.getLength(); i++) {
            content = (Element)nl.item(i);
            nodeinfo.mContents.add(getWebSiteNodeContent(content));
        }
        
        return nodeinfo;
	}
	
    protected String getWebSiteNodeContent(Element element) {
        String value = null;
        if (element != null) {
           Node node = element.getFirstChild();
           if (node != null) {
               value = node.getNodeValue();
           }
        }
        return value;
    }
}
