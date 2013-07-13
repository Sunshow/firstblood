package web.action.lottery;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanOrderService;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.*;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.util.CoreNumberUtil;
import com.opensymphony.xwork2.Action;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class LotteryPlanRefundAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	
	private LotteryPlanService lotteryPlanService;
	private PhaseService phaseService;
	private LotteryPlanOrderService lotteryPlanOrderService;
	
	private Plan plan;
	
	private List<Plan> plans;
	private List<PlanOrder> planOrders;
	
	private String userName;
	private String planId;
	private Integer lotteryTypeId;
	private String phase;
	private Integer planTypeId;
	private Integer selectTypeId;
	private Integer playTypeId;
	private Integer uploadStatus;
	private Integer publicStatus;
	private Integer resultStatus;
	private Integer allowAutoFollow;
	
	private Date rbeginDate;
	private Date rendDate;
	private Date lbeginDate;
	private Date lendDate;
	
	private String orderStr;
	private String orderView;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	private String nowDate;
	
	private String totalAmount;
	
	private String totalPrizePostTax;
	
	private Integer planStatusValue;
	private Integer planTicketStatusValue;	
	
	private String refundStr;
	
	public String handle() {
		logger.info("进入方案退款查询");
		nowDate = DateUtil.formatDate(new Date());
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入方案退款查询");
		nowDate = DateUtil.formatDate(new Date());
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		
		if (rbeginDate != null && rendDate != null) {
			if (!DateUtil.isSameMonth(rbeginDate, rendDate)) {
				logger.error("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		if (lbeginDate != null && lendDate != null) {			
			if (!DateUtil.isSameMonth(lbeginDate, lendDate)) {
				logger.error("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		List<String> planStatusList = null;
		if (planStatusValue != null && planStatusValue != PlanStatus.ALL.getValue()) {
			planStatusList = new ArrayList<String>();
			planStatusList.add(String.valueOf(planStatusValue));
		} else {
			planStatusList = getPlanStatus();
		}
		PlanTicketStatus pts = planTicketStatusValue == null ? null : PlanTicketStatus.getItem(planTicketStatusValue);
		
		Map<String, Object> map = null;
		try {
			map = lotteryPlanService.getResult(userName,
					planId, parseLotteryType(), null, phase, parsePlanType(), parseSelectType(),
					parsePlayType(), planStatusList, parseYesNoStatus(uploadStatus), parsePublicStatus(),
					parseResultStatus(), parseYesNoStatus(allowAutoFollow), pts, null, rbeginDate, rendDate,
					lbeginDate, lendDate, null, null, null, null, this.getOrderStr(), this.getOrderView(), super.getPageBean(),null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {			
			plans = (List<Plan>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		Map<String, Object> statisticsMap;
		try {
			statisticsMap = lotteryPlanService.lotteryPlanStatistics(userName,
					planId, parseLotteryType(), null, phase, parsePlanType(), parseSelectType(),
					parsePlayType(), planStatusList, parseYesNoStatus(uploadStatus), parsePublicStatus(),
					parseResultStatus(), parseYesNoStatus(allowAutoFollow), pts, null, rbeginDate, rendDate,
					lbeginDate, lendDate, null, null, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
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
					super.setErrorMessage("总金额转换成double类型异常，原因：" + e);
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
					super.setErrorMessage("税后奖金金额转换成double类型异常，原因：" + e);
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
			logger.error("统计金额异常");
			super.setErrorMessage("统计金额异常");
			return "failure";
		}
		logger.info("方案退款查询结束");
		return "list";
	}
	
	private LotteryType parseLotteryType() {
		if (lotteryTypeId == null || lotteryTypeId == LotteryType.ALL.getValue()) {
			return null;
		}
		return LotteryType.getItem(lotteryTypeId);
	}
	
	private ResultStatus parseResultStatus() {
		if (resultStatus == null || resultStatus == ResultStatus.ALL.getValue()) {
			return null;
		}
		return ResultStatus.getItem(resultStatus);
	}
	
	private PublicStatus parsePublicStatus() {
		if (publicStatus == null || publicStatus == PublicStatus.ALL.getValue()) {
			return null;
		}
		return PublicStatus.getItem(publicStatus);
	}
	
	private YesNoStatus parseYesNoStatus(Integer status) {
		if (status == null || status == YesNoStatus.ALL.getValue()) {
			return null;
		}
		return YesNoStatus.getItem(status);
	}
	
	private PlayType parsePlayType() {
		if (playTypeId == null || playTypeId == PlayType.ALL.getValue()) {
			return null;
		}
		return PlayType.getItem(playTypeId);
	}
	
	private SelectType parseSelectType() {
		if (selectTypeId == null || selectTypeId == SelectType.ALL.getValue()) {
			return null;
		}
		return SelectType.getItem(selectTypeId);
	}
	
	private PlanType parsePlanType() {
		if (planTypeId == null) {
			return null;
		}
		return PlanType.getItem(planTypeId);
	}
	
	@SuppressWarnings("unchecked")
	public String view() {
		logger.info("进入退款方案详情查询");
		if (plan != null && plan.getId() != null && !"".equals(plan.getId())) {
			try {
				plan = lotteryPlanService.get(plan.getId());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			orderStr = PlanOrder.ORDER_CREATED_TIME;
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
			
			Map<String, Object> map = null;
			try {
				map = lotteryPlanOrderService.getResult(null,
						 null, plan.getId(), null, null, null, null,
						null, null, null, orderStr, orderView, null, null, super.getPageBean());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			if (map != null) {			
				planOrders = (List<PlanOrder>)map.get(Global.API_MAP_KEY_LIST);
				PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
				super.setPageString(PageUtil.getPageString(request, pageBean));
				super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
			}
		}else{
			return "failure";
		}
		logger.info("查询退款方案详情结束");
		return "view";
	}
	
	public String batchRefund() {
		logger.info("进入批量退款");
		String successIds = "";
		String errorIds = "";
        String nochangeIds = "";
		if (refundStr != null && !refundStr.equals("")){
			String [] str = StringUtils.split(refundStr, ",");

            List<Plan> planList = new ArrayList<Plan>();
            for (String planId : str) {
                Plan latestPlan = null;
                try {
                    latestPlan = lotteryPlanService.get(planId);
                } catch (ApiRemoteCallFailedException e) {
                    logger.error(e.getMessage(), e);
                }

                if (latestPlan == null) {
                    logger.error("查找方案失败, id={}", planId);
                    continue;
                }

                planList.add(latestPlan);
            }

            List<String> successList = new ArrayList<String>();
            List<String> failureList = new ArrayList<String>();
            List<String> nochangeList = new ArrayList<String>();

            try {
                lotteryPlanService.returnPlanTicket(planList, null, successList, failureList, nochangeList);
            } catch (ApiRemoteCallFailedException e) {
                logger.error(e.getMessage(), e);
            }

            successIds = StringUtils.join(successList, ",");
            errorIds = StringUtils.join(failureList, ",");
            nochangeIds = StringUtils.join(nochangeList, ",");
		}
		JSONObject json = new JSONObject();
		json.put("msg", "成功[" + successIds + "]\n失败[" + errorIds + "]\n未改变[" + nochangeIds + "]");
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("批量退款结束");
		return Action.NONE;
	}
	
	public String refund() {
		logger.info("进入退款");
		if (plan != null && StringUtils.isNotEmpty(plan.getId())) {
			Plan latestPlan = null;
			try {
				latestPlan = lotteryPlanService.get(plan.getId());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
			}

            if (latestPlan == null) {
                logger.error("查找方案失败, id={}", plan.getId());
                super.setErrorMessage("查找方案失败, id=" + plan.getId());
            } else {

                List<Plan> planList = new ArrayList<Plan>();
                planList.add(latestPlan);

                List<String> successList = new ArrayList<String>();

                try {
                    lotteryPlanService.returnPlanTicket(planList, null, successList, null, null);
                } catch (ApiRemoteCallFailedException e) {
                    logger.error(e.getMessage(), e);
                }

                if (successList.size() == 1) {
                    logger.info("方案({})退款成功", latestPlan.getId());
                    super.setErrorMessage("方案(" + latestPlan.getId() + ")退款成功");
                } else {
                    logger.info("方案({})退款失败", latestPlan.getId());
                    super.setErrorMessage("方案(" + latestPlan.getId() + ")退款失败");
                }
            }
		}
		logger.info("退款结束");
		return handle();
	}
	public String updateCanNotSplit() {
		logger.info("进入方案票状态置为不可拆票");
		if (plan != null && plan.getId() != null && !"".equals(plan.getId())) {
			PlanTicketStatus status = PlanTicketStatus.CAN_NOT_SPLIT;
			if (lotteryPlanService.updateTicketStatus(plan, status)) {
				logger.info("修改状态为[{}]成功", status.getName());
				super.setErrorMessage("修改状态为["+status.getName()+"]成功");
			} else {
				logger.info("修改状态为[{}]失败", status.getName());
				super.setErrorMessage("修改状态为["+status.getName()+"]失败");
			}
		}
		logger.info("退款结束");
		return handle();
	}
	
	public String batchCanSplit() {
		logger.info("进入方案票状态置为可拆票");
		String successIds = "";
		String errorIds = "";
		if (plans != null && plans.size() > 0) {
			PlanTicketStatus status = PlanTicketStatus.CAN_SPLIT;
			for (Plan p : plans) {
				if (p != null && p.getId() != null && !p.getId().equals("")) {
					if (lotteryPlanService.updateTicketStatus(p, status)) {
						logger.info("修改状态为[{}]成功", status.getName());
						successIds += p.getId() + ",\n";
					} else {
						logger.info("修改状态为[{}]失败", status.getName());
						errorIds += p.getId() + ",\n";
					}
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("msg", "成功[" + successIds + "]\n失败[" + errorIds + "]");
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("批量方案票状态置为可拆票结束");
		return Action.NONE;
	}
	
	public String batchCanNotSplit() {
		logger.info("进入方案票状态置为不可拆票");
		String successIds = "";
		String errorIds = "";
		if (plans != null && plans.size() > 0) {
			PlanTicketStatus status = PlanTicketStatus.CAN_NOT_SPLIT;
			for (Plan p : plans) {
				if (p != null && p.getId() != null && !p.getId().equals("")) {
					if (lotteryPlanService.updateTicketStatus(p, status)) {
						logger.info("修改状态为[{}]成功", status.getName());
						successIds += p.getId() + ",\n";
					} else {
						logger.info("修改状态为[{}]失败", status.getName());
						errorIds += p.getId() + ",\n";
					}
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("msg", "成功[" + successIds + "]\n失败[" + errorIds + "]");
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("批量方案票状态置为不可拆票结束");
		return Action.NONE;
	}
	
	public String updateCanSplit() {
		logger.info("进入方案票状态置为可拆票");
		if (plan != null && plan.getId() != null && !"".equals(plan.getId())) {
			PlanTicketStatus status = PlanTicketStatus.CAN_SPLIT;
			if (lotteryPlanService.updateTicketStatus(plan, status)) {
				logger.info("修改状态为[{}]成功", status.getName());
				super.setErrorMessage("修改状态为["+status.getName()+"]成功");
			} else {
				logger.info("修改状态为[{}]失败", status.getName());
				super.setErrorMessage("修改状态为["+status.getName()+"]失败");
			}
		}
		logger.info("退款结束");
		return handle();
	}
	
	public void fetchPlayTypes() {
		logger.info("进入获取玩法列表");
		HttpServletResponse response = ServletActionContext.getResponse();
		List<PlayType> list = null;
		if (lotteryTypeId != null && lotteryTypeId != LotteryType.ALL.getValue()) {
			list = PlayType.getItemsByLotteryType(parseLotteryType());
		}
		JSONArray ja = null;
		if (list != null && list.size() > 0) {
			ja = JSONArray.fromObject(list);
		}
		if (ja == null) {
			writeRs(response, "[]");
		} else {		
			writeRs(response, ja);
		}
		logger.info("获取玩法列表结束");
	}
	
	public String getOrderStr() {
		if (orderStr == null || "".equals(orderStr)) {
			orderStr = Plan.ORDER_CREATED_TIME;
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
		orderStrMap.put(Plan.ORDER_ID, "方案编号");
		orderStrMap.put(Plan.ORDER_PHASE, "彩期");
		orderStrMap.put(Plan.ORDER_CREATED_TIME, "创建时间");
		orderStrMap.put(Plan.ORDER_DEAD_LINE, "截止时间");
		orderStrMap.put(Plan.ORDER_PRINT_TIME, "出票时间");
		orderStrMap.put(Plan.ORDER_AMOUNT, "方案金额");
		orderStrMap.put(Plan.ORDER_PRETAX_PRIZE, "税前奖金");
		orderStrMap.put(Plan.ORDER_POSTTAX_PRIZE, "税后奖金");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	//获得可退款的状态
	private List<String> getPlanStatus() {
		List<String> list = new ArrayList<String>();
		list.add(String.valueOf(PlanStatus.PRINT_WAITING.getValue()));
		list.add(String.valueOf(PlanStatus.PRINTING.getValue()));
		list.add(String.valueOf(PlanStatus.PAID_NOT.getValue()));
		list.add(String.valueOf(PlanStatus.RECRUITING.getValue()));
		return list;
	}
	
	
	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}
	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}
	public Plan getPlan() {
		return plan;
	}
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	public List<Plan> getPlans() {
		return plans;
	}
	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
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
	public Integer getPlanTypeId() {
		return planTypeId;
	}
	public void setPlanTypeId(Integer planTypeId) {
		this.planTypeId = planTypeId;
	}
	public Integer getSelectTypeId() {
		return selectTypeId;
	}
	public void setSelectTypeId(Integer selectTypeId) {
		this.selectTypeId = selectTypeId;
	}
	public Integer getPlayTypeId() {
		return playTypeId;
	}
	public void setPlayTypeId(Integer playTypeId) {
		this.playTypeId = playTypeId;
	}
	public Integer getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(Integer uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public Integer getPublicStatus() {
		return publicStatus;
	}
	public void setPublicStatus(Integer publicStatus) {
		this.publicStatus = publicStatus;
	}
	public Integer getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(Integer resultStatus) {
		this.resultStatus = resultStatus;
	}
	public Integer getAllowAutoFollow() {
		return allowAutoFollow;
	}
	public void setAllowAutoFollow(Integer allowAutoFollow) {
		this.allowAutoFollow = allowAutoFollow;
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
	public List<LotteryType> getLotteryTypes() {
		return OnSaleLotteryList.getForQuery();
	}
	public List<PlanType> getPlanTypes() {
		return PlanType.getItems();
	}
	public List<SelectType> getSelectTypes() {
		return SelectType.getItems();
	}
	//可退款的方案票状态
	public List<PlanStatus> getPlanStatuses() {
		List<PlanStatus> list = new ArrayList<PlanStatus>();
		list.add(PlanStatus.ALL);
		list.add(PlanStatus.PRINT_WAITING);
		list.add(PlanStatus.PRINTING);
		list.add(PlanStatus.PAID_NOT);
		list.add(PlanStatus.RECRUITING);
		return list;
	}
	public List<PlanTicketStatus> getPlanTicketStatuses() {
		return PlanTicketStatus.getItems();
	}
	public List<YesNoStatus> getYesNoStatuses() {
		return YesNoStatus.getItemsForQuery();
	}
	public List<PublicStatus> getPublicStatuses() {
		return PublicStatus.getItems();
	}
	public List<ResultStatus> getResultStatuses() {
		return ResultStatus.getItems();
	}
	public PhaseService getPhaseService() {
		return phaseService;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	public String getNowDate() {
		return nowDate;
	}
	public void setNowDate(String nowDate) {
		this.nowDate = nowDate;
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
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalPrizePostTax(String totalPrizePostTax) {
		this.totalPrizePostTax = totalPrizePostTax;
	}
	public String getTotalPrizePostTax() {
		return totalPrizePostTax;
	}
	
	public PlanTicketStatus getCanNotSplitStatus() {
		return PlanTicketStatus.CAN_NOT_SPLIT;
	}
	public PlanTicketStatus getCanSplitStatus() {
		return PlanTicketStatus.CAN_SPLIT;
	}

	public Integer getPlanStatusValue() {
		return planStatusValue;
	}

	public void setPlanStatusValue(Integer planStatusValue) {
		this.planStatusValue = planStatusValue;
	}

	public Integer getPlanTicketStatusValue() {
		return planTicketStatusValue;
	}

	public void setPlanTicketStatusValue(Integer planTicketStatusValue) {
		this.planTicketStatusValue = planTicketStatusValue;
	}

	public String getRefundStr() {
		return refundStr;
	}

	public void setRefundStr(String refundStr) {
		this.refundStr = refundStr;
	}
	public PlanTicketStatus getCanNotSplit() {
		return PlanTicketStatus.CAN_NOT_SPLIT;
	}
	public PlanTicketStatus getCanSplit() {
		return PlanTicketStatus.CAN_SPLIT;
	}
}
