// IPushServiceAidlInterface.aidl
package com.longbei.im_push_service_sdk.aidl;

// Declare any non-default types here with import statements

interface IPushServiceAidlInterface {
    void im_connect(in String version);//上线 通
        boolean appOnline();//程序前台运行
        void setAppOnline(in boolean status);//程序运行状态。前台，后台
        void im_logout();//IM下线
        boolean im_search(in String keyword);//查找好友 通
        boolean newGroupOp(in int op, in List uids);//创建群
        boolean addOrDeleteGroupMembersOp(in int op, in String gid, in List uids);//加好友入群,删除群好友
        boolean retreatGroupMembersOp(in int op, in String gid, in String nextOwnerId);//退出群
        boolean saveGroupConfigOp(in int op, in String gid, in boolean isGroupSaved, in boolean isNewsNotifyShielded);//保存配置信息
        boolean updateGroupNickNameOp(in int op, in String gid, in String nick);//修改群昵称或者群成员昵称
        boolean getGroupDetailsOp(in int op, in String gid);//获取群详细信息
        boolean getUserOp(in int op, in long uid);//获取用户并查看是否是好友
        boolean im_userOp(in int o, in long uid, in String nick, in String avatar, in String text);//加好友 通
        boolean im_info(in int op, in String text);//获得分组，好友列表 调试中
        boolean im_contacts_status(in String contactJson);//手机联系人状态匹配
        boolean im_contacts_upload(in String contactJson);//手机联系人上传
        String im_sendMessage(int type, in long uidorgid, in int contentType, in String content, in String retry,in String avatar,in String nick,in String toHidden,in String user_info,in String selfHidden);//发送消息
        String im_saveMessage(int type, in long uidorgid, in int contentType, in String content, in String retry);//发送消息(针对语音，图片等，只存不发)
        boolean im_update(in int op, in long uid, in String text);//更新各种信息 op 1更新昵称 2更新群里的昵称 3更新好友名称
    	boolean im_giftzsb(in long uid, in int num);//赠送中搜币
    	boolean addGroupMemberOp(in int op, in String gid,in String inviterId,in int mode,in List uids);//加人进群
    	boolean findGroupInfo(in int op, in long group_id, in List memberIds);//查询群信息
    	boolean getMemberDetail(in int op,in long group_id,in long memberId); //获取群成员详细信息 op 9
}
