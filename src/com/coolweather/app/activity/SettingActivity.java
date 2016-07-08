package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.receiver.AutoBootReceiver;
import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.VersionUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
				if (isChecked) {
					System.out.println("已按下开关...");
				} else {
					System.out.println("已关掉开关...");
				}
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
