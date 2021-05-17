package com.longbei.im_push_service_sdk.im.push.manager;


import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.longbei.im_push_service_sdk.common.Factory;
import com.longbei.im_push_service_sdk.im.push.factory.IMSClientFactory;
import com.longbei.im_push_service_sdk.im.push.interf.IMSClientInterface;
import com.longbei.im_push_service_sdk.im.push.service.IMservice;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       IMSClientBootstrap.java</p>
 * <p>@PackageName:     com.freddy.chat.im</p>
 * <b>
 * <p>@Description:     应用层的imsClient启动器</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/08 00:25</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class PushServiceManager {

    private static final PushServiceManager INSTANCE = new PushServiceManager();
    private IMSClientInterface imsClient;
    private PushServiceManager() {

    }

    public static PushServiceManager getInstance() {
        return INSTANCE;
    }

    public synchronized void init(Context context,String userId, String token, String hosts, int appStatus) {


        System.out.println("开启 IMservice 服务");
        Intent i = new Intent(context, IMservice.class);
        i.putExtra("userId", userId);
        i.putExtra("token", token);
        context.startService(i);
    }


    /**
     * 发送消息
     *
     * @param msg
     */
    public void im_sendMessage(MqttMessage msg) {
        IMSClientFactory.getIMSClient().sendMsg(msg);
    }

    /**
     * 私聊好友的操作
     * @param op 1加好友并发验证内容 2确认成为好友 3拒绝成为好友 4删除好友关系
     * @param uid 对应的好友id
     * @param text 附加信息
     */
    public boolean im_userOp(int op, long uid, String nick, String avatar, String text) throws RemoteException {
        return false;
    };

    /**
     * 根据条件取网络信息
     * @param op 1所有群和所有好友 2所有群 3单个群 4所有好友 5指定好友
     * @param text 当取单个群或好友时，为对应的群id或好友id
     */
    public boolean im_info(int op, String text) throws RemoteException {
        boolean b = false;
        if (text == null)
            text = "";
        return b;
    }

    /**
     * 搜索用户
     */
    public boolean im_search(String keyword) throws RemoteException {
        return false;
    }


    /**
     * 手机联系人状态
     */
    public boolean im_contacts_status(String contactJson) throws RemoteException {
        return false;
    }

    /**
     * IM下线
     */
    public void im_logout() throws RemoteException {
    }


    /**
     * 更新各种信息
     * @param op 1更新昵称 2更新群里的昵称 3更新好友名称
     * @param text
     */
    public boolean im_update(int op, long uid, String text) throws RemoteException {
        return false;
    }

    public String im_saveMessage(int type, long uidorgid, int contentType,
                                 String content, String retry) throws RemoteException {
        return null;
    }

    /**
     * 建群
     */
    public boolean newGroupOp(int op, List uids) throws RemoteException {

        return false;
    }

    /**
     * 加好友入群或者删除好友
     */
    public boolean addOrDeleteGroupMembersOp(int op, String gid, List uids)
            throws RemoteException {
        return false;
    }

    /**
     * 退群
     */
    public boolean retreatGroupMembersOp(int op, String gid,
                                         String nextOwnerId) throws RemoteException {
        return false;
    }

    /**
     * 保存设置信息
     */
    public boolean saveGroupConfigOp(int op, String gid,
                                     boolean isGroupSaved, boolean isNewsNotifyShielded)
            throws RemoteException {
        return false;
    }

    /**
     * 修改群昵称或者修改群成员昵称
     */
    public boolean updateGroupNickNameOp(int op, String gid, String nick)
            throws RemoteException {
        return false;
    }

    /**
     * 获取群详细信息
     */
    public boolean getGroupDetailsOp(int op, String gid)
            throws RemoteException {
        return false;
    }


    public boolean addGroupMemberOp(int op, String gid, String inviterId, int mode, List uids) throws RemoteException {
        return false;
    }


    public boolean findGroupInfo(int op, long group_id, List memberIds) throws RemoteException {
        return false;
    }

    public boolean getMemberDetail(int op, long group_id, long memberId) throws RemoteException {
        return false;
    }

//

    public boolean getUserOp(int op, long uid) throws RemoteException {
        return false;
    }


}
