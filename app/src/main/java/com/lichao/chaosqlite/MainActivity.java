package com.lichao.chaosqlite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lichao.chaosqlite.sqlcore.DaoManagerFactory;
import com.lichao.chaosqlite.sqlcore.IBaseDao;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "lichao";
    IBaseDao<User> baseDao ;
    IBaseDao<FileBean> fileDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDao = DaoManagerFactory.getInstanse().getDataHelper(UserDao.class, User.class);
        fileDao = DaoManagerFactory.getInstanse().getDataHelper(FileDao.class, FileBean.class);
    }

    public void insert(View view) {
        //扩展
//        FileBean fileBean = new FileBean();
//        fileBean.setTime("2018-1-01-31");
//        fileBean.setPath("data/data/path");
//        fileBean.setDescription("插入文件操作记录");
//        fileDao.insert(fileBean);
        User user = new User();
        user.setName("李四");
        user.setPassword("123456");
        baseDao.insert(user);
        Log.i(TAG, "insert");
    }

    /**
     * 自定义框架插入1000条数据
     */
    public void insert1000(View view) {
        User user = new User();
        user.setName("李四");
        user.setPassword("123456");
        long start = System.currentTimeMillis();
        Log.i(TAG, "-------------自定义框架框架----------");
        for(int i = 0; i < 1000; i++) {
            baseDao.insert(user);
        }
        Log.i(TAG, "耗时:  " + (System.currentTimeMillis() - start) + "  ms");
    }

    /**
     * ormlite框架插入1000条数据
     * @param view
     */
    public void ormliteinsert1000(View view) {
        com.lichao.chaosqlite.sqlcore.otlitem.UserDao userDao = new com.lichao.chaosqlite.sqlcore.otlitem.UserDao(this);
        com.lichao.chaosqlite.sqlcore.otlitem.User user = new com.lichao.chaosqlite.sqlcore.otlitem.User();
        user.setName("李四");
        user.setPassword("123456");
        long start = System.currentTimeMillis();
        Log.i(TAG,"-------------ortim框架----------");
        for(int i = 0; i < 1000; i++) {
            userDao.add(user);
        }
        Log.i(TAG, "耗时:  " + (System.currentTimeMillis() - start) + "  ms");
    }

    /**
     * 更新数据
     * @param view
     */
    public void update(View view) {
        User user = new User();
        user.setName("Sindy");
        user.setPassword("123456789");
        User where = new User();
        where.setName("李四");
        baseDao.update(user, where);
        Log.i(TAG, "update");
    }

    public void delete(View view) {
        User where=new User();
        where.setName("Sindy");
        int result = baseDao.delete(where);
        Log.i(TAG, "共删除  "+ result + "  条数据");
    }

    public void query(View view) {
        User where = new User();
        where.setName("李四");
        List<User> list = baseDao.query(where);
        Log.i(TAG, "共查询到  " + list.size() + "  条数据");
        for (User user : list) {
            Log.i(TAG, user.toString());
        }
    }
}
