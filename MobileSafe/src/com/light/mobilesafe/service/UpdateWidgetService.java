package com.light.mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.light.mobilesafe.R;
import com.light.mobilesafe.utils.SystemInfoUtils;

public class UpdateWidgetService extends Service {

	private Timer timer;
	private TimerTask task;
	private AppWidgetManager appWidgetManager;
	private LockScreenReceiver lockScreenReceiver;
	private ActivityManager am;
	private unLockScreenReceiver unLockScreenReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		lockScreenReceiver = new LockScreenReceiver();
		unLockScreenReceiver = new unLockScreenReceiver();
		registerReceiver(lockScreenReceiver, new IntentFilter(
				Intent.ACTION_SCREEN_OFF));
		registerReceiver(unLockScreenReceiver, new IntentFilter(
				Intent.ACTION_SCREEN_ON));
		startTask();
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(lockScreenReceiver);
		stopTask();
		super.onDestroy();
	}

	private void stopTask() {
		if (timer != null && task != null) {
			lockScreenReceiver = null;
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}

	private class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("screen off");
			stopTask();
		}

	}

	private class unLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("screen on");
			startTask();
		}

	}

	public void startTask() {
		if (task == null && task == null) {
			am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			timer = new Timer();
			appWidgetManager = AppWidgetManager
					.getInstance(UpdateWidgetService.this);
			task = new TimerTask() {

				@Override
				public void run() {
					ComponentName provider = new ComponentName(
							UpdateWidgetService.this, Widget.class);
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.widget);
					views.setTextViewText(
							R.id.tv_widget_count,
							"当前进程数："
									+ SystemInfoUtils
											.getRunningAppProcesses(UpdateWidgetService.this));
					views.setTextViewText(
							R.id.tv_widget_mem,
							"剩余内存："
									+ Formatter.formatFileSize(
											UpdateWidgetService.this,
											SystemInfoUtils.getMemInfo(
													UpdateWidgetService.this)
													.get("availMem")));
					Intent intent = new Intent();
					intent.setAction("com.light.mobilesafe.clean");

					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							UpdateWidgetService.this, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.iv_widget_clean,
							pendingIntent);
					appWidgetManager.updateAppWidget(provider, views);
					System.out.println("updateAppWidget(provider, views)");
				}
			};
			timer.schedule(task, 0, 3000);
		}
	}

}
