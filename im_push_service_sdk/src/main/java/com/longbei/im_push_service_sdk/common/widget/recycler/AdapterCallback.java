package com.longbei.im_push_service_sdk.common.widget.recycler;

/**
 * @version 1.0.0
 */
public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
