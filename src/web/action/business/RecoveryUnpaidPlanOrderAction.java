package web.action.business;

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
import com.lehecai.admin.web.service.lottery.LotteryPlanOrderService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PlanOrderStatus;
import com.lehecai.core.lottery.PlanOrderType;
import com.lehecai.core.lottery.PrizeStatus;
import com.lehecai.core.util.CoreNumberUtil;

public class RecoveryUnpaidPlanOrderAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(RecoveryUnpaidPlanOrderAction.class);
	
	private LotteryPlanOrderService lotteryPlanOrderService;
	
	private List<PlanOrder> planOrders;
	
	private String userName;
	private String uid;
	private String planId;
	private String orderId;
	private Integer orderType;
	
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
	
	public String handle() {
		logger.info("进入查询未支付订单");
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询未支付订单");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		
		if(rbeginDate != null && rendDate != null){			
			if(!DateUtil.isSameMonth(rbeginDate, rendDate)){
				logger.error("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		if(lbeginDate != null && lendDate != null){			
			if(!DateUtil.isSameMonth(lbeginDate, lendDate)){
				logger.error("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		
		PlanOrderType pot = orderType == null ? null : PlanOrderType.getItem(orderType);
		
		Map<String, Object> map;
		try {
			map = lotteryPlanOrderService.getResult(userName,
					orderId, planId, PlanOrderStatus.PAID_NOT, null, pot, rbeginDate,
					rendDate, lbeginDate, lendDate, this.getOrderStr(), this.getOrderView(),null , null, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询未支付订单，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){			
			planOrders = (List<PlanOrder>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		Map<String, Object> statisticsMap;
		try {
			statisticsMap = this.lotteryPlanOrderService.lotteryPlanOrderStatistics(userName,
					orderId, planId, PlanOrderStatus.PAID_NOT, null, pot,
					rbeginDate, rendDate, lbeginDate, lendDate, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("统计未支付订单，api调用异常，{}", e.getMessage());
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
					logger.error("格式化税后奖金金额异常");
					super.setErrorMessage("格式化税后奖金金额异常");
					return "failure";
				}
			} else {
				logger.info("税后奖金金额为空");
			}
		} else {
			logger.error("统计金额失败");
			super.setErrorMessage("统计金额失败");
			return "failure";
		}
		logger.info("查询未支付订单结束");
		return "list";
	}
	
	public String recover() {
		logger.info("进入回收未支付订单");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", -1);
		try {
			lotteryPlanOrderService.counterResetOrderNotPaid(uid);
			lotteryPlanOrderService.updatePlanOrderStatus(orderId, PlanOrderStatus.PAID_NOT_CANCELLED);
			jsonObject.put("code", 0);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("回收未支付订单，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			jsonObject.put("msg", "api调用异常，请联系技术人员!原因:" + e.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), jsonObject);
		logger.info("回收未支付订单结束");
		return null;
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
	public String getOrderStr() {
		if(orderStr == null || "".equals(orderStr)){
			orderStr = PlanOrder.ORDER_CREATED_TIME;
		}
		return orderStr;
	}
	public String getOrderView() {
		if(orderView == null || "".equals(orderView)){
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public LotteryPlanOrderService getLotteryPlanOrderService() {
		return lotteryPlanOrderService;
	}
	public void setLotteryPlanOrderService(
			LotteryPlanOrderService lotteryPlanOrderService) {
		this.lotteryPlanOrderService = lotteryPlanOrderService;
	}
	public List<PlanOrder> getPlanOrders() {
		return planOrders;
	}
	public void setPlanOrders(List<PlanOrder> planOrders) {
		this.planOrders = planOrders;
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
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
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
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public List<PlanOrderStatus> getOrderStatuses(){
		return PlanOrderStatus.getItems();
	}
	public List<PrizeStatus> getPrizeStatuses(){
		return PrizeStatus.getItems();
	}
	public List<PlanOrderType> getOrderTypes(){
		return PlanOrderType.getItems();
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
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
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
}
