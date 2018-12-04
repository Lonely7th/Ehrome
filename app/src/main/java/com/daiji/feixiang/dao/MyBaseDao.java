package com.daiji.feixiang.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.daiji.feixiang.db.MyBaseDb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class MyBaseDao {
    private String mTable;
    protected String pkName = "_id";
    protected SQLiteDatabase writableDatabase;
    protected SQLiteDatabase readableDatabase;
    private Class clazz;

    public MyBaseDao() {
        //获取表名
        mTable = getTableName();
        MyBaseDb db = getDb();
        writableDatabase = db.getWritableDatabase();
        readableDatabase = db.getReadableDatabase();
        clazz = getClazz();
        //获取主键
        String pk = getPkName();
        if (pk != null) {
            pkName = pk;
        }
    }

    /**
     * 添加数据
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public long add(Object obj) throws IllegalAccessException {
        Field[] declaredFields = clazz.getDeclaredFields();
        int len = declaredFields.length;
        ContentValues values = new ContentValues();
        for (int i = 0; i < len; i++) {
            Field declaredField = declaredFields[i];
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            String name = declaredField.getName();
            String simpleName = type.getSimpleName();
            if (declaredField.get(obj) == null) {
                continue;
            }

            if (name.equals(pkName)) {
                //主键过滤
                continue;
            }
            if (simpleName.contains("String")) {
                //String
                values.put(name, declaredField.get(obj) + "");
            } else if (simpleName.contains("int")) {
                //int
                values.put(name, declaredField.getInt(obj));
            } else {
                //其它类型,暂未处理

            }
        }
        long id = writableDatabase.insert(mTable, null, values);
        return id;
    }

    /**
     * 删除某条数据
     *
     * @param id 主键id
     */
    public void delete(int id) {
        writableDatabase.delete(mTable, pkName + "=?", new String[]{id + ""});
    }

    /**
     * 删除所有
     */
    public void removeAll() {
        writableDatabase.delete(mTable, null, null);
    }

    /**
     * 获取所有
     *
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List getAll() throws InstantiationException, IllegalAccessException {
        List lists = new ArrayList();
        Field[] declaredFields = clazz.getDeclaredFields();
        int len = declaredFields.length;
        Cursor cursor = readableDatabase.query(mTable, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Object o = clazz.newInstance();
            for (int i = 0; i < len; i++) {
                Field declaredField = declaredFields[i];
                declaredField.setAccessible(true);
                Class<?> type = declaredField.getType();
                String name = declaredField.getName();
                int columnIndex = cursor.getColumnIndex(name);
                if (columnIndex < 0) {
                    continue;
                }
                String simpleName = type.getSimpleName();
                if (simpleName.contains("String")) {
                    //String
                    declaredField.set(o, cursor.getString(columnIndex));
                } else if (simpleName.contains("int")) {
                    //int
                    declaredField.setInt(o, cursor.getInt(columnIndex));
                } else {
                    //其它类型,暂未处理

                }

            }
            lists.add(o);
        }
        cursor.close();
        return lists;
    }


    /**
     * 分页查询
     *
     * @param page 第几页
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List page(int page) throws InstantiationException, IllegalAccessException {
        return page(page, 20);
    }

    /**
     * 分页查询
     *
     * @param page 第几页
     * @param size 每页多少条
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List page(int page, int size) throws InstantiationException, IllegalAccessException {
        List lists = new ArrayList();
        Field[] declaredFields = clazz.getDeclaredFields();
        int len = declaredFields.length;

        int start = (page - 1) * size;
        String sql = "select * from " + mTable + " order by " + pkName + " desc limit " + start + "," + size;
        Cursor cursor = readableDatabase.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            Object o = clazz.newInstance();
            for (int i = 0; i < len; i++) {
                Field declaredField = declaredFields[i];
                declaredField.setAccessible(true);
                Class<?> type = declaredField.getType();
                String name = declaredField.getName();
                int columnIndex = cursor.getColumnIndex(name);
                if (columnIndex < 0) {
                    continue;
                }
                String simpleName = type.getSimpleName();
                if (simpleName.contains("String")) {
                    //String
                    declaredField.set(o, cursor.getString(columnIndex));
                } else if (simpleName.contains("int")) {
                    //int
                    declaredField.setInt(o, cursor.getInt(columnIndex));
                } else {
                    //其它类型,暂未处理

                }

            }
            lists.add(o);
        }
        cursor.close();
        return lists;
    }

    /**
     * 获取单条数据
     *
     * @param id 主键id
     * @return
     */
    public Object getOne(int id) throws InstantiationException, IllegalAccessException {
        Field[] declaredFields = clazz.getDeclaredFields();
        int len = declaredFields.length;
        Cursor cursor = readableDatabase.query(mTable, null, pkName + "=?", new String[]{id + ""}, null, null, null);
        Object o = null;
        while (cursor.moveToNext()) {
            o = clazz.newInstance();
            for (int i = 0; i < len; i++) {
                Field declaredField = declaredFields[i];
                declaredField.setAccessible(true);
                Class<?> type = declaredField.getType();
                String name = declaredField.getName();
                int columnIndex = cursor.getColumnIndex(name);
                if (columnIndex < 0) {
                    continue;
                }
                String simpleName = type.getSimpleName();
                if (simpleName.contains("String")) {
                    //String
                    declaredField.set(o, cursor.getString(columnIndex));
                } else if (simpleName.contains("int")) {
                    //int
                    declaredField.setInt(o, cursor.getInt(columnIndex));
                } else {
                    //其它类型,暂未处理

                }

            }
        }
        cursor.close();
        return o;
    }

    /**
     * 更新数据
     *
     * @param id  主键id
     * @param obj
     * @return
     */
    public int edit(int id, Object obj) throws IllegalAccessException {
        Field[] declaredFields = clazz.getDeclaredFields();
        int len = declaredFields.length;
        ContentValues values = new ContentValues();
        for (int i = 0; i < len; i++) {
            Field declaredField = declaredFields[i];
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            String name = declaredField.getName();
            String simpleName = type.getSimpleName();
            if (declaredField.get(obj) == null) {
                continue;
            }
            if (name.equals(pkName)) {
                //主键过滤
                continue;
            }
            if (simpleName.contains("String")) {
                //String
                values.put(name, declaredField.get(obj) + "");
            } else if (simpleName.contains("int")) {
                //int
                values.put(name, declaredField.getInt(obj));
            } else {
                //其它类型,暂未处理

            }
        }
        return writableDatabase.update(mTable, values, pkName + "=?", new String[]{id + ""});
    }

    /**
     * 返回db对象
     */
    public abstract MyBaseDb getDb();

    /**
     * 返回关联的bean
     *
     * @return
     */
    public abstract Class getClazz();

    /**
     * 返回关联的表名
     *
     * @return
     */
    public abstract String getTableName();

    /**
     * 获取主键
     *
     * @return
     */
    public String getPkName() {
        return null;
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (writableDatabase != null) {
            writableDatabase.close();
        }
        if (readableDatabase != null) {
            readableDatabase.close();
        }
    }
}
