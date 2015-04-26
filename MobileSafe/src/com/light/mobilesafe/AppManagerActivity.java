package com.light.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.light.mobilesafe.domain.AppInfo;
import com.light.mobilesafe.engin.AppManager;
import com.light.mobilesafe.utils.DensityUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class AppManagerActivity extends Activity implements OnClickListener {

	private TextView tv_rom_avail;
	private TextView tv_sd_avail;
	private ListView lv_app_manager_info;
	private List<AppInfo> appInfos;
	private List<AppInfo> sysAppInfos;
	private List<AppInfo> usrAppInfos;
	private TextView tv_app_manager_lv;
	private PopupWindow popupWindow;
	private Button popup_start;
	private Button popup_share;
	private Button popup_uninstall;
	private AppInfo appInfo;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
            setTranslucentStatus(true);  
        }  
  
        SystemBarTintManager tintManager = new SystemBarTintManager(this);  
        tintManager.setStatusBarTintEnabled(true);  
        tintManager.setStatusBarTintResource(R.color.statusbar_app_manager);
		tv_rom_avail = (TextView) findViewById(R.id.tv_rom_avail);
		tv_sd_avail = (TextView) findViewById(R.id.tv_sd_avail);
		tv_app_manager_lv = (TextView) findViewById(R.id.tv_app_manager_lv);
		lv_app_manager_info = (ListView) findViewById(R.id.lv_app_manager_info);
		tv_rom_avail.setText("内存"
				+ getAvailSize(Environment.getDataDirectory().getPath()));
		tv_sd_avail.setText("SD卡"
				+ getAvailSize(Environment.getExternalStorageDirectory()
						.getPath()));

		sysAppInfos = new ArrayList<AppInfo>();
		usrAppInfos = new ArrayList<AppInfo>();

		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("请稍候...");
		pd.show();

		new Thread(new Runnable() {

			@Override
			public void run() {

				appInfos = AppManager.getAppInfo(AppManagerActivity.this);
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isSysApp()) {
						sysAppInfos.add(appInfo);
					} else {
						usrAppInfos.add(appInfo);
					}
				}

				runOnUiThread(new Runnable() {

					

					@Override
					public void run() {
						tv_app_manager_lv.setText(" 用户程序（" + usrAppInfos.size()
								+ "个）");
						tv_app_manager_lv.setBackgroundResource(R.color.statusbar_app_manager);
						adapter = new MyAdapter();
						lv_app_manager_info.setAdapter(adapter);
						lv_app_manager_info
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
										if (usrAppInfos != null
												&& sysAppInfos != null) {

											if (firstVisibleItem > usrAppInfos
													.size()) {
												tv_app_manager_lv
														.setText(" 系统程序（"
																+ sysAppInfos
																		.size()
																+ "个）");
											} else {
												tv_app_manager_lv
														.setText(" 用户程序（"
																+ usrAppInfos
																		.size()
																+ "个）");
											}
										}

										if (popupWindow != null
												&& popupWindow.isShowing()) {
											popupWindow.dismiss();
											popupWindow = null;
										}

									}
								});
						lv_app_manager_info
								.setOnItemClickListener(new OnItemClickListener() {

									

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										appInfo = null;
										if (position == 0
												|| position == usrAppInfos
														.size() + 1) {
											return;
										} else if (position < usrAppInfos
												.size() + 1) {
											appInfo = usrAppInfos
													.get(position - 1);
										} else {
											appInfo = sysAppInfos.get(position
													- usrAppInfos.size() - 2);
										}
										System.out.println(appInfo
												.getPackName());

										int location[] = new int[2];
										view.getLocationOnScreen(location);

										// TextView textView = new
										// TextView(getApplicationContext());
										// textView.setText(appInfo.getPackName());

										View contentView = null;
										contentView = View
												.inflate(
														AppManagerActivity.this,
														R.layout.app_manager_popup_window,
														null);

										popup_start = (Button) contentView
												.findViewById(R.id.popup_start);
										popup_share = (Button) contentView
												.findViewById(R.id.popup_share);
										popup_uninstall = (Button) contentView
												.findViewById(R.id.popup_uninstall);

										popup_start
												.setOnClickListener(AppManagerActivity.this);
										popup_share
												.setOnClickListener(AppManagerActivity.this);
										popup_uninstall
												.setOnClickListener(AppManagerActivity.this);

										if (popupWindow != null
												&& popupWindow.isShowing()) {
											popupWindow.dismiss();
											popupWindow = null;
										}

										popupWindow = new PopupWindow(
												contentView,
												ViewGroup.LayoutParams.WRAP_CONTENT,
												ViewGroup.LayoutParams.WRAP_CONTENT);
										popupWindow
												.setBackgroundDrawable(new ColorDrawable(
														Color.TRANSPARENT));
										ScaleAnimation sa = new ScaleAnimation(
												0.3f, 1.0f, 0.3f, 1.0f,
												Animation.RELATIVE_TO_SELF, 0,
												Animation.RELATIVE_TO_SELF,
												0.5f);
										sa.setDuration(300);
										AlphaAnimation aa = new AlphaAnimation(
												0.3f, 1f);
										aa.setDuration(300);
										AnimationSet set = new AnimationSet(
												false);
										set.addAnimation(aa);
										set.addAnimation(sa);
										contentView.setAnimation(set);

										popupWindow
												.showAtLocation(
														parent,
														Gravity.LEFT
																| Gravity.TOP,
														DensityUtil
																.dip2px(AppManagerActivity.this,
																		50),
														location[1]
																- DensityUtil
																		.dip2px(AppManagerActivity.this,
																				5));

									}
								});
						pd.dismiss();
					}
				});
			}
		}).start();
	}

	private String getAvailSize(String path) {
		StatFs statFs = new StatFs(path);
		long availableBlocksLong = statFs.getAvailableBlocks();
		long blockSizeLong = statFs.getBlockSize();
		long blockCountLong = statFs.getBlockCount();
		return "可用"
				+ Formatter.formatFileSize(AppManagerActivity.this,
						(blockSizeLong * blockCountLong)) + "(" + 100
				* availableBlocksLong / blockCountLong + "%)";
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return appInfos.size() + 2;
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
			AppInfo appInfo;
			View view;
			ViewHolder holder;
			if (position == 0) {
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText(" 用户程序（" + usrAppInfos.size() + "个）");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundResource(R.color.statusbar_app_manager);
				tv.setTextSize(18);
				return tv;
			} else if (position < usrAppInfos.size() + 1) {
				appInfo = usrAppInfos.get(position - 1);
			} else if (position == usrAppInfos.size() + 1) {
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText(" 系统程序（" + sysAppInfos.size() + "个）");
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(18);
				tv.setBackgroundResource(R.color.statusbar_app_manager);
				return tv;
			} else {
				appInfo = sysAppInfos.get(position - usrAppInfos.size() - 2);
			}

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(AppManagerActivity.this,
						R.layout.app_manager_item, null);
				holder = new ViewHolder();
				holder.tv_appName = (TextView) view
						.findViewById(R.id.tv_app_manager_app_name);
				holder.tv_appLocation = (TextView) view
						.findViewById(R.id.tv_app_manager_app_location);
				holder.iv_appIcon = (ImageView) view
						.findViewById(R.id.iv_app_manager_app);
				view.setTag(holder);
			}
			holder.tv_appName.setText(appInfo.getAppName());
			holder.tv_appLocation.setText(appInfo.isInRom() ? "内部储存" : "外部储存");
			holder.iv_appIcon.setImageDrawable(appInfo.getIcon());
			return view;
		}

	}

	private class ViewHolder {
		private TextView tv_appName;
		private TextView tv_appLocation;
		private ImageView iv_appIcon;
	}

	@Override
	protected void onDestroy() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_start:
			PackageManager packageManager = this.getPackageManager();
			Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(appInfo.getPackName());
			if(launchIntentForPackage == null){
				Toast.makeText(this, "当前应用不可被启动", 0).show();
			} else {
			startActivity(launchIntentForPackage);
			}
			break;
		case R.id.popup_share:
			Intent intent = new Intent();
			intent.setAction("android.intent.action.SEND");
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT,"亲，这款应用("+appInfo.getAppName()+")很好用，推荐你使用哦");
			startActivity(intent);
			break;
		case R.id.popup_uninstall:
			if(appInfo.getPackName().equals(getPackageName())){
				Toast.makeText(AppManagerActivity.this, "不能卸载自身应用", 0).show();
				return;
			}
			if(appInfo.isSysApp()){
				Toast.makeText(AppManagerActivity.this, "不能卸载系统应用", 0).show();
				return;
			}
			Intent intent2 = new Intent();
//			action android:name="android.intent.action.VIEW" />
//            <action android:name="android.intent.action.DELETE" />
//            <category android:name="android.intent.category.DEFAULT" />
//            <data android:scheme="package" />
			
			intent2.setAction("android.intent.action.VIEW");
			intent2.setAction("android.intent.action.DELETE");
			intent2.addCategory("android.intent.category.DEFAULT");
			intent2.setData(Uri.parse("package:"+appInfo.getPackName()));
			startActivityForResult(intent2, 0);
			break;

		}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("请稍候...");
		pd.show();
		sysAppInfos.removeAll(sysAppInfos);
		usrAppInfos.removeAll(usrAppInfos);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				appInfos = AppManager.getAppInfo(AppManagerActivity.this);
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isSysApp()) {
						sysAppInfos.add(appInfo);
					} else {
						usrAppInfos.add(appInfo);
					}
				}			
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
							pd.dismiss();
						}
					});
			
			}
		}).start();
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
