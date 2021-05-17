package com.longbei.im_push_service_sdk.im.lpresenter.presenter.message;


import com.longbei.im_push_service_sdk.im.db.Message;
import com.longbei.im_push_service_sdk.im.db.User;
import com.longbei.im_push_service_sdk.im.db.helper.UserHelper;
import com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.message.MessageRepository;

/**
 * @version 1.0.0
 */
public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter {

    MessageRepository mMessageRepository;

    public ChatUserPresenter(MessageRepository mMessageRepository,ChatContract.UserView view, String receiverId) {
        // 数据源，View，接收者，接收者的类型
        super(mMessageRepository = new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);

        this.mMessageRepository = mMessageRepository;
    }

    @Override
    public void start() {
        super.start();

        // 从本地拿这个人的信息
        User receiver = UserHelper.findFromLocal(mReceiverId);
        getView().onInit(receiver);
    }
}
