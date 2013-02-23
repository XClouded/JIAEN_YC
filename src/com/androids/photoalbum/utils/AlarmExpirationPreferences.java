package com.androids.photoalbum.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AlarmExpirationPreferences {
    // Preferences file
    private static final String PREFERENCES_FILE = "password-expiration";

    // Preferences field names
    private static final String POLICY_PASSWORD_EXPIRATION_TIMEOUT =
        "password-expiration-timeout";

    private static final String POLICY_PASSWORD_EXPIRATION_DATE =
        "password-expiration-date";

    private static AlarmExpirationPreferences sPreferences;

    final SharedPreferences mSharedPreferences;

    private AlarmExpirationPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES_FILE,
                Context.MODE_PRIVATE);
    }

    /**
     * TODO need to think about what happens if this gets GCed along with the
     * Activity that initialized it. Do we lose ability to read Preferences in
     * further Activities? Maybe this should be stored in the Application
     * context.
     */
    public static synchronized AlarmExpirationPreferences getPreferences(Context context) {
        if (sPreferences == null) {
            sPreferences = new AlarmExpirationPreferences(context);
        }
        return sPreferences;
    }

    public long getPasswordExpirationTimeout() {
        return mSharedPreferences.getLong(POLICY_PASSWORD_EXPIRATION_TIMEOUT, 0);
    }

    public void setPasswordExpirationTimeout(long passwordExpirationTimeout) {
        mSharedPreferences.edit()
                .putLong(POLICY_PASSWORD_EXPIRATION_TIMEOUT, passwordExpirationTimeout)
                .commit();
    }

    public long getPasswordExpirationDate() {
        return mSharedPreferences.getLong(POLICY_PASSWORD_EXPIRATION_DATE, 0);
    }

    public void setPasswordExpirationDate(long passwordExpirationDate) {
        mSharedPreferences.edit().putLong(POLICY_PASSWORD_EXPIRATION_DATE,
                passwordExpirationDate).commit();
    }

    public void clear() {
        mSharedPreferences.edit().clear().commit();
    }

}
