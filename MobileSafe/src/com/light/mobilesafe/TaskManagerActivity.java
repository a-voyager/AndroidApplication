package com.light.mobilesafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.light.mobilesafe.domain.Task;
import com.light.mobilesafe.engin.TaskInfo;
import com.light.mobilesafe.utils.SystemInfoUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class TaskManagerActivity extends Activity implements OnClickListener {

	private TextView tv_task_manager_thread;
	private TextView tv_task_mem_avail;
	private TextView tv_task_manager_lv;
	private ListView lv_task_manager_info;
	private List<Task> taskInfoList;
	private List<Task> systaskInfoList;
	private List<Task> usrtaskInfoList;
	private MyAdapter adapter;
	private Button btn_task_manager_select;
	private Button btn_task_manager_clean;
	private Button btn_task_manager_setting;
	private Map<String, Long> memInfo;
	private int thread;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.statusbar_task_manager);// 通知栏所需颜色

		lv_task_manager_info = (ListView) findViewById(R.id.lv_task_manager_info);
		tv_task_manager_lv = (TextView) findViewById(R.id.tv_task_manager_lv);
		tv_task_manager_thread = (TextView) findViewById(R.id.tv_task_manager_thread);
		tv_task_mem_avail = (TextView) findViewById(R.id.tv_task_mem_avail);
		btn_task_manager_select = (Button) findViewById(R.id.btn_task_manager_select);
		btn_task_manager_clean = (Button) findViewById(R.id.btn_task_manager_clean);
		btn_task_manager_setting = (Button) findViewById(R.id.btn_task_manager_setting);

		btn_task_manager_select.setOnClickListener(this);
		btn_task_manager_clean.setOnClickListener(this);
		btn_task_manager_setting.setOnClickListener(this);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		refreshView();
	}

	private void refreshView() {
		final ProgressDialog pd = new ProgressDialog(TaskManagerActivity.this);
		pd.setMessage("请稍候...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				taskInfoList = TaskInfo.getTaskInfo(TaskManagerActivity.this);
				systaskInfoList = new ArrayList<Task>();
				usrtaskInfoList = new ArrayList<Task>();
				for (Task task : taskInfoList) {
					if (task.isSysTask()) {
						systaskInfoList.add(task);
					} else {
						usrtaskInfoList.add(task);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (adapter == null) {
							adapter = new MyAdapter();
						} else {
							adapter.notifyDataSetChanged();
						}
						lv_task_manager_info.setAdapter(adapter);
						lv_task_manager_info
								.setOnScrollListener(new OnScrollListener() {

									@Override
									public void onScrollStateChanged(
											AbsListView view, int scrollState) {

									}

									@Override
									public void onScroll(AbsListView view,
											int firstVisibleItem,
											int visibleItemCount,
											int totalItemCount) {
										tv_task_manager_lv
												.setBackgroundResource(R.color.statusbar_task_manager);
										tv_task_manager_lv
												.setTextColor(Color.WHITE);
										if (firstVisibleItem < usrtaskInfoList
												.size() + 1) {
											tv_task_manager_lv.setText(" 用户进程（"
													+ usrtaskInfoList.size()
													+ "个）");
										} else {
											tv_task_manager_lv.setText(" 系统进程（"
													+ systaskInfoList.size()
													+ "个）");
										}
									}
								});
						lv_task_manager_info
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										Task task;
										if (position == 0
												|| position == usrtaskInfoList
														.size() + 1) {
											return;
										}
										if (position <= usrtaskInfoList.size()) {
											task = usrtaskInfoList
													.get(position - 1);
										} else {
											task = systaskInfoList.get(position
													- 2
													- usrtaskInfoList.size());
										}

										if (task.getPackName().equals(
												getPackageName())) {
											return;
										}
										ViewHolder holder = (ViewHolder) view
												.getTag();
										if (holder.cb.isChecked()) {
											holder.cb.setChecked(false);
											task.setChecked(false);
										} else {
											holder.cb.setChecked(true);
											task.setChecked(true);
										}

									}

								});

						thread = SystemInfoUtils
								.getRunningAppProcesses(TaskManagerActivity.this);
						setMemInfo();

					}

				});

				pd.dismiss();
			}
		}).start();
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

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (sp.getBoolean("showSys", true)) {
				return taskInfoList.size() + 2;
			}
			return usrtaskInfoList.size() + 1;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			Task taskInfo = null;
			if (position == 0) {
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setText(" 用户进程（" + usrtaskInfoList.size() + "个）");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundResource(R.color.statusbar_task_manager);
				tv.setTextSize(18);
				return tv;
			} else if (position == usrtaskInfoList.size() + 1) {
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setText(" 系统进程（" + systaskInfoList.size() + "个）");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundResource(R.color.statusbar_task_manager);
				tv.setTextSize(18);
				return tv;
			} else if (position <= usrtaskInfoList.size()) {
				taskInfo = usrtaskInfoList.get(position - 1);
			} else {
				taskInfo = systaskInfoList.get(position - 2
						- usrtaskInfoList.size());
			}
			ViewHolder holder = new ViewHolder();
			View view;

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(TaskManagerActivity.this,
						R.layout.task_manager_item, null);
				holder.icon = (ImageView) view
						.findViewById(R.id.iv_task_manager_app);
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_task_manager_app_name);
				holder.tv_mem = (TextView) view
						.findViewById(R.id.tv_task_manager_mem);
				holder.cb = (CheckBox) view.findViewById(R.id.cb_task_manager);
				if (taskInfo.getPackName().equals(getPackageName())) {
					holder.cb.setVisibility(View.INVISIBLE);
				} else {
					holder.cb.setVisibility(View.VISIBLE);
				}
				view.setTag(holder);
			}
			if (taskInfo.getIcon() != null)
				holder.icon.setImageDrawable(taskInfo.getIcon());
			if (taskInfo.getAppName() != null)
				holder.tv_app_name.setText(taskInfo.getAppName());
			if (taskInfo.getMem() != 0)
				holder.tv_mem.setText("占用内存："
						+ android.text.format.Formatter.formatFileSize(
								TaskManagerActivity.this, taskInfo.getMem()));

			holder.cb.setChecked(taskInfo.isChecked());
			return view;
		}

	}

	private class ViewHolder {
		private ImageView icon;
		private TextView tv_app_name;
		private TextView tv_mem;
		private CheckBox cb;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_task_manager_select:
			if (btn_task_manager_select.getText().toString().equals("全选")) {
				setAll(true);
				btn_task_manager_select.setText("反选");
			} else if (btn_task_manager_select.getText().toString()
					.equals("反选")) {
				setInvert();
				btn_task_manager_select.setText("全选");
			}
			break;

		case R.id.btn_task_manager_clean:
			killChecked();
			if (btn_task_manager_select.getText().toString().equals("反选")) {
				setInvert();
				btn_task_manager_select.setText("全选");
			}
			break;

		case R.id.btn_task_manager_setting:
			Intent intent = new Intent(this, TaskManagerSettingActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	private void setMemInfo() {
		tv_task_manager_thread.setText("当前进程数：" + thread);
		memInfo = SystemInfoUtils.getMemInfo(TaskManagerActivity.this);
		tv_task_mem_avail.setText("可用/总内存："
				+ android.text.format.Formatter.formatFileSize(
						TaskManagerActivity.this, memInfo.get("availMem"))
				+ "/"
				+ android.text.format.Formatter.formatFileSize(
						TaskManagerActivity.this, memInfo.get("totalMem")));
	}

	private void killChecked() {

		ActivityManager activityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		int count = 0;
		long mem = 0;
		List<Task> killList = new ArrayList<Task>();
		for (Task task : taskInfoList) {
			if (task.isChecked()) {
				activityManager.killBackgroundProcesses(task.getPackName());
				count++;
				mem += task.getMem();
				killList.add(task);
				if (task.isSysTask()) {
					systaskInfoList.remove(task);
				} else if (!task.isSysTask()) {
					usrtaskInfoList.remove(task);
				}
			}
		}
		taskInfoList.removeAll(killList);
		thread -= count;
		adapter.notifyDataSetChanged();
		setMemInfo();
		Toast.makeText(
				TaskManagerActivity.this,
				"清理了"
						+ count
						+ "个进程,释放了"
						+ Formatter.formatFileSize(TaskManagerActivity.this,
								mem) + "内存", 0).show();

	}

	private void setAll(boolean checked) {
		for (Task task : taskInfoList) {
			if (task.getPackName().equals(getPackageName())) {
				continue;
			}
			task.setChecked(checked);
		}
		adapter.notifyDataSetChanged();
	}

	private void setInvert() {
		for (Task task : taskInfoList) {
			if (task.getPackName().equals(getPackageName())) {
				continue;
			}
			task.setChecked(!task.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

}
