package com.light.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	private static List<Sms> list;

	public static void smsBackup(Context context, ProgressDialog dialog) {
		Uri uri = Uri.parse("content://sms/");
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, new String[] { "_id",
				"address", "date", "type", "body" }, null, null, null);
		if (cursor == null || cursor.getCount() <= 0) {
			return;
		}
		System.out.println("cursor.getCount()=" + cursor.getCount());
		int progess = 0;
		int max = cursor.getCount();
		dialog.setMax(max);
		list = new ArrayList<Sms>();
		SmsUtils smsUtils = new SmsUtils();
		Sms sms = smsUtils.new Sms();
		int i = 1;
		while (cursor.moveToNext()) {
			sms = smsUtils.new Sms();
			sms.setaddress(cursor.getLong(1));
			sms.setBody(EmojiFilter.filterEmoji(cursor.getString(4)));
			sms.setDate(cursor.getLong(2));
			sms.setId(i++);
			sms.setType(cursor.getInt(3));
			System.out.println(sms.getaddress() + sms.getBody());
			list.add(sms);
		}
		cursor.close();

		XmlSerializer serializer = Xml.newSerializer();
		File saveFile = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/backup/sms");
		saveFile.mkdirs();
		File file = new File(saveFile, "smsBackup.xml");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			serializer.setOutput(fos, "utf-8");

			serializer.startDocument("utf-8", true);
			serializer.startTag(null, "SMSS");
			serializer.attribute(null, "max", String.valueOf(max));
			for (Sms smss : list) {
				serializer.startTag(null, "sms");
				serializer.attribute(null, "id", String.valueOf(smss.getId()));

				serializer.startTag(null, "type");
				serializer.text(String.valueOf(smss.getType()));
				serializer.endTag(null, "type");

				serializer.startTag(null, "address");
				serializer.text(String.valueOf(smss.getaddress()));
				serializer.endTag(null, "address");

				serializer.startTag(null, "date");
				serializer.text(String.valueOf(smss.getDate()));
				serializer.endTag(null, "date");

				serializer.startTag(null, "body");
				serializer.text(String.valueOf(smss.getBody()));
				serializer.endTag(null, "body");

				serializer.endTag(null, "sms");
				progess++;
				dialog.setProgress(progess);
			}
			serializer.endTag(null, "SMSS");
			serializer.endDocument();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void smsRec(Context context, ProgressDialog dialog,
			boolean delAll) {
		Uri uri = Uri.parse("content://sms/");
		ContentResolver contentResolver = context.getContentResolver();
		if (delAll) {
			contentResolver.delete(uri, null, null);
		}
		XmlPullParser pullParser = Xml.newPullParser();
		File saveFile = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/backup/sms");
		File file = new File(saveFile, "smsBackup.xml");
		try {
			FileInputStream fis = new FileInputStream(file);
			pullParser.setInput(fis, "utf-8");
			int eventType = pullParser.getEventType();
			String address = null, date = null, body = null, type = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = pullParser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if (name.equals("address")) {
						address = pullParser.nextText();
					} else if (name.equals("body")) {
						body = pullParser.nextText();
					} else if (name.equals("date")) {
						date = pullParser.nextText();
					} else if (name.equals("type")) {
						type = pullParser.nextText();
					}
					System.out.println("read:address=" + address + "body="
							+ body);
					break;

				case XmlPullParser.END_TAG:
					if (name.equals("sms")) {
						ContentValues values = new ContentValues();
						values.put("address", address);
						values.put("body", body);
						values.put("date", date);
						values.put("type", type);
						System.out.println("insert:address=" + address
								+ "body=" + body);
						contentResolver.insert(uri, values);
					}
					break;
				}
				eventType = pullParser.next();
			}
			fis.close();
		} catch (Exception e) {
			System.out.println("还原备份出错");
			e.printStackTrace();
		}
	}

	/**
	 * @author wuhaojie inner class for sms item
	 */
	public class Sms {

		private int id, type;
		private long address, date;
		private String body;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public long getaddress() {
			return address;
		}

		public void setaddress(long address) {
			this.address = address;
		}

		public long getDate() {
			return date;
		}

		public void setDate(long date) {
			this.date = date;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		@Override
		public String toString() {
			return "sms [id=" + id + ", type=" + type + ", address=" + address
					+ ", date=" + date + ", body=" + body + "]";
		}

		public Sms() {
		}

	}
}