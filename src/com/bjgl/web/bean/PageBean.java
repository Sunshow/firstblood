package com.bjgl.web.bean;

import java.io.Serializable;

public class PageBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private int count = 0; // 记录总数
    private int pageSize = 20; // 每页显示记录数
    private int pageCount = 0; // 总页数
    private int page = 1; // 当前页数
    private boolean pagging = true;//是否分页 ,默认为true分页 ,等于false时不分页

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isPagging() {
        return pagging;
    }

    public void setPagging(boolean pagging) {
        this.pagging = pagging;
    }
}
