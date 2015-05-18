package com.light.mobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class AntiVirusActivity extends Activity {

	private PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		scanVirus();
	}

	private void scanVirus() {
		pm = getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		for(PackageInfo info : packages){
			String datadir = info.applicationInfo.dataDir;	// data/data
			String sourcedir =  info.applicationInfo.sourceDir;	//apkÎÄ¼þÂ·¾¶
			String md5 = getMD5(sourcedir);
			System.out.println(info.applicationInfo.loadLabel(pm)+md5);
		}
	}

	private String getMD5(String path) {
		File file = new File(path);
		try {
			MessageDigest digest = MessageDigest.getInstance("sha1");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = fis.read(buffer))!=-1){
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer stringBuffer = new StringBuffer();
			for(byte b : result){
				int num = b & 0xff;
				String str = Integer.toHexString(num);
				if(str.length() == 1){
					stringBuffer.append("0");
				}
				stringBuffer.append(str);
			}
			return stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}



}
