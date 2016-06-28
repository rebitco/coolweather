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
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用户显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期信息
	 */
	private TextView currentDateText;
	/**
	 * 用于切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	/**
	 * 记录按下返回键的时间
	 */
	private long returnTime = 0;
	/**
	 * 更新时间前的字符串
	 */
	private String dateText = "";
	/**
	 * 读取天气的描述 . eg: 多云 晴 雨 雪
	 */
	private String weatherDsp = "";
	/**
	 * 天气背景layout
	 */
	private RelativeLayout weatherView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		/**
		 * 设置天气背景图
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
		//这个intent是哪里传过来的? chooseActivity?县级代号
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			//有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			//查询县级代码所对应的天气代号
			queryWeatherCode(countyCode);
		} else {
			//没有县级代号时就直接显示本地天气
			showWeather();
		}
		//设置点击事件
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	
	/**
	 * 点击设置按钮,跳转到设置页面
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
			publishText.setText("同步中...");
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
	 * 查询县级代码所对应的天气代号
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" +
				countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	
	/**
	 * 查询天气代号所对应的天气
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://apis.baidu.com/apistore/weatherservice/cityid?cityid="+
				weatherCode;
		queryFromServer(address, "weatherCode");
	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 * @param address
	 * @param type
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if (array!=null && array.length==2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if("weatherCode".equals(type)) {
					//处理服务器返回的信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					//回到主线程去更新UI
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
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	
	/**
	 *  从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */
	private void showWeather(){
		SharedPreferences preferences = 
				PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(preferences.getString("city", ""));
		temp1Text.setText(preferences.getString("l_temp", "")+"°C");
		temp2Text.setText(preferences.getString("h_temp", "")+"°C");
		/**
		 * 根据天气描述改变背景图
		 */
		weatherDsp = preferences.getString("weather", "");
		weatherDespText.setText(weatherDsp);
		if (weatherDsp.contains("多云")||weatherDsp.contains("晴")) {
			weatherView.setBackgroundResource(R.drawable.sunny);
		} else if(weatherDsp.contains("雨")){
			weatherView.setBackgroundResource(R.drawable.rain);
		} else if(weatherDsp.contains("雪")){
			weatherView.setBackgroundResource(R.drawable.snow);
		} else {
			weatherView.setBackgroundResource(R.drawable.other);
		}
		
		/**
		 * 根据天气更新的time,判断一下如果当前的时间小于更新的时间,应该是昨天更新的
		 */
		Calendar mCalendar = Calendar.getInstance();
		int HH = mCalendar.get(Calendar.HOUR_OF_DAY);
		//返回的字符串是 18:00形式 截取的字符串从下标0开始 , 往后截取两位
		if(HH<Integer.parseInt((preferences.getString("time", "").substring(0, 2)))){
			dateText = "昨天";
		} else {
			dateText = "今天";
		}
		publishText.setText(dateText+preferences.getString("time", "")+"发布");
		currentDateText.setText(preferences.getString("current_time", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		//激活AutoUpdateService服务
		/**
		 * 这样只要一旦选中了某个城市并成功更新天气之后，AutoUpdateService 就会一直在后台
		 * 运行，并保证每 8 小时更新一次天气
		 */
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}
	
	/**
	 * 相隔2秒 再按一次就退出 吐司显示
	 */
	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis()-returnTime > 2000) {
			Toast.makeText(this, "再按一次退出", 0).show();
			returnTime = System.currentTimeMillis();
			return;
		}
		finish();
	}
}
