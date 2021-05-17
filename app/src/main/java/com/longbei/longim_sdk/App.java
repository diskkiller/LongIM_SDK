package com.longbei.longim_sdk;

import android.content.Context;

import com.longbei.im_push_service_sdk.common.Factory;
import com.longbei.im_push_service_sdk.common.app.Application;
import com.longbei.im_push_service_sdk.im.push.manager.PushServiceManager;
import com.qw.soul.permission.SoulPermission;


/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PushServiceManager.getInstance().init(this, "12345", "","", 1);

        SoulPermission.init(this);

        // 调用Factory进行初始化
        Factory.setup();
    }

    @Override
    protected void showAccountView(Context context) {

        // 登录界面的显示


    }
}
