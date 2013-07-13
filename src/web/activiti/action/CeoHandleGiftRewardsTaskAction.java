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
import com.lehecai.admin.web.activiti.entity.GiftRewardsTask;
import com.lehecai.admin.web.activiti.form.GiftRewardsTaskForm;
import com.lehecai.admin.web.activiti.service.GiftRewardsTaskService;
import com.lehecai.admin.web.activiti.task.giftrewards.CeoHandleGiftRewardsTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;

/**
 * @author chirowong
 * 处理彩金派送流程
 */
public class CeoHandleGiftRewardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private CeoHandleGiftRewardsTask ceoHandleGiftRewardsTask;
	@Autowired
	private GiftRewardsTaskService giftRewardsTaskService;
	
	private List<GiftRewardsTaskForm> giftRewardsTaskFormList;
	private GiftRewardsTaskForm giftRewardsTaskForm;
	private String taskId;
	private String processId;
	private Double amount;
	private String memo;
	
	public String handle() {
		logger.info("获取彩金赠送任务列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		giftRewardsTaskFormList = ceoHandleGiftRewardsTask.listByRoleOrUser(userSessionBean.getRole().getId() + "", userSessionBean.getUser().getId() + "");
		return "list";
	}
	
	public String claim() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		boolean flag = true;
		String message = "";
		if (giftRewardsTaskFormList == null || giftRewardsTaskFormList.size() == 0) {
			logger.error("认领彩金赠送工单失败，原因：giftRewardsTaskFormList为空");
			message = "认领彩金赠送工单失败，原因：giftRewardsTaskFormList为空";
			flag = false;
		}
		if (flag) {
			try {
				for (GiftRewardsTaskForm giftRewardsTaskForm : giftRewardsTaskFormList) {
					Task dbTask = ceoHandleGiftRewardsTask.queryTask(giftRewardsTaskForm.getTaskId());
					if (dbTask != null && dbTask.getAssignee() == null) {
						ceoHandleGiftRewardsTask.claim(giftRewardsTaskForm.getTaskId(), userSessionBean.getUser().getId() + "");
					} else {
						String code = giftRewardsTaskForm.getCode() == null ? "" : giftRewardsTaskForm.getCode();
						logger.error("认领彩金卡工单失败,原因：编码" + code + "已处理");
						if (!message.equals("")) {
							message += ",";
						}
						message += code;
					}
				}
				if (message.equals("")) {
					message = "批量认领成功，确定跳转至我的任务列表";
				} else {
					message = "认领彩金卡工单中编码" + message + "已认领，确定跳转至我的任务列表";
				}
			} catch(Exception e) {
				logger.error("批量认领失败，原因{}", e.getMessage());
				message = "批量认领失败，原因" + e.getMessage();
				flag = false;
			}
		}
		JSONObject object = new JSONObject();
		object.put("message", message);
		object.put("flag", flag);
		super.writeRs(response, object);
		return null;
	}
	
	public String listUserTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		giftRewardsTaskFormList = ceoHandleGiftRewardsTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "userTaskList";
	}
	
	public String agreeTask(){
		logger.info("通过彩金赠送任务");
		try {
			Task dbTask = ceoHandleGiftRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该彩金赠送工单已处理");
				super.setErrorMessage("该彩金赠送工单已处理");
				super.setForwardUrl("/process/ceoHandleGiftRewardsTask.do?action=listUserTask");
				return "failure";
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
			ceoHandleGiftRewardsTask.agreeTask(taskId,userSessionBean.getUser().getId());
			logger.info(userSessionBean.getUser().getName()+"通过彩金赠送");
			super.setSuccessMessage("通过彩金赠送！");
		} catch (Exception e) {
			logger.error("通过彩金赠送工单失败，原因{}", e.getMessage());
			super.setErrorMessage("通过彩金赠送工单失败，原因{}" + e.getMessage());
		}
		return "success";
	}
	
	public String disagreeTask(){
		return "disagree";
	}
	
	public String disagreeTaskReason(){
		logger.info("拒绝彩金赠送任务");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		try {
			Task dbTask = ceoHandleGiftRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该彩金赠送工单已处理");
				super.setErrorMessage("该彩金赠送工单已处理");
				super.setForwardUrl("/process/ceoHandleGiftRewardsTask.do?action=listUserTask");
				return "failure";
			}
			GiftRewardsTaskForm giftRewardsTaskForm = (GiftRewardsTaskForm)ceoHandleGiftRewardsTask.getVariable(processId, "giftRewardsTaskForm");
			Long id = giftRewardsTaskForm.getGiftRewardsTask().getId();
			GiftRewardsTask giftRewardsTask = giftRewardsTaskService.get(id);
			giftRewardsTask.setHandleUser(userSessionBean.getUser().getId());
			giftRewardsTask.setMemo(memo);
			giftRewardsTaskService.merge(giftRewardsTask);
			ceoHandleGiftRewardsTask.disAgreeTask(taskId);
			logger.info(userSessionBean.getUser().getName()+"未通过彩金赠送，原因："+memo);
			super.setSuccessMessage("未通过彩金赠送，原因："+memo);
		} catch (Exception e) {
			logger.error("拒绝彩金赠送工单失败，原因：{}" , e.getMessage());
			super.setErrorMessage("拒绝彩金赠送工单已处理，原因：" + e.getMessage());
			super.setForwardUrl("/process/ceoHandleGiftRewardsTask.do?action=listUserTask");
			return "failure";
		}
		return "success";
	}

	public CeoHandleGiftRewardsTask getCeoHandleGiftRewardsTask() {
		return ceoHandleGiftRewardsTask;
	}

	public void setCeoHandleGiftRewardsTask(
			CeoHandleGiftRewardsTask ceoHandleGiftRewardsTask) {
		this.ceoHandleGiftRewardsTask = ceoHandleGiftRewardsTask;
	}

	public List<GiftRewardsTaskForm> getGiftRewardsTaskFormList() {
		return giftRewardsTaskFormList;
	}

	public void setGiftRewardsTaskFormList(
			List<GiftRewardsTaskForm> giftRewardsTaskFormList) {
		this.giftRewardsTaskFormList = giftRewardsTaskFormList;
	}

	public GiftRewardsTaskForm getGiftRewardsTaskForm() {
		return giftRewardsTaskForm;
	}

	public void setGiftRewardsTaskForm(GiftRewardsTaskForm giftRewardsTaskForm) {
		this.giftRewardsTaskForm = giftRewardsTaskForm;
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
}
