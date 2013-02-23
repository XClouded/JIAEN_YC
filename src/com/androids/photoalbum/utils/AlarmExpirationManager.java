package com.androids.photoalbum.utils;

import com.androids.photoalbum.view.BootCompleteReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmExpirationManager {

    public static final String ACTION_ALARM_EXPIRING =
        "cn.android.common.ACTION_PASSWORD_EXPIRING";

    public static final long MS_PER_DAY = 1 * 24 * 60 * 60 * 1000;
    
    public static final long MS_PER_HOUR = 1 * 60 * 60 * 1000;
    
    public static final long MS_PER_MINUTE = 1 * 60 * 1000;

    private Context mContext;

    public static class ExpiredAlarmReceiver extends BroadcastReceiver {

        private Context mContext;

        public void onReceive(Context context, Intent intent) {
            mContext = context;
            Log.d("peter", "ExpiredAlarmReceiver");
            String action = intent.getAction();
            if (/*Intent.ACTION_BOOT_COMPLETED.equals(action) || */
                    ACTION_ALARM_EXPIRING.equals(action)) {
                AlarmExpirationManager.getInstance(mContext)
                        .handleAlarmExpiration();
            }
        }

    }

    private static AlarmExpirationManager sInstance = null;

    /**
     * Get the security policy instance
     */
    public synchronized static AlarmExpirationManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AlarmExpirationManager(context);
        }
        return sInstance;
    }

    /**
     * Private constructor (one time only)
     */
    private AlarmExpirationManager(Context context) {
        mContext = context;
    }

    /**ACTION_PASSWORD_EXPIRING
     * If the expiration password is set for the account, send command for
     * deleting data and reset the expiration alarm.
     */
    private void handleAlarmExpiration() {
    	Log.d("peter", "handleAlarmExpiration");
//        long expirationTimeout = getPasswordExpirationTimeout();
//        long expirationDate = getPasswordExpiration();
//        long currentTimeMillis = System.currentTimeMillis();
//        if ((expirationTimeout > 0) && (expirationDate > 0)
//                && (currentTimeMillis > expirationDate)) {
        	Log.d("peter", "handleAlarmExpiration ACTION_BROADCAST");
            Intent in = new Intent(BootCompleteReceiver.ACTION_BROADCAST);
            mContext.sendBroadcast(in);
//        }
        setExpirationAlarm();
    }

    /**
     * called when password changed
     */
    public void updatePasswordExpiration() {
        long expirationTimeout = getPasswordExpirationTimeout();
        if (expirationTimeout > 0) {
            long passwordExpirationDate = 0;
            passwordExpirationDate = System.currentTimeMillis() + expirationTimeout;
            savePasswordExpiration(expirationTimeout, passwordExpirationDate);
            setExpirationAlarm();
        }
    }

    /**
     * Save the expiration timeout and expiration date, clear and reset a new
     * expiration alarm.
     *
     * @param timeout The new expiration timeout.
     */
    public void setPasswordExpirationTimeout(long timeout) {
    	Log.d("peter", "setPasswordExpirationTimeout");
        long expirationDate = getPasswordExpiration();
        if (timeout > 0) {
            long expirationTimeout = getPasswordExpirationTimeout();
            long currentMillis = System.currentTimeMillis();
            if ((expirationTimeout == 0) || (expirationDate == 0)
                    || (expirationDate > currentMillis)) {
                expirationDate = currentMillis + timeout;
            }
        } else if (timeout == 0) {
            // timeout is zero, clear this policy
            expirationDate = 0;
        }
        savePasswordExpiration(timeout, expirationDate);
        setExpirationAlarm();
    }

    /**
     * Save the expiration timeout and date to user preference.
     *
     * @param passwordExpirationTimeout Expiration duration.
     * @param passwordExpirationDate next password expire date.
     */
    private void savePasswordExpiration(long passwordExpirationTimeout,
            long passwordExpirationDate) {
        AlarmExpirationPreferences.getPreferences(mContext)
            .setPasswordExpirationTimeout(passwordExpirationTimeout);
        AlarmExpirationPreferences.getPreferences(mContext)
            .setPasswordExpirationDate(passwordExpirationDate);
    }

    /**
     * Get the password expire date.
     *
     * @return date
     */
    public long getPasswordExpiration() {
        return AlarmExpirationPreferences.getPreferences(mContext)
            .getPasswordExpirationDate();
    }

    /**
     * Get the expiration duration.
     *
     * @return timeout
     */
    public long getPasswordExpirationTimeout() {
        return AlarmExpirationPreferences.getPreferences(mContext)
                .getPasswordExpirationTimeout();
    }

    /**
     * Reset the expiration alarm according to the timeout duration.
     */
    protected void setExpirationAlarm() {
    	Log.d("peter", "setExpirationAlarm");
        AlarmManager alarmManager = (AlarmManager)mContext
            .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_ALARM_EXPIRING);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                    intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        long expirationDate = getPasswordExpiration();
        if (getPasswordExpirationTimeout() > 0 && expirationDate > 0) {
            long currentTimeMillis = System.currentTimeMillis();
            long timeout = expirationDate - currentTimeMillis;
            //if password has expired, recalculate a new expiration date
            if (timeout <= 0) {
                expirationDate = currentTimeMillis + timeout % MS_PER_DAY + MS_PER_DAY;
                Log.d("peter", "setExpirationAlarm expirationDate:" + expirationDate);
                expirationDate = currentTimeMillis + getPasswordExpirationTimeout();
                Log.d("peter", "setExpirationAlarm expirationDate:" + expirationDate);
            }

            alarmManager.set(AlarmManager.RTC, expirationDate, pendingIntent);
        }
    }

}
