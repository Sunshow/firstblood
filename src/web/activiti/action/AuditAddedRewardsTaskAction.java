/**
 * 
 */
package web.activiti.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.form.AddedRewardsTaskForm;
import com.lehecai.admin.web.activiti.task.rewards.AuditAddedRewardsTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;

/**
 * @author qatang
 *
 */
public class AuditAddedRewardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private AuditAddedRewardsTask auditAddedRewardsTask;
	
	private List<AddedRewardsTaskForm> addedRewardsTaskFormList;
	
	private String taskId;
	private String processId;
	
	private String taskIds;
	private String processIds;
	
	private static boolean running = false;
	private static Object __lock__ = new Object();
	
	public String handle() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		addedRewardsTaskFormList = auditAddedRewardsTask.listByRoleOrUser(userSessionBean.getRole().getId() + "", userSessionBean.getUser().getId() + "");
		return "listSupervisorTask";
	}
	
	public String claimSupervisorTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		String message = "";
		boolean flag = true;
		if (addedRewardsTaskFormList == null || addedRewardsTaskFormList.size() == 0) {
			logger.error("认领补充派奖工单失败，原因：addedRewardsTaskFormList为空");
			message = "认领补充派奖工单失败，原因：addedRewardsTaskFormList为空";
			flag = false;
		}
		if (flag) {
			try {
				for (AddedRewardsTaskForm addedRewardsTaskForm : addedRewardsTaskFormList) {
					Task dbTask = auditAddedRewardsTask.queryTask(addedRewardsTaskForm.getTaskId());
					
					if (dbTask != null && dbTask.getAssignee() == null) {
						auditAddedRewardsTask.claim(addedRewardsTaskForm.getTaskId(), userSessionBean.getUser().getId() + "");
					} else {
						String planId = addedRewardsTaskForm.getPlanId() == null ? "" : addedRewardsTaskForm.getPlanId();
						logger.error("认领补充派奖工单失败,原因：方案" + planId + "已处理");
						if (!message.equals("")) {
							message += ",";
						}
						message += planId;
					}
				}
				if (message.equals("")) {
					message = "批量认领成功，确定跳转至我的任务列表";
				} else {
					message = "补充派奖工单中方案" + message + "已认领，确定跳转至我的任务列表";
				}
			} catch (Exception e) {
				logger.error("认领补充派奖工单失败，原因：{}", e.getMessage());
				message = "认领补充派奖工单失败" +  e.getMessage();
				flag = false;
			}
		}
		
		JSONObject object = new JSONObject();
		object.put("message", message);
		object.put("flag", flag);
		super.writeRs(response, object);
		return null;
	}
	
	
	public String listClaimedSupervisorTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		addedRewardsTaskFormList = auditAddedRewardsTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "listClaimedSupervisorTask";
	}
	
	public String approve() {
		if (taskId == null) {
			logger.error("补充派奖处理失败，原因：taskId为空");
			super.setErrorMessage("补充派奖处理失败，原因：taskId为空");
			return "failure";
		}
		try {
			Task dbTask = auditAddedRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该补充派奖任务已处理");
				super.setErrorMessage("该补充派奖任务已处理");
				super.setForwardUrl("/process/auditAddedRewardsTask.do?action=listClaimedSupervisorTask");
				return "failure";
			}
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("agree", true);
			auditAddedRewardsTask.complete(taskId, variables);
		} catch (Exception e) {
			logger.error("补充派奖处理失败，原因：{}" , e.getMessage());
			super.setErrorMessage("补充派奖处理失败，原因：" + e.getMessage());
			super.setForwardUrl("/process/auditAddedRewardsTask.do?action=listClaimedSupervisorTask");
			return "failure";
		}
		super.setSuccessMessage("补充派奖已批准，等待财务人员处理！");
		super.setForwardUrl("/process/auditAddedRewardsTask.do?action=listClaimedSupervisorTask");
		return "success";
	}
	
	public String batchApprove() {
		if (running) {
			return null;
		}
		synchronized (__lock__) {
			running = true;
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		String message = "";
		boolean flag = true;
		if (addedRewardsTaskFormList == null || addedRewardsTaskFormList.size() == 0) {
			logger.error("批量批准补充派奖工单失败，原因：addedRewardsTaskFormList为空");
			message = "批量批准补充派奖工单失败，原因：addedRewardsTaskFormList为空";
			flag = false;
		}
		List<String> dealtList = new ArrayList<String>();
		List<String> errorList = new ArrayList<String>();
		if (flag) {
			try {
				for (AddedRewardsTaskForm addedRewardsTaskForm : addedRewardsTaskFormList) {
					String taskId = addedRewardsTaskForm.getTaskId();
					try{
						if (!taskId.equals("")) {
							Task dbTask = auditAddedRewardsTask.queryTask(taskId);
							if (dbTask == null) {
								logger.error("该补充派奖任务已处理,taskId=" + taskId);
								dealtList.add(taskId);
								continue;
							}
							Map<String, Object> variables = new HashMap<String, Object>();
							variables.put("agree", true);
							auditAddedRewardsTask.complete(taskId, variables);
						} else {
							logger.error("taskId为空");
						}
					} catch (Exception e) {
						logger.error("taskId为" + taskId + "补充派奖任务处理失败，原因：", e.getMessage());
						errorList.add(taskId);
					}
				}
				//提示信息处理
				if (dealtList.size() == 0 && errorList.size() == 0) {
					message = "补充派奖批量同意已成功，等待财务人员处理！";
				} else {
					int num = 0;
					for (String id : dealtList) {
						if (num > 0) {
							message += ",";
						}
						message += id;
						num++;
					}
					if (num > 0) {
						message = "taskId为" + message + "的补充派奖任务已处理";
					}
					num = 0;
					for (String id : errorList) {
						if (num > 0) {
							message += ",";
						}
						message += id;
						num++;
					}
					if (num > 0) {
						message = "taskId为" + message + "的补充派奖任务处理失败";
					}
				}
			} catch (Exception e) {
				logger.error("批量同意补充派奖处理失败，原因：{}" , e.getMessage());
				flag = false;
			} finally {
				synchronized (__lock__) {
					running = false;
				}
			}
		}
		
		JSONObject object = new JSONObject();
		object.put("message", message);
		object.put("flag", flag);
		super.writeRs(response, object);
		return null;
	}
	
	public String reject() {
		if (taskId == null) {
			logger.error("补充派奖处理失败，原因：taskId为空");
			super.setErrorMessage("补充派奖处理失败，原因：taskId为空");
			return "failure";
		}
		try {
			Task dbTask = auditAddedRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该补充派奖任务已处理");
				super.setErrorMessage("该补充派奖任务已处理");
				super.setForwardUrl("/process/auditAddedRewardsTask.do?action=listClaimedSupervisorTask");
				return "failure";
			}
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("agree", false);
			auditAddedRewardsTask.complete(taskId, variables);
		} catch (Exception e) {
			logger.error("补充派奖处理失败，原因：{}" , e.getMessage());
			super.setErrorMessage("补充派奖处理失败，原因：" + e.getMessage());
			super.setForwardUrl("/process/auditAddedRewardsTask.do?action=listClaimedSupervisorTask");
			return "failure";
		}
		super.setSuccessMessage("补充派奖审核未通过，流程结束！");
		super.setForwardUrl("/process/auditAddedRewardsTask.do?action=listClaimedSupervisorTask");
		return "success";
	}
	
	public String batchReject() {
		if (running) {
			return null;
		}
		synchronized (__lock__) {
			running = true;
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		String message = "";
		boolean flag = true;
		if (addedRewardsTaskFormList == null || addedRewardsTaskFormList.size() == 0) {
			logger.error("批量拒绝补充派奖工单失败，原因：addedRewardsTaskFormList为空");
			message = "批量拒绝补充派奖工单失败，原因：addedRewardsTaskFormList为空";
			flag = false;
		}
		List<String> dealtList = new ArrayList<String>();
		List<String> errorList = new ArrayList<String>();
		if (flag) {
			try {
				for (AddedRewardsTaskForm addedRewardsTaskForm : addedRewardsTaskFormList) {
					String taskId = addedRewardsTaskForm.getTaskId();
					try {
						if (!taskId.equals("")) {
							Task dbTask = auditAddedRewardsTask.queryTask(taskId);
							if (dbTask == null) {
								logger.error("该补充派奖任务已处理,taskId=" + taskId);
								dealtList.add(taskId);
								continue;
							}
							Map<String, Object> variables = new HashMap<String, Object>();
							variables.put("agree", false);
							auditAddedRewardsTask.complete(taskId, variables);
						} else {
							logger.error("taskId为空");
						}
					} catch (Exception e) {
						errorList.add(taskId);
						logger.error("批量拒绝补充派奖处理失败，原因：{}" , e.getMessage());
						flag = false;
					}
				}
				//提示信息处理
				if (dealtList.size() == 0 && errorList.size() == 0) {
					message = "补充派奖审核未通过，流程结束！";
				} else {
					int num = 0;
					for (String id : dealtList) {
						if (num > 0) {
							message += ",";
						}
						message += id;
						num++;
					}
					if (num > 0) {
						message = "taskId为" + message + "的补充派奖任务已处理。";
					}
					num = 0;
					for (String id : errorList) {
						if (num > 0) {
							message += ",";
						}
						message += id;
						num++;
					}
					if (num > 0) {
						message = "taskId为" + message + "的补充派奖任务处理失败。";
					}
				}
			} catch (Exception e) {
				logger.error("批量拒绝补充派奖处理失败，原因：{}" , e.getMessage());
			} finally {
				synchronized (__lock__) {
					running = false;
				}
			}
		}
		JSONObject object = new JSONObject();
		object.put("message", message);
		object.put("flag", flag);
		super.writeRs(response, object);
		return null;
	}
	
	public List<AddedRewardsTaskForm> getAddedRewardsTaskFormList() {
		return addedRewardsTaskFormList;
	}

	public void setAddedRewardsTaskFormList(
			List<AddedRewardsTaskForm> addedRewardsTaskFormList) {
		this.addedRewardsTaskFormList = addedRewardsTaskFormList;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public void setProcessIds(String processIds) {
		this.processIds = processIds;
	}

	public String getProcessIds() {
		return processIds;
	}

	public void setTaskIds(String taskIds) {
		this.taskIds = taskIds;
	}

	public String getTaskIds() {
		return taskIds;
	}

}
