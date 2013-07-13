package web.action.process;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.domain.process.WorkTask;
import com.lehecai.admin.web.domain.user.Role;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.enums.WeekType;
import com.lehecai.admin.web.service.process.WorkProcessUserService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;

public class ProcessUserAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	private List<WorkProcessUser> workProcessUserList;
	private WorkProcessUser workProcessUser;
	private WorkTask workTask;
	private WorkProcessUserService workProcessUserService;
	
	private List<User> users;
	private List<Role> roles;
	private PermissionService permissionService;
	
	private Long userid;
	private String userName;
	private String name;
	private Date beginDate;
	private Date endDate;
	private Long roleId;
	private String valid;
	private Integer processUserTypeValue;
	
	private List<Integer> weekList;
	
	public String handle() {
		logger.info("进入查询流程人员列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		ProcessUserType put = processUserTypeValue == null || processUserTypeValue == ProcessUserType.ALL.getValue() ? null : ProcessUserType.getItem(processUserTypeValue);
		if (workProcessUser == null) {
			workProcessUser = new WorkProcessUser();
		}
		workProcessUser.setProcessUserType(put);
		workProcessUserList = workProcessUserService.list(workProcessUser, super.getPageBean());
		PageBean pageBean = workProcessUserService.getPageBean(workProcessUser, super.getPageBean());
		if (pageBean != null) {
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		return "list";
	}
	
	public String manage() {
		logger.info("进入更新流程人员配置");
		if (workProcessUser != null && workProcessUser.getWeekList() != null && workProcessUser.getWeekList().size() > 0) {
			String dutyDate = "";
			for (Integer i : workProcessUser.getWeekList()) {
				dutyDate = dutyDate + i + ",";
			}
			if (dutyDate.length() > 0) {
				dutyDate = dutyDate.substring(0, dutyDate.length() - 1);
			}
			workProcessUser.setDutyDate(dutyDate);
		}
		try {
			if (workProcessUser != null && workProcessUser.getNotifyTimes() != null && !"".equals(workProcessUser.getNotifyTimes())) {
				String[] dates = workProcessUser.getNotifyTimes().split("\\,");
				JSONArray jsonArray = new JSONArray();
				for (int i = 0; i < dates.length; i++) {
					String[] temp = dates[i].split("-");
					JSONObject j = new JSONObject();
					j.put("starttime", temp[0]);
					j.put("endtime", temp[1]);
					jsonArray.add(j);
				}
				workProcessUser.setNotifyTimes(jsonArray.toString());
			}
		} catch (Exception e) {
			logger.error("更新人员配置，转换时间出错");
			super.setErrorMessage("更新人员配置，转换时间出错");
			return "failure";
		}
		ProcessUserType put = processUserTypeValue == null || processUserTypeValue ==0 ? null : ProcessUserType.getItem(processUserTypeValue);
		if (workProcessUser != null) {
			workProcessUser.setProcessUserType(put);
		}
		workProcessUserService.manage(workProcessUser);
		super.setForwardUrl("/process/workProcess.do?action=userList&workTask.id="+workTask.getId());
		return "success";
	}
	
	public String input() {
		if (workProcessUser != null && workProcessUser.getId() != null && workProcessUser.getId() != 0) {
			workProcessUser = workProcessUserService.get(workProcessUser.getId());
			return "inputForm";
		} else if (workProcessUser != null && workProcessUser.getOperateId() != null && workProcessUser.getOperateId() != 0) { 
			User userTmp = permissionService.getUser(workProcessUser.getOperateId());
			workProcessUser.setName(userTmp.getName());
			workProcessUser.setTel(userTmp.getTel());
			workProcessUser.setEmail(userTmp.getEmail());
			return "inputForm";
		} else {
			logger.info("进入查询用户");
			HttpServletRequest request = ServletActionContext.getRequest();
			
			String endDateStr = DateUtil.formatDate(endDate);
			endDateStr = endDateStr + " 23:59:59";
			users = permissionService.list(userName, name, beginDate, DateUtil.parseDate(endDateStr, DateUtil.DATETIME), 
					roleId, valid, super.getPageBean());//多条件分页查询用户
			PageBean pageBean = permissionService.getPageBean(userName, name, beginDate, endDate, 
					roleId, valid, super.getPageBean());//封装多条件查询分页信息
			super.setPageString(PageUtil.getPageString(request, pageBean));
			
			roles = permissionService.listRoles(null);//查询所有角色
			if (roles == null || roles.size() == 0) {
				logger.info("查询用户，暂无角色");
			}
			
			logger.info("查询用户结束");
			return "inputList";
		}
	}
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
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

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}
	
	public List<WeekType> getWeeks() {
		return WeekType.list;
	}

	public List<Integer> getWeekList() {
		return weekList;
	}

	public void setWeekList(List<Integer> weekList) {
		this.weekList = weekList;
	}

	public List<WorkProcessUser> getWorkProcessUserList() {
		return workProcessUserList;
	}

	public void setWorkProcessUserList(List<WorkProcessUser> workProcessUserList) {
		this.workProcessUserList = workProcessUserList;
	}

	public WorkProcessUserService getWorkProcessUserService() {
		return workProcessUserService;
	}

	public void setWorkProcessUserService(
			WorkProcessUserService workProcessUserService) {
		this.workProcessUserService = workProcessUserService;
	}

	public WorkProcessUser getWorkProcessUser() {
		return workProcessUser;
	}
	
	public WorkTask getWorkTask() {
		return workTask;
	}

	public void setWorkTask(WorkTask workTask) {
		this.workTask = workTask;
	}

	public void setWorkProcessUser(WorkProcessUser workProcessUser) {
		this.workProcessUser = workProcessUser;
	}
	public List<ProcessUserType> getProcessUserTypes() {
		return ProcessUserType.list;
	}

	public Integer getProcessUserTypeValue() {
		return processUserTypeValue;
	}

	public void setProcessUserTypeValue(Integer processUserTypeValue) {
		this.processUserTypeValue = processUserTypeValue;
	}

}
