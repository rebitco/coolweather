package com.coolweather.app.util;

import android.R;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionUtil {
	/**
	 * °æ±¾Ãû
	 */
	public static String getVersionName(Context context){
		try {
			PackageInfo pi = 
					context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return context.getString(R.string.unknownName);
		}
	}
	
	/**
	 * °æ±¾ºÅ
	 */
	public static int getVersionCode(Context context){
		PackageInfo pi;
		try {
			pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
