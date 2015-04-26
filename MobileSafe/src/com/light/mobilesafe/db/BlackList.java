package com.light.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackList extends SQLiteOpenHelper {

	public BlackList(Context context) {
		super(context, "BlackList.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blacklist(_id integer primary key,name varchar(20),num varchar(20), mode varchar(2));");
		System.out.println("黑名单数据库已创建");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
