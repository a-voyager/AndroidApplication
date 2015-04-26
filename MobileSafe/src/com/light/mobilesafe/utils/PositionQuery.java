package com.light.mobilesafe.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PositionQuery {

	/**
	 * @param num
	 * @return 返回归属地
	 */
	public String query(String num) {
		String position = num;
		// 获取资源data/data
		String path = "data/data/com.light.mobilesafe/files/address.db";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (num.matches("^1[3458]\\d{9}$")) {
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ?)",
							new String[] { num.substring(0, 7) });
			while (cursor.moveToNext()) {
				position = cursor.getString(0);
			}
			cursor.close();
		} else {
			if(num.length()>10&& num.startsWith("0")){
				//010 12345678
				Cursor cursor = database.rawQuery("select location from data2 where area = ?", 
						new String[]{num.substring(1, 3)});
				if(cursor.moveToNext()){
					position = cursor.getString(0);
				}
				cursor.close();
				
				//0855 12345678
				 cursor = database.rawQuery("select location from data2 where area = ?", 
						new String[]{num.substring(1, 4)});
				if(cursor.moveToNext()){
					position = cursor.getString(0);
				}
				cursor.close();
			} else {
				switch (num.length()) {
				case 3:
					position = "紧急号码";
					break;
				case 5:
					position = "服务电话";
					break;
				case 8:
					position = "本地号码";
					break;
				case 7:
					position = "本地号码";
					break;
				}
			}

		}
		return position;
	}
}
