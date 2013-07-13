package web.domain.process;
import java.io.Serializable;
import java.util.Date;

import com.lehecai.core.util.CoreNumberUtil;

public class AmountSetting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String processId;
	private String taskId;
	private Long operateId;
	private Integer cycleYear;
	private Integer cycleMonth;
	private Integer cycleDay;
	private Double cycleAmount;
	private Double restAmount;
	private Date beginTime;
	private Date endTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public Long getOperateId() {
		return operateId;
	}
	public void setOperateId(Long operateId) {
		this.operateId = operateId;
	}
	public Integer getCycleYear() {
		return cycleYear;
	}
	public void setCycleYear(Integer cycleYear) {
		this.cycleYear = cycleYear;
	}
	public Integer getCycleMonth() {
		return cycleMonth;
	}
	public void setCycleMonth(Integer cycleMonth) {
		this.cycleMonth = cycleMonth;
	}
	public Integer getCycleDay() {
		return cycleDay;
	}
	public void setCycleDay(Integer cycleDay) {
		this.cycleDay = cycleDay;
	}
	public Double getCycleAmount() {
		return cycleAmount;
	}
	public void setCycleAmount(Double cycleAmount) {
		this.cycleAmount = cycleAmount;
	}
	public Double getRestAmount() {
		return restAmount;
	}
	public void setRestAmount(Double restAmount) {
		this.restAmount = restAmount;
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
	public String getRestAmountStr() {
		return CoreNumberUtil.formatNumBy2Digits(restAmount);
	}
	public String getCycleAmountStr() {
		return CoreNumberUtil.formatNumBy2Digits(cycleAmount);
	}
}
