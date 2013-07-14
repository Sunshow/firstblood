package com.bjgl.web.entity.user;

import java.io.Serializable;

public class Menu implements Serializable {

    private static final long serialVersionUID = -6100412628585480536L;
    private Long id;
    private String name;
    private String url;
    private Integer orderView;
    private Boolean valid;
    private String memo;

    public Long getId() {
        return id;
    }

    public void setId(Long iD) {
        id = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getOrderView() {
        return orderView;
    }

    public void setOrderView(Integer orderView) {
        this.orderView = orderView;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
