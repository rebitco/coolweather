package com.coolweather.app.activity;

import java.util.Calendar;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	/**
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	 * �û���ʾ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ������ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	/**
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	/**
	 * ������ʾ��ǰ������Ϣ
	 */
	private TextView currentDateText;
	/**
	 * �����л����а�ť
	 */
	private Button switchCity;
	/**
	 * ����������ť
	 */
	private Button refreshWeather;
	/**
	 * ��¼���·��ؼ���ʱ��
	 */
	private long returnTime = 0;
	/**
	 * ����ʱ��ǰ���ַ���
	 */
	private String dateText = "";
	/**
	 * ��ȡ���������� . eg: ���� �� �� ѩ
	 */
	private String weatherDsp = "";
	/**
	 * ��������layout
	 */
	private RelativeLayout weatherView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		/**
		 * ������������ͼ
		 */
		weatherView = (RelativeLayout) findViewById(R.id.weather_view);
		
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh);
		//���intent�����ﴫ������? chooseActivity?�ؼ�����
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			//���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			//��ѯ�ؼ���������Ӧ����������
			queryWeatherCode(countyCode);
		} else {
			//û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		//���õ���¼�
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	
	/**
	 * ������ð�ť,��ת������ҳ��
	 */
	public void setting(View v){
		
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh:
			publishText.setText("ͬ����...");
			SharedPreferences preferences = 
					PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = preferences.getString("citycode", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" +
				countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	
	/**
	 * ��ѯ������������Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://apis.baidu.com/apistore/weatherservice/cityid?cityid="+
				weatherCode;
		queryFromServer(address, "weatherCode");
	}

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 * @param address
	 * @param type
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//�ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if (array!=null && array.length==2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if("weatherCode".equals(type)) {
					//������������ص���Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					//�ص����߳�ȥ����UI
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	
	/**
	 *  ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
	 */
	private void showWeather(){
		SharedPreferences preferences = 
				PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(preferences.getString("city", ""));
		temp1Text.setText(preferences.getString("l_temp", "")+"��C");
		temp2Text.setText(preferences.getString("h_temp", "")+"��C");
		/**
		 * �������������ı䱳��ͼ
		 */
		weatherDsp = preferences.getString("weather", "");
		weatherDespText.setText(weatherDsp);
		if (weatherDsp.contains("����")||weatherDsp.contains("��")) {
			weatherView.setBackgroundResource(R.drawable.sunny);
		} else if(weatherDsp.contains("��")){
			weatherView.setBackgroundResource(R.drawable.rain);
		} else if(weatherDsp.contains("ѩ")){
			weatherView.setBackgroundResource(R.drawable.snow);
		} else {
			weatherView.setBackgroundResource(R.drawable.other);
		}
		
		/**
		 * �����������µ�time,�ж�һ�������ǰ��ʱ��С�ڸ��µ�ʱ��,Ӧ����������µ�
		 */
		Calendar mCalendar = Calendar.getInstance();
		int HH = mCalendar.get(Calendar.HOUR_OF_DAY);
		//���ص��ַ����� 18:00��ʽ ��ȡ���ַ������±�0��ʼ , �����ȡ��λ
		if(HH<Integer.parseInt((preferences.getString("time", "").substring(0, 2)))){
			dateText = "����";
		} else {
			dateText = "����";
		}
		publishText.setText(dateText+preferences.getString("time", "")+"����");
		currentDateText.setText(preferences.getString("current_time", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		//����AutoUpdateService����
		/**
		 * ����ֻҪһ��ѡ����ĳ�����в��ɹ���������֮��AutoUpdateService �ͻ�һֱ�ں�̨
		 * ���У�����֤ÿ 8 Сʱ����һ������
		 */
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}
	
	/**
	 * ���2�� �ٰ�һ�ξ��˳� ��˾��ʾ
	 */
	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis()-returnTime > 2000) {
			Toast.makeText(this, "�ٰ�һ���˳�", 0).show();
			returnTime = System.currentTimeMillis();
			return;
		}
		finish();
	}
}
