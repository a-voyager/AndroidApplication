package com.light.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import com.light.mobilesafe.domain.Task;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CleanReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("widget:一键清理被点击");
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo processInfo : list) {
			activityManager.killBackgroundProcesses(processInfo.processName);
		}
	}
}
