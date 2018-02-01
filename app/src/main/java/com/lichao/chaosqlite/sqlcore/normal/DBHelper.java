package com.lichao.chaosqlite.sqlcore.normal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018-02-01.
 */

public class DBHelper extends SQLiteOpenHelper {

    /** 版本号 */
    private static final int DB_VERSION = 1;

    /** 数据库名称 */
    private static final String DB_NAME = "schoolhelper.db";

    /** 建立user表SQL */
    public static final String CREATE_TABLE_USER = "create table if not exists wifi(id INTEGER PRIMARY KEY AUTOINCREMENT,username varchar(20),password varchar(20),idcard varchar(20),type int,phone varchar(15),userId varchar(50))";

    /** 建立course表SQL */
    public static final String CREATE_TABLE_COURSE = "create  table if not exists  course(id INTEGER PRIMARY KEY AUTOINCREMENT,firstTime varvhar(20),twoTime  varchar(100),mondayCourse varchar(100),tuesdayCourse varchar(100),wednesdayCourse varchar(100),thursdayCourse varchar(100),fridayCourse varchar(100),saturdayCourse varchar(100),sundayCourse varchar(100),courseId int,mondayBg int,tuesdayBg int,wednesdayBg int,thursdayBg int,fridayBg int,saturdayBg int,sundayBg int)";

    public static DBHelper mInstance = null;

    public DBHelper(Context context) throws SQLiteException {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 单例模式
     * @param context
     * @return
     */
    public static synchronized DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_COURSE);
        // 初始化课程数据
        init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // 更新操作
        db.execSQL("DROP TABLE IF EXISTS COURSE");
        onCreate(db);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    public void init(SQLiteDatabase db) {

    }
}
