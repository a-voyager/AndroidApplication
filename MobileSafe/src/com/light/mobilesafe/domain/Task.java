package com.light.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class Task {
	private Drawable icon;
	private String appName;
	private String packName;
	private long mem;
	private boolean sysTask;
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public long getMem() {
		return mem;
	}
	public void setMem(long mem) {
		this.mem = mem;
	}
	public boolean isSysTask() {
		return sysTask;
	}
	public void setSysTask(boolean sysTask) {
		this.sysTask = sysTask;
	}
	@Override
	public String toString() {
		return "Task [icon=" + icon + ", appName=" + appName + ", packName="
				+ packName + ", mem=" + mem + ", sysTask=" + sysTask
				+ ", checked=" + checked + "]";
	}
	
}
