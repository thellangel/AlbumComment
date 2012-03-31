package com.unidw.album.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class HttpHandler extends Handler {

	private static final String	TAG				= HttpHandler.class.getSimpleName();

	private Context				context;
	private boolean				showProgress	= false;
	private boolean				cancelAble		= false;
	private ProgressDialog		progressDialog;
	private String				progressTitle;
	private String				progressMessage;

	public HttpHandler(Context context, boolean showProgress, boolean cancelAble, String title, String message) {
		this.context = context;
		this.showProgress = showProgress;
		this.progressTitle = title;
		this.progressMessage = message;
		this.cancelAble = cancelAble;
	}

	public Context getContext() {
		return this.context;
	}

	protected void start() {
		if (showProgress) {
			try {
				progressDialog = ProgressDialog.show(context, progressTitle, progressMessage, true, cancelAble);
			}
			catch (Exception e) {
				LogUtil.e(TAG, "progressDialog show error, is your activity running?", e);
			}
		}
	}

	protected void succeed(String jObject) {
		if (progressDialog != null && progressDialog.isShowing()) {
			try {
				progressDialog.dismiss();
			}
			catch (Exception e) {
				LogUtil.e(TAG, "progressDialog show error, is your activity running?", e);
			}
		}
	}

	protected void failed(String jObject) {
		if (progressDialog != null && progressDialog.isShowing()) {
			try {
				progressDialog.dismiss();
			}
			catch (Exception e) {
				LogUtil.e(TAG, "progressDialog show error, is your activity running?", e);
			}
		}
	}

	protected void otherHandleMessage(Message message) {

	}

	public void handleMessage(Message message) {
		switch (message.what) {
			case HttpUtil.DID_START: // connection start
				LogUtil.d(TAG, "HttpUtil.DID_START");
				start();
				break;
			case HttpUtil.DID_SUCCEED: // connection success
				if (showProgress)
					progressDialog.dismiss();
				String response = (String) message.obj;
				LogUtil.d(TAG, "HttpUtil.DID_SUCCEED, response=" + response);
				succeed(response);
				break;
			case HttpUtil.DID_ERROR: // connection error
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Exception e = (Exception) message.obj;
				e.printStackTrace();
				failed(e.getMessage());
				LogUtil.e(TAG, "HttpUtil.DID_ERROR error=" + e.getMessage(), e);
				break;
		}
		otherHandleMessage(message);
	}

}
