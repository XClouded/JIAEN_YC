/*********************************************************************
 *  ____                      _____      _                           *
 * / ___|  ___  _ __  _   _  | ____|_ __(_) ___ ___ ___  ___  _ __   *
 * \___ \ / _ \| '_ \| | | | |  _| | '__| |/ __/ __/ __|/ _ \| '_ \  *
 *  ___) | (_) | | | | |_| | | |___| |  | | (__\__ \__ \ (_) | | | | *
 * |____/ \___/|_| |_|\__, | |_____|_|  |_|\___|___/___/\___/|_| |_| *
 *                    |___/                                          *
 *                                                                   *
 *********************************************************************
 * Copyright 2009 Sony Ericsson Mobile Communications Japan, Inc.    *
 * All rights, including trade secret rights, reserved.              *
 *********************************************************************/
package com.androids.photoalbum.utils;

//import com.sonyericsson.android.util.Log;
//import com.sonyericsson.android.util.Config;
import android.util.Log;
import android.util.Config;

/**
 * This class is used to log messages.
 */
public final class SpbLog {

    /** log filter error. */
    public static final boolean LOGE = true;

    /** log filter warn. */
    public static final boolean LOGW = true;

    /** log filter info. */
    public static final boolean LOGI = false;
// 2010/09/10 eDream 2.0 mod start (DMS00864423)
    /** log filter debug. */
    public static final boolean LOGD = Config.DEBUG;

    /** log filter verbose. */
    public static final boolean LOGV = false;
// 2010/09/10 eDream 2.0 mod start (DMS00864423)
    /** TAG. */
    private static final String TAG = "socialphonebook";

    /**
     * Log a message with ERROR level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     */
    public static void e(final String aTag, final String aMsg) {
        if (LOGE) {
            Log.e(TAG, aTag + ": " + aMsg);
        }
    }

    /**
     * Log a message and an exception with ERROR level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     * @param aTr An exception to log
     */
    public static void e(final String aTag, final String aMsg,
            final Throwable aTr) {
        if (LOGE) {
            Log.e(TAG, aTag + ": " + aMsg, aTr);
        }
    }

    /**
     * Log a message with WARN level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     */
    public static void w(final String aTag, final String aMsg) {
        if (LOGW) {
            Log.w(TAG, aTag + ": " + aMsg);
        }
    }

    /**
     * Log an exception with WARN level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aTr An exception to log
     */
    public static void w(final String aTag, final Throwable aTr) {
        if (LOGW) {
            Log.w(TAG, aTr);
        }
    }

    /**
     * Log a message and an exception with WARN level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     * @param aTr An exception to log
     */
    public static void w(final String aTag, final String aMsg,
            final Throwable aTr) {
        if (LOGW) {
            Log.w(TAG, aTag + ": " + aMsg, aTr);
        }
    }

    /**
     * Log a message with INFO level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     */
    public static void i(final String aTag, final String aMsg) {
        if (LOGI) {
            Log.i(TAG, aTag + ": " + aMsg);
        }
    }

    /**
     * Log a message with DEBUG level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     */
    public static void d(final String aTag, final String aMsg) {
        if (LOGD) {
            Log.d(TAG, aTag + ": " + aMsg);
        }
    }

    /**
     * Log a message and an exception with DEBUG level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     * @param aTr An exception to log
     */
    public static void d(final String aTag, final String aMsg,
            final Throwable aTr) {
        if (LOGD) {
            Log.d(TAG, aTag + ": " + aMsg, aTr);
        }
    }

    /**
     * Log end log of the method .
     *
     * @param aTag Used to identify the source of a log message.
     */
    public static void begin(final String aTag) {
        if (LOGD) {
            Log.d(TAG, aTag + ": " + getMethodName() + " begin");
        }
    }

    /**
     * Log beginning log of the method .
     *
     * @param aTag Used to identify the source of a log message.
     */
    public static void end(final String aTag) {
        if (LOGD) {
            Log.d(TAG, aTag + ": " + getMethodName() + " end");
        }
    }

    /**
     * Log a method name in which this method is called.
     *
     * @param aTag Used to identify the source of a log message.
     */
    public static void method(final String aTag) {
        if (LOGD) {
            Log.d(TAG, aTag + ": " + getMethodName());
        }
    }

    /**
     * Log a message with VERBOSE level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     */
    public static void v(final String aTag, final String aMsg) {
        if (LOGV) {
            Log.v(TAG, aTag + ": " + aMsg);
        }
    }

    /**
     * Log a message and an exception with VERBOSE level.
     *
     * @param aTag Used to identify the source of a log message.
     * @param aMsg The message you would like logged.
     * @param aTr An exception to log
     */
    public static void v(final String aTag, final String aMsg,
            final Throwable aTr) {
        if (LOGV) {
            Log.v(TAG, aTag + ": " + aMsg, aTr);
        }
    }

    /**
     * Returns method name in which logging method is called.
     *
     * @return method name
     */
    private static String getMethodName() {
        Exception e = new Exception();
        StackTraceElement[] list = e.getStackTrace();
        String method = "";
        if (list != null && list.length > 2) {
            method = list[2].getMethodName();
        }
        return method;
    }

    /**
     * Constructor.
     */
    private SpbLog() {
    }

}
