package com.light.mobilesafe.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class SystemInfoUtils {
	public static int getRunningAppProcesses(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		return runningAppProcesses.size();
	}

	public static Map<String, Long> getMemInfo(Context context){
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put("availMem", outInfo.availMem);
		map.put("totalMem", outInfo.totalMem);
		return map;
	}
}
