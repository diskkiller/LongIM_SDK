package com.longbei.im_push_service_sdk.im.push.client.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.longbei.im_push_service_sdk.common.Common;

public class NoticeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String acton = intent.getAction();
        String msg = intent.getStringExtra("msg");

        Log.i("Notification","通知栏点击广播："+intent.getAction()+" msg: "+msg);

        if (acton.equals("com.tuita.sdk.action.longbei.im.notice_cancel")) {

            Log.i("Notification","发送广播：com.tuita.sdk.action.longbei.im   msg: "+msg);


            Intent clickIntent = new Intent();
            clickIntent.setAction("com.tuita.sdk.action.longbei.im");

            ComponentName componentName = new ComponentName("com.longbei.longim_sdk",
                    "com.longbei.longim_sdk.receiver.ImOfflineReceiver");

            clickIntent.putExtra(Common.TYPE, Common.TYPE_GET_DATA);
            clickIntent.putExtra(Common.DATA, msg);

            clickIntent.setComponent(componentName);

            clickIntent.setClassName("com.longbei.longim_sdk",
                    "com.longbei.longim_sdk.receiver.ImOfflineReceiver");

            context.sendBroadcast(clickIntent);

            Log.i("Notification","发送广播：====  msg: "+msg);


        }
    }
}
