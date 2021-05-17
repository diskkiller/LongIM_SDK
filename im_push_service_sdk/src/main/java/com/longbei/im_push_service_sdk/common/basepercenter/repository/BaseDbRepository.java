package com.longbei.im_push_service_sdk.common.basepercenter.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.longbei.im_push_service_sdk.common.app.kit.reflect.Reflector;
import com.longbei.im_push_service_sdk.common.basepercenter.data.DbDataSource;
import com.longbei.im_push_service_sdk.im.db.BaseDbModel;
import com.longbei.im_push_service_sdk.im.db.helper.DbHelper;
import com.longbei.im_push_service_sdk.im.utils.CollectionUtil;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;


import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础的数据库仓库
 * 实现对数据库的基本的监听操作
 *
 * @version 1.0.0
 */
public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements DbDataSource<Data>,
        DbHelper.ChangedListener<Data>,
        QueryTransaction.QueryResultListCallback<Data> {
    // 和Presenter交互的回调
    private SucceedCallback<List<Data>> callback;
    protected final LinkedList<Data> dataList = new LinkedList<>(); // 当前缓存的数据
    private Class<Data> dataClass; // 当前范型对应的真实的Class信息

    @SuppressWarnings("unchecked")
    public BaseDbRepository() {
        // 拿当前类的范型数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SucceedCallback<List<Data>> callback) {

        Log.i("datainfo","BaseDbRepository  load : 加载数据 注册观察者，对数据库监听操作 : "+dataClass.getSimpleName());

        this.callback = callback;
        // 进行数据库监听操作
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        // 取消监听，销毁数据
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }

    // 数据库统一通知的地方：增加／更改
    @Override
    public void onDataSave(Data[] list) {
        boolean isChanged = false;
        // 当数据库数据变更的操作
        for (Data data : list) {
            // 是关注的人，同时不是我自己
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }

        Log.i("datainfo","BaseDbRepository  onDataSave : 增加／更改  当数据库数据变更的操作 :"
                +dataClass.getSimpleName()+" 是否有数据变更 isChanged :"+isChanged);

        // 有数据变更，则进行界面刷新
        if (isChanged)
            notifyDataChange();

    }


    @Override
    public void test_onDataSave(String obj) {
        System.out.println("处理推送来的消息,当数据库数据变更的操作");
        test_notifyDataChange(obj);
    }

    // 数据库统一通知的地方：删除
    @Override
    public void onDataDelete(Data[] list) {
        // 在删除情况下不用进行过滤判断
        // 但数据库数据删除的操作
        boolean isChanged = false;
        for (Data data : list) {
            if (dataList.remove(data))
                isChanged = true;
        }

        Log.i("datainfo","BaseDbRepository  onDataDelete : 删除  当数据库数据变更的操作 :"
                +dataClass.getSimpleName()+" 是否有数据变更 isChanged :"+isChanged);

        // 有数据变更，则进行界面刷新
        if (isChanged)
            notifyDataChange();



    }

    // DbFlow 框架通知的回调
    @Override
    public void onListQueryResult(QueryTransaction transaction,
                                  @NonNull List<Data> tResult) {

        Log.i("datainfo","BaseDbRepository  onListQueryResult :数据查询通知的回调 :"
                +dataClass.getSimpleName()+" load 加载数据 数据库加载数据成功 回到数据集更新的操作中 ：onDataSave");
        // 数据库加载数据成功
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();
            return;
        }

        // 转变为数组
        Data[] users = CollectionUtil.toArray(tResult, dataClass);
        // 回到数据集更新的操作中
        onDataSave(users);

    }

    // 插入或者更新
    private void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {
            replace(index, data);
        } else {
            insert(data);
        }
    }

    // 更新操作，更新某个坐标下的数据
    protected void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    // 添加方法
    protected void insert(Data data) {
        dataList.add(data);
    }


    // 查询一个数据是否在当前的缓存数据中，如果在则返回坐标
    protected int indexOf(Data newData) {
        int index = -1;
        for (Data data : dataList) {
            index++;
            if (data.isSame(newData)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * 检查一个User是否是我需要关注的数据
     *
     * @param data Data
     * @return True是我关注的数据
     */
    protected abstract boolean isRequired(Data data);

    /**
     * 添加数据库的监听操作
     */
    protected void registerDbChangedListener() {
        System.out.println("registerDbChangedListener.....");
        DbHelper.addChangedListener(dataClass, this);
    }

    /**
     * 通知界面刷新的方法  由presenter进行实现在调度到界面
     */
    private void notifyDataChange() {

        Log.i("datainfo","BaseDbRepository  notifyDataChange :"
                +dataClass.getSimpleName()+" 有数据变更 通知界面刷新 由presenter进行实现在调度到界面callback.onDataLoaded");


        SucceedCallback<List<Data>> callback = this.callback;
        if (callback != null)
            callback.onDataLoaded(dataList);

        // TODO: 2020/4/29 数据监听测试临时修改

/*
        SucceedCallback<List<Data>> callback = this.callback;
        if (callback != null)
            callback.test_onDataLoaded("123");*/
    }

    private void test_notifyDataChange(String str) {
        SucceedCallback<List<Data>> callback = this.callback;
        if (callback != null)
            callback.test_onDataLoaded(str);
    }

}
