package com.longbei.im_push_service_sdk.app.fragment.message;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.os.PowerManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.R2;
import com.longbei.im_push_service_sdk.app.activitys.MessageActivity;
import com.longbei.im_push_service_sdk.app.fragment.message.picactivitys.ChatPicturePreviewActivity;
import com.longbei.im_push_service_sdk.app.fragment.panel.PanelFragment;
import com.longbei.im_push_service_sdk.app.pictureselector.GlideCacheEngine;
import com.longbei.im_push_service_sdk.app.pictureselector.GlideEngine;
import com.longbei.im_push_service_sdk.app.pictureselector.GlideRatioScaleTransForm;
import com.longbei.im_push_service_sdk.common.Common;
import com.longbei.im_push_service_sdk.common.app.Application;
import com.longbei.im_push_service_sdk.common.app.PresenterFragment;
import com.longbei.im_push_service_sdk.common.app.face.Face;
import com.longbei.im_push_service_sdk.common.app.kit.airpanel.AirPanel;
import com.longbei.im_push_service_sdk.common.app.kit.airpanel.Util;
import com.longbei.im_push_service_sdk.common.app.kit.handler.Run;
import com.longbei.im_push_service_sdk.common.app.kit.handler.runable.Action;
import com.longbei.im_push_service_sdk.common.app.kit.ui.Ui;
import com.longbei.im_push_service_sdk.common.app.kit.ui.compat.UiCompat;
import com.longbei.im_push_service_sdk.common.app.kit.ui.widget.Loading;
import com.longbei.im_push_service_sdk.common.app.recordaudio.ChatRecordManager;
import com.longbei.im_push_service_sdk.common.app.tools.AudioPlayHelper;
import com.longbei.im_push_service_sdk.common.app.widget.PortraitView;
import com.longbei.im_push_service_sdk.common.widget.adapter.TextWatcherAdapter;
import com.longbei.im_push_service_sdk.common.widget.recycler.RecyclerAdapter;
import com.longbei.im_push_service_sdk.im.db.Account;
import com.longbei.im_push_service_sdk.im.db.Message;
import com.longbei.im_push_service_sdk.im.db.Message_Table;
import com.longbei.im_push_service_sdk.im.db.User;
import com.longbei.im_push_service_sdk.im.db.utils.FileCache;
import com.longbei.im_push_service_sdk.im.lpresenter.dataCenter.message.MessageRepository;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.message.ChatContract;
import com.longbei.im_push_service_sdk.im.push.Utils.StringUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureCustomDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;
import com.qw.soul.permission.callbcak.GoAppDetailCallBack;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 */
public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel>, PanelFragment.PanelCallback,SensorEventListener {

    private static final String TAG = "ChatFragment";
    protected String mReceiverId;
    protected Adapter mAdapter;
    protected MessageRepository mMessageRepository;
    private int  PAGE = 1;

    @BindView(R2.id.recycler)
    RecyclerView mRecyclerView;



    @BindView(R2.id.edit_content)
    EditText mContent;

    @BindView(R2.id.btn_submit)
    View mSubmit;

    @BindView(R2.id.im_longClickToSay)
    Button say;

    @BindView(R2.id.im_key_layout)
    LinearLayout write;

    @BindView(R2.id.btn_record)
    ImageButton record;

    // 控制顶部面板与软键盘过度的Boss控件
    private AirPanel.Boss mPanelBoss;
    private PanelFragment mPanelFragment;

    // 语音的基础
    private FileCache<AudioHolder> mAudioFileCache;
    private AudioPlayHelper<AudioHolder> mAudioPlayer;
    private View buttonview;
    private View lay_content;
    private int lastCompletelyVisibleItemPosition;
    private int firstCompletelyVisibleItemPosition;
    private int themeId;
    private PictureParameterStyle mPictureParameterStyle;
    private PictureCropParameterStyle mCropParameterStyle;
    private PictureWindowAnimationStyle mWindowAnimationStyle;
    private boolean mKeyboardOpen;
    private ChatRecordManager manager;

    private AudioManager audioManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private String curAudiofilePath;
    private AudioHolder curAudioholder;
    private boolean isAudioPlaying = false;
    private boolean isFirstLoad;
    private boolean isMoreData = true;
    private MyResultCallback mPicResultCallback;
    private boolean isToDownFirst = false;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notificationManager;
    private LinearLayoutManager layout;


    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }


    @Override
    protected final int getContentLayoutId() {
        return R.layout.fragment_chat_common;
    }

    // 得到顶部布局的资源Id
    @LayoutRes
    protected abstract int getHeaderLayoutId();

    @Override
    protected void initWidget(View root) {
        //getActivity().getWindow().setBackgroundDrawableResource(R.drawable.bg_src_girl);

        // 在这里进行了控件绑定
        super.initWidget(root);

        themeId = R.style.picture_WeChat_style;
        getWeChatStyle();

        initStartPicActivity();

        initEditContent();

        initSensorEvent();

        setRecordManager();


        notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        // 初始化面板操作
        mPanelBoss = (AirPanel.Boss) root.findViewById(R.id.lay_content);
        mPanelBoss.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                // 请求隐藏软键盘
                Util.hideKeyboard(mContent);
            }
        });
        mPanelBoss.setOnStateChangedListener(new AirPanel.OnStateChangedListener() {
            @Override
            public void onPanelStateChanged(boolean isOpen) {
                // 面板改变
                if (isOpen)
                    onBottomPanelOpened();
            }

            @Override
            public void onSoftKeyboardStateChanged(boolean isOpen) {
                // 软键盘改变
                mKeyboardOpen = isOpen;
                Log.i(TAG, "软键盘改变:  "+mKeyboardOpen);
                if (isOpen)
                    onBottomPanelOpened();
            }
        });
        mPanelFragment = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        mPanelFragment.setup(this);


        // RecyclerView基本设置
        layout = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layout);
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        // 添加适配器监听器，进行点击的实现
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Message>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Message message) {

                if (mPanelBoss.isOpen()) {
                    // 关闭面板并且返回true代表自己已经处理了消费了返回
                    mPanelBoss.closePanel();
                    return;
                }else if(mKeyboardOpen){
                    // 请求隐藏软键盘
                    Util.hideKeyboard(mContent);
                    return;
                }

                if (message.getType() == Message.TYPE_AUDIO && holder instanceof ChatFragment.AudioHolder) {
                    // 权限的判断，当然权限已经全局申请了
                    mAudioFileCache.download((ChatFragment.AudioHolder) holder, message.getContent());
                }else{
                    int position = 0;
                    if(message.getType() == Message.TYPE_PIC || message.getType() == Message.TYPE_VIDEO){
                        for (int i = 0; i < preData.size(); i++) {
                            if(message.getContent().equals(preData.get(i).getPath())){
                                position = i;
                                break;
                            }
                        }

                        onPreview(position);
                    }
                }
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, Message message) {
                super.onItemLongClick(holder, message);
                ToastUtils.s(getActivity(),message.getContent());
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "--------------------------------------");
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                Log.i(TAG, "firstCompletelyVisibleItemPosition: "+firstCompletelyVisibleItemPosition);
                if(firstCompletelyVisibleItemPosition==0){

                    if(!isMoreData) {
                        Log.i(TAG, "第一次进入界面滑动到顶部直接返回");
                        return;
                    }
                    Log.i(TAG, "滑动到顶部 加载更多...");

                    SQLite.select()
                            .from(Message.class)
                            .where(OperatorGroup.clause()
                                    .and(Message_Table.sender_id.eq(mReceiverId))
                                    .and(Message_Table.group_id.isNull()))
                            .or(Message_Table.receiver_id.eq(mReceiverId))
                            .orderBy(Message_Table.createAt, false)
                            .limit(30)
                            .offset(PAGE*30)
                            .async()
                            .queryListResultCallback(new QueryTransaction.QueryResultListCallback<Message>() {
                                @Override
                                public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
                                    Run.onUiSync(new Action() {
                                        @Override
                                        public void call() {

                                            if(tResult==null||tResult.size()<=0){
                                                isMoreData = false;
                                                return;
                                            }

                                            //反转数据之前先把多媒体数据按照正序添加到初始化时已经反转数据的多媒体集合
                                            //以达到整体数据都是倒序的
                                            int height = 0,width = 0;
                                            for (Message message : tResult) {
                                                long duration = 0;
                                                if(message.getType() == Message.TYPE_PIC || message.getType() == Message.TYPE_VIDEO){
                                                    LocalMedia localMedia = new LocalMedia();
                                                    String mimeType = PictureMimeType.getImageMimeType(message.getContent());
                                                    if (PictureMimeType.isHasVideo(mimeType)) {
                                                        int[] size = MediaUtils.getVideoSizeForUrl(mimeType);
                                                        width = size[0];
                                                        height = size[1];
                                                        duration = MediaUtils.extractDuration(getContext(), SdkVersionUtils.checkedAndroid_Q(), message.getContent());
                                                    } else if (PictureMimeType.isHasImage(mimeType)) {
                                                        int[] size = MediaUtils.getImageSizeForUrl(mimeType);
                                                        width = size[0];
                                                        height = size[1];
                                                    }
                                                    localMedia.setMimeType(mimeType);
                                                    localMedia.setHeight(height);
                                                    localMedia.setWidth(width);
                                                    localMedia.setPath(message.getContent());
                                                    localMedia.setDuration(duration);
                                                    preData.add(0,localMedia);
                                                }

                                                Log.i("datainfo","ChatFragment  onAdapterDataChanged（dataList） :数据返回到界面====》"+message.getContent());
                                            }

                                            // 反转返回的集合
                                            Collections.reverse(tResult);
                                            mAdapter.getItems().addAll(0,tResult);
                                            Log.i(TAG, "滑动到顶部 tResult:"+tResult.size());
                                            mAdapter.notifyDataSetChanged();

                                            mRecyclerView.scrollToPosition(tResult.size()+lastCompletelyVisibleItemPosition-1);

                                            if(tResult.size()<30){
                                                isMoreData = false;
                                                return;
                                            }


                                            PAGE++;
                                        }
                                    });
                                }
                            })
                            .execute();

                }
                lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                Log.i(TAG, "lastCompletelyVisibleItemPosition: "+lastCompletelyVisibleItemPosition);
                if(lastCompletelyVisibleItemPosition==layoutManager.getItemCount()-1) {
                    Log.i(TAG, "滑动到底部");
                }

            }

            /*@Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                Log.i(TAG, "RecyclerView  newState: "+newState);

                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mRecyclerView, mAdapter.getItemCount()-1);
                }
            }*/

        });


        //lay_content = root.findViewById(R.id.lay_content);
        //buttonview = root.findViewById(R.id.buttonview);

        //controlKeyboardLayout(lay_content,buttonview);

    }


    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll = true;
    //记录目标项位置
    private int mToPosition;

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


    private void initSensorEvent() {

        audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, this.getClass().getName());

    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        if(mSensorManager!=null)
            mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float range = sensorEvent.values[0];
        if (range == mSensor.getMaximumRange()) {
            //wakeLock.acquire(); // 点亮屏幕
            Log.i(TAG, "正常模式  ");
            Toast.makeText(getActivity(), "正常模式", Toast.LENGTH_LONG).show();
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
        } else {
            //三星,华为听筒外放切换卡顿
            //这个问题只能采用折中的办法:重新播放
            Toast.makeText(getActivity(), "听筒模式", Toast.LENGTH_LONG).show();
            if(isAudioPlaying){
                //wakeLock.release(); // 熄灭屏幕
                Log.i(TAG, "听筒模式 isAudioPlaying " + isAudioPlaying);

                mAudioPlayer.stop2();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                } else {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                }
                audioManager.setSpeakerphoneOn(false);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);//获取当前通话最大音量
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume,AudioManager.USE_DEFAULT_STREAM_TYPE);
                //解决切换听筒模式丢失几秒语音问题
                mAudioPlayer.getMediaPlayer().setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mAudioPlayer.play(curAudioholder,curAudiofilePath);
            }else{
                Log.i(TAG, "正常模式 " );
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
            }


        }
    }

    /**
     * @param root
     *            最外层布局，需要调整的布局
     * @param scrollToView
     *            被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    private void controlKeyboardLayout(final View root, final View scrollToView) {
        // 注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数。
        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        // 获取root在窗体的可视区域
                        root.getWindowVisibleDisplayFrame(rect);
                        // 当前视图最外层的高度减去现在所看到的视图的最底部的y坐标
                        int rootInvisibleHeight = root.getRootView()
                                .getHeight() - rect.bottom;
                        Log.i("tag", "最外层的高度" + root.getRootView().getHeight());
                        // 若rootInvisibleHeight高度大于100，则说明当前视图上移了，说明软键盘弹出了
                        if (rootInvisibleHeight > 100) {
                            //软键盘弹出来的时候
                            int[] location = new int[2];
                            // 计算root滚动高度，使scrollToView在可见区域的底部
                            int srollHeight = (location[1] + scrollToView
                                    .getHeight()) - rect.bottom;
                            root.scrollTo(0, srollHeight);
                        } else {
                            // 软键盘没有弹出来的时候
                            root.scrollTo(0, 0);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 进入界面的时候就进行初始化
        mAudioPlayer = new AudioPlayHelper<>(new AudioPlayHelper.RecordPlayListener<AudioHolder>() {
            @Override
            public void onPlayStart(AudioHolder audioHolder) {
                // 范型作用就在于此
                audioHolder.onPlayStart();
            }

            @Override
            public void onPlayStop(AudioHolder audioHolder) {
                // 直接停止
                audioHolder.onPlayStop();
            }

            @Override
            public void onPlayError(AudioHolder audioHolder) {
                // 提示失败
                Application.showToast(R.string.toast_audio_play_error);
            }
        });

        // 下载工具类
        mAudioFileCache = new FileCache<>("audio/cache", "mp3", new FileCache.CacheListener<AudioHolder>() {
            @Override
            public void onDownloadSucceed(final AudioHolder holder, final File file) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        curAudiofilePath = file.getAbsolutePath();
                        curAudioholder = holder;
                        // 主线程播放
                        mAudioPlayer.trigger(holder, file.getAbsolutePath());
                    }
                });
            }

            @Override
            public void onDownloadFailed(AudioHolder holder) {
                Application.showToast(R.string.toast_download_error);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAudioPlayer!=null)
            mAudioPlayer.destroy();
    }

    private void onBottomPanelOpened() {

        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
        Log.i("datainfo","ChatFragment onBottomPanelOpened  scrollToPosition");

    }

    @Override
    public boolean onBackPressed() {
        if (mPanelBoss.isOpen()) {
            // 关闭面板并且返回true代表自己已经处理了消费了返回
            mPanelBoss.closePanel();
            return true;
        }
        Common.curSessionId = "";
        return super.onBackPressed();
    }

    @Override
    protected void initData() {
        super.initData();
        // 开始进行初始化操作
        mPresenter.start();
    }


    // 初始化输入框监听
    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                // 设置状态，改变对应的Icon
                mSubmit.setActivated(needSendMsg);
            }
        });
    }


    @OnClick(R2.id.btn_face)
    void onFaceClick() {
        // 仅仅只需请求打开即可
        mPanelBoss.openPanel();
        mPanelFragment.showFace();
    }

    @OnClick(R2.id.btn_record)
    void onRecordClick() {

        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.RECORD_AUDIO,
                new CheckRequestPermissionListener() {
                    @Override
                    public void onPermissionOk(Permission permission) {

                        if (say == null || write == null)
                            return;
                        if (say.isShown()) {
                            showTextBtn();
                        } else {
                            showSayBtn();
                        }

                    }

                    @Override
                    public void onPermissionDenied(Permission permission) {
                        // see CheckPermissionWithRationaleAdapter
                        if (permission.shouldRationale()) {

                        } else {

                        }
                        show_tips(R.string.string_help_audio);
                    }
                });


    }

    private void showTextBtn() {
        write.setVisibility(View.VISIBLE);
        say.setVisibility(View.GONE);
        record.setImageDrawable(getResources().getDrawable(
                R.drawable.chatting_setmode_voice_btn));
        if (mPanelBoss.isOpen()) {
            // 关闭面板并且返回true代表自己已经处理了消费了返回
            mPanelBoss.closePanel();
        }
        Util.showKeyboard(mContent);
    }

    private void showSayBtn() {
        write.setVisibility(View.GONE);
        say.setVisibility(View.VISIBLE);
        record.setImageDrawable(getResources().getDrawable(
                R.drawable.chatting_setmode_keyboard_btn));
        if (mPanelBoss.isOpen()) {
            // 关闭面板并且返回true代表自己已经处理了消费了返回
            mPanelBoss.closePanel();
        }
        Util.hideKeyboard(mContent);
    }

    private void show_tips(int errorMsg){

        final PictureCustomDialog dialog =
                new PictureCustomDialog(getContext(), com.luck.picture.lib.R.layout.picture_wind_base_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button btn_cancel = dialog.findViewById(com.luck.picture.lib.R.id.btn_cancel);
        Button btn_commit = dialog.findViewById(com.luck.picture.lib.R.id.btn_commit);
        btn_commit.setText(getString(com.luck.picture.lib.R.string.picture_go_setting));
        TextView tvTitle = dialog.findViewById(com.luck.picture.lib.R.id.tvTitle);
        TextView tv_content = dialog.findViewById(com.luck.picture.lib.R.id.tv_content);
        tvTitle.setText(getString(com.luck.picture.lib.R.string.picture_prompt));
        tv_content.setText(errorMsg);
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

            }
        });
    }


    @OnClick(R2.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            // 发送  //顾家家居[ft001][ft010][ft106]几个    [fb009][fb011]扣扣
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        mPanelBoss.openPanel();
        //mPanelFragment.showGallery();
        mPanelFragment.showMore();
    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 界面没有占位布局，Recycler是一直显示的，所有不需要做任何事情

        Log.i("datainfo","ChatFragment  onAdapterDataChanged :数据返回到界面");
    }

    @Override
    public void onAdapterDataChanged(List<Message> dataList) {
        // 界面没有占位布局，Recycler是一直显示的，所有不需要做任何事情

        Log.i("datainfo","ChatFragment  onAdapterDataChanged（dataList） :数据返回到界面");


        if(dataList.size()>0){

            if(dataList.size()>10){
                //当数据大于一屏时设置linenerlayout数据重底部开始展示，
                // 解决recycleview滑动到滋补不准确问题
                layout.setStackFromEnd(true);
            }


            Log.i("datainfo","ChatFragment onAdapterDataChanged  scrollToPosition");

            mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);

            if(StringUtil.isNotEmpty(dataList.get(dataList.size()-1).getContent())){
                if(dataList.get(dataList.size()-1).getContent().equals("/bot")&&Common.isNewPusMsg){
                    Log.i("datainfo","ChatFragment  ======/bot========");
                    mPanelBoss.openPanel();
                    mPanelFragment.showBotPanel();
                }
            }
            //是否为最新推送的消息
            Common.isNewPusMsg  = false;

            if(isFirstLoad)
                return;

            int height = 0,width = 0;
            for (Message message : dataList) {
                long duration = 0;
                if(message.getType() == Message.TYPE_PIC || message.getType() == Message.TYPE_VIDEO){
                    LocalMedia localMedia = new LocalMedia();
                    String mimeType = PictureMimeType.getImageMimeType(message.getContent());
                    if (PictureMimeType.isHasVideo(mimeType)) {
                        int[] size = MediaUtils.getVideoSizeForUrl(mimeType);
                         width = size[0];
                         height = size[1];
                         duration = MediaUtils.extractDuration(getContext(), SdkVersionUtils.checkedAndroid_Q(), message.getContent());
                    } else if (PictureMimeType.isHasImage(mimeType)) {
                        int[] size = MediaUtils.getImageSizeForUrl(mimeType);
                         width = size[0];
                         height = size[1];
                    }
                    localMedia.setMimeType(mimeType);
                    localMedia.setHeight(height);
                    localMedia.setWidth(width);
                    localMedia.setPath(message.getContent());
                    localMedia.setDuration(duration);
                    preData.add(localMedia);
                }

                Log.i("datainfo","ChatFragment  onAdapterDataChanged（dataList） :数据返回到界面====》"+message.getContent());
            }
            isFirstLoad = true;

        }
    }

    @Override
    public EditText getInputEditText() {
        // 返回输入框
        return mContent;
    }

    @Override
    public void onSendGallery(String[] paths) {
        // 图片回调回来
        mPresenter.pushImages(paths);
    }


    @Override
    public void onGotoGallery() {
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                new CheckRequestPermissionListener() {
                    @Override
                    public void onPermissionOk(Permission permission) {
                        startPicActivity();
                    }

                    @Override
                    public void onPermissionDenied(Permission permission) {
                        // see CheckPermissionWithRationaleAdapter
                        if (permission.shouldRationale()) {

                        } else {

                        }
                        show_tips(R.string.string_help_gallery);
                    }
                });
    }

    @Override
    public void onGotoCamera() {
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.CAMERA,
                new CheckRequestPermissionListener() {
                    @Override
                    public void onPermissionOk(Permission permission) {
                        startCameraActivity();
                    }

                    @Override
                    public void onPermissionDenied(Permission permission) {
                        // see CheckPermissionWithRationaleAdapter
                        if (permission.shouldRationale()) {

                        } else {

                        }
                        show_tips(R.string.string_help_camera);
                    }
                });

    }

    private void startPicActivity() {

        PictureSelector.create(ChatFragment.this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isUseCustomCamera(true)// 是否使用自定义相机
                .setLanguage(0)// 设置语言，默认中文
                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
                .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
                .isWithVideoImage(true)// 图片和视频是否可以同选
                .maxSelectNum(9)// 最大图片选择数量
                //.minSelectNum(1)// 最小选择数量
//                .minVideoSelectNum(1)// 视频最小选择数量，如果没有单独设置的需求则可以不设置，同用minSelectNum字段
                .maxVideoSelectNum(9) // 视频最大选择数量，如果没有单独设置的需求则可以不设置，同用maxSelectNum字段
                .imageSpanCount(4)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(true)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .selectionMode(PictureConfig.MULTIPLE )// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(true)// 是否可预览视频
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isEnableCrop(false)// 是否裁剪
                .isCompress(true)// 是否压缩
                .compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(true)//同步false或异步true 压缩 默认同步
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .isPreviewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(mPicResultCallback);

    }

    private void initStartPicActivity() {

        mPicResultCallback = new MyResultCallback();


        PictureSelector.create(ChatFragment.this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isUseCustomCamera(true)// 是否使用自定义相机
                .setLanguage(0)// 设置语言，默认中文
                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
                .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
                .isWithVideoImage(true)// 图片和视频是否可以同选
                .maxSelectNum(9)// 最大图片选择数量
                //.minSelectNum(1)// 最小选择数量
//                .minVideoSelectNum(1)// 视频最小选择数量，如果没有单独设置的需求则可以不设置，同用minSelectNum字段
                .maxVideoSelectNum(9) // 视频最大选择数量，如果没有单独设置的需求则可以不设置，同用maxSelectNum字段
                .imageSpanCount(4)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(true)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .selectionMode(PictureConfig.MULTIPLE )// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(true)// 是否可预览视频
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isEnableCrop(false)// 是否裁剪
                .isCompress(true)// 是否压缩
                .compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(true)//同步false或异步true 压缩 默认同步
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .isPreviewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .minimumCompressSize(100);// 小于100kb的图片不压缩

    }


    private void startCameraActivity() {

        PictureSelector.create(ChatFragment.this)
                .openCamera(PictureMimeType.ofAll())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isUseCustomCamera(true)// 是否使用自定义相机
                .setLanguage(0)// 设置语言，默认中文
                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
                .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
                .isWithVideoImage(true)// 图片和视频是否可以同选
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
//                .minVideoSelectNum(1)// 视频最小选择数量，如果没有单独设置的需求则可以不设置，同用minSelectNum字段
                .maxVideoSelectNum(9) // 视频最大选择数量，如果没有单独设置的需求则可以不设置，同用maxSelectNum字段
                .imageSpanCount(4)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(true)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .selectionMode(PictureConfig.MULTIPLE )// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(true)// 是否可预览视频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isEnableCrop(false)// 是否裁剪
                .isCompress(true)// 是否压缩
                .compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(true)//同步false或异步true 压缩 默认同步
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .isPreviewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(mPicResultCallback);

    }


    private void getWeChatStyle() {

        PictureSelectionConfig.imageEngine = GlideEngine.createGlideEngine();


        // 相册主题
        mPictureParameterStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        mPictureParameterStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        mPictureParameterStyle.isOpenCompletedNumStyle = false;
        // 是否开启类似QQ相册带数字选择风格
        mPictureParameterStyle.isOpenCheckNumStyle = true;
        // 状态栏背景色
        mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#801572fc");
        // 相册列表标题栏背景色
        mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#801572fc");
        // 相册父容器背景色
        mPictureParameterStyle.pictureContainerBackgroundColor = ContextCompat.getColor(getContext(), R.color.app_color_black);
        // 相册列表标题栏右侧上拉箭头
        mPictureParameterStyle.pictureTitleUpResId = R.drawable.picture_icon_wechat_up;
        // 相册列表标题栏右侧下拉箭头
        mPictureParameterStyle.pictureTitleDownResId = R.drawable.picture_icon_wechat_down;
        // 相册文件夹列表选中圆点
        mPictureParameterStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        mPictureParameterStyle.pictureLeftBackIcon = R.drawable.picture_icon_back;
        // 标题栏字体颜色
        mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(getContext(), R.color.picture_color_white);
        // 相册右侧按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        mPictureParameterStyle.pictureCancelTextColor = ContextCompat.getColor(getContext(), R.color.picture_color_53575e);
        // 相册右侧按钮字体默认颜色
        mPictureParameterStyle.pictureRightDefaultTextColor = ContextCompat.getColor(getContext(), R.color.picture_color_53575e);
        // 相册右侧按可点击字体颜色,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureRightSelectedTextColor = ContextCompat.getColor(getContext(), R.color.picture_color_white);
        // 相册右侧按钮背景样式,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureUnCompleteBackgroundStyle = R.drawable.picture_send_button_default_bg;
        // 相册右侧按钮可点击背景样式,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureUnCompleteBackgroundStyle = R.drawable.picture_send_button_bg;
        // 相册列表勾选图片样式
        mPictureParameterStyle.pictureCheckedStyle = R.drawable.picture_wechat_num_selector;
        // 相册标题背景样式 ,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureWeChatTitleBackgroundStyle = R.drawable.picture_album_bg;
        // 微信样式 预览右下角样式 ,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureWeChatChooseStyle = R.drawable.picture_wechat_select_cb;
        // 相册返回箭头 ,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureWeChatLeftBackStyle = R.drawable.picture_icon_back;
        // 相册列表底部背景色
        mPictureParameterStyle.pictureBottomBgColor = ContextCompat.getColor(getContext(), R.color.picture_color_grey);
        // 已选数量圆点背景样式
        mPictureParameterStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        mPictureParameterStyle.picturePreviewTextColor = ContextCompat.getColor(getContext(), R.color.picture_color_white);
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        mPictureParameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(getContext(), R.color.picture_color_9b);
        // 相册列表已完成色值(已完成 可点击色值)
        mPictureParameterStyle.pictureCompleteTextColor = ContextCompat.getColor(getContext(), R.color.picture_color_fa632d);
        // 相册列表未完成色值(请选择 不可点击色值)
        mPictureParameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(getContext(), R.color.picture_color_9b);
        // 预览界面底部背景色
        mPictureParameterStyle.picturePreviewBottomBgColor = ContextCompat.getColor(getContext(), R.color.picture_color_half_grey);
        // 外部预览界面删除按钮样式
        mPictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalFontColor = ContextCompat.getColor(getContext(), R.color.app_color_white);
        // 外部预览界面是否显示删除按钮
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true;
        // 设置NavBar Color SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
        mPictureParameterStyle.pictureNavBarColor = Color.parseColor("#393a3e");

        // 裁剪主题
        mCropParameterStyle = new PictureCropParameterStyle(
                ContextCompat.getColor(getContext(), R.color.app_color_grey),
                ContextCompat.getColor(getContext(), R.color.app_color_grey),
                Color.parseColor("#393a3e"),
                ContextCompat.getColor(getContext(), R.color.app_color_white),
                mPictureParameterStyle.isChangeStatusBarFontColor);


        mWindowAnimationStyle = new PictureWindowAnimationStyle();
        mWindowAnimationStyle.ofAllAnimation(R.anim.picture_anim_up_in, R.anim.picture_anim_down_out);

    }


    /**
            * 返回结果回调
     */
    private  class MyResultCallback implements OnResultCallbackListener<LocalMedia> {

        public MyResultCallback() {
            super();
        }

        @Override
        public void onResult(List<LocalMedia> result) {

            String[] imagePaths = new String[result.size()];
            String[] videoPaths = new String[result.size()];
            int imageIndex = 0;
            int videoIndex = 0;

            for (LocalMedia media : result) {
                Log.i(TAG, "是否压缩:" + media.isCompressed());
                Log.i(TAG, "压缩:" + media.getCompressPath());
                Log.i(TAG, "原图:" + media.getPath());
                Log.i(TAG, "是否裁剪:" + media.isCut());
                Log.i(TAG, "裁剪:" + media.getCutPath());
                Log.i(TAG, "是否开启原图:" + media.isOriginal());
                Log.i(TAG, "原图路径:" + media.getOriginalPath());
                Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
                Log.i(TAG, "宽高: " + media.getWidth() + "x" + media.getHeight());
                Log.i(TAG, "Size: " + media.getSize());


                preData.add(media);

                if(PictureMimeType.isHasImage(media.getMimeType())){
                    mPresenter.pushImages(new String[]{media.getPath()});
                }else if(PictureMimeType.isHasVideo(media.getMimeType())){
                    mPresenter.pushVideos(new String[]{media.getPath()});
                }


                // TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
            }
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "PictureSelector Cancel");
        }
    }


    private List<LocalMedia> preData = new ArrayList<>();
    private void onPreview(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(PictureConfig.EXTRA_POSITION, position);
        bundle.putParcelableArrayList(PictureConfig.EXTRA_PREVIEW_DATA, (ArrayList<? extends Parcelable>) preData);
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), ChatPicturePreviewActivity.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }

        getActivity().overridePendingTransition(com.luck.picture.lib.R.anim.picture_anim_enter,
                com.luck.picture.lib.R.anim.picture_anim_fade_in);
    }


    private void setRecordManager() {

        manager = new ChatRecordManager(say, getActivity());
        manager.setupRecordAudioCallback(new ChatRecordManager.RecordAudioCallback() {
            @Override
            public void onRecordDone(File file, long time) {
                // 语音回调回来
                mPresenter.pushAudio(file.getAbsolutePath(), time);
                Log.i(TAG, "语音回调回来");
            }
        });
    }

    @Override
    public void onRecordDone(File file, long time) {

    }



    // 内容的适配器
    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        protected int getItemViewType(int position, Message message) {
            // 我发送的在右边，收到的在左边
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());

            switch (message.getType()) {
                // 文字内容
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;

                // 语音内容
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;

                // 视频内容
                case Message.TYPE_VIDEO:
                    return isRight ? R.layout.cell_chat_video_right : R.layout.cell_chat_video_left;

                // 图片内容
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;

                // 其他内容：文件
                default:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            // 左右都是同一个
            if (viewType == R.layout.cell_chat_text_right || viewType == R.layout.cell_chat_text_left) {
                return new TextHolder(root);
            } else if (viewType == R.layout.cell_chat_audio_right || viewType == R.layout.cell_chat_audio_left) {
                return new AudioHolder(root);
            } else if (viewType == R.layout.cell_chat_pic_right || viewType == R.layout.cell_chat_pic_left ||
                    viewType == R.layout.cell_chat_video_right || viewType == R.layout.cell_chat_video_left) {
                return new PicHolder(root);

                // 默认情况下，返回的就是Text类型的Holder进行处理
                // 文件的一些实现
            } else {
                return new TextHolder(root);
            }
        }
    }


    // Holder的基类
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R2.id.im_portrait)
        PortraitView mPortrait;

        // 允许为空，左边没有，右边有
        @Nullable
        @BindView(R2.id.loading)
        Loading mLoading;


        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            // 进行数据加载
            sender.load();
            // 头像加载
            mPortrait.setup(Glide.with(ChatFragment.this), sender);

            if (mLoading != null) {
                // 当前布局应该是在右边
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    // 正常状态, 隐藏Loading
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    // 正在发送中的状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    // 发送失败状态, 允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }

                // 当状态是错误状态时才允许点击
                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        @OnClick(R2.id.im_portrait)
        void onRePushClick() {
            // 重新发送

            if (mLoading != null && mPresenter.rePush(mData)) {
                // 必须是右边的才有可能需要重新发送
                // 状态改变需要重新刷新界面当前的信息
                updateData(mData);
            }

        }
    }

    // 文字的Holder
    class TextHolder extends BaseHolder {
        @BindView(R2.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);

            Spannable spannable = new SpannableString(message.getContent());

            // 解析表情
            Face.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));

            // 把内容设置到布局上
            mContent.setText(spannable);
        }
    }

    // 语音的Holder
    class AudioHolder extends BaseHolder {
        @BindView(R2.id.txt_content)
        TextView mContent;
        @BindView(R2.id.im_audio_track)
        ImageView mAudioTrack;

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // long 30000
            String attach = TextUtils.isEmpty(message.getAttach()) ? "0" :
                    message.getAttach();
            mContent.setText(formatTime(attach));
        }

        // 当播放开始
        void onPlayStart() {

            isAudioPlaying = true;
            // 显示
            mAudioTrack.setVisibility(View.VISIBLE);
        }

        // 当播放停止
        void onPlayStop() {

            isAudioPlaying = false;
            // 占位并隐藏
            mAudioTrack.setVisibility(View.INVISIBLE);
        }

        private String formatTime(String attach) {
            float time;
            try {
                // 毫秒转换为秒
                time = Float.parseFloat(attach) / 1000f;
            } catch (Exception e) {
                time = 0;
            }
            // 12000/1000f = 12.0000000
            // 取整一位小数点 1.234 -> 1.2 1.02 -> 1.0
            String shortTime = String.valueOf(Math.round(time * 10f) / 10f);
            // 1.0 -> 1     1.2000 -> 1.2
            shortTime = shortTime.replaceAll("[.]0+?$|0+?$", "");
            return String.format("%s″", shortTime);
        }
    }

    // 图片的Holder
    class PicHolder extends BaseHolder {
        @BindView(R2.id.im_image)
        ImageView mContent;


        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // 当是图片类型的时候，Content中就是具体的地址
            String content = message.getContent();

            RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(10));//图片圆角为30

            Glide.with(getActivity())
                    .asBitmap()
                    .load(content)
                    .apply(options)
                    .placeholder(R.color.white)
                    .thumbnail(0.5f)//缩略图
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new GlideRatioScaleTransForm(mContent));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
