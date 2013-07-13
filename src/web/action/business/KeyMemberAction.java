package web.action.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.business.KeyMember;
import com.lehecai.admin.web.domain.business.SmsMailMember;
import com.lehecai.admin.web.domain.business.SmsMailMemberGroup;
import com.lehecai.admin.web.service.business.KeyMemberService;
import com.lehecai.admin.web.service.business.SmsMailMemberGroupService;
import com.lehecai.admin.web.service.business.SmsMailMemberService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class KeyMemberAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(KeyMemberAction.class);
	
	private KeyMemberService keyMemberService;
	private MemberService memberService;
	private SmsMailMemberGroupService smsMailMemberGroupService;
	private SmsMailMemberService smsMailMemberService;
	
	private Long uid;			//会员编码
	private String userName;	//会员用户名
	private Date rbeginDate;	//会员注册起始时间
	private Date rendDate; 		//会员注册结束时间
	private Date lbeginDate;	//会员最后登录起始时间
	private Date lendDate;		//会员最后登录结束时间
	private String orderStr = Member.ORDER_UID;	//排序字段(默认为编码)
	private String orderView = ApiConstant.API_REQUEST_ORDER_ASC;	//排序方式(升序)
	
	private Member member;			//会员对象
	private KeyMember keyMember;	//重点会员对象
	
	private List<KeyMember> keyMemberList;	//重点会员列表
	private List<SmsMailMemberGroup> smsGroupList;//短信组列表
	private List<SmsMailMember> keyMembersmsGroupList;//重点会员所在短信组列表
	private List<Long> keyMembersmsGroupIdList;//重点会员所在短信组编号列表
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	/**
	 * 查询所有重点会员
	 * @return
	 */
	public String handle () {
		logger.info("进入查询重点会员");
		return "list";
	}
	
	/**
	 * 查询所有重点会员
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query () {
		logger.info("进入查询重点会员");
		List<KeyMember> tempKeyMemberList = keyMemberService.findList();//查询所有重点会员
		
		if (tempKeyMemberList == null || tempKeyMemberList.size() == 0) {
			logger.info("暂无重点会员");
			return "list";
		}

		List<String> keyMemberUids = new ArrayList<String>();
		for (KeyMember tempKeyMember : tempKeyMemberList) {
			keyMemberUids.add(tempKeyMember.getUid() + "");	//添加重点会员会员编号到集合
		}
		
		if (uid != null && uid != 0L) {
			if (!keyMemberUids.contains(uid+"")) {
				logger.info("暂无重点会员");
				return "list";
			}
		}
		
		if (userName != null && !"".equals(userName)) {
			Long getMemberId = null;
			try {
				getMemberId = memberService.getIdByUserName(userName);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询用户名，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			
			if (!keyMemberUids.contains(getMemberId+"")) {
				logger.info("暂无重点会员");
				return "list";
			}
		}
		Map<String,Object> map = null;
		try {
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(20);
			map = keyMemberService.fuzzyQueryResult(keyMemberUids, uid, userName, rbeginDate, rendDate, lbeginDate, lendDate, orderStr, 
					orderView, super.getPageBean());//多条件并分页查询会员，和MemberService的fuzzyQueryResult()方法区别在于多个keyMemberUids查询条件
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询重点会员，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		
		if (map != null) {
			HttpServletRequest request = ServletActionContext.getRequest();
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			
			List<Member> tempMemberList = (List<Member>)map.get(Global.API_MAP_KEY_LIST);
			
			keyMemberList = new ArrayList<KeyMember>();
			for (Member tempMember : tempMemberList) {//循环查询返回的所有会员
				for (KeyMember tempKeyMember : tempKeyMemberList) {//循环所有重点会员
					if (tempKeyMember.getUid() == tempMember.getUid()) {
						tempKeyMember.setRegisterTime(tempMember.getRegisterTime());	//设置注册时间
						tempKeyMember.setLastLoginTime(tempMember.getLastLoginTime());	//设置最后登录时间
						tempKeyMember.setLastConsumeTime(tempMember.getLastConsumeTime());	//设置最后消费时间
						tempKeyMember.setLastRechargeTime(tempMember.getLastRechargeTime());	//设置最后充值时间
						keyMemberList.add(tempKeyMember);
						break;
					}
				}
			}
		} else {
			logger.info("多条件并分页查询重点会员暂无数据！");
		}
		logger.info("查询重点会员结束");
		return "list";
	}
	
	/**
	 * 查询重点会员详细信息
	 */
	public String view() {
		logger.info("进入查询重点会员详细信息");
		if (keyMember == null || (keyMember.getId() == null || keyMember.getId() == 0L)) {
			logger.error("查询重点会员详细信息，编码为空");
			super.setErrorMessage("查询重点会员详细信息，编码不能为空");
			return "failure";
		}
		keyMember = keyMemberService.getById(keyMember.getId());//根据重点会员编号查询重点会员信息
		try {
			member = memberService.get(keyMember.getUid());//根据重点会员会员编码查询会员信息
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询会员信息，api调用异常");
			super.setErrorMessage("查询会员信息，api调用异常");
			return "failure";
		}
		keyMember.setRegisterTime(member.getRegisterTime());//设置注册时间
		keyMember.setLastLoginTime(member.getLastLoginTime());//设置最后登录时间
		keyMember.setLastConsumeTime(member.getLastConsumeTime());	//设置最后消费时间
		keyMember.setLastRechargeTime(member.getLastRechargeTime());	//设置最后充值时间
		logger.info("查询重点会员详细信息结束");
		return "view";
	}
	
	/**
	 * 转向添加重点会员
	 * @return
	 */
	public String input () {
		logger.info("进入输入重点会员信息");
		return "inputForm";
	}
	
	/**
	 * 添加重点会员
	 * @return
	 */
	public String manage () {
		logger.info("进入添加重点会员");
		if (keyMember != null) {
			if (keyMember.getId() == null || keyMember.getId() == 0L) {//添加重点会员
				if ((keyMember.getUid() == null || keyMember.getUid() == 0) && (keyMember.getUserName() == null || "".equals(keyMember.getUserName()))) {
					logger.error("添加重点会员，会员编码和会员用户名为空");
					super.setErrorMessage("添加重点会员，会员编码或会员用户名不能为空");
					return "failure";
				}
				if (keyMember.getUid() != null && keyMember.getUid() != 0) {//如果有填写会员编码，以会员编码为主，
					Member getMemberByUid = null;
					try {
						getMemberByUid = memberService.get(keyMember.getUid());//根据会员编码查询会员
					} catch (ApiRemoteCallFailedException e) {
						logger.error("查询会员信息，api调用异常，{}", e.getMessage());
						super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
						return "failure";
					}
					if (getMemberByUid == null) {
						logger.error(keyMember.getUid() + "编码对应的会员不存在");
						super.setErrorMessage(keyMember.getUid() + "编码对应的会员不存在");
						return "failure";
					}
					KeyMember tempGetKeyMember = keyMemberService.getByUid(keyMember.getUid());//根据会员编码查询重点会员
					if (tempGetKeyMember != null) {
						logger.error(keyMember.getUid() + "编码对应的会员已经是重点会员");
						super.setErrorMessage(keyMember.getUid() + "编码对应的会员已经是重点会员");
						return "failure";
					}
					String userName = "";
					try {
						userName = memberService.getUserNameById(keyMember.getUid());//根据会员编码查询会员用户名
					} catch (ApiRemoteCallFailedException e) {
						logger.error("查询会员用户名，api调用异常，{}", e.getMessage());
						super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
						return "failure";
					}
					keyMember.setUserName(userName);		//设置重点会员会员用户名
				} else {//没有填写则以会员用户名为主
					Member getMemberByUserName = null;
					try {
						getMemberByUserName = memberService.get(keyMember.getUserName());//根据会员用户名查询会员
					} catch (ApiRemoteCallFailedException e) {
						logger.error("查询会员信息，api调用异常，{}", e.getMessage());
						super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
						return "failure";
					}
					if (getMemberByUserName == null) {
						logger.error(keyMember.getUserName() + "用户名对应的会员不存在");
						super.setErrorMessage(keyMember.getUserName() + "用户名对应的会员不存在");
						return "failure";
					}
					KeyMember tempGetKeyMember = keyMemberService.getByUserName(keyMember.getUserName());//根据会员用户名查询重点会员
					if (tempGetKeyMember != null) {
						logger.error(keyMember.getUserName() + "用户名对应的会员已经是重点会员");
						super.setErrorMessage(keyMember.getUserName() + "用户名对应的会员已经是重点会员");
						return "failure";
					}
					Long uid = null; 
					try {
						uid = memberService.getIdByUserName(keyMember.getUserName());//根据会员用户名查询会员编码
					} catch (ApiRemoteCallFailedException e) {
						logger.error("查询会员编码，api调用异常，{}", e.getMessage());
						super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
						return "failure";
					}
					if (uid == null || uid == 0L) {
						logger.error("未查询到username={}的会员编码", keyMember.getUserName());
						super.setErrorMessage("未查询到username=" + keyMember.getUserName() + "的会员编码");
						return "failure";
					}
					keyMember.setUid(uid);		//设置重点会员会员编码
				}
			}
		}
		
		keyMemberService.mergeKeyMember(keyMember);//添加重点会员
		
		super.setForwardUrl("/business/keyMember.do");
		logger.info("添加重点会员结束");
		return "success";
	}
	
	/**
	 * 修改重点会员备注
	 */
	public String manageMemo () {
		logger.info("进入修改重点会员备注");
		if (keyMember == null || (keyMember.getId() == null || keyMember.getId() == 0L)) {
			logger.error("修改重点会员备注，编码为空");
			super.setErrorMessage("修改重点会员备注，编码为空");
			return "failure";
		}
		
		keyMemberService.mergeKeyMember(keyMember);//修改重点会员备注
		
		JSONObject rs = new JSONObject();
		rs.put("memo", keyMember.getMemo());
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return null;
	}
	
	/**
	 * 删除重点会员
	 * @return
	 */
	public String del() {
		logger.info("进入删除重点会员");
		KeyMember getKeyMember = null;
		if (keyMember != null) {
			if ((keyMember.getUid() == null || keyMember.getUid() == 0) && (keyMember.getUserName() == null || "".equals(keyMember.getUserName()))) {
				logger.error("删除重点会员，会员编码和用户名为空");
				super.setErrorMessage("删除重点会员，会员编码或用户名不能为空");
				return "failure";
			}
			
			if (keyMember.getUid() != null && keyMember.getUid() != 0) {//如果有填写会员编码，以会员编码为主，
				getKeyMember = keyMemberService.getByUid(keyMember.getUid());//根据重点会员会员编码查询重点会员
				if (getKeyMember == null) {
					logger.error(keyMember.getUid() + "编码对应的会员不是重点会员");
					super.setErrorMessage(keyMember.getUid() + "编码对应的会员不是重点会员");
					return "failure";
				}
			} else {//没有填写则以会员用户名为主
				getKeyMember = keyMemberService.getByUserName(keyMember.getUserName());//根据重点会员会员用户名查询重点会员
				if (getKeyMember == null) {
					logger.error(keyMember.getUserName() + "用户名对应的会员不是重点会员");
					super.setErrorMessage(keyMember.getUserName() + "用户名对应的会员不是重点会员");
					return "failure";
				}
			}
		}
		
		keyMemberService.deleteKeyMember(getKeyMember.getId());
		
		super.setForwardUrl("/business/keyMember.do");
		logger.info("删除重点会员结束");
		return "success";
	}
	
	/**
	 * 转向调整短信组
	 * @return
	 */
	public String inputSmsGroup () {
		logger.info("进入选择短信组");
		if (keyMember != null) {
			if (keyMember.getUid() == null || keyMember.getUid() == 0L) {
				logger.error("选择短信组，会员编码为空");
				super.setErrorMessage("选择短信组，会员编码为空");
				return "failure";
			}
 		}
		keyMembersmsGroupList = smsMailMemberService.findSmsMailMemberByUid(keyMember.getUid());//查询重点会员所在短信组
		
		smsGroupList = smsMailMemberGroupService.findSmsMailMemberGroupList(null, null, "true");//查询所有短信组
		return "inputKeyMemberSmsGroup";
	}
	
	/**
	 * 调整短信组
	 * @return
	 */
	public String manageSmsGroup() {
		logger.info("进入调整短信组");
		if (keyMember != null) {
			if (keyMember.getUid() == null || keyMember.getUid() == 0L) {
				logger.error("调整短信组，会员编码为空");
				super.setErrorMessage("调整短信组，会员编码为空");
				return "failure";
			}
		}
		
		keyMembersmsGroupList = smsMailMemberService.findSmsMailMemberByUid(keyMember.getUid());//查询重点会员所在短信组
		for (SmsMailMember tempSmsMember : keyMembersmsGroupList) {
			smsMailMemberService.del(tempSmsMember);//移除重点会员所在的短信组
		}
		
		if (keyMembersmsGroupIdList != null && keyMembersmsGroupIdList.size() != 0) {
			for (Long tempSmsGroupId : keyMembersmsGroupIdList) {//循环遍历最新短信组
				SmsMailMember newSmsMailMember = new SmsMailMember();
				Member tempMember = null;
				try {
					tempMember = memberService.get(keyMember.getUid());
				} catch (ApiRemoteCallFailedException e) {
					logger.error("查询用户信息，api调用异常，{}", e.getMessage());
					super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
					return "failure";
				}
				newSmsMailMember.setUid(keyMember.getUid());			//设置短信邮件会员编码
				newSmsMailMember.setUserName(tempMember.getUsername()); //设置短信邮件会员用户名
				newSmsMailMember.setGroupId(tempSmsGroupId);			//设置短信邮件会员组
				smsMailMemberService.merge(newSmsMailMember);//重新添加会员到短信组
			}
		} else {
			logger.info("没有选择任何短信组！");
			
		}
		logger.info("调整短信组成功！");
		
		//super.setForwardUrl("/business/keyMember.do?action=inputSmsGroup&keyMember.uid=" + keyMember.getUid());
		super.setForwardUrl("/business/keyMember.do");
		logger.info("调整短信组结束");
		return "success";
	}
	
	public KeyMemberService getKeyMemberService() {
		return keyMemberService;
	}
	public void setKeyMemberService(KeyMemberService keyMemberService) {
		this.keyMemberService = keyMemberService;
	}
	public MemberService getMemberService() {
		return memberService;
	}
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	public SmsMailMemberGroupService getSmsMailMemberGroupService() {
		return smsMailMemberGroupService;
	}
	public void setSmsMailMemberGroupService(
			SmsMailMemberGroupService smsMailMemberGroupService) {
		this.smsMailMemberGroupService = smsMailMemberGroupService;
	}
	public SmsMailMemberService getSmsMailMemberService() {
		return smsMailMemberService;
	}
	public void setSmsMailMemberService(SmsMailMemberService smsMailMemberService) {
		this.smsMailMemberService = smsMailMemberService;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getRbeginDate() {
		return rbeginDate;
	}
	public void setRbeginDate(Date rbeginDate) {
		this.rbeginDate = rbeginDate;
	}
	public Date getRendDate() {
		return rendDate;
	}
	public void setRendDate(Date rendDate) {
		this.rendDate = rendDate;
	}
	public Date getLbeginDate() {
		return lbeginDate;
	}
	public void setLbeginDate(Date lbeginDate) {
		this.lbeginDate = lbeginDate;
	}
	public Date getLendDate() {
		return lendDate;
	}
	public void setLendDate(Date lendDate) {
		this.lendDate = lendDate;
	}
	public String getOrderStr() {
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public KeyMember getKeyMember() {
		return keyMember;
	}
	public void setKeyMember(KeyMember keyMember) {
		this.keyMember = keyMember;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public List<KeyMember> getKeyMemberList() {
		return keyMemberList;
	}
	public void setKeyMemberList(List<KeyMember> keyMemberList) {
		this.keyMemberList = keyMemberList;
	}
	public List<SmsMailMemberGroup> getSmsGroupList() {
		return smsGroupList;
	}
	public void setSmsGroupList(List<SmsMailMemberGroup> smsGroupList) {
		this.smsGroupList = smsGroupList;
	}
	public List<SmsMailMember> getKeyMembersmsGroupList() {
		return keyMembersmsGroupList;
	}
	public void setKeyMembersmsGroupList(List<SmsMailMember> keyMembersmsGroupList) {
		this.keyMembersmsGroupList = keyMembersmsGroupList;
	}
	public List<Long> getKeyMembersmsGroupIdList() {
		return keyMembersmsGroupIdList;
	}
	public void setKeyMembersmsGroupIdList(List<Long> keyMembersmsGroupIdList) {
		this.keyMembersmsGroupIdList = keyMembersmsGroupIdList;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(Member.ORDER_UID, "编码");
		orderStrMap.put(Member.ORDER_REG_TIME, "注册时间");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
}
