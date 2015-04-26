package com.light.mobilesafe.ui;

import com.light.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItem extends RelativeLayout {

	private CheckBox cb;
	private TextView tv_title;
	private TextView tv_content;
	private String title;
	private String content_checked;
	private String content_unchecked;

	public SettingItem(Context context) {
		super(context);
		inflatView(context);
	}

	public SettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflatView(context);
		setByAttrs(attrs);
	}

	/**
	 * @param attrs
	 *            通过布局文件设置控件信息
	 */
	private void setByAttrs(AttributeSet attrs) {
		title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.light.mobilesafe",
				"title");
		content_checked = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.light.mobilesafe",
				"content_checked");
		content_unchecked = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.light.mobilesafe",
				"content_unchecked");

	}

	public SettingItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflatView(context);
		setByAttrs(attrs);
	}

	/**
	 * inflat所有控件
	 */
	private void inflatView(Context context) {
		View.inflate(context, R.layout.activity_setting_item, SettingItem.this);
		tv_title = (TextView) this.findViewById(R.id.tv_setting_item_title);
		tv_content = (TextView) this.findViewById(R.id.tv_setting_item_content);
		cb = (CheckBox) this.findViewById(R.id.cb_setting_item);

	}

	public void setTvTitle() {
		tv_title.setText(title);
	}

	public void setCheckedTvContent() {
		tv_content.setText(content_checked);
	}

	public void setUncheckedTvContent() {
		tv_content.setText(content_unchecked);
	}

	/**
	 * 检查是否选中
	 */
	public boolean getCbStatus() {
		return cb.isChecked();
	}

	/**
	 * 设置CheckBox状态
	 */
	public void setCbStatus(boolean status) {
		cb.setChecked(status);
	}

	/**
	 * 设置项目标题
	 */
	public void setTvTitle(String text) {
		tv_title.setText(text);
	}

	/**
	 * 设置项目描述
	 */
	public void setTvContent(String text) {
		tv_content.setText(text);
	}

	/**
	 * @param bool
	 * =============最终==============、
	 * 设置文本，勾选状态
	 */
	public void setSIVchecked(boolean bool){
		setTvTitle();
		setCbStatus(bool);
		if(bool){
			setCheckedTvContent();
		} else {
			setUncheckedTvContent();
		}
	}
	
}
