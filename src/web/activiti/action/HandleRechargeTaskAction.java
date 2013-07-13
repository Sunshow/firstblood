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
import com.lehecai.admin.web.activiti.entity.RechargeTask;
import com.lehecai.admin.web.activiti.form.RechargeTaskForm;
import com.lehecai.admin.web.activiti.service.RechargeTaskService;
import com.lehecai.admin.web.activiti.task.recharge.ApproveRechargeTask;
import com.lehecai.admin.web.activiti.task.recharge.ClaimRechargeTask;
import com.lehecai.admin.web.activiti.task.recharge.ContinueRechargeTask;
import com.lehecai.admin.web.activiti.task.recharge.HandleRechargeTask;
import com.lehecai.admin.web.activiti.task.recharge.RejectRechargeTask;
import com.lehecai.admin.web.activiti.task.recharge.ReturnRechargeTask;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.ManuallyRechargeService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ManuallyRechargeType;
import com.lehecai.core.lottery.WalletType;
import com.opensymphony.xwork2.Action;

/**
 * @author qatang
 *
 */
public class HandleRechargeTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	private static final String RECHARGE_TASK_FORM = "rechargeTaskForm";
	
	@Autowired
	private RechargeTaskService rechargeTaskService;
	@Autowired
	private ManuallyRechargeService manuallyRechargeService;
	
	@Autowired
	private ClaimRechargeTask claimRechargeTask;
	@Autowired
	private HandleRechargeTask handleRechargeTask;
	@Autowired
	private ReturnRechargeTask returnRechargeTask;
	@Autowired
	private ContinueRechargeTask continueRechargeTask;
	@Autowired
	private ApproveRechargeTask approveRechargeTask;
	@Autowired
	private RejectRechargeTask rejectRechargeTask;
	
	private List<RechargeTaskForm> rechargeTaskFormList;
	
	private String taskId;
	private String processId;
	
	private double serviceCharge;//手续费
	private String memo;
	
	private boolean received;//是否到帐
	
	@SuppressWarnings("unchecked")
	public String handle() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		Map<String, Object> map = null;
		map = claimRechargeTask.listByRoleOrUser(userSessionBean.getRole().getId() + "", userSessionBean.getUser().getId() + "",super.getPageBean());
		PageBean pageBean = new PageBean();
		if(map == null || map.size() == 0){
			rechargeTaskFormList = new ArrayList<RechargeTaskForm>();
		}else{
			rechargeTaskFormList = (List<RechargeTaskForm>) map.get(Global.API_MAP_KEY_LIST);
			pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
		}
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		return "list";
	}
	
	public String claim() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		String message = "";
		boolean flag = true;
		if (rechargeTaskFormList == null || rechargeTaskFormList.size() == 0) {
			logger.error("认领充值工单失败，原因：rechargeTaskFormList为空");
			message = "认领充值工单失败，原因：rechargeTaskFormList为空";
			flag = false;
		}
		if (flag) {
			try {
				for (RechargeTaskForm rechargeTaskForm : rechargeTaskFormList) {
					Task task = handleRechargeTask.queryTask(rechargeTaskForm.getTaskId());
					if (task != null && task.getAssignee() == null) {
						claimRechargeTask.claim(rechargeTaskForm.getTaskId(), userSessionBean.getUser().getId() + "");
					} else {
						logger.error("批量认领失败,原因：编号" + task.getProcessInstanceId() + "已处理");
						if (!message.equals("")) {
							message += ",";
						}
						message += rechargeTaskForm.getCode();
					}
				}
				if (message.equals("")) {
					message = "批量认领成功，确定跳转至我的任务列表";
				} else {
					message = "批量认领中编号" + message + "已经认领，确定跳转至我的任务列表";
				}
			} catch (Exception e) {
				logger.error("批量认领失败，原因{}", e.getMessage());
				message = "批量认领失败，原因" + e.getMessage();
				flag = false;
			}
		}
		JSONObject object = new JSONObject();
		object.put("flag", flag);
		object.put("message", message);
		super.writeRs(response, object);
		return Action.NONE;
	}
	
	public String listUserTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		rechargeTaskFormList = handleRechargeTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "userTaskList";
	}
	
	public String startHandle() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		boolean flag = true;
		String message = "";
		if (rechargeTaskFormList == null || rechargeTaskFormList.size() == 0) {
			logger.error("处理充值工单失败，原因：rechargeTaskFormList为空");
			message = "处理汇款充值工单失败，原因：rechargeTaskFormList为空";
			flag = false;
		}
		if (flag) {
			try {
				for (RechargeTaskForm form : rechargeTaskFormList) {
					Task task = handleRechargeTask.queryTask(form.getTaskId());
					if (task != null) {
						RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)handleRechargeTask.getVariable(form.getProcessId(), RECHARGE_TASK_FORM);
						// 从财务人员配置中得到额度
						Double userAmount = handleRechargeTask.getRestAmount(userSessionBean.getUser().getId(),userSessionBean.getUser().getRoleID());
						userAmount = userAmount == null ? 0 : userAmount;
						logger.info("判断财务人员额度：userId={}的可用额度为{}", userSessionBean.getUser().getId(), userAmount);
						
						double rechargeAmount = rechargeTaskForm.getRechargeTask().getAmount();//用户充值金额
						logger.info("网站用户汇款金额：username={}的可用额度为{}", rechargeTaskForm.getRechargeTask().getUsername(), userAmount);
						
						boolean enough = false;
						if (userAmount >= rechargeAmount) {
							handleRechargeTask.minusAmount(userSessionBean.getUser().getId(), rechargeAmount);
							enough = true;
						}
						handleRechargeTask.completeAndClaimNextTask(enough, form.getTaskId(), form.getProcessId(), userSessionBean.getUser().getId() + "");
					} else {
						if (!message.equals("")) {
							message += ",";
						}
						message += form.getCode();
						logger.error("编码为" + form.getCode() + "任务已经处理");
					}
				}
				if (!message.equals("")) {
					message = "编码为" + message + "的任务已经处理";
				} else {
					message = "批量处理成功！";
				}
			} catch (Exception e) {
				logger.error("批量认领错误，原因：{}" , e.getMessage());
				message = "批量处理出现错误，原因：" + e.getMessage();
			}
		}
		JSONObject object = new JSONObject();
		object.put("message", message);
		super.writeRs(response, object);
		return listUserTask();
	}
	
	public String listReturnTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		rechargeTaskFormList = returnRechargeTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "returnTaskList";
	}
	
	public String returnTask() {
		if (taskId == null) {
			logger.error("由于额度不足退还充值工单失败，原因：taskId为空");
			super.setErrorMessage("由于额度不足退还充值工单失败，原因：taskId为空");
			super.setForwardUrl("/process/handleRechargeTask.do?action=listReturnTask");
			return "failure";
		}
		try{
			Task task = handleRechargeTask.queryTask(taskId);
			if (task == null) {
				logger.error("该任务已处理");
				super.setErrorMessage("该任务已处理");
				super.setForwardUrl("/process/handleRechargeTask.do?action=listReturnTask");
				return "failure";
			}
			logger.info("由于额度不足退还充值工单，taskId={}", taskId);
			returnRechargeTask.complete(taskId, null);
		} catch (Exception e) {
			logger.error("退还充值工单失败，原因：{}", e.getMessage());
			super.setErrorMessage("退还充值工单失败，原因：" + e.getMessage());
			super.setForwardUrl("/process/handleRechargeTask.do?action=listReturnTask");
			return "failure";
		}
		return listReturnTask();
	}
	
	public String listContinueTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		rechargeTaskFormList = continueRechargeTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "continueTaskList";
	}
	public String receiveMoney() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		if (taskId == null) {
			logger.error("汇款到帐处理失败，原因：taskId为空");
			super.setErrorMessage("汇款到帐处理失败，原因：taskId为空");
			return "failure";
		}
		
		try{
			Task task = handleRechargeTask.queryTask(taskId);
			if (task != null) {
				continueRechargeTask.completeAndClaimNextTask(true, taskId, processId, userSessionBean.getUser().getId() + "");
			} else {
				logger.error("该任务已处理");
				super.setErrorMessage("该任务已处理");
				super.setForwardUrl("/process/handleRechargeTask.do?action=listContinueTask");
				return "failure";
			}
		} catch (Exception e) {
			logger.error("汇款到帐处理失败");
			super.setErrorMessage("汇款到帐处理失败");
			return "failure";
		}
		return listContinueTask();
	}
	
	public String receiveNoMoney() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		if (taskId == null) {
			logger.error("汇款未到帐处理失败，原因：taskId为空");
			super.setErrorMessage("汇款未到帐处理失败，原因：taskId为空");
			return "failure";
		}
		
		try{
			Task task = handleRechargeTask.queryTask(taskId);
			if (task != null) {
				continueRechargeTask.completeAndClaimNextTask(false, taskId, processId, userSessionBean.getUser().getId() + "");
			} else {
				logger.error("该任务已处理");
				super.setErrorMessage("该任务已处理");
				super.setForwardUrl("/process/handleRechargeTask.do?action=listContinueTask");
				return "failure";
			}
		} catch (Exception e) {
			logger.error("汇款未到帐处理失败，原因：", e.getMessage());
			super.setErrorMessage("汇款未到帐处理失败，原因：" + e.getMessage());
			return "failure";
		}
		
		return listContinueTask();
	}
	
	public String listApprovedTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		rechargeTaskFormList = approveRechargeTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "approvedTaskList";
	}
	
	public String approveTaskInput() {
		if (taskId == null) {
			logger.error("汇款到帐处理失败，原因：taskId为空");
			super.setErrorMessage("汇款到帐处理失败，原因：taskId为空");
			return "failure";
		}
		
		return "approveTaskInput";
	}
	
	public String listRejectedTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		rechargeTaskFormList = rejectRechargeTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "rejectedTaskList";
	}
	
	public String rejectTaskInput() {
		if (taskId == null) {
			logger.error("汇款未到帐处理失败，原因：taskId为空");
			super.setErrorMessage("汇款未到帐处理失败，原因：taskId为空");
			return "failure";
		}
		
		return "rejectTaskInput";
	}
	
	public String approve() {
		Task task = approveRechargeTask.queryTask(taskId);
		if (task == null) {
			super.setErrorMessage("已完成充值，请勿重复操作");
			super.setForwardUrl("/process/handleRechargeTask.do?action=listApprovedTask");
			return "failure";
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)approveRechargeTask.getVariable(processId, "rechargeTaskForm");
		Long id = rechargeTaskForm.getRechargeTask().getId();
		RechargeTask rechargeTask = rechargeTaskService.get(id);
		rechargeTask.setHandleUser(userSessionBean.getUser().getId());
		rechargeTask.setServiceCharge(serviceCharge);
		rechargeTask.setMemo(memo);
		rechargeTaskService.merge(rechargeTask);
		
		// 调用充值接口，汇款金额 - 手续费 = 实际充值金额
		double amount = rechargeTask.getAmount() - serviceCharge;
		if (amount <= 0) {
			super.setErrorMessage("汇款金额 - 手续费 <= 0");
			return "failure";
		}
		try {
			manuallyRechargeService.recharge(rechargeTask.getUsername(), amount,
					null, null, userSessionBean.getUser(), WalletType.CASH, ManuallyRechargeType.RECHARGE_MANUALLY, rechargeTask.getRechargeBankId().toString(), null, null);
		} catch (ApiRemoteCallFailedException e) {
			handleRechargeTask.addAmount(userSessionBean.getUser().getId(), amount);
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			super.setForwardUrl("/process/handleRechargeTask.do?action=listApprovedTask");
			return "failure";
		}
		Map<String, Object> variables = new HashMap<String,Object>();
		variables.put("ifApprove", true);
		approveRechargeTask.complete(taskId, variables);
		super.setSuccessMessage("已成功向[" + rechargeTask.getUsername() + "]账户充值[" + amount + "]元");
		return "success";
	}
	
	public String reject() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		try {
			Task task = handleRechargeTask.queryTask(taskId);
			if (task != null) {
				RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)rejectRechargeTask.getVariable(processId, "rechargeTaskForm");
				Long id = rechargeTaskForm.getRechargeTask().getId();
				RechargeTask rechargeTask = rechargeTaskService.get(id);
				rechargeTask.setHandleUser(userSessionBean.getUser().getId());
				rechargeTask.setMemo(memo);
				handleRechargeTask.addAmount(userSessionBean.getUser().getId(), rechargeTask.getAmount());
				rechargeTaskService.merge(rechargeTask);
				Map<String, Object> variables = new HashMap<String,Object>();
				variables.put("ifApprove", false);
				variables.put("memo", memo);
				rejectRechargeTask.complete(taskId, variables);
			} else {
				logger.error("该任务已处理");
				super.setErrorMessage("该任务已处理");
				super.setForwardUrl("/process/handleRechargeTask.do?action=listRejectedTask");
				return "failure";
			}
		} catch (Exception e) {
			
		}
		return "success";
	}

	public List<RechargeTaskForm> getRechargeTaskFormList() {
		return rechargeTaskFormList;
	}

	public void setRechargeTaskFormList(List<RechargeTaskForm> rechargeTaskFormList) {
		this.rechargeTaskFormList = rechargeTaskFormList;
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

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public double getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
