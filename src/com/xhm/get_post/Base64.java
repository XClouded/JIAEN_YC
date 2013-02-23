package com.xhm.get_post;

/**
 * <br>
 * Copyright (C) 2011, poodu Ltd.,<br>
 * All Rights Reserved<br>
 * Class name: Info.java<br>
 * Description: .<br>
 * <br>
 * author: lixi<br>
 * email: lixi@9.cn<br>
 * <br>
 * Modification History:<br>
 ********************************************************** <br>
 * Date Author Comments <br>
 * 2011-9-21 lixi Created<br>
 ********************************************************** <br>
 * <br>
 */
public class Base64 {
	// �� s ���� BASE64 ����
	public static String Encode(byte[] s) {
		if (s == null)
			return null;
		return (new BASE64Encoder()).encode(s);
	}

	// �� BASE64 ������ַ��� s ���н���
	public static byte[] Decode(String s) {
		if (s == null)
			return null;
		try {
			byte[] b = (new BASE64Decoder()).decodeBuffer(s);
			return b;
		} catch (Exception e) {
			return null;
		}
	}
}
