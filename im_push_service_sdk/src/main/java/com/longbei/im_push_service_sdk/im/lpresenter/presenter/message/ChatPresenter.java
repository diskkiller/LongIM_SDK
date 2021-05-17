package com.longbei.im_push_service_sdk.im.lpresenter.presenter.message;

import androidx.recyclerview.widget.DiffUtil;
import android.text.TextUtils;
import android.util.Log;

import com.longbei.im_push_service_sdk.im.api.message.MsgCreateModel;
import com.longbei.im_push_service_sdk.im.db.Account;
import com.longbei.im_push_service_sdk.im.db.Message;
import com.longbei.im_push_service_sdk.im.db.helper.MessageHelper;
import com.longbei.im_push_service_sdk.im.db.utils.DiffUiDataCallback;
import com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.message.MessageDataSource;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.BaseSourcePresenter;
import com.longbei.im_push_service_sdk.im.push.client.SnowflakeIdWorker;
import com.longbei.im_push_service_sdk.im.push.manager.PushServiceManager;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.UUID;


/**
 * 聊天Presenter的基础类
 *
 * @version 1.0.0
 */
@SuppressWarnings("WeakerAccess")
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {

    // 接收者Id，可能是群，或者人的ID
    protected String mReceiverId;
    // 区分是人还是群Id
    protected int mReceiverType;

    //Twitter的分布式自增ID算法snowflake
    private SnowflakeIdWorker idWorker;


    public ChatPresenter(MessageDataSource source, View view,
                         String receiverId, int receiverType) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
        idWorker = new SnowflakeIdWorker(0, 0);
    }

    @Override
    public void pushText(String content) {
        // 构建一个新的消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver("123", mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();

        // 进行网络发送
        MessageHelper.push(model);

        MqttMessage mqttMessage = new MqttMessage();
        long id = idWorker.nextId();
        mqttMessage.setId((int) id);
        mqttMessage.setPayload(content.getBytes());
        PushServiceManager.getInstance().im_sendMessage(mqttMessage);
    }

    @Override
    public void pushAudio(String path, long time) {
        if(TextUtils.isEmpty(path)){
            return;
        }

        // 构建一个新的消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(path, Message.TYPE_AUDIO)
                .attach(String.valueOf(time))
                .build();

        // 进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushImages(String[] paths) {
        if (paths == null || paths.length == 0 || paths[0] == null)
            return;
        // 此时路径是本地的手机上的路径
        for (String path : paths) {
            if(path == null)
                continue;
            // 构建一个新的消息
            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId, mReceiverType)
                    .content(path, Message.TYPE_PIC)
                    .build();

            // 进行网络发送
            MessageHelper.push(model);
        }
    }

    @Override
    public void pushVideos(String[] paths) {
        if (paths == null || paths.length == 0 || paths[0] == null)
            return;
        // 此时路径是本地的手机上的路径
        for (String path : paths) {
            if(path == null)
                continue;
            // 构建一个新的消息
            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId, mReceiverType)
                    .content(path, Message.TYPE_VIDEO)
                    .build();

            // 进行网络发送
            MessageHelper.push(model);
        }
    }

    @Override
    public boolean rePush(Message message) {
        // 确定消息是可重复发送的
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED) {

            // 更改状态
            message.setStatus(Message.STATUS_CREATED);
            // 构建发送Model
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }

        return false;
    }

    /**
     * SucceedCallback 由数据中心Repository进行调度
     */
    @Override
    public void onDataLoaded(List<Message> messages) {
        Log.i("datainfo","ChatPresenter  onDataLoaded :SucceedCallback 由数据中心Repository进行调度回来");


        ChatContract.View view = getView();
        if (view == null)
            return;

        // 拿到老数据  //getRecyclerAdaptery由界面实现
        @SuppressWarnings("unchecked")
        List<Message> old = view.getRecyclerAdapter().getItems();

        Log.i("datainfo","ChatPresenter  onDataLoaded :差异计算==> old: "+ old.size()+"  new messages: "+messages.size());
        Log.e("datainfo","ChatPresenter  onDataLoaded :差异计算==> old: "+ old.size()+"  new messages: "+messages.size());

        // 差异计算
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);

        DiffUtil.DiffResult result = null;

        try {
            result = DiffUtil.calculateDiff(callback);
        }catch (Exception e){

            Log.e("datainfo","ChatPresenter  onDataLoaded :result==> : "+e.toString());
            Log.e("datainfo","ChatPresenter  onDataLoaded :差异计算==> old: "+ old.size()+"  new messages: "+messages.size());

        }

        Log.e("datainfo","ChatPresenter  onDataLoaded :result==> : "+result.toString());


        // 进行界面刷新
        refreshData(result, messages);

    }

    @Override
    public void test_onDataLoaded(String str) {
        System.out.println("处理推送来的消息,差异计算，进行界面刷新");
    }
}
