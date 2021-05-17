package com.longbei.longim_sdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


import com.longbei.longim_sdk.util.SPManager;
import com.longbei.longim_sdk.util.DownloadUtil;

import java.io.File;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ADIntentService extends IntentService {
    public static final String ACTION_DOWNLOAD_AD = "com.jkt.update.action.downloadAD";

    public ADIntentService() {
        super("ADIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //单一action  无需action判断
            Log.i("theadintent", "start ......");
            adIo(intent);
        }
    }



    private void adIo(Intent intent) {


        String downUrl = intent.getStringExtra("downUrl");
        String title = intent.getStringExtra("title");

        Log.i("theadintent", "  开始下载："+downUrl);

            DownloadUtil.get().download(downUrl,title, getApplicationContext().getCacheDir().getAbsolutePath() + File.separator, title+"_diskkiller.png", new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(File file,String title) {
                    Log.i("theadintent", "  下载图片成功："+file.getAbsolutePath());
                    SPManager.setADFilePath(ADIntentService.this, title,file.getAbsolutePath());
                }

                @Override
                public void onDownloading(int progress) {
                    Log.i("theadintent", " %"+progress);
                }

                @Override
                public void onDownloadFailed(Exception e) {

                }
            });
    }


}
