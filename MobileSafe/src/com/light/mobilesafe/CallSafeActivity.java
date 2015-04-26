package com.light.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.light.mobilesafe.db.BlackListDao;
import com.light.mobilesafe.db.BlacklistNum;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class CallSafeActivity extends Activity {

	private ListView lv_call_safe_blacklist;
	private List<BlacklistNum> blacklistNums;
	private BlackListDao blackListDao;
	private ImageView iv_callsafe_blacklist_new;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_safe);
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
	            setTranslucentStatus(true);  
	        }  
	  
	        SystemBarTintManager tintManager = new SystemBarTintManager(this);  
	        tintManager.setStatusBarTintEnabled(true);  
	        tintManager.setStatusBarTintResource(R.color.statusbar_call_safe);//通知栏所需颜色  
		lv_call_safe_blacklist = (ListView) findViewById(R.id.lv_call_safe_blacklist);

		iv_callsafe_blacklist_new = (ImageView) findViewById(R.id.iv_callsafe_blacklist_new);

		blackListDao = new BlackListDao(CallSafeActivity.this);
		blacklistNums = blackListDao.queryAll();
		adapter = new MyAdapter();
		lv_call_safe_blacklist.setAdapter(adapter);
		lv_call_safe_blacklist
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						showChangeDialog(position);
					}

					private void showChangeDialog(final int position) {
						AlertDialog.Builder builder = new Builder(CallSafeActivity.this);
						final AlertDialog dialog = builder.create();
						final View view = View.inflate(CallSafeActivity.this,
								R.layout.dialog_callsafe_new_blacklist, null);
						dialog.setView(view);
						Button btn_ok = (Button) view.findViewById(R.id.btn_dialog_ok);
						Button btn_cancel = (Button) view
								.findViewById(R.id.btn_dialog_cancel);
						
						final EditText et_dialog_name = (EditText) view
								.findViewById(R.id.et_dialog_name);
						final EditText et_dialog_num = (EditText) view
								.findViewById(R.id.et_dialog_num);
						et_dialog_name.setText(blacklistNums.get(position).getName());
						et_dialog_num.setText(blacklistNums.get(position).getNum());
						
						final CheckBox cb_intercept_call = (CheckBox) view.findViewById(R.id.cb_intercept_call);
						final CheckBox cb_intercept_sms = (CheckBox) view.findViewById(R.id.cb_intercept_sms);

						String mode = blacklistNums.get(position).getMode();
						
						if(mode.contains("s")){
							cb_intercept_sms.setChecked(true);
						}
						if(mode.contains("c")){
							cb_intercept_call.setChecked(true);
						}
						
						btn_cancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						btn_ok.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
								
								
								String newname = et_dialog_name.getText().toString();
								String num = et_dialog_num.getText().toString().trim();
								if (TextUtils.isEmpty(num)
										|| (!cb_intercept_call.isChecked() && !cb_intercept_sms
												.isChecked())) {
									Toast.makeText(CallSafeActivity.this, "请正确填写", 0)
											.show();
									return;
								}
								String mode = null;
								if(!cb_intercept_sms.isChecked() && cb_intercept_call.isChecked()){
									mode = "c";
								} else if(!cb_intercept_call.isChecked() && cb_intercept_sms.isChecked()){
									mode = "s";
								} else {
									mode = "sc";
								}
								blackListDao.update(num, newname, mode);
								blacklistNums.remove(position);
								BlacklistNum blacklistNum = new BlacklistNum();
								blacklistNum.setMode(mode);
								blacklistNum.setName(newname);
								blacklistNum.setNum(num);
								blacklistNums.add(position, blacklistNum);
								adapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				});

		iv_callsafe_blacklist_new.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showNewBlacklistDialog();
			}

			public void showNewBlacklistDialog() {
				AlertDialog.Builder builder = new Builder(CallSafeActivity.this);
				final AlertDialog dialog = builder.create();
				final View view = View.inflate(CallSafeActivity.this,
						R.layout.dialog_callsafe_new_blacklist, null);
				Button btn_ok = (Button) view.findViewById(R.id.btn_dialog_ok);
				Button btn_cancel = (Button) view
						.findViewById(R.id.btn_dialog_cancel);
				btn_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 获取输入信息，保存数据库，更新listview
						EditText et_dialog_name = (EditText) view
								.findViewById(R.id.et_dialog_name);
						EditText et_dialog_num = (EditText) view
								.findViewById(R.id.et_dialog_num);
						CheckBox cb_intercept_call = (CheckBox) view.findViewById(R.id.cb_intercept_call);
						CheckBox cb_intercept_sms = (CheckBox) view.findViewById(R.id.cb_intercept_sms);

						String name = et_dialog_name.getText().toString();
						String num = et_dialog_num.getText().toString().trim();

						if (TextUtils.isEmpty(num)
								|| (!cb_intercept_call.isChecked() && !cb_intercept_sms
										.isChecked())) {
							Toast.makeText(CallSafeActivity.this, "请正确填写", 0)
									.show();
							return;
						}

						System.out.println("name=" + name + "\tnum=" + num);
						
						String mode = null;
						if(!cb_intercept_sms.isChecked() && cb_intercept_call.isChecked()){
							mode = "c";
						} else if(!cb_intercept_call.isChecked() && cb_intercept_sms.isChecked()){
							mode = "s";
						} else {
							mode = "sc";
						}
						
						blackListDao.add(num, name, mode);
						
						BlacklistNum newNum = new BlacklistNum();
						newNum.setMode(mode);
						newNum.setName(name);
						newNum.setNum(num);
						blacklistNums.add(0, newNum);
						
						adapter.notifyDataSetChanged();
						
						dialog.dismiss();
					}
				});
				btn_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.setView(view);
				dialog.show();
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return blacklistNums.size();
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.callsafe_blacklist_num_item, null);
				holder = new ViewHolder();
				holder.tv_num_and_name = (TextView) view
						.findViewById(R.id.tv_callsafe_blacklist_num_and_name);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_callsafe_blacklist_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_blacklist_delete);
				view.setTag(holder);
			}

			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSafeActivity.this);
					builder.setTitle("提示");
					builder.setMessage("确认从黑名单中移除该号码？");
					builder.setPositiveButton("移除", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							blackListDao.delete(blacklistNums.get(position).getNum());
							System.out.println("移除"+blacklistNums.get(position).getNum());
							
							blacklistNums.remove(position);
							adapter.notifyDataSetChanged();
							
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
			
			String num_and_name;
			if (TextUtils.isEmpty(blacklistNums.get(position).getName())) {

				num_and_name = blacklistNums.get(position).getNum();
			} else {
				num_and_name = blacklistNums.get(position).getName() + "（"
						+ blacklistNums.get(position).getNum() + "）";
			}
			holder.tv_num_and_name.setText(num_and_name);
			String mode = blacklistNums.get(position).getMode();
			if (mode.equals("s")) {
				mode = "仅拦截短信";
			} else if (mode.equals("c")) {
				mode = "仅拦截通话";
			} else if (mode.equals("sc")) {
				mode = "拦截通话和短信";
			} else {
				mode = "未启用拦截";
			}
			holder.tv_mode.setText(mode);
			return view;
		}

	}

	private class ViewHolder {
		public TextView tv_num_and_name;
		public TextView tv_mode;
		public ImageView iv_delete;
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

}
