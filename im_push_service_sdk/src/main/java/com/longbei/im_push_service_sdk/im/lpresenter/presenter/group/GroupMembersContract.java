package com.longbei.im_push_service_sdk.im.lpresenter.presenter.group;


import com.longbei.im_push_service_sdk.common.basepercenter.presenter.BaseContract;
import com.longbei.im_push_service_sdk.im.db.viewmodle.MemberUserModel;

/**
 * 群成员的契约
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public interface GroupMembersContract {
    interface Presenter extends BaseContract.Presenter {
        // 具有一个刷新的方法
        void refresh();
    }

    // 界面
    interface View extends BaseContract.RecyclerView<Presenter, MemberUserModel> {
        // 获取群的ID
        String getGroupId();
    }
}
