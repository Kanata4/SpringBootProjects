package com.geekaca.news.utils;

public class PageBean {
    private Integer pageNo;
    private Integer pageSize;
    private Integer newsId;
    private Integer commentStatus;
    private Integer comment_id;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}