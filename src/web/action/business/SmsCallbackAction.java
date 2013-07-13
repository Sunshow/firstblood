package web.action.business;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.business.Sms;
import com.lehecai.admin.web.enums.StatusType;
import com.lehecai.admin.web.service.business.SmsService;
import com.lehecai.core.queue.QueueCallback;
import com.lehecai.core.queue.QueueConstant;
import com.opensymphony.xwork2.Action;

public class SmsCallbackAction extends BaseAction {

	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private SmsService smsService;
	
	private Sms sms;
	
	public String handle() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String leheqinfo = request.getParameter(QueueConstant.CALLBACK_PARAMETER_NAME);
		logger.info("leheqinfo:{}",leheqinfo);
		Long smsId = sms.getId();
		if(smsId != null && smsId != null){
			sms = smsService.get(smsId);
			if (sms == null) {
				logger.error("未找到指定的短信记录:{}", smsId);
				return Action.NONE;
			}
			QueueCallback queueCallback = new QueueCallback(leheqinfo);
			logger.info("queueCallback rc:{}",queueCallback.getRc());
			if(queueCallback != null && queueCallback.getRc() == QueueConstant.RC_FAILURE){
				sms.setStatus(StatusType.FAILURETYPE);
			}else{		
				sms.setStatus(StatusType.SUCCESSTYPE);
			}
			sms.setSendTime(new Date());
			smsService.update(sms);
		}
		return Action.NONE;
	}
	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public Sms getSms() {
		return sms;
	}

	public void setSms(Sms sms) {
		this.sms = sms;
	}
}
