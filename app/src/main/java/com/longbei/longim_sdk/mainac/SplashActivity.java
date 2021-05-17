package com.longbei.longim_sdk.mainac;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.longbei.im_push_service_sdk.common.app.status.AppStatusConstant;
import com.longbei.im_push_service_sdk.common.app.status.AppStatusManager;
import com.longbei.longim_sdk.R;
import com.longbei.longim_sdk.bean.ADBean;
import com.longbei.longim_sdk.service.ADIntentService;
import com.longbei.longim_sdk.util.DensityUtil;
import com.longbei.longim_sdk.util.SPManager;
import com.longbei.longim_sdk.util.TypeUtil;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private CountDownTimer mDownTimer;
    private boolean mIsStopTimer;
    private TextView mVersionTV;
    private ADBean mAdBean;
    private CountDownTimer mFirstDownTimer;
    private boolean isDown = false;//是否下载图片
    private String imgUrl,title,contentUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        invadeStatusBar();
        //addVersionView();

        downloadADPic();
        mFirstDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("theadintent", " 倒计时开始 ："+millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                //app状态改为正常
                Log.i("AppStatusConstant", " app状态改为正常 ");
                AppStatusManager.getInstance().setAppStatus(AppStatusConstant.STATUS_NORMAL);
                finishDowntimer();
            }
        };
        mFirstDownTimer.start();
        Log.i("theadintent", " 倒计时开始 ");

    }

    private void initNet() {

        //广告图片url
        String imgUrl = "http://img.zcool.cn/community/01e51e581074cda84a0d304ff02d18.png@1280w_1l_2o_100sh.png";
        //广告标题
        String title = "百度首页";
        //广告内容地址（webView呈现）
        String contentUrl = "http://www.baidu.com";
        //最好将ad广告对象封装起来，结构有广告图片url,广告标题，广告内容url；
        mAdBean = new ADBean(title, imgUrl, contentUrl);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //adWork(mAdBean);
            }
        });


    }

    /**
     * 先请求服务器是否需要下载广告图片
     * 首页倒计时开始时就下载，倒计时结束根据图片是否下载成功做不同的处理
     */
    private void downloadADPic(){

        /**
         * 模拟服务器请求返回数据
         * isDown：是否需要更新下载图片，
         * 无论需不需要更新，首页倒计时结束后，
         * 都统一执行倒计时结束后的逻辑，
         * 需要更新就去下载图片，不管下载成不成功，还是存在旧的图片，
         * 只要有图片文件就会显示出来
         */
        //广告图片url
       imgUrl = "http://img.zcool.cn/community/01e51e581074cda84a0d304ff02d18.png@1280w_1l_2o_100sh.png";
        //广告标题
        title = "ad_pic1";
        //广告内容地址（webView呈现）
        contentUrl = "http://www.baidu.com";
        //最好将ad广告对象封装起来，结构有广告图片url,广告标题，广告内容url；
        mAdBean = new ADBean(title, imgUrl, contentUrl);

        if(isDown){
            adWork(mAdBean,title);
        }

    }

    /**
     * 开启服务下载图片
     * @param bean
     * @param title
     */
    private void adWork(ADBean bean,String title) {
            //图片下载后路径会存入sp。之后通过sp拿取数据。
            // 不过建议，本地数据库存多个图片（可以切换显示广告），多图片优先级跟你们后端商定.
            //开启intentService下载图片.并存入本地.用sp来获取图片本地路径
            Intent intent = new Intent(this, ADIntentService.class);
            intent.putExtra("downUrl", bean.getImgUrl());
            intent.putExtra("title", bean.getTitle());
            startService(intent);
            //toast(博客介绍使用,实际开发中应该删除)
            //showToast("本地无图片资源,启动service下载图片");
    }

    /**
     * 首页倒计时结束后的处理
     */
    private void finishDowntimer(){

        Log.i("theadintent", " 倒计时结束 / 没有需要下载更新的图片 ");

        String adFilePath = SPManager.getADFilePath(this,title);

        File file=new File(adFilePath);

        // 如果图片已经下载到了本地，那么就加载该图片。如果没有的话，直接进入首页
        if (!file.exists()) {
            Log.i("theadintent", " 没有图片文件直接进入首页");
            //没有文件直接进入首页
            startActivity(MainActivity.class);
            finish();
        }else{
            Log.i("theadintent", " 存在图片文件，显示广告图片");
            //显示广告图片
            initViews(adFilePath);
        }
    }

    /**
     * 图片下载成功后的处理
     * @param adValue
     */
    private void initViews(String adValue) {
        Log.i("theadintent", "  "+adValue);
        setContentView(R.layout.ad_layout);
        final View layout = findViewById(R.id.splash_rl);
        ImageView imageView = (ImageView) findViewById(R.id.splash_iv);
        final Button button = (Button) findViewById(R.id.splash_bn);
        if (!TypeUtil.isBlank(adValue)) {

            Glide.with(this)
                    .load(adValue)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                            layout.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                        }
                    });
        }
        //显示广告图片，开始倒计时
        mDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                button.setText("跳过 " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                button.setText("跳过 " + 0 + "s");
                if (!mIsStopTimer) {
                    startActivity(MainActivity.class);
                    finish();
                }
            }
        };
        mDownTimer.start();
        //注意，如果点击进入广告内容activity，中断mDownTimer的计时，防止首页打开
        button.setOnClickListener(this);
        layout.setOnClickListener(this);
    }

    private void addVersionView() {
        mVersionTV = new TextView(this);
        mVersionTV.setTextColor(getResources().getColor(R.color.white));
        mVersionTV.setTextSize(14);
        mVersionTV.setText("v 1.0.0");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mVersionTV.setPadding(0, 0, 0, DensityUtil.dp2px(this, 20));
        addContentView(mVersionTV, params);
    }

    public void removeVersionView() {
        if (TypeUtil.isNull(mVersionTV)) {
            return;
        }
        mVersionTV.setVisibility(View.GONE);
    }

    protected void invadeStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.splash_bn:
                //toast(博客介绍使用,实际开发中应该删除)
                showToast("点击跳过");
                stopDownTimer();
                startActivity(MainActivity.class);
                finish();
                break;
            case R.id.splash_rl:
                //toast(博客介绍使用,实际开发中应该删除)
                /*showToast("点击广告图片,进入详情");
                stopDownTimer();
                startActivity(new Intent(SplashActivity.this, CommonActivity.class).putExtra(TYPE_KEY, 4));
*/
                /*Intent intent = new Intent(this, ADActivity.class);
                intent.putExtra(ConstantUtil.Intent.AD_TITLE, mAdBean.getTitle());
                intent.putExtra(ConstantUtil.Intent.AD_CONTENT_URL, mAdBean.getContentUrl());
                startActivity(intent);*/
                finish();
                break;
        }
    }

    private void stopDownTimer() {
        if (mDownTimer != null) {
            mDownTimer.cancel();
        }
        mIsStopTimer = true;
    }

    private void startActivity(Class tClass) {
        startActivity(new Intent(SplashActivity.this, tClass));
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
