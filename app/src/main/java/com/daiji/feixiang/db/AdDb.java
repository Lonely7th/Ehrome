package com.daiji.feixiang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AdDb extends MyBaseDb {

    public AdDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public String getCreateTableSql() {
        return "CREATE TABLE ad (" +
                "_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "url  TEXT," +
                "type  INTEGER" +
                ");";
    }
}
