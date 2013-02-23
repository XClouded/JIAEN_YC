package com.androids.photoalbum.parser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.androids.photoalbum.netinfo.EffectWebsiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.utils.NetWorkUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class EffectWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar) {
		EffectWebsiteInfo effectWebsiteInfo = null;
		EffectWebsiteInfo.EffectInfo effectInfo = null;
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
                Log.d("zheng", "dom: " + dom.toString());
                Element docEle = dom.getDocumentElement();

                WebsiteInfo result = getResult(docEle);
                if (!ServerInfo.SUCCESS.equals(result.result)) {
                	return result;
                }
                
                NodeList nl = docEle.getElementsByTagName("effectsaddress");
                if (nl != null && nl.getLength() > 0) {
                	effectWebsiteInfo = new EffectWebsiteInfo();
                    Log.d("zheng", "nl.getLength():" + nl.getLength());
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        effectInfo = new EffectWebsiteInfo.EffectInfo();
//                    	entry = (Element)nl.item(i);
                    	effectInfo = (EffectWebsiteInfo.EffectInfo)getProductInfo(entry);
                        effectWebsiteInfo.newEntry(effectInfo);
                    }
                }
                
                return effectWebsiteInfo;
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
//        Element effectsaddress = (Element)entry.getElementsByTagName("effectsaddress").item(0);
		
        EffectWebsiteInfo.EffectInfo nodeinfo = new EffectWebsiteInfo.EffectInfo();
        nodeinfo.effectsaddress = getWebSiteNodeValue(entry);
        return nodeinfo;
	}
}
