/**
 * 
 */
package web.service.impl.notify;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.notity.NotifyService;
import com.lehecai.core.queue.QueueConstant;
import com.lehecai.core.queue.QueueTaskService;
import com.lehecai.core.queue.mail.MailQueueTask;
import com.lehecai.core.queue.sms.SmsQueueTask;

/**
 * @author qatang
 *
 */
public class NotifyServiceImpl implements NotifyService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private boolean disableWarning = false;
	
	private QueueTaskService smsQueueTaskService;
	private QueueTaskService mailQueueTaskService;

	@Override
	public void sendEmail(String subject, String content, List<String> email) {
		if (disableWarning) {
			return;
		}
		
		if (email == null || email.size() == 0) {
			logger.error("邮件联系人未设置");
			return;
		}
		
		String contactStr = StringUtils.join(email, ",");
		
		MailQueueTask task = new MailQueueTask();
		task.setSubject(subject);
		task.setMailto(contactStr);
		task.setTaskType(QueueConstant.TASK_MAIL_DEFAULT);
		task.setText(content);
		
		try {
			int rc = mailQueueTaskService.postToQueue(task);
			if (rc == QueueConstant.RC_SUCCESS) {
				logger.info("发送警告成功");
			} else {
				logger.error("发送警告失败, rc={}", rc);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendEmail(String subject, String content, String email) {
		List<String> emailList = new ArrayList<String>();
		emailList.add(email);
		this.sendEmail(subject, content, emailList);
	}

	@Override
	public void sendMessage(String content, List<Long> roleIdList,
			List<Long> userIdList) {

	}

	@Override
	public void sendSms(String message, List<String> contact) {
		if (disableWarning) {
			return;
		}
		
		if (contact == null || contact.size() == 0) {
			logger.error("短信联系人未设置");
			return;
		}
		
		SmsQueueTask task = new SmsQueueTask();
		task.setTaskType(QueueConstant.TASK_SMS_DEFAULT);
		for (String c : contact) {
			task.addReceiver(c);
		}
		task.setContent(message);

		try {
			int rc = smsQueueTaskService.postToQueue(task);
			if (rc == QueueConstant.RC_SUCCESS) {
				logger.info("发送警告成功");
			} else {
				logger.error("发送警告失败, rc={}", rc);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendSms(String message, String contact) {
		List<String> contactList = new ArrayList<String>();
		contactList.add(contact);
		this.sendSms(message, contactList);
	}

	public boolean isDisableWarning() {
		return disableWarning;
	}

	public void setDisableWarning(boolean disableWarning) {
		this.disableWarning = disableWarning;
	}

	public QueueTaskService getSmsQueueTaskService() {
		return smsQueueTaskService;
	}

	public void setSmsQueueTaskService(QueueTaskService smsQueueTaskService) {
		this.smsQueueTaskService = smsQueueTaskService;
	}

	public QueueTaskService getMailQueueTaskService() {
		return mailQueueTaskService;
	}

	public void setMailQueueTaskService(QueueTaskService mailQueueTaskService) {
		this.mailQueueTaskService = mailQueueTaskService;
	}

}
