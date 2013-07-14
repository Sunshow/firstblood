package com.bjgl.web.entity.user;

import java.io.Serializable;

public class Role implements Serializable {

    private static final long serialVersionUID = -3481413914584370939L;
    private Long id;
    private String name;
    private Boolean valid;
    private String memo;
    private Boolean restriction;
    private String restrictionIp;

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

    public String getRestrictionIp() {
        return restrictionIp;
    }

    public void setRestrictionIp(String restrictionIp) {
        this.restrictionIp = restrictionIp;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getRestriction() {
        return restriction;
    }

    public void setRestriction(Boolean restriction) {
        this.restriction = restriction;
    }
}
