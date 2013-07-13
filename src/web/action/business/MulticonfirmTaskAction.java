package web.action.business;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTaskStatus;
import com.lehecai.admin.web.service.multiconfirm.MulticonfirmService;
import com.lehecai.admin.web.utils.PageUtil;

public class MulticonfirmTaskAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5872878035173685419L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private MulticonfirmService multiconfirmService;
	
	private Long id;
	private String taskKey;
	private Long configId;
	private Integer taskStatusValue;
	private Date createTimeFrom;
	private Date createTimeTo;
	private Date timeoutTimeFrom;
	private Date timeoutTimeTo;
	
	private MulticonfirmTask task;
	private MulticonfirmRecord record;
	
	private List<MulticonfirmTask> taskList;
	private List<MulticonfirmRecord> recordLsit;
	
	
	public String handle() {
		return "list";
	}
	
	public String query() {
		logger.info("进入多次确认任务查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		MulticonfirmTaskStatus taskStatus = taskStatusValue != null && taskStatusValue != MulticonfirmTaskStatus.ALL.getValue() ? MulticonfirmTaskStatus.getItem(taskStatusValue) : null;
		taskList = multiconfirmService.getTaskList(id, configId, taskKey, taskStatus, createTimeFrom, createTimeTo, timeoutTimeFrom, timeoutTimeTo, super.getPageBean());
		
		if (taskList != null && taskList.size() > 0) {
			for (MulticonfirmTask t : taskList) {
				if(t.getTaskStatus().getValue() == MulticonfirmTaskStatus.OPEN.getValue() && multiconfirmService.auditTimeout(t)) {
					t.setTaskStatus(MulticonfirmTaskStatus.TIMEOUT);
				}
			}
		}
		
		PageBean pageBean = multiconfirmService.getTaskPageBean(id, configId, taskKey, taskStatus, createTimeFrom, createTimeTo, timeoutTimeFrom, timeoutTimeTo, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		logger.info("结束多次确认任务查询");
		return "list";
	}
	
	public String view() {
		logger.info("进入查看多次确认任务信息");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (task != null && task.getId() != null && task.getId() != 0) {
			task = multiconfirmService.getTask(task.getId());
		}
		recordLsit = multiconfirmService.getRecordList(task, super.getPageBean());
		
		PageBean pageBean = multiconfirmService.getRecordPageBean(task, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		
		logger.info("查看多次确认任务信息结束");
		return "view";
	}
	
	public MulticonfirmService getMulticonfirmService() {
		return multiconfirmService;
	}

	public void setMulticonfirmService(MulticonfirmService multiconfirmService) {
		this.multiconfirmService = multiconfirmService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskKey() {
		return taskKey;
	}

	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public Integer getTaskStatusValue() {
		return taskStatusValue;
	}

	public void setTaskStatusValue(Integer taskStatusValue) {
		this.taskStatusValue = taskStatusValue;
	}

	public MulticonfirmTask getTask() {
		return task;
	}

	public void setTask(MulticonfirmTask task) {
		this.task = task;
	}

	public List<MulticonfirmTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<MulticonfirmTask> taskList) {
		this.taskList = taskList;
	}

	public List<MulticonfirmRecord> getRecordLsit() {
		return recordLsit;
	}

	public void setRecordLsit(List<MulticonfirmRecord> recordLsit) {
		this.recordLsit = recordLsit;
	}

	public MulticonfirmRecord getRecord() {
		return record;
	}

	public void setRecord(MulticonfirmRecord record) {
		this.record = record;
	}
	
	public List<MulticonfirmTaskStatus> getTaskStatuses() {
		return MulticonfirmTaskStatus.list;
	}

	public Date getCreateTimeFrom() {
		return createTimeFrom;
	}

	public void setCreateTimeFrom(Date createTimeFrom) {
		this.createTimeFrom = createTimeFrom;
	}

	public Date getCreateTimeTo() {
		return createTimeTo;
	}

	public void setCreateTimeTo(Date createTimeTo) {
		this.createTimeTo = createTimeTo;
	}

	public Date getTimeoutTimeFrom() {
		return timeoutTimeFrom;
	}

	public void setTimeoutTimeFrom(Date timeoutTimeFrom) {
		this.timeoutTimeFrom = timeoutTimeFrom;
	}

	public Date getTimeoutTimeTo() {
		return timeoutTimeTo;
	}

	public void setTimeoutTimeTo(Date timeoutTimeTo) {
		this.timeoutTimeTo = timeoutTimeTo;
	}

}