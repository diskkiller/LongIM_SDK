package com.longbei.im_push_service_sdk.im.push.client;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;


import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.common.Common;
import com.longbei.im_push_service_sdk.common.Factory;
import com.longbei.im_push_service_sdk.im.push.Utils.StringUtil;
import com.longbei.im_push_service_sdk.im.push.client.receiver.NoticeBroadcastReceiver;
import com.longbei.im_push_service_sdk.im.push.config.IMSConfig;
import com.longbei.im_push_service_sdk.im.push.factory.ExecutorServiceFactory;
import com.longbei.im_push_service_sdk.im.push.interf.IMSClientInterface;
import com.longbei.im_push_service_sdk.im.push.listener.IMSConnectStatusCallback;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


/**
 * <p>@ClassName:       MQTTClient.java</p>
 * <b>
 * <p>@Description:     基于MQTT实现的tcp ims</p>
 * </b>
 */
public class MQTTClient implements IMSClientInterface {

    private static volatile MQTTClient instance;


    private boolean isClosed = false;// 标识ims是否已关闭
    private IMSConnectStatusCallback mIMSConnectStatusCallback;// ims连接状态回调监听器
    private ExecutorServiceFactory loopGroup;// 线程池工厂

    private boolean isReconnecting = false;// 是否正在进行重连
    private int connectStatus = IMSConfig.CONNECT_STATE_FAILURE;// ims连接状态，初始化为连接失败

    private String currentHost = null;// 当前连接host
    private int currentPort = -1;// 当前连接port

    // 重连间隔时长
    private int reconnectInterval = IMSConfig.DEFAULT_RECONNECT_BASE_DELAY_TIME;

    private Context context;

    public static final String TAG = MQTTClient.class.getSimpleName();


    private static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
//        public        String HOST           = "tcp://192.168.1.11:1883";//服务器地址（协议+地址+端口号）
    public String HOST = "tcp://repo.osrad.com:1883";//服务器地址（协议+地址+端口号）
    public String USERNAME = "admin";//用户名
    public String PASSWORD = "password";//密码
    public static String [] PUBLISH_TOPICS = {"gytopic","diskkiller"};//发布主题
    public static String PUBLISH_TOPIC = "gytopic";//发布主题
    public static int [] PUBLISH_TOPICS_QOS = {1,2};//发布主题
    public static String RESPONSE_TOPIC = "message_arrived";//响应主题
    @SuppressLint("MissingPermission")
    public String CLIENTID = Build.SERIAL;//客户端ID，一般以客户端唯一标识符表示，这里用设备序列号表示


    private MQTTClient() {
    }

    public static MQTTClient getInstance() {
        if (null == instance) {
            synchronized (MQTTClient.class) {
                if (null == instance) {
                    instance = new MQTTClient();
                }
            }
        }

        return instance;
    }

    /**
     * 初始化
     *
     * @param callback ims连接状态回调
     */
    @Override
    public void init(Context context, IMSConnectStatusCallback callback) {
        close();
        isClosed = false;
        this.mIMSConnectStatusCallback = callback;
        this.context = context;
        loopGroup = new ExecutorServiceFactory();
        loopGroup.initBossLoopGroup();// 初始化重连线程组

        initMqttClient();
    }



    /**
     * 初始化bootstrap
     */
    private void initMqttClient() {
        String serverURI = HOST; //服务器地址（协议+地址+端口号）
        mqttAndroidClient = new MqttAndroidClient(context, serverURI, CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(false); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(6); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码
        mMqttConnectOptions.setAutomaticReconnect(false);//关闭自动重连
    }


    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "收到消息： topic: "+topic +"  message: "+ new String(message.getPayload()));
            // TODO: 消息解析入库分发
            onMessageArrived(new String(message.getPayload()));

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            try {
                Log.i(TAG, "发送成功 message " + new String(arg0.getMessage().getPayload())+
                        "   messageID: "+new String(arg0.getMessage().getId()+""));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开 ");
            resetConnect();//连接断开，重连
        }
    };



    /**
     * 发送消息
     *
     * @param msg
     */
    @Override
    public void sendMsg(MqttMessage msg) {
        this.sendMsg(msg, false);
    }

    /**
     * 发送消息
     * 重载
     *
     * @param msg
     * @param isJoinTimeoutManager 是否加入发送超时管理器
     */
    @Override
    public void sendMsg(final MqttMessage msg, boolean isJoinTimeoutManager) {
        if (msg == null || msg.getPayload() == null) {
            System.out.println("发送消息失败，消息为空\tmessage=" + msg);
            return;
        }

        String topic = PUBLISH_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            /*
            publish调用后将消息插入到ClientState的队列中，通过CommsSender线程中发送给服务器，
            发送完成时（或收到ack后）会回调MqttCallback接口中的deliveryComplete方法。
            可以设置IMqttActionListener接口获取发送是成功还是失败的回调。
            如果收到一个新的消息，最终通过MqttCallback中的messageArrived回调给用户。
            */
            mqttAndroidClient.publish(topic, msg.getPayload(), qos.intValue(), retained.booleanValue(), null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "当前发送消息 message ："+msg.getPayload()+" messageID ："+msg.getId()+"   success");

                    // TODO: 消息解析入库分发

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "当前消息fail：" + asyncActionToken.toString());

                    // TODO: 消息解析入库分发
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    /**
     * 真正连接服务器的地方
     */
    private void toServer() {
        try {
            mqttAndroidClient.connect(mMqttConnectOptions, null, new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken arg0) {
                    Log.i(TAG, "连接成功====== ");
                    try {
                        /**
                         * 消息传送提供三种服务质量（QOS）：0，1，2
                         * “至多一次”
                         * 消息根据底层因特网协议网络尽最大努力进行传递。 可能会丢失消息。
                         * 例如，将此服务质量与通信环境传感器数据一起使用。 对于是否丢失个别读取或是否稍后立即发布新的读取并不重要。
                         * “至少一次”
                         * 保证消息抵达，但可能会出现重复。
                         * “刚好一次”
                         * 确保只收到一次消息。
                         * 例如，将此服务质量与记帐系统一起使用。 重复或丢失消息可能会导致不便或收取错误费用。
                         */
                        mqttAndroidClient.subscribe(PUBLISH_TOPICS, PUBLISH_TOPICS_QOS);//订阅主题，参数：主题、服务质量
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken arg0, Throwable arg1) {
                    arg1.printStackTrace();
                    Log.i(TAG, "连接失败 ::" + arg1.getLocalizedMessage());
                }
            });
        } catch (Exception e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.err.println(String.format("连接Server(ip[%s], port[%s])失败", currentHost, currentPort));
        }

    }

    private static String getBroadcastAction(Context context) {
        String action = "com.tuita.sdk.action.longbei.im";
        return action;
    }
    /**
     * 消息达到时
     *
     * @param message 新消息
     */
    private void onMessageArrived(String message) {
        // 交给Factory处理
        Factory.dispatchPush(message);

        /*Log.i(TAG, "notify,data = " + message);
        Intent intent = new Intent(getBroadcastAction(context));
        intent.putExtra(Common.TYPE, Common.TYPE_GET_DATA);
        intent.putExtra(Common.DATA, message);
        Notification n = null;
        n = new Notification(android.R.drawable.sym_def_app_icon, message, System.currentTimeMillis());
        n.defaults |= Notification.DEFAULT_SOUND;
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.setLatestEventInfo(context, showName, showContent, PendingIntent.getBroadcast(context, RANDOM.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT));



        String name = "name";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(id)
                    .setContentTitle("活动")
                    .setContentText("您有一项新活动")
                    .setSmallIcon(android.R.drawable.sym_def_app_icon).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("活动")
                    .setContentText("您有一项新活动")
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setOngoing(true)
                    .setChannelId(id);//无效
            notification = notificationBuilder.build();
        }
        // notificationManager.notify(1, notification);把通知显示出来
        startForeground(1,notification);//前台通知(会一直显示在通知栏)*/

        showOpenHistoryNotice(context,"您有一新消息",message);


    }


    /**
     * 消息提醒
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showOpenHistoryNotice(Context context, String title, String msg) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(context.getPackageName(), "通知", NotificationManager.IMPORTANCE_MAX);
            channel.enableLights(true);
            channel.setLightColor(ContextCompat.getColor(context, R.color.picture_color_ffe85d));
            channel.setShowBadge(true);
            channel.setDescription("互救吧");
            manager.createNotificationChannel(channel);
        }

        /*Intent clickIntent = new Intent(context, NoticeBroadcastReceiver.class);
        clickIntent.setAction("com.tuita.sdk.action.longbei.im");
        clickIntent.putExtra(Common.TYPE, Common.TYPE_GET_DATA);
        clickIntent.putExtra(Common.DATA, msg);*/
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, context.getPackageName());
        mBuilder.setContentTitle(title)
                .setContentText(msg)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.sym_def_app_icon))
                .setOnlyAlertOnce(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setColor(ContextCompat.getColor(context, R.color.picture_color_ffe85d));
        //clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 点击删除
        Intent cancelIntent = new Intent(context, NoticeBroadcastReceiver.class);
        cancelIntent.setAction("com.tuita.sdk.action.longbei.im.notice_cancel");
        cancelIntent.putExtra("msg", msg);
        /**
         * FLAG_CANCEL_CURRENT：如果构建的PendingIntent已经存在，则取消前一个，重新构建一个。（PendingIntent contentIntent = PendingIntent.getActivity(context, 0,  intent, PendingIntent.FLAG_CANCEL_CURRENT); 第二个参数是0 ）
         *
         * FLAG_NO_CREATE：如果前一个PendingIntent已经不存在了，将不再构建它。
         *
         * FLAG_ONE_SHOT：表明这里构建的PendingIntent只能使用一次。
         *
         * FLAG_UPDATE_CURRENT：如果构建的PendingIntent已经存在，那么 系统将不会重复创建，
         * 只是把之前不同的传值替换掉。（所以这里如果值不变 是不是有变化的 以致于会出现一些问题
         * 通常做法就是在构建PendingIntent的时候传入不一样的requestCode来改变 更新PendingIntent
         * 一般都是用这个；
         */
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, new Random().nextInt(), cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(cancelPendingIntent);
        //mBuilder.setDeleteIntent(cancelPendingIntent);
        mBuilder.setAutoCancel(true);
        manager.notify(1, mBuilder.build());

    }



    @Override
    public MqttAndroidClient getMqttAndroidClient() {
        return mqttAndroidClient;
    }




    @Override
    public int getReconnectInterval() {
        return reconnectInterval;
    }


    /**
     * 重置连接，也就是重连
     * 首次连接也可认为是重连
     */
    @Override
    public void resetConnect() {
        this.resetConnect(false);
    }

    /**
     * 重置连接，也就是重连
     * 首次连接也可认为是重连
     * 重载
     *
     * @param isFirst 是否首次连接
     */
    @Override
    public void resetConnect(boolean isFirst) {
        if (!isFirst) {
            try {
                Thread.sleep(IMSConfig.DEFAULT_RECONNECT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 只有第一个调用者才能赋值并调用重连
        if (!isClosed && !isReconnecting) {
            synchronized (this) {
                if (!isClosed && !isReconnecting) {
                    // 标识正在进行重连
                    isReconnecting = true;
                    // 回调ims连接状态
                    onConnectStatusCallback(IMSConfig.CONNECT_STATE_CONNECTING);
                    // 执行重连任务
                    loopGroup.execBossTask(new ResetConnectRunnable(isFirst));
                }
            }
        }
    }

    /**
     * 关闭连接，同时释放资源
     */
    @Override
    public void close() {

        System.out.println("关闭连接，同时释放资源");

        if (isClosed) {
            return;
        }
        isClosed = true;
        // 断开mqtt
        try {
            if (mqttAndroidClient != null) {
                mqttAndroidClient.disconnect();
                mqttAndroidClient.unregisterResources();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            // 释放线程池
            if (loopGroup != null) {
                loopGroup.destroy();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            isReconnecting = false;
            mqttAndroidClient = null;
        }
    }

    /**
     * 标识ims是否已关闭
     *
     * @return
     */
    @Override
    public boolean isClosed() {
        return isClosed;
    }



    /**
     * 回调ims连接状态
     *
     * @param connectStatus
     */
    private void onConnectStatusCallback(int connectStatus) {
        this.connectStatus = connectStatus;
        switch (connectStatus) {
            case IMSConfig.CONNECT_STATE_CONNECTING: {
                System.out.println("ims连接中...");
                if (mIMSConnectStatusCallback != null) {
                    mIMSConnectStatusCallback.onConnecting();
                }
                break;
            }

            case IMSConfig.CONNECT_STATE_SUCCESSFUL: {
                System.out.println(String.format("ims连接成功，host『%s』, port『%s』", currentHost, currentPort));
                if (mIMSConnectStatusCallback != null) {
                    mIMSConnectStatusCallback.onConnected();
                }
                break;
            }

            case IMSConfig.CONNECT_STATE_FAILURE:
            default: {
                System.out.println("ims连接失败");
                if (mIMSConnectStatusCallback != null) {
                    mIMSConnectStatusCallback.onConnectFailed();
                }
                break;
            }
        }
    }


    /**
     * 从应用层获取网络是否可用
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }


    /**
     * 重连任务
     */
    private class ResetConnectRunnable implements Runnable {

        private boolean isFirst;

        public ResetConnectRunnable(boolean isFirst) {
            this.isFirst = isFirst;
        }

        @Override
        public void run() {
            // 非首次进行重连，执行到这里即代表已经连接失败，回调连接状态到应用层
            if (!isFirst) {
                onConnectStatusCallback(IMSConfig.CONNECT_STATE_FAILURE);
            }

            try {

                while (!isClosed) {
                    if (!isNetworkAvailable()) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    // 网络可用才进行连接
                    int status;
                    if ((status = reConnect()) == IMSConfig.CONNECT_STATE_SUCCESSFUL) {
                        onConnectStatusCallback(status);
                        // 连接成功，跳出循环
                        break;
                    }

                    if (status == IMSConfig.CONNECT_STATE_FAILURE) {
                        onConnectStatusCallback(status);
                        try {
                            Thread.sleep(IMSConfig.DEFAULT_RECONNECT_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                // 标识重连任务停止
                isReconnecting = false;
            }
        }

        /**
         * 重连，首次连接也认为是第一次重连
         *
         * @return
         */
        private int reConnect() {
            // 未关闭才去连接
            if (!isClosed) {
                return connectServer();
            }
            return IMSConfig.CONNECT_STATE_FAILURE;
        }

        /**
         * 连接服务器
         *
         * @return
         */
        private int connectServer() {
            // 如果服务器地址无效，直接回调连接状态，不再进行连接
            // 有效的服务器地址示例：127.0.0.1 8860

            if (!isClosed) {
                if (StringUtil.isEmpty(HOST)) {
                    return IMSConfig.CONNECT_STATE_FAILURE;
                }

                for (int j = 1; j <= IMSConfig.DEFAULT_RECONNECT_COUNT; j++) {
                    // 如果ims已关闭，或网络不可用，直接回调连接状态，不再进行连接
                    if (isClosed || !isNetworkAvailable()) {
                        return IMSConfig.CONNECT_STATE_FAILURE;
                    }

                    // 回调连接状态
                    if (connectStatus != IMSConfig.CONNECT_STATE_CONNECTING) {
                        onConnectStatusCallback(IMSConfig.CONNECT_STATE_CONNECTING);
                    }
                    System.out.println(String.format("正在进行『%s』的第『%d』次连接，当前重连延时时长为『%dms』", HOST, j, j * getReconnectInterval()));

                    try {
                        toServer();// 连接服务器
                        Thread.sleep(getReconnectInterval());

                        if (mqttAndroidClient.isConnected()) {
                            return IMSConfig.CONNECT_STATE_SUCCESSFUL;
                        } else {
                            // 连接失败，则线程休眠n * 重连间隔时长
                            System.out.println(String.format("连接失败，当前重连延时时长为『%dms』", j * getReconnectInterval()));

                            Thread.sleep(j * getReconnectInterval());
                        }
                    } catch (InterruptedException e) {
                        close();
                        break;// 线程被中断，则强制关闭
                    }
                }

            }
            // 执行到这里，代表连接失败
            return IMSConfig.CONNECT_STATE_FAILURE;
        }
    }
}
