package com.longbei.im_push_service_sdk.im.lpresenter.presenter.message;


import com.longbei.im_push_service_sdk.im.db.Account;
import com.longbei.im_push_service_sdk.im.db.Group;
import com.longbei.im_push_service_sdk.im.db.Message;
import com.longbei.im_push_service_sdk.im.db.helper.GroupHelper;
import com.longbei.im_push_service_sdk.im.db.viewmodle.MemberUserModel;
import com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.message.MessageGroupRepository;

import java.util.List;

/**
 * 群聊天的逻辑
 *
 * @version 1.0.0
 */
public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter {

    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {
        // 数据源，View，接收者，接收者的类型
        super(new MessageGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();

        // 拿群的信息
        Group group = GroupHelper.findFromLocal(mReceiverId);
        if (group != null) {
            // 初始化操作
            ChatContract.GroupView view = getView();

            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            view.showAdminOption(isAdmin);

            // 基础信息初始化
            view.onInit(group);

            // 成员初始化
            List<MemberUserModel> models = group.getLatelyGroupMembers();
            final long memberCount = group.getGroupMemberCount();
            // 没有显示的成员的数量
            long moreCount = memberCount - models.size();
            view.onInitGroupMembers(models, moreCount);
        }

    }
}