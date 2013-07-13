package web.action.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.action.lottery.ChaseAction;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.business.Mail;
import com.lehecai.admin.web.enums.StatusType;
import com.lehecai.admin.web.enums.TextType;
import com.lehecai.admin.web.service.business.MailService;
import com.lehecai.admin.web.service.business.ResetPlanDrawStatusService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.queue.QueueConstant;
import com.lehecai.core.queue.QueueTaskService;
import com.lehecai.core.queue.mail.MailQueueTask;
import com.lehecai.core.util.CoreStringUtils;
import com.opensymphony.xwork2.Action;

public class ResetPlanDrawStatusAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(ChaseAction.class);
	
	private String ids;
	private ResetPlanDrawStatusService resetPlanDrawStatusService;
	private String callbackUrl;
	private List<String> mailList;

	private MailService mailService;
	private QueueTaskService mailQueueTaskService;
	
	public String handle(){
		return "input";
	}
	
	/**
	 * 执行重置
	 * @return
	 */
	public String reset() {
		logger.info("进入重置开奖方案状态");
		int rc = 0;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (ids == null || ids.length() == 0) {
			logger.error("重置开奖方案状态，编码为空");
			super.setErrorMessage("重置开奖方案状态，编码为空");
			msg = "重置开奖方案状态，编码为空";
			rc = 1;
			json.put("code", rc);
			json.put("message", msg);
			writeRs(response, json);
			return Action.NONE;
		}
				
		String[] idArray = StringUtils.split(ids, " ,;\n\t");
		List<String> idList = new ArrayList<String>();
		if (idArray != null && idArray.length > 0) {
			for (int i = 0; i < idArray.length; i++) {
				idList.add(idArray[i]);
			}
		}
		
		List<String> successList = new ArrayList<String>();
		List<String> failList = new ArrayList<String>();
		
		try {
			resetPlanDrawStatusService.reset(idList, successList, failList);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("重置开奖方案状态，api调用异常，{}", e.getMessage());
			super.setErrorMessage("重置开奖方案状态，api调用失败，请联系技术人员!原因：" + e.getMessage());
			msg = "重置开奖方案状态，api调用失败，请联系技术人员!原因：" + e.getMessage();
			rc = 1;
			json.put("code", rc);
			json.put("message", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		
		//发送邮件提醒
		String mailTo = StringUtils.join(mailList,",");
		Mail mail = new Mail();
		mail.setMailTo(mailTo);
		mail.setSubject("重置方案开奖状态");
		mail.setTextType(TextType.PLAINTYPE);
		mail.setStatus(StatusType.WAITINGTYPE);
		String operationTime = DateUtil.formatDate(new Date(), DateUtil.DATETIME);
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		String operationUserName = userSessionBean.getUser().getUserName();
		mail.setContent("操作人：" + operationUserName + " 操作时间：" + operationTime+ " 方案编号：" + ids);
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
				
		StringBuffer sb = new StringBuffer();
		sb.append("重置开奖方案状态完成,成功方案").append(successList.size()).append("个\n{");
		if (successList != null && successList.size() > 0) {
			String successStr = CoreStringUtils.join(successList, ",");
			sb.append(successStr);
		}
		sb.append("}\n失败方案").append(failList.size()).append("个\n{");
		if (failList != null && failList.size() > 0) {
			String failStr = CoreStringUtils.join(failList, ",");
			sb.append(failStr);
		}
		sb.append("}");
		msg = sb.toString();
		json.put("code", rc);
		json.put("message", msg);
		writeRs(response, json);
		logger.info("重置开奖方案状态结束");
		return Action.NONE;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public ResetPlanDrawStatusService getResetPlanDrawStatusService() {
		return resetPlanDrawStatusService;
	}

	public void setResetPlanDrawStatusService(
			ResetPlanDrawStatusService resetPlanDrawStatusService) {
		this.resetPlanDrawStatusService = resetPlanDrawStatusService;
	}
	
	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	
	public List<String> getMailList() {
		return mailList;
	}

	public void setMailList(List<String> mailList) {
		this.mailList = mailList;
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
}
