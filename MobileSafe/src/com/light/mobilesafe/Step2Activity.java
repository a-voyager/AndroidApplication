package com.light.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class Step2Activity extends BaseStepActivity {

	private CheckBox cb_phonefinder_step2;
	private TelephonyManager tm;
	private String simSerialNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step2);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		simSerialNumber = tm.getSimSerialNumber();
		cb_phonefinder_step2 = (CheckBox) findViewById(R.id.cb_phonefinder_step2);
		cb_phonefinder_step2.setChecked(!TextUtils.isEmpty(sp.getString(
				"simNum", null)));
		
	}

	@Override
	public void toNext(View view) {
		Editor editor = sp.edit();
		if (!cb_phonefinder_step2.isChecked()) {
			System.out.println("Î´°ó¶¨SIM¿¨");
			editor.putString("simNum", null);
			Toast.makeText(Step2Activity.this, "Çë°ó¶¨SIM¿¨", 0).show();
		} else {
			editor.putString("simNum", simSerialNumber);
		}
		editor.commit();
		startActivity(new Intent(this, Step3Activity.class));
		finish();
		overridePendingTransition(R.anim.translate_window_in,
				R.anim.translate_window_out);
	}

	@Override
	public void toLast(View view) {
		startActivity(new Intent(this, Step1Activity.class));
		finish();
		overridePendingTransition(R.anim.translate_window_in_back,
				R.anim.translate_window_out_back);
	}

}
