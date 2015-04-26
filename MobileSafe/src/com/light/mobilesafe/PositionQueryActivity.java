package com.light.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.light.mobilesafe.utils.PositionQuery;

import android.app.Activity;
import android.os.Bundle;
import android.renderscript.Byte3;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PositionQueryActivity extends Activity {

	private EditText et_position_query_num;
	private String num;
	private TextView tv_position_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position_query);
		et_position_query_num = (EditText) findViewById(R.id.et_position_query_num);
		tv_position_result = (TextView) findViewById(R.id.tv_position_result);
		
		File file = new File(getFilesDir(), "address.db");
		if(!(file.exists() && file.length() > 0)){

			try {
				InputStream inputStream = getAssets().open("address.db");
				FileOutputStream  fileOutputStream = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = inputStream.read(buffer))!=-1){
					fileOutputStream.write(buffer, 0, len);
					System.out.println("正在拷贝db");
				}
				inputStream.close();
				fileOutputStream.close();
				
			} catch (IOException e) {
				System.out.println("getAssets().open address.db ERROR");
				e.printStackTrace();
			}
		}
		
		
		
		et_position_query_num.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()>=3){
					String postion = new PositionQuery().query(s.toString());
					tv_position_result.setText(postion);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		
		
		
		//点击事件
//		num = et_position_query_num.getText().toString().trim();
	}

	
}
