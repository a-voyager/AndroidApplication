package com.light.mobilesafe.service;

import com.light.mobilesafe.utils.PositionQuery;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author wuhaojie
 *	来电归属地显示服务
 */
public class ShowAddressService extends Service {

	private TelephonyManager tm;
	private MyListener listener;
	private OutCallReciver outCallReceiver;
//	private WindowManager wm;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("服务已创建");
		outCallReceiver = new OutCallReciver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(outCallReceiver, intentFilter);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("服务已开启");
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		return super.onStartCommand(intent, flags, startId);
	}
	private class MyListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				
				String address = new PositionQuery().query(incomingNumber);
				System.out.println("归属地="+address);
				Toast.makeText(ShowAddressService.this, address, 1).show();
//				myToast(address);
				break;
			
			case TelephonyManager.CALL_STATE_IDLE:
				
				
				break;

			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		
	}
	
	@Override
	public void onDestroy() {
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		
		//取消接受者
		
		unregisterReceiver(outCallReceiver);
		outCallReceiver = null;
		super.onDestroy();
	}
	
//	/**
//	 * 自定义吐司
//	 * @param text
//	 */
//	public void myToast(String text) {
//		TextView tv = new TextView(getApplicationContext());
//		tv.setText(text);
//		tv.setTextSize(22);
//	}
	
	
	class OutCallReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String address = new PositionQuery().query(getResultData());
			Toast.makeText(arg0, address, 1).show();
//			myToast(address);
			System.out.println("归属地="+address);
		}

	}
}
