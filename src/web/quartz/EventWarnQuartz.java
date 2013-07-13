package web.quartz;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.EventService;
import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.event.EventInfoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreStringUtils;
import com.lehecai.core.warning.IWarningTool;

/**
 * 
 * @author He Wang
 *
 */
public class EventWarnQuartz {
	private final Logger logger = LoggerFactory.getLogger(EventWarnQuartz.class);
	
	private EventService eventService;
	private List<String> mailSendList;
	private IWarningTool warningTool;
	
	@SuppressWarnings("unchecked")
	public void run() {
		logger.info("开始定时处理24小时以内结束活动提醒");
		if (mailSendList == null || mailSendList.size() == 0) {
			logger.error("定时处理24小时以内结束活动提醒待发送邮件列表为空");
			return ;
		}
		// 根据条件查询
		Map<String, Object> map = null;
		Calendar cal = Calendar.getInstance();
		Date endDateFrom = cal.getTime();
		cal.add(Calendar.DATE, 1);
		Date endDateTo = cal.getTime();
		
		try {
			map = eventService.findEventInfoListByCondition(null, null, null, 
					null, endDateFrom, endDateTo, EventInfoStatus.OPEN, null);//条件查询所有活动
			if (map != null) {
				List<EventInfo> events = (List<EventInfo>)map.get(Global.API_MAP_KEY_LIST);
				String mailContent = "";
				if (events != null && events.size() > 0) {
					mailContent = getMailCss();
					mailContent += "<table cellpadding='0' cellspacing='0' border='0' class='querytab'><tr><th>活动编号</th><th>活动名称</th><th>起始时间</th><th>结束时间</th><tr>";
					for (EventInfo event : events) {
						String startTime = event.getEventStartTime() == null ? "" : CoreDateUtils.formatDate(event.getEventStartTime(), CoreDateUtils.DATETIME);
						String endTime = event.getEventEndTime() == null ? "" : CoreDateUtils.formatDate(event.getEventEndTime(), CoreDateUtils.DATETIME);
						mailContent += "<tr><td>" + event.getEventId() + "</td><td>" + event.getEventName() + "</td><td>" + startTime + "</td><td>" + endTime + "</td></tr>";
					}
					mailContent += "</table>";
					sendMail(mailSendList, mailContent);
				}
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("条件查询24小时以内结束活动，api调用异常，{}", e.getMessage());
		}
			
		logger.info("定时处理24小时以内到期活动提醒结束");
	}
	
	private String getMailCss(){
		StringBuffer sb = new StringBuffer("<style type=\"text/css\"> table.querytab {background: none repeat scroll 0 0 #FFFFFF;border-collapse: collapse;color: #344B50;");
		sb.append("font-size: 12px;margin: 0 auto;text-align: center; width: 70%;font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;}");
		sb.append(" table.querytab td{padding: 3px 5px;border:1px solid #a8c7ce;} table.querytab th{padding: 3px 5px;border:1px solid #a8c7ce;}");
		sb.append("</style>");
		return sb.toString();
	}
	
	/**
	 * 发送邮件
	 * @param members
	 */
	private void sendMail(List<String> mailList, String mailContent){

		if(mailList != null && mailList.size() > 0 ){
			// 发送邮件
			try {
				warningTool.sendMail("活动到期提醒", mailContent, CoreStringUtils.join(mailList, ","));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public EventService getEventService() {
		return eventService;
	}


	public void setMailSendList(List<String> mailSendList) {
		this.mailSendList = mailSendList;
	}


	public List<String> getMailSendList() {
		return mailSendList;
	}

	public void setWarningTool(IWarningTool warningTool) {
		this.warningTool = warningTool;
	}

	public IWarningTool getWarningTool() {
		return warningTool;
	}
}
