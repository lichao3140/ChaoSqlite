package com.lichao.chaosqlite.sqlcore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
        int result = -1;
        //将条件对象转换成Map
        Map values = getValues(entity);
        Condition condition = new Condition(getValues(where));
        result = database.update(tableName, getContentValues(values), condition.whereClause, condition.whereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where,null,null,null);
    }

    @Override
    public List<T> query(T where, String oderBy, Integer startIndex, Integer limit) {
        Map values = getValues(where);
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(values);
        Cursor cursor = null;
        cursor = database.query(tableName, null, condition.getWhereClause(), condition.getWhereArgs(), null, null, oderBy, limitString);
        List<T> result = getResult(cursor, where);
        cursor.close();
        return result;
    }

    @Override
    public int delete(T where) {
        Map values = getValues(where);
        Condition condition = new Condition(values);
        int result = database.delete(tableName, condition.getWhereClause(), condition.getWhereArgs());
        return result;
    }

    @Override
    public int batch(List<T> list) {
        return 0;
    }

    private List<T> getResult(Cursor cursor, T where) {
        ArrayList list = new ArrayList();
        Object item;
        //遍历游标所有数据，每次遍历一条数据，生成一个对象，并且添加值list集合
        while (cursor.moveToNext()) {
            Class<?> clazz = where.getClass();
            try {
                item = clazz.newInstance();
                //列名 name  成员变量名  field
                Iterator iterator = cacheFieldMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = (Map.Entry<String, Field>) iterator.next();
                    //列名
                    String columnName = entry.getKey();
                    Field field = entry.getValue();
                    //通过列名，得到列名在游标的位置
                    Integer columnIndex = cursor.getColumnIndex(columnName);
                    Class type = field.getType();
                    if(columnIndex != -1) {
                        //成员变量类型判断
                        if (type == String.class) {
                            //通过反射方式赋值
                            field.set(item, cursor.getString(columnIndex));
                        } else if (type == Integer.class) {
                            field.set(item, cursor.getInt(columnIndex));
                        } else if(type == Long.class) {
                            field.set(item,cursor.getLong(columnIndex));
                        } else if(type == Double.class) {
                            field.set(item,cursor.getDouble(columnIndex));
                        } else if(type == byte[].class) {
                            field.set(item,cursor.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
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

    /**
     * 封装更新语句
     */
    class Condition {
        //查询条件  类似 name=? && password=?
        private String whereClause;
        private String[] whereArgs;
        public Condition(Map<String, String> map) {
            ArrayList list = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            //条件恒成立, 防止报错
            stringBuilder.append(" 1=1 ");
            Set keys = map.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = map.get(key);
                if (value != null) {
                    //拼接条件查询语句 1=1 and name=? and password=?
                    stringBuilder.append(" and " + key + " =? ");
                    list.add(value);
                }
            }
            this.whereClause = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }

        public String getWhereClause() {
            return whereClause;
        }

        public String[] getWhereArgs() {
            return whereArgs;
        }
    }
}
