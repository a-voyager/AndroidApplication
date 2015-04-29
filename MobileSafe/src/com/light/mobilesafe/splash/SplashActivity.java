package com.light.mobilesafe.splash;

import java.io.File;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.light.mobilesafe.MainActivity;
import com.light.mobilesafe.R;
import com.light.mobilesafe.TaskManagerSettingActivity;
import com.light.mobilesafe.service.AutoCleanService;
import com.light.mobilesafe.service.CallIntercept;
import com.light.mobilesafe.service.ShowAddressService;
import com.light.mobilesafe.utils.ServiceUtils;
import com.light.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {

	protected static final int UPDATE = 0;
	protected static final int TOHOME = 1;
	protected static final int FAILCHECK = 2;
	protected static final int DOWNLOADFAIL = 3;
	private TextView tv_splash_version;
	private final String UPDATEURL = "http://10.0.2.2:8080/update.json";
	private String updateText;
	private Handler handler;
	private Message msg = Message.obtain();
	private String apkUrl;
	private TextView tv_download_progerss;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
		tv_download_progerss = (TextView) findViewById(R.id.tv_download_progerss);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本:" + getVersion());

		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		readSettings();
		System.out
				.println("sharedPreferences.getBoolean(\"AutoUpadate\", true)="
						+ sharedPreferences.getBoolean("AutoUpadate", true));
		if (sharedPreferences.getBoolean("AutoUpdate", true)) {
			checkUpdate();
		} else {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					toHome();
				}
			}, 2000);
		}

		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(800);
		findViewById(R.id.splash_layout).startAnimation(animation);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE:
					update();
					break;

				case TOHOME:
					System.out.println("toHome()");
					toHome();
					break;

				case FAILCHECK:
					toast("检查更新失败");
					toHome();
					break;

				case DOWNLOADFAIL:
					toast("下载失败");
				default:
					break;
				}

			}
		};

	}

	/**
	 * 读取设置
	 */
	private void readSettings() {
		// 读取显示号码归属地设置
		if (sharedPreferences.getBoolean("showAddress", false)) {
			boolean isRunning = ServiceUtils.isServiceRunning(
					SplashActivity.this,
					"com.light.mobilesafe.service.ShowAddressService");
			if (!isRunning) {
				System.out.println("服务未运行");
				startService(new Intent(SplashActivity.this,
						ShowAddressService.class));
			} else {
				return;
			}
		}
		// 读取骚扰拦截设置
		if (sharedPreferences.getBoolean("callIntercept", false)) {
			boolean isRunning = ServiceUtils.isServiceRunning(
					SplashActivity.this,
					"com.light.mobilesafe.service.CallIntercept");
			if (!isRunning) {
				System.out.println("骚扰拦截服务未运行");
				startService(new Intent(SplashActivity.this,
						CallIntercept.class));
			} else {
				return;
			}
		}
		// 读取是否第一次打开应用
		if (sharedPreferences.getBoolean("firstOpen", true)) {
			// 添加桌面快捷图标
			if(!hasShortcut(this, getString(R.string.app_name))) {
				createShortCut();
				Toast.makeText(this, "桌面快捷方式创建成功", 0).show();
			}
			// 写入非第一次启动
			Editor editor = sharedPreferences.edit();
			editor.putBoolean("firstOpen", false);
			editor.commit();
		}
		
		//判断是否锁屏自动清理进程
		if(sharedPreferences.getBoolean("autoClean", false)){
			if(!ServiceUtils.isServiceRunning(SplashActivity.this, "com.light.mobilesafe.service.AutoCleanService")){
				System.out.println("锁屏自动清理服务未运行");
				startService(new Intent(SplashActivity.this, AutoCleanService.class));
			}
		}
	}

	private void createShortCut() {
		  // 创建快捷方式的Intent
		  Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		  // 不允许重复创建
		  shortcutintent.putExtra("duplicate", false);
		  // 需要现实的名称
		  shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
		  // 快捷图片
		  Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher);
		  shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		  // 点击快捷图片，运行的程序主入口
		  shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), SplashActivity.class));
		  // 发送广播。OK
		  sendBroadcast(shortcutintent);
		 }
	
	public boolean hasShortcut(Activity activity, String shortcutName) {
		  String url = "";
		  url = "content://" + getAuthorityFromPermission(activity, "com.android.launcher.permission.READ_SETTINGS")            + 

		"/favorites?notify=true";
		  ContentResolver resolver = activity.getContentResolver();
		  Cursor cursor = resolver.query(Uri.parse(url), new String[] { "title", "iconResource" }, "title=?", new String[] { 

		getString(R.string.app_name).trim() }, null);
		  if (cursor != null && cursor.moveToFirst()) {
		   cursor.close();
		   return true;
		  }
		  return false;
		 }
	
	private String getAuthorityFromPermission(Context context, String permission) {
		  if (permission == null)
		   return null;
		  List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
		  if (packs != null) {
		   for (PackageInfo pack : packs) {
		    ProviderInfo[] providers = pack.providers;
		    if (providers != null) {
		     for (ProviderInfo provider : providers) {
		      if (permission.equals(provider.readPermission))
		       return provider.authority;
		      if (permission.equals(provider.writePermission))
		       return provider.authority;
		     }
		    }
		   }
		  }
		  return null;
		 }
	
	private void toast(String string) {
		Toast.makeText(SplashActivity.this, string, Toast.LENGTH_SHORT).show();
	}

	private void update() {
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		builder.setTitle(R.string.splash_update_dailog_title);
		builder.setMessage(updateText);
		builder.setPositiveButton(R.string.splash_update_dailog_update,
				new OnClickListener() {

					private FileNameMap fileNameMap;

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 升级操作
						if (Environment.getExternalStorageState().equals(
								Environment.MEDIA_MOUNTED)) {
							File file = new File(Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/MobileSafe/cache");
							file.mkdirs();
							FinalHttp finalHttp = new FinalHttp();
							finalHttp.download(apkUrl, file.getAbsolutePath()
									+ "/update.apk", new AjaxCallBack<File>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									super.onFailure(t, errorNo, strMsg);
									System.out.println("下载失败" + strMsg
											+ errorNo);
									msg.what = DOWNLOADFAIL;
									handler.sendMessage(msg);
								}

								@Override
								public void onLoading(long count, long current) {
									tv_download_progerss
											.setText("已下载" + (int) current
													* 100 / count + "%");
									System.out.println("已下载" + (int) current
											* 100 / count + "%");
									super.onLoading(count, current);
								}

								@Override
								public void onStart() {
									System.out.println("开始下载");
									super.onStart();
								}

								@Override
								public void onSuccess(File t) {
									tv_download_progerss.setText("下载完成,等待安装");
									System.out.println("下载成功,开始安装");

									// 安装软件
									installApp(t);

									super.onSuccess(t);
								}

								private void installApp(File t) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t),
											"application/vnd.android.package-archive");
									startActivity(intent);
								}

							});
						}

					}
				});
		builder.setNegativeButton(R.string.splash_update_dialog_saynext,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						toHome();
						dialog.dismiss();
					}
				});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				toHome();
				dialog.dismiss();
				System.out.println("toHome();dialog.dismiss();");
			}
		});

		builder.show();
	}

	/**
	 * 进入主界面
	 */
	private void toHome() {
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void checkUpdate() {
		new Thread(new Runnable() {

			private String newVersion;

			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				System.out.println(UPDATEURL);
				try {
					URL url = new URL(UPDATEURL);
					HttpURLConnection connection;

					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(3000);
					connection.setReadTimeout(5000);
					connection.connect();
					if (connection.getResponseCode() == 200) {
						System.out.println("开启链接成功");
						InputStream is = connection.getInputStream();
						String readFromStream = StreamTools.readFromStream(is);
						JSONObject jsonObject = new JSONObject(readFromStream);
						newVersion = jsonObject
								.getString(getString(R.string.web_json_version));
						updateText = jsonObject
								.getString(getString(R.string.web_json_description));
						apkUrl = jsonObject
								.getString(getString(R.string.web_json_apkurl));
						System.out.println("newVersion=" + newVersion
								+ "\nupdateText=" + updateText + "\napkUrl="
								+ apkUrl);
						if (!getVersion().equals(newVersion)) {
							msg.what = UPDATE;
						} else {
							msg.what = TOHOME;
						}
						handler.sendMessage(msg);

					}

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("检查更新失败");
					msg.what = FAILCHECK;
					handler.sendMessage(msg);
				} finally {
					long endTime = System.currentTimeMillis();
					System.out.println("endTime-startTime="
							+ (endTime - startTime));
					if (endTime - startTime < 2000) {
						try {
							Thread.sleep(2000 - (endTime - startTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Thread.sleep错误");
						}
					}
				}

			}

		}).start();

	}

	/**
	 * 获取当前应用版本
	 */
	private String getVersion() {
		PackageManager manager = getPackageManager();
		try {
			PackageInfo packageInfo = manager.getPackageInfo(getPackageName(),
					0);
			System.out.println("版本" + packageInfo.versionName);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}

	}

}
