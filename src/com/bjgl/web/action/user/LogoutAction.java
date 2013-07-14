package com.bjgl.web.action.user;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.constant.Global;

public class LogoutAction extends BaseAction {
    private static final long serialVersionUID = -8830679912602886965L;

    public String handle() {
        logger.info("进入注销登录");
        if (super.getSession().get(Global.USER_SESSION) != null) {
            super.getSession().remove(Global.USER_SESSION);
        }
        super.setForwardUrl("/");
        logger.info("注销登录结束");
        return "forward";
    }
}
