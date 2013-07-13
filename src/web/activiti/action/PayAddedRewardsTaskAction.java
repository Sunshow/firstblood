/**
 * 
 */
package web.activiti.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.entity.AddedRewardsTask;
import com.lehecai.admin.web.activiti.form.AddedRewardsTaskForm;
import com.lehecai.admin.web.activiti.service.AddedRewardsTaskService;
import com.lehecai.admin.web.activiti.task.recharge.ApproveRechargeTask;
import com.lehecai.admin.web.activiti.task.rewards.ApproveAddedRewardsTask;
import com.lehecai.admin.web.activiti.task.rewards.PayAddedRewardsTask;
import com.lehecai.admin.web.activiti.task.rewards.ReturnAddedRewardsTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.process.AmountSettingService;
import com.lehecai.admin.web.service.process.WorkProcessUserService;

/**
 * @author qatang
 *
 */
public class PayAddedRewardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	private static final String ADDED_REWARDS_PROCESS = "addedRewardsProcess";
	private static final String PAY_ADDED_REWARDS_TASK = "payAddedRewardsTask";
	private static final String ADDED_REWARDS_TASK_FORM = "addedRewardsTaskForm";
	@Autowired
	private PayAddedRewardsTask payAddedRewardsTask;
	@Autowired
	private ApproveAddedRewardsTask approveAddedRewardsTask;
	@Autowired
	private ApproveRechargeTask approveRechargeTask;
	@Autowired
	private ReturnAddedRewardsTask returnAddedRewardsTask;
	@Autowired
	private WorkProcessUserService workProcessUserService;
	@Autowired
	private AmountSettingService amountSettingService;
	@Autowired
	private AddedRewardsTaskService addedRewardsTaskService;
	
	private List<AddedRewardsTaskForm> addedRewardsTaskFormList;
	
	private String taskId;
	private String processId;
	private String memo;
	
	public String handle() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		addedRewardsTaskFormList = payAddedRewardsTask.listByRoleOrUser(userSessionBean.getRole().getId() + "", userSessionBean.getUser().getId() + "");
		return "listFinancialTask";
	}
	
	public String claimFinancialTask() {
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
					Task dbTask = payAddedRewardsTask.queryTask(addedRewardsTaskForm.getTaskId());
					
					if (dbTask != null && dbTask.getAssignee() == null) {
						payAddedRewardsTask.claim(addedRewardsTaskForm.getTaskId(), userSessionBean.getUser().getId() + "");
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
	
	
	public String listClaimedFinancialTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		addedRewardsTaskFormList = payAddedRewardsTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "listClaimedFinancialTask";
	}
	
	public String startHandle() {
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
			
				for (AddedRewardsTaskForm form : addedRewardsTaskFormList) {
					Task dbTask = payAddedRewardsTask.queryTask(form.getTaskId());
					if (dbTask == null) {
						if (!message.equals("")) {
							message += ",";
						}
						message += form.getPlanId();
						continue;
					}
					AddedRewardsTaskForm addedRewardsTaskForm = (AddedRewardsTaskForm)payAddedRewardsTask.getVariable(form.getProcessId(), ADDED_REWARDS_TASK_FORM);
					
					// 从财务人员配置中得到额度
					Double userAmount = amountSettingService.auditAmount(ADDED_REWARDS_PROCESS, PAY_ADDED_REWARDS_TASK, userSessionBean.getUser().getId(),userSessionBean.getUser().getRoleID());
					userAmount = userAmount == null ? 0.0D : userAmount;
					logger.info("判断财务人员额度：userId={}的可用额度为{}", userSessionBean.getUser().getId(), userAmount);
					
					double posttaxPrize = addedRewardsTaskForm.getAddedRewardsTask().getPosttaxPrize();//税后奖金
					logger.info("方案中奖税后金额：planId={}的，posttax={}", addedRewardsTaskForm.getAddedRewardsTask().getPlanId(), posttaxPrize);
					
					boolean enough = false;
					if (userAmount >= posttaxPrize) {
						amountSettingService.manageAmountMinus(ADDED_REWARDS_PROCESS, PAY_ADDED_REWARDS_TASK, userSessionBean.getUser().getId(), posttaxPrize);
						enough = true;
					}
					
					payAddedRewardsTask.completeAndClaimNextTask(enough, form.getTaskId(), form.getProcessId(), userSessionBean.getUser().getId() + "");
				}
				if (message.equals("")) {
					message = "批量处理成功！";
				} else {
					message = "认领补充派奖工单方案" + message + "已处理";
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
	
	public String listApprovedFinancialTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		addedRewardsTaskFormList = approveAddedRewardsTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "listApprovedFinancialTask";
	}
	
	public String agree(){
		logger.info("批准补充派奖任务");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		try {
			Task dbTask = payAddedRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该补充派奖任务已处理");
				super.setErrorMessage("该补充派奖任务已处理");
				super.setForwardUrl("/process/payAddedRewardsTask.do?action=listApprovedFinancialTask");
				return "failure";
			}
			approveRechargeTask.agreeTask(taskId,userSessionBean.getUser());
			logger.info(userSessionBean.getUser().getName()+"批准补充派奖");
			super.setSuccessMessage("批准补充派奖！");
		} catch (Exception e) {
			logger.error("该补充派奖任务批准失败，原因：{}", e.getMessage());
			super.setErrorMessage("该补充派奖任务批准失败，原因：" + e.getMessage());
			super.setForwardUrl("/process/payAddedRewardsTask.do?action=listApprovedFinancialTask");
			return "failure";
		}
		return "success";
	}
	
	public String disagree(){
		return "disagree";
	}
	
	public String disagreeTaskReason(){
		logger.info("拒绝补充派奖任务");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		try {
			Task dbTask = payAddedRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该补充派奖任务已处理");
				super.setErrorMessage("该补充派奖任务已处理");
				super.setForwardUrl("/process/payAddedRewardsTask.do?action=listApprovedFinancialTask");
				return "failure";
			}
			AddedRewardsTaskForm addedRewardsTaskForm = (AddedRewardsTaskForm)payAddedRewardsTask.getVariable(processId, "addedRewardsTaskForm");
			Long id = addedRewardsTaskForm.getAddedRewardsTask().getId();
			AddedRewardsTask addedRewardsTask = addedRewardsTaskService.get(id);
			addedRewardsTask.setHandleUser(userSessionBean.getUser().getId());
			addedRewardsTask.setMemo(memo);
			addedRewardsTaskService.merge(addedRewardsTask);
			approveRechargeTask.disAgreeTask(taskId);
			logger.info(userSessionBean.getUser().getName()+"未通过补充派奖任务，原因："+memo);
			super.setSuccessMessage("未通过补充派奖任务，原因："+memo);
		} catch (Exception e) {
			logger.error("该补充派奖任务处理失败，原因：{}", e.getMessage());
			super.setErrorMessage("该补充派奖任务处理失败，原因：" + e.getMessage());
			super.setForwardUrl("/process/payAddedRewardsTask.do?action=listApprovedFinancialTask");
			return "failure";
		}
		return "success";
	}
	
	public String listReturnFinancialTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		addedRewardsTaskFormList = returnAddedRewardsTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "listReturnFinancialTask";
	}
	
	public String reject() {
		if (taskId == null) {
			logger.error("补充派奖处理失败，原因：taskId为空");
			super.setErrorMessage("补充派奖处理失败，原因：taskId为空");
			return "failure";
		}
		try {
			Task dbTask = payAddedRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该补充派奖任务已处理");
				super.setErrorMessage("该补充派奖任务已处理");
				super.setForwardUrl("/process/payAddedRewardsTask.do?action=listReturnFinancialTask");
				return "failure";
			}
			returnAddedRewardsTask.complete(taskId, null);
		} catch (Exception e) {
			logger.error("该补充派奖任务处理失败，原因：{}", e.getMessage());
			super.setErrorMessage("该补充派奖任务处理失败，原因：" + e.getMessage());
			super.setForwardUrl("/process/payAddedRewardsTask.do?action=listReturnFinancialTask");
			return "failure";
		}
		super.setSuccessMessage("财务人员额度不足，需上级财务人员处理！");
		return "success";
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

	public WorkProcessUserService getWorkProcessUserService() {
		return workProcessUserService;
	}

	public void setWorkProcessUserService(
			WorkProcessUserService workProcessUserService) {
		this.workProcessUserService = workProcessUserService;
	}

	public AmountSettingService getAmountSettingService() {
		return amountSettingService;
	}

	public void setAmountSettingService(AmountSettingService amountSettingService) {
		this.amountSettingService = amountSettingService;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
