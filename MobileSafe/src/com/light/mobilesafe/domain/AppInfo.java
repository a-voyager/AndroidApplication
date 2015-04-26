package com.light.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
		private Drawable icon;
		private String appName;
		private String packName;
		private boolean inRom;
		private boolean sysApp;
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
		public boolean isInRom() {
			return inRom;
		}
		public void setInRom(boolean inRom) {
			this.inRom = inRom;
		}
		public boolean isSysApp() {
			return sysApp;
		}
		public void setSysApp(boolean sysApp) {
			this.sysApp = sysApp;
		}
		public String getPackName() {
			return packName;
		}
		public void setPackName(String packName) {
			this.packName = packName;
		}
		@Override
		public String toString() {
			return "AppInfo [icon=" + icon + ", appName=" + appName
					+ ", packName=" + packName + ", inRom=" + inRom
					+ ", sysApp=" + sysApp + "]";
		}
		
}
