package com.longbei.longim_sdk.mainac;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.longbei.im_push_service_sdk.app.fragment.main.ActiveFragment;
import com.longbei.im_push_service_sdk.app.fragment.main.ContactFragment;
import com.longbei.im_push_service_sdk.app.fragment.main.GroupFragment;
import com.longbei.longim_sdk.fragments.WebFragment;
import com.longbei.im_push_service_sdk.common.app.Activity;
import com.longbei.im_push_service_sdk.common.app.kit.ui.widget.FloatActionButton;
import com.longbei.im_push_service_sdk.common.app.tools.NavHelper;
import com.longbei.longim_sdk.R;
import com.longbei.longim_sdk.dialog.PictureCustomDialog;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.bean.Special;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.qw.soul.permission.callbcak.GoAppDetailCallBack;
import com.qw.soul.permission.callbcak.SpecialPermissionListener;


import java.util.Objects;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {

    @BindView(R.id.appbar)
    View mLayAppbar;



    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavHelper;
    private long mTime;

    /**
     * MainActivity 显示的入口
     *
     * @param context 上下文
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void restartApp(Context context) {
        /*跳转引导页，重启APP*/
        startActivity(new Intent(this, SplashActivity.class));
        finish();
        return;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        /*if (Account.isComplete()) {
            // 判断用户信息是否完全，完全则走正常流程
            return super.initArgs(bundle);
        } else {
            UserActivity.show(this);
            return false;
        }*/
        return super.initArgs(bundle);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        checkAndRequestPermission();
        checkSpecialPermission();

        // 初始化底部辅助工具类
        mNavHelper = new NavHelper<>(this, R.id.lay_container,
                getSupportFragmentManager(), this);
        mNavHelper
                .add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
//                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
//                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_web, new NavHelper.Tab<>(WebFragment.class, R.string.title_web))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));


        // 添加对底部按钮点击的监听
        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, Drawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        this.view.setBackground(resource.getCurrent());

                    }

                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        }
    }

    //请求悬浮窗权限
    @TargetApi(Build.VERSION_CODES.M)
    private void getOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }


    private void checkAndRequestPermission() {
        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(
                        Manifest.permission.READ_PHONE_STATE
                        ),
                new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] permissions) {
                        Toast.makeText(MainActivity.this,
                                "已授权", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(Permission[] permissions) {

                        for (Permission permission : permissions) {
                            if (permission.shouldRationale()) {
//                                Toast.makeText(LoginActivity.this, permission.toString() +
//                                        " 点击拒绝 ", Toast.LENGTH_SHORT).show();

                            } else {
//                                Toast.makeText(LoginActivity.this, permission.toString() +
//                                        " 点击拒绝并勾选不再提示", Toast.LENGTH_SHORT).show();
                            }

                        }
                        boolean checkResult = SoulPermission.getInstance().checkSpecialPermission(Special.NOTIFICATION);

                        show_tips();

                    }

                });
    }

    private void checkSpecialPermission(){
        SoulPermission.getInstance().checkAndRequestPermission(Special.SYSTEM_ALERT, new SpecialPermissionListener() {
            @Override
            public void onGranted(Special permission) {
                Toast.makeText(MainActivity.this,
                        "已授权", Toast.LENGTH_SHORT).show();            }

            @Override
            public void onDenied(Special permission) {
                show_tips();            }
        });
    }





    private void show_tips(){

        final PictureCustomDialog dialog =
                new PictureCustomDialog(this, com.luck.picture.lib.R.layout.picture_wind_base_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button btn_cancel = dialog.findViewById(com.luck.picture.lib.R.id.btn_cancel);
        Button btn_commit = dialog.findViewById(com.luck.picture.lib.R.id.btn_commit);
        btn_commit.setText(getString(com.luck.picture.lib.R.string.picture_go_setting));
        TextView tvTitle = dialog.findViewById(com.luck.picture.lib.R.id.tvTitle);
        TextView tv_content = dialog.findViewById(com.luck.picture.lib.R.id.tv_content);
        tvTitle.setText(getString(com.luck.picture.lib.R.string.picture_prompt));
        tv_content.setText(R.string.string_help_text);
        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        btn_commit.setOnClickListener(v -> {
            goApplicationSettings();
            dialog.dismiss();
        });
        dialog.show();

    }

    public void goApplicationSettings(){
        SoulPermission.getInstance().goApplicationSettings(new GoAppDetailCallBack() {
            @Override
            public void onBackFromAppDetail(Intent data) {
                checkAndRequestPermission();
            }
        });
    }


    @Override
    protected void initData() {
        super.initData();

        // 从底部导中接管我们的Menu，然后进行手动的触发第一次点击
        Menu menu = mNavigation.getMenu();
        // 触发首次选中Home
        menu.performIdentifierAction(R.id.action_home, 0);

        // 初始化头像加载
        //mPortrait.setup(Glide.with(this), getUser());
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        //PersonalActivity.show(this, Account.getUserId());
    }

    /*@OnClick(R.id.im_search)
    void onSearchMenuClick() {
        // 在群的界面的时候，点击顶部的搜索就进入群搜索界面
        // 其他都为人搜索的界面
        *//*int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group) ?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);*//*
    }*/

    @OnClick(R.id.btn_action)
    void onActionClick() {
        // 浮动按钮点击时，判断当前界面是群还是联系人界面
        // 如果是群，则打开群创建的界面
        if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)) {
            // 打开群创建界面
            //GroupCreateActivity.show(this);
        } else {
            // 如果是其他，都打开添加用户的界面
            //SearchActivity.show(this, SearchActivity.TYPE_USER);
        }
    }

    /**
     * 当我们的底部导航被点击的时候触发
     *
     * @param item MenuItem
     * @return True 代表我们能够处理这个点击
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 转接事件流到工具类中
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     * NavHelper 处理后回调的方法
     *
     * @param newTab 新的Tab
     * @param oldTab 就的Tab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        // 从额外字段中取出我们的Title资源Id
        mTitle.setText(newTab.extra);


        // 对浮动按钮进行隐藏与显示的动画
        /*float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            // 主界面时隐藏
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            // transY 默认为0 则显示
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                // 群
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                // 联系人
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }

        // 开始动画
        // 旋转，Y轴位移，弹性差值器，时间
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();*/


    }

    @Override
    public void onBackPressed() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - mTime > 2000) {
            Toast.makeText(this, "再点一次退出", Toast.LENGTH_SHORT).show();
            mTime = timeMillis;
            return;
        }
        super.onBackPressed();

        //String path = Environment.getExternalStorageDirectory() + File.separator;

        /*FileUtil.clearAllCacheFile(MainActivity.this);
        String adFilePath = SPManager.getADFilePath(this);
        File file=new File(adFilePath);

        //本地没有图片url对应的本地路径（之前没有下载该图片）
        if (file.exists()) {
            FileUtil.deleteFile(file);
        }*/
        finish();
    }
}
