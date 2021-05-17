package com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.user;

import com.longbei.im_push_service_sdk.common.basepercenter.data.DataSource;
import com.longbei.im_push_service_sdk.common.basepercenter.repository.BaseDbRepository;
import com.longbei.im_push_service_sdk.im.db.Account;
import com.longbei.im_push_service_sdk.im.db.User;
import com.longbei.im_push_service_sdk.im.db.User_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;


/**
 * 联系人仓库
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource {
    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        super.load(callback);

        // 加载本地数据库数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }
}
