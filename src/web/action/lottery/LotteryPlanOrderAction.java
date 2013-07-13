package web.action.lottery;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.action.member.MemberAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanOrderService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlanOrderStatus;
import com.lehecai.core.lottery.PlanOrderType;
import com.lehecai.core.lottery.PrizeStatus;
import com.lehecai.core.util.CoreNumberUtil;

public class LotteryPlanOrderAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(MemberAction.class);
	
	private LotteryPlanOrderService lotteryPlanOrderService;
	
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
	
	private Date rbeginDate;
	private Date rendDate;
	private Date lbeginDate;
	private Date lendDate;
	
	private String orderStr;
	private String orderView;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	private String totalAmount;
	
	private String totalPrizePostTax;
	
	
	public String handle(){
		logger.info("进入订单统计查询");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入订单统计查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		
		if (rbeginDate != null && rendDate != null) {
			if (!DateUtil.isSameMonth(rbeginDate, rendDate)) {
				logger.error("开始时间和结束时间必须在同一年同一月");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		if (lbeginDate != null && lendDate != null) {			
			if (!DateUtil.isSameMonth(lbeginDate, lendDate)) {
				logger.error("开始时间和结束时间必须在同一年同一月");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		
		PlanOrderStatus pos = orderStatus == null ? null : PlanOrderStatus.getItem(orderStatus);
		PrizeStatus ps = prizeStatus == null ? null : PrizeStatus.getItem(prizeStatus);
		PlanOrderType pot = orderType == null ? null : PlanOrderType.getItem(orderType);
		LotteryType l = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		
		Map<String, Object> map;
		try {
			map = lotteryPlanOrderService.getResult(userName,
					orderId, planId, pos, ps, pot, rbeginDate,
					rendDate, lbeginDate, lendDate, this.getOrderStr(),
					this.getOrderView(), l, phase, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取订单统计，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {			
			planOrders = (List<PlanOrder>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		Map<String, Object> statisticsMap;
		try {
			statisticsMap = lotteryPlanOrderService.lotteryPlanOrderStatistics(userName,
					orderId, planId, pos, ps, pot,
					rbeginDate, rendDate, lbeginDate, lendDate, l, phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("统计订单，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (statisticsMap != null) {
			Object amountObj = statisticsMap.get(Global.API_MAP_KEY_AMOUNT);
			Object prizePostTaxObj = statisticsMap.get(Global.API_MAP_KEY_POSTTAXPRIZE);
			if (amountObj != null) {
				double amountDou = 0;
				try {
					amountDou = Double.parseDouble(amountObj.toString());
				} catch (Exception e) {
					logger.error("总金额转换成double类型异常，{}", e);
					super.setErrorMessage("总金额转换成double类型异常");
					return "failure";
				}
				totalAmount = CoreNumberUtil.formatNumBy2Digits(amountDou);
				if (totalAmount == null || "".equals(totalAmount)) {
					logger.error("格式化总金额异常");
					super.setErrorMessage("格式化总金额异常");
					return "failure";
				}
			} else {
				logger.info("总金额为空");
			}
			
			if (prizePostTaxObj != null) {
				double prizePostTaxDou = 0;
				try {
					prizePostTaxDou = Double.parseDouble(prizePostTaxObj.toString());
				} catch (Exception e) {
					logger.error("税后奖金金额转换成double类型异常，{}", e);
					super.setErrorMessage("税后奖金金额转换成double类型异常");
					return "failure";
				}
				totalPrizePostTax = CoreNumberUtil.formatNumBy2Digits(prizePostTaxDou);
				if (totalPrizePostTax == null || "".equals(totalPrizePostTax)) {
					logger.error("格式化中奖金额错误");
					super.setErrorMessage("格式化中奖金额错误");
					return "failure";
				}
			} else {
				logger.info("税后奖金总额为空.");
			}
		} else {
			logger.error("统计金额异常");
			super.setErrorMessage("统计金额异常");
			return "failure";
		}
		logger.info("查询订单统计结束");
		return "list";
	}
	
	public String view() {
		logger.info("进入查看订单详情");
		if (planOrder != null && planOrder.getId() != null && !"".equals(planOrder.getId())) {
			try {
				planOrder = lotteryPlanOrderService.get(planOrder.getId());
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查看订单详情，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		} else {
			return "failure";
		}
		logger.info("查看订单详情结束");
		return "view";
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
		return LotteryType.getItems();
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
	
}
