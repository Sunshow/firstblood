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

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.entity.AddedRewardsTask;
import com.lehecai.admin.web.activiti.form.AddedRewardsTaskForm;
import com.lehecai.admin.web.activiti.service.AddedRewardsTaskService;
import com.lehecai.admin.web.activiti.task.rewards.StartAddedRewardsTask;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.PlanStatus;
import com.lehecai.core.lottery.ResultStatus;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

/**
 * @author qatang
 * @author chirowong
 */
public class CreateAddedRewardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private StartAddedRewardsTask startAddedRewardsTask;
	@Autowired
	private LotteryPlanService lotteryPlanService;
	@Autowired
	private AddedRewardsTaskService addedRewardsTaskService;
	
	private AddedRewardsTaskForm addedRewardsTaskForm;
	
	private List<AddedRewardsTaskForm> addedRewardsTaskFormList;
	
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("创建补充派奖工单");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		if (addedRewardsTaskForm == null) {
			logger.info("第一次进入查询界面");
			return "list";
		}
		
		if (addedRewardsTaskForm.getBeginDate() != null && addedRewardsTaskForm.getEndDate() != null) {
			if (!DateUtil.isSameMonth(addedRewardsTaskForm.getBeginDate(), addedRewardsTaskForm.getEndDate())) {
				logger.error("开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		
		AddedRewardsTask addedRewardsTask = addedRewardsTaskForm.getAddedRewardsTask();
		
		List<String> planStatusList = null;
		if (addedRewardsTask.getPlanStatus() != null && addedRewardsTask.getPlanStatus() != PlanStatus.ALL.getValue()) {
			planStatusList = new ArrayList<String>();
			planStatusList.add(String.valueOf(addedRewardsTask.getPlanStatus()));
		}
		LotteryType lt = addedRewardsTask.getLotteryType() == null ? null : LotteryType.getItem(addedRewardsTask.getLotteryType());
		PhaseType phaset = addedRewardsTask.getPhaseType() == null ? null : PhaseType.getItem(addedRewardsTask.getPhaseType());
		ResultStatus rs = addedRewardsTask.getResultStatus() == null ? null : ResultStatus.getItem(addedRewardsTask.getResultStatus());
		
		Map<String, Object> map;
		PageBean pb = super.getPageBean();
		pb.setPageSize(200);
		try {
			map = lotteryPlanService.getResult(addedRewardsTask.getUsername(),
					addedRewardsTask.getPlanId(), lt, phaset, addedRewardsTask.getPhase(), null, null,
					null, planStatusList, null, null,
					rs, null, null, null,
					addedRewardsTaskForm.getBeginDate(), addedRewardsTaskForm.getEndDate(),
					null, null, null, null, null, null, Plan.ORDER_CREATED_TIME, ApiConstant.API_REQUEST_ORDER_DESC, pb,null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询方案统计，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {			
			addedRewardsTaskForm.setPlans((List<Plan>)map.get(Global.API_MAP_KEY_LIST));
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		logger.info("查询方案统计结束");
		request.setAttribute("lotteryTypeId", lt.getValue());
		request.setAttribute("phaseTypeId", phaset.getValue());
		return "list";
	}
	
	public String check() {
		logger.info("判断重复方案补充派奖工单");
		if (addedRewardsTaskFormList == null || addedRewardsTaskFormList.size() == 0) {
			logger.error("判断重复方案补充派奖工单失败，原因：addedRewardsTaskFormList为空");
			super.setErrorMessage("判断重复方案补充派奖工单失败，原因：addedRewardsTaskFormList为空");
			return "list";
		}
		for (AddedRewardsTaskForm form : addedRewardsTaskFormList) {
			List<AddedRewardsTask> addedRewardsTaskList = addedRewardsTaskService.getByPlanId(form.getPlanId());
			if (addedRewardsTaskList != null && addedRewardsTaskList.size() > 0) {
				form.setRepeat(true);
			}
		}
		return "check";
	}
	
	public String manage() {
		logger.info("创建补充派奖工单，启动补充派奖工作流程");
		if (addedRewardsTaskFormList == null || addedRewardsTaskFormList.size() == 0) {
			logger.error("创建补充派奖工单失败，原因：addedRewardsTaskFormList为空");
			super.setErrorMessage("创建补充派奖工单失败，原因：addedRewardsTaskFormList为空");
			return "list";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		String message = "";
		boolean flag = true;
		try {
			for (AddedRewardsTaskForm form : addedRewardsTaskFormList) {
				form.getAddedRewardsTask().setInitiator(userSessionBean.getUser().getUserName());
				form.setResultStatus(form.getResultStatus());
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("addedRewardsTaskForm", form);
				
				ProcessInstance processInstance = startAddedRewardsTask.start(variables);
				Task currentTask = startAddedRewardsTask.getCurrentTask(processInstance.getProcessInstanceId());
				startAddedRewardsTask.claim(currentTask.getId(), userSessionBean.getUser().getId() + "");
			}
			if (message.equals("")) {
				message = "创建汇款充值工单成功！";
			}
		} catch (Exception e) {
			logger.error("创建汇款充值工单失败，原因：{}", e.getMessage());
			message = "创建汇款充值工单失败";
			flag = false;
		}
	    
		JSONObject obj = new JSONObject();
		obj.put("message", message);
		obj.put("flag", flag);
		super.writeRs(response, obj);
		return null;
	}

	public AddedRewardsTaskForm getAddedRewardsTaskForm() {
		return addedRewardsTaskForm;
	}

	public void setAddedRewardsTaskForm(AddedRewardsTaskForm addedRewardsTaskForm) {
		this.addedRewardsTaskForm = addedRewardsTaskForm;
	}

	public List<AddedRewardsTaskForm> getAddedRewardsTaskFormList() {
		return addedRewardsTaskFormList;
	}

	public void setAddedRewardsTaskFormList(
			List<AddedRewardsTaskForm> addedRewardsTaskFormList) {
		this.addedRewardsTaskFormList = addedRewardsTaskFormList;
	}
	public List<LotteryType> getLotteryTypes() {
		return OnSaleLotteryList.getForQuery();
	}
	public List<PhaseType> getPhaseTypes() {
		return PhaseType.getItems();
	}
	public List<PlanStatus> getPlanStatuses() {
		return PlanStatus.getItems();
	}

}
