package com.light.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoCleanService extends Service {

	private LockScreenReceiver receiver;
	private ActivityManager am;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		receiver = new LockScreenReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		super.onCreate();
		System.out.println("AutoCleanService：onCreate()");
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
		System.out.println("AutoCleanService：onCreate()");
	}
	
	private class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("screen off");
			List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
			for(RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses){
				am.killBackgroundProcesses(runningAppProcessInfo.processName);
				System.out.println("自动清理："+runningAppProcessInfo.processName);
			}
		}
		
	}
}
