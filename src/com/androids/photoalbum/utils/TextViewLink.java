package com.androids.photoalbum.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;

public class TextViewLink {
	public static char strarray[];

	public static void addURLSpan(String str, TextView textView) {
		if (str == null || str.length() == 0) {
			return;
		}
		SpannableString ss = new SpannableString(str);
		strarray = str.toCharArray();
		int l = str.length() - 10;
		for (int i = 0; i < l; i++) {
			if ((i + 7 < l) && strarray[i] == 'h' && strarray[i + 1] == 't'
					&& strarray[i + 2] == 't' && strarray[i + 3] == 'p'
					&& strarray[i + 4] == ':' && strarray[i + 5] == '/'
					&& strarray[i + 6] == '/') {
				StringBuffer sb = new StringBuffer("http://");
				for (int j = i + 7; true; j++) {
					// if(j>=l){break;}
					if (((int)strarray[j] >= 19968 && (int)strarray[j] <= 171941) || strarray[j] == '，' || strarray[j] == ' ' || j == str.length() - 1) {
						Log.d("http", sb.toString());
						if (j == str.length() - 1) {
							j++;
						}
						ss.setSpan(new URLSpan(sb.toString()), i, j,
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						i = j;
						break;
					} else {
						sb.append(strarray[j]);
					}
				}
			}
		}
		l = str.length();
		StringBuffer sb = null;
		boolean start = false;
		boolean found = false;
		int startIndex = 0;
		int i = 0;
		for (; i < l; i++) {
			if (strarray[i] == '@') {
				start = true;
				sb = new StringBuffer("weiboatme://view/");
				startIndex = i;
			} else {
				if (start) {
					if (strarray[i] == ':' || strarray[i] == ' ') {
						ss.setSpan(new URLSpan(sb.toString()), startIndex, i,
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						found = true;
						sb = null;
						start = false;
					} else {
						sb.append(strarray[i]);
					}
				}
			}

		}

		// if (!found) {
		// i--;
		// if (i > 0) {
		// ss.setSpan(new URLSpan(sb.toString()), startIndex,i,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// }
		// }
		// ��������ַ�����ʵ�־ֲ�l��
		start = false;
		startIndex = 0;
		i = 0;
		for (; i < l; i++) {// ����i���ַ���#
			if (strarray[i] == '#') {
				if (!start)// ����ǿ�ͷ
				{
					start = true;
					sb = new StringBuffer("weibohuati://view/");
					startIndex = i;// ��ס��ǰ���⿪ͷ���ַ���ʼλ��
				} else {
					sb.append('#');
					// �趨�ֲ�l��
					ss.setSpan(new URLSpan(sb.toString()), startIndex, i + 1,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					sb = null;
					start = false;
				}
			} else {
				if (start)// ����⿪ʼ�����ַ������ӵ�StringBuffer��
				{
					sb.append(strarray[i]);
				}
			}
		}

		textView.setText(ss);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		strarray = null;
	}

}
