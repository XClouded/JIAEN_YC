package com.xhm.get_post;

/**<br>
 * Copyright (C) 2012, poodu Ltd.,<br>
 * All Rights Reserved<br>
 * Class name: AuthCode.java<br>
 * Description: AuthCode .<br>
 * <br>
 * author: 	lixi<br>
 * email: 	lixi@9.cn<br>
 * <br>
 * Modification History:<br>
 **********************************************************	<br>
 * Date			Author		Comments	<br>
 * 2012-8-8		lixi		Created<br>
 **********************************************************	<br>
 *<br>
 */

import java.io.File;
import java.util.Calendar;
import java.util.Random;


public class AuthCode {

	public enum DiscuzAuthcodeMode {
		Encode, Decode
	};

	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { // �������ֽ�ת���� 16 ���Ʊ�ʾ���ַ�
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 �ļ�������һ�� 128 λ�ĳ�������
										// ���ֽڱ�ʾ���� 16 ���ֽ�
			char str[] = new char[16 * 2]; // ÿ���ֽ��� 16 ���Ʊ�ʾ�Ļ���ʹ�������ַ���
											// ���Ա�ʾ�� 16 ������Ҫ 32 ���ַ�
			int k = 0; // ��ʾת������ж�Ӧ���ַ�λ��
			for (int i = 0; i < 16; i++) { // �ӵ�һ���ֽڿ�ʼ���� MD5 ��ÿһ���ֽ�
											// ת���� 16 �����ַ���ת��
				byte byte0 = tmp[i]; // ȡ�� i ���ֽ�
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // ȡ�ֽ��и� 4 λ������ת��,
															// >>>
															// Ϊ�߼����ƣ�������λһ������
				str[k++] = hexDigits[byte0 & 0xf]; // ȡ�ֽ��е� 4 λ������ת��
			}
			s = new String(str); // ����Ľ��ת��Ϊ�ַ���

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	// / <summary>
	// / ���ַ�����ָ��λ�ý�ȡָ�����ȵ����ַ���
	// / </summary>
	// / <param name="str">ԭ�ַ���</param>
	// / <param name="startIndex">���ַ�������ʼλ��</param>
	// / <param name="length">���ַ����ĳ���</param>
	// / <returns>���ַ���</returns>
	public static String CutString(String str, int startIndex, int length) {
		if (startIndex >= 0) {
			if (length < 0) {
				length = length * -1;
				if (startIndex - length < 0) {
					length = startIndex;
					startIndex = 0;
				} else {
					startIndex = startIndex - length;
				}
			}

			if (startIndex > str.length()) {
				return "";
			}

		} else {
			if (length < 0) {
				return "";
			} else {
				if (length + startIndex > 0) {
					length = length + startIndex;
					startIndex = 0;
				} else {
					return "";
				}
			}
		}

		if (str.length() - startIndex < length) {

			length = str.length() - startIndex;
		}

		return str.substring(startIndex, startIndex + length);
	}

	// / <summary>
	// / ���ַ�����ָ��λ�ÿ�ʼ��ȡ���ַ�����β���˷���
	// / </summary>
	// / <param name="str">ԭ�ַ���</param>
	// / <param name="startIndex">���ַ�������ʼλ��</param>
	// / <returns>���ַ���</returns>
	public static String CutString(String str, int startIndex) {
		return CutString(str, startIndex, str.length());
	}

	// / <summary>
	// / �����ļ��Ƿ����
	// / </summary>
	// / <param name="filename">�ļ���</param>
	// / <returns>�Ƿ����</returns>
	public static boolean FileExists(String filename) {
		File f = new File(filename);
		return f.exists();
	}

	// / <summary>
	// / MD5����
	// / </summary>
	// / <param name="str">ԭʼ�ַ���</param>
	// / <returns>MD5���</returns>
	public static String MD5(String str) {
		return getMD5(str.getBytes());
	}

	// / <summary>
	// / �ֶδ��Ƿ�ΪNull��Ϊ""(��)
	// / </summary>
	// / <param name="str"></param>
	// / <returns></returns>
	public static boolean StrIsNullOrEmpty(String str) {
		// #if NET1
		if (str == null || str.trim().equals("")) {
			return true;
		}

		return false;
	}

	// / <summary>
	// / ���� RC4 ��������
	// / </summary>
	// / <param name="pass">�����ִ�</param>
	// / <param name="kLen">��Կ���ȣ�һ��Ϊ 256</param>
	// / <returns></returns>
	static private byte[] GetKey(byte[] pass, int kLen) {
		byte[] mBox = new byte[kLen];

		for (int i = 0; i < kLen; i++) {
			mBox[i] = (byte) i;
		}

		int j = 0;
		for (int i = 0; i < kLen; i++) {

			j = (j + (int) ((mBox[i] + 256) % 256) + pass[i % pass.length])
					% kLen;

			byte temp = mBox[i];
			mBox[i] = mBox[j];
			mBox[j] = temp;
		}

		return mBox;
	}

	// / <summary>
	// / ��������ַ�
	// / </summary>
	// / <param name="lens">����ַ�����</param>
	// / <returns>����ַ�</returns>
	public static String RandomString(int lens) {
		char[] CharArray = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		int clens = CharArray.length;
		String sCode = "";
		Random random = new Random();
		for (int i = 0; i < lens; i++) {
			sCode += CharArray[Math.abs(random.nextInt(clens))];
		}
		return sCode;
	}

	// / <summary>
	// / ʹ�� Discuz authcode �������ַ�������
	// / </summary>
	// / <param name="source">ԭʼ�ַ���</param>
	// / <param name="key">��Կ</param>
	// / <param name="expiry">�����ִ���Чʱ�䣬��λ����</param>
	// / <returns>���ܽ��</returns>
	public static String authcodeEncode(String source, String key, int expiry) {
		return authcode(source, key, DiscuzAuthcodeMode.Encode, expiry);

	}

	// / <summary>
	// / ʹ�� Discuz authcode �������ַ�������
	// / </summary>
	// / <param name="source">ԭʼ�ַ���</param>
	// / <param name="key">��Կ</param>
	// / <returns>���ܽ��</returns>
	public static String authcodeEncode(String source, String key) {
		return authcode(source, key, DiscuzAuthcodeMode.Encode, 0);

	}

	// / <summary>
	// / ʹ�� Discuz authcode �������ַ�������
	// / </summary>
	// / <param name="source">ԭʼ�ַ���</param>
	// / <param name="key">��Կ</param>
	// / <returns>���ܽ��</returns>
	public static String authcodeDecode(String source, String key) {
		return authcode(source, key, DiscuzAuthcodeMode.Decode, 0);

	}

	// / <summary>
	// / ʹ�� ���ε� rc4 ���뷽�����ַ������м��ܻ��߽���
	// / </summary>
	// / <param name="source">ԭʼ�ַ���</param>
	// / <param name="key">��Կ</param>
	// / <param name="operation">���� ���ܻ��ǽ���</param>
	// / <param name="expiry">�����ִ�����ʱ��</param>
	// / <returns>���ܻ��߽��ܺ���ַ���</returns>
	private static String authcode(String source, String key,
			DiscuzAuthcodeMode operation, int expiry) {
		try {
			if (source == null || key == null) {
				return "";
			}

			int ckey_length = 4;
			String keya, keyb, keyc, cryptkey, result;

			key = MD5(key);

			keya = MD5(CutString(key, 0, 16));

			keyb = MD5(CutString(key, 16, 16));

			keyc = ckey_length > 0 ? (operation == DiscuzAuthcodeMode.Decode ? CutString(
					source, 0, ckey_length) : RandomString(ckey_length))
					: "";

			cryptkey = keya + MD5(keya + keyc);

			if (operation == DiscuzAuthcodeMode.Decode) {
				byte[] temp;

				temp = Base64.Decode(CutString(source, ckey_length));
				result = new String(RC4(temp, cryptkey));
				if (CutString(result, 10, 16).equals(
						CutString(MD5(CutString(result, 26) + keyb), 0, 16))) {
					return CutString(result, 26);
				} else {
					temp = Base64.Decode(CutString(source + "=", ckey_length));
					result = new String(RC4(temp, cryptkey));
					if (CutString(result, 10, 16)
							.equals(CutString(
									MD5(CutString(result, 26) + keyb), 0, 16))) {
						return CutString(result, 26);
					} else {
						temp = Base64.Decode(CutString(source + "==",
								ckey_length));
						result = new String(RC4(temp, cryptkey));
						if (CutString(result, 10, 16).equals(
								CutString(MD5(CutString(result, 26) + keyb), 0,
										16))) {
							return CutString(result, 26);
						} else {
							return "2";
						}
					}
				}
			} else {
				source = "0000000000" + CutString(MD5(source + keyb), 0, 16)
						+ source;

				byte[] temp = RC4(source.getBytes("GBK"), cryptkey);

				return keyc + Base64.Encode(temp);

			}
		} catch (Exception e) {
			return "";
		}

	}

	// / <summary>
	// / RC4 ԭʼ�㷨
	// / </summary>
	// / <param name="input">ԭʼ�ִ�����</param>
	// / <param name="pass">��Կ</param>
	// / <returns>�������ִ�����</returns>
	private static byte[] RC4(byte[] input, String pass) {
		if (input == null || pass == null)
			return null;

		byte[] output = new byte[input.length];
		byte[] mBox = GetKey(pass.getBytes(), 256);

		// ����
		int i = 0;
		int j = 0;

		for (int offset = 0; offset < input.length; offset++) {
			i = (i + 1) % mBox.length;
			j = (j + (int) ((mBox[i] + 256) % 256)) % mBox.length;

			byte temp = mBox[i];
			mBox[i] = mBox[j];
			mBox[j] = temp;
			byte a = input[offset];

			// byte b = mBox[(mBox[i] + mBox[j] % mBox.Length) % mBox.Length];
			// mBox[j] һ���� mBox.Length С������Ҫ��ȡģ
			byte b = mBox[(toInt(mBox[i]) + toInt(mBox[j])) % mBox.length];

			output[offset] = (byte) ((int) a ^ (int) toInt(b));
		}

		return output;
	}

	public static int toInt(byte b) {
		return (int) ((b + 256) % 256);
	}

	public long getUnixTimestamp() {
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis() / 1000;
	}

}