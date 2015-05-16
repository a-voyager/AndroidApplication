package com.light.mobilesafe;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.light.mobilesafe.utils.WaterWaveView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class NetManagerActivity extends Activity {

	private WaterWaveView waterWaveView;
	private TextView tv_net_manager_left;
	private TextView tv_net_manager_right;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_net_manager);
		setStatus();
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> installedApplications = pm
				.getInstalledApplications(0);
		for (ApplicationInfo info : installedApplications) {
			int uid = info.uid;
			long uploadTotal = TrafficStats.getUidTxBytes(uid);
			long downloadTotal = TrafficStats.getUidRxBytes(uid);
		}
		long mobileTxBytes = TrafficStats.getMobileTxBytes();
		long mobileRxBytes = TrafficStats.getMobileRxBytes();
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		long totalRxBytes = TrafficStats.getTotalRxBytes();

		tv_net_manager_left = (TextView) findViewById(R.id.tv_net_manager_left);
		tv_net_manager_right = (TextView) findViewById(R.id.tv_net_manager_right);
		waterWaveView = (WaterWaveView) findViewById(R.id.water_net_manager);

  
		tv_net_manager_right.setText("本月已用："
				+ Formatter.formatFileSize(NetManagerActivity.this,
						mobileRxBytes + mobileTxBytes));

		
		waterWaveView.startWave();
	}

	private void setStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.statusbar_net_manager);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	@Override
	protected void onDestroy() {
		waterWaveView.stopWave();
		waterWaveView = null;
		super.onDestroy();
	}

}
