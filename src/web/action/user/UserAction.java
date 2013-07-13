package web.action.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.user.Role;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.domain.user.UserRole;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreStringUtils;

public class UserAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(UserAction.class);
	
	private PermissionService permissionService;
	private static final String NEED_MODIFY_PASSWORD = "1";
	
	private User user;
	private Role role;
	
	private String checkValid;
	private String conPassword;
	private String pwdFlag;
	
	private List<User> users;
	private List<Role> roles;
	
	private String userName;
	private String name;
	private Date beginDate;
	private Date endDate;
	private Long roleID;
	private String valid;
	
	/**
	 * 多条件分页查询所有用户
	 * @return
	 */
	public String handle() {
		logger.info("进入查询用户");
		roles = permissionService.listRoles(null);//查询所有角色
		if (roles == null || roles.size() == 0) {
			logger.info("查询用户，暂无角色");
		}
		return "list";
	}
	
	public String query() {
		logger.info("进入查询用户");
		HttpServletRequest request = ServletActionContext.getRequest();
		String endDateStr = DateUtil.formatDate(endDate);
		endDateStr = endDateStr + " 23:59:59";
		try {
			users = permissionService.list(userName, name, beginDate, DateUtil.parseDate(endDateStr, DateUtil.DATETIME), 
					roleID, valid, super.getPageBean());//多条件分页查询用户
		} catch (Exception e) {
			logger.error(e.getMessage());
			super.setErrorMessage(e.getMessage());
			return "failure";
		}//多条件分页查询用户
		PageBean pageBean = null;
		try {
			pageBean = permissionService.getPageBean(userName, name, beginDate, endDate, 
					 roleID, checkValid, super.getPageBean());//封装多条件查询分页信息
		} catch (Exception e) {
			logger.error(e.getMessage());
			super.setErrorMessage(e.getMessage());
			return "failure";
		}//封装多条件查询分页信息
		super.setPageString(PageUtil.getPageString(request, pageBean));
		
		try {
			roles = permissionService.listRoles(null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			super.setErrorMessage(e.getMessage());
			return "failure";
		}//查询所有角色
		if (roles == null || roles.size() == 0) {
			logger.info("查询用户，暂无角色");
		}
		
		logger.info("查询用户结束");
		return "list";
		
	}
	
	public String manage() {
		logger.info("进入更新用户信息");
		if (user != null) {
			if (user.getName() == null || "".equals(user.getName())) {
				logger.error("添加用户，用户姓名为空");
				super.setErrorMessage("用户姓名不能为空");
				return "failure";
			}
			User pUser = null;
			UserRole ur = null;
			if (user.getId() != null ) {//修改
				if (!StringUtils.isEmpty(pwdFlag)) {
					try {
						pUser = permissionService.getUser(user.getId());
					} catch (Exception e) {
						logger.error(e.getMessage());
						super.setErrorMessage(e.getMessage());
						return "failure";
					}
					if (NEED_MODIFY_PASSWORD.equals(pwdFlag)) {//需要修改密码
						if (user.getPassword() == null || "".equals(user.getPassword())) {
							logger.error("密码为空");
							super.setErrorMessage("密码不能为空");
							return "failure";
						}
						if (conPassword == null || "".equals(conPassword)) {
							logger.error("确认密码为空");
							super.setErrorMessage("确认密码不能为空");
							return "failure";
						}
						if (!(user.getPassword()).equals(conPassword)) {
							logger.error("两次密码输入不一致");
							super.setErrorMessage("两次密码输入不一致");
							return "failure";
						}
						pUser.setPassword(CoreStringUtils.md5(user.getPassword(), CharsetConstant.CHARSET_UTF8));
					}
					if (checkValid != null && "on".equals(checkValid)) {
						pUser.setValid(true);
					}else{
						pUser.setValid(false);
					}
					pUser.setUpdateTime(new Date());
					pUser.setTel(user.getTel());
					pUser.setEmail(user.getEmail());
				} else {
					logger.error("传递pwdFlag参数错误");
					super.setErrorMessage("传递pwdFlag参数错误");
					return "failure";
				}
				try {
					List<UserRole> userRoleList = permissionService.getRolesByUser(pUser);
					if(userRoleList!=null && userRoleList.size() > 0){
						ur = userRoleList.get(0);
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					super.setErrorMessage(e.getMessage());
					return "failure";
				}
			} else {//添加
				if (user.getUserName() == null || "".equals(user.getUserName())) {
					logger.error("用户名为空");
					super.setErrorMessage("用户名不能为空");
					return "failure";
				} else {
					User sUser = null;
					try {
						sUser = permissionService.getByUserName(user.getUserName());
					} catch (Exception e) {
						logger.error(e.getMessage());
						super.setErrorMessage(e.getMessage());
						return "failure";
					}
					if (sUser != null && sUser.getId() != null) {
						logger.error("用户名已被使用");
						super.setErrorMessage("用户名已被使用");
						return "failure";
					}
				}
				if (StringUtils.isEmpty(user.getPassword())) {
					logger.error("密码为空");
					super.setErrorMessage("密码不能为空");
					return "failure";
				}
				if (StringUtils.isEmpty(conPassword)) {
					logger.error("确认密码为空");
					super.setErrorMessage("确认密码不能为空");
					return "failure";
				}
				if (!(user.getPassword()).equals(conPassword)) {
					logger.error("两次密码输入不一致");
					super.setErrorMessage("两次密码输入不一致");
					return "failure";
				}
				user.setPassword(CoreStringUtils.md5(user.getPassword(), CharsetConstant.CHARSET_UTF8));
				user.setCreateTime(new Date());
				user.setUpdateTime(new Date());
				user.setRoleID(new Long(0));
				if (checkValid != null && "on".equals(checkValid)) {
					user.setValid(true);
				}
				pUser = user;
			}
			if(ur == null){
				ur = new UserRole();
			}
			ur.setRoleId(user.getRole().getId());
			try {
				permissionService.manage(pUser,ur);
			} catch (Exception e) {
				logger.error(e.getMessage());
				super.setErrorMessage(e.getMessage());
				return "failure";
			}
		} else {
			logger.error("添加用户错误，提交表单为空");
			super.setErrorMessage("添加用户错误，提交表单不能为空");
			return "failure";
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
		if (user != null && user.getId() != null) {
			try {
				user = permissionService.getUser(user.getId());
				List<UserRole> urList = permissionService.getRolesByUser(user);
				if(urList != null && urList.size() > 0){
					UserRole ur = urList.get(0);
					Role role = new Role();
					role.setId(ur.getRoleId());
					user.setRole(role);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				super.setErrorMessage(e.getMessage());
				return "failure";
			}
		}
		try {
			roles = permissionService.listRoles(role);
		} catch (Exception e) {
			logger.error(e.getMessage());
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		if (user != null && user.getId() != null) {
			if (user.isValid()) {
				checkValid = "on";
			}
		}
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看用户详情");
		if (user != null && user.getId() != null) {
			user = permissionService.getUser(user.getId());
			role = permissionService.getRole(user.getRoleID());
		} else {
			logger.error("查看用户详情，编码为空");
			super.setErrorMessage("查看用户详情，编码为空");
			return "failure";
		}
		logger.info("查看用户详情结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除用户");
		if (user != null && user.getId() != null) {
			user = permissionService.getUser(user.getId());
			permissionService.delUser(user);
		} else {
			logger.error("删除用户，编码为空");
			super.setErrorMessage("删除用户，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/user/user.do");
		logger.info("删除用户结束");
		return "forward";
	}
	
	public void check() {
		logger.info("进入检验用户名是否存在");
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean flag = true;
		User sUser = permissionService.getByUserName(user.getUserName());
		if (sUser != null && sUser.getId() != null) {
			flag = false;
		}
		PrintWriter out = null;
		response.setContentType("text/html; charset=utf-8");
		try {
			out = response.getWriter();
			out.print(flag);
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public String getCheckValid() {
		return checkValid;
	}
	public void setCheckValid(String checkValid) {
		this.checkValid = checkValid;
	}
	public String getConPassword() {
		return conPassword;
	}
	public void setConPassword(String conPassword) {
		this.conPassword = conPassword;
	}
	public String getPwdFlag() {
		return pwdFlag;
	}
	public void setPwdFlag(String pwdFlag) {
		this.pwdFlag = pwdFlag;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public Long getRoleID() {
		return roleID;
	}
	public void setRoleID(Long roleID) {
		this.roleID = roleID;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
}
