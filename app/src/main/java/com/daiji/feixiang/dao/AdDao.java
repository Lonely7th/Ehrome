package com.daiji.feixiang.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.daiji.feixiang.application.MyApplication;
import com.daiji.feixiang.bean.AdBean;
import com.daiji.feixiang.db.AdDb;

import java.util.ArrayList;
import java.util.List;

public class AdDao extends MyBaseDao {
    private final String table = "ad";

    /**
     * 添加
     *
     * @param url
     * @param type
     * @return
     */
    public long add(String url, int type) {
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("type", type);
        long id = writableDatabase.insert(table, null, values);
        return id;
    }

    /**
     * 查询某一个是否存在
     *
     * @param url
     * @param type
     * @return
     */
    public boolean findOne(String url, int type) {
        Cursor cursor = readableDatabase.query(table, new String[]{"_id"}, "url=? and type=?", new String[]{url, type + ""}, null, null, null);
        if (cursor.moveToNext()) {
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * 获取所有
     *
     * @param type
     * @return
     */
    public List<AdBean> getAll(int type) {
        List<AdBean> lists = new ArrayList<>();
        Cursor cursor = readableDatabase.query(table, new String[]{"url", "type", "_id"}, "type=?", new String[]{type + ""}, null, null, null);
        while (cursor.moveToNext()) {
            AdBean bean = new AdBean();
            bean.url = cursor.getString(0);
            bean.type = cursor.getInt(1);
            bean._id = cursor.getInt(2);
            lists.add(bean);
        }
        cursor.close();
        return lists;
    }

    /**
     * 删除
     *
     * @param url
     * @param type
     */
    public void remove(String url, int type) {
        writableDatabase.delete(table, "url=? and type=?", new String[]{url, type + ""});
    }

    @Override
    public AdDb getDb() {
        return new AdDb(MyApplication.getContext(), table, null, 1);
    }

    @Override
    public Class getClazz() {
        return AdBean.class;
    }

    @Override
    public String getTableName() {
        return table;
    }
}
