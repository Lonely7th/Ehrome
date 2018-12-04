package com.daiji.feixiang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BrowserHistoryDb extends MyBaseDb {
    public BrowserHistoryDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * type 1 短信 2 电话
     */
    @Override
    public String getCreateTableSql() {
        return "CREATE TABLE browserhistory (" +
                "_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "url  TEXT," +
                "title  TEXT," +
                "time  TEXT" +
                ");";
    }
}
