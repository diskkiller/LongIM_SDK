package com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.user;


import com.longbei.im_push_service_sdk.im.db.t_card.UserCard;

/**
 * 用户中心的基本定义
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public interface UserCenter {
    // 分发处理一堆用户卡片的信息，并更新到数据库
    void dispatch(UserCard... cards);
}
