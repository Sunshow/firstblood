package web.action.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.Sms;
import com.lehecai.admin.web.domain.business.SmsMailMemberGroup;
import com.lehecai.admin.web.domain.business.SmsMailModel;
import com.lehecai.admin.web.enums.ModelType;
import com.lehecai.admin.web.enums.SmsType;
import com.lehecai.admin.web.enums.StatusType;
import com.lehecai.admin.web.service.business.SmsMailMemberGroupService;
import com.lehecai.admin.web.service.business.SmsMailModelService;
import com.lehecai.admin.web.service.business.SmsService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.admin.web.utils.SmsUtil;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.queue.QueueConstant;
import com.lehecai.core.queue.QueueTaskService;
import com.lehecai.core.queue.sms.SmsQueueTask;

public class SmsAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SmsService smsService;
	private MemberService memberService;
	private SmsMailModelService smsMailModelService;
	private SmsMailMemberGroupService smsMailMemberGroupService;
	
	private Sms sms;
	
	private List<Sms> smsList;
	private List<String> usernames;
	private List<SmsMailModel> smsMailModelList;
	private List<SmsMailMemberGroup> smsMailMemberGroupList;
	
	private String smsTo;
	private String subject;
	private Date beginDate;
	private Date endDate;
	private Integer type;
	private String uid;
	private List<String> uidList;
	
	private QueueTaskService smsQueueTaskService;
	
	private String callbackUrl;
	
	private List<String> successList;
	private List<String> failureList;
	
	private Integer statusTypeId;
	
	
	public String handle() {
		logger.info("进入查询短信列表");
		return "list";
	}
	
	public String query() {
		logger.info("进入查询短信列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		smsList = smsService.list(smsTo, subject, statusTypeId, beginDate, endDate, super.getPageBean());
		PageBean pageBean = smsService.getPageBean(smsTo, subject, statusTypeId, beginDate, endDate, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		return "list";
	}
	
	public String manage() {
		logger.info("进入发送短信");
		if (sms != null) {
			if (sms.getId() != null) {
				if (sms.getSmsTo() == null || "".equals(sms.getSmsTo())) {				
					super.setErrorMessage("接收人为空");
					super.setErrorMessage("接收人不能为空");
					return "failure";
				}
				usernames = new ArrayList<String>();
				String[] smsToArray = StringUtils.split(sms.getSmsTo(), ",");
				for (String s : smsToArray) {
					usernames.add(s);
				}
			} else {
				if (usernames == null || usernames.size() == 0) {
					logger.error("接收人为空");
					super.setErrorMessage("接收人不能为空");
					return "failure";
				}
			}
			if (sms.getContent() == null || "".equals(sms.getContent())) {
				logger.error("内容为空");
				super.setErrorMessage("内容不能为空");
				return "failure";
			} else {
				if (sms.getContent() != null && !"".equals(sms.getContent())) {
					if (sms.getContent().length() > 140) {
						logger.error("内容不能超过140个字符");
						super.setErrorMessage("内容不能超过140个字符");
						return "failure";
					}
				}
			}
			
			sms.setSmsTo(StringUtils.join(usernames, ","));
			sms.setStatus(StatusType.WAITINGTYPE);
			List<Sms> smses = smsService.manage(sms);
			
			Sms smsResult = smses.get(0);
			
			String phoneNos = "";
			if (type == null || type == SmsType.USERNAME.getValue()) {
				List<Member> members = null;
				try {
					List<String> uids = memberService.getUidsByUsernames(usernames);//根据usernames列表查询对应的uids列表
					if (uids == null || uids.size() == 0) {
						logger.error("对应的会员编号列表为空");
						super.setErrorMessage("对应的会员编号列表为空");
						return "failure";
					}
					members = memberService.getMembersByUids(uids);//根据uids列表查询对应的members列表
				} catch (ApiRemoteCallFailedException e) {
					logger.error("查询对应的会员列表，api调用异常，{}", e.getMessage());
					super.setErrorMessage("api调用异常，请联系技术人员，原因：" + e.getMessage());
					return "failure";
				}
				if (members == null || members.size() == 0) {
					logger.error("对应的会员列表为空");
					super.setErrorMessage("对应的会员列表为空");
					return "failure";
				}
				for (Member m : members) {
					if (m.getPhone() != null && !"".equals(m.getPhone())) {
						phoneNos = phoneNos + m.getPhone() + ",";
					}
				}
				if (phoneNos != null && phoneNos.endsWith(",")) {
					phoneNos = phoneNos.substring(0, phoneNos.length() - 1);
				}
			} else {
				phoneNos = smsResult.getSmsTo();
			}
			
			if (phoneNos == null || "".equals(phoneNos)) {
				logger.error("接收人为空");
				super.setErrorMessage("接收人不能为空");
				return "failure";
			}
			
			//执行完数据库操作之后发送邮件
			
			SmsQueueTask task = new SmsQueueTask();
			task.addReceiver(phoneNos);
			task.setContent(smsResult.getContent());
			task.setCallback(callbackUrl + "sms.id=" + smsResult.getId());
			int i = smsQueueTaskService.postToQueue(task);
			
			if (i == QueueConstant.RC_SUCCESS) {
				smsResult.setStatus(StatusType.SENDINGTYPE);
				smsService.update(smsResult);
			} else {
				logger.error("放入队列任务失败");
				super.setErrorMessage("放入队列任务失败");
				return "failure";
			}
		} else {
			logger.error("添加短信错误，提交表单为空！");
			super.setErrorMessage("添加短信错误，提交表单为空！");
			return "failure";
		}
		logger.info("发送短信结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入短信");
		if (sms != null) {
			if (sms.getId() != null) {			
				sms = smsService.get(sms.getId());
				statusTypeId = sms.getStatus().getValue();
			}
		} else {
			sms = new Sms();
		}
		//使用uid进行邮件发送
		if (!StringUtils.isEmpty(uid) && StringUtils.isEmpty(sms.getSmsTo())) {
			try {
				Member member = memberService.get(Long.valueOf(uid));
				sms.setSmsTo(member.getUsername());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("根据uid获取username时失败，原因：" + e.getMessage());
				return "failure";
			}
		}
		if (usernames != null && usernames.size() > 0) {
			usernames = removeRepeat(usernames);
		} else if (uidList != null && uidList.size() > 0) {
			uidList = removeRepeat(uidList);
			List<Member> members = null;
			try {
				members = memberService.getMembersByUids(uidList);//根据uids列表查询对应的member列表
				if (members != null && members.size() > 0) {
					usernames = new ArrayList<String>();
					for (Member m : members) {
						uidList.add(m.getUsername());
					}
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询对应的会员列表，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员，原因：" + e.getMessage());
				return "failure";
			}
		}
		
		statusTypeId = StatusType.SENDINGTYPE.getValue();
		try {
			smsMailModelList = smsMailModelService.list(null, null, ModelType.SMS.getValue(), null, null, null, null, null, null);
		} catch (Exception e) {
			logger.error("短信模板获取错误");
			super.setErrorMessage("短信模板获取错误");
			return "failure";
		}
		PageBean pageBean = super.getPageBean();
		pageBean.setPageFlag(false);
		smsMailMemberGroupList = smsMailMemberGroupService.findSmsMailMemberGroupList(pageBean, "", "true");//查询所有用户组
		return "inputForm";
	}
	
	/**
	 * 群发时 用户名去重
	 */
	List<String> removeRepeat(List<String> usernames) {
		List<String> usersTmp = new ArrayList<String>();
		boolean flag = false;
		for (int i = 0; i < usernames.size(); i++) {
			if (usernames.get(i) == null || "".equals(usernames.get(i))) {
				continue;
			}
			flag = false;
			if (usersTmp.size() >0) {
				for (int j = 0; j < usersTmp.size(); j++) {
					if (usernames.get(i).equals(usersTmp.get(j))) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					usersTmp.add(usernames.get(i));
				}
			} else {
				usersTmp.add(usernames.get(i));
			}
		}
		return usersTmp;
	}

	public String retry() {
		logger.info("进入重新发送短信");
		if (sms != null) {
			if (sms.getId() != null) {			
				sms = smsService.get(sms.getId());
				
				String phoneNos = "";
				List<String> phoneNoList = Arrays.asList(StringUtils.split(sms.getSmsTo(), ","));
				if (!SmsUtil.checkPhoneNo(phoneNoList.get(0))) {
					List<Member> members = null;
					try {
						members = memberService.getMembersByUids(memberService.getUidsByUsernames(phoneNoList));
					} catch (ApiRemoteCallFailedException e) {
						logger.error(e.getMessage(), e);
					}
					if (members != null && members.size() > 0) {
						for (Member m : members) {
							if (m.getPhone() != null && !"".equals(m.getPhone())) {
								phoneNos = phoneNos + m.getPhone() + ",";
							}
						}
						if (phoneNos != null && phoneNos.endsWith(",")) {
							phoneNos = phoneNos.substring(0, phoneNos.length() - 1);
						}
					}
				} else {
					phoneNos = sms.getSmsTo();
				}
				
				SmsQueueTask task = new SmsQueueTask();
				task.addReceiver(phoneNos);
				task.setContent(sms.getContent());
				task.setCallback(callbackUrl + "sms.id=" + sms.getId());
				int i = smsQueueTaskService.postToQueue(task);
				
				if (i == QueueConstant.RC_SUCCESS) {
					sms.setStatus(StatusType.SENDINGTYPE);
					smsService.update(sms);
				}
			}
		}
		super.setForwardUrl("/business/sms.do");
		logger.info("重新发送短信结束");
		return "forward";
	}
	
	public String view() {
		logger.info("进入查看短信详细信息");
		if (sms != null && sms.getId() != null) {
			sms = smsService.get(sms.getId());
		} else {
			logger.error("查看短信详细信息，编码为空");
			super.setErrorMessage("查看短信详细信息，编码不能为空");
			return "failure";
		}
		logger.info("查看短信详细信息结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除短信");
		if (sms != null && sms.getId() != null) {
			sms = smsService.get(sms.getId());
			smsService.del(sms);
		} else {
			logger.error("删除短信，编码为空");
			super.setErrorMessage("删除短信，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/business/sms.do");
		logger.info("删除短信结束");
		return "forward";
	}
	
	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public SmsMailMemberGroupService getSmsMailMemberGroupService() {
		return smsMailMemberGroupService;
	}

	public void setSmsMailMemberGroupService(SmsMailMemberGroupService smsMailMemberGroupService) {
		this.smsMailMemberGroupService = smsMailMemberGroupService;
	}

	public Sms getSms() {
		return sms;
	}

	public void setSms(Sms sms) {
		this.sms = sms;
	}

	public List<Sms> getSmsList() {
		return smsList;
	}

	public void setSmsList(List<Sms> smsList) {
		this.smsList = smsList;
	}

	public String getSmsTo() {
		return smsTo;
	}

	public void setSmsTo(String smsTo) {
		this.smsTo = smsTo;
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

	public SmsMailModelService getSmsMailModelService() {
		return smsMailModelService;
	}

	public void setSmsMailModelService(SmsMailModelService smsMailModelService) {
		this.smsMailModelService = smsMailModelService;
	}

	public QueueTaskService getSmsQueueTaskService() {
		return smsQueueTaskService;
	}

	public void setSmsQueueTaskService(QueueTaskService smsQueueTaskService) {
		this.smsQueueTaskService = smsQueueTaskService;
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

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public List<String> getUsernames() {
		return usernames;
	}

	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}

	public List<String> getSuccessList() {
		return successList;
	}

	public void setSuccessList(List<String> successList) {
		this.successList = successList;
	}

	public List<String> getFailureList() {
		return failureList;
	}

	public void setFailureList(List<String> failureList) {
		this.failureList = failureList;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public List<SmsType> getTypeList() {
		return SmsType.list;
	}

	public List<SmsMailModel> getSmsMailModelList() {
		return smsMailModelList;
	}

	public void setSmsMailModelList(List<SmsMailModel> smsMailModelList) {
		this.smsMailModelList = smsMailModelList;
	}

	public List<SmsMailMemberGroup> getSmsMailMemberGroupList() {
		return smsMailMemberGroupList;
	}

	public void setSmsMailMemberGroupList(List<SmsMailMemberGroup> smsMailMemberGroupList) {
		this.smsMailMemberGroupList = smsMailMemberGroupList;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public void setUidList(List<String> uidList) {
		this.uidList = uidList;
	}

	public List<String> getUidList() {
		return uidList;
	}

}
