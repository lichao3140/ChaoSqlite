package com.lichao.chaosqlite;

import com.lichao.chaosqlite.sqlcore.BaseDao;

/**
 * Created by Administrator on 2018-01-31.
 */

public class FileDao extends BaseDao {

    @Override
    public String createTable() {
        return "create table if not exists tb_file(time varchar(20),path varchar(10),description varchar(20))";
    }
}
