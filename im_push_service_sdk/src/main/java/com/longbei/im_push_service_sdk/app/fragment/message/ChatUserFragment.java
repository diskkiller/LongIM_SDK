package com.longbei.im_push_service_sdk.app.fragment.message;


import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.widget.Toolbar;

import android.hardware.Sensor;
import android.view.MenuItem;
import android.view.View;

import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.im.db.User;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.message.ChatContract;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.message.ChatUserPresenter;

/**
 * 用户聊天界面
 */
public class ChatUserFragment extends ChatFragment<User>
        implements ChatContract.UserView {
    /*@BindView(R2.id.im_portrait)
    PortraitView mPortrait;*/

    private MenuItem mUserInfoMenuItem;

    public ChatUserFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }


    // 进行高度的综合运算，透明我们的头像和Icon
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }


    @Override
    protected ChatContract.Presenter initPresenter() {
        // 初始化Presenter
        return new ChatUserPresenter(mMessageRepository,this, mReceiverId);
    }

    @Override
    public void onInit(User user) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
