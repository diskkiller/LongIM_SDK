package com.longbei.im_push_service_sdk.im.db.helper;

import com.longbei.im_push_service_sdk.im.db.Session;
import com.longbei.im_push_service_sdk.im.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;


/**
 * 会话辅助工具类
 *
 * @version 1.0.0
 */
public class SessionHelper {
    // 从本地查询Session
    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
