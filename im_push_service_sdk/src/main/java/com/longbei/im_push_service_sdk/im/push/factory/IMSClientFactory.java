package com.longbei.im_push_service_sdk.im.push.factory;


import com.longbei.im_push_service_sdk.im.push.client.MQTTClient;
import com.longbei.im_push_service_sdk.im.push.interf.IMSClientInterface;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       IMSClientFactory.java</p>
 * <p>@PackageName:     com.freddy.im</p>
 * <b>
 * <p>@Description:     ims实例工厂方法</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/03/31 20:54</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class IMSClientFactory {

    public static IMSClientInterface getIMSClient() {
        return MQTTClient.getInstance();
    }
}
