/**
 * 
 */
package web.service.notity;

import java.util.List;

/**
 * @author qatang
 *
 */
public interface NotifyService {
	/**
	 * 批量发送邮件
	 * @param subject
	 * @param content
	 * @param email
	 */
	public void sendEmail(String subject, String content, List<String> email);
	/**
	 * 单个发送邮件
	 * @param subject
	 * @param content
	 * @param email
	 */
	public void sendEmail(String subject, String content, String email);
	
	/**
	 * 批量发送短信
	 * @param message
	 * @param contact
	 */
	public void sendSms(String message, List<String> contact);
	/**
	 * 单个发送短信
	 * @param message
	 * @param contact
	 */
	public void sendSms(String message, String contact);
	
	/**
	 * 给角色或用户发送站内消息
	 * @param content
	 * @param roleIdList
	 * @param userIdList
	 */
	public void sendMessage(String content, List<Long> roleIdList, List<Long> userIdList);
}
