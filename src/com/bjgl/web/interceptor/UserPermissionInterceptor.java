package com.bjgl.web.interceptor;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.bean.MenuPermissionBean;
import com.bjgl.web.bean.UserSessionBean;
import com.bjgl.web.constant.Global;
import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.entity.user.PermissionItem;
import com.bjgl.web.service.user.PermissionItemService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserPermissionInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 5545476931270525263L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MenuPermissionBean menuPermissionBean;

    private PermissionItemService permissionItemService;

    public String intercept(ActionInvocation invocation) throws Exception {
        logger.info("Enter UserPermissionInterceptor");
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

        logger.info("Start judge permission");

        List<Permission> permissionList = menuPermissionBean.getPermissionListById(userSessionBean.getPermissionIdList());

        boolean pass = false;

        for (Permission permission : permissionList) {
            if (!permission.getActionName().equals(actionName)) {
                continue;
            }
            if (StringUtils.isNotBlank(permission.getParamName())) {
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
            List<PermissionItem> permissionItemList = permissionItemService.findByPermissionId(permission.getId());

            if (action != null && permissionItemList != null
                    && !permissionItemList.isEmpty()) {
                Set<Long> permissionItemIdSet = null;

                if (userSessionBean.getPermissionItemIdSetMap() != null) {
                    permissionItemIdSet = userSessionBean.getPermissionItemIdSetMap().get(permission.getId());
                }

                if (permissionItemIdSet != null) {
                    for (PermissionItem permissionItem : permissionItemList) {
                        if (permissionItemIdSet.contains(permissionItem.getId())) {
                            if (permissionItem.getMethodName().equals(action)) {
                                pass = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        logger.info("End judge permission");
        logger.info("Judge permission result:{}", pass);
        logger.info("Exit UserPermissionInterceptor");
        if (pass) {
            return invocation.invoke();
        } else {
            return "unauthorized";
        }
    }

    public void setMenuPermissionBean(MenuPermissionBean menuPermissionBean) {
        this.menuPermissionBean = menuPermissionBean;
    }

    public void setPermissionItemService(PermissionItemService permissionItemService) {
        this.permissionItemService = permissionItemService;
    }
}
