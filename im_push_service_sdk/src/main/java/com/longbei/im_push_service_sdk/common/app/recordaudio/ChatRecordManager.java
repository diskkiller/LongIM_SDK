package com.longbei.im_push_service_sdk.common.app.recordaudio;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.example.wood.samplevideo.L;
import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.common.app.Application;
import com.longbei.im_push_service_sdk.common.app.kit.handler.Run;
import com.longbei.im_push_service_sdk.common.app.kit.handler.runable.Action;
import com.longbei.im_push_service_sdk.common.app.tools.AudioRecordHelper;

import java.io.File;


/**
 * @author Administrator
 * 
 */
public class ChatRecordManager implements OnTouchListener {

	private View recordBtn;
	private Activity context;
	private ImageView amplitudeIv;
	private ImageView im_record_mic;
	private ImageView iv_im_cancel_record;
	private TextView progressTitle;
	private TextView tv_im_record;
	private PopupWindow recordPopupWindow;
	protected boolean isRecording;
	private Handler micRefreshHandler;
	private int mCurrentMotionY = 0;
	private int mOldMotionY = 0;
	private int mCurrentMotionX = 0;
	private int mOldMotionX = 0;
	public AudioRecordHelper helper;
	private int mVolume;
	private RecordAudioCallback mCallback;
	private boolean isCancle = true;

	public ChatRecordManager(View startBtn, Activity activity) {
		this.recordBtn = startBtn;
		this.context = activity;
		init();
	}

	private void init() {
		Display dp = context.getWindowManager().getDefaultDisplay();
		recordBtn.setOnTouchListener(this);
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.im_chat_record, null);
		amplitudeIv = (ImageView) contentView
				.findViewById(R.id.im_chat_record_img);
		im_record_mic = (ImageView) contentView
				.findViewById(R.id.im_record_mic);
		progressTitle = (TextView) contentView
				.findViewById(R.id.record_seconds);
		tv_im_record = (TextView) contentView.findViewById(R.id.tv_im_record);
		iv_im_cancel_record = (ImageView) contentView
				.findViewById(R.id.iv_im_cancel_record);
		recordPopupWindow = new PopupWindow(contentView, dp.getWidth() / 2,
				dp.getWidth() / 2);
		micRefreshHandler = new Handler();


		// 录音的缓存文件
		File tmpFile = Application.getAudioTmpFile(true);
		// 录音辅助工具类
		helper = new AudioRecordHelper(tmpFile, new AudioRecordHelper.RecordCallback() {
			@Override
			public void onRecordStart() {
				//...
			}

			@Override
			public void onProgress(long time, int Volume) {
				//录音取消
				Log.i("record","isCancle "+isCancle+"  isRecording  "+isRecording);

				if(isCancle && !isRecording){
					Run.onUiSync(new Action() {
						@Override
						public void call() {
							if (recordPopupWindow.isShowing()) {
								recordPopupWindow.dismiss();
								Log.i("record","录音窗口消失");

							}
						}
					});
					return;
				}


				mVolume = Volume;
				Run.onUiSync(new Action() {
					@Override
					public void call() {
						startRefreshMic();
						if (!recordPopupWindow.isShowing()) {
							recordPopupWindow.showAtLocation(
									context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
						}
						im_record_mic.setImageResource(R.drawable.im_record_mic);
					}
				});


			}

			@Override
			public void onRecordDone(File file, long time) {
				// 时间是毫秒，小于1秒则不发送

				Run.onUiSync(new Action() {
					@Override
					public void call() {
						if (recordPopupWindow.isShowing()) {
							recordPopupWindow.dismiss();
							Log.i("record","录音窗口消失");

						}
					}
				});


				if (time < 1000) {
					return;
				}

				// 更改为一个发送的录音文件
				File audioFile = Application.getAudioTmpFile(false);
				if (file.renameTo(audioFile)) {
					// 通知到聊天界面
					if (mCallback != null) {
						mCallback.onRecordDone(audioFile, time);
						Log.i("record","录音完成   通知到聊天界面");
					}
				}
			}
		});

	}


	public void onRecording() {

		isRecording = true;
		// 请求开始
		helper.recordAsync();
	}


	private void startRefreshMic() {
		micRefreshHandler.postDelayed(micLoopAni, 500);
	}

	private Runnable micLoopAni = new Runnable() {

		@Override
		public void run() {
			int mVolume = getVolume();
			L.debug("volume", mVolume + "");
			switch (mVolume) {
			case 1:
				amplitudeIv.setImageResource(R.drawable.recording_1);
				break;
			case 2:
				amplitudeIv.setImageResource(R.drawable.recording_2);
				break;
			case 3:
				amplitudeIv.setImageResource(R.drawable.recording_3);
				break;
			case 4:
				amplitudeIv.setImageResource(R.drawable.recording_4);
				break;
			case 5:
				amplitudeIv.setImageResource(R.drawable.recording_5);
				break;
			case 6:
				amplitudeIv.setImageResource(R.drawable.recording_6);
				break;
			case 7:
				amplitudeIv.setImageResource(R.drawable.recording_7);
				break;
			default:
				break;
			}
			if (isRecording) {
				micRefreshHandler.postDelayed(this, 100);
			}
		}
	};


	// 获取音量值，只是针对录音音量
	public int getVolume() {
		L.debug("volume===", mVolume + "");
		int volume = 0;
		// 录音
		if (isRecording) {
			volume = mVolume;
			Log.d("db", volume + "");
			if (volume != 0)
				volume = (int) (10 * Math.log10(volume)) / 8;
			L.debug("volume", volume + "");
		}
		return volume;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mOldMotionY = (int) event.getY();
			mOldMotionX = (int) event.getX();
			iv_im_cancel_record.setVisibility(View.GONE);
			tv_im_record.setText("手指上滑取消发送");
			progressTitle.setVisibility(View.GONE);
			amplitudeIv.setVisibility(View.VISIBLE);
			im_record_mic.setVisibility(View.VISIBLE);
			isRecording = true;
			isCancle = false;
			onRecording();
			break;
		case MotionEvent.ACTION_UP:
			isRecording = false;
			L.debug("volume  recordPopupWindow", recordPopupWindow.isShowing() + "");
			helper.stop(isCancle);
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrentMotionY = (int) event.getY();
			mCurrentMotionX = (int) event.getX();
			if (mCurrentMotionY < mOldMotionY
					&& Math.abs(mCurrentMotionX - mOldMotionX) < Math
							.abs(mCurrentMotionY - mOldMotionY)) {
				if (Math.abs(mOldMotionY) > 350) {
					iv_im_cancel_record.setVisibility(View.VISIBLE);
					progressTitle.setVisibility(View.GONE);
					tv_im_record.setText("松开手指，取消发送");
					amplitudeIv.setVisibility(View.INVISIBLE);
					im_record_mic.setVisibility(View.INVISIBLE);
					isCancle = true;
				}
			} else if (mCurrentMotionY > mOldMotionY
					&& Math.abs(mCurrentMotionX - mOldMotionX) < Math
							.abs(mCurrentMotionY - mOldMotionY)) {
				if (Math.abs(mOldMotionY) < 350) {
					iv_im_cancel_record.setVisibility(View.GONE);
					amplitudeIv.setVisibility(View.VISIBLE);
					im_record_mic.setVisibility(View.VISIBLE);
					progressTitle.setVisibility(View.GONE);
					tv_im_record.setText("手指上滑取消发送");
					isCancle = false;
				}
			}
			mOldMotionY = (int) event.getY();
			mOldMotionX = (int) event.getX();
			break;
		default:
			break;
		}
		return false;
	}

	public void setupRecordAudioCallback(RecordAudioCallback callback) {
		mCallback = callback;
	}

	// 回调聊天界面的Callback
	public interface RecordAudioCallback {
		// 返回录音文件和时常
		void onRecordDone(File file, long time);
	}
}
