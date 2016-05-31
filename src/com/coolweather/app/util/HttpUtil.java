package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpConnection;

import android.R.string;

public class HttpUtil {
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
		//�¿����߳�
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(8000);
					connection.setConnectTimeout(8000);
					InputStream in = connection.getInputStream();
					//�ֽ���ת��Ϊ�ַ��� reader����һ��һ�еĶ�ȡ 
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line=reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
