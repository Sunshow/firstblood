package com.bjgl.web.interceptor;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.bean.UserSessionBean;
import com.bjgl.web.constant.Global;
import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.entity.user.PermissionItem;
import com.bjgl.web.service.user.PermissionService;
import com.bjgl.web.utils.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UserSessionInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 5545476931270525263L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PermissionService permissionService;

    public String intercept(ActionInvocation invocation) throws Exception {
        boolean pass = false;
        logger.info("Enter UserSessionInterceptor");
        ActionContext ac = invocation.getInvocationContext();
        String actionName = ac.getName();
        Map<?, ?> paramMap = ac.getParameters();
        logger.info("ActionName:{}", actionName);
        logger.info("Parameters:{}", paramMap.toString());
        UserSessionBean userSessionBean = (UserSessionBean) ac.getSession().get(Global.USER_SESSION);

        //HttpServletRequest request = (HttpServletRequest)ac.get(ServletActionContext.HTTP_REQUEST);
        //request.setAttribute("Sunshow", "asdfasfdasf");

        if (userSessionBean == null) {
            BaseAction basetion = (BaseAction) invocation.getAction();
            basetion.setErrorMessage("您的session丢失，请重新登录");
            return "index";
        }
        String message = StringUtil.getStringByActionHashMap(paramMap);
        if (message != null && message.length() >= 1000) {
            message = message.substring(0, 900);
        }
        logger.info("Start judge permission");
        List<Permission> permissions = userSessionBean.getPermissions();
        for (Permission permission : permissions) {
            if (permission.getActionName().trim().equals(actionName.trim())) {
                if (permission.getParamName() != null && !"".equals(permission.getParamName())) {
                    String[] paramValue = (String[]) paramMap.get(permission.getParamName());
                    if (paramValue == null || paramValue.length == 0) {
                        continue;
                    }
                    if (!paramValue[0].equals(permission.getParamValue())) {
                        continue;
                    }
                }
                String[] actions = (String[]) paramMap.get("action");
                if (actions == null || actions.length == 0) {
                    pass = true;
                    break;
                }
                String action = actions[0];
                if ("handle".equals(action)) {
                    pass = true;
                    break;
                }
                List<PermissionItem> permissionItems = null;
                permissionItems = permissionService.listPermissionItems(permission);

                if (action != null && permissionItems != null
                        && permissionItems.size() > 0
                        && permission.getPermissionItemStr() != null
                        && permission.getPermissionItemStr().size() > 0) {
                    for (PermissionItem permissionItem : permissionItems) {
                        for (String permItemId : permission.getPermissionItemStr()) {
                            if (permissionItem.getId().toString().equals(permItemId)) {
                                if (permissionItem.getMethodName().equals(action)) {
                                    pass = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.info("End judge permission");
        logger.info("Judge permission result:{}", pass);
        logger.info("Exit UserSessionInterceptor");
        if (pass) {
            return invocation.invoke();
        } else {
            return "unauthorized";
        }
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
