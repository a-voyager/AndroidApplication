package com.light.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.light.mobilesafe.utils.Md5;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class MainActivity extends Activity {

	private String[] itemList = { "手机防盗", "通讯卫士", "应用管理", "进程管理", "流量统计",
			"手机杀毒", "缓存清理", "高级工具", "设置中心"

	};

	private int[] itemIcon = { R.drawable.safe_0, R.drawable.callmanager_1,
			R.drawable.appmanager_2, R.drawable.taskmanager_3,
			R.drawable.netmanager_4, R.drawable.antivirus_5,
			R.drawable.cachemanager_6, R.drawable.advancedtools_7,
			R.drawable.settings_8 };

	private SharedPreferences sp;

	private EditText et_firstPwd;

	private EditText et_secondPwd;

	private AlertDialog dialog;

	private EditText et_pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
            setTranslucentStatus(true);  
        }  
  
        SystemBarTintManager tintManager = new SystemBarTintManager(this);  
        tintManager.setStatusBarTintEnabled(true);  
        tintManager.setStatusBarTintResource(R.color.statusbar_act_main);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		GridView gridView_main = (GridView) findViewById(R.id.gv_main);
		MyAdapter adapter = new MyAdapter();
		gridView_main.setAdapter(adapter);

		// 项目点击事件
		gridView_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: // 手机防盗
					toPhoneFinder();
					break;

				case 1:
					startActivity(new Intent(MainActivity.this,
							CallSafeActivity.class));
					break;

				case 2: // 应用管理
					startActivity(new Intent(MainActivity.this,
							AppManagerActivity.class));
					break;

				case 3: //进程管理
					startActivity(new Intent(MainActivity.this,
							TaskManagerActivity.class));
					break;

				case 4:

					break;

				case 5:

					break;

				case 6:

					break;

				case 7: // 高级工具
					startActivity(new Intent(MainActivity.this,
							AtoolsActivity.class));
					break;

				case 8: // 设置
					startActivity(new Intent(MainActivity.this,
							SettingActivity.class));
					break;

				default:
					break;
				}

			}
		});

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

	
	/**
	 * 执行进入点击手机防盗按钮的操作
	 */
	protected void toPhoneFinder() {
		// if (hasPwd()) {
		// View view = View.inflate(MainActivity.this,
		// R.layout.dialog_phonefinder_inputpwd, null);
		// builder.setView(view);
		// builder.show();
		//
		// } else
		if (hasPwd()) {
			showInputDialog();
		} else {
			showSetDialog();
		}
	}

	private void showInputDialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		View view = View.inflate(MainActivity.this,
				R.layout.dialog_phonefinder_inputpwd, null);
		et_pwd = (EditText) view.findViewById(R.id.et_dialog_inputpwd);
		Button btn_cancel = (Button) view
				.findViewById(R.id.btn_dialog_inputpwd_cancel);
		Button btn_ok = (Button) view.findViewById(R.id.btn_dialog_inputpwd_ok);
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_pwd.getText().toString().trim();

				// md5加密

				password = new Md5().coding(password);

				String realPwd = sp.getString("pwd", null);
				if (!realPwd.equals(password)) {
					Toast.makeText(MainActivity.this, "密码错误", 0).show();

					Animation shake = AnimationUtils.loadAnimation(
							MainActivity.this, R.anim.etshake);
					et_pwd.startAnimation(shake);

					et_pwd.setText("");
					return;
				}
				if (realPwd.equals(password)) {
					Toast.makeText(MainActivity.this, "密码正确", 0).show();

					dialog.dismiss();
					// 启动第二界面

					sp = getSharedPreferences("config", MODE_PRIVATE);
					if (!sp.getBoolean("PhoneFinderConfiged", false)) {
						Intent intent = new Intent(MainActivity.this,
								Step1Activity.class);
						startActivity(intent);
					} else {
						startActivity(new Intent(MainActivity.this,
								PhoneFinderActivity.class));
					}

				}
			}
		});

		dialog = builder.create();
		dialog.setView(view);
		dialog.show();

	}

	private void showSetDialog() {

		System.out.println("showSetDialog()");
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		View view = View.inflate(MainActivity.this,
				R.layout.dialog_phonefinder_setpwd, null);
		et_firstPwd = (EditText) view.findViewById(R.id.et_dialog_setpwd_first);
		et_secondPwd = (EditText) view
				.findViewById(R.id.et_dialog_setpwd_second);
		Button btn_ok = (Button) view.findViewById(R.id.btn_dialog_setpwd_ok);
		Button btn_cancel = (Button) view
				.findViewById(R.id.btn_dialog_setpwd_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String firstPwd = et_firstPwd.getText().toString().trim();
				String secondPwd = et_secondPwd.getText().toString().trim();

				if (firstPwd.isEmpty() || secondPwd.isEmpty()) {
					Toast.makeText(MainActivity.this, "密码不能为空", 0).show();
					return;
				}

				if (!firstPwd.equals(secondPwd)) {
					Toast.makeText(MainActivity.this, "密码不一致", 0).show();
					et_secondPwd.setText("");
					return;
				}

				// 执行密码输入符合规定的操作
				// 执行md5加密
				String password = firstPwd;
				// 执行md5加密
				// 执行md5加密
				password = new Md5().coding(password);
				// 保存密码
				Editor editor = sp.edit();
				editor.putString("pwd", password);
				editor.commit();

				Toast.makeText(MainActivity.this, "密码保存成功", 0).show();
				dialog.dismiss();
			}
		});

		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

	}

	private boolean hasPwd() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		return !TextUtils.isEmpty(sp.getString("pwd", null));
	}

	/**
	 * @author wuhaojie 主界面选项Adapter
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return itemList.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(MainActivity.this,
					R.layout.main_gv_item_view, null);
			TextView textView = (TextView) view.findViewById(R.id.tv_item);
			ImageView imageView = (ImageView) view.findViewById(R.id.iv_item);

			textView.setText(itemList[position]);
			imageView.setImageResource(itemIcon[position]);

			return view;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(MainActivity.this, SettingActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
