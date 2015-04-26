package com.light.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


public class BlackListDao {
		private BlackList blackList;
		
		
	public BlackListDao(Context context){
		blackList = new BlackList(context);
	}
	
	public boolean query(String num){
		boolean number = false;
		SQLiteDatabase db = blackList.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacklist where num = ?", new String[]{num});
		if(cursor.moveToNext()){
			number = true;
		}
		cursor.close();
		db.close();
		return number;
	}
	public String queryMode(String num){
		String mode = null;
		SQLiteDatabase db = blackList.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacklist where num = ?", new String[]{num});
		while(cursor.moveToNext()){
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return mode;
	}
	public List<BlacklistNum> queryAll(){
		List<BlacklistNum> result = new ArrayList<BlacklistNum>();
		SQLiteDatabase db = blackList.getReadableDatabase();
		Cursor cursor = db.rawQuery("select num,name,mode from blacklist order by _id desc", null);
		while(cursor.moveToNext()){
			BlacklistNum blacklistNum = new BlacklistNum();
			blacklistNum.setNum(cursor.getString(0));
			blacklistNum.setName(cursor.getString(1));
			blacklistNum.setMode(cursor.getString(2));
			result.add(blacklistNum);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public void add(String num, String name, String mode){
		SQLiteDatabase db = blackList.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("num", num);
		values.put("name", name);
		values.put("mode", mode);
		db.insert("blacklist", null, values);
		db.close();
		System.out.println("²åÈë£º"+num+name+mode);
	}
	
	public void update(String num, String newname, String newmode){
		SQLiteDatabase db = blackList.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		values.put("name", newname);
		db.update("blacklist", values, "num = ?", new String[]{num});
		db.close();
		System.out.println("¸üÐÂ£º"+num+newname+newmode);
	}
	public void delete(String num){
		SQLiteDatabase db = blackList.getWritableDatabase();
		db.delete("blacklist", "num = ?", new String[]{num});
		db.close();
		System.out.println("É¾³ý£º"+num);
	}
	
	
}
