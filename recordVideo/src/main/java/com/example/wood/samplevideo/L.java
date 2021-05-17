package com.example.wood.samplevideo;

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
    public static boolean debug = true; //BuildConfig.DEBUG;

    public static void debug(String TAG, String info) {
//        if (debug) {
            Log.e(TAG, "" + info);
//        }
    }

    public static void error(String TAG, Exception ex) {

        if (debug) {
            ex.printStackTrace();
            Log.w(TAG, "ex: " + ex.toString());
        }
    }

    public static void error(String TAG, Error err) {
        if (debug) {
            err.printStackTrace();
            Log.e(TAG, "err: " + err.toString());
        }
    }
}
