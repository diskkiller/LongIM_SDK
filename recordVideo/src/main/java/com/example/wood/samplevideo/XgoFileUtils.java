package com.example.wood.samplevideo;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

public class XgoFileUtils {

	private static final String ROOT_DIR = "jaaaja_camera_xgo";


	public static String getPicDir() {
		return getDir("pic_imageloader");
	}

	public static String getPicSmailDir(){
		return getDir("pic_xgo"+File.separator+"smail");
	}

	public static String getVideoDir() {
		return getDir("video_xgo");
	}

	public static String getVideoSmailDir(){
		return getDir("video_xgo"+File.separator+"smail");
	}

	/**
	 * 根据手机状态自动挑选存储介质（SD or 手机内部）
	 * @param string
	 * @return
	 */
	private static String getDir(String string) {
		if (isSDAvailable()) {
			return getSDDir(string);
		} else {
			return getDataDir(string);
		}
	}

	/**
	 * 判断sd卡是否可以用
	 *
	 * @return
	 */
	private static boolean isSDAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? true : false;
	}

	/**
	 * 获取到手机内存的目录
	 * @param string
	 * @return
	 */
	private static String getDataDir(String string) {
		// data/data/包名/cache
		String path = RecordActivity.getContext().getCacheDir().getAbsolutePath()+File.separator+string;
		File file = new File(path);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return file.getAbsolutePath();
			} else {
				return "";
			}
		}
		return file.getAbsolutePath();
	}

	/**
	 * 获取到sd卡的目录
	 *
	 * @param key_dir
	 * @return
	 */
	private static String getSDDir(String key_dir) {
		StringBuilder sb = new StringBuilder();
		String absolutePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();// /mnt/sdcard
		sb.append(absolutePath);
		sb.append(File.separator).append(ROOT_DIR).append(File.separator).append(key_dir);

		String filePath = sb.toString();
		File file = new File(filePath);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return file.getAbsolutePath();
			} else {
				return "";
			}
		}

		return file.getAbsolutePath();

	}


	public static File getOwnCacheDirectory(Context context, String cacheDir) {
		File appCacheDir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			appCacheDir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath(), cacheDir);// /mnt/sdcard
		}
		if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}
}
