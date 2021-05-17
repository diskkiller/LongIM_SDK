package com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.message;


import com.longbei.im_push_service_sdk.im.db.t_card.MessageCard;

/**
 * 消息中心，进行消息卡片的消费
 *
 * @version 1.0.0
 */
public interface MessageCenter {
    void dispatch(MessageCard... cards);
    default void test_dispatch(String msg){

    }
}
