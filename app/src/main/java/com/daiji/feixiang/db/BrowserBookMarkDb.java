package com.daiji.feixiang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BrowserBookMarkDb extends MyBaseDb {
    public BrowserBookMarkDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * type 1 短信 2 电话
     */
    @Override
    public String getCreateTableSql() {
        return "CREATE TABLE browserbookmark (" +
                "_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "url  TEXT," +
                "title  TEXT" +
                ");";
    }
}
