package com.bjgl.web.entity.user;

import java.io.Serializable;

public class PermissionItem implements Serializable {

    private static final long serialVersionUID = -6100412628585480536L;
    private Long id;
    private String name;
    private Long permissionId;
    private Integer orderView;
    private String methodName;
    private Boolean valid;
    private String memo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
