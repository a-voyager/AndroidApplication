package com.light.mobilesafe.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootBroadcastReciver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	private String simNum;

	@Override
	public void onReceive(Context context, Intent intent) {
		tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		if (sp.getBoolean("openService", false)) {
			simNum = sp.getString("simNum", null);
			if (!tm.getSimSerialNumber().equals(simNum)) {
				// 插入了新SIM卡
				String safeNum = sp.getString("safeNum", null);
				System.out.println("插入了新SIM卡");
				System.out.println("safeNum:" + safeNum);
				
				//发送短信
				
				if (safeNum != null) {
					SmsManager manager = SmsManager.getDefault();
					manager.sendTextMessage(safeNum, null, "您的手机SIM卡已被更换", null,	null);
				}
			} else {
				System.out.println("SIM卡未更换");
			}
		}
		
	}
}
