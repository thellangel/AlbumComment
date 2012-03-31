package com.unidw.album.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class WebUtil {

	private static WebUtil	webUtil;
	
	private HttpHandler		handler;
	
	private final static String baseUrl = "http://192.168.13.100/CodeIgniter/";

	public static WebUtil instance(Context _context, HttpHandler handler) {
		if (webUtil == null) {
			webUtil = new WebUtil();
		}
		webUtil.handler = handler;
		return webUtil;
	}
	
	/**
	 * 加载默认相册列表
	 * @param androidId 设备id
	 * @param pageIndex 页号
	 * @throws Exception 异常
	 */
	public void loadDefaultAlbumList(String androidId, int pageIndex) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("androidId", androidId));
		params.add(new BasicNameValuePair("pageIndex", Integer.valueOf(pageIndex).toString()));
		new HttpUtil(handler).requestAsync(baseUrl + "photo/photoLoadAlbumList", params);
	}
	
	/**
	 * 加载设备信息
	 */
	public void loadDeviceInfo(String androidId){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("androidId", androidId));
		new HttpUtil(handler).requestAsync(baseUrl + "photo/photoLoadDevice", params);
	}
}
