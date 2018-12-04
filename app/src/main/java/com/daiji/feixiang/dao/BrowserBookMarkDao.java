package com.daiji.feixiang.dao;

import android.content.ContentValues;

import com.daiji.feixiang.application.MyApplication;
import com.daiji.feixiang.bean.BrowserBookMarkBean;
import com.daiji.feixiang.db.BrowserBookMarkDb;

public class BrowserBookMarkDao extends MyBaseDao {
    private final String table = "browserbookmark";

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
        long id = writableDatabase.insert(table, null, values);
        return id;
    }

    /**
     * 编辑
     *
     * @param _id
     * @param url
     * @param title
     */
    public void edit(int _id, String url, String title) {
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("title", title);
        writableDatabase.update(table, values, "_id=?", new String[]{_id + ""});
    }

    @Override
    public BrowserBookMarkDb getDb() {
        return new BrowserBookMarkDb(MyApplication.getContext(), table, null, 1);
    }

    @Override
    public Class getClazz() {
        return BrowserBookMarkBean.class;
    }

    @Override
    public String getTableName() {
        return table;
    }
}
