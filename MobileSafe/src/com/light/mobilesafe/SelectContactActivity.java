package com.light.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {

	private ListView lv_contacts;
	private Cursor cursorData;
	private List<Map<String, String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lv_contacts = (ListView) findViewById(R.id.lv_contacts);

		final ProgressDialog pd = new ProgressDialog(SelectContactActivity.this);
		pd.setMessage("请稍等...");
		pd.show();
		List<Map<String, String>> contact_list;
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<Map<String, String>> contact_list = getContacts();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						lv_contacts.setAdapter(new SimpleAdapter(
								SelectContactActivity.this, contact_list,
								R.layout.contact_item, new String[] { "name",
										"phone" }, new int[] {
										R.id.tv_contact_name,
										R.id.tv_contact_num }));
						pd.dismiss();
					}
				});

			}
		}).start();
		lv_contacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = list.get(position).get("phone");
				if (phone.isEmpty()) {
					return;
				}
				phone = phone.replaceAll("-", "").trim();
				phone = phone.replaceAll(" ", "");
				System.out.println("选中的号码：" + phone);
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				finish();
			}
		});
	}

	private List<Map<String, String>> getContacts() {
		list = new ArrayList<Map<String, String>>();
		ContentResolver contentResolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uriData = Uri.parse("content://com.android.contacts/data");

		Cursor cursor = contentResolver.query(uri,
				new String[] { "contact_id" }, null, null, null);

		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			System.out.println("id=" + id);
			if (id == null) {
				continue;
			}
			Map<String, String> map = new HashMap<String, String>();

			cursorData = contentResolver
					.query(uriData, new String[] { "data1", "mimetype" },
							"raw_contact_id=?", new String[] { id }, null);

			while (cursorData.moveToNext()) {
				String data1 = cursorData.getString(0);
				String mimetype = cursorData.getString(1);
				if (data1 == null) {
					continue;
				}
				if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
					map.put("phone", data1);
					System.out.println("phone=" + data1);
				} else if ("vnd.android.cursor.item/name".equals(mimetype)) {
					map.put("name", data1);
					System.out.println("name" + data1);
				}
			}
			cursorData.close();

			list.add(map);
		}
		cursor.close();

		return list;

	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent();
		intent.putExtra("phone", "");
		setResult(0, intent);
		finish();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("phone", "");
		setResult(0, intent);
		finish();
		super.onBackPressed();
	}
}
