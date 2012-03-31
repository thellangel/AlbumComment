package com.unidw.album.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;

public class HttpUtil implements Runnable {

	// Tag
	public static final String	TAG			= HttpUtil.class.getSimpleName();
	private static HttpClient	client		= null;

	public static final int		DID_START	= 0;
	public static final int		DID_ERROR	= 1;
	public static final int		DID_SUCCEED	= 2;

	private String				url;
	private List<NameValuePair>	data;

	/** 异步请求Handler **/
	private Handler				asyncHandler;

	HttpUtil(Handler handler) {
		asyncHandler = handler;
	}

	/**
	 * 同步请求
	 * 
	 * @param url
	 * @param data
	 */
	public static String requestSync(String url, List<NameValuePair> data) throws Exception {
		LogUtil.d(TAG, "requestSync url:" + url + " ,data:" + data);
		HttpPost request = new HttpPost(url);
		request.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
		LogUtil.d(TAG, "begin request " + url);
		HttpResponse response = newInstance().execute(request);
		if (HttpUtil.isHttp200(response)) {
			LogUtil.d(TAG, "end request " + url);
			return EntityUtils.toString(response.getEntity());
		}
		else if (HttpUtil.isHttp302(response)) {
			LogUtil.e(TAG, "request " + url + " 302");
			throw new Exception();
		}
		else if (HttpUtil.isHttp400(response)) {
			LogUtil.e(TAG, "request " + url + " 400");
			throw new Exception();
		}
		else if (HttpUtil.isHttp500(response)) {
			LogUtil.e(TAG, "request " + url + " 500");
			throw new Exception();
		}
		else {
			LogUtil.e(TAG, "request " + url + " status Exception");
			throw new Exception();
		}
	}

	/**
	 * 异步请求
	 * 
	 * @param url
	 * @param data
	 */
	public void requestAsync(String url, List<NameValuePair> data) {
		LogUtil.d(TAG, "requestAsync url:" + url + " ,data:" + data);
		this.url = url;
		this.data = data;
		ConnectionManager.getInstance().push(this);
	}

	/**
	 * 创建http client实例
	 */
	public static HttpClient newInstance() {
		if (client == null) {
			// Create and initialize HTTP parameters
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);// 建立http连接，30秒超时
			HttpConnectionParams.setSoTimeout(params, 2 * 60 * 1000);// 建立http连接后，数据读取间隔，2分钟超时，下载时间超过2分钟停止
			ConnManagerParams.setMaxTotalConnections(params, 100);// 连接管理器最大100个连接
			ConnManagerParams.setTimeout(params, 30 * 1000);// 在向连接管理器要空闲连接时，线程阻塞5分钟超时
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

			// // Increase max connections for url:80 to 50
			// ConnPerRouteBean connPerRoute = new ConnPerRouteBean(5);//每个路由默认分配的最大连接数是5个
			// // TODO change
			// connPerRoute.setMaxForRoute(new HttpRoute(new HttpHost("mobile.quanshi.com", 80)), 5);//到bbs的路由的连接20最大20个
			// ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

			// Create and initialize scheme registry
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			// schemeRegistry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 80));
			// schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

			// Create an HttpClient with the ThreadSafeClientConnManager.
			// This connection manager must be used if more than one thread will
			// be using the HttpClient.
			// HttpHost proxy = new HttpHost("localhost", 8888);
			// params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
			client = new HttpUtil.DefaultHttpClientEx(cm, params);
			client = new DefaultHttpClient(params);
		}
		return client;
	}

	static class DefaultHttpClientEx extends DefaultHttpClient {

		CookieStore	cookieStore;

		public DefaultHttpClientEx(ClientConnectionManager cm, HttpParams params) {
			super(cm, params);
		}

		@Override
		protected CookieStore createCookieStore() {
			cookieStore = new BasicCookieStore();
			return cookieStore;
		}
	}

	/**
	 * 判断http协议返回的内容是否是xml格式
	 */
	public static boolean isXmlContentType(HttpResponse response) {
		return response.getHeaders("Content-Type") != null && response.getHeaders("Content-Type").length == 1 && response.getHeaders("Content-Type")[0].getValue().indexOf("text/xml") != -1;
	}

	/**
	 * 判断http协议返回的内容是否是html格式
	 */
	public static boolean isHtmlContentType(HttpResponse response) {
		return response.getHeaders("Content-Type") != null && response.getHeaders("Content-Type").length == 1 && response.getHeaders("Content-Type")[0].getValue().indexOf("text/html") != -1;
	}

	/**
	 * 判断http协议返回的状态码是否是200，正常结果
	 */
	public static boolean isHttp200(HttpResponse response) {
		return response.getStatusLine().getStatusCode() == 200 && "OK".equals(response.getStatusLine().getReasonPhrase());
	}

	/**
	 * 判断http协议返回的状态码是否是302，需要跳转
	 */
	public static boolean isHttp302(HttpResponse response) {
		return response.getStatusLine().getStatusCode() == 302 && "Found".equals(response.getStatusLine().getReasonPhrase());
	}

	/**
	 * 判断http协议返回的状态码是否是400，失败
	 */
	public static boolean isHttp400(HttpResponse response) {
		return response.getStatusLine().getStatusCode() == 400 && "Bad Request".equals(response.getStatusLine().getReasonPhrase());
	}

	/**
	 * 判断http协议返回的状态码是否是500，失败
	 */
	public static boolean isHttp500(HttpResponse response) {
		return response.getStatusLine().getStatusCode() == 500 && "Internal Server Error".equals(response.getStatusLine().getReasonPhrase());
	}

	@Override
	public void run() {
		if (asyncHandler != null)
			asyncHandler.sendMessage(Message.obtain(asyncHandler, HttpUtil.DID_START));
		client = newInstance();
		try {
			HttpResponse response = null;
			HttpPost httpPost = new HttpPost(url);
			LogUtil.d(TAG, "request url=" + url);
			httpPost.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
			response = client.execute(httpPost);
			if (HttpUtil.isHttp200(response)) {
				LogUtil.d(TAG, "end request " + url);
				processEntity(response.getEntity());
			}
			else if (HttpUtil.isHttp302(response)) {
				LogUtil.e(TAG, "request " + url + " 302");
				throw new Exception();
			}
			else if (HttpUtil.isHttp400(response)) {
				LogUtil.e(TAG, "request " + url + " 400");
				throw new Exception();
			}
			else if (HttpUtil.isHttp500(response)) {
				LogUtil.e(TAG, "request " + url + " 500");
				throw new Exception();
			}
			else {
				LogUtil.e(TAG, "request " + url + " status Exception");
				throw new Exception();
			}

		}
		catch (Exception e) {
			if (asyncHandler != null)
				asyncHandler.sendMessage(Message.obtain(asyncHandler, HttpUtil.DID_ERROR, e));
		}
		ConnectionManager.getInstance().didComplete(this);
	}

	private void processEntity(HttpEntity entity) throws IllegalStateException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line, result = "";
		while ((line = br.readLine()) != null)
			result += line;

		if (asyncHandler != null) {
			Message message = Message.obtain(asyncHandler, DID_SUCCEED, result);
			asyncHandler.sendMessage(message);
		}
	}
}
