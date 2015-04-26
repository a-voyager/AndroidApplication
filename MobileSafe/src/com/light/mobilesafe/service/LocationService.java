package com.light.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

	private LocationManager locationManager;
	private SharedPreferences sp;
	private MyListener listener;

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("LocationService——onCreate()");
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(criteria, true);
		listener = new MyListener();
		locationManager.requestLocationUpdates(provider, 0, 0, listener);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
	

		return null;
	}

	private class MyListener implements LocationListener {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			// 纬度
			double latitude = location.getLatitude();
			// 经度
			double longitude = location.getLongitude();
			// 精确度
			float accuracy = location.getAccuracy();

			ModifyOffset modifyOffset;
			try {
				modifyOffset = ModifyOffset.getInstance(LocationService.class.getResourceAsStream("axisoffset.dat"));
				PointDouble double1 =modifyOffset.s2c(new PointDouble(latitude, longitude));
				latitude = double1.x;
				longitude = double1.y;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("latitude=" + latitude + "\nlongitude="
					+ longitude + "\naccuracy" + accuracy);
			String lastLocation = "latitude=" + latitude + "\nlongitude="
					+ longitude + "\naccuracy=" + accuracy;
			sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastLocation", lastLocation);
			editor.commit();
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("LocationService——onStartCommand()");
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(criteria, true);
		listener = new MyListener();
		locationManager.requestLocationUpdates(provider, 0, 0, listener);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(listener);
		listener = null;
		super.onDestroy();
	}

}
