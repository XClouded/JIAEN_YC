package com.androids.photoalbum.view;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.ForcePushWebsiteInfo.ProductInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.ForcePushWebsiteParser;
import com.androids.photoalbum.utils.ForcePushDecoder;
import com.androids.photoalbum.utils.NetWorkUtils;

/**
 * 后台用来接受推送的服务
 */
public class NotificationBroadcastProcessService extends IntentService {
	public static final String LOG_TAG = "zheng NotificationBroadcastProcessService";

	public NotificationBroadcastProcessService() {
		super("intent service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("zheng", "NotificationBroadcastProcessService onHandleIntent");
		if (BootCompleteReceiver.ACTION_BROADCAST.equals(intent.getAction())) {
			final Intent broadcastIntent = intent
					.getParcelableExtra(Intent.EXTRA_INTENT);
			final String broadcastAction;

			if (broadcastIntent == null) {
				Log.d(LOG_TAG, "no broadcast intent to get");
				return;
			}
			broadcastAction = broadcastIntent.getAction();

			if (Intent.ACTION_BOOT_COMPLETED.equals(broadcastAction)) {
				boolean forcePushGetted = false;
				while (!forcePushGetted) {
					forcePushGetted = onBootCompleted();
					if (!forcePushGetted) {
						sleep();
					}
				}
			} else if (BootCompleteReceiver.ACTION_BROADCAST
					.equals(broadcastAction)) {
				onBootCompleted();
			}
		}
	}

	private final static long ONE_NIMUTE = 1000 * 60;

	private void sleep() {
		try {
			Thread.currentThread().sleep(ONE_NIMUTE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setTimestamp(String timestamp) {
		SharedPreferences.Editor editor = getSharedPreferences(
				MainTabActivity.USER_INFO, MODE_WORLD_READABLE).edit();
		editor.putString("timestamp", timestamp);
		editor.commit();
	}

	public String getTimestamp() {
		if (false) {
			return "1340781321057";
		}
		SharedPreferences userLoginPreference = getSharedPreferences(
				MainTabActivity.USER_INFO, 0);
		return userLoginPreference.getString("timestamp", "0");
	}

	private boolean onBootCompleted() {
		Log.d("peter", "NotificationBroadcastProcessService onBootCompleted");
		String test = "http://www.uuunm.com/Collection.jsp?userid=17495&pageindex=0&pagecount=30";
		WebsiteInfo webinfo = new ForcePushWebsiteParser()
				.parseWebsiteInfo(NetWorkUtils.FORCE_PUSH + getTimestamp());
		if (!ServerInfo.SUCCESS.equals(webinfo.result)) {
			Log.d("peter", "NotificationBroadcastProcessService FAIL");
			Log.d("peter", "webinfo.result:" + webinfo.result);
			Log.d("peter", "webinfo.resultdesc:" + webinfo.resultdesc);
			return false;
		}
		ProductInfo productinfo = (ProductInfo) webinfo.getProductInfo(0);
		setTimestamp(productinfo.time);
		Log.d("peter", "time:" + productinfo.time);
		if (productinfo.mContents.size() == 0) {
			return true;
		}

		String encoded = "";
		String message = "";
		int latestPos = productinfo.mContents.size() - 1;
		encoded = (String) productinfo.mContents.get(latestPos);
		message = ForcePushDecoder.decode(encoded);
		// for (int i = 0; i < productinfo.mContents.size(); i++) {
		// encoded = (String)productinfo.mContents.get(i);
		// Log.d("peter", "encoded:" + encoded);
		// message += i;
		// message += ForcePushDecoder.decode(encoded);
		// if (i != productinfo.mContents.size()-1) {
		// message += "\n";
		// }
		// }

		Log.d("peter",
				"received force items count:" + productinfo.mContents.size());

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.icon, "彩信推送",
				System.currentTimeMillis());
		n.flags = Notification.FLAG_AUTO_CANCEL;
		Intent i = new Intent(NotificationBroadcastProcessService.this,
				MainTabActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		// PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(
				NotificationBroadcastProcessService.this, R.string.app_name, i,
				PendingIntent.FLAG_UPDATE_CURRENT);

		n.setLatestEventInfo(NotificationBroadcastProcessService.this, "彩信推送",
				"您有新彩信:" + message, contentIntent);
		nm.notify(R.string.app_name, n);
		return true;
	}
}
