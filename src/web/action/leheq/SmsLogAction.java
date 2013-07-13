package web.action.leheq;

import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.leheq.SmsLog;
import com.lehecai.admin.web.service.leheq.SmsLogService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;

/**
 * 短信日志Action
 * @author yanweijie
 *
 */
public class SmsLogAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(SmsLogAction.class);
	
	private SmsLogService smsLogService;

	private List<SmsLog> smsLogList;
	private SmsLog smsLog;
	
	private Integer id;									//短信日志编号
	private String smsto;								//接收人手机号
	private Date beginSendTime;							//起始发送时间
	private Date endSendTime;							//截止发送时间
	private int result = YesNoStatus.ALL.getValue();	//发送结果
	
	/**
	 * 条件并分页查询短信日志
	 * @return
	 */
	public String handle() {
		return "list";
	}
	
	/**
	 * 条件并分页查询短信日志
	 * @return
	 */
	public String query() {
		if (beginSendTime != null && endSendTime != null) {
			if (beginSendTime.after(endSendTime)) {
				logger.error("起始时间大于截止时间");
				super.setErrorMessage("起始时间应该小于截止时间");
				return "failure";
			}
		}
		
		YesNoStatus yesNoStatus = YesNoStatus.getItem(result);
		
		smsLogList = smsLogService.findSmsLogList(smsto, beginSendTime, endSendTime, yesNoStatus, super.getPageBean());
		PageBean pageBean = smsLogService.getPageBean(smsto, beginSendTime, endSendTime, yesNoStatus, super.getPageBean());
		super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
		
		return "list";
	}
	
	/**
	 * 查询短信日志详情
	 * @return
	 */
	public String view () {
		if (id == null || id == 0) {
			logger.error("短信日志编号为空");
			super.setErrorMessage("短信日志编号为空");
			return "failure";
		}
		smsLog = smsLogService.get(id);
		
		return "view";
	}
	
	public SmsLogService getSmsLogService() {
		return smsLogService;
	}
	public void setSmsLogService(SmsLogService smsLogService) {
		this.smsLogService = smsLogService;
	}
	public List<SmsLog> getSmsLogList() {
		return smsLogList;
	}
	public void setSmsLogList(List<SmsLog> smsLogList) {
		this.smsLogList = smsLogList;
	}
	public SmsLog getSmsLog() {
		return smsLog;
	}
	public void setSmsLog(SmsLog smsLog) {
		this.smsLog = smsLog;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSmsto() {
		return smsto;
	}
	public void setSmsto(String smsto) {
		this.smsto = smsto;
	}
	public Date getBeginSendTime() {
		return beginSendTime;
	}
	public void setBeginSendTime(Date beginSendTime) {
		this.beginSendTime = beginSendTime;
	}
	public Date getEndSendTime() {
		return endSendTime;
	}
	public void setEndSendTime(Date endSendTime) {
		this.endSendTime = endSendTime;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
}
