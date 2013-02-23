package com.androids.photoalbum.parser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.util.Log;

import com.androids.photoalbum.netinfo.ClassificationLocalSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.LocalSiteInfo;
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

public class SearchWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar) {
		SearchWebsiteInfo searchWebsiteInfo = null;
    	SearchWebsiteInfo.ProductInfo productInfo = null;
//    	ClassificationWebSiteInfo.ConclassInfo conclassInfo = null;
//    	ClassificationWebSiteInfo.BaseInfo baseInfo = null;
//    	ClassificationWebSiteInfo.ProductInfo productInfo = null;
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
                
                NodeList nl = docEle.getElementsByTagName("product");
                if (nl != null && nl.getLength() > 0) {
                	searchWebsiteInfo = new SearchWebsiteInfo();
                    Log.d("zheng", "nl.getLength():" + nl.getLength());
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        productInfo = new SearchWebsiteInfo.ProductInfo();
//                    	entry = (Element)nl.item(i);
                    	productInfo = (SearchWebsiteInfo.ProductInfo)getProductInfo(entry);
                        
                        searchWebsiteInfo.newEntry(productInfo);
                    }
                }
                
                return searchWebsiteInfo;
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
        Element productid = (Element)entry.getElementsByTagName("productid").item(0);
        Element parentclass = (Element)entry.getElementsByTagName("parentclass").item(0);
        Element sonclass = (Element)entry.getElementsByTagName("sonclass").item(0);
        Element proname = (Element)entry.getElementsByTagName("proname").item(0);
        Element productnum = (Element)entry.getElementsByTagName("productnum").item(0);
        Element istj = (Element)entry.getElementsByTagName("istj").item(0);
        Element ishot = (Element)entry.getElementsByTagName("ishot").item(0);
        Element isyn = (Element)entry.getElementsByTagName("isyn").item(0);
        Element hits = (Element)entry.getElementsByTagName("hits").item(0);
        Element downhits = (Element)entry.getElementsByTagName("downhits").item(0);
        Element addtime = (Element)entry.getElementsByTagName("addtime").item(0);
        
        Element photo = (Element)entry.getElementsByTagName("photo").item(0);
        Element photoaddress = (Element)photo.getElementsByTagName("photoaddress").item(0);
		
        SearchWebsiteInfo.ProductInfo nodeinfo = new SearchWebsiteInfo.ProductInfo();
        nodeinfo.productid = getWebSiteNodeValue(productid);
        nodeinfo.parentclass = getWebSiteNodeValue(parentclass);
        nodeinfo.sonclass = getWebSiteNodeValue(sonclass);
        nodeinfo.proname = getWebSiteNodeValue(proname);
        nodeinfo.productnum = getWebSiteNodeValue(productnum);
        nodeinfo.istj = getWebSiteNodeValue(istj);
        nodeinfo.ishot = getWebSiteNodeValue(ishot);
        nodeinfo.isyn = getWebSiteNodeValue(isyn);
        nodeinfo.hits = getWebSiteNodeValue(hits);
        nodeinfo.downhits = getWebSiteNodeValue(downhits);
        nodeinfo.addtime = getWebSiteNodeValue(addtime);
        nodeinfo.photoaddress = getWebSiteNodeValue(photoaddress);
        return nodeinfo;
	}
}
