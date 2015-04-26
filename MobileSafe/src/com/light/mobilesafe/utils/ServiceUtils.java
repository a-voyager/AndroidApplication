package com.light.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {

	public static boolean isServiceRunning(Context context, String Service){
		String className = null;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(100);
		for(RunningServiceInfo runningServiceInfo: runningServices){
			className = runningServiceInfo.service.getClassName();
		}
		return Service.equals(className);
		
	}
	
}
