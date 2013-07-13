package web.action.lottery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.action.member.MemberAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanOrderService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.search.OrderSearchService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlanOrderStatus;
import com.lehecai.core.lottery.PlanOrderType;
import com.lehecai.core.lottery.PrizeStatus;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.search.entity.lottery.PlanOrderSearch;
import com.lehecai.core.search.entity.lottery.PlanOrderSearchDefine;
import com.lehecai.core.util.CoreMathUtils;

public class LotteryPlanOrderSearchAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(MemberAction.class);
	private final String QUERY_ALL = "ALL";
	private final String QUERY_STATISTICS = "statistics";
	private final String QUERY_DETAIL = "detail";
	
	private LotteryPlanOrderService lotteryPlanOrderService;
	private OrderSearchService orderSearchService;
	private MemberService memberService;
	
	private PlanOrder planOrder;
	
	private List<PlanOrder> planOrders;
	
	private String userName;
	private String orderId;
	private String planId;
	private Integer orderStatus;
	private Integer prizeStatus;
	private Integer orderType;
	private Integer lotteryTypeId;
	private String phase;
	private String queryFlag;
	private String phaseStr;
	private Long userId;
	
	private Date rbeginDate;
	private Date rendDate;
	private Date lbeginDate;
	private Date lendDate;
	
	private String orderStr;
	private String orderView;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	private Map<String, String> queryTypeMap;
	
	private String totalAmount;
	
	private String totalPrizePostTax;
	
	
	public String handle(){
		logger.info("进入订单统计查询页面");
		rbeginDate = getDefaultQueryBeginDate(-7);
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("开始订单统计查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		PlanOrderStatus pos = orderStatus == null ? null : PlanOrderStatus.getItem(orderStatus);
		
		PlanOrderSearch searchEntity = new PlanOrderSearch();
		if (pos != null && pos.getValue() != PlanOrderStatus.ALL.getValue()) {
			searchEntity.setOrderStatus(pos);
		}
		if (!StringUtils.isEmpty(planId)) {
			searchEntity.setPlanId(planId);
		}
		if (!StringUtils.isEmpty(orderId)) {
			searchEntity.setId(orderId);
		}
		if (!StringUtils.isEmpty(phase) && !phase.equals("-1")) {
			searchEntity.setPhase(phase);
		}
		if (lotteryTypeId != null && lotteryTypeId != LotteryType.ALL.getValue()) {
			searchEntity.setLotteryType(LotteryType.getItem(lotteryTypeId));
			//当输入彩期不为空时，使用输入彩期替代选择彩期
			if (!StringUtils.isEmpty(phaseStr)) {
				searchEntity.setPhase(phaseStr.trim());
			}
		} else {
			if (!StringUtils.isEmpty(phaseStr)) {
				super.setErrorMessage("手动填写彩期时必须先选择彩种");
				return "failure";
			}
		}
		if (orderType != null && orderType != PlanOrderType.ALL.getValue()) {
			searchEntity.setOrderType(PlanOrderType.getItem(orderType));
		}
		
		if (prizeStatus != null && prizeStatus != PrizeStatus.ALL.getValue()) {
			searchEntity.setPrizeStatus(PrizeStatus.getItem(prizeStatus));
		}
		if (orderStatus != null && orderStatus != PlanOrderStatus.ALL.getValue()) {
			searchEntity.setOrderStatus(PlanOrderStatus.getItem(orderStatus));
		}
		
		Map<String, Object> map;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(PlanOrderSearchDefine.CREATE_AT_START, rbeginDate);
		param.put(PlanOrderSearchDefine.CREATE_AT_END, rendDate);
		param.put(PlanOrderSearchDefine.PRIZE_TIME_START, lbeginDate);
		param.put(PlanOrderSearchDefine.PRIZE_TIME_END, lendDate);
		
		param.put("orderStr", this.getOrderStr());
		param.put("orderView", this.getOrderView());
		
		if (userName != null && !"".equals(userName)) {
			Member member;
			try {
				member = memberService.get(userName);
				if (member != null) {				
					userId = member.getUid();
				} else {
					return "list";
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		}
		if (userId != null && userId > 0) {
			searchEntity.setUid(userId);
		}
		if (queryFlag != null) {
			if (queryFlag.equals(QUERY_DETAIL) || queryFlag.equals(QUERY_ALL)) {
				try {
					PageBean pageBean = super.getPageBean();
					pageBean.setPageSize(50);
					map = orderSearchService.getLotteryPlanOrderResult(searchEntity, param, pageBean);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
					return "failure";
				}
				if (map != null) {			
					planOrders = (List<PlanOrder>)map.get(Global.API_MAP_KEY_LIST);
					PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
					super.setPageString(PageUtil.getPageString(request, pageBean));
					super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
				}
			}
			if (queryFlag.equals(QUERY_STATISTICS) || queryFlag.equals(QUERY_ALL)) {
				String[] fieldArray = {PlanOrder.ORDER_AMOUNT, PlanOrder.ORDER_POSTTAX_PRIZE};
				Map<String, Object> statisticsMap;
				try {
					statisticsMap = orderSearchService.lotteryPlanOrderStatistics(searchEntity, param, fieldArray);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
					return "failure";
				}
				if (statisticsMap != null) {
					JSONArray jsonArray = (JSONArray)statisticsMap.get(Global.API_MAP_KEY_AMOUNT);
					Long amount = jsonArray.getLong(0);
					totalAmount = CoreMathUtils.div(amount, 100, 2) + "";
					Long prizePostTax = jsonArray.getLong(1);
					totalPrizePostTax = CoreMathUtils.div(prizePostTax, 100, 2) + "";
				} else {
					totalAmount = "";
					totalPrizePostTax = "";
				}
			}
		} else {
			logger.error("查询类型为空");
			super.setErrorMessage("查询类型为空");
			return "failure";
		}
		logger.info("查询订单统计结束");
		return "list";
	}
	
	public String getOrderStr() {
		if (orderStr == null || "".equals(orderStr)) {
			orderStr = PlanOrder.ORDER_CREATED_TIME;
		}
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		if (orderView == null || "".equals(orderView)) {
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(PlanOrder.ORDER_ID, "订单编号");
		orderStrMap.put(PlanOrder.ORDER_CREATED_TIME, "创建时间");
		orderStrMap.put(PlanOrder.ORDER_PRIZE_TIME, "派奖时间");
		orderStrMap.put(PlanOrder.ORDER_AMOUNT, "订单金额");
		orderStrMap.put(PlanOrder.ORDER_POSTTAX_PRIZE, "税后奖金");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	
	public List<PlanOrderStatus> getOrderStatuses() {
		return PlanOrderStatus.getItems();
	}
	public List<PrizeStatus> getPrizeStatuses() {
		return PrizeStatus.getItems();
	}
	public List<PlanOrderType> getOrderTypes() {
		return PlanOrderType.getItems();
	}
	public List<YesNoStatus> getStatisticsList() {
		return YesNoStatus.getItems();
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
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
	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}
	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}
	public LotteryPlanOrderService getLotteryPlanOrderService() {
		return lotteryPlanOrderService;
	}
	public void setLotteryPlanOrderService(
			LotteryPlanOrderService lotteryPlanOrderService) {
		this.lotteryPlanOrderService = lotteryPlanOrderService;
	}
	public PlanOrder getPlanOrder() {
		return planOrder;
	}
	public void setPlanOrder(PlanOrder planOrder) {
		this.planOrder = planOrder;
	}
	public List<PlanOrder> getPlanOrders() {
		return planOrders;
	}
	public void setPlanOrders(List<PlanOrder> planOrders) {
		this.planOrders = planOrders;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Integer getPrizeStatus() {
		return prizeStatus;
	}
	public void setPrizeStatus(Integer prizeStatus) {
		this.prizeStatus = prizeStatus;
	}
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
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
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public List<LotteryType> getLotteryTypes() {
		List<LotteryType> list = new ArrayList<LotteryType>();
		list.add(LotteryType.ALL);
		list.addAll(OnSaleLotteryList.get());
		return list;
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public void setOrderSearchService(OrderSearchService orderSearchService) {
		this.orderSearchService = orderSearchService;
	}

	public OrderSearchService getOrderSearchService() {
		return orderSearchService;
	}

	public void setPhaseStr(String phaseStr) {
		this.phaseStr = phaseStr;
	}

	public String getPhaseStr() {
		return phaseStr;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setQueryTypeMap(Map<String, String> queryTypeMap) {
		this.queryTypeMap = queryTypeMap;
	}

	public Map<String, String> getQueryTypeMap() {
		queryTypeMap = new LinkedHashMap<String, String>();
		queryTypeMap.put(QUERY_STATISTICS, "仅统计");
		queryTypeMap.put(QUERY_DETAIL, "仅明细");
		queryTypeMap.put(QUERY_ALL, "统计及明细");
		return queryTypeMap;
	}

	public void setQueryFlag(String queryFlag) {
		this.queryFlag = queryFlag;
	}

	public String getQueryFlag() {
		return queryFlag;
	}
	
}
