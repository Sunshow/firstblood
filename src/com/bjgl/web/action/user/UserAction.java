package com.bjgl.web.action.user;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.entity.user.Role;
import com.bjgl.web.entity.user.User;
import com.bjgl.web.entity.user.UserRole;
import com.bjgl.web.service.user.RoleService;
import com.bjgl.web.service.user.UserRoleService;
import com.bjgl.web.service.user.UserService;
import com.bjgl.web.utils.CharsetConstant;
import com.bjgl.web.utils.CoreStringUtils;
import com.bjgl.web.utils.PageUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserAction extends BaseAction {
    private static final long serialVersionUID = 2436161530465382824L;

    private UserService userService;
    private RoleService roleService;
    private UserRoleService userRoleService;

    private User user;
    private Role role;

    private Long roleId;

    private String confirmPassword;

    private List<User> userList;
    private List<Role> roleList;

    private String username;
    private String name;
    private String phone;
    private Date beginDate;
    private Date endDate;

    private Integer validValue;

    /**
     * 多条件分页查询所有用户
     *
     * @return
     */
    public String handle() {
        logger.info("进入查询用户");
        return "list";
    }

    public String query() {
        logger.info("进入查询用户");
        HttpServletRequest request = ServletActionContext.getRequest();

        List<Criterion> criterionList = new ArrayList<Criterion>();

        if (StringUtils.isNotBlank(this.getUsername())) {
            criterionList.add(Restrictions.like("username", "%" + StringUtils.trim(this.getUsername()) + "%"));
        }
        if (StringUtils.isNotBlank(this.getName())) {
            criterionList.add(Restrictions.like("name", "%" + StringUtils.trim(this.getName()) + "%"));
        }
        if (StringUtils.isNotBlank(this.getPhone())) {
            criterionList.add(Restrictions.like("tel", "%" + StringUtils.trim(this.getPhone()) + "%"));
        }

        if (this.getValidValue() != null && this.getValidValue() >= 0) {
            criterionList.add(Restrictions.eq("valid", this.getValidValue() > 0));
        }

        if (beginDate != null) {
            criterionList.add(Restrictions.ge("loginTime", beginDate));
        }
        if (endDate != null) {
            criterionList.add(Restrictions.le("loginTime", endDate));
        }

        userList = userService.findByExample(null, criterionList, this.getPageBean(), Order.asc("id"));

        this.setPageString(PageUtil.getPageString(request, userService.getPageBean(null, criterionList, this.getPageBean())));

        logger.info("查询用户结束");
        return "list";

    }

    public String manage() {
        logger.info("进入更新用户信息");

        if (user == null) {
            this.errorForward(FAILURE, "非法请求");
        }

        this.emptyCheck(user.getUsername(), FAILURE, "用户名不能为空");
        this.emptyCheck(user.getName(), FAILURE, "用户姓名不能为空");
        this.emptyCheck(this.getRoleId(), FAILURE, "所属角色不能为空");

        if (user.getId() == null) {
            // 新用户需要判断密码输入是否匹配
            this.emptyCheck(user.getPassword(), FAILURE, "密码不能为空");
            this.emptyCheck(this.getConfirmPassword(), FAILURE, "请确认密码");

            if (!user.getPassword().equals(this.getConfirmPassword())) {
                this.errorForward(FAILURE, "两次密码输入不一致");
            }

            // 确认用户名不重复
            if (userService.findByUsername(user.getUsername()) != null) {
                this.errorForward(FAILURE, "用户名已存在");
            }

            // 通过检验，开始添加用户
            user.setPassword(CoreStringUtils.md5(user.getPassword(), CharsetConstant.CHARSET_UTF8));
            user.setCreateTime(new Date());
            user.setUpdateTime(user.getCreateTime());

            UserRole userRole = new UserRole();
            userRole.setRoleId(this.getRoleId());

            List<UserRole> userRoleList = new ArrayList<UserRole>();
            userRoleList.add(userRole);

            userService.saveOrUpdate(user, userRoleList);
        } else {
            User updateUser = userService.findById(user.getId());

            // 是否设置了修改密码
            if (StringUtils.isNotBlank(user.getPassword()) || StringUtils.isNotBlank(this.getConfirmPassword())) {
                // 新用户需要判断密码输入是否匹配
                this.emptyCheck(user.getPassword(), FAILURE, "密码不能为空");
                this.emptyCheck(this.getConfirmPassword(), FAILURE, "请确认密码");

                if (!user.getPassword().equals(this.getConfirmPassword())) {
                    this.errorForward(FAILURE, "两次密码输入不一致");
                }

                // 设置密码
                updateUser.setPassword(CoreStringUtils.md5(user.getPassword(), CharsetConstant.CHARSET_UTF8));
            }

            // 通过检验，开始修改用户
            updateUser.setName(user.getName());
            updateUser.setTel(user.getTel());
            updateUser.setEmail(user.getEmail());
            updateUser.setValid(user.getValid());
            updateUser.setMemo(user.getMemo());

            updateUser.setUpdateTime(new Date());

            UserRole userRole = new UserRole();
            userRole.setRoleId(this.getRoleId());

            List<UserRole> userRoleList = new ArrayList<UserRole>();
            userRoleList.add(userRole);

            userService.saveOrUpdate(updateUser, userRoleList);
        }

        super.setSuccessMessage("更新成功");
        super.setForwardUrl("/user/user.do");
        logger.info("更新用户信息结束");
        return "success";
    }

    /**
     * 转向添加/修改用户
     */
    public String input() {
        if (user == null) {
            user = new User();
        }
        if (user.getId() != null) {
            user = userService.findById(user.getId());

            if (user == null) {
                this.errorForward(FAILURE, "要编辑的用户不存在");
            }

            List<UserRole> userRoleList = userRoleService.findByUserId(user.getId());
            if (userRoleList != null && !userRoleList.isEmpty()) {
                // 取用户所属第一个角色
                roleId = userRoleList.get(0).getRoleId();
            }
        }

        // 查找可用的角色
        Role example = new Role();
        example.setValid(Boolean.TRUE);
        roleList = roleService.findByExample(example, null);

        return "inputForm";
    }

    public String view() {
        logger.info("进入查看用户详情");

        this.emptyCheck(user, FAILURE, "非法请求");
        this.emptyCheck(user.getId(), FAILURE, "非法请求");

        user = userService.findById(user.getId());
        this.emptyCheck(user, FAILURE, "用户不存在");

        List<UserRole> userRoleList = userRoleService.findByUserId(user.getId());
        if (userRoleList != null && !userRoleList.isEmpty()) {
            // 取用户所属第一个角色
            roleId = userRoleList.get(0).getRoleId();
            role = roleService.findById(roleId);
        }

        logger.info("查看用户详情结束");
        return "view";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getValidValue() {
        return validValue;
    }

    public void setValidValue(Integer validValue) {
        this.validValue = validValue;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public void setUserRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }
}
