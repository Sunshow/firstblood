package web.action.member;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.CreditLogService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.CreditLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.CreditType;

public class CreditLogAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(CreditLogAction.class);
	
	private CreditLogService creditLogService;
	private MemberService memberService;
	
	private CreditLog creditLog;
	
	private List<CreditLog> creditLogs;
	
	private Long uid;			//用户ID
	private String username;	//用户名
	private Date tbeginDate;	//开始操作时间
	private Date tendDate;		//结束操作时间
	private String type;		//彩贝类型
	
	private String orderStr;	//排序字段
	private String orderView;	//排序方式
	
	private Map<String, String> orderStrMap;	//排序字段列表
	private Map<String, String> orderViewMap;	//排序方式
	
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入查询彩贝流水数据");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		
//		if (tbeginDate == null) {
//			tbeginDate = getDefaultQueryBeginDate();
//		}
		
		try {
			if (uid != null && uid != 0) {
				username = memberService.getUserNameById(uid);
			}
			map = creditLogService.getResult(uid == null ? 0 : uid.longValue(), username, tbeginDate, tendDate, type, getOrderStr(), getOrderView(), super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩贝流水数据，API调用异常，{}", e.getMessage());
		}
		if(map != null){
			creditLogs = (List<CreditLog>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询彩贝流水数据结束");
		return "list";
	}
	
	public CreditLog getCreditLog() {
		return creditLog;
	}
	public void setCreditLog(CreditLog creditLog) {
		this.creditLog = creditLog;
	}
	public List<CreditLog> getCreditLogs() {
		return creditLogs;
	}
	public void setCreditLogs(List<CreditLog> creditLogs) {
		this.creditLogs = creditLogs;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getTbeginDate() {
		return tbeginDate;
	}
	public void setTbeginDate(Date tbeginDate) {
		this.tbeginDate = tbeginDate;
	}
	public Date getTendDate() {
		return tendDate;
	}
	public void setTendDate(Date tendDate) {
		this.tendDate = tendDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOrderStr() {
		if(orderStr == null && !"".equals(orderStr)){
			orderStr = CreditLog.ORDER_LOG_ID;
		}
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		if(orderView == null && !"".equals(orderView)){
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public CreditLogService getCreditLogService() {
		return creditLogService;
	}

	public void setCreditLogService(CreditLogService creditLogService) {
		this.creditLogService = creditLogService;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(CreditLog.ORDER_LOG_ID, "流水号");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public List<CreditType> getCreditTypeItems(){
		return CreditType.getItems();
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
}
