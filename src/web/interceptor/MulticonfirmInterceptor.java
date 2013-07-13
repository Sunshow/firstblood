package web.interceptor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.business.Mail;
import com.lehecai.admin.web.domain.business.Sms;
import com.lehecai.admin.web.enums.StatusType;
import com.lehecai.admin.web.enums.TextType;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmSign;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTaskStatus;
import com.lehecai.admin.web.multiconfirm.confirm.IMulticonfirmConfirm;
import com.lehecai.admin.web.multiconfirm.confirm.MulticonfirmConfirmFactory;
import com.lehecai.admin.web.multiconfirm.param.IMulticonfirmParam;
import com.lehecai.admin.web.multiconfirm.param.MulticonfirmParamFactory;
import com.lehecai.admin.web.service.business.MailService;
import com.lehecai.admin.web.service.business.SmsService;
import com.lehecai.admin.web.service.multiconfirm.MulticonfirmService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.MailUtil;
import com.lehecai.admin.web.utils.StringUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.queue.QueueConstant;
import com.lehecai.core.queue.QueueTaskService;
import com.lehecai.core.queue.mail.MailQueueTask;
import com.lehecai.core.queue.sms.SmsQueueTask;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreHttpUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class MulticonfirmInterceptor extends AbstractInterceptor {

	/**
	 * 多次确认拦截器接口
	 */
	private static final long serialVersionUID = 3218305391409219329L;
	
	private final Logger logger = LoggerFactory.getLogger(MulticonfirmInterceptor.class);
	
	private MulticonfirmConfirmFactory confirmFactory;
	private MulticonfirmParamFactory paramFactory;
	
	private String callbackUrlMail;
	private String callbackUrlSms;
	
	private PermissionService permissionService;
	private MailService mailService;
	private QueueTaskService mailQueueTaskService;
	private SmsService smsService;
	private QueueTaskService smsQueueTaskService;
	private MulticonfirmService multiconfirmService;
	
	public String intercept(ActionInvocation invocation) throws Exception{
		
		StringBuffer message = new StringBuffer();
		
		logger.info("Enter MutipleConfirmInterceptor");
		message.append("进入多次确认任务<br />");
		ActionContext ac =  invocation.getInvocationContext();
		
		String actionName = ac.getName();
		Map<String, Object> paramMap = ac.getParameters();
		String methodName = "";
		if (paramMap.get("action") != null) {
			methodName = ((String[]) paramMap.get("action"))[0];
		}
		if (methodName == null || methodName.equals("") || methodName.equals("excute")) {
			methodName = "handle";
		}
		logger.info("ClassName:{}", actionName);
		logger.info("MethodNAme:{}", methodName);
		logger.info("Parameters:{}", paramMap.toString());
		
		String configDefaultString = MulticonfirmSign.getDefaultConfigString(actionName, methodName);
		MulticonfirmConfig multiconfirmConfig = multiconfirmService.getConfig(configDefaultString);
		if (multiconfirmConfig == null) {
			logger.info("not found multiconfirmConfig!");
			logger.info("Exit MutipleConfirmInterceptor");
			return invocation.invoke();
		}
		
		String configString = MulticonfirmSign.getConfigString(actionName, methodName, paramMap, multiconfirmConfig);
		
		MulticonfirmConfig multiconfirmConfigTmp = multiconfirmService.getConfig(configString);
		if (multiconfirmConfigTmp != null) {
			multiconfirmConfig = multiconfirmConfigTmp;
		}
		logger.info("ConfigKey:{}", multiconfirmConfig.getConfigKey());
		
		String xmlId = MulticonfirmSign.getXMLId(actionName, methodName);
		IMulticonfirmParam param = paramFactory.getHandler(xmlId);
		UserSessionBean userSessionBean = (UserSessionBean) ac.getSession().get(Global.USER_SESSION);
		if (param == null) {
			logger.info("not found multiconfirmConfig!");
			sendSmsEmail(multiconfirmConfig, null, null, userSessionBean);
			logger.info("Exit MutipleConfirmInterceptor");
			return invocation.invoke();
		}
		
		StringBuffer taskMessage = new StringBuffer();
		MulticonfirmTask task = param.getTask(multiconfirmConfig, paramMap, MulticonfirmTaskStatus.OPEN, taskMessage);
		message.append(taskMessage);
		logger.info("TaskKey:{}", task.getTaskKey());
		
		if (userSessionBean == null || userSessionBean.getUser() == null) {
			logger.info("userSession or user is null!");
			logger.info("Exit MutipleConfirmInterceptor");
			BaseAction basetion = (BaseAction) invocation.getAction();
			basetion.setErrorMessage("您的session丢失，请重新登录");
			return "index";
		}
		
		MulticonfirmRecord record = param.manageRecord(task, paramMap, userSessionBean.getUser().getId());
		if (record == null || record.getResult() == null || record.getResult().equals("")) {
			logger.info("记录结果输入错误");
			message.append("记录结果输入错误");
			ac.put("multiconfirmMessage", message.toString());
			return "multiconfirm";
		}
		
		IMulticonfirmConfirm confirm = confirmFactory.getHandler(xmlId);
		if (confirm == null) {
			logger.info("not found confirm class!");
			logger.info("Exit MutipleConfirmInterceptor");
			message.append("未找到对应的确认信息实现类");
			ac.put("multiconfirmMessage", message.toString());
			return "multiconfirm";
		}
		
		StringBuffer auditMessage = new StringBuffer();
		boolean auditFlag = confirm.auditConfirm(task, auditMessage);
		message.append(auditMessage);
		if (!auditFlag) {
			logger.info("task needn't confirm!");
			logger.info("Exit MutipleConfirmInterceptor");
			ac.put("multiconfirmMessage", message.toString());
			return "multiconfirm";
		} 
		
		
		if (confirm.comfirm(task, message)) {
			logger.info("task confirm complate!");
			sendSmsEmail(multiconfirmConfig, taskMessage, auditMessage, userSessionBean);
			logger.info("Exit MutipleConfirmInterceptor");
			ac.put("multiconfirmMessage", message.toString());
			return invocation.invoke();
		} else {
			logger.info("task confirm failure!");
			logger.info("Exit MutipleConfirmInterceptor");
			ac.put("multiconfirmMessage", message.toString());
			return "multiconfirm";
		}
		
	}
	
	private void sendSmsEmail(MulticonfirmConfig multiconfirmConfig, StringBuffer taskMessage, StringBuffer auditMessage, UserSessionBean userSessionBean) {
		logger.info("send Email!");
		if (multiconfirmConfig.getIsEmail().getValue() == YesNoStatus.YES.getValue()) {
			//发送邮件
			
			HttpServletRequest request=ServletActionContext.getRequest();
			String[] ipArray = CoreHttpUtils.getClientIPArray(request);
			String ip = "";
			for(int i = 0; i < ipArray.length; i++) {
				ip = ip + ipArray[i] + ",";
			}
			if (ip != null && !ip.equals("")) {
				ip = ip.substring(0, ip.length() - 1);
			}
			
			StringBuffer emailStr = new StringBuffer();  	//邮件内容
			emailStr.append("用户：[").append(userSessionBean.getUser().getName()).append("]IP:[").append(ip).append("]于[").append(CoreDateUtils.formatDate(new Date(), CoreDateUtils.DATETIME))
				.append("]对配置名为:[").append(multiconfirmConfig.getConfigName()).append("]的功能进行了操作<br />")
				.append("任务信息:<br />").append(taskMessage).append("<br />输入信息").append(auditMessage);
			
			boolean emailFlag = true;
			Mail mail = new Mail();
			if (emailFlag && multiconfirmConfig.getEmailAddress() != null && !multiconfirmConfig.getEmailAddress().equals("")) {
				mail.setMailTo(multiconfirmConfig.getEmailAddress());
			} else {
				logger.error("邮件接收人地址为空");
				emailFlag = false;
			}
			mail.setSubject("多次确认操作提醒");
			mail.setContent(emailStr.toString());
			mail.setTextType(TextType.PLAINTYPE);
			if (emailFlag && mail.getMailTo().indexOf(",") != -1) {
				String[] mails = StringUtil.split(mail.getMailTo(), ',');
				for(int i = 0;i < mails.length;i ++) {
					String mailStr = mails[i];
					if (!MailUtil.checkEmail(mailStr)) {
						emailFlag = false;
						break;
					}
				}
				if (!emailFlag) {
					logger.error("收件人格式错误!正确格式：a@a.com,b@b.com,c@c.com!");
				}
			}
			
			if (emailFlag) {
				mail.setStatus(StatusType.WAITINGTYPE);
				List<Mail> mails = mailService.manage(mail);
				
				//执行完数据库操作之后发送邮件
				for(Mail m: mails) {
					MailQueueTask mailTask = new MailQueueTask();
					mailTask.setMailto(m.getMailTo());
					mailTask.setSubject(m.getSubject());
					if (m.getTextType().getValue() == TextType.PLAINTYPE.getValue()) {
						mailTask.setText(m.getContent());
					} else {
						mailTask.setHtmlText(m.getContent());
					}
					mailTask.setCallback(callbackUrlMail + "mail.id=" + m.getId());
					int i = mailQueueTaskService.postToQueue(mailTask);
					
					if (i == QueueConstant.RC_SUCCESS) {
						m.setStatus(StatusType.SENDINGTYPE);
						mailService.update(m);
					}
				}
			}
		}
		logger.info("send Email End!");
		
		logger.info("send Sms!");
		if (multiconfirmConfig.getIsSms().getValue() == YesNoStatus.YES.getValue()) {
			StringBuffer smsStr = new StringBuffer();		//短信内容
			smsStr.append("用户：[").append(userSessionBean.getUser().getName()).append("]于[").append(CoreDateUtils.formatDate(new Date(), CoreDateUtils.DATETIME))
			.append("]对配置名为:[").append(multiconfirmConfig.getConfigName()).append("]的功能进行了操作");
			Sms sms = new Sms();
			boolean smsFlag = true;
			if (smsFlag && multiconfirmConfig.getSmsAddress() != null && !multiconfirmConfig.getSmsAddress().equals("")) {
				sms.setSmsTo(multiconfirmConfig.getSmsAddress());
			} else {
				logger.error("短信地址为空");
				smsFlag = false;
			}
			sms.setContent(smsStr.toString());
			if (sms.getContent() != null && !"".equals(sms.getContent())) {
				if (sms.getContent().length() > 140) {
					logger.error("短信内容不能超过140个字符");
					smsFlag = false;
				}
			}
			sms.setStatus(StatusType.WAITINGTYPE);
			if (smsFlag) {
				List<Sms> smses = smsService.manage(sms);
				Sms smsResult = smses.get(0);
				String phoneNos = smsResult.getSmsTo();
				if (phoneNos == null || "".equals(phoneNos)) {
					logger.error("接收人为空");
					smsFlag = false;
				}
				if (smsFlag) {
					//执行完数据库操作之后发送短信
					SmsQueueTask smsQueueTask = new SmsQueueTask();
					smsQueueTask.addReceiver(phoneNos);
					smsQueueTask.setContent(smsResult.getContent());
					smsQueueTask.setCallback(callbackUrlSms + "sms.id=" + smsResult.getId());
					int i = smsQueueTaskService.postToQueue(smsQueueTask);
					
					if (i == QueueConstant.RC_SUCCESS) {
						smsResult.setStatus(StatusType.SENDINGTYPE);
						smsService.update(smsResult);
					} else {
						logger.error("放入队列任务失败");
					}
				}
			}
		}
		logger.info("send Sms End!");
	}

	public MulticonfirmConfirmFactory getConfirmFactory() {
		return confirmFactory;
	}

	public void setConfirmFactory(MulticonfirmConfirmFactory confirmFactory) {
		this.confirmFactory = confirmFactory;
	}

	public MulticonfirmParamFactory getParamFactory() {
		return paramFactory;
	}

	public void setParamFactory(MulticonfirmParamFactory paramFactory) {
		this.paramFactory = paramFactory;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public String getCallbackUrlMail() {
		return callbackUrlMail;
	}

	public void setCallbackUrlMail(String callbackUrlMail) {
		this.callbackUrlMail = callbackUrlMail;
	}

	public String getCallbackUrlSms() {
		return callbackUrlSms;
	}

	public void setCallbackUrlSms(String callbackUrlSms) {
		this.callbackUrlSms = callbackUrlSms;
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public QueueTaskService getMailQueueTaskService() {
		return mailQueueTaskService;
	}

	public void setMailQueueTaskService(QueueTaskService mailQueueTaskService) {
		this.mailQueueTaskService = mailQueueTaskService;
	}

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public QueueTaskService getSmsQueueTaskService() {
		return smsQueueTaskService;
	}

	public void setSmsQueueTaskService(QueueTaskService smsQueueTaskService) {
		this.smsQueueTaskService = smsQueueTaskService;
	}

	public MulticonfirmService getMulticonfirmService() {
		return multiconfirmService;
	}

	public void setMulticonfirmService(MulticonfirmService multiconfirmService) {
		this.multiconfirmService = multiconfirmService;
	}

}
