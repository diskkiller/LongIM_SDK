package com.longbei.im_push_service_sdk.im.lpresenter.presenter.message;

import androidx.recyclerview.widget.DiffUtil;

import com.longbei.im_push_service_sdk.im.db.Session;
import com.longbei.im_push_service_sdk.im.db.utils.DiffUiDataCallback;
import com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.message.SessionDataSource;
import com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.message.SessionRepository;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.BaseSourcePresenter;

import java.util.List;

/**
 * 最近聊天列表的Presenter
 *
 * @version 1.0.0
 */
public class SessionPresenter extends BaseSourcePresenter<Session, Session,
        SessionDataSource, SessionContract.View> implements SessionContract.Presenter {

    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view = getView();
        if (view == null)
            return;

        // 差异对比
        List<Session> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(old, sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 刷新界面
        refreshData(result, sessions);
    }
}
