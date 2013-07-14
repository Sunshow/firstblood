package com.bjgl.web.entity.user;

import java.io.Serializable;

public class Permission implements Serializable {

    private static final long serialVersionUID = -6100412628585480536L;
    private Long id;
    private String name;
    private String url;
    private Long menuId;
    private Integer orderView;
    private String actionName;
    private String paramName;
    private String paramValue;
    private Boolean valid;
    private String memo;
    private Boolean menuItem;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Integer getOrderView() {
        return orderView;
    }

    public void setOrderView(Integer orderView) {
        this.orderView = orderView;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(Boolean menuItem) {
        this.menuItem = menuItem;
    }
}
