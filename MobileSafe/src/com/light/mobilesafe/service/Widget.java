package com.light.mobilesafe.service;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Widget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		startUpdateService(context);
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		System.out.println("Widget:onUpdate()");
		startUpdateService(context);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		System.out.println("Widget:onEnabled()");
		startUpdateService(context);
		super.onEnabled(context);
	}

	private void startUpdateService(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}

	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
	

	
	
}
