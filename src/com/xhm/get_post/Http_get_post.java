package com.xhm.get_post;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.MessageActivity;

@SuppressLint("NewApi")
public final class Http_get_post {
	public static HttpClient httpClient;

	private static String name;
	private static String pwd;
	private static Editor edit;
	private static Dialog dialog, regist_dialog, dialog_tishi;
	private static View loginDialog, loginDialog_2, dialog_tishi_view;
	private static Button dialog_login, dialog_exit, login_regist;
	private static Button exit_dialog_2, login_dialog_2, login_dialog_2_1,
			btn_sure, btn_not;
	private static SmsManager smsManager;
	private static int a;

	@SuppressLint("NewApi")
	public static String Http_get(final Context context) {
		edit = MessageActivity.share.edit();
		// ��ʾ��¼Dialog�������û���Ϣ��
		smsManager = SmsManager.getDefault();
		loginDialog = ((Activity) context).getLayoutInflater().inflate(
				R.layout.login, null);
		loginDialog_2 = ((Activity) context).getLayoutInflater().inflate(
				R.layout.login_regist, null);
		dialog_tishi_view = ((Activity) context).getLayoutInflater().inflate(
				R.layout.dialog_tishi, null);
		dialog = new Dialog(context);
		dialog_tishi = new Dialog(context);
		dialog_tishi.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_tishi.setContentView(dialog_tishi_view);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(loginDialog);
		dialog.show();
		// ��ʼ����¼���˳���ť
		dialog_login = (Button) loginDialog.findViewById(R.id.login_dialog);
		dialog_exit = (Button) loginDialog.findViewById(R.id.exit_dialog);
		login_regist = (Button) loginDialog.findViewById(R.id.login_regist);
		// ��ʼ����¼���˳���ť
		login_dialog_2_1 = (Button) loginDialog_2
				.findViewById(R.id.login_dialog_2_1);
		login_dialog_2 = (Button) loginDialog_2
				.findViewById(R.id.login_dialog_2);
		exit_dialog_2 = (Button) loginDialog_2.findViewById(R.id.exit_dialog_2);
		// dialog_tishi_view中的button
		btn_sure = (Button) dialog_tishi_view.findViewById(R.id.sure);
		btn_not = (Button) dialog_tishi_view.findViewById(R.id.not);
		// 方法
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (a) {
				case 1:
					smsManager.sendTextMessage("10655867603", null, "ktwp",
							null, null);
					dialog_tishi.dismiss();
					break;
				case 2:
					Intent intent = new Intent(Intent.ACTION_SENDTO, Uri
							.parse("smsto:10655867603"));
					intent.putExtra("sms_body", "ktwp");
					context.startActivity(intent);
					dialog_tishi.dismiss();
					break;
				default:
					break;
				}
			}
		});
		btn_not.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog_tishi.dismiss();
			}
		});
		// ΪDialog��ť��ӹ���
		dialog_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				name = ((EditText) loginDialog.findViewById(R.id.name))
						.getText().toString();
				pwd = ((EditText) loginDialog.findViewById(R.id.pass))
						.getText().toString();
				http_Client("http://www.uuunm.com/IsExistUser.jsp", context);
				dialog_login.getBackground().setAlpha(155);
				dialog_login.invalidate();
				dialog.dismiss();
			}
		});
		dialog_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_exit.getBackground().setAlpha(155);
				dialog_exit.invalidate();
				dialog.dismiss();
			}

		});
		login_regist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login_regist.getBackground().setAlpha(155);
				login_regist.invalidate();
				dialog.dismiss();
				regist_dialog = new Dialog(context);
				regist_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				regist_dialog.setContentView(loginDialog_2);
				regist_dialog.show();
			}
		});
		// 手动注册
		login_dialog_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				a = 2;
				regist_dialog.dismiss();
				dialog_tishi.show();

			}
		});
		exit_dialog_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				regist_dialog.dismiss();
			}
		});
		// 自动注册
		login_dialog_2_1.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				a = 1;
				regist_dialog.dismiss();
				dialog_tishi.show();
			}
		});
		return null;
	}

	public static String Http_post(String username, String userpwd,
			String isejia, String userid, byte[] bitmap, Context context) {
		String ret = "ʧ��";
		try {
			if (isejia.equals("0")) {// ����1+�û�����ע�ᣬ���ϴ�����
				// 1+ע��
				httpClient = new HttpClient();

				// ���� Http ���ӳ�ʱ 5s
				httpClient.getHttpConnectionManager().getParams()
						.setConnectionTimeout(50000);
				// PostMethod getMethod = new PostMethod(url);
				/* 2.��� GetMethod �������ò��� */
				String url = "http://file1.11619.cn/api/user/register";
				NameValuePair[] pairs = new NameValuePair[3];
				String areaidname = "areaid";
				String areaidvalue = "1";
				String appidname = "appid";
				String appidvalue = "2";
				String codename = "code";
				String myappkey = "1ce1fefb4c6083c7";
				pairs[0] = new NameValuePair(areaidname, areaidvalue);
				pairs[1] = new NameValuePair(appidname, appidvalue);
				String urlstr = "username=" + username + "&password=" + userpwd
						+ "&email=";
				if (username.indexOf("156") == 0
						|| username.indexOf("155") == 0
						|| username.indexOf("130") == 0
						|| username.indexOf("132") == 0
						|| username.indexOf("186") == 0) {
					urlstr = urlstr + username + "@wo.com.cn";
				} else {
					urlstr = urlstr + username + "@11619.cn";
				}
				AuthCode ac = new AuthCode();
				pairs[2] = new NameValuePair(codename, ac.authcodeEncode(
						urlstr, myappkey));
				System.out.println("code:" + urlstr);
				System.out.println("code:"
						+ ac.authcodeEncode(urlstr, myappkey));
				GetMethod getMethod = new GetMethod(url);
				getMethod.addRequestHeader("Content-Type",
						"text/html; charset=" + "utf-8");
				// ���� get ����ʱ 5s
				getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,
						50000);
				// �����������Դ���
				getMethod.getParams().setParameter(
						HttpMethodParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler());
				getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs,
						"utf-8"));
				/* 3.ִ�� HTTP GET ���� */
				try {
					int statusCode = httpClient.executeMethod(getMethod);
					// �жϷ��ʵ�״̬��
					if (statusCode != HttpStatus.SC_OK) {
						System.err.println("Method failed: "
								+ getMethod.getStatusLine());
					} else {
						InputStream is = getMethod.getResponseBodyAsStream();
						SAXReader reader = new SAXReader();
						Document doc = reader.read(is);
						Element foo = doc.getRootElement();
						String status = foo.elementText("status");
						System.out.println("status:" + status);
						// ע��ɹ�
						if (status.equals("1")) {
							Element Node = null;
							Node = foo.element("results");
							ArrayList NodeList = new ArrayList();
							NodeList = (ArrayList) Node.elements();
							String uid = "";
							String logsign = "";
							for (int i = 0; i < NodeList.size(); i++) {
								String name = ((Element) NodeList.get(i))
										.attributeValue("name");
								String value = ((Element) NodeList.get(i))
										.getText();
								System.out.println("name:" + name);
								System.out.println("value:" + value);
								if (name.equals("uid")) {
									uid = value;
								} else if (name.equals("logsign")) {
									logsign = value;
								}
							}
							is.close();
							// ע��ɹ����û����Ϊ1+�û�-->����һ���ӿ�
							httpClient = new HttpClient();
							pairs = new NameValuePair[1];
							pairs[0] = new NameValuePair("userid", userid);
							getMethod = new GetMethod(
									"http://www.uuunm.com/updateejia.jsp");
							getMethod.addRequestHeader("Content-Type",
									"text/html; charset=" + "utf-8");
							// ���� get ����ʱ 5s
							getMethod.getParams().setParameter(
									HttpMethodParams.SO_TIMEOUT, 50000);
							// �����������Դ���
							getMethod.getParams().setParameter(
									HttpMethodParams.RETRY_HANDLER,
									new DefaultHttpMethodRetryHandler());
							getMethod.setQueryString(EncodingUtil
									.formUrlEncode(pairs, "utf-8"));
							/* 3.ִ�� HTTP GET ���� */
							try {
								statusCode = httpClient
										.executeMethod(getMethod);
								// �жϷ��ʵ�״̬��
								if (statusCode != HttpStatus.SC_OK) {
									System.err.println("Method failed: "
											+ getMethod.getStatusLine());
								} else {
									is = getMethod.getResponseBodyAsStream();
									reader = new SAXReader();
									doc = reader.read(is);
									Element eleroot = doc.getRootElement();
									String result = eleroot.element("opresult")
											.element("result")
											.attributeValue("attr");
									String resultdesc = eleroot
											.element("opresult")
											.element("resultdesc")
											.attributeValue("attr");
									System.out.println("result:" + result);
									if (result.equals("succ")) {// ���³ɹ�
										// ���Ϊ1+�û�
										edit.putString("isejia", "1");
										edit.commit();
										// �ϴ�ͼƬ�ļ�
										/* 2.��� GetMethod �������ò��� */
										url = "http://dzz.11619.cn:8088/api/file/upload?areaid="
												+ areaidvalue
												+ "&appid="
												+ appidvalue
												+ "&code="
												+ java.net.URLEncoder
														.encode(ac
																.authcodeEncode(
																		"uid="
																				+ uid
																				+ "&username="
																				+ username
																				+ "&logsign="
																				+ logsign
																				+ "&parentid=2",
																		myappkey),
																"utf-8")
														.replace("+", "%20");
										URL url1 = new java.net.URL(url);
										HttpURLConnection connection = (HttpURLConnection) url1
												.openConnection();
										String BOUNDARYSTR = "----------fyuifgfdftDfgfsaasrrhgj19418";
										String postdata = "";
										String startData = "--"
												+ BOUNDARYSTR
												+ "\r\nContent-Disposition: form-data; name=\"postfile\"; filename=\"1.jpg\" \r\n"
												+ "Content-Type: application/octet-stream \r\n\r\n";
										System.out.println("startData:"
												+ startData);
										String endData = "\r\n--" + BOUNDARYSTR
												+ "--";
										byte[] startDataBytes = startData
												.getBytes("UTF-8");
										byte[] endDataBytes = endData
												.getBytes("UTF-8");

										int length = startDataBytes.length
												+ endDataBytes.length
												+ bitmap.length;
										System.out.println("length:" + length);
										connection.setRequestProperty(
												"Content-Type",
												"multipart/form-data; boundary="
														+ BOUNDARYSTR);
										connection.setRequestProperty(
												"Content-Length", length + "");

										connection.setConnectTimeout(20000);
										connection.setReadTimeout(20000);
										connection.setRequestMethod("POST");
										connection.setDoInput(true);
										connection.setDoOutput(true);
										connection.setUseCaches(false);
										connection.connect();

										DataOutputStream parOut = new DataOutputStream(
												connection.getOutputStream());
										parOut.write(startDataBytes);
										parOut.write(bitmap);
										parOut.write(endDataBytes);
										// DataOutputStream.writeBytes���ַ��е�16λ��unicode�ַ���8λ���ַ���ʽд��������
										parOut.flush();
										parOut.close();
										InputStreamReader in = new InputStreamReader(
												connection.getInputStream(),
												"UTF-8");
										doc = reader.read(in);
										foo = doc.getRootElement();
										status = foo.elementText("status");
										System.out.println("status��" + status);
										if (status.equals("1")) {
											Node = foo.element("results");
											NodeList = new ArrayList();
											NodeList = (ArrayList) Node
													.elements();
											String id = "";
											String guid = "";
											for (int i = 0; i < NodeList.size(); i++) {
												String name = ((Element) NodeList
														.get(i))
														.attributeValue("name");
												String value = ((Element) NodeList
														.get(i)).getText();
												System.out.println("name:"
														+ name);
												System.out.println("value:"
														+ value);
												if (name.equals("id")) {
													id = value;
												} else if (name.equals("guid")) {
													guid = value;
												}
											}
											is.close();
											ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
													+ "    <root>\r\n"
													+ "        <opresult>\r\n"
													+ "            <result attr=\"succ\"/>\r\n"
													+ "            <resultdesc attr=\"1+����ɹ���\"/>\r\n"
													+ "        </opresult>\r\n"
													+ "    </root>";
										} else {
											is.close();
											ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
													+ "    <root>\r\n"
													+ "        <opresult>\r\n"
													+ "            <result attr=\"fail\"/>\r\n"
													+ "            <resultdesc attr=\"1+�����ϴ�ʧ�ܣ�\"/>\r\n"
													+ "        </opresult>\r\n"
													+ "    </root>";
										}
										System.out.println("ret:" + ret);
									} else {// ����ʧ��
										System.out.println("resultdesc:"
												+ resultdesc);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}
					// }
				} catch (HttpException e) {
					// ����������쳣��������Э�鲻�Ի��߷��ص�����������
					// System.out.println("Please check your provided
					// http address!");
					e.printStackTrace();
				} catch (IOException e) {
					// ���������쳣
					e.printStackTrace();
				} finally {
					// �ͷ�����
					// xmloutfile="";
					getMethod.releaseConnection();
				}
			} else if (isejia.equals("1")) {// ��1+�û�������֤�����ϴ�����
				// 1+��֤
				HttpClient httpClient = new HttpClient();
				// ���� Http ���ӳ�ʱ 5s
				httpClient.getHttpConnectionManager().getParams()
						.setConnectionTimeout(50000);
				// PostMethod getMethod = new PostMethod(url);
				/* 2.��� GetMethod �������ò��� */
				String url = "http://dzz.11619.cn:8088/api/user/login";
				NameValuePair[] pairs = new NameValuePair[3];
				String areaidname = "areaid";
				String areaidvalue = "1";
				String appidname = "appid";
				String appidvalue = "2";
				String codename = "code";
				String myappkey = "1ce1fefb4c6083c7";
				pairs[0] = new NameValuePair(areaidname, areaidvalue);
				pairs[1] = new NameValuePair(appidname, appidvalue);
				String urlstr = "username=" + username + "&password=" + userpwd;
				AuthCode ac = new AuthCode();
				pairs[2] = new NameValuePair(codename, ac.authcodeEncode(
						urlstr, myappkey));
				GetMethod getMethod = new GetMethod(url);
				getMethod.addRequestHeader("Content-Type",
						"text/html; charset=" + "utf-8");
				// ���� get ����ʱ 5s
				getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,
						50000);
				// �����������Դ���
				getMethod.getParams().setParameter(
						HttpMethodParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler());
				getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs,
						"utf-8"));
				/* 3.ִ�� HTTP GET ���� */
				try {
					int statusCode = httpClient.executeMethod(getMethod);
					// �жϷ��ʵ�״̬��
					if (statusCode != HttpStatus.SC_OK) {
						System.err.println("Method failed: "
								+ getMethod.getStatusLine());
					} else {
						// String ret = new
						// String(getMethod.getResponseBodyAsString().getBytes(),charset);
						// System.out.println("Ret:"+ret);
						// if (ret.indexOf("URL") >=
						// 0&&ret.indexOf("����")>=0) {
						// System.out.println("����URL���?�޷�������");
						// } else {
						InputStream is = getMethod.getResponseBodyAsStream();
						SAXReader reader = new SAXReader();
						Document doc = reader.read(is);
						Element foo = doc.getRootElement();
						String status = foo.elementText("status");
						System.out.println("status:" + status);
						if (status.equals("1")) {
							Element Node = null;
							Node = foo.element("results");
							ArrayList NodeList = new ArrayList();
							NodeList = (ArrayList) Node.elements();
							String uid = "";
							String logsign = "";
							for (int i = 0; i < NodeList.size(); i++) {
								String name = ((Element) NodeList.get(i))
										.attributeValue("name");
								String value = ((Element) NodeList.get(i))
										.getText();
								System.out.println("name:" + name);
								System.out.println("value:" + value);
								if (name.equals("uid")) {
									uid = value;
								} else if (name.equals("logsign")) {
									logsign = value;
								}
							}
							is.close();
							// �ϴ�ͼƬ�ļ�
							/* 2.��� GetMethod �������ò��� */
							url = "http://file1.11619.cn/api/file/upload?areaid="
									+ areaidvalue
									+ "&appid="
									+ appidvalue
									+ "&code="
									+ java.net.URLEncoder.encode(
											ac.authcodeEncode("uid=" + uid
													+ "&username=" + username
													+ "&logsign=" + logsign
													+ "&parentid=2", myappkey),
											"utf-8").replace("+", "%20");
							System.out.println("url:" + url);
							URL url1 = new java.net.URL(url);
							HttpURLConnection connection = (HttpURLConnection) url1
									.openConnection();
							String BOUNDARYSTR = "----------fyuifgfdftDfgfsaasrrhgj19418";
							String postdata = "";
							String startData = "--"
									+ BOUNDARYSTR
									+ "\r\nContent-Disposition: form-data; name=\"postfile\"; filename=\"1.jpg\" \r\n"
									+ "Content-Type: application/octet-stream \r\n\r\n";
							System.out.println("startData:" + startData);
							String endData = "\r\n--" + BOUNDARYSTR + "--";
							byte[] startDataBytes = startData.getBytes("UTF-8");
							byte[] endDataBytes = endData.getBytes("UTF-8");

							int length = startDataBytes.length
									+ endDataBytes.length + bitmap.length;
							System.out.println("length:" + length);
							connection.setRequestProperty("Content-Type",
									"multipart/form-data; boundary="
											+ BOUNDARYSTR);
							connection.setRequestProperty("Content-Length",
									length + "");

							connection.setConnectTimeout(20000);
							connection.setReadTimeout(20000);
							connection.setRequestMethod("POST");
							connection.setDoInput(true);
							connection.setDoOutput(true);
							connection.setUseCaches(false);
							connection.connect();

							DataOutputStream parOut = new DataOutputStream(
									connection.getOutputStream());
							parOut.write(startDataBytes);
							parOut.write(bitmap);
							parOut.write(endDataBytes);
							// DataOutputStream.writeBytes���ַ��е�16λ��unicode�ַ���8λ���ַ���ʽд��������
							parOut.flush();
							parOut.close();
							InputStreamReader in = new InputStreamReader(
									connection.getInputStream(), "UTF-8");
							doc = reader.read(in);
							foo = doc.getRootElement();
							status = foo.elementText("status");
							System.out.println("status��" + status);
							if (status.equals("1")) {
								Node = foo.element("results");
								NodeList = new ArrayList();
								NodeList = (ArrayList) Node.elements();
								String id = "";
								String guid = "";
								for (int i = 0; i < NodeList.size(); i++) {
									String name = ((Element) NodeList.get(i))
											.attributeValue("name");
									String value = ((Element) NodeList.get(i))
											.getText();
									System.out.println("name:" + name);
									System.out.println("value:" + value);
									if (name.equals("id")) {
										id = value;
									} else if (name.equals("guid")) {
										guid = value;
									}
								}
								is.close();
								ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
										+ "    <root>\r\n"
										+ "        <opresult>\r\n"
										+ "            <result attr=\"succ\"/>\r\n"
										+ "            <resultdesc attr=\"1+分享成功！\"/>\r\n"
										+ "        </opresult>\r\n"
										+ "    </root>";
								Toast.makeText(context, "1+分享上传成功！", 3000)
										.show();
							} else {
								is.close();
								ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
										+ "    <root>\r\n"
										+ "        <opresult>\r\n"
										+ "            <result attr=\"fail\"/>\r\n"
										+ "            <resultdesc attr=\"1+分享上传失败！\"/>\r\n"
										+ "        </opresult>\r\n"
										+ "    </root>";
								Toast.makeText(context, "1+分享上传失败！", 3000)
										.show();
							}
							System.out.println("ret:" + ret);
						}
					}
				} catch (HttpException e) {
					// ����������쳣��������Э�鲻�Ի��߷��ص�����������
					// System.out.println("Please check your provided
					// http address!");
					e.printStackTrace();
				} catch (IOException e) {
					// ���������쳣
					e.printStackTrace();
				} finally {
					// �ͷ�����
					// xmloutfile="";
					getMethod.releaseConnection();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;

	}

	public static String http_Client(String url, Context context) {
		httpClient = new HttpClient();
		String response = "";
		NameValuePair[] pairs = new NameValuePair[2];
		pairs[0] = new NameValuePair("username", name);
		pairs[1] = new NameValuePair("pwd", pwd);
		GetMethod getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "text/html; charset="
				+ "utf-8");
		// ���� get ����ʱ 5s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
		// �����������Դ���
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setQueryString(EncodingUtil.formUrlEncode(pairs, "utf-8"));
		/* 3.ִ�� HTTP GET ���� */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// �жϷ��ʵ�״̬��
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			} else {
				InputStream is = getMethod.getResponseBodyAsStream();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(is);
				Element eleroot = doc.getRootElement();
				String result = eleroot.element("opresult").element("result")
						.attributeValue("attr");
				if (result.equals("succ")) {
					System.out.println("jinlai l ma ");
					// �û�id
					edit.putString("userid", eleroot.element("userinfo")
							.element("userid").attributeValue("attr"));
					// �û�����
					edit.putString("pwd", pwd);
					// �û���
					edit.putString("name",
							eleroot.element("userinfo").element("username")
									.attributeValue("attr"));
					// �Ƿ�Ϊ1+�����û�
					edit.putString("isejia", eleroot.element("userinfo")
							.element("isejia").attributeValue("attr"));
					edit.commit();
					String userid = MessageActivity.share.getString("userid",
							"-1");
					System.out.println("userid=" + userid);
					response = "登录成功！";
				} else {
					response = "登录失败，用户名或密码错误！";
				}
				Toast.makeText(context, response, 3000).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
