package com.longbei.im_push_service_sdk.app.fragment.message.picactivitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longbei.im_push_service_sdk.R;
import com.luck.picture.lib.PictureBaseActivity;
import com.luck.picture.lib.PictureSelectorPreviewWeChatStyleActivity;
import com.luck.picture.lib.adapter.PictureSimpleFragmentAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.widget.PreviewViewPager;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

/**
 * @描述:图片预览
 */
public class ChatPicturePreviewActivity extends PictureBaseActivity implements
        View.OnClickListener, PictureSimpleFragmentAdapter.OnCallBackActivity {
    private static final String TAG = ChatPicturePreviewActivity.class.getSimpleName();
    protected ImageView pictureLeftBack;
    protected TextView tvMediaNum, tvTitle, mTvPictureMore;
    protected PreviewViewPager viewPager;
    protected int position;
    protected PictureSimpleFragmentAdapter adapter;
    protected int index;
    protected int screenWidth;
    protected Handler mHandler;
    protected RelativeLayout selectBarLayout;
    protected View titleViewBg;
    private int mPage = 0;
    List<LocalMedia> data;

    @Override
    public int getResourceId() {
        return R.layout.chat_picture_preview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mHandler = new Handler();
        titleViewBg = findViewById(R.id.titleViewBg);
        screenWidth = ScreenUtils.getScreenWidth(this);
        pictureLeftBack = findViewById(R.id.pictureLeftBack);
        viewPager = findViewById(R.id.preview_pager);
        pictureLeftBack.setOnClickListener(this);
        mTvPictureMore = findViewById(R.id.tv_more);
        tvMediaNum = findViewById(R.id.tvMediaNum);
        selectBarLayout = findViewById(R.id.select_bar_layout);
        mTvPictureMore.setOnClickListener(this);
        tvMediaNum.setOnClickListener(this);
        tvTitle = findViewById(R.id.picture_title);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        tvMediaNum.setSelected(config.checkNumMode);
        data = getIntent().
                getParcelableArrayListExtra(PictureConfig.EXTRA_PREVIEW_DATA);
        initViewPageAdapterData(data);
        setTitle();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (getContext() instanceof PictureSelectorPreviewWeChatStyleActivity) {
                    //  不做处理
                }
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                setTitle();
                LocalMedia media = adapter.getItem(position);
                if (media == null) {
                    return;
                }
                index = media.getPosition();
                onPageSelectedChange(media);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }



    @Override
    protected void initCompleteText(int startCount) {

    }

    /**
     * ViewPage滑动数据变化回调
     *
     * @param media
     */
    protected void onPageSelectedChange(LocalMedia media) {

    }

    /**
     * 动态设置相册主题
     */
    @Override
    public void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureTitleTextColor != 0) {
                tvTitle.setTextColor(config.style.pictureTitleTextColor);
            }
            if (config.style.pictureTitleTextSize != 0) {
                tvTitle.setTextSize(config.style.pictureTitleTextSize);
            }
            if (config.style.pictureLeftBackIcon != 0) {
                pictureLeftBack.setImageResource(config.style.pictureLeftBackIcon);
            }
            if (config.style.picturePreviewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(config.style.picturePreviewBottomBgColor);
            }
            if (config.style.pictureCheckNumBgStyle != 0) {
                tvMediaNum.setBackgroundResource(config.style.pictureCheckNumBgStyle);
            }
            if (config.style.pictureUnCompleteTextColor != 0) {
                mTvPictureMore.setTextColor(config.style.pictureUnCompleteTextColor);
            }
            if (!TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                mTvPictureMore.setText(config.style.pictureUnCompleteText);
            }
        }
        titleViewBg.setBackgroundColor(colorPrimary);
    }


    /**
     * 初始化ViewPage数据
     *
     * @param list
     */
    private void initViewPageAdapterData(List<LocalMedia> list) {
        adapter = new PictureSimpleFragmentAdapter(config, this);
        adapter.bindData(list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        setTitle();
        LocalMedia media = adapter.getItem(position);
        if (media != null) {
            index = media.getPosition();
        }
    }


    private void onPreview(int position) {
       // Bundle bundle = new Bundle();
        //bundle.putInt(PictureConfig.EXTRA_POSITION, position);
        //bundle.putParcelableArrayList(PictureConfig.EXTRA_PREVIEW_DATA, (ArrayList<? extends Parcelable>) data);
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, ChatPictureSelectorActivity.class);
            //ntent.putExtras(bundle);
            startActivity(intent);
        }

        overridePendingTransition(com.luck.picture.lib.R.anim.picture_anim_enter,
                com.luck.picture.lib.R.anim.picture_anim_fade_in);
    }



    /**
     * 设置标题
     */
    private void setTitle() {

        tvTitle.setText(getString(com.luck.picture.lib.R.string.picture_preview_image_num,
                position + 1, adapter.getSize()));
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pictureLeftBack) {
            onBackPressed();

        } else if (id == R.id.tv_more) {

            onPreview(position);
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        if (config.windowAnimationStyle != null
                && config.windowAnimationStyle.activityPreviewExitAnimation != 0) {
            finish();
            overridePendingTransition(0, config.windowAnimationStyle != null
                    && config.windowAnimationStyle.activityPreviewExitAnimation != 0 ?
                    config.windowAnimationStyle.activityPreviewExitAnimation : com.luck.picture.lib.R.anim.picture_anim_exit);
        } else {
            closeActivity();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isOnSaveInstanceState) {
            ImagesObservable.getInstance().clearPreviewMediaData();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (adapter != null) {
            adapter.clear();
        }
    }

    @Override
    public void onActivityBackPressed() {
        onBackPressed();
    }

}
