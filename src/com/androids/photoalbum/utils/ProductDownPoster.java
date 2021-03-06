package com.androids.photoalbum.utils;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import android.util.Log;

public class ProductDownPoster {
	public static String getProductDownAPIXMLString(String _productid, String _musicid, String base64content) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();

		Element root = doc.createElement("MMessage");
		doc.appendChild(root); // 将根元素添加到文档上

		Element consignid = doc.createElement("consignid");
		consignid.setAttribute("attr", "kehuduan");
		root.appendChild(consignid);// 添加属性
		
		Element password = doc.createElement("password");
		password.setAttribute("attr", "123456");
		root.appendChild(password);// 添加属性
		
		Element desttermid = doc.createElement("desttermid");
		desttermid.setAttribute("attr", "13081041234");
		root.appendChild(desttermid);// 添加属性
		
		long timestamp = System.currentTimeMillis();
		Element linkid = doc.createElement("linkid");
		linkid.setAttribute("attr", String.valueOf(timestamp));
		root.appendChild(linkid);// 添加属性
		
		Element subject = doc.createElement("subject");
		subject.setAttribute("attr", "您好");
		root.appendChild(subject);// 添加属性
		
		Element productid = doc.createElement("productid");
		productid.setAttribute("attr", _productid);
		root.appendChild(productid);// 添加属性
		
		Element musicid = doc.createElement("musicid");
		musicid.setAttribute("attr", _musicid);
		root.appendChild(musicid);// 添加属性
		
		Element textcontent = doc.createElement("textcontent");
		Text content = doc.createTextNode(base64content);
		textcontent.appendChild(content);
		root.appendChild(textcontent);// 添加属性
		String result = doc.getTextContent();
		
		String str = toStringFromDoc(doc);
       Log.d("zheng", "the XML:" + str);
		return result;
	} 
	
	public static String getProductDownNewAPIXMLString(String _productid, String _musicid, String base64content) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();

		Element root = doc.createElement("MMessage");
		doc.appendChild(root); // 将根元素添加到文档上

		Element consignid = doc.createElement("consignid");
		consignid.setAttribute("attr", "kehuduan");
		root.appendChild(consignid);// 添加属性
		
		Element password = doc.createElement("password");
		password.setAttribute("attr", "123456");
		root.appendChild(password);// 添加属性
		
		Element desttermid = doc.createElement("desttermid");
		desttermid.setAttribute("attr", "13081041234");
		root.appendChild(desttermid);// 添加属性
		
		long timestamp = System.currentTimeMillis();
		Element linkid = doc.createElement("linkid");
		linkid.setAttribute("attr", String.valueOf(timestamp));
		root.appendChild(linkid);// 添加属性
		
		Element subject = doc.createElement("subject");
		subject.setAttribute("attr", "您好");
		root.appendChild(subject);// 添加属性
		
		Element productid = doc.createElement("productid");
		productid.setAttribute("attr", _productid);
		root.appendChild(productid);// 添加属性
		
		Element musicid = doc.createElement("musicid");
		musicid.setAttribute("attr", _musicid);
		root.appendChild(musicid);// 添加属性
		
		Element textcontent = doc.createElement("textcontent");
		Text content = doc.createTextNode(base64content);
		textcontent.appendChild(content);
		root.appendChild(textcontent);// 添加属性
		String result = doc.getTextContent();
		
		String str = toStringFromDoc(doc);
       Log.d("zheng", "the XML:" + str);
		return result;
	} 
	
	/*
	 * 把dom文件转换为xml字符串
	 */
	public static String toStringFromDoc(Document document) {
		String result = null;

		if (document != null) {
			StringWriter strWtr = new StringWriter();
			StreamResult strResult = new StreamResult(strWtr);
			TransformerFactory tfac = TransformerFactory.newInstance();
			try {
				javax.xml.transform.Transformer t = tfac.newTransformer();
				t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty(OutputKeys.METHOD, "xml"); // xml, html,
																// text
				t.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "4");
				t.transform(new DOMSource(document.getDocumentElement()),
						strResult);
			} catch (Exception e) {
				System.err.println("XML.toString(Document): " + e);
			}
			result = strResult.getWriter().toString();
			try {
				strWtr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
//	try {
//			FileOutputStream fos = new FileOutputStream(outfile);
//			OutputStreamWriter outwriter = new OutputStreamWriter(fos);
//			// ((XmlDocument)doc).write(outwriter); //出错！
//			callWriteXmlFile(doc, outwriter, "gb2312");
//			outwriter.close();
//			fos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
}