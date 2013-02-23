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

public class ClassificationWebsiteParser extends WebsiteParser {
	
	@Override
    public WebsiteInfo parseWebsiteInfo(String urlPar, LocalSiteInfo localInfo) {
    	ClassificationWebSiteInfo.ParentClassInfo parentClassInfo = null;
    	ClassificationWebSiteInfo.ConclassInfo conclassInfo = null;
    	ClassificationWebSiteInfo.BaseInfo baseInfo = null;
    	ClassificationWebSiteInfo.ProductInfo productInfo = null;
    	WebsiteInfo classificationWebsiteInfo = null;
    	WebsiteInfo webinfo = new WebsiteInfo();
    	 
    	ClassificationLocalSiteInfo localSiteInfo = (ClassificationLocalSiteInfo)localInfo;
		String param = "?optype=" + localSiteInfo.optype + "&classid=" + localSiteInfo.classid
				+ "&classname=" + localSiteInfo.classname;
    	
        try {
            URL url = new URL(NetWorkUtils.BASE_URI + urlPar + param);
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
                
                if (localSiteInfo.optype.equals(ALL) || localSiteInfo.optype.equals(PARENTCLASS) || localSiteInfo.optype.equals(SINGLE)) {
                    NodeList nl = docEle.getElementsByTagName("parentclass");
                    if (nl != null && nl.getLength() > 0) {
                		classificationWebsiteInfo = new ClassificationWebSiteInfo();
                        Log.d("zheng", "nl.getLength():" + nl.getLength());
                        for (int i = 0; i < nl.getLength(); i++) {
                            Element entry = (Element)nl.item(i);
                            baseInfo = getWebsiteBaseInfo(entry);
                            parentClassInfo = new ClassificationWebSiteInfo.ParentClassInfo();
                            parentClassInfo.mBaseInfo = baseInfo;
                            Log.d("zheng", "Parent classid:" + baseInfo.classid);
                            Log.d("zheng", "Parent classname:" + baseInfo.classname);
                            
                            NodeList conclass_nl = entry.getElementsByTagName("sonclass");
                            
                            if (conclass_nl != null && conclass_nl.getLength() > 0) {
                                for (int j = 0; j < conclass_nl.getLength(); j++) {
                                    entry = (Element)conclass_nl.item(j);
                                    baseInfo = getWebsiteBaseInfo(entry);
                                    conclassInfo = new ClassificationWebSiteInfo.ConclassInfo();
                                    conclassInfo.mBaseInfo = baseInfo;
                                    Log.d("zheng", " Sonclass classid:" + baseInfo.classid);
                                    Log.d("zheng", " Sonclass classname:" + baseInfo.classname);
                                    
                                    NodeList product_nl = entry.getElementsByTagName("product");
                                    
                                    if (product_nl != null && product_nl.getLength() > 0) {
                                        for (int k = 0; k < product_nl.getLength(); k++) {
                                        	entry = (Element)product_nl.item(k);
                                        	productInfo = (ClassificationWebSiteInfo.ProductInfo)getProductInfo(entry);
                                        	conclassInfo.newEntry(productInfo);
//                                        	Log.d("zheng", " Product productid:" + productInfo.productid);
//                                            Log.d("zheng", " Product proname:" + productInfo.proname);
//                                            Log.d("zheng", " Product productnum:" + productInfo.productnum);
//                                            Log.d("zheng", " Product photoaddress:" + productInfo.photoaddress);
                                        }
                                    }
                                    parentClassInfo.newEntry(conclassInfo);
                                }
                            }
                            
                            classificationWebsiteInfo.newEntry(parentClassInfo);
                        }
                    }
                    
                    if (localSiteInfo.optype.equals(ALL)) {
                    	return classificationWebsiteInfo;
                    } else if (localSiteInfo.optype.equals(PARENTCLASS)) {
                    	classificationWebsiteInfo = parentClassInfo;
                    	return parentClassInfo;
                    } else if (localSiteInfo.optype.equals(SINGLE)) {
                    	classificationWebsiteInfo = conclassInfo;
                    	return conclassInfo;
                    }
                } 
//                else if (localSiteInfo.optype.equals(SINGLE)) {
//                	NodeList conclass_nl = docEle.getElementsByTagName("sonclass");
//                	Element entry = null;
//                    if (conclass_nl != null && conclass_nl.getLength() > 0) {
//                        for (int i = 0; i < conclass_nl.getLength(); i++) {
//                            entry = (Element)conclass_nl.item(i);
//                            conclassInfo = (ClassificationWebSiteInfo.ConclassInfo)getWebsiteInfo(entry);
//                            Log.d("zheng", " Child Node:" + conclassInfo.mBaseInfo.classname);
//                            return conclassInfo;
//                        }
//                    }
//                }
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

//	@Override
//	protected WebsiteInfo getWebsiteInfo(Element entry) {
//        Element classid = (Element)entry.getElementsByTagName("classid").item(0);
//        Element classname = (Element)entry.getElementsByTagName("classname").item(0);
//        
//        ClassificationWebSiteInfo.BaseInfo nodeinfo = new ClassificationWebSiteInfo.BaseInfo();
//        nodeinfo.classid = getWebSiteNodeValue(classid);
//        nodeinfo.classname = getWebSiteNodeValue(classname);
//        
//        return nodeinfo;
//	}
	
	protected ClassificationWebSiteInfo.BaseInfo getWebsiteBaseInfo(Element entry) {
      Element classid = (Element)entry.getElementsByTagName("classid").item(0);
      Element classname = (Element)entry.getElementsByTagName("classname").item(0);
      
      ClassificationWebSiteInfo.BaseInfo nodeinfo = new ClassificationWebSiteInfo.BaseInfo();
      nodeinfo.classid = getWebSiteNodeValue(classid);
      nodeinfo.classname = getWebSiteNodeValue(classname);
      
      return nodeinfo;
	}
	
	private WebsiteInfo getProductInfo(Element entry) {
        Element productid = (Element)entry.getElementsByTagName("productid").item(0);
        Element proname = (Element)entry.getElementsByTagName("proname").item(0);
        Element productnum = (Element)entry.getElementsByTagName("productnum").item(0);
        Element photoaddress = (Element)entry.getElementsByTagName("photoaddress").item(0);
        
        ClassificationWebSiteInfo.ProductInfo nodeinfo = new ClassificationWebSiteInfo.ProductInfo();
        nodeinfo.productid = getWebSiteNodeValue(productid);
        nodeinfo.proname = getWebSiteNodeValue(proname);
        nodeinfo.productnum = getWebSiteNodeValue(productnum);
        nodeinfo.photoaddress = getWebSiteNodeValue(photoaddress);
        return nodeinfo;
	}
}
