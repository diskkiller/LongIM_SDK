package com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.message;

import android.util.Log;

import androidx.annotation.NonNull;

import com.longbei.im_push_service_sdk.common.basepercenter.repository.BaseDbRepository;
import com.longbei.im_push_service_sdk.im.db.Message;
import com.longbei.im_push_service_sdk.im.db.Message_Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;


import java.util.Collections;
import java.util.List;

/**
 * 跟某人聊天的时候的聊天列表
 * 关注的内容一定是我发给这个人的，或者是他发送给我的
 *
 * @version 1.0.0
 */
public class MessageRepository extends BaseDbRepository<Message>
        implements MessageDataSource {
    // 聊天的对象Id
    private String receiverId;

    public MessageRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        //(sender_id == receiverId and group_id == null)
        // or (receiver_id==receiverId)
        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();

        Log.i("datainfo","MessageRepository  load 真正加载数据 ");
    }



    @Override
    protected boolean isRequired(Message message) {
        // receiverId 如果是发送者，那么Group==null情况下一定是发送给我的消息
        // 如果消息的接收者不为空，那么一定是发送给某个人的，这个人只能是我或者是某个人
        // 如果这个"某个人"就是receiverId，那么就是我需要关注的信息
        Log.i("datainfo","MessageRepository  isRequired : onDataSave调用 检查一个数据是否是我需要关注的数据 ");
        return (receiverId.equalsIgnoreCase(message.getSender().getId())
                && message.getGroup() == null)
                || (message.getReceiver() != null
                && receiverId.equalsIgnoreCase(message.getReceiver().getId())
        );
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {

        Log.i("datainfo","MessageRepository  onListQueryResult : 数据查询通知的回调 反转返回的集合 然后由父类调度");

        // 反转返回的集合
        Collections.reverse(tResult);
        // 然后再调度
        super.onListQueryResult(transaction, tResult);
    }
}
