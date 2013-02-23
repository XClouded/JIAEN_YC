package com.androids.photoalbum.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.android.photoalbum.R;
import com.androids.photoalbum.utils.AlarmExpirationManager;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.utils.Utils;
import com.xhm.firstPage.ViewPagerActivity;
import com.xhm.q3.view.q3_function_activity;

/**
 * 加载页面，使用Runnable来从网络上加载操作页的图片，从而判断网络是否连接。
 * 若连接并下载成功，则跳转操作页面，同时发送一个广播和。若没有下载成功，则显示网路异常，关闭应用。
 * 
 * @author xhm
 * 
 */
public class YC extends Activity {
	public static Drawable mLogo = null;
	private SharedPreferences sharedPreferences;
	private Editor mEditor;
	private int i = 0;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		sharedPreferences = this.getSharedPreferences("test",
				MODE_WORLD_READABLE);
		i = sharedPreferences.getInt("isFirst", -1);
		// 启动新功能展示页
		if (-1 == i) {
			mEditor = sharedPreferences.edit();
			mEditor.putInt("isFirst", 1);
			mEditor.commit();
			Intent intent = new Intent(this, ViewPagerActivity.class);
			startActivity(intent);
			finish();
		} else {
			setContentView(R.layout.splash);
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Runnable action = new Runnable() {

			@Override
			public void run() {
				try {
					// 加载上标题的图片，从网络上
					Bitmap bm = Utils.getBitMapFromNetwork(
							NetWorkUtils.LOGO_URI, YC.this);
					if (bm != null) {
						mLogo = new BitmapDrawable(bm);
						mLogo = getResources().getDrawable(
								R.drawable.q3_msm_title);
					}
					// 隐式跳转的广播
					Intent in = new Intent(
							BootCompleteReceiver.ACTION_BROADCAST);
					sendBroadcast(in);
					AlarmExpirationManager.getInstance(YC.this)
							.setPasswordExpirationTimeout(
									AlarmExpirationManager.MS_PER_HOUR * 1);
				} catch (Exception e) {
					if (!NetWorkUtils.checkNet(YC.this)) {
						Utils.notifyToUser("网络异常，请检查网络后重试", YC.this);
					} else {
						Utils.notifyToUser("发生未知异常，请重新启动！", YC.this);
					}
					finish();
					return;
				}
				if (mLogo != null) {
					launchYC();
				} else {
					Utils.notifyToUser("网络异常，请重新启动", YC.this);
					YC.this.finish();
				}
			}
		};

		new Thread(action).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("zheng", "SplashActivity onResume");
	}

	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return true;
		}

		return super.onKeyDown(keyCode, keyEvent);
	}

	class SplashRunnable implements Runnable {
		public void run() {
			YC localSplashActivity = YC.this;
			Context localContext = YC.this.getApplicationContext();
			Intent mainTabIntent = new Intent(localContext,
					MainTabActivity.class);
			localSplashActivity.startActivity(mainTabIntent);
			localSplashActivity.finish();
		}
	}

	private void launchYC() {
		YC localSplashActivity = YC.this;
		Context localContext = YC.this.getApplicationContext();
		if (1 == i) {
			Intent mainTabIntent = new Intent(localContext,
					q3_function_activity.class);
			localSplashActivity.startActivity(mainTabIntent);
			localSplashActivity.finish();
		}

	}
}
