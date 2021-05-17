package com.longbei.im_push_service_sdk.im.lpresenter.presenter.contact;


import com.longbei.im_push_service_sdk.common.basepercenter.presenter.BaseContract;
import com.longbei.im_push_service_sdk.im.db.User;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public interface ContactContract {
    // 什么都不需要额外定义，开始就是调用start即可
    interface Presenter extends BaseContract.Presenter {

    }

    // 都在基类完成了
    interface View extends BaseContract.RecyclerView<Presenter, User> {

    }
}
