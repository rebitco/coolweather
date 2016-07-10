package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.receiver.AutoBootReceiver;
import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.DialogUtil;
import com.coolweather.app.util.VersionUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingActivity extends Activity {
	
	//自动更新天气开关
	private ToggleButton autoSwitch;
	//检查新版本
	private RelativeLayout check_vesion;
	//关于
	private RelativeLayout about_cool;
	//版本号
	private TextView version;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_layout);
		autoSwitch = (ToggleButton) findViewById(R.id.auto_switch);
		check_vesion = (RelativeLayout) findViewById(R.id.rl_check_vesion);
		about_cool = (RelativeLayout) findViewById(R.id.rl_about);
		version = (TextView) findViewById(R.id.version);
		//显示版本号
		String versionName = VersionUtil.getVersionName(this);
		version.setText(versionName);
		//开机自启
		autoSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = 
						PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				if (isChecked) {
					editor.putBoolean("isOpen", isChecked);
				} else {
					editor.putBoolean("isOpen", isChecked);
				}
				editor.commit();
			}
		});
		//一旦启动设置页面, 改变开关的UI
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isOpen = preferences.getBoolean("isOpen", true);
		autoSwitch.setChecked(isOpen);
		
		//弹窗关于
		about_cool.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogUtil.show(SettingActivity.this,
							"About CoolWeather", 
							"        酷天气, 是基于郭大神的书籍-<<第一行代码>>里面的项目案例敲出来的, " +
							"地区接口依然使用中国天气网的, 而天气接口改用了百度API的. 基本上实现了切换地区" +
							"查询实时天气的功能, 后台自启动更新天气数据, 还有一些背景美化等...", 
							true);
			}
		});
		
		//检查新版本
		check_vesion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(SettingActivity.this, "当前版本是最新版", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	//返回事件
	public void returnBtn(View v){
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
	}
}
