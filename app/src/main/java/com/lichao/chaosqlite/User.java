package com.lichao.chaosqlite;

import com.lichao.chaosqlite.sqlcore.DbTable;

/**
 * Created by Administrator on 2018-01-30.
 */

@DbTable("tb_user")
public class User {
    public String name;

    public String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
