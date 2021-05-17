package com.longbei.im_push_service_sdk.im.push.interf;



import android.content.Context;

import com.longbei.im_push_service_sdk.im.push.listener.IMSConnectStatusCallback;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * <p>@ClassName:       IMSClientInterface.java</p>
 * <b>
 * <p>@Description:     ims抽象接口，需要切换到其它方式实现im功能，实现此接口即可</p>
 * </b>
 */
public interface IMSClientInterface {

    /**
     * 初始化
     *
     *
     * @param callback      ims连接状态回调
     */
    void init(Context context,IMSConnectStatusCallback callback);

    /**
     * 重置连接，也就是重连
     * 首次连接也可认为是重连
     */
    void resetConnect();

    /**
     * 重置连接，也就是重连
     * 首次连接也可认为是重连
     * 重载
     *
     * @param isFirst 是否首次连接
     */
    void resetConnect(boolean isFirst);

    /**
     * 关闭连接，同时释放资源
     */
    void close();

    /**
     * 标识ims是否已关闭
     *
     * @return
     */
    boolean isClosed();

    /**
     * 发送消息
     *
     * @param msg
     */
    void sendMsg(MqttMessage msg);

    /**
     * 发送消息
     * 重载
     *
     * @param msg
     * @param isJoinTimeoutManager 是否加入发送超时管理器
     */
    void sendMsg(MqttMessage msg, boolean isJoinTimeoutManager);

    /**
     * 获取重连间隔时长
     *
     * @return
     */
    int getReconnectInterval();

    /**
     * 获取mqttAndroidClient
     *
     * @return
     */
    MqttAndroidClient getMqttAndroidClient();



}
