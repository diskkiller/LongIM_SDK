package com.longbei.im_push_service_sdk.im.db.helper;

import com.longbei.im_push_service_sdk.im.db.Group;
import com.longbei.im_push_service_sdk.im.db.GroupMember;
import com.longbei.im_push_service_sdk.im.db.GroupMember_Table;
import com.longbei.im_push_service_sdk.im.db.Group_Table;
import com.longbei.im_push_service_sdk.im.db.User;
import com.longbei.im_push_service_sdk.im.db.User_Table;
import com.longbei.im_push_service_sdk.im.db.viewmodle.MemberUserModel;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 对群的一个简单的辅助工具类
 *
 * @version 1.0.0
 */
public class GroupHelper {

    // 从本地找Group
    public static Group findFromLocal(String groupId) {
        return SQLite.select()
                .from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }



    // 获取一个群的成员数量
    public static long getMemberCount(String id) {
        return SQLite.selectCountOf()
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();
    }



    // 关联查询一个用户和群成员的表，返回一个MemberUserModel表的集合
    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {
        return SQLite.select(GroupMember_Table.alias.withTable().as("alias"),
                User_Table.id.withTable().as("userId"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(GroupMember.class)
                .join(User.class, Join.JoinType.INNER)
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))
                .where(GroupMember_Table.group_id.withTable().eq(groupId))
                .orderBy(GroupMember_Table.user_id, true)
                .limit(size)
                .queryCustomList(MemberUserModel.class);
    }


}
