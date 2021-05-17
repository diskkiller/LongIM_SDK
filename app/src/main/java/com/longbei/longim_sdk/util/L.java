package com.longbei.longim_sdk.util;

import android.util.Log;


/**
 * @author
 * @ClassName: L
 * @Description: log
 * @date 15/11/1 上午10:41
 */
public class L {


    /**
     *
     */
    public static boolean debug = true;
    public static boolean debugLog = true;
    public static boolean changeModle = false;
    public static boolean isConnected = false;
    public static String deviceNo = "";

    public static void debug(String TAG, String info) {
        if (debugLog) {
            Log.d(TAG, "" + info);
        }
    }
    public static void info(String TAG, String info) {
        if (debugLog) {
            Log.i(TAG, "" + info);
        }
    }


    public static void error(String TAG, String info) {
        if (debugLog) {
            Log.e(TAG, "" + info);
        }
    }

    public static void error(String TAG, Exception ex) {

        if (debugLog) {
            ex.printStackTrace();
            Log.w(TAG, "ex: " + ex.toString());
        }
    }

    public static void error(String TAG, Error err) {
        if (debugLog) {
            err.printStackTrace();
            Log.e(TAG, "err: " + err.toString());
        }
    }
}
