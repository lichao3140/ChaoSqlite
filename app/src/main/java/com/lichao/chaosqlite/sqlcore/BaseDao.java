package com.lichao.chaosqlite.sqlcore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018-01-30.
 */

public abstract class BaseDao<T> implements IBaseDao<T> {
    private SQLiteDatabase database;
    private boolean isInit = false;
    private String tableName;
    private Class<T> entityClass;
    private Map<String, Field> cacheFieldMap;

    public synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (!isInit) {
            this.database = sqLiteDatabase;
            this.tableName = entity.getAnnotation(DbTable.class).value();
            this.entityClass = entity;
            if (!database.isOpen()) {
                return false;
            }
            if (!TextUtils.isEmpty(createTable())) {
                database.execSQL(createTable());
            }
            cacheFieldMap = new HashMap<>();
            initCacheMap();
            isInit = true;
        }
        return true;
    }

    private void initCacheMap() {
        String sql = "select * from " + this.tableName + " limit 1,0";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            //表的类名数组
            String[] columnNames = cursor.getColumnNames();
            //拿到Filed数组
            Field[] columnFields = entityClass.getFields();
            for (Field field : columnFields) {
                field.setAccessible(true);
            }
            //开始找对应关系
            for (String columnName : columnNames) {
                //如果找到对应的Field就赋值给它
                Field columnToFiled = null;
                for (Field field : columnFields) {
                    String fieldName = null;
                    //通过注解判断
                    if (field.getAnnotation(DbField.class) != null) {
                        fieldName = field.getAnnotation(DbField.class).value();
                    } else {
                        fieldName = field.getName();
                    }
                    //找到了映射关系
                    //如果表的列名  等于  成员变量注解名字
                    if (columnName.equals(fieldName)) {
                        columnToFiled = field;
                        break;
                    }
                }
                //找到了对应关系
                if (columnToFiled != null) {
                    cacheFieldMap.put(columnName, columnToFiled);
                }
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
    }

    public abstract String createTable();

    @Override
    public Long insert(T entity) {
        //将对象转化为Map集合
        Map<String, String> map = getValues(entity);
        ContentValues contentValues = getContentValues(map);
        Long result = database.insert(tableName,null, contentValues);
        return result;
    }

    @Override
    public int update(T entity, T where) {
        return 0;
    }

    @Override
    public List<T> query(T where) {
        return null;
    }

    @Override
    public List<T> query(T where, String oderBy, Integer startIndex, Integer limit) {
        return null;
    }

    @Override
    public int delete(T where) {
        return 0;
    }

    @Override
    public int batch(List<T> list) {
        return 0;
    }

    /**
     * 将Map转换成ContentValue
     * @param map
     * @return
     */
    private ContentValues getContentValues(Map<String,String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = map.get(key);
            if(value != null) {
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }

    /**
     * 将对象转化为Map集合
     * @param entity
     * @return
     */
    private Map<String, String> getValues(T entity) {
        HashMap<String,String> result=new HashMap<>();
        Iterator fieldsIterator = cacheFieldMap.values().iterator();
        //循环遍历 映射Map的Filed
        while (fieldsIterator.hasNext()) {
            Field columnToFiled = (Field) fieldsIterator.next();
            String cacheKey = null;
            String cacheValue = null;
            if (columnToFiled.getAnnotation(DbField.class) != null) {
                cacheKey = columnToFiled.getAnnotation(DbField.class).value();
            } else {
                cacheKey = columnToFiled.getName();
            }
            try {
                if (null == columnToFiled.get(entity)) {
                    continue;
                }
                cacheValue = columnToFiled.get(entity).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result.put(cacheKey, cacheValue);
        }
        return result;
    }
}
