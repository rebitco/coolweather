package com.coolweather.app.util;

public interface HttpCallbackListener {
	/**
	 * ����ɹ�
	 * @param response
	 */
	void onFinish(String response);
	/**
	 * ����ʧ��
	 */
	void onError(Exception e);
}
