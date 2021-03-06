package com.androids.photoalbum.parser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.androids.photoalbum.netinfo.ClassificationLocalSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.LocalSiteInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.utils.MicroBlogUtils;

public abstract class WebsiteParser {
    public final static String ALL = "all";
    public final static String PARENTCLASS = "parentclass";
    public final static String SINGLE = "single";
    
    public WebsiteInfo parseWebsiteInfo(String urlPar, LocalSiteInfo localInfo) {
    	Log.d("zheng", "call parent parseWebsiteInfo 1");
    	return null;
    }
    
    public WebsiteInfo parseWebsiteInfo(String urlPar) {
    	Log.d("zheng", "call parent parseWebsiteInfo 2");
    	return null;
    }
    
//    protected abstract WebsiteInfo getWebsiteInfo(Element entry);
    
	protected ClassificationWebSiteInfo.BaseInfo getWebsiteBaseInfo(Element entry) {
        Element classid = (Element)entry.getElementsByTagName("classid").item(0);
        Element classname = (Element)entry.getElementsByTagName("classname").item(0);
        
        ClassificationWebSiteInfo.BaseInfo nodeinfo = new ClassificationWebSiteInfo.BaseInfo();
        nodeinfo.classid = getWebSiteNodeValue(classid);
        nodeinfo.classname = getWebSiteNodeValue(classname);
        
        return nodeinfo;
	}

    protected String getWebSiteNodeValue(Element element) {
        String value = null;
        if (element != null) {
        	String name = element.getNodeName();
//        	Log.d("zheng", "=========name:" + name);
        	value = element.getAttribute("attr");
//        	Log.d("zheng", "=========value:" + value);
        	
//           Node node = element.getFirstChild();
//           if (node != null) {
//               blogInfo = node.getNodeValue();
//           }
        }
        return value;
    }
    
    protected WebsiteInfo getResult(Element docEle) {
    	WebsiteInfo result = null;
    	result = new WebsiteInfo();
        NodeList nl = docEle.getElementsByTagName("opresult");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element entry = (Element)nl.item(i);
                Element resultElement = (Element)entry.getElementsByTagName("result").item(0);
                result.result = getWebSiteNodeValue(resultElement);
                
                Element resultdescElement = (Element)entry.getElementsByTagName("resultdesc").item(0);
                result.resultdesc = getWebSiteNodeValue(resultdescElement);
            }
        }
        Log.d("zheng", "===getResult: " + result);
        return result;
    }
}
