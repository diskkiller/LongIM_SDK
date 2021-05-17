package com.longbei.im_push_service_sdk.im.lpresenter.presenter.contact;

import com.longbei.im_push_service_sdk.common.app.kit.handler.Run;
import com.longbei.im_push_service_sdk.common.app.kit.handler.runable.Action;
import com.longbei.im_push_service_sdk.common.basepercenter.data.DataSource;
import com.longbei.im_push_service_sdk.common.basepercenter.presenter.BasePresenter;
import com.longbei.im_push_service_sdk.im.db.t_card.UserCard;


/**
 * 关注的逻辑实现
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class FollowPresenter extends BasePresenter<FollowContract.View>
        implements FollowContract.Presenter, DataSource.Callback<UserCard> {

    public FollowPresenter(FollowContract.View view) {
        super(view);
    }

    @Override
    public void follow(String id) {
        start();
        // TODO: 2020/4/30 放开注释
        //UserHelper.follow(id, this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        // 成功
        final FollowContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onFollowSucceed(userCard);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final FollowContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }
}
