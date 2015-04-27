package com.light.mobilesafe;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskManagerSettingActivity extends Activity implements
		OnCheckedChangeListener {

	private CheckBox cb_task_setting_clean;
	private CheckBox cb_task_setting_showsys;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager_setting);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.statusbar_task_manager);
		cb_task_setting_clean = (CheckBox) findViewById(R.id.cb_task_setting_clean);
		cb_task_setting_showsys = (CheckBox) findViewById(R.id.cb_task_setting_showsys);
		cb_task_setting_showsys.setOnCheckedChangeListener(this);
		cb_task_setting_clean.setOnCheckedChangeListener(this);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_task_setting_showsys.setChecked(sp.getBoolean("showSys", true));
		cb_task_setting_clean.setChecked(sp.getBoolean("autoClean", false));
	}

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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Editor editor = sp.edit();
		switch (buttonView.getId()) {
		case R.id.cb_task_setting_clean:
			editor.putBoolean("autoClean", isChecked);
			System.out.println("putBoolean(autoClean, isChecked);"+isChecked);
			break;

		case R.id.cb_task_setting_showsys:
			editor.putBoolean("showSys", isChecked);
			System.out.println("putBoolean(showSys, isChecked)"+isChecked);
			break;

		}
		editor.commit();
	}
}
