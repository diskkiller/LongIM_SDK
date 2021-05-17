package com.longbei.longim_sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.longbei.im_push_service_sdk.common.Common;
import com.longbei.longim_sdk.mainac.MainActivity;

public class ImOfflineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Notification","收到通知栏点击广播："+intent.getAction()+" msg: "+intent.getStringExtra(Common.DATA));
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(Common.TYPE)) {
            case Common.TYPE_GET_DATA:
                Intent intent1 = new Intent();
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.setClass(context, MainActivity.class);
                context.startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
