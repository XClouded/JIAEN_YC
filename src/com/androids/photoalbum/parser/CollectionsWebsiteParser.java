package com.androids.photoalbum.parser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.util.Log;

import com.androids.photoalbum.netinfo.ClassificationLocalSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.CollectionsLocalSiteInfo;
import com.androids.photoalbum.netinfo.CollectionsWebsiteInfo;
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

public class CollectionsWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar) {
		CollectionsWebsiteInfo collectionsWebsiteInfo = null;
		CollectionsWebsiteInfo.ProductInfo productInfo = null;
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
                
                NodeList nl = docEle.getElementsByTagName("collection");
                if (nl != null && nl.getLength() > 0) {
                	collectionsWebsiteInfo = new CollectionsWebsiteInfo();
                    Log.d("zheng", "nl.getLength():" + nl.getLength());
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        productInfo = new CollectionsWebsiteInfo.ProductInfo();
                    	entry = (Element)nl.item(i);
                    	productInfo = (CollectionsWebsiteInfo.ProductInfo)getProductInfo(entry);
                        
                        collectionsWebsiteInfo.newEntry(productInfo);
                    }
                }
                
                return collectionsWebsiteInfo;
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
		CollectionsWebsiteInfo.ProductInfo nodeinfo = new CollectionsWebsiteInfo.ProductInfo();
        Element id = (Element)entry.getElementsByTagName("id").item(0);
        Element photocount = (Element)entry.getElementsByTagName("photocount").item(0);
        Element proid = (Element)entry.getElementsByTagName("proid").item(0);

        nodeinfo.id = getWebSiteNodeValue(id);
        nodeinfo.photocount = getWebSiteNodeValue(photocount);
        nodeinfo.productid = getWebSiteNodeValue(proid);
        
        Element photo = (Element)entry.getElementsByTagName("photo").item(0);
        Element photoaddress = (Element)photo.getElementsByTagName("photoaddress").item(0);
        nodeinfo.photoaddress = getWebSiteNodeValue(photoaddress);
        
        return nodeinfo;
	}
}
