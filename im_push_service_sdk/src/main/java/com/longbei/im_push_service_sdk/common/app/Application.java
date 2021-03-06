package com.longbei.im_push_service_sdk.common.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.StringRes;
import android.widget.Toast;


import com.longbei.im_push_service_sdk.common.app.kit.handler.Run;
import com.longbei.im_push_service_sdk.common.app.kit.handler.runable.Action;
import com.longbei.im_push_service_sdk.common.app.status.AppStatusManager;
import com.luck.picture.lib.app.IApp;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.engine.PictureSelectorEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0.0
 */
public class Application extends android.app.Application {
    private static Application instance;
    private List<Activity> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppStatusManager.init(this);

        PictureAppMaster.getInstance().setApp(new IApp() {
            @Override
            public Context getAppContext() {
                return instance.getApplicationContext();
            }

            @Override
            public PictureSelectorEngine getPictureSelectorEngine() {
                return null;
            }
        });

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activities.remove(activity);
            }
        });
    }


    // ????????????
    public void finishAll(){
        for (Activity activity : activities) {
            activity.finish();
        }

        showAccountView(this);
    }

    protected void showAccountView(Context context){

    }

    /**
     * ??????????????????
     *
     * @return Application
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * ???????????????????????????
     *
     * @return ??????APP????????????????????????
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    /**
     * ???????????????????????????????????????
     *
     * @return ????????????
     */
    public static File getPortraitTmpFile() {
        // ?????????????????????????????????
        File dir = new File(getCacheDirFile(), "portrait");
        // ?????????????????????????????????
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        // ?????????????????????????????????
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }

        // ????????????????????????????????????????????????
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }

    /**
     * ?????????????????????????????????
     *
     * @param isTmp ???????????????????????? True??????????????????????????????????????????
     * @return ?????????????????????
     */
    public static File getAudioTmpFile(boolean isTmp) {
        File dir = new File(getCacheDirFile(), "audio");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }

        // aar
        File path = new File(getCacheDirFile(), isTmp ? "tmp.mp3" : SystemClock.uptimeMillis() + ".mp3");
        return path.getAbsoluteFile();
    }

    /**
     * ????????????Toast
     *
     * @param msg ?????????
     */
    public static void showToast(final String msg) {
        // Toast ???????????????????????????????????????????????????????????????
        // ????????????????????????????????????show??????
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // ?????????????????????????????????????????????????????????
                Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * ????????????Toast
     *
     * @param msgId ??????????????????????????????
     */
    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }

}
