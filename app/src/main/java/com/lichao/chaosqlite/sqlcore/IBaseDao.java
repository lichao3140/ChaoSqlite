package com.lichao.chaosqlite.sqlcore;

import java.util.List;

/**
 * Created by Administrator on 2018-01-30.
 */

public interface IBaseDao<T> {
    /**
     * 插入数据
     * @param entity
     * @return
     */
    Long insert(T entity);

    /**
     * 更新数据
     * @param entity
     * @param where
     * @return
     */
    int update(T entity,T where);

    /**
     * 查询所有数据
     * @param where
     * @return
     */
    List<T> query(T where);

    /**
     * 条件查询
     * @param where
     * @param oderBy
     * @param startIndex
     * @param limit
     * @return
     */
    List<T> query(T where,String oderBy,Integer startIndex,Integer limit);

    /**
     *
     * @param where
     * @return
     */
    int delete(T where);

    /**
     *
     * @param list
     * @return
     */
    int batch(List<T> list);
}
