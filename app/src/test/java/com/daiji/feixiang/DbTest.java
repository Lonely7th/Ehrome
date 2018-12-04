package com.daiji.feixiang;

import com.daiji.feixiang.application.MyApplication;
import com.daiji.feixiang.bean.AdBean;
import com.daiji.feixiang.dao.MyBaseDao;
import com.daiji.feixiang.db.AdDb;
import com.daiji.feixiang.db.MyBaseDb;

import org.junit.Test;

import java.lang.reflect.Field;

public class DbTest {

    @Test
    public void test() throws InstantiationException, IllegalAccessException {
        AdBean bean = new AdBean();
        bean.url = "22222222222220";
        bean.type = 22;

        Class clazz = AdBean.class;
        Field[] declaredFields = clazz.getDeclaredFields();
        int len = declaredFields.length;

        Object o = clazz.newInstance();
        for (int i = 0; i < len; i++) {
            Field declaredField = declaredFields[i];
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            System.out.println(type);
            System.out.println(declaredField.getName());

            System.out.println(declaredField.get(bean));
            if (type.getSimpleName().contains("int")) {
                declaredField.set(bean, 555);
            }

        }

        System.out.println(bean._id);
    }

    @Test
    public void test2() {

    }

}
