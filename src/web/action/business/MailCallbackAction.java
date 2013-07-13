   package web.action.business;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.business.Mail;
import com.lehecai.admin.web.enums.StatusType;
import com.lehecai.admin.web.service.business.MailService;
import com.lehecai.core.queue.QueueCallback;
import com.lehecai.core.queue.QueueConstant;
import com.opensymphony.xwork2.Action;

public class MailCallbackAction extends BaseAction {

	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private MailService mailService;
	
	private Mail mail;

	public String handle(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String leheqinfo = request.getParameter(QueueConstant.CALLBACK_PARAMETER_NAME);
		logger.info("leheqinfo:{}",leheqinfo);
		if(mail != null && mail.getId() != null){
			mail = mailService.get(mail.getId());
			QueueCallback queueCallback = new QueueCallback(leheqinfo);
			logger.info("queueCallback rc:{}",queueCallback.getRc());
			if(queueCallback != null && queueCallback.getRc() == QueueConstant.RC_FAILURE){
				mail.setStatus(StatusType.FAILURETYPE);
			}else{		
				mail.setStatus(StatusType.SUCCESSTYPE);
			}
			mail.setSendTime(new Date());
			mailService.update(mail);
		}
		return Action.NONE;
	}
	
	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}
}
