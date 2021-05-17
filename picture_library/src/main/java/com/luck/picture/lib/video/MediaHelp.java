package com.luck.picture.lib.video;

import com.luck.picture.lib.app.PictureAppMaster;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MediaHelp {
	private static IMediaPlayer mPlayer;

	public static IMediaPlayer getInstance() {
		IjkExoMediaPlayer IjkExoMediaPlayer = new IjkExoMediaPlayer(PictureAppMaster.getInstance().getAppContext());
		mPlayer = IjkExoMediaPlayer;
		return mPlayer;
	}

	/**
	 * MediaPlayer resume
	 */
	public static void resume() {
		if (mPlayer != null) {
			mPlayer.start();
		}

	}

	/**
	 * MediaPlayer pause
	 */
	public static void pause() {
		if (mPlayer != null) {
			mPlayer.pause();
		}
	}

	/**
	 * MediaPlayer release
	 */
	public static void release() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

}
