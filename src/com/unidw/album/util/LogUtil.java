package com.unidw.album.util;

import android.util.Log;

public class LogUtil {
	public static final String	prefix	= "Album->>";

	public static void d(String tag, String str) {
		Log.d(tag, prefix + str);
	}

	public static void w(String tag, String str) {
		Log.w(tag, prefix + str);
	}

	public static void w(String tag, String str, Exception e) {
		Log.w(tag, prefix + str, e);
	}

	public static void e(String tag, String str) {
		Log.e(tag, prefix + str);
	}

	public static void e(String tag, String str, Exception e) {
		Log.e(tag, prefix + str + ", " + e.getMessage(), e);
	}

	public static void i(String tag, String str) {
		Log.i(tag, prefix + str);
	}

	public static void u(String tag, String flagStr, String str) {

	}
}
