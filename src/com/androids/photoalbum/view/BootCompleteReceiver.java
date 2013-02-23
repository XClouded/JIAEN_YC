package com.androids.photoalbum.view;

import com.androids.photoalbum.utils.AlarmExpirationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * The broadcast receiver. The actual job is done in EmailBroadcastProcessor on
 * a worker thread.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
	public static final String ACTION_BROADCAST = "com.androids.photoalbum.ACTION_BROADCAST";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("zheng", "BootCompleteReceiver onReceive");
		if (intent != null
				&& Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Log.d("peter", "BootCompleteReceiver ACTION_BOOT_COMPLETED");

			AlarmExpirationManager.getInstance(context)
					.setPasswordExpirationTimeout(
							AlarmExpirationManager.MS_PER_HOUR * 1);
		}
		processBroadcastIntent(context, intent);
	}

	// 启动了一个接受推送的服务
	public static void processBroadcastIntent(Context context,
			Intent broadcastIntent) {
		Intent i = new Intent(context,
				NotificationBroadcastProcessService.class);
		i.setAction(ACTION_BROADCAST);
		i.putExtra(Intent.EXTRA_INTENT, broadcastIntent);
		context.startService(i);
	}
}