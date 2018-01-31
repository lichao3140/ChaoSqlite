package com.lichao.chaosqlite;

import com.lichao.chaosqlite.sqlcore.DbTable;

/**
 * Created by Administrator on 2018-01-31.
 */

@DbTable("tb_file")
public class FileBean {
    public String time;
    public String path;
    public String description;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
