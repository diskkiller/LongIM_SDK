package com.example.wood.samplevideo;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * 视频拍摄页面
 * Created by Wood on 2016/4/6.
 */
public class RecordVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = "RecordVideoActivity";
    private static final int REQ_CODE = 110;
    private static final int RES_CODE = 111;
    /**
     * 录制进度
     */
    private static final int RECORD_PROGRESS = 100;
    /**
     * 录制结束
     */
    private static final int RECORD_FINISH = 101;

    private MovieRecorderView movieRecorderView;
    private Button buttonShoot;
    private RelativeLayout rlBottomRoot;
    private RecoderProgress progressVideo;
    private TextView textViewCountDown;
    private TextView textViewUpToCancel;//上移取消
    private TextView textViewReleaseToCancel;//释放取消
    /**
     * 是否结束录制
     */
    private boolean isFinish = true;
    /**
     * 是否触摸在松开取消的状态
     */
    private boolean isTouchOnUpToCancel = false;
    /**
     * 当前进度
     */
    private int currentTime = 0;


    /**
     * 录制最长时间
     */
    public  static int RECORD_TIME_MAX = 60 * 1000;
    /**
     * 录制最小时间
     */
    public  static int RECORD_TIME_MIN = 3 * 1000;

    /**
     * progress 小于录制最少时间的颜色
     */
    private static int LOW_MIN_TIME_PROGRESS_COLOR = 0xFFFC2828;

    /**
     * progress 颜色
     */
    private static int PROGRESS_COLOR = 0xFF00FF00;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RECORD_PROGRESS) {
                progressVideo.startAnimation();
                if (currentTime < 10) {
                    textViewCountDown.setText("00:0" + currentTime);
                } else {
                    textViewCountDown.setText("00:" + currentTime);
                }

            } else if (msg.what == RECORD_FINISH) {
                if (isTouchOnUpToCancel) {//录制结束，还在上移删除状态没有松手，就复位录制
                    resetData();
                } else {//录制结束，在正常位置，录制完成跳转页面
                    progressVideo.stopAnimation();
                    isFinish = true;
                    buttonShoot.setEnabled(false);
                    finishActivity();
                }

            } else if (msg.what == R.string.state_compress) {
                if (compressing) {
                    compressTime++;
                    L.debug("upyun", "压缩用时：" + XgoTimeUtils.stringForTime(compressTime));
                    sendEmptyMessageDelayed(R.string.state_compress, 1000);
                } else {
                    compressTime = 0;
                }

            } else if (msg.what == R.string.state_compress01) {
                L.debug("upyun", "----" + "正在压缩");

            } else if (msg.what == R.string.state_compress02) {
                L.debug("upyun", "----" + "正在压缩");

            } else if (msg.what == R.string.state_compress03) {
                L.debug("upyun", "----" + "正在压缩");

            } else if (msg.what == R.string.state_compress04) {
                L.debug("upyun", "----" + "正在压缩");

            } else if (msg.what == R.string.state_compress_error) {
                L.debug("upyun", "----" + "压缩失败");

            } else if (msg.what == R.string.state_compress_end) {
                L.debug("upyun", "----" + "压缩完成");
                compressed = true;
                try {
                    long size2 = FileSizeUtil.getFileSize(file2);
                    double size4 = FileSizeUtil.FormetFileSize(size2, 3);
                    L.debug("upyun", "压缩后文件大小：" + size4 + "MB");

                    File file = new File(file2.getAbsolutePath());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };
    /**
     * 按下的位置
     */
    private float startY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        ((TextView) findViewById(R.id.title)).setText("录制视频");
        findViewById(R.id.title_left).setOnClickListener(this);

        movieRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        buttonShoot = (Button) findViewById(R.id.button_shoot);
        rlBottomRoot = (RelativeLayout) findViewById(R.id.rl_bottom_root);
        //progressVideo = (DonutProgress) findViewById(R.id.progress_video);
        progressVideo = (RecoderProgress) findViewById(R.id.progressBar_loading);
        progressVideo.setMaxTime(RECORD_TIME_MAX);
        progressVideo.setMinRecordertime(RECORD_TIME_MIN);
        progressVideo.setLowMinTimeProgressColor(LOW_MIN_TIME_PROGRESS_COLOR);
        progressVideo.setProgressColor(PROGRESS_COLOR);
        textViewCountDown = (TextView) findViewById(R.id.textView_count_down);
        textViewCountDown.setText("00:00");
        textViewUpToCancel = (TextView) findViewById(R.id.textView_up_to_cancel);
        textViewReleaseToCancel = (TextView) findViewById(R.id.textView_release_to_cancel);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) movieRecorderView.getLayoutParams();
        layoutParams.height = width * 4 / 3;//根据屏幕宽度设置预览控件的尺寸，为了解决预览拉伸问题
        //LogUtil.e(LOG_TAG, "mSurfaceViewWidth:" + width + "...mSurfaceViewHeight:" + layoutParams.height);
//        L.debug("upyun", "mSurfaceViewWidth：" + movieRecorderView.getRecordFile().getAbsolutePath());
        movieRecorderView.setLayoutParams(layoutParams);

        FrameLayout.LayoutParams rlBottomRootLayoutParams = (FrameLayout.LayoutParams) rlBottomRoot.getLayoutParams();
        rlBottomRootLayoutParams.height = width / 3 * 2;
        rlBottomRoot.setLayoutParams(rlBottomRootLayoutParams);

        //处理触摸事件
        buttonShoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    textViewUpToCancel.setVisibility(View.VISIBLE);//提示上移取消
                    startRecoderAnim(buttonShoot);
                    isFinish = false;//开始录制
                    startY = event.getY();//记录按下的坐标
                    movieRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {
                        @Override
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(RECORD_FINISH);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    textViewUpToCancel.setVisibility(View.GONE);
                    textViewReleaseToCancel.setVisibility(View.GONE);
                    stopRecoderAnim(buttonShoot);
                    if (startY - event.getY() > 100) {//上移超过一定距离取消录制，删除文件
                        if (!isFinish) {
                            resetData();
                        }
                    } else {
                        if (movieRecorderView.getTimeCount() > 3) {//录制时间超过三秒，录制完成
                            handler.sendEmptyMessage(RECORD_FINISH);
                        } else {//时间不足取消录制，删除文件
                            Toast.makeText(RecordVideoActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                            resetData();
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    //根据触摸上移状态切换提示
                    if (startY - event.getY() > 100) {
                        isTouchOnUpToCancel = true;//触摸在松开就取消的位置
                        if (textViewUpToCancel.getVisibility() == View.VISIBLE) {
                            textViewUpToCancel.setVisibility(View.GONE);
                            textViewReleaseToCancel.setVisibility(View.VISIBLE);
                        }
                    } else {
                        isTouchOnUpToCancel = false;//触摸在正常录制的位置
                        if (textViewUpToCancel.getVisibility() == View.GONE) {
                            textViewUpToCancel.setVisibility(View.VISIBLE);
                            textViewReleaseToCancel.setVisibility(View.GONE);
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    resetData();
                }
                return true;
            }
        });

        movieRecorderView.setOnRecordProgressListener(new MovieRecorderView.OnRecordProgressListener() {
            @Override
            public void onProgressChanged(int maxTime, int currentTime) {
                RecordVideoActivity.this.currentTime = currentTime;
                handler.sendEmptyMessage(RECORD_PROGRESS);
            }
        });
    }

    private void startRecoderAnim(View paramView) {
        AnimatorSet locald = new AnimatorSet();
        locald.playTogether(
                ObjectAnimator.ofFloat(paramView, "scaleX", new float[]{1.0F, 1.2F, 1.5F}),
                ObjectAnimator.ofFloat(paramView, "scaleY", new float[]{1.0F, 1.2F, 1.5F}),
                ObjectAnimator.ofFloat(paramView, "alpha", new float[]{1.0F, 0.25F, 0.0F})
        );

        locald.setDuration(300L).start();
    }

    private void stopRecoderAnim(View paramView) {
        AnimatorSet locald = new AnimatorSet();
        locald.playTogether(
                ObjectAnimator.ofFloat(paramView, "scaleX", new float[]{1.5F, 1.2F, 1.0F}),
                ObjectAnimator.ofFloat(paramView, "scaleY", new float[]{1.5F, 1.2F, 1.0F}),
                ObjectAnimator.ofFloat(paramView, "alpha", new float[]{0.0F, 0.25F, 1.0F})
        );

        locald.setDuration(300L).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkCameraPermission();
    }

    /**
     * 检测摄像头和录音权限
     */
    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            Toast.makeText(this, "视频录制和录音没有授权", Toast.LENGTH_LONG);
            this.finish();
        } else {
            resetData();
        }
    }

    /**
     * 重置状态
     */
    private void resetData() {
        if (movieRecorderView.getRecordFile() != null)
            movieRecorderView.getRecordFile().delete();
        movieRecorderView.stop();
        isFinish = true;
        progressVideo.stopAnimation();
        currentTime = 0;
        textViewCountDown.setText("00:00");
        buttonShoot.setEnabled(true);
        textViewUpToCancel.setVisibility(View.GONE);
        textViewReleaseToCancel.setVisibility(View.GONE);
        try {
            movieRecorderView.initCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = true;
        movieRecorderView.stop();
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                if (!deleteDir(new File(dir, children[i]))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public void onDestroy() {
        //TODO 退出界面删除文件，如果要删除文件夹，需要提供文件夹路径
        if (movieRecorderView.getRecordFile() != null) {
            File file = new File(movieRecorderView.getRecordFile().getAbsolutePath());
            if (file != null && file.exists()) {
                Log.e(LOG_TAG, "file.exists():" + file.exists());
                file.delete();
            }
        }
        super.onDestroy();
    }

    /**
     * TODO 录制完成需要做的事情
     */
    private void finishActivity() {
        if (isFinish) {
            movieRecorderView.stop();
            L.debug("upyun", "文件路径：" + movieRecorderView.getRecordFile().getAbsolutePath());
            file1 =  new File(movieRecorderView.getRecordFile().getAbsolutePath());
            long size2 = 0;
            try {
                size2 = FileSizeUtil.getFileSize(file1);
                double size4 = FileSizeUtil.FormetFileSize(size2, 3);
                L.debug("upyun", "文件大小：" + size4+"MB");
            } catch (Exception e) {
                e.printStackTrace();
            }
//            compressThread(movieRecorderView.getRecordFile().getAbsolutePath(),480,320);
            Intent intent = new Intent(this, VideoPreviewActivity.class);
            intent.putExtra("path", movieRecorderView.getRecordFile().getAbsolutePath());
            startActivityForResult(intent, REQ_CODE);
        }
    }


    /**
     * 视频压缩
     *
     * @param
     * @param videoH
     * @param compressFilePath
     */
/*
    private void compressThread(final String compressFilePath,final int videoH, final int videoW ) {

        new Thread() {

            @Override
            public void run() {
                compressSize();
            }

            */
/**
             * 视频压缩
             *//*

            private void compressSize() {
                // 准备压缩
                handler.sendEmptyMessage(R.string.state_compress);
                compressing = true;

                file1 =  new File(compressFilePath);
                long size2 = 0;
                try {
                    size2 = FileSizeUtil.getFileSize(file1);
                    double size4 = FileSizeUtil.FormetFileSize(size2, 3);
                    L.debug("upyun", "压缩前文件大小：" + size4+"MB");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                file2 = new File(XgoFileUtils.getVideoSmailDir(), file1.getName());
                L.debug("upyun", "----" + XgoFileUtils.getVideoSmailDir());
                try {
                    File fileAppRoot = new File(
                            RecordVideoActivity.this.getApplicationInfo().dataDir);
                    FfmpegController fc = new FfmpegController(
                            RecordVideoActivity.this, fileAppRoot);
                    L.debug("upyun", "--22--" + file2.getCanonicalPath());
                    fc.compress_clipVideo(file1.getCanonicalPath(),
                            file2.getCanonicalPath(), 320, 480,
                            new ShellUtils.ShellCallback() {
                                private int flag = 0;

                                @Override
                                public void shellOut(String shellLine) {
                                    int temp = flag % 4;
                                    if (temp == 0) {
                                        handler.sendEmptyMessage(R.string.state_compress01);
                                    } else if (temp == 1) {
                                        handler.sendEmptyMessage(R.string.state_compress02);
                                    } else if (temp == 2) {
                                        handler.sendEmptyMessage(R.string.state_compress03);
                                    } else if (temp == 3) {
                                        handler.sendEmptyMessage(R.string.state_compress04);
                                    }
                                    flag++;
                                }

                                @Override
                                public void processComplete(int exitValue) {
                                    compressing = false;
                                    if (exitValue != 0) {
                                        handler.sendEmptyMessage(R.string.state_compress_error);
                                    } else {
                                        if (file2.exists()) {
                                            handler.sendEmptyMessage(R.string.state_compress_end);
                                        }
                                    }
                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }.start();
    }
*/


    /**
     * 未压缩视频保存路径
     */
    private File file1;
    /**
     * 压缩后的保存路径
     */
    private File file2;
    /**
     * 压缩使用时间&正在压缩标记
     */
    private int compressTime = 0;
    private boolean compressing = false;

    /**
     * 压缩完成标记
     */
    private boolean compressed = false;
    public class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {


            }
        }
    }




    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.title_left) {
            finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == RES_CODE) {
            setResult(RES_CODE);
            finish();
        }
    }
}
