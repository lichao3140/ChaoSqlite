package com.lichao.chaosqlite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lichao.chaosqlite.sqlcore.DaoManagerFactory;
import com.lichao.chaosqlite.sqlcore.IBaseDao;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "lichao";
    IBaseDao<User> baseDao ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDao= DaoManagerFactory.getInstanse().getDataHelper(UserDao.class,User.class);
    }

    public void insert(View view) {
        User user = new User();
        user.setName("李四");
        user.setPassword("123456");
        baseDao.insert(user);
        Log.e(TAG, "insert");
    }
}
