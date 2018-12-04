package com.daiji.feixiang.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.daiji.feixiang.application.MyApplication;
import com.daiji.feixiang.bean.BrowserHistoryBean;
import com.daiji.feixiang.db.BrowserHistoryDb;

public class BrowserHistoryDao extends MyBaseDao {
    private final String table = "browserhistory";

    /**
     * 添加
     *
     * @param url
     * @param title
     * @return
     */
    public long add(String url, String title) {
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("title", title);
        values.put("time", System.currentTimeMillis());
        long id = writableDatabase.insert(table, null, values);
        return id;
    }

    @Override
    public BrowserHistoryDb getDb() {
        return new BrowserHistoryDb(MyApplication.getContext(), table, null, 1);
    }

    @Override
    public Class getClazz() {
        return BrowserHistoryBean.class;
    }

    @Override
    public String getTableName() {
        return table;
    }

    /**
     * 删除最后一条
     */
    public void deleteLastHistory() {
        int lastId = getLastId();
        if (lastId > 0) {
            writableDatabase.execSQL("delete from " + table + " where " + pkName + "=" + lastId);
        }
    }

    /**
     * 返回最后的id
     *
     * @return
     */
    public int getLastId() {
        Cursor cursor = readableDatabase.rawQuery("select " + pkName + " from " + table + " order by " + pkName + " desc limit 1", null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }
        return 0;
    }

    /**
     * 返回上一页url
     *
     * @return
     */
    public String getPreHistoryUrl() {
        deleteLastHistory();
        Cursor cursor = readableDatabase.rawQuery("select url from " + table + " order by " + pkName + " desc limit 1", null);
        if (cursor.moveToNext()) {
            String url = cursor.getString(0);
            cursor.close();
            return url;
        }
        return null;
    }
}
