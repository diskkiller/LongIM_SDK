package com.longbei.im_push_service_sdk.app.fragment.main;


import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.R2;
import com.longbei.im_push_service_sdk.app.activitys.MessageActivity;
import com.longbei.im_push_service_sdk.common.app.PresenterFragment;
import com.longbei.im_push_service_sdk.common.app.widget.EmptyView;
import com.longbei.im_push_service_sdk.common.app.widget.PortraitView;
import com.longbei.im_push_service_sdk.common.widget.recycler.RecyclerAdapter;
import com.longbei.im_push_service_sdk.im.db.User;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.contact.ContactContract;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.contact.ContactPresenter;


import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ContactFragment extends PresenterFragment<ContactContract.Presenter>
        implements ContactContract.View {

    @BindView(R2.id.empty)
    EmptyView mEmptyView;

    @BindView(R2.id.recycler)
    RecyclerView mRecycler;

    // 适配器，User，可以直接从数据库查询数据
    private RecyclerAdapter<User> mAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);


//        mPlaceHolderView.triggerOkOrEmpty(true);



       /* // 初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<User>() {
            @Override
            protected int getItemViewType(int position, User userCard) {
                // 返回cell的布局id
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }
        });

        // 点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                // 跳转到聊天界面
                MessageActivity.show(getContext(), user);
            }
        });*/

        // 初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);



        User user = new User();
        user .setId("123");

        // 跳转到聊天界面
        MessageActivity.show(getContext(), user);

    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        // 进行一次数据加载
//        mPresenter.start();
    }

    @Override
    protected ContactContract.Presenter initPresenter() {
        // 初始化Presenter
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 进行界面操作
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    @Override
    public void onAdapterDataChanged(List<User> dataList) {

    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<User> {
        @BindView(R2.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R2.id.txt_name)
        TextView mName;

        @BindView(R2.id.txt_desc)
        TextView mDesc;


        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(ContactFragment.this), user);
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }

        @OnClick(R2.id.im_portrait)
        void onPortraitClick() {
            // 显示信息
            // TODO: 2020/4/30
            //PersonalActivity.show(getContext(), mData.getId());
        }
    }
}
