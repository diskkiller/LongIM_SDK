package com.example.wood.samplevideo;

import java.util.Formatter;
import java.util.Locale;


public class XgoTimeUtils{

	/**
	 * 把毫秒转换成：1:20:30形式
	 *
	 * @param time
	 * @return
	 */
	public static String stringForTime(int time) {
		StringBuilder mFormatBuilder = new StringBuilder();
		String result;
		Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

		//	int totalSeconds = timeMs / 1000;
		int seconds = time % 60;

		int minutes = (time / 60) % 60;

		int hours = time / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			result =  mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		} else {
			result=  mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}

		mFormatter.close();

		return result;
	}
}
