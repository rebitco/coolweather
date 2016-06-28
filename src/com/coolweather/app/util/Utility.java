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
	 * �����봦����������ص�ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response){
		if (!TextUtils.isEmpty(response)) {
			//����ֵ:01|����,02|�Ϻ�,03|���,04|����,05|������,06|����,07|����
			//��","������ֵ���
			String[] allProvinces = response.split(",");
			if (allProvinces!=null && allProvinces.length>0) {
				for(String p : allProvinces){
					String[] array = p.split("\\|");//|��ת��� , �����\\����ת������������
					Province province = new Province();
					//ʡ�ݴ���λ��array��һ��λ��
					province.setProvinceCode(array[0]);
					//ʡ����λ��array�ڶ���λ��
					province.setProvinceName(array[1]);
					//���������������ݴ��浽Province��
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ������������������ص��м�����
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
	 * ������������������ص��ؼ�����
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
	 * �������������ص�json���� , �������ݴ��浽����
	 * {"weatherinfo":
	 *	{"city":"��ɽ","cityid":"101190404","temp1":"21��","temp2":"9��",
	 *	"weather":"����תС��","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}
	 *	}
	 */
	
	/**
	 * �ٶ�API������json����
	 *  "errNum": 0,
	    "errMsg": "success",
	    "retData": {
	        "city": "����",
	        "pinyin": "beijing",
	        "citycode": "101010100",
	        "date": "16-06-08",
	        "time": "08:00",
	        "postCode": "100000",
	        "longitude": 116.391,
	        "latitude": 39.904,
	        "altitude": "33",
	        "weather": "����",
	        "temp": "31",
	        "l_tmp": "19",
	        "h_tmp": "31",
	        "WD": "�޳�������",
	        "WS": "΢��(<10km/h)",
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
			//�����ݻ��浽����
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����������ͨ��sharedpreferences���浽����
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
				new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor = 
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		//��ѡ��״̬ͬ�����浽����, ����ڵص�ѡ��ҳ������ж��Ƿ�ѡ�����ؼ�
		editor.putBoolean("city_selected", true);
		editor.putString("city", cityName);
		//��weatherCode���浽���� , ������ҳ��ֱ�ӿ���get
		editor.putString("citycode", weatherCode);
		editor.putString("l_temp", temp1);
		editor.putString("h_temp", temp2);
		editor.putString("weather", weatherDesp);
		editor.putString("time", publishTime);
		editor.putString("current_time", dateFormat.format(new Date()));
		editor.commit();
	}
}
