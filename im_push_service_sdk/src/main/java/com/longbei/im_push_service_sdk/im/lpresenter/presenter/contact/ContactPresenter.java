package com.longbei.im_push_service_sdk.im.lpresenter.presenter.contact;

import androidx.recyclerview.widget.DiffUtil;

import com.longbei.im_push_service_sdk.common.basepercenter.data.DataSource;
import com.longbei.im_push_service_sdk.common.widget.recycler.RecyclerAdapter;
import com.longbei.im_push_service_sdk.im.db.User;
import com.longbei.im_push_service_sdk.im.db.utils.DiffUiDataCallback;
import com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.user.ContactDataSource;
import com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.user.ContactRepository;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.BaseSourcePresenter;

import java.util.List;


/**
 * 联系人的Presenter实现
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class ContactPresenter extends BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View>
        implements ContactContract.Presenter, DataSource.SucceedCallback<List<User>> {

    public ContactPresenter(ContactContract.View view) {
        // 初始化数据仓库
        super(new ContactRepository(), view);
    }


    @Override
    public void start() {
        super.start();

        // 加载网络数据
        // TODO: 2020/4/30 放开注释
        //UserHelper.refreshContacts();
    }

    // 运行到这里的时候是子线程
    @Override
    public void onDataLoaded(List<User> users) {
        // 无论怎么操作，数据变更，最终都会通知到这里来
        final ContactContract.View view = getView();
        if (view == null)
            return;

        RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
        List<User> old = adapter.getItems();

        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 调用基类方法进行界面刷新
        refreshData(result, users);
    }
}
