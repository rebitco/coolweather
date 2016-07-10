package com.coolweather.app.util;

import android.app.AlertDialog;
import android.content.Context;

public class DialogUtil {
	public static void show(Context context, String title, String message, boolean cancelable){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title)
			  .setMessage(message)
			  .setCancelable(cancelable)
			  .create()
			  .show();
	}
}
