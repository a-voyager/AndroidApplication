package com.light.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.light.mobilesafe.db.BlackListDao;

public class CallIntercept extends Service {

	private SmsReceiver smsReceiver;
	private BlackListDao dao;
	private TelephonyManager tm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage smsMessage = SmsMessage
						.createFromPdu((byte[]) object);
				String address = smsMessage.getOriginatingAddress();
				String mode = dao.queryMode(address);
				if (mode.contains("s")) {
					abortBroadcast();
					System.out.println("拦截：" + address);
				}
			}
		}

	}

	@Override
	public void onCreate() {
		dao = new BlackListDao(getApplicationContext());
		smsReceiver = new SmsReceiver();
		IntentFilter intentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(smsReceiver, intentFilter);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		System.out.println("拦截服务已创建");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(smsReceiver);
		smsReceiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				if (dao.queryMode(incomingNumber).contains("c")) {
					System.out.println("需要挂断" + incomingNumber);

					getContentResolver().registerContentObserver(
							Uri.parse("content://call_log/calls"), true,
							new ContentObserver(new Handler()) {

								@Override
								public boolean deliverSelfNotifications() {
									// TODO Auto-generated method stub
									return super.deliverSelfNotifications();
								}

								@Override
								public void onChange(boolean selfChange) {
									super.onChange(selfChange);
									System.out.println("通话记录更新");
									getContentResolver()
											.unregisterContentObserver(this);
									delCallLog(incomingNumber);
								}

							});

					interceptCall();
				}

				break;

			default:
				break;
			}
		}

	}

	/**
	 * 挂断电话,需要两个包 com.android.internal.telephony
	 */
	public void interceptCall() {
		// IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			// 加载servicemanager的字节码
			Class clazz = CallIntercept.class.getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("挂断电话");
	}

	public void delCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		resolver.delete(Uri.parse("content://call_log/calls"), "number = ?",
				new String[] { incomingNumber });
	}
}
