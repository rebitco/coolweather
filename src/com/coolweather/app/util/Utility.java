package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	/**
	 * 解析与处理服务器返回的省份数据
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response){
		if (!TextUtils.isEmpty(response)) {
			//返回值:01|北京,02|上海,03|天津,04|重庆,05|黑龙江,06|吉林,07|辽宁
			//用","将返回值拆分
			String[] allProvinces = response.split(",");
			if (allProvinces!=null && allProvinces.length>0) {
				for(String p : allProvinces){
					String[] array = p.split("\\|");//|是转义符 , 必须加\\进行转义才能正常拆分
					Province province = new Province();
					//省份代码位于array第一个位置
					province.setProvinceCode(array[0]);
					//省份名位于array第二个位置
					province.setProvinceName(array[1]);
					//将解析出来的数据储存到Province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析并处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities!=null && allCities.length>0) {
				for(String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析并处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties!=null && allCounties.length>0) {
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析服务器返回的json数据 , 并将数据储存到本地
	 * {"weatherinfo":
	 *	{"city":"昆山","cityid":"101190404","temp1":"21℃","temp2":"9℃",
	 *	"weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}
	 *	}
	 */
	
	/**
	 * 百度API的天气json数据
	 *  "errNum": 0,
	    "errMsg": "success",
	    "retData": {
	        "city": "北京",
	        "pinyin": "beijing",
	        "citycode": "101010100",
	        "date": "16-06-08",
	        "time": "08:00",
	        "postCode": "100000",
	        "longitude": 116.391,
	        "latitude": 39.904,
	        "altitude": "33",
	        "weather": "多云",
	        "temp": "31",
	        "l_tmp": "19",
	        "h_tmp": "31",
	        "WD": "无持续风向",
	        "WS": "微风(<10km/h)",
	        "sunrise": "04:45",
	        "sunset": "19:41"
	    }
	 */
	public static void handleWeatherResponse(Context context, String response) {
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weather_info = jsonObject.getJSONObject("retData");
			String cityName = weather_info.getString("city");
			String weatherCode = weather_info.getString("citycode");
			String temp1 = weather_info.getString("l_tmp");
			String temp2 = weather_info.getString("h_tmp");
			String weatherDesp = weather_info.getString("weather");
			String publishTime = weather_info.getString("time");
			//将数据缓存到本地
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将天气数据通过sharedpreferences缓存到本地
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 */
	private static void saveWeatherInfo(Context context, String cityName, String weatherCode,
			String temp1, String temp2, String weatherDesp, String publishTime) {
		SimpleDateFormat dateFormat = 
				new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = 
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		//将选中状态同样保存到本地, 如果在地点选择页面可以判断是否选择了县级
		editor.putBoolean("city_selected", true);
		editor.putString("city", cityName);
		//将weatherCode保存到本地 , 在天气页面直接可以get
		editor.putString("citycode", weatherCode);
		editor.putString("l_temp", temp1);
		editor.putString("h_temp", temp2);
		editor.putString("weather", weatherDesp);
		editor.putString("time", publishTime);
		editor.putString("current_time", dateFormat.format(new Date()));
		editor.commit();
	}
}
