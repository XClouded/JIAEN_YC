package com.xhm.q3.GetVideo_info;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.xhm.q3.view.q3_Haoyou_Info;
import com.xhm.q3.view.q3_Video_Info;
import com.xhm.q3.view.q3_youcai_activity;

public final class q3_GetVideo_Info {
	public static ArrayList<q3_Video_Info> mVideo_Infos;
	public static HttpClient httpClient;
	public static ArrayList<q3_Haoyou_Info> mHaoyou_Infos;
	// 网络添加   网络添加1
	// 本地添加  本地添加1
	// 获取视频的所有信息
	public static ArrayList<q3_Video_Info> getVideoInfo(String classid,
			String count, String pageindex, String ordernum, String username,
			String keywords) {
		mVideo_Infos = new ArrayList<q3_Video_Info>();
		GetMethod getMethod = null;
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(50000);
		String url = "http://www.uuunm.com/getVideo.jsp";
		NameValuePair[] pairs = new NameValuePair[6];
		pairs[0] = new NameValuePair("classid", classid);
		pairs[1] = new NameValuePair("count", count);
		pairs[2] = new NameValuePair("pageindex", pageindex);
		pairs[3] = new NameValuePair("ordernum", ordernum);
		pairs[4] = new NameValuePair("username", username);
		pairs[5] = new NameValuePair("keywords", keywords);

		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "text/html; charset="
				+ "utf-8");
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs, "utf-8"));
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			} else {
				InputStream is = getMethod.getResponseBodyAsStream();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(is);
				Element root = doc.getRootElement();
				for (Iterator i = root.elementIterator("videoinfo"); i
						.hasNext();) {
					Element video = (Element) i.next();
					for (Iterator j = video.elementIterator("video"); j
							.hasNext();) {
						Element ele = (Element) j.next();
						q3_Video_Info info = new q3_Video_Info();
						int index = 0;
						for (Iterator k = ele.elementIterator(); k.hasNext();) {
							Element e = (Element) k.next();
							String attr = e.attributeValue("attr");
							switch (index) {
							case 0:
								info.setmId(attr);
								break;
							case 1:
								info.setmClassid(attr);
								break;
							case 2:
								info.setmPath(attr);
								info.setmPic_Path(attr.replace("mp4", "jpg"));
								break;
							case 3:
								info.setmName(attr);
								break;
							case 4:
								info.setmDescrib(attr);
								break;
							case 5:
								info.setmTime(attr);
								break;
							case 6:
								info.setmShow(attr);
								break;
							case 7:
								info.setmSize(attr);
								break;
							case 8:
								info.setmKeep(attr);
								break;
							case 9:
								info.setmShare(attr);
								break;
							default:
								break;
							}
							index++;
						}
						mVideo_Infos.add(info);
					}
				}

			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		q3_youcai_activity.pd.dismiss();
		return mVideo_Infos;
	}

	// 分享次数的增加
	public static String VideoShare(String id, String OriNum, String DestNum) {
		String url = "http://www.uuunm.com/getVideo.jsp";
		GetMethod getMethod = null;
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(50000);
		NameValuePair[] pairs = new NameValuePair[3];
		pairs[0] = new NameValuePair("id", id);
		pairs[1] = new NameValuePair("OriNum", OriNum);
		pairs[2] = new NameValuePair("DestNum", DestNum);
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "text/html; charset="
				+ "utf-8");
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs, "utf-8"));
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			} else {
				InputStream is = getMethod.getResponseBodyAsStream();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(is);
				Element root = doc.getRootElement();
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// 视频播放增加
	public static String Videoplaynum(String id, String username) {
		String url = "http://www.uuunm.com/setVideoplaynum.jsp";
		GetMethod getMethod = null;
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(50000);
		NameValuePair[] pairs = new NameValuePair[2];
		pairs[0] = new NameValuePair("id", id);
		pairs[1] = new NameValuePair("username", username);
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "text/html; charset="
				+ "utf-8");
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs, "utf-8"));
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			} else {
				InputStream is = getMethod.getResponseBodyAsStream();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(is);
				Element root = doc.getRootElement();
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// 收藏增加数量
	public static String Videocollectnum(String id, String username) {
		String url = "http://www.uuunm.com/setVideocollect.jsp";
		GetMethod getMethod = null;
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(50000);
		NameValuePair[] pairs = new NameValuePair[2];
		pairs[0] = new NameValuePair("id", id);
		pairs[1] = new NameValuePair("username", username);
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "text/html; charset="
				+ "utf-8");
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs, "utf-8"));
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			} else {
				InputStream is = getMethod.getResponseBodyAsStream();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(is);
				Element root = doc.getRootElement();
				Element opresult = (Element) root.elementIterator("opresult")
						.next();
				String str = null;
				for (Iterator k = opresult.elementIterator(); k.hasNext();) {
					Element e = (Element) k.next();
					String attr = e.attributeValue("attr");
					str += attr;
				}
				return str;
			}

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// 获得好友信息
	public static ArrayList<q3_Haoyou_Info> GetfriendsInfo(String count,
			String pageindex, String username) {
		String url = "http://www.uuunm.com/getfriendsInfo.jsp";
		mHaoyou_Infos = new ArrayList<q3_Haoyou_Info>();
		GetMethod getMethod = null;
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(50000);
		NameValuePair[] pairs = new NameValuePair[3];
		pairs[0] = new NameValuePair("count", count);
		pairs[1] = new NameValuePair("pageindex", pageindex);
		pairs[2] = new NameValuePair("username", username);
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "text/html; charset="
				+ "utf-8");
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs, "utf-8"));
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			} else {
				InputStream is = getMethod.getResponseBodyAsStream();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(is);
				Element root = doc.getRootElement();
				Element opresult = (Element) root.elementIterator("opresult")
						.next();
				String str = null;
				for (Iterator k = opresult.elementIterator(); k.hasNext();) {
					Element e = (Element) k.next();
					String attr = e.attributeValue("attr");
					str += attr;
					System.out.println("str===" + str);

				}
				for (Iterator i = root.elementIterator("friendsinfo"); i
						.hasNext();) {
					Element video = (Element) i.next();
					for (Iterator j = video.elementIterator("friends"); j
							.hasNext();) {
						Element ele = (Element) j.next();
						q3_Haoyou_Info info = new q3_Haoyou_Info();
						int index = 0;
						for (Iterator k = ele.elementIterator(); k.hasNext();) {
							Element e = (Element) k.next();
							String attr = e.attributeValue("attr");
							switch (index) {
							case 0:
								info.setmID(attr);
								break;
							case 1:
								info.setmUsernaem(attr);
								break;
							case 2:
								info.setmName(attr);
								break;
							case 3:
								info.setmPhone_num(attr);
								break;
							case 4:
								info.setmFriendfirstletter(attr);
								break;
							default:
								break;
							}
							index++;
						}
						mHaoyou_Infos.add(info);
					}
				}
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("mHaoyou_Infos=222=" + mHaoyou_Infos.size());
		return mHaoyou_Infos;

	}

	// 删除好友
	public static String Delfriend(String id) {
		String url = "http://www.uuunm.com/delfriend.jsp";
		GetMethod getMethod = null;
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(50000);
		NameValuePair[] pairs = new NameValuePair[1];
		pairs[0] = new NameValuePair("id", id);
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "text/html; charset="
				+ "utf-8");
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs, "utf-8"));
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			} else {
				InputStream is = getMethod.getResponseBodyAsStream();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(is);
				Element root = doc.getRootElement();
				Element opresult = (Element) root.elementIterator("opresult")
						.next();
				String str = null;
				for (Iterator k = opresult.elementIterator(); k.hasNext();) {
					Element e = (Element) k.next();
					String attr = e.attributeValue("attr");
					str += attr;
					System.out.println("sttt===" + str);
				}
				return str;
			}

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
