package com.longbei.im_push_service_sdk.im.push.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.longbei.im_push_service_sdk.im.push.factory.IMSClientFactory;
import com.longbei.im_push_service_sdk.im.push.interf.IMSClientInterface;
import com.longbei.im_push_service_sdk.im.push.listener.IMSConnectStatusListener;



public class IMservice extends Service {
    private IMSClientInterface imsClient;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");
    }


    public void im_connect() {
        System.out.println("开始连接服务器...");
        imsClient = IMSClientFactory.getIMSClient();
        imsClient.init(this, new IMSConnectStatusListener());
        imsClient = IMSClientFactory.getIMSClient();
        imsClient.resetConnect(true);
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");


        if(intent != null){
            String useid = intent.getStringExtra("userId");
            System.out.println("onStartCommand   useid: "+useid);
        }

        if(IMSClientFactory.getIMSClient().getMqttAndroidClient()==null){
            im_connect();
        }else{
            System.out.println("服务已启动");
        }


        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("关闭服务");
        imsClient.close();
    }
}
