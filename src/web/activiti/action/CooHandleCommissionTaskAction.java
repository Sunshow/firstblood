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
import com.lehecai.admin.web.activiti.entity.CommissionTask;
import com.lehecai.admin.web.activiti.form.CommissionTaskForm;
import com.lehecai.admin.web.activiti.service.CommissionTaskService;
import com.lehecai.admin.web.activiti.task.commission.CooHandleCommissionTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.process.AmountSettingService;

public class CooHandleCommissionTaskAction extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private CooHandleCommissionTask cooHandleCommissionTask;
	@Autowired
	private AmountSettingService amountSettingService;
	@Autowired
	private CommissionTaskService commissionTaskService;
	
	private List<CommissionTaskForm> commissionTaskFormList;
	private CommissionTaskForm commissionTaskForm;
	private String taskId;
	private String processId;
	private Double amount;
	private String memo;
	private long id;
	
	public String handle() {
		logger.info("获取佣金派发任务列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		commissionTaskFormList = cooHandleCommissionTask.listByRoleOrUser(userSessionBean.getRole().getId() + "", userSessionBean.getUser().getId() + "");
		return "list";
	}
	
	public String listUserTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		commissionTaskFormList = cooHandleCommissionTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "userTaskList";
	}
	
	public String agreeTask(){
		logger.info("通过佣金派发任务");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		logger.info("计算操作人员额度！");
		Double rechargeAmount = amountSettingService.auditAmount(CooHandleCommissionTask.COMMISSION_PROCESS, CooHandleCommissionTask.COO_HANDLE_COMMISSION_TASK, userSessionBean.getUser().getId(),userSessionBean.getUser().getRoleID());
		rechargeAmount = rechargeAmount == null?0.0D:rechargeAmount;
		boolean enough = false;
		if (amount <= rechargeAmount) {
			enough = true;
		}
		Task task = cooHandleCommissionTask.queryTask(taskId);
		if(task != null){
			try{
				cooHandleCommissionTask.agreeTask(enough, taskId, userSessionBean.getUser().getId());
			}catch(Exception e){
				super.setErrorMessage("通过该任务时，发生异常，请联系管理员。");
				super.setForwardUrl("/process/cooHandleCommissionTask.do?action=listUserTask");
				return "failure";
			}
			if(enough){
				logger.info(userSessionBean.getUser().getName()+"通过佣金派发任务！");
				super.setSuccessMessage("通过佣金派发任务！");
			}else{
				logger.info(userSessionBean.getUser().getName()+"通过佣金派发任务,但您的可用额度不够，系统将该工单自动提交给上一级主管！");
				super.setSuccessMessage("通过佣金派发任务,但您的可用额度不够，系统将该工单自动提交给上一级主管！");
			}
		}else{
			super.setErrorMessage("该任务已处理。");
			super.setForwardUrl("/process/cooHandleCommissionTask.do?action=listUserTask");
			return "failure";
		}
		super.setForwardUrl("/process/cooHandleCommissionTask.do?action=listUserTask");
		return "success";
	}

	public String disagreeTaskReason(){
		return "disagree";
	}
	
	public String disagreeTask(){
		logger.info("拒绝佣金派发");
		try {
			Task dbTask = cooHandleCommissionTask.queryTask(taskId);
			if (dbTask == null) {
				super.setErrorMessage("该佣金派发任务已处理");
				super.setForwardUrl("/process/cooHandleCommissionTask.do?action=listUserTask");
				return "failure";
			}
			CommissionTask task = commissionTaskService.get(id);
			task.setMemo(memo);
			commissionTaskService.merge(task);
			cooHandleCommissionTask.disAgreeTask(taskId);
		} catch (Exception e) {
			logger.error("拒绝佣金派发失败{}", e.getMessage());
			super.setErrorMessage("拒绝佣金派发失败" + e.getMessage());
			super.setForwardUrl("/process/cooHandleCommissionTask.do?action=listUserTask");
			return "failure";
		}
		super.setSuccessMessage("拒绝佣金派发，原因："+memo);
		super.setForwardUrl("/process/cooHandleCommissionTask.do?action=listUserTask");
		return "success";

	}
	
	public String claim() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		String message = "";
		boolean flag = true;
		if (commissionTaskFormList == null || commissionTaskFormList.size() == 0) {
			logger.error("认领佣金派发工单失败，原因：commissionTaskFormList为空");
			message = "认领佣金派发工单失败，原因：commissionTaskFormList为空";
			flag = false;
		}
		if (flag) {
			try {
				for (CommissionTaskForm form : commissionTaskFormList) {
					Task dbTask = cooHandleCommissionTask.queryTask(form.getTaskId());
					if (dbTask != null && dbTask.getAssignee() == null) {
						cooHandleCommissionTask.claim(form.getTaskId(), userSessionBean.getUser().getId() + "");
					} else {
						String userName = form.getUserName() == null ? "" : form.getUserName();
						logger.error("认领佣金派发工单失败,原因：用户名" + userName + "已处理");
						if (!message.equals("")) {
							message += ",";
						}
						message += userName;
					}
				}
				if (message.equals("")) {
					message = "批量认领成功，确定跳转至我的任务列表";
				} else {
					message = "佣金派发工单中用户" + message + "已认领，确定跳转至我的任务列表";
				}
			} catch (Exception e) {
				logger.error("认领佣金派发工单失败，原因：{}", e.getMessage());
				message = "认领佣金派发工单失败";
				flag = false;
			}
		}
		JSONObject object = new JSONObject();
		object.put("flag", flag);
		object.put("message", message);
		super.writeRs(response, object);
		return null;
	}
	
	public List<CommissionTaskForm> getCommissionTaskFormList() {
		return commissionTaskFormList;
	}

	public void setCommissionTaskFormList(
			List<CommissionTaskForm> commissionTaskFormList) {
		this.commissionTaskFormList = commissionTaskFormList;
	}

	public CommissionTaskForm getCommissionTaskForm() {
		return commissionTaskForm;
	}

	public void setCommissionTaskForm(CommissionTaskForm commissionTaskForm) {
		this.commissionTaskForm = commissionTaskForm;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
