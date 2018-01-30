package com.lichao.chaosqlite;

import com.lichao.chaosqlite.sqlcore.BaseDao;

/**
 * Created by Administrator on 2018-01-30.
 */

public class UserDao extends BaseDao {

    @Override
    public String createTable() {
        return "create table if not exists tb_user(name varchar(20),password varchar(10),age int )";
    }
}
