package com.light.mobilesafe;

import com.light.mobilesafe.service.CallIntercept;
import com.light.mobilesafe.service.ShowAddressService;
import com.light.mobilesafe.ui.SettingItem;
import com.light.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	private SettingItem item;
	private SharedPreferences sp;
	private Editor spEditor;

	private SettingItem si_showAddress;
	private Intent showAddressServiceIntent;

	private SettingItem si_intercept;
	private Intent interceptIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		//骚扰拦截
		si_intercept = (SettingItem) findViewById(R.id.si_callsafe_intercept);
		si_intercept.setSIVchecked(sp.getBoolean("callIntercept", false));
		interceptIntent = new Intent(SettingActivity.this, CallIntercept.class);
		si_intercept.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(si_intercept.getCbStatus()){
					si_intercept.setSIVchecked(false);
					editor.putBoolean("callIntercept", false);
					stopService(interceptIntent);
					System.out.println("startService(interceptIntent);");
				} else {
					si_intercept.setSIVchecked(true);
					editor.putBoolean("callIntercept", true);
					startService(interceptIntent);
					System.out.println("stopService(interceptIntent);");
				}
				editor.commit();
			}
		});
		
		//来电归属地显示
		si_showAddress = (SettingItem) findViewById(R.id.si_setting_show_address);
		si_showAddress.setSIVchecked((sp.getBoolean("showAddress", false)));
		showAddressServiceIntent = new Intent(SettingActivity.this,
				ShowAddressService.class);
		si_showAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (si_showAddress.getCbStatus()) {
					si_showAddress.setSIVchecked(false);
					stopService(showAddressServiceIntent);
					System.out.println("stop(showAddressServiceIntent);");
					editor.putBoolean("showAddress", false);
				} else {
					si_showAddress.setSIVchecked(true);
					startService(showAddressServiceIntent);
					System.out
							.println("startService(showAddressServiceIntent);");
					editor.putBoolean("showAddress", true);
				}
				editor.commit();
			}
		});

		item = (SettingItem) findViewById(R.id.si_setting_autoUpdate);
		item.setTvTitle();
		readSettings();
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (item.getCbStatus()) {
					// 已经选中，需设置为未选中
					setUpdateItemStatus(false);
				} else {
					setUpdateItemStatus(true);
				}
			}
		});
	}

	// 提示：Boolean 和 boolean 是有大大的区别的

	/**
	 * 设置更新状态、标题、内容，并储存
	 */
	private void setUpdateItemStatus(boolean bool) {
		item.setCbStatus(bool);
		spEditor = sp.edit();
		spEditor.putBoolean("AutoUpdate", bool);
		String a = null;
		if (bool) {
			// a = "开启";
			item.setCheckedTvContent();
		} else {
			// a = "关闭";
			item.setCheckedTvContent();
		}
		spEditor.commit();
		// item.setTvContent("自动更新已"+a);
	}

	/**
	 * 读取设置数据
	 */
	private void readSettings() {
		if (sp.getBoolean("AutoUpdate", true)) {
			setUpdateItemStatus(true);
		} else {
			setUpdateItemStatus(false);
		}

	}

}
