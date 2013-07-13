/**
 * 
 */
package web.activiti.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.entity.GiftRewardsTask;
import com.lehecai.admin.web.activiti.form.GiftRewardsTaskForm;
import com.lehecai.admin.web.activiti.service.GiftRewardsTaskService;
import com.lehecai.admin.web.activiti.task.giftrewards.CheckGiftRewardsTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;

/**
 * @author chirowong
 * 处理彩金派送流程
 */
public class CheckGiftRewardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private CheckGiftRewardsTask checkGiftRewardsTask;
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
		giftRewardsTaskFormList = checkGiftRewardsTask.listByRoleOrUser(userSessionBean.getRole().getId() + "", userSessionBean.getUser().getId() + "");
		return "list";
	}
	
	public String finishTask(){
		logger.info("完成彩金赠送任务");
		try {
			Task task = checkGiftRewardsTask.queryTask(taskId);
			if (task == null) {
				logger.error("该彩金赠送任务已处理");
				super.setErrorMessage("该彩金赠送任务已处理");
				super.setForwardUrl("/process/checkGiftRewardsTask.do");
				return "failure";
			}
			GiftRewardsTaskForm giftRewardsTaskForm = (GiftRewardsTaskForm)checkGiftRewardsTask.getVariable(processId, "giftRewardsTaskForm");
			Long id = giftRewardsTaskForm.getGiftRewardsTask().getId();
			GiftRewardsTask giftRewardsTask = giftRewardsTaskService.get(id);
			giftRewardsTask.setFinished(true);
			giftRewardsTaskService.merge(giftRewardsTask);
			checkGiftRewardsTask.finishTask(taskId);
		} catch (Exception e) {
			logger.error("完成彩金赠送任务失败，原因：{}", e.getMessage());
			super.setErrorMessage("完成彩金赠送任务失败，原因：" + e.getMessage());
			super.setForwardUrl("/process/checkGiftRewardsTask.do");
			return "failure";
		}
		super.setForwardUrl("/process/checkGiftRewardsTask.do");
		return "forward";
	}
	
	public CheckGiftRewardsTask getCheckGiftRewardsTask() {
		return checkGiftRewardsTask;
	}

	public void setCheckGiftRewardsTask(CheckGiftRewardsTask checkGiftRewardsTask) {
		this.checkGiftRewardsTask = checkGiftRewardsTask;
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
