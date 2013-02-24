package com.androids.photoalbum.parser;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.netinfo.WordsAddWebsiteInfo;

public class WorkingPhotoWebsiteParser extends WebsiteParser {

	public String parseWebsiteInfo(InputStream in) {
		WordsAddWebsiteInfo nameWebsiteInfo = null;
		WordsAddWebsiteInfo.ProductInfo nameInfo = null;
		WebsiteInfo webinfo = new WebsiteInfo();
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db = dbfactory.newDocumentBuilder();
			System.out.println("in===" + in.toString());
			// 解析这个出了问题，然后报了异常
			Document dom = db.parse(in);
			Element docEle = dom.getDocumentElement();
			WebsiteInfo result = getResult(docEle);
			System.out.println("result.resultdesc=="+result.resultdesc);
			if (!ServerInfo.SUCCESS.equals(result.result)) {
				return null;
			}
			NodeList nl = docEle.getElementsByTagName("photo");
			if (nl != null && nl.getLength() > 0) {
				Log.d("zheng", "nl.getLength():" + nl.getLength());
				for (int i = 0; i < nl.getLength(); i++) {
					Element entry = (Element) nl.item(i);
					return getNodeValue(entry);
				}
			}
			return null;
		} catch (Exception e) {
			System.out.println("yichangl ");
			e.printStackTrace();
		}

		System.out.println("yichangl____xia ");
		webinfo.result = ServerInfo.FAIL;
		webinfo.resultdesc = ServerInfo.UNKNOWN_FAIL;
		return null;
	}

	private WebsiteInfo getProductInfo(Element entry) {
		// Element outPath =
		// (Element)entry.getElementsByTagName("OutPath").item(0);

		WordsAddWebsiteInfo.ProductInfo nodeinfo = new WordsAddWebsiteInfo.ProductInfo();
		nodeinfo.photoaddress = getNodeValue(entry);
		return nodeinfo;
	}

	protected String getNodeValue(Element element) {
		String value = null;
		if (element != null) {
			String name = element.getNodeName();
			Log.d("zheng", "=========name:" + name);
			// value = element.getAttribute("attr");

			Node node = element.getFirstChild();
			if (node != null) {
				value = node.getNodeValue();
				Log.d("zheng", "=========value:" + value);
			}
		}
		return value;
	}
}
