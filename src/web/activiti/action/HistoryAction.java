/**
 * 
 */
package web.activiti.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.entity.AddedRewardsTask;
import com.lehecai.admin.web.activiti.entity.CommissionTask;
import com.lehecai.admin.web.activiti.entity.GiftCardsTask;
import com.lehecai.admin.web.activiti.entity.GiftRewardsTask;
import com.lehecai.admin.web.activiti.entity.HistoryQueryCondition;
import com.lehecai.admin.web.activiti.entity.HistoryQueryObject;
import com.lehecai.admin.web.activiti.entity.HistoryTask;
import com.lehecai.admin.web.activiti.entity.RechargeTask;
import com.lehecai.admin.web.activiti.service.AddedRewardsTaskService;
import com.lehecai.admin.web.activiti.service.CommissionTaskService;
import com.lehecai.admin.web.activiti.service.GiftCardsTaskService;
import com.lehecai.admin.web.activiti.service.GiftRewardsTaskService;
import com.lehecai.admin.web.activiti.service.RechargeTaskService;
import com.lehecai.admin.web.activiti.service.WorkFlowHistoryService;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.WorkProcess;
import com.lehecai.admin.web.service.process.WorkProcessService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.PageUtil;

/**
 * @author chirowong
 *
 */
public class HistoryAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private WorkFlowHistoryService workFlowHistoryService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private WorkProcessService workProcessService;
	
	@Autowired
	private AddedRewardsTaskService addedRewardsTaskService;
	@Autowired
	private GiftCardsTaskService giftCardsTaskService;
	@Autowired
	private GiftRewardsTaskService giftRewardsTaskService;
	@Autowired
	private RechargeTaskService rechargeTaskService;
	@Autowired
	private CommissionTaskService commissionTaskService;
	
	private List<HistoricProcessInstance> finishedProcessList;
	private String processDefinitionKey;
	private String processInstanceId;
	private String initiator;//发起人
	private Date beginTime;//开始时间
	private Date endTime;//结束时间
	private boolean finished;//是否完成
	private List<HistoryQueryObject> historyQueryObjectList;
	private List<HistoryTask> lehecaiHistoryTaskList;
	private AddedRewardsTask addedRewardsTask;
	private GiftCardsTask giftCardsTask;
	private GiftRewardsTask giftRewardsTask;
	private RechargeTask rechargeTask;
	private CommissionTask commissionTask;
	
	public String handle() {
		logger.info("工作流查询开始");
		HistoryQueryObject condition = new HistoryQueryObject();
		if(processDefinitionKey == null || processDefinitionKey.equals("")){
			processDefinitionKey = HistoryQueryCondition.ADDEDREWARDSROCESS.getProcessName();
		}
		condition.setProcessName(processDefinitionKey);
		condition.setProcessTable(HistoryQueryCondition.getTableName(processDefinitionKey));
		condition.setInitiator(initiator);
		historyQueryObjectList = workFlowHistoryService.queryHistory(condition,super.getPageBean(),finished,beginTime,endTime);
		PageBean pageBean = workFlowHistoryService.queryHistoryPageBean(condition,super.getPageBean(),finished,beginTime,endTime);
		HttpServletRequest request = ServletActionContext.getRequest();
		super.setPageString(PageUtil.getPageString(request, pageBean));
		logger.info("工作流查询结束");
		return "list";
	}
	
	public String showProcess(){
		List<HistoricTaskInstance> list3 = historyService.createHistoricTaskInstanceQuery().processDefinitionKey(processDefinitionKey).processInstanceId(processInstanceId).list();
		lehecaiHistoryTaskList = new ArrayList<HistoryTask>();
		for (HistoricTaskInstance h : list3) {
			HistoryTask lehecaiHistoryTask = new HistoryTask();
			lehecaiHistoryTask.setTaskName(h.getName());
			lehecaiHistoryTask.setStartTime(h.getStartTime());
			lehecaiHistoryTask.setEndTime(h.getEndTime());
			String assignee = h.getAssignee();
			if(assignee != null && !assignee.equals("")){
				lehecaiHistoryTask.setUserName(permissionService.getUser(Long.parseLong(assignee)).getName());
			}
			lehecaiHistoryTaskList.add(lehecaiHistoryTask);
		}
		if(processDefinitionKey.equals(HistoryQueryCondition.GIFTCARDSROCESS.getProcessName())){
			giftCardsTask = giftCardsTaskService.getByProcessId(processInstanceId);
		}else if(processDefinitionKey.equals(HistoryQueryCondition.GIFTREWARDSROCESS.getProcessName())){
			giftRewardsTask = giftRewardsTaskService.getByProcessId(processInstanceId);
		}else if(processDefinitionKey.equals(HistoryQueryCondition.ADDEDREWARDSROCESS.getProcessName())){
			addedRewardsTask = addedRewardsTaskService.getByProcessId(processInstanceId);
		}else if(processDefinitionKey.equals(HistoryQueryCondition.RECHARGEROCESS.getProcessName())){
			rechargeTask = rechargeTaskService.getByProcessId(processInstanceId);
		}else if(processDefinitionKey.equals(HistoryQueryCondition.COMMISSIONTASKPROCESS.getProcessName())){
			commissionTask = commissionTaskService.getByProcessId(processInstanceId);
		}
		return "viewTask";
	}

	public List<HistoricProcessInstance> getFinishedProcessList() {
		return finishedProcessList;
	}

	public void setFinishedProcessList(
			List<HistoricProcessInstance> finishedProcessList) {
		this.finishedProcessList = finishedProcessList;
	}

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}
	
	public List<WorkProcess> getWorkProcessList(){
		return workProcessService.list(null, null);
	}

	public List<HistoryQueryObject> getHistoryQueryObjectList() {
		return historyQueryObjectList;
	}

	public void setHistoryQueryObjectList(
			List<HistoryQueryObject> historyQueryObjectList) {
		this.historyQueryObjectList = historyQueryObjectList;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public List<HistoryTask> getLehecaiHistoryTaskList() {
		return lehecaiHistoryTaskList;
	}

	public void setLehecaiHistoryTaskList(
			List<HistoryTask> lehecaiHistoryTaskList) {
		this.lehecaiHistoryTaskList = lehecaiHistoryTaskList;
	}

	public AddedRewardsTask getAddedRewardsTask() {
		return addedRewardsTask;
	}

	public void setAddedRewardsTask(AddedRewardsTask addedRewardsTask) {
		this.addedRewardsTask = addedRewardsTask;
	}

	public GiftCardsTask getGiftCardsTask() {
		return giftCardsTask;
	}

	public void setGiftCardsTask(GiftCardsTask giftCardsTask) {
		this.giftCardsTask = giftCardsTask;
	}

	public GiftRewardsTask getGiftRewardsTask() {
		return giftRewardsTask;
	}

	public void setGiftRewardsTask(GiftRewardsTask giftRewardsTask) {
		this.giftRewardsTask = giftRewardsTask;
	}

	public RechargeTask getRechargeTask() {
		return rechargeTask;
	}

	public void setRechargeTask(RechargeTask rechargeTask) {
		this.rechargeTask = rechargeTask;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	//finishedProcessList = historyService.createHistoricProcessInstanceQuery().processDefinitionKey("").finished().list();
	/*for (HistoricProcessInstance h : list) {
		System.out.println(h.getBusinessKey());
		System.out.println(h.getProcessDefinitionId());
		System.out.println(h.getStartActivityId());
		System.out.println(h.getEndActivityId());
		System.out.println(h.getStartUserId());
		System.out.println(h.getStartTime());
		System.out.println(h.getEndTime());
		System.out.println(h.getId());
	}*/

	public CommissionTask getCommissionTask() {
		return commissionTask;
	}

	public void setCommissionTask(CommissionTask commissionTask) {
		this.commissionTask = commissionTask;
	}
	
		/*List<HistoricActivityInstance> list2 = historyService.createHistoricActivityInstanceQuery().activityType("userTask").finished().list();
		for (HistoricActivityInstance h : list2) {
			//RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)runtimeService.getVariable(h.getProcessInstanceId(), "rechargeTaskForm");
			System.out.println(h.getActivityId());
			System.out.println(h.getActivityName());
			System.out.println(h.getActivityType());
			System.out.println(h.getAssignee());
			System.out.println(h.getExecutionId());
			System.out.println(h.getProcessDefinitionId());
			System.out.println(h.getProcessInstanceId());
			System.out.println(h.getStartTime());
			System.out.println(h.getEndTime());
		}
		List<HistoricTaskInstance> list3 = historyService.createHistoricTaskInstanceQuery().processDefinitionKey("channelPayProcess").finished().list();
		for (HistoricTaskInstance h : list3) {
			System.out.println(h.getName());
			System.out.println(h.getOwner());
			System.out.println(h.getDescription());
			System.out.println(h.getAssignee());
			System.out.println(h.getExecutionId());
			System.out.println(h.getProcessDefinitionId());
			System.out.println(h.getProcessInstanceId());
			System.out.println(h.getStartTime());
			System.out.println(h.getEndTime());
		}
		/*List<HistoricDetail> list4 = historyService.createHistoricDetailQuery().list();
		for (HistoricDetail h : list4) {
			System.out.println(h.getActivityInstanceId());
			System.out.println(h.getExecutionId());
			System.out.println(h.getProcessInstanceId());
			System.out.println(h.getTaskId());
			System.out.println(h.getExecutionId());
		}
		return "list";*/
}
