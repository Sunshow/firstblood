package web.action.business;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.Mail;
import com.lehecai.admin.web.domain.business.SmsMailModel;
import com.lehecai.admin.web.enums.ModelType;
import com.lehecai.admin.web.enums.StatusType;
import com.lehecai.admin.web.enums.TextType;
import com.lehecai.admin.web.service.business.MailService;
import com.lehecai.admin.web.service.business.SmsMailModelService;
import com.lehecai.admin.web.utils.MailUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.admin.web.utils.StringUtil;
import com.lehecai.core.queue.QueueConstant;
import com.lehecai.core.queue.QueueTaskService;
import com.lehecai.core.queue.mail.MailQueueTask;

public class MailAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(MailAction.class);
	
	private MailService mailService;
	private SmsMailModelService smsMailModelService;
	
	private Mail mail;
	
	private List<Mail> mailList;
	private List<SmsMailModel> smsMailModelList;
	
	private String mailTo;
	private String subject;
	private Date beginDate;
	private Date endDate;
	private Integer textTypeId;
	private Integer statusTypeId;
	
	private QueueTaskService mailQueueTaskService;
	
	private String callbackUrl;
	
	public String handle() {
		logger.info("进入查询邮件列表");
		return "list";
	}
	
	public String query() {
		logger.info("进入查询邮件列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		mailList = mailService.list(mailTo, subject, statusTypeId, beginDate, endDate, super.getPageBean());
		PageBean pageBean = mailService.getPageBean(mailTo, subject, statusTypeId, beginDate, endDate, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		return "list";
	}
	
	public String manage() {
		logger.info("进入发送邮件");
		if (mail != null) {
			if (mail.getMailTo() == null || "".equals(mail.getMailTo())) {
				logger.error("接收人为空");
				super.setErrorMessage("接收人不能为空");
				return "failure";
			}
			if (mail.getSubject() == null || "".equals(mail.getSubject())) {
				logger.error("主题为空");
				super.setErrorMessage("主题不能为空！");
				return "failure";
			}
			if (textTypeId == null) {
				logger.error("文本格式为空");
				super.setErrorMessage("文本格式不能为空");
				return "failure";
			} else {
				if (textTypeId != TextType.PLAINTYPE.getValue() && textTypeId != TextType.HTMLTYPE.getValue()) {
					logger.error("无此文本格式!1:纯文本；2:富文本");
					super.setErrorMessage("无此文本格式!1:纯文本；2:富文本");
					return "failure";
				}
			}
			if (mail.getMailTo().indexOf(",") != -1) {
				boolean flag = true;
				String[] mails = StringUtil.split(mail.getMailTo(), ',');
				for(int i = 0;i < mails.length;i ++) {
					String mailStr = mails[i];
					if (!MailUtil.checkEmail(mailStr)) {
						flag = false;
						break;
					}
				}
				if (!flag) {
					logger.error("收件人格式错误!正确格式：a@a.com,b@b.com,c@c.com!");
					super.setErrorMessage("收件人格式错误!正确格式：a@a.com,b@b.com,c@c.com!");
					return "failure";
				}
			} else {
				if (!MailUtil.checkEmail(mail.getMailTo())) {
					logger.error("收件人格式错误!正确格式：a@a.com,b@b.com,c@c.com!");
					super.setErrorMessage("收件人格式错误!正确格式：a@a.com,b@b.com,c@c.com!");
					return "failure";
				}
			}
			mail.setTextType(TextType.getItem(textTypeId));
			mail.setStatus(StatusType.WAITINGTYPE);
			List<Mail> mails = mailService.manage(mail);
			
			//执行完数据库操作之后发送邮件
			for(Mail m: mails) {
				MailQueueTask task = new MailQueueTask();
				task.setMailto(m.getMailTo());
				task.setSubject(m.getSubject());
				if (m.getTextType().getValue() == TextType.PLAINTYPE.getValue()) {
					task.setText(m.getContent());
				} else {
					task.setHtmlText(m.getContent());
				}
				task.setCallback(callbackUrl + "mail.id=" + m.getId());
				int i = mailQueueTaskService.postToQueue(task);
				
				if (i == QueueConstant.RC_SUCCESS) {
					m.setStatus(StatusType.SENDINGTYPE);
					mailService.update(m);
				}
			}
		} else {
			logger.error("添加邮件错误，提交表单为空!");
			super.setErrorMessage("添加邮件错误，提交表单为空!");
			return "failure";
		}
		super.setForwardUrl("/business/mail.do");
		logger.info("发送邮件结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入邮件信息");
		if (mail != null) {
			if (mail.getId() != null) {			
				mail = mailService.get(mail.getId());
				textTypeId = mail.getTextType().getValue();
			}
		} else {
			mail = new Mail();
			textTypeId = TextType.HTMLTYPE.getValue();
		}
		statusTypeId = StatusType.SENDINGTYPE.getValue();
		try {
			smsMailModelList = smsMailModelService.list(null, null, ModelType.MAIL.getValue(),null, null, null, null, null, null);
		} catch (Exception e) {
			logger.error("邮件模板获取错误");
			super.setErrorMessage("邮件模板获取错误");
			return "failure";
		}
		return "inputForm";
	}
	
	public String retry() {
		logger.info("进入重试发送邮件");
		if (mail != null) {
			if (mail.getId() != null) {			
				mail = mailService.get(mail.getId());
				
				MailQueueTask task = new MailQueueTask();
				task.setMailto(mail.getMailTo());
				task.setSubject(mail.getSubject());
				if (mail.getTextType().getValue() == TextType.PLAINTYPE.getValue()) {
					task.setText(mail.getContent());
				} else {
					task.setHtmlText(mail.getContent());
				}
				task.setCallback(callbackUrl + "mail.id=" + mail.getId());
				int i = mailQueueTaskService.postToQueue(task);
				
				if (i == QueueConstant.RC_SUCCESS) {
					mail.setStatus(StatusType.SENDINGTYPE);
					mailService.update(mail);
				}
			}
		}
		super.setForwardUrl("/business/mail.do");
		logger.info("重试发送邮件结束");
		return "forward";
	}
	
	public String view() {
		logger.info("进入查看邮件详细信息");
		if (mail != null && mail.getId() != null) {
			mail = mailService.get(mail.getId());
		} else {
			logger.error("查看邮件详细信息，编码为空");
			super.setErrorMessage("查看邮件详细信息，编码为空");
			return "failure";
		}
		logger.info("查看邮件详细信息结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除邮件");
		if (mail != null && mail.getId() != null) {
			mail = mailService.get(mail.getId());
			mailService.del(mail);
		} else {
			logger.error("删除邮件，编码为空");
			super.setErrorMessage("删除邮件，编码为空");
			return "failure";
		}
		super.setForwardUrl("/business/mail.do");
		logger.info("删除邮件结束");
		return "forward";
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

	public List<Mail> getMailList() {
		return mailList;
	}

	public void setMailList(List<Mail> mailList) {
		this.mailList = mailList;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getBeginDate() {
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

	public QueueTaskService getMailQueueTaskService() {
		return mailQueueTaskService;
	}

	public void setMailQueueTaskService(QueueTaskService mailQueueTaskService) {
		this.mailQueueTaskService = mailQueueTaskService;
	}

	public Integer getTextTypeId() {
		return textTypeId;
	}

	public void setTextTypeId(Integer textTypeId) {
		this.textTypeId = textTypeId;
	}
	public List<TextType> getTextTypes() {
		return TextType.list;
	}
	public TextType getHtmlTextType() {
		return TextType.HTMLTYPE;
	}

	public Integer getStatusTypeId() {
		return statusTypeId;
	}

	public void setStatusTypeId(Integer statusTypeId) {
		this.statusTypeId = statusTypeId;
	}
	public List<StatusType> getStatusTypes() {
		return StatusType.list;
	}
	public StatusType getSuccessStatusType() {
		return StatusType.SUCCESSTYPE;
	}
	public StatusType getFailureStatusType() {
		return StatusType.FAILURETYPE;
	}
	public StatusType getWaitingStatusType() {
		return StatusType.WAITINGTYPE;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public SmsMailModelService getSmsMailModelService() {
		return smsMailModelService;
	}

	public void setSmsMailModelService(SmsMailModelService smsMailModelService) {
		this.smsMailModelService = smsMailModelService;
	}

	public List<SmsMailModel> getSmsMailModelList() {
		return smsMailModelList;
	}

	public void setSmsMailModelList(List<SmsMailModel> smsMailModelList) {
		this.smsMailModelList = smsMailModelList;
	}
}