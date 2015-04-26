package com.light.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Step3Activity extends BaseStepActivity {

	private Button btn_choose_num;
	private EditText et_safe_num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step3);
		et_safe_num = (EditText) findViewById(R.id.et_phonefinder_safe_num);
		btn_choose_num = (Button) findViewById(R.id.btn_phonefinder_choose_num);
		et_safe_num.setText(sp.getString("safeNum", null));
		btn_choose_num.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Step3Activity.this, SelectContactActivity.class);
				startActivityForResult(intent, 0);
				
			}
		});
	}

	@Override
	public void toNext(View view) {
		if(TextUtils.isEmpty(et_safe_num.getText().toString().trim())){
			Toast.makeText(Step3Activity.this, "«Î ‰»Î∞≤»´∫≈¬Î", 0).show();
			return;
		}
		Editor editor = sp.edit();
		editor.putString("safeNum", et_safe_num.getText().toString().trim());
		editor.commit();
		startActivity(new Intent(this, Step4Activity.class));	
		finish();
		overridePendingTransition(R.anim.translate_window_in,
				R.anim.translate_window_out);
	}

	@Override
	public void toLast(View view) {
		startActivity(new Intent(this, Step2Activity.class));
		finish();
		overridePendingTransition(R.anim.translate_window_in_back,
				R.anim.translate_window_out_back);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != 0){
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
		String phone = data.getStringExtra("phone");
		et_safe_num.setText(phone);
		super.onActivityResult(requestCode, resultCode, data);
	}
}
