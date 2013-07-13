/**
 * 
 */
package web.activiti.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.entity.AddedRewardsTask;
import com.lehecai.admin.web.activiti.form.AddedRewardsTaskForm;
import com.lehecai.admin.web.activiti.service.AddedRewardsTaskService;
import com.lehecai.admin.web.activiti.task.rewards.DrawAddedRewardsTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.ManuallyDrawService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.ResultStatus;

/**
 * @author qatang
 *
 */
public class HandleAddedRewardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	private final static String KEY_IS_WINNING = "isWinning";
	private final static String KEY_PRETAXPRIZE = "pretaxPrize";
	private final static String KEY_POSTTAXPRIZE = "posttaxPrize";
	
	private ManuallyDrawService manuallyDrawService;
	
	@Autowired
	private AddedRewardsTaskService addedRewardsTaskService;
	@Autowired
	private DrawAddedRewardsTask drawAddedRewardsTask;
	
	private List<AddedRewardsTaskForm> addedRewardsTaskFormList;
	
	//用于在开奖结果页面进行展示
	private List<AddedRewardsTaskForm> showAddedRewardsTaskFormList;
	private AddedRewardsTaskForm addedRewardsTaskForm;
	
	private String taskId;
	private String processId;
	
	private String memo;
	private Integer lotteryType;
	private String lotteryTypeValue;
	//中奖状态，用于查询
	private Integer result;
	//中奖状态，用于删除附带参数
	private String resultValue;
	//是否中奖，次数只会是-1,0,1，而不是中奖状态里面的各种值
	private Integer ifWinning;
	public List<LotteryType> getLotteryTypes(){
		return LotteryType.getItems();
	}
	
	public List<ResultStatus> getResultStatus(){
		return ResultStatus.getItems();
	}
	
	public List<YesNoStatus> getIfWinningList(){
		return YesNoStatus.getItemsForQuery();
	}
	
	public String handle() {
		logger.info("获取补充派奖任务列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		addedRewardsTaskFormList = drawAddedRewardsTask.listByAssignee(userSessionBean.getUser().getId() + "");
		if(lotteryType != null && lotteryType!= LotteryType.ALL.getValue()){
			List<AddedRewardsTaskForm> tempList = new ArrayList<AddedRewardsTaskForm>();
			for(AddedRewardsTaskForm ad : addedRewardsTaskFormList){
				int lotteryTypeValue = ad.getAddedRewardsTask().getLotteryType();
				if(lotteryType == lotteryTypeValue){
					tempList.add(ad);
				}
			}
			addedRewardsTaskFormList.clear();
			addedRewardsTaskFormList = tempList;
		}
		if(result != null && result!= ResultStatus.ALL.getValue()){
			List<AddedRewardsTaskForm> tempList = new ArrayList<AddedRewardsTaskForm>();
			for(AddedRewardsTaskForm ad : addedRewardsTaskFormList){
				int resultStatusValue = ad.getAddedRewardsTask().getResultStatus();
				if(result == resultStatusValue){
					tempList.add(ad);
				}
			}
			addedRewardsTaskFormList.clear();
			addedRewardsTaskFormList = tempList;
		}
		return "drawTaskList";
	}
	
	public String del() {
		logger.info("删除补充派奖工单开始");
		AddedRewardsTask addedRewardsTask = addedRewardsTaskForm.getAddedRewardsTask();
		String rewardsTaskId = addedRewardsTaskForm.getTaskId();
		String url = "/process/handleAddedRewardsTask.do?random=" + Calendar.getInstance().getTimeInMillis();
		if (lotteryTypeValue != null && !lotteryTypeValue.equals("") && Integer.parseInt(lotteryTypeValue)!= LotteryType.ALL.getValue()) {
			url += "&lotteryType="+lotteryTypeValue;
		}
		if (resultValue != null && !resultValue.equals("") && Integer.parseInt(resultValue)!= ResultStatus.ALL.getValue()) {
			url += "&result="+resultValue;
		}
		if (rewardsTaskId == null || rewardsTaskId.equals("")) {
			logger.error("删除补充派奖工单，任务ID为空");
			super.setErrorMessage("删除补充派奖工单，任务ID为空");
			super.setForwardUrl(url);
			return "failure";
		}
		try {
			Task dbTask = drawAddedRewardsTask.queryTask(rewardsTaskId);
			if (dbTask == null) {
				logger.error("该补充派奖工单已删除");
				super.setErrorMessage("该补充派奖工单已删除");
				super.setForwardUrl(url);
				return "failure";
			}
			if (!StringUtils.isEmpty(addedRewardsTask.getProcessId())) {
				drawAddedRewardsTask.deleteTask(addedRewardsTask.getProcessId());
			} else {
				logger.error("删除补充派奖工单，流程编码为空");
				super.setErrorMessage("删除补充派奖工单，流程编码为空");
				super.setForwardUrl(url);
				return "failure";
			}
		} catch (Exception e) {
			logger.error("删除补充派奖工单失败", e.getMessage());
			super.setErrorMessage("删除补充派奖工单失败");
			super.setForwardUrl(url);
			return "failure";
		}
		super.setForwardUrl(url);
		logger.info("删除补充派奖工单结束");
		return "forward";
	}
	
	public String batchdel() {
		logger.info("批量删除补充派奖工单开始");
		if (addedRewardsTaskFormList == null || addedRewardsTaskFormList.size() == 0) {
			logger.error("批量删除补充派奖任务开奖失败，原因：addedRewardsTaskFormList为空");
			super.setErrorMessage("批量删除补充派奖任务开奖失败，原因：addedRewardsTaskFormList为空");
			return handle();
		}
		String message = "";
		try {
			for (AddedRewardsTaskForm addedRewardsTaskForm : addedRewardsTaskFormList) {
				AddedRewardsTask addedRewardsTask = addedRewardsTaskForm.getAddedRewardsTask();
				Task dbTask = drawAddedRewardsTask.queryTask(addedRewardsTaskForm.getTaskId());
				if (dbTask != null) {
					if (!StringUtils.isEmpty(addedRewardsTask.getProcessId())) {
						drawAddedRewardsTask.deleteTask(addedRewardsTask.getProcessId());
					}
				} else {
					if (!message.equals("")) {
						message += ",";
					}
					message += addedRewardsTaskForm.getPlanId() == null ? "" : addedRewardsTaskForm.getPlanId();
				}
			}
		} catch (Exception e) {
			logger.error("批量删除补充派奖失败");
			super.setErrorMessage("批量删除补充派奖失败");
			return "failure";
		}
		String url = "/process/handleAddedRewardsTask.do";
		if(lotteryTypeValue != null && Integer.parseInt(lotteryTypeValue)!= LotteryType.ALL.getValue()){
			url = "/process/handleAddedRewardsTask.do?lotteryType="+lotteryTypeValue;
		}
		if (!message.equals("")) {
			message = "批量删除补充派奖工单结束,方案号为" + message + "补充派奖工单已删除";
			super.setErrorMessage(message);
			return handle();
		}
		super.setForwardUrl(url);
		logger.info("批量删除补充派奖工单结束");
		return "forward";
	}
	
	public String draw() {
		logger.info("对补充派奖任务逐一开奖");
		if (addedRewardsTaskFormList == null || addedRewardsTaskFormList.size() == 0) {
			logger.error("补充派奖任务开奖失败，原因：addedRewardsTaskFormList为空");
			super.setErrorMessage("补充派奖任务开奖失败，原因：addedRewardsTaskFormList为空");
			return handle();
		}
		showAddedRewardsTaskFormList = new ArrayList<AddedRewardsTaskForm>();
		List<AddedRewardsTaskForm> tempList = new ArrayList<AddedRewardsTaskForm>();
		for (AddedRewardsTaskForm form : addedRewardsTaskFormList) {
			String taskId = form.getTaskId();
			Task dbTask = drawAddedRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				continue;
			}
			AddedRewardsTaskForm theForm = (AddedRewardsTaskForm)drawAddedRewardsTask.getVariable(form.getProcessId(), "addedRewardsTaskForm");
			JSONObject obj = manuallyDrawService.draw(form.getAddedRewardsTask().getPlanId());
			if (obj == null) {
				form.setDrawSucc(false);
			} else {
				form.setDrawSucc(true);
				if (obj.getBoolean(KEY_IS_WINNING)) {
					form.getAddedRewardsTask().setResultStatus(ResultStatus.WON.getValue());
					form.getAddedRewardsTask().setResultStatusName("已中奖");
				} else {
					form.getAddedRewardsTask().setResultStatus(ResultStatus.NOT_WON.getValue());
					form.getAddedRewardsTask().setResultStatusName("未中奖");
				}
				form.getAddedRewardsTask().setPretaxPrize(obj.getDouble(KEY_PRETAXPRIZE));
				form.getAddedRewardsTask().setPosttaxPrize(obj.getDouble(KEY_POSTTAXPRIZE));
			}
			tempList.add(form);
			theForm.setDrawSucc(form.isDrawSucc());
			
			theForm.getAddedRewardsTask().setPretaxPrize(form.getAddedRewardsTask().getPretaxPrize());
			theForm.getAddedRewardsTask().setPosttaxPrize(form.getAddedRewardsTask().getPosttaxPrize());
			theForm.getAddedRewardsTask().setResultStatus(form.getAddedRewardsTask().getResultStatus());
			theForm.getAddedRewardsTask().setResultStatusName(form.getAddedRewardsTask().getResultStatusName());
			drawAddedRewardsTask.setVariable(form.getProcessId(), "addedRewardsTaskForm", theForm);
			
		}
		if (addedRewardsTaskFormList.size() != tempList.size()) {
			addedRewardsTaskFormList = tempList;
		}
		if (ifWinning != null && ifWinning != YesNoStatus.ALL.getValue() ) {
			for (AddedRewardsTaskForm form : addedRewardsTaskFormList) {
				if (form.isDrawSucc()){
					if (ifWinning == YesNoStatus.YES.getValue() && form.getAddedRewardsTask().getResultStatus() != null && form.getAddedRewardsTask().getResultStatus() == ResultStatus.WON.getValue()) {
						showAddedRewardsTaskFormList.add(form);
					} else if (ifWinning == YesNoStatus.NO.getValue() && form.getAddedRewardsTask().getResultStatus() != null && form.getAddedRewardsTask().getResultStatus() == ResultStatus.NOT_WON.getValue()) {
						showAddedRewardsTaskFormList.add(form);
					}
				}
			}
		} else {
			showAddedRewardsTaskFormList = addedRewardsTaskFormList;
		}
		return "drawResultList";
	}
	
	public String confirmInput() {
		if (processId == null) {
			logger.error("补充派奖手动开奖确认失败，原因：processId为空");
			super.setErrorMessage("补充派奖手动开奖确认失败，原因：processId为空");
			return "failure";
		}
		if (taskId == null) {
			logger.error("补充派奖手动开奖确认失败，原因：taskId为空");
			super.setErrorMessage("补充派奖手动开奖确认失败，原因：taskId为空");
			return "failure";
		}
		addedRewardsTaskForm = (AddedRewardsTaskForm)drawAddedRewardsTask.getVariable(processId, "addedRewardsTaskForm");
		return "drawConfirmInput";
	}
	
	public String confirm() {
		if (processId == null) {
			logger.error("补充派奖手动开奖确认失败，原因：processId为空");
			super.setErrorMessage("补充派奖手动开奖确认失败，原因：processId为空");
			return "failure";
		}
		if (taskId == null) {
			logger.error("补充派奖手动开奖确认失败，原因：taskId为空");
			super.setErrorMessage("补充派奖手动开奖确认失败，原因：taskId为空");
			return "failure";
		}
		try {
			Task dbTask = drawAddedRewardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该补充派奖手动开奖确认已处理");
				super.setErrorMessage("该补充派奖手动开奖确认已处理");
				super.setForwardUrl("/process/handleAddedRewardsTask.do");
				return "failure";
			}
			AddedRewardsTaskForm form = (AddedRewardsTaskForm)drawAddedRewardsTask.getVariable(processId, "addedRewardsTaskForm");
			AddedRewardsTask addedRewardsTask = form.getAddedRewardsTask();
			addedRewardsTask.setPretaxPrize(addedRewardsTaskForm.getAddedRewardsTask().getPretaxPrize());
			addedRewardsTask.setPosttaxPrize(addedRewardsTaskForm.getAddedRewardsTask().getPosttaxPrize());
			addedRewardsTask.setMemo(addedRewardsTaskForm.getAddedRewardsTask().getMemo());
			drawAddedRewardsTask.setVariable(processId, "addedRewardsTaskForm", form);
			
			AddedRewardsTask task = addedRewardsTaskService.get(addedRewardsTask.getId());
			task.setResultStatus(addedRewardsTask.getResultStatus());
			task.setResultStatusName(addedRewardsTask.getResultStatusName());
			task.setPretaxPrize(addedRewardsTask.getPretaxPrize());
			task.setPosttaxPrize(addedRewardsTask.getPosttaxPrize());
			task.setMemo(addedRewardsTask.getMemo());
			addedRewardsTaskService.merge(task);
			
			drawAddedRewardsTask.complete(taskId, null);
		} catch (Exception e) {
			logger.error("补充派奖手动开奖确认失败，原因：{}", e.getMessage());
			super.setErrorMessage("补充派奖手动开奖确认失败，原因：" + e.getMessage());
			return "failure";
		}
		super.setSuccessMessage("补充派奖手动开奖完毕，等待主管处理!");
		return "successDraw";
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public ManuallyDrawService getManuallyDrawService() {
		return manuallyDrawService;
	}

	public void setManuallyDrawService(ManuallyDrawService manuallyDrawService) {
		this.manuallyDrawService = manuallyDrawService;
	}

	public AddedRewardsTaskForm getAddedRewardsTaskForm() {
		return addedRewardsTaskForm;
	}

	public void setAddedRewardsTaskForm(AddedRewardsTaskForm addedRewardsTaskForm) {
		this.addedRewardsTaskForm = addedRewardsTaskForm;
	}

	public Integer getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(Integer lotteryType) {
		this.lotteryType = lotteryType;
	}

	public String getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(String lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getResult() {
		return result;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setIfWinning(Integer ifWinning) {
		this.ifWinning = ifWinning;
	}

	public Integer getIfWinning() {
		return ifWinning;
	}

	public void setShowAddedRewardsTaskFormList(
			List<AddedRewardsTaskForm> showAddedRewardsTaskFormList) {
		this.showAddedRewardsTaskFormList = showAddedRewardsTaskFormList;
	}

	public List<AddedRewardsTaskForm> getShowAddedRewardsTaskFormList() {
		return showAddedRewardsTaskFormList;
	}

	
}
