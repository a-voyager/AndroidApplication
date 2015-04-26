package com.light.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PhoneFinderActivity extends Activity {

	private SharedPreferences sp;
	private ImageView iv_configed;
	private TextView tv_safeNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_finder);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		iv_configed = (ImageView) findViewById(R.id.iv_phone_finder_configed);
		tv_safeNum = (TextView) findViewById(R.id.tv_phonefinder_safe_num);
		
		tv_safeNum.setText(sp.getString("safeNum", "Œ¥≈‰÷√"));
		if(sp.getBoolean("openService", false)){
			iv_configed.setImageResource(R.drawable.lock);
		}else {
			iv_configed.setImageResource(R.drawable.unlock);
		}
	}

	
	public void reConfig(View view){
		Intent intent = new Intent(PhoneFinderActivity.this, Step1Activity.class);
		startActivity(intent);
		finish();
	}
	
}
