package com.longbei.im_push_service_sdk.im.lpresenter.presenter;

import com.longbei.im_push_service_sdk.common.basepercenter.data.DataSource;
import com.longbei.im_push_service_sdk.common.basepercenter.data.DbDataSource;
import com.longbei.im_push_service_sdk.common.basepercenter.presenter.BaseContract;
import com.longbei.im_push_service_sdk.common.basepercenter.presenter.BaseRecyclerPresenter;


import java.util.List;

/**
 * 基础的仓库源的Presenter定义
 * SucceedCallback 由数据中心Repository进行调度
 *
 * @version 1.0.0
 */
public abstract class BaseSourcePresenter<Data, ViewModel,
        Source extends DbDataSource<Data>,
        View extends BaseContract.RecyclerView>
        extends BaseRecyclerPresenter<ViewModel, View>
        implements DataSource.SucceedCallback<List<Data>> {

    protected Source mSource;

    public BaseSourcePresenter(Source source, View view) {
        super(view);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource != null)
            mSource.load(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource = null;
    }
}
