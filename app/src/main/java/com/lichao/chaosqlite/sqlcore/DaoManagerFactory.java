package com.lichao.chaosqlite.sqlcore;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018-01-30.
 */

public class DaoManagerFactory {
    /**
     * 数据库路径
     */
    private String sqliteDataPath;

    private SQLiteDatabase sqLiteDatabase;

    public static DaoManagerFactory instanse = new DaoManagerFactory(
            new File(Environment.getExternalStorageDirectory(), "user.db"));

    public static DaoManagerFactory getInstanse() {
        return instanse;
    }

    private DaoManagerFactory(File file) {
        this.sqliteDataPath = file.getAbsolutePath();
        openDatabase();
    }

    public synchronized <T extends BaseDao<M>, M> T getDataHelper(Class<T> clazz, Class<M> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = clazz.newInstance();
            baseDao.init(entityClass, sqLiteDatabase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

    private void openDatabase() {
        this.sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDataPath, null);
    }
}
