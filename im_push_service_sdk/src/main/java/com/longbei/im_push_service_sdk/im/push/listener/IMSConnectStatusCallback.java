package com.longbei.im_push_service_sdk.im.push.listener;

/**
 * <p>@ClassName:       IMSConnectStatusCallback.java</p>
 * <b>
 * <p>@Description:     IMS连接状态回调</p>
 */
public interface IMSConnectStatusCallback {

    /**
     * ims连接中
     */
    void onConnecting();

    /**
     * ims连接成功
     */
    void onConnected();

    /**
     * ims连接失败
     */
    void onConnectFailed();
}
