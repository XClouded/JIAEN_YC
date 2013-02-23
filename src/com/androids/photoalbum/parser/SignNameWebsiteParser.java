package com.androids.photoalbum.parser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.util.Log;

import com.androids.photoalbum.netinfo.ClassificationLocalSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.LocalSiteInfo;
import com.androids.photoalbum.netinfo.SearchLocalSiteInfo;
import com.androids.photoalbum.netinfo.SearchWebsiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.SignNameWebsiteInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.utils.NetWorkUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SignNameWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar) {
		SignNameWebsiteInfo signNameWebsiteInfo = null;
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
                
                NodeList nl = docEle.getElementsByTagName("OutPath");
                if (nl != null && nl.getLength() > 0) {
                	signNameWebsiteInfo = new SignNameWebsiteInfo();
                    Log.d("zheng", "nl.getLength():" + nl.getLength());
                    Element entry = (Element)nl.item(0);
                    signNameWebsiteInfo.yishu = getWebSiteNodeValue(entry);
                    Log.d("zheng", "yishu: " + signNameWebsiteInfo.yishu);
                    
                    entry = (Element)nl.item(1);
                    signNameWebsiteInfo.fanti = getWebSiteNodeValue(entry);
                    Log.d("zheng", "fanti: " + signNameWebsiteInfo.fanti);
                    
                    entry = (Element)nl.item(2);
                    signNameWebsiteInfo.gongwen = getWebSiteNodeValue(entry);
                    Log.d("zheng", "gongwen: " + signNameWebsiteInfo.gongwen);
                    
                    entry = (Element)nl.item(3);
                    signNameWebsiteInfo.huati = getWebSiteNodeValue(entry);
                    Log.d("zheng", "huati: " + signNameWebsiteInfo.huati);
                }
                
                return signNameWebsiteInfo;
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

//	private WebsiteInfo getWebsiteInfo(Element entry) {
//		NodeList nl = entry.getElementsByTagName("OutPath");
//        if (nl != null && nl.getLength() > 0) {
//        	searchWebsiteInfo = new SearchWebsiteInfo();
//            Log.d("zheng", "nl.getLength():" + nl.getLength());
//            for (int i = 0; i < nl.getLength(); i++) {
//                Element entry = (Element)nl.item(i);
//                productInfo = new SearchWebsiteInfo.ProductInfo();
//            	entry = (Element)nl.item(i);
//            	productInfo = (SearchWebsiteInfo.ProductInfo)getProductInfo(entry);
//                
//                searchWebsiteInfo.newEntry(productInfo);
//            }
//        }
//        
//		Element yishu = (Element)nodeList.item(0);
//		Element fanti = (Element)nodeList.item(1);
//		Element gongwen = (Element)nodeList.item(2);
//		Element huati = (Element)nodeList.item(3);
		
//        Element productid = (Element)entry.getElementsByTagName("OutPath").item(0);
//        Element parentclass = (Element)entry.getElementsByTagName("parentclass").item(0);
//        Element sonclass = (Element)entry.getElementsByTagName("sonclass").item(0);
//        Element proname = (Element)entry.getElementsByTagName("proname").item(0);
//        Element productnum = (Element)entry.getElementsByTagName("productnum").item(0);
//        Element istj = (Element)entry.getElementsByTagName("istj").item(0);
//        Element ishot = (Element)entry.getElementsByTagName("ishot").item(0);
//        Element isyn = (Element)entry.getElementsByTagName("isyn").item(0);
//        Element hits = (Element)entry.getElementsByTagName("hits").item(0);
//        Element downhits = (Element)entry.getElementsByTagName("downhits").item(0);
//        Element addtime = (Element)entry.getElementsByTagName("addtime").item(0);
//        
//        Element photo = (Element)entry.getElementsByTagName("photo").item(0);
//        Element photoaddress = (Element)photo.getElementsByTagName("photoaddress").item(0);
		
//		SignNameWebsiteInfo nodeinfo = new SignNameWebsiteInfo();
//        nodeinfo.yishu = getWebSiteNodeValue(yishu);
//        nodeinfo.fanti = getWebSiteNodeValue(fanti);
//        nodeinfo.gongwen = getWebSiteNodeValue(gongwen);
//        nodeinfo.huati = getWebSiteNodeValue(huati);
//        nodeinfo.productnum = getWebSiteNodeValue(productnum);
//        nodeinfo.istj = getWebSiteNodeValue(istj);
//        nodeinfo.ishot = getWebSiteNodeValue(ishot);
//        nodeinfo.isyn = getWebSiteNodeValue(isyn);
//        nodeinfo.hits = getWebSiteNodeValue(hits);
//        nodeinfo.downhits = getWebSiteNodeValue(downhits);
//        nodeinfo.addtime = getWebSiteNodeValue(addtime);
//        nodeinfo.photoaddress = getWebSiteNodeValue(photoaddress);
//        return nodeinfo;
//	}
	
    protected String getWebSiteNodeValue(Element element) {
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
