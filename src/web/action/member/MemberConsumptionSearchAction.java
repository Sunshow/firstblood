package web.action.member;

import java.util.Calendar;
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
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.search.OrderSearchService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlanOrderStatus;
import com.lehecai.core.search.entity.lottery.PlanOrderSearch;
import com.lehecai.core.search.entity.lottery.PlanOrderSearchDefine;

public class MemberConsumptionSearchAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(MemberConsumptionSearchAction.class);
	
	private OrderSearchService orderSearchService;
	private MemberService memberService;
	
	private List<PlanOrder> planOrders;
	
	private Integer lotteryTypeId;
	private Long uid;
	private String username;
	private Date beginDate;
	private Date endDate;
	
	private Integer seconds;//倒计时
	
	private String orderStr;
	private String orderView;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	String totalAmount = "";
	String totalPrizePostTax = "";
	
	public String handle() {
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String list() {
		logger.info("进入查询会员消费数据");

        if (beginDate == null || endDate == null) {
            logger.error("开始时间和结束时间必须指定");
            super.setErrorMessage("开始时间和结束时间必须指定!");
            return "failure";
        }
        
		PlanOrderStatus pos = PlanOrderStatus.PAID_FINISHED; //订单状态为支付完成
		
		PlanOrderSearch searchEntity = new PlanOrderSearch();
		searchEntity.setOrderStatus(pos);
		if (lotteryTypeId != null && lotteryTypeId != LotteryType.ALL.getValue()) {
			searchEntity.setLotteryType(LotteryType.getItem(lotteryTypeId));
		}
		if (username != null && !"".equals(username)) {
			Member member;
			try {
				member = memberService.get(username);
				if (member != null) {				
					uid = member.getUid();
				} else {
					return "list";
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		}
		if (uid != null && uid > 0) {
			searchEntity.setUid(uid);
		}
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(PlanOrderSearchDefine.CREATE_AT_START, beginDate);
		param.put(PlanOrderSearchDefine.CREATE_AT_END, endDate);
		param.put("orderStr", this.getOrderStr());
		param.put("orderView", this.getOrderView());
		
		Map<String, Object> map;
		try {
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(50);
			map = orderSearchService.getMemberConsumptionResult(searchEntity, param, pageBean);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		if (map != null) {			
			planOrders = (List<PlanOrder>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}

		logger.info("查询会员消费结束");
		return "list";
	}
	
	public String getOrderStr() {
		if (orderStr == null && !"".equals(orderStr)){
			orderStr = PlanOrder.ORDER_AMOUNT;
		}
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		if (orderView == null && !"".equals(orderView)){
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(PlanOrder.ORDER_AMOUNT, "消费金额");
		orderStrMap.put(PlanOrder.ORDER_POSTTAX_PRIZE, "中奖金额");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public List<LotteryType> getLotteryTypes(){
		return LotteryType.getItems();
	}
	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}
	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getBeginDate() {
		if (beginDate == null) {
			Calendar cd = Calendar.getInstance();
			cd.add(Calendar.DATE, -1);
			cd.set(Calendar.HOUR_OF_DAY, 0);
			cd.set(Calendar.MINUTE, 0);
			cd.set(Calendar.SECOND, 0);
			beginDate = cd.getTime();
		}
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		if (endDate == null) {
			Calendar cd = Calendar.getInstance();
			cd.add(Calendar.DATE, -1);
			cd.set(Calendar.HOUR_OF_DAY, 23);
			cd.set(Calendar.MINUTE, 59);
			cd.set(Calendar.SECOND, 59);
			endDate = cd.getTime();
		}
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public OrderSearchService getOrderSearchService() {
		return orderSearchService;
	}

	public void setOrderSearchService(OrderSearchService orderSearchService) {
		this.orderSearchService = orderSearchService;
	}

	public List<PlanOrder> getPlanOrders() {
		return planOrders;
	}

	public void setPlanOrders(List<PlanOrder> planOrders) {
		this.planOrders = planOrders;
	}

	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public Integer getSeconds() {
		if (seconds == null) {
			seconds = 600;
		}
		return seconds;
	}
	public void setSeconds(Integer seconds) {
		this.seconds = seconds;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTotalPrizePostTax() {
		return totalPrizePostTax;
	}

	public void setTotalPrizePostTax(String totalPrizePostTax) {
		this.totalPrizePostTax = totalPrizePostTax;
	}
	
}
