package com.light.mobilesafe.engin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.light.mobilesafe.domain.Task;

public class TaskInfo {
		public static List<Task> getTaskInfo(Context context){
			List<Task> tasks = new ArrayList<Task>();
			PackageManager manager = context.getPackageManager();
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
			Task task; 
			for(RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
				task = new Task();
				
				task.setPackName(runningAppProcessInfo.processName);
				
				try {
					task.setIcon(manager.getApplicationIcon(runningAppProcessInfo.processName));
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					System.out.println("图标设置错误");
				}
				
				MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
				int menSize = memoryInfo[0].getTotalPrivateDirty() * 1024;
				task.setMem(menSize);
				
				try {
					String appName;
					appName = manager.getApplicationInfo(runningAppProcessInfo.processName, 0).loadLabel(manager).toString();
					task.setAppName(appName);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					task.setAppName(runningAppProcessInfo.processName);
					System.out.println("appName设置错误");
				}
				
				try {
					task.setSysTask((manager.getApplicationInfo(runningAppProcessInfo.processName, 0).flags&ApplicationInfo.FLAG_SYSTEM)!=0);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					System.out.println("判断系统应用错误");
				}
				
				tasks.add(task);
			}
			return tasks;
		}
}
