package com.longbei.im_push_service_sdk.common.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.gyf.barlibrary.ImmersionBar;
import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.common.app.status.AppStatusConstant;
import com.longbei.im_push_service_sdk.common.app.status.AppStatusManager;
import com.longbei.im_push_service_sdk.common.app.widget.convention.PlaceHolderView;

import java.util.List;

import butterknife.ButterKnife;

/**
 */

public abstract class Activity extends AppCompatActivity {

    protected PlaceHolderView mPlaceHolderView;
    private String TAG = "AppStatusConstant";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this).barColor(R.color.app_add_widget).init();

        Log.e(TAG, "getAppStatus ::"+AppStatusManager.getInstance().getAppStatus());

        switch (AppStatusManager.getInstance().getAppStatus()) {
            case AppStatusConstant.STATUS_FORCE_KILLED:
                Log.i(TAG, "STATUS_FORCE_KILLED");
                /*处理APP被强杀*/
                restartApp(this);
                break;
            case AppStatusConstant.STATUS_KICK_OUT:
                /*处理APP被退出登录*/
                Log.i(TAG, "STATUS_KICK_OUT");
                break;
            case AppStatusConstant.STATUS_NORMAL:
                /*APP正常状态*/
                Log.i(TAG, "STATUS_NORMAL");
                break;
        }

        // 在界面未初始化之前调用的初始化窗口
        initWidows();

        if (initArgs(getIntent().getExtras())) {
            // 得到界面Id并设置到Activity界面中
            int layId = getContentLayoutId();
            setContentView(layId);
            initBefore();
            initWidget();
            initData();
        } else {
            finish();
        }

    }

    /**
     * 处理APP被强杀,交给子类自己去处理
     */
    /**
     * 重启app
     * @param context
     */
    protected  void restartApp(Context context) {

    }

    /**
     * 初始化控件调用之前
     */
    protected void initBefore() {

    }

    /**
     * 初始化窗口
     */
    protected void initWidows() {
    }

    /**
     * 初始化相关参数
     *
     * @param bundle 参数Bundle
     * @return 如果参数正确返回True，错误返回False
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 得到当前界面的资源文件Id
     *
     * @return 资源文件Id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }


    @Override
    public boolean onSupportNavigateUp() {
        // 当点击界面导航返回时，Finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // 得到当前Activity下的所有Fragment
        @SuppressLint("RestrictedApi")
        List<androidx.fragment.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        // 判断是否为空
        if (fragments != null && fragments.size() > 0) {
            for (androidx.fragment.app.Fragment fragment : fragments) {
                // 判断是否为我们能够处理的Fragment类型
                if (fragment instanceof Fragment) {
                    // 判断是否拦截了返回按钮
                    if (((Fragment) fragment).onBackPressed()) {
                        // 如果有直接Return
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
        finish();
    }

    /**
     * 设置占位布局
     *
     * @param placeHolderView 继承了占位布局规范的View
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
    }
}
