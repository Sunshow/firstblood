package web.action.process;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.WorkProcess;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.domain.process.WorkTask;
import com.lehecai.admin.web.domain.user.Role;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.enums.TaskType;
import com.lehecai.admin.web.service.process.WorkProcessService;
import com.lehecai.admin.web.service.process.WorkProcessUserService;
import com.lehecai.admin.web.service.process.WorkTaskService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.PageUtil;

public class WorkProcessAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

	private Long id;
	private Integer processUserTypeValue;
	private Long userId;
	private Integer taskTypeValue;
	private Long roleId;
	private String processId;
	private String taskId;
	private List<WorkProcess> processList;
	private WorkProcess workProcess;
	private WorkTask workTask;
	private WorkProcessService workProcessService;
	private List<WorkTask> taskList;
	private WorkTaskService workTaskService;
	private List<WorkProcessUser> workProcessUserList;
	private PermissionService permissionService;
	private WorkProcessUserService workProcessUserService;
	private WorkProcessUser workProcessUser;
	private List<User> users;
	private List<Role> roles;
	
	public String handle() {
		logger.info("进入查询流程列表");

		HttpServletRequest request = ServletActionContext.getRequest();
		processList = workProcessService.list(processId, super.getPageBean());
		PageBean pageBean = workProcessService.getPageBean(processId, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		logger.info("结束查询流程列表");
		return "list";
	}
	
	public String input() {
		if (workProcess != null && workProcess.getId() != null && workProcess.getId() != 0) {
			workProcess = workProcessService.get(workProcess.getId());
		}
		return "inputForm";
	}
	
	public String manage() {
		logger.info("进入更新流程信息");
		if (workProcess != null) {
			if (workProcess.getProcessId() == null || "".equals(workProcess.getProcessId())) {
				logger.error("流程ID不能为空");
				super.setErrorMessage("流程ID不能为空");
				return "failure";
			}
			if (workProcess.getId() == null || workProcess.getId() == 0) {
				WorkProcess workProcessTemp = workProcessService.getByProcessId(workProcess.getProcessId());
				if (workProcessTemp != null && workProcessTemp.getId() != null && workProcessTemp.getId() != 0) {
					logger.error("流程ID已存在");
					super.setErrorMessage("流程ID已存在");
					return "failure";
				}
			}
			workProcessService.manage(workProcess);
		}
		super.setForwardUrl("/process/workProcess.do");
		return "success";
	}
	
	
	
	public String del() {
		logger.info("进入删除流程信息");
		if (workProcess != null && workProcess.getId() != null && workProcess.getId() != 0) {
			workProcess = workProcessService.get(workProcess.getId());
			workProcessService.del(workProcess);
			super.setForwardUrl("/process/workProcess.do");
			return "success";
		} else {
			logger.error("流程信息为空");
			super.setErrorMessage("流程信息为空");
			return "failure";
		}
	}
	
	public String taskList() {
		logger.info("进入查询任务列表");

		HttpServletRequest request = ServletActionContext.getRequest();
		if (workTask != null) {
			taskList = workTaskService.list(workTask.getProcessId(), taskId, super.getPageBean());
			PageBean pageBean = workTaskService.getPageBean(workTask.getProcessId(), taskId, super.getPageBean());
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
			logger.info("结束查询任务列表");
		}
		return "taskList";
	}
	
	public String taskInput() {
		if (workTask != null && workTask.getId() != null && workTask.getId() != 0) {
			workTask = workTaskService.get(workTask.getId());
		}
		return "taskInputForm";
	}
	
	public String taskManage() {
		logger.info("进入更新任务信息");
		if (workTask != null) {
			if (workTask.getTaskId() == null || "".equals(workTask.getTaskId())) {
				logger.error("任务ID不能为空");
				super.setErrorMessage("任务ID不能为空");
				return "failure";
			}
			if (workTask.getProcessId() == null || "".equals(workTask.getProcessId())) {
				logger.error("流程ID不能为空");
				super.setErrorMessage("流程ID不能为空");
				return "failure";
			}
			if (taskTypeValue == null) {
				logger.error("任务类型不能为空或者全部");
				super.setErrorMessage("任务类型不能为空或者全部");
				return "failure";
			}
			TaskType tt = TaskType.getItem(taskTypeValue);
			workTask.setTaskType(tt);
			WorkProcess workProcessTemp = workProcessService.getByProcessId(workTask.getProcessId());
			if (workProcessTemp == null || workProcessTemp.getId() == null || workProcessTemp.getId() == 0) {
				logger.error("流程不存在");
				super.setErrorMessage("流程不存在");
				return "failure";
			}
			if (workTask.getId() == null || workTask.getId() == 0) {
				WorkTask workTaskTemp = workTaskService.getByTaskIdAndProcessId(workTask.getTaskId(), workTask.getProcessId());
				if (workTaskTemp != null && workTaskTemp.getId() != null && workTaskTemp.getId() != 0) {
					logger.error("任务ID已存在");
					super.setErrorMessage("任务ID已存在");
					return "failure";
				}
			}
			workTaskService.manage(workTask);
		}
		super.setForwardUrl("/process/workProcess.do?action=taskList&workTask.processId="+workTask.getProcessId());
		return "success";
	}
	
	public String taskDel() {
		logger.info("进入删除任务信息");
		if (workTask != null && workTask.getId() != null && workTask.getId() != 0) {
			workTask = workTaskService.get(workTask.getId());
			workTaskService.del(workTask);
			super.setForwardUrl("/process/workProcess.do?action=taskList&workTask.processId="+workTask.getProcessId());
			return "success";
		} else {
			logger.error("任务信息为空");
			super.setErrorMessage("任务信息为空");
			return "failure";
		}
	}
	
	public String userList() {
		if (workTask != null && workTask.getId() != null && workTask.getId() != 0) {
			workTask = workTaskService.get(workTask.getId());
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		workProcessUserList = workProcessUserService.list(workTask.getProcessId(), workTask.getTaskId(), null, super.getPageBean());
		PageBean pageBean = workProcessUserService.getPageBean(workTask.getProcessId(), workTask.getTaskId(), null, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		return "userList";
	}
	public String userInput() {
		if (workTask != null && workTask.getId() != null && workTask.getId() != 0) {
			workTask = workTaskService.get(workTask.getId());
		}
		if (processUserTypeValue != null) {
			if (processUserTypeValue == ProcessUserType.USER.getValue()) {
				users = permissionService.list(null, null, null, null, null, null, super.getPageBean());
				HttpServletRequest request = ServletActionContext.getRequest();
				PageBean pageBean = permissionService.getPageBean(null, null, null, null, null, null, super.getPageBean());
				super.setPageString(PageUtil.getPageString(request, pageBean));
				super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
			}
			if (processUserTypeValue == ProcessUserType.ROLE.getValue()) {
				roles = permissionService.listRoles(null);
			}
			if (processUserTypeValue == ProcessUserType.ALL.getValue()) {
				logger.error("添加类型不能为全部");
				super.setErrorMessage("添加类型不能为全部");
				return "failure";
			}
		}
		return "userInputForm";
	}
	public String userManage() {
		if (workTask != null && workTask.getId() != null && workTask.getId() != 0) {
			workTask = workTaskService.get(workTask.getId());
		}
		if (userId != null && userId != 0) {
			User user = permissionService.getUser(userId);
			if (user == null || user.getId() == null || user.getId() == 0) {
				logger.error("用户或权限ID不能为空");
				super.setErrorMessage("用户或权限ID不能为空");
				return "failure";
			}
			List<WorkProcessUser> users = workProcessUserService.list(workTask.getProcessId(),workTask.getTaskId(), ProcessUserType.USER,user.getId());
			if(users == null || users.size() == 0){
				workProcessUser = new WorkProcessUser();
				workProcessUser.setName(user.getName());
				workProcessUser.setOperateId(user.getId());
				workProcessUser.setProcessId(workTask.getProcessId());
				workProcessUser.setProcessUserType(ProcessUserType.USER);
				workProcessUser.setTaskId(workTask.getTaskId());
				workProcessUserService.manage(workProcessUser);
			}else{
				logger.error("该用户已添加");
				super.setErrorMessage("该用户已添加");
				return "failure";
			}
		} 
		if (roleId != null && roleId != 0) {
			Role role = permissionService.getRole(roleId);
			if (role == null || role.getId() == null || role.getId() == 0) {
				logger.error("用户或权限ID不能为空");
				super.setErrorMessage("用户或权限ID不能为空");
				return "failure";
			}
			List<WorkProcessUser> users = workProcessUserService.list(workTask.getProcessId(),workTask.getTaskId(), ProcessUserType.ROLE,role.getId());
			if(users == null || users.size() == 0){
				workProcessUser = new WorkProcessUser();
				workProcessUser.setName(role.getName());
				workProcessUser.setOperateId(role.getId());
				workProcessUser.setProcessId(workTask.getProcessId());
				workProcessUser.setProcessUserType(ProcessUserType.ROLE);
				workProcessUser.setTaskId(workTask.getTaskId());
				workProcessUserService.manage(workProcessUser);
			}else{
				logger.error("该角色已添加");
				super.setErrorMessage("该角色已添加");
				return "failure";
			}
		} 
		super.setForwardUrl("/process/workProcess.do?action=userList&workTask.id="+workTask.getId());
		return "success";
	}
	
	public String userDel() {
		if (workProcessUser != null && workProcessUser.getId() != null && workProcessUser.getId() != 0) {
			workProcessUser = workProcessUserService.get(workProcessUser.getId());
			workProcessUserService.del(workProcessUser);
		}
		super.setForwardUrl("/process/workProcess.do?action=userList&workTask.id="+workTask.getId());
		return "success";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public List<WorkProcess> getProcessList() {
		return processList;
	}

	public void setProcessList(List<WorkProcess> processList) {
		this.processList = processList;
	}

	public WorkProcess getWorkProcess() {
		return workProcess;
	}

	public void setWorkProcess(WorkProcess workProcess) {
		this.workProcess = workProcess;
	}

	public WorkTask getWorkTask() {
		return workTask;
	}

	public void setWorkTask(WorkTask workTask) {
		this.workTask = workTask;
	}

	public WorkProcessService getWorkProcessService() {
		return workProcessService;
	}

	public void setWorkProcessService(WorkProcessService workProcessService) {
		this.workProcessService = workProcessService;
	}

	public List<WorkTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<WorkTask> taskList) {
		this.taskList = taskList;
	}

	public WorkTaskService getWorkTaskService() {
		return workTaskService;
	}

	public void setWorkTaskService(WorkTaskService workTaskService) {
		this.workTaskService = workTaskService;
	}

	public Integer getProcessUserTypeValue() {
		return processUserTypeValue;
	}

	public void setProcessUserTypeValue(Integer processUserTypeValue) {
		this.processUserTypeValue = processUserTypeValue;
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

	public void setWorkProcessUser(WorkProcessUser workProcessUser) {
		this.workProcessUser = workProcessUser;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
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
	
	public List<ProcessUserType> getProcessUserTypes() {
		return ProcessUserType.list;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Integer getTaskTypeValue() {
		return taskTypeValue;
	}

	public void setTaskTypeValue(Integer taskTypeValue) {
		this.taskTypeValue = taskTypeValue;
	}
	
	public List<TaskType> getTaskTypes() {
		return TaskType.list;
	}
}
