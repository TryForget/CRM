package com.shsxt.crm.base;


/**
 * 通用的查询类
 */
public class BaseQuery {
    // 分页参数的字段名必须设置为page和limit
    private Integer page = 1;
    private Integer limit = 10;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
