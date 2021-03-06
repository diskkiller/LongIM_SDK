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
     * MainActivity ???????????????
     *
     * @param context ?????????
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void restartApp(Context context) {
        /*????????????????????????APP*/
        startActivity(new Intent(this, SplashActivity.class));
        finish();
        return;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        /*if (Account.isComplete()) {
            // ?????????????????????????????????????????????????????????
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

        // ??????????????????????????????
        mNavHelper = new NavHelper<>(this, R.id.lay_container,
                getSupportFragmentManager(), this);
        mNavHelper
                .add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
//                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
//                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_web, new NavHelper.Tab<>(WebFragment.class, R.string.title_web))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));


        // ????????????????????????????????????
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

    //?????????????????????
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
                                "?????????", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(Permission[] permissions) {

                        for (Permission permission : permissions) {
                            if (permission.shouldRationale()) {
//                                Toast.makeText(LoginActivity.this, permission.toString() +
//                                        " ???????????? ", Toast.LENGTH_SHORT).show();

                            } else {
//                                Toast.makeText(LoginActivity.this, permission.toString() +
//                                        " ?????????????????????????????????", Toast.LENGTH_SHORT).show();
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
                        "?????????", Toast.LENGTH_SHORT).show();            }

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

        // ??????????????????????????????Menu?????????????????????????????????????????????
        Menu menu = mNavigation.getMenu();
        // ??????????????????Home
        menu.performIdentifierAction(R.id.action_home, 0);

        // ?????????????????????
        //mPortrait.setup(Glide.with(this), getUser());
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        //PersonalActivity.show(this, Account.getUserId());
    }

    /*@OnClick(R.id.im_search)
    void onSearchMenuClick() {
        // ????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????
        *//*int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group) ?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);*//*
    }*/

    @OnClick(R.id.btn_action)
    void onActionClick() {
        // ?????????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????
        if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)) {
            // ?????????????????????
            //GroupCreateActivity.show(this);
        } else {
            // ????????????????????????????????????????????????
            //SearchActivity.show(this, SearchActivity.TYPE_USER);
        }
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param item MenuItem
     * @return True ????????????????????????????????????
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // ??????????????????????????????
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     * NavHelper ????????????????????????
     *
     * @param newTab ??????Tab
     * @param oldTab ??????Tab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        // ?????????????????????????????????Title??????Id
        mTitle.setText(newTab.extra);


        // ?????????????????????????????????????????????
        /*float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            // ??????????????????
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            // transY ?????????0 ?????????
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                // ???
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                // ?????????
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }

        // ????????????
        // ?????????Y????????????????????????????????????
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
            Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
            mTime = timeMillis;
            return;
        }
        super.onBackPressed();

        //String path = Environment.getExternalStorageDirectory() + File.separator;

        /*FileUtil.clearAllCacheFile(MainActivity.this);
        String adFilePath = SPManager.getADFilePath(this);
        File file=new File(adFilePath);

        //??????????????????url??????????????????????????????????????????????????????
        if (file.exists()) {
            FileUtil.deleteFile(file);
        }*/
        finish();
    }
}
