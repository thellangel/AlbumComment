package com.unidw.album.util;

import android.content.Context;


public class CommonUtil {
	/**
	 * ȡ��android id��
	 */
	public static String getAndroidId(Context context){
		String androidId=android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
		return androidId;
	}
}
