package web.action.process;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.AmountSetting;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.domain.process.WorkTask;
import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.service.process.AmountSettingService;
import com.lehecai.admin.web.service.process.WorkProcessUserService;
import com.lehecai.admin.web.utils.PageUtil;

public class AmountSettingAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

	private WorkProcessUser workProcessUser;
	private WorkTask workTask;
	private WorkProcessUserService workProcessUserService;
	private AmountSetting amountSetting;
	private AmountSettingService amountSettingService;
	private List<AmountSetting> amountSettingList;
	
	public String handle() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (workProcessUser == null || workProcessUser.getId() == null || workProcessUser.getId() == 0) {
			logger.error("工作流人员id为空");
			super.setErrorMessage("工作流人员id为空");
			return "failure";
		} 
		workProcessUser = workProcessUserService.get(workProcessUser.getId());
		if (workProcessUser.getProcessUserType().getValue() == ProcessUserType.ROLE.getValue()) {
			logger.error("不能给角色配置额度");
			super.setErrorMessage("不能给角色配置额度");
			return "failure";
		}
		amountSettingList = amountSettingService.updateAndQueryList(workProcessUser.getProcessId(), workProcessUser.getTaskId(), workProcessUser.getOperateId(), super.getPageBean());
		PageBean pageBean = amountSettingService.getPageBean(workProcessUser.getProcessId(), workProcessUser.getTaskId(), workProcessUser.getOperateId(), super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		return "list";
	}
	
	public String view () {
		if (amountSetting != null && amountSetting.getId() != null && amountSetting.getId() != 0) {
			amountSetting = amountSettingService.update(amountSetting.getId());
			return "view";
		} else {
			logger.error("额度配置id为空");
			super.setErrorMessage("额度配置id为空");
			return "failure";
		}
	}
	
	public String input() {
		if (workProcessUser == null || workProcessUser.getId() == null || workProcessUser.getId() == 0) {
			logger.error("工作流人员id为空");
			super.setErrorMessage("工作流人员id为空");
			return "failure";
		} 
		workProcessUser = workProcessUserService.get(workProcessUser.getId());
		return "inputForm";
	}
	
	public String manage() {
		logger.info("进入更新额度配置信息");
		if (amountSetting != null) {
			if (amountSetting != null) {
				if (amountSetting.getProcessId() == null || "".equals(amountSetting.getProcessId())) {
					logger.error("流程ID不能为空");
					super.setErrorMessage("流程ID不能为空");
					return "failure";
				}
				if (amountSetting.getTaskId() == null || "".equals(amountSetting.getTaskId())) {
					logger.error("任务ID不能为空");
					super.setErrorMessage("任务ID不能为空");
					return "failure";
				}
				if (amountSetting.getOperateId() == null || amountSetting.getOperateId() == 0) {
					logger.error("流程人员ID不能为空");
					super.setErrorMessage("流程人员ID不能为空");
					return "failure";
				}
				if (amountSetting.getCycleAmount() == null || amountSetting.getCycleAmount() == 0) {
					logger.error("周期额度不能为空");
					super.setErrorMessage("周期额度不能为空");
					return "failure";
				}
				if ((amountSetting.getCycleDay() == null || amountSetting.getCycleDay() == 0)
					&& (amountSetting.getCycleMonth() == null || amountSetting.getCycleMonth() == 0)
					&& (amountSetting.getCycleYear() == null || amountSetting.getCycleYear() == 0)) {
					logger.error("年月日周期不能都为空");
					super.setErrorMessage("年月日周期额度不能都为空");
					return "failure";
				}
				Date date = new Date();
				amountSetting.setBeginTime(date);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				if (amountSetting.getCycleYear() != null && amountSetting.getCycleYear() != 0) {
					cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + amountSetting.getCycleYear());
				}
				if (amountSetting.getCycleMonth() != null && amountSetting.getCycleMonth() != 0) {
					cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + amountSetting.getCycleMonth());
				}
				if (amountSetting.getCycleDay() != null && amountSetting.getCycleDay() != 0) {
					cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + amountSetting.getCycleDay());
				}
				amountSetting.setEndTime(cal.getTime());
				amountSetting.setRestAmount(amountSetting.getCycleAmount());
				amountSettingService.manage(amountSetting);
			}
		}
		super.setForwardUrl("/process/amountSetting.do?workProcessUser.id="+workProcessUser.getId()+"&workTask.id="+workTask.getId());
		return "success";
	}
	
	public String del() {
		logger.info("进入删除额度配置信息");
		if (amountSetting != null && amountSetting.getId() != null && amountSetting.getId() != 0) {
			amountSetting = amountSettingService.update(amountSetting.getId());
			amountSettingService.del(amountSetting);
			super.setForwardUrl("/process/amountSetting.do?workProcessUser.id="+workProcessUser.getId()+"&workTask.id="+workTask.getId());
			return "success";
		} else {
			logger.error("额度配置信息为空");
			super.setErrorMessage("额度配置信息为空");
			return "failure";
		}
	}

	public WorkProcessUser getWorkProcessUser() {
		return workProcessUser;
	}

	public void setWorkProcessUser(WorkProcessUser workProcessUser) {
		this.workProcessUser = workProcessUser;
	}

	public WorkTask getWorkTask() {
		return workTask;
	}

	public void setWorkTask(WorkTask workTask) {
		this.workTask = workTask;
	}

	public WorkProcessUserService getWorkProcessUserService() {
		return workProcessUserService;
	}

	public void setWorkProcessUserService(
			WorkProcessUserService workProcessUserService) {
		this.workProcessUserService = workProcessUserService;
	}

	public AmountSetting getAmountSetting() {
		return amountSetting;
	}

	public void setAmountSetting(AmountSetting amountSetting) {
		this.amountSetting = amountSetting;
	}

	public AmountSettingService getAmountSettingService() {
		return amountSettingService;
	}

	public void setAmountSettingService(AmountSettingService amountSettingService) {
		this.amountSettingService = amountSettingService;
	}

	public List<AmountSetting> getAmountSettingList() {
		return amountSettingList;
	}

	public void setAmountSettingList(List<AmountSetting> amountSettingList) {
		this.amountSettingList = amountSettingList;
	}
}
