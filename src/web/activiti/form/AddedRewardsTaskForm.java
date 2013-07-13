/**
 * 
 */
package web.activiti.form;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.activiti.entity.AddedRewardsTask;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.lottery.Plan;

/**
 * @author qatang
 *
 */
public class AddedRewardsTaskForm implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AddedRewardsTask addedRewardsTask;
	
	private String taskId;
	private String processId;
	private String planId;
	private String nowDate;
	private Date beginDate;
	private Date endDate;
	private boolean repeat;
	//中奖状态
	private Integer resultStatus;
	private boolean drawSucc;
	
	private List<Plan> plans;
	
	private String totalAmount;
	private String totalPrizePostTax;

	public AddedRewardsTask getAddedRewardsTask() {
		if (addedRewardsTask == null) {
			addedRewardsTask = new AddedRewardsTask();
		}
		return addedRewardsTask;
	}

	public void setAddedRewardsTask(AddedRewardsTask addedRewardsTask) {
		this.addedRewardsTask = addedRewardsTask;
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

	public String getNowDate() {
		if (StringUtils.isEmpty(nowDate)) {
			nowDate = DateUtil.formatDate(new Date());
		}
		return nowDate;
	}

	public void setNowDate(String nowDate) {
		this.nowDate = nowDate;
	}

	public Date getBeginDate() {
		if (beginDate == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -7);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			beginDate = calendar.getTime();
		}
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTotalPrizePostTax() {
		return totalPrizePostTax;
	}

	public void setTotalPrizePostTax(String totalPrizePostTax) {
		this.totalPrizePostTax = totalPrizePostTax;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isDrawSucc() {
		return drawSucc;
	}

	public void setDrawSucc(boolean drawSucc) {
		this.drawSucc = drawSucc;
	}

	public void setResultStatus(Integer resultStatus) {
		this.resultStatus = resultStatus;
	}

	public Integer getResultStatus() {
		return resultStatus;
	}
	
}
