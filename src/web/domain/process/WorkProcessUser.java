package web.domain.process;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.enums.WeekType;

public class WorkProcessUser implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String processId;
	private String taskId;
	private ProcessUserType processUserType;
	private String name;
	private Long operateId;
	private String tel;
	private String email;
	private Double amount;
	private String dutyDate;
	private String notifyTimes;
	private List<Integer> weekList;
	private boolean notify;
	private boolean emailNotify;
	private boolean smsNotify;
	private boolean messageNotify;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public ProcessUserType getProcessUserType() {
		return processUserType;
	}
	public void setProcessUserType(ProcessUserType processUserType) {
		this.processUserType = processUserType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getDutyDate() {
		return dutyDate;
	}
	public void setDutyDate(String dutyDate) {
		this.dutyDate = dutyDate;
	}
	public String getNotifyTimes() {
		return notifyTimes;
	}
	public void setNotifyTimes(String notifyTimes) {
		this.notifyTimes = notifyTimes;
	}
	public boolean isNotify() {
		return notify;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	public Long getOperateId() {
		return operateId;
	}
	public void setOperateId(Long operateId) {
		this.operateId = operateId;
	}
	public String getNotifyTimesStr() {
		String str = "";
		if (this.getNotifyTimes() != null && !"".equals(this.getNotifyTimes())){
			JSONArray jsonArray = JSONArray.fromObject(this.getNotifyTimes());
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject j = jsonArray.getJSONObject(i);
					str = str + j.getString("starttime") + "-";
					str = str + j.getString("endtime") + ",";
				}
				if (str.length() > 0) {
					str = str.substring(0, str.length() - 1);
				}
			}
		}
		return str;
	}
	public String getDutyDateStr() {
		String str = "";
		for (Integer i : this.getWeekList()) {
			str = str + WeekType.getItem(i).getName() + ",";
		}
		if (str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
	public boolean isEmailNotify() {
		return emailNotify;
	}
	public void setEmailNotify(boolean emailNotify) {
		this.emailNotify = emailNotify;
	}
	public boolean isMessageNotify() {
		return messageNotify;
	}
	public void setMessageNotify(boolean messageNotify) {
		this.messageNotify = messageNotify;
	}
	public boolean isSmsNotify() {
		return smsNotify;
	}
	public void setSmsNotify(boolean smsNotify) {
		this.smsNotify = smsNotify;
	}
	public List<Integer> getWeekList() {
		if (weekList == null && dutyDate != null && !"".equals(dutyDate)) {
			String [] strArray = dutyDate.split("\\,");
			weekList = new ArrayList<Integer>();
			for (int i = 0; i < strArray.length; i++) {
				weekList.add(Integer.parseInt(strArray[i]));
			}
		}
		return weekList;
	}
	public void setWeekList(List<Integer> weekList) {
		this.weekList = weekList;
	}
}
