package com.longbei.longim_sdk.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by 天哥哥 on 2017/2/1 0018.
 */
public class SPManager {


    private SPManager() {
    }


    public static void setADFilePath(Context context, String name,String path) {
        SharedPreferences preferences = context.getSharedPreferences("app", context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(name, path);
        edit.commit();
    }

    public static String getADFilePath(Context context,String name) {
        SharedPreferences preferences = context.getSharedPreferences("app", context.MODE_PRIVATE);
        return preferences.getString(name, "");
    }




}
