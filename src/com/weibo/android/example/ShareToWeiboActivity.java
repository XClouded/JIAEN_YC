/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weibo.android.example;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.weibo.net.ShareActivity;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;
import com.xhm.q3.view.q3_youcai_tuijian;

/**
 * Sample code for testing weibo APIs.
 * 
 * @author ZhangJie (zhangjie2@staff.sina.com.cn)
 */

public class ShareToWeiboActivity extends BaseActivity {
	Weibo mWeibo = Weibo.getInstance();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setView(R.layout.main);
		uploadWeibo();
	}

	private void uploadWeibo() {
		try {
			String subject = q3_youcai_tuijian.mDeclear;
			String weiboContent = null;
			if (subject == null || subject.length() == 0) {
				weiboContent = "#彩信Android版# " + q3_youcai_tuijian.mDeclear;
			} else {
				weiboContent = q3_youcai_tuijian.mDeclear;
			}

			share2weibo(weiboContent, q3_youcai_tuijian.mFlie.getAbsolutePath());
			Intent i = new Intent(ShareToWeiboActivity.this,
					ShareActivity.class);
			ShareToWeiboActivity.this.startActivity(i);
			finish();
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}

	private void share2weibo(String content, String picPath)
			throws WeiboException {
		Weibo weibo = Weibo.getInstance();
		weibo.share2weibo(this, weibo.getAccessToken().getToken(), weibo
				.getAccessToken().getSecret(), content, picPath);

	}

	private String getPublicTimeline(Weibo weibo) throws MalformedURLException,
			IOException, WeiboException {
		String url = Weibo.SERVER + "statuses/public_timeline.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		String rlt = weibo.request(this, url, bundle, "GET",
				mWeibo.getAccessToken());
		return rlt;
	}

	private String upload(Weibo weibo, String source, String file,
			String status, String lon, String lat) throws WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", source);
		bundle.add("pic", file);
		bundle.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("lat", lat);
		}
		String rlt = "";
		String url = Weibo.SERVER + "statuses/upload.json";
		try {
			rlt = weibo.request(this, url, bundle, Utility.HTTPMETHOD_POST,
					mWeibo.getAccessToken());
		} catch (WeiboException e) {
			throw new WeiboException(e);
		}
		return rlt;
	}

	private String update(Weibo weibo, String source, String status,
			String lon, String lat) throws WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", source);
		bundle.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("lat", lat);
		}
		String rlt = "";
		String url = Weibo.SERVER + "statuses/update.json";
		rlt = weibo.request(this, url, bundle, Utility.HTTPMETHOD_POST,
				mWeibo.getAccessToken());
		return rlt;
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		// TODO Auto-generated method stub
		finish();
	}
}
