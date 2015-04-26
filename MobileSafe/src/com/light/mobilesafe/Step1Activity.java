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

public class Step1Activity extends BaseStepActivity {

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step1);
		
	}

	@Override
	public void toNext(View view) {
		startActivity(new Intent(Step1Activity.this, Step2Activity.class));
		finish();
		overridePendingTransition(R.anim.translate_window_in,
				R.anim.translate_window_out);
	}

	@Override
	public void toLast(View view) {
		// TODO Auto-generated method stub

	}

}
