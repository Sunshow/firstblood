package com.bjgl.web.interceptor;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.bean.MenuBean;
import com.bjgl.web.bean.MenuPermissionBean;
import com.bjgl.web.bean.UserSessionBean;
import com.bjgl.web.constant.Global;
import com.bjgl.web.entity.user.Menu;
import com.bjgl.web.entity.user.Permission;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class UserSessionInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 8993269523298061799L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MenuPermissionBean menuPermissionBean;

    public String intercept(ActionInvocation invocation) throws Exception {
        logger.info("Enter UserSessionInterceptor");
        ActionContext ac = invocation.getInvocationContext();
        UserSessionBean userSessionBean = (UserSessionBean) ac.getSession().get(Global.USER_SESSION);

        if (userSessionBean == null) {
            BaseAction basetion = (BaseAction) invocation.getAction();
            basetion.setErrorMessage("您的session丢失，请重新登录");
            return "index";
        }

        List<Permission> permissionList = menuPermissionBean.getPermissionListById(userSessionBean.getPermissionIdList());

        Map<Long, List<Permission>> menuPermissionListMap = new HashMap<Long, List<Permission>>();

        Set<Long> menuIdSet = new HashSet<Long>();
        if (permissionList != null) {
            for (Permission permission : permissionList) {
                menuIdSet.add(permission.getMenuId());

                if (permission.getMenuItem() == null || !permission.getMenuItem()) {
                    continue;
                }

                if (!menuPermissionListMap.containsKey(permission.getMenuId())) {
                    menuPermissionListMap.put(permission.getMenuId(), new ArrayList<Permission>());
                }

                menuPermissionListMap.get(permission.getMenuId()).add(permission);
            }
        }

        List<Menu> menuList = menuPermissionBean.getMenuListById(new ArrayList<Long>(menuIdSet));

        List<MenuBean> menuBeanList = new ArrayList<MenuBean>();

        if (menuList != null) {
            for (Menu menu : menuList) {
                MenuBean menuBean = new MenuBean();
                menuBean.setMenu(menu);

                List<Permission> menuPermissionList = menuPermissionListMap.get(menu.getId());
                if (menuPermissionList != null) {
                    // 排序
                    Collections.sort(menuPermissionList, new Comparator<Permission>() {
                        public int compare(Permission arg0, Permission arg1) {
                            Integer o0 = arg0.getOrderView() == null ? 0 : arg0.getOrderView();
                            Integer o1 = arg1.getOrderView() == null ? 0 : arg1.getOrderView();

                            if (o0.intValue() == o1.intValue()) {
                                return arg0.getId().compareTo(arg1.getId());
                            }
                            return o1.compareTo(o0);
                        }
                    });
                    menuBean.setPermissionList(menuPermissionList);
                }

                menuBeanList.add(menuBean);
            }

            // 排序
            Collections.sort(menuBeanList, new Comparator<MenuBean>() {
                public int compare(MenuBean arg0, MenuBean arg1) {
                    Integer o0 = arg0.getMenu().getOrderView() == null ? 0 : arg0.getMenu().getOrderView();
                    Integer o1 = arg1.getMenu().getOrderView() == null ? 0 : arg1.getMenu().getOrderView();
                    if (o0.intValue() == o1.intValue()) {
                        return arg0.getMenu().getId().compareTo(arg1.getMenu().getId());
                    }
                    return o1.compareTo(o0);
                }
            });
        }

        HttpServletRequest request = (HttpServletRequest)ac.get(ServletActionContext.HTTP_REQUEST);
        request.setAttribute(Global.MENU_BEAN_LIST, menuBeanList);

        return invocation.invoke();
    }

    public void setMenuPermissionBean(MenuPermissionBean menuPermissionBean) {
        this.menuPermissionBean = menuPermissionBean;
    }
}
