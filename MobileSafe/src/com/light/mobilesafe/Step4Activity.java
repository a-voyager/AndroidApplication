package com.light.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class Step4Activity extends BaseStepActivity {

	private CheckBox cb_openService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step4);
		cb_openService = (CheckBox) findViewById(R.id.cb_phonefinder_step4_openservice);
		cb_openService.setChecked(sp.getBoolean("openService", false));
	}

	@Override
	public void toNext(View view) {
		Editor editor = sp.edit();
		if(cb_openService.isChecked()){
			editor.putBoolean("openService", true);
		}else{
			editor.putBoolean("openService", false);
		}
		editor.putBoolean("PhoneFinderConfiged", true);
		editor.commit();
		startActivity(new Intent(this, PhoneFinderActivity.class));	
		finish();
		overridePendingTransition(R.anim.translate_window_in,
				R.anim.translate_window_out);
	}

	@Override
	public void toLast(View view) {
		startActivity(new Intent(this, Step3Activity.class));
		finish();
		overridePendingTransition(R.anim.translate_window_in_back,
				R.anim.translate_window_out_back);
	}

	
}
