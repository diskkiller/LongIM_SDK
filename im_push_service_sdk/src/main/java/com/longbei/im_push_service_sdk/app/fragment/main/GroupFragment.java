package com.longbei.im_push_service_sdk.app.fragment.main;


import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.common.app.Fragment;

public class GroupFragment extends Fragment{

    private WebView mWV;
    private android.widget.TextView tx_web1,tx_web2;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

}
