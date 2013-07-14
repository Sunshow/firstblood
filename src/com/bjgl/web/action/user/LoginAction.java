package com.bjgl.web.action.user;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.bean.UserSessionBean;
import com.bjgl.web.constant.Global;
import com.bjgl.web.entity.user.*;
import com.bjgl.web.service.user.PermissionService;
import com.bjgl.web.service.user.UserService;
import com.bjgl.web.utils.CaptchaServiceSingleton;
import com.bjgl.web.utils.CoreHttpUtils;
import com.octo.captcha.service.CaptchaServiceException;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class LoginAction extends BaseAction {
    private static final long serialVersionUID = -8830679912602886965L;

    private UserService userService;

    private PermissionService permissionService;

    private User user;

    private String username;
    private String password;
    private String verifyCode;

    private Boolean enableVerifyCode;

    private final static String INDEX = "index";

    @SuppressWarnings("unchecked")
    public String handle() {
        logger.info("进入验证登录");

        this.emptyCheck(this.getUsername(), INDEX, "用户名不能为空");
        this.emptyCheck(this.getPassword(), INDEX, "密码不能为空");

        if (this.getEnableVerifyCode()) {
            this.emptyCheck(this.getVerifyCode(), INDEX, "验证码不能为空");

            Boolean isVerifyCodeRight = Boolean.FALSE;
            HttpServletRequest request = ServletActionContext.getRequest();
            String captchaId = null;
            logger.info("request.getSession(false): {}", request.getSession(false));
            if (request.getSession(false) == null) {
                captchaId = request.getSession(true).getId();
            } else {
                captchaId = request.getSession(false).getId();
            }
            try {
                isVerifyCodeRight = CaptchaServiceSingleton.getInstance()
                        .validateResponseForID(captchaId, verifyCode);
            } catch (CaptchaServiceException e) {
                logger.error("验证码超时");
                super.setErrorMessage("验证码超时,请重新登录");
                return "index";
            }
            if (!isVerifyCodeRight) {
                logger.error("验证码错误");
                super.setErrorMessage("验证码错误");
                return "index";
            }
        }
        User user = userService.login(this.getUsername(), this.getPassword());
        if (user == null) {
            logger.error("用户名或密码错误");
            super.setErrorMessage("用户名或密码错误");
            return "index";
        }

        List<UserRole> userRoleList;
        try {
            userRoleList = permissionService.getRolesByUser(user);
        } catch (Exception e) {
            logger.error(e.getMessage());
            super.setErrorMessage(e.getMessage());
            return "failure";
        }
        Long roleId = null;
        if (userRoleList != null && userRoleList.size() > 0) {
            roleId = userRoleList.get(0).getRoleId();
        }
        if (roleId != null && roleId.longValue() != 0) {
            Role role;
            try {
                role = permissionService.getRole(roleId);
            } catch (Exception e) {
                logger.error(e.getMessage());
                super.setErrorMessage(e.getMessage());
                return "failure";
            }
            if (role.isRestriction()) {//限定ip时，进行有效ip段验证
                String remoteIp = getRemoteIp(ServletActionContext.getRequest());
                if (!matchingIp(role.getRestrictionIp(), remoteIp)) {
                    logger.error("IP地址无效");
                    super.setErrorMessage("您的IP地址无效");
                    return "index";
                }
            }

            if (user.getLoginTime() != null) {
                user.setLastLoginTime(user.getLoginTime());
            }
            user.setLoginTime(new Date());
            try {
                permissionService.manage(user);
            } catch (Exception e) {
                logger.error(e.getMessage());
                super.setErrorMessage(e.getMessage());
                return "failure";
            }

            //创建userSessionBean
            UserSessionBean userSessionBean = new UserSessionBean();
            //setUser
            userSessionBean.setUser(user);
            //serRole
            userSessionBean.setRole(role);
            List<Permission> permList = new ArrayList<Permission>();
            List<Menu> menuList = new ArrayList<Menu>();
            List<RolePermission> rolePermList = new ArrayList<RolePermission>();


            // 一次性读出所有菜单
            List<Menu> allMenuList = permissionService.listMenus(null);
            Map<Long, Menu> allMenuMap = new HashMap<Long, Menu>();
            // 转置map
            if (allMenuList != null) {
                for (Menu menu : allMenuList) {
                    allMenuMap.put(menu.getId(), menu);
                }
            }

            // 一次性读出所有权限
            List<Permission> allPermissionList = permissionService.listPermissions(null);
            Map<Long, Permission> allPermissionMap = new HashMap<Long, Permission>();
            // 转置map
            if (allPermissionList != null) {
                for (Permission permission : allPermissionList) {
                    allPermissionMap.put(permission.getId(), permission);
                }
            }


            rolePermList = permissionService.getPermissionsByRole(role);
            if (rolePermList == null || rolePermList.size() == 0) {
                logger.error("您所拥有的角色没有任何权限");
                super.setErrorMessage("您所拥有的角色没有任何权限，请联系管理员");
                return "index";
            }
            Set<Long> tmpMenuId = new HashSet<Long>();
            for (RolePermission rp : rolePermList) {
                //添加主权限
                Permission tmpPermission = allPermissionMap.get(rp.getPermissionId());
                if (tmpPermission == null) {
                    // 权限已经被删除
                    continue;
                }
                String permissionItemIds = rp.getPermissionItemIds();
                if (permissionItemIds != null && !"".equals(permissionItemIds)) {
                    List<String> list2 = new ArrayList<String>();
                    String[] permItemNode = StringUtils.split(permissionItemIds, ',');
                    for (String permItemStr : permItemNode) {
                        list2.add(permItemStr);
                    }
                    tmpPermission.setPermissionItemStr(list2);
                }
                permList.add(tmpPermission);
                tmpMenuId.add(tmpPermission.getMenuID());
            }
            //添加菜单
            for (Long menuId : tmpMenuId) {
                Menu menu = allMenuMap.get(menuId);
                menuList.add(menu);
            }

            //按orderView排序 数值大的在前面
            Collections.sort(menuList, new Comparator<Menu>() {
                public int compare(Menu arg0, Menu arg1) {
                    return arg1.getOrderView().compareTo(arg0.getOrderView());
                }
            });

            //按orderView排序 数值大的在前面
            Collections.sort(permList, new Comparator<Permission>() {
                public int compare(Permission arg0, Permission arg1) {
                    return arg1.getOrderView().compareTo(arg0.getOrderView());
                }
            });

            userSessionBean.setPermissions(permList);
            userSessionBean.setMenus(menuList);
            super.getSession().put(Global.USER_SESSION, userSessionBean);
            super.setForwardUrl("/main.do");
            logger.info("验证登录结束");
            return "forward";
        } else {
            super.setErrorMessage("没有分配角色，请联系管理员");
            return "failure";
        }
    }

    //ip段匹配
    private boolean matchingIp(String restrictionIp, String remoteIp) {
        //有效ip段或请求ip不存在
        if (restrictionIp == null || remoteIp == null) {
            return false;
        }

        String[] restrictionIpArray = restrictionIp.trim().split(",");
        String[] remoteIpItem = remoteIp.trim().split("\\.");
        if (restrictionIpArray.length > 0) {
            for (String restrictionIpSegment : restrictionIpArray) {
                String[] restrictionIpItem = restrictionIpSegment.trim().split("\\.");
                if (restrictionIpItem.length == 4 && remoteIpItem.length == 4) {
                    boolean flag = true;
                    for (int i = 0; i < 4; i++) {
                        if (!restrictionIpItem[i].equals("*") && !restrictionIpItem[i].equals(remoteIpItem[i])) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        return true;
                    }
                }
            }
        }


        return false;
    }

    //获取访问者IP
    private String getRemoteIp(HttpServletRequest request) {
        String[] clientIPArray = CoreHttpUtils.getClientIPArray(request);
        if (clientIPArray != null && clientIPArray.length > 0) {
            return clientIPArray[0];
        }
        return null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public Boolean getEnableVerifyCode() {
        if (enableVerifyCode == null) {
            return Boolean.TRUE;
        }
        return enableVerifyCode;
    }

    public void setEnableVerifyCode(Boolean enableVerifyCode) {
        this.enableVerifyCode = enableVerifyCode;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
