package com.coolweather.app.util;

public interface HttpCallbackListener {
	/**
	 * 请求成功
	 * @param response
	 */
	void onFinish(String response);
	/**
	 * 请求失败
	 */
	void onError(Exception e);
}
