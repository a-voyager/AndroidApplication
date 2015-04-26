package com.light.mobilesafe;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.light.mobilesafe.utils.SmsUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class AtoolsActivity extends Activity implements OnClickListener {

	private Button btn_atools_position_query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.statusbar_atools);
		btn_atools_position_query = (Button) findViewById(R.id.btn_atools_position_query);
		Button btn_atools_sms_backup = (Button) findViewById(R.id.btn_atools_sms_backup);
		Button btn_atools_sms_recovery = (Button) findViewById(R.id.btn_atools_sms_recovery);
		btn_atools_position_query.setOnClickListener(this);
		btn_atools_sms_backup.setOnClickListener(this);
		btn_atools_sms_recovery.setOnClickListener(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_atools_position_query:
			Intent intent = new Intent(AtoolsActivity.this,
					PositionQueryActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_atools_sms_backup:
			final ProgressDialog dialog = new ProgressDialog(
					AtoolsActivity.this);
			dialog.setMessage("备份短信中...");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					SmsUtils.smsBackup(AtoolsActivity.this, dialog);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份完成", 0)
									.show();
							dialog.dismiss();
						}
					});
				}
			}).start();

			break;
		case R.id.btn_atools_sms_recovery:

			// Intent setIntent = new Intent(Intent.ACTION_MAIN);
			// ComponentName component = new
			// ComponentName("com.android.settings",
			// "com.android.settings.Settings");
			// setIntent.setComponent(component);
			// setIntent.putExtra(":android:show_fragment",
			// "com.android.settings.application.AppOpsSummary");
			// startActivity(setIntent);

			File saveFile = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/backup/sms");
			File file = new File(saveFile, "smsBackup.xml");
			if (!file.exists()) {
				Toast.makeText(AtoolsActivity.this, "暂无可用还原文件", 0).show();
				return;
			}
			final ProgressDialog recDialog = new ProgressDialog(
					AtoolsActivity.this);
			recDialog.setMessage("还原短信中");
			recDialog.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					SmsUtils.smsRec(AtoolsActivity.this, recDialog, true);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原备份完成", 0)
									.show();
							recDialog.dismiss();
						}
					});
				}
			}).start();
			break;
		}
	}

}
