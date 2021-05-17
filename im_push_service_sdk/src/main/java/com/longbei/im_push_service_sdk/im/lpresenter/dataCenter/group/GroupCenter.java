package com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.group;


import com.longbei.im_push_service_sdk.im.db.t_card.GroupCard;
import com.longbei.im_push_service_sdk.im.db.t_card.GroupMemberCard;

/**
 * 群中心的接口定义
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public interface GroupCenter {
    // 群卡片的处理
    void dispatch(GroupCard... cards);

    // 群成员的处理
    void dispatch(GroupMemberCard... cards);
}
