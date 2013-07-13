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
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.*;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.util.CoreNumberUtil;
import com.lehecai.core.util.lottery.FetcherLotteryDrawConverter;
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

public class LotteryPlanAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(LotteryPlanAction.class);
	
	private LotteryPlanService lotteryPlanService;
	private PhaseService phaseService;
	private LotteryPlanOrderService lotteryPlanOrderService;
	
	private Plan plan;
	private String result;
	
	private List<Plan> plans;
	private List<PlanOrder> planOrders;
	
	private String userName;
	private String planId;
	private Integer lotteryTypeId;
	private Integer phaseTypeId;
	private String phase;
	private String writePhase;
	private Integer planTypeId;
	private Integer selectTypeId;
	private Integer playTypeId;
	private Integer planStatus;
	private Integer uploadStatus;
	private Integer publicStatus;
	private Integer resultStatus;
	private Integer allowAutoFollow;
	private String lotteryTypeValue;
	private String content;
	private Long sourceId;
	private Integer planCreateTypeId;
	
	private int top;
	
	private Integer minuteCount;//距方案发起时间
	private Integer seconds;//倒计时
	
	private Date rbeginDate;
	private Date rendDate;
	private Date lbeginDate;
	private Date lendDate;
	private Date pbeginDate;
	private Date pendDate;
	
	private String orderStr;
	private String orderView;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	private String nowDate;
	
	private String totalAmount;
	
	private String totalPrizePostTax;
	
	private Integer planTicketStatusValue;
	
	public String handle(){
		logger.info("进入方案统计查询");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入方案统计查询");
		nowDate = DateUtil.formatDate(new Date());
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		
		if (rbeginDate != null && rendDate != null) {
			if (!DateUtil.isSameMonth(rbeginDate, rendDate)) {
				logger.error("开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		if (lbeginDate != null && lendDate != null) {			
			if (!DateUtil.isSameMonth(lbeginDate, lendDate)) {
				logger.error("开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		if (pbeginDate != null && pendDate != null) {			
			if (!DateUtil.isSameMonth(pbeginDate, pendDate)) {
				logger.error("开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		
		List<String> planStatusList = null;
		if (planStatus != null && planStatus != PlanStatus.ALL.getValue()) {
			planStatusList = new ArrayList<String>();
			planStatusList.add(String.valueOf(planStatus));
		}
		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		PhaseType phaset = phaseTypeId == null ? null : PhaseType.getItem(phaseTypeId);
		PlanType pt = planTypeId == null ? null : PlanType.getItem(planTypeId);
		SelectType st = selectTypeId == null ? null : SelectType.getItem(selectTypeId);
		PlayType plt = playTypeId == null ? null : PlayType.getItem(playTypeId);
		YesNoStatus us = uploadStatus == null ? null : YesNoStatus.getItem(uploadStatus);
		PublicStatus ps = publicStatus == null ? null : PublicStatus.getItem(publicStatus);
		ResultStatus rs = resultStatus == null ? null : ResultStatus.getItem(resultStatus);
		YesNoStatus af = allowAutoFollow == null ? null : YesNoStatus.getItem(allowAutoFollow);
		PlanTicketStatus pts = planTicketStatusValue == null ? null : PlanTicketStatus.getItem(planTicketStatusValue);
		PlanCreateType pct = planCreateTypeId == null ? null : PlanCreateType.getItem(planCreateTypeId);
		
		Date lastTimeDateFrom = null;
		Date lastTimeDateTo = null;

		if (lastTimeDateFrom != null || lastTimeDateTo != null) {
			phase = null;
		}
		
		Map<String, Object> map;
		try {
			map = lotteryPlanService.getResult(userName,
					planId, lt, phaset, phase, pt, st,
					plt, planStatusList, us, ps,
					rs, af, pts, sourceId,
					rbeginDate, rendDate,
					lbeginDate, lendDate, pbeginDate, pendDate, lastTimeDateFrom, lastTimeDateTo, this.getOrderStr(), this.getOrderView(), super.getPageBean(),pct);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询方案统计，api调用异常，{}", e.getMessage());
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
			statisticsMap = this.lotteryPlanService.lotteryPlanStatistics(userName,
					planId, lt, phaset, phase, pt, st,
					plt, planStatusList, us, ps,
					rs, af, pts, sourceId, rbeginDate, rendDate,
					lbeginDate, lendDate, pbeginDate, pendDate, lastTimeDateFrom, lastTimeDateTo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("统计方案，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
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
					logger.error("方案总金额转换成double类型异常，{}", e);
				}
				totalAmount = CoreNumberUtil.formatNumBy2Digits(amountDou);
			} else {
				logger.error("方案总金额为空");
			}
			
			if (prizePostTaxObj != null) {
				double prizePostTaxDou = 0;
				try {
					prizePostTaxDou = Double.parseDouble(prizePostTaxObj.toString());
				} catch (Exception e) {
					logger.error("税后奖金金额转换成double类型异常，{}", e);
				}
				totalPrizePostTax = CoreNumberUtil.formatNumBy2Digits(prizePostTaxDou);
			} else {
				logger.error("税后奖金金额为空");
			}
		} else {
			logger.error("方案查询统计失败");
			super.setErrorMessage("方案查询统计失败");
			return "failure";
		}
		logger.info("查询方案统计结束");
		return "list";
	}
	
	public String queryOutOfTimeList() {
		logger.info("进入距发起时间方案统计查询");
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		return "outOfTimelist";
	}
	
	@SuppressWarnings("unchecked")
	public String queryRealOutOfTimeList() {
		logger.info("进入距发起时间方案统计查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.MINUTE, -this.getMinuteCount());
		rendDate = cd.getTime();
		
		List<String> planStatusList = null;
		if (planStatus != null && planStatus != PlanStatus.ALL.getValue()) {
			planStatusList = new ArrayList<String>();
			planStatusList.add(String.valueOf(planStatus));
		}
		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		PlanType pt = planTypeId == null ? null : PlanType.getItem(planTypeId);
		SelectType st = selectTypeId == null ? null : SelectType.getItem(selectTypeId);
		PlayType plt = playTypeId == null ? null : PlayType.getItem(playTypeId);
		YesNoStatus us = uploadStatus == null ? null : YesNoStatus.getItem(uploadStatus);
		PublicStatus ps = publicStatus == null ? null : PublicStatus.getItem(publicStatus);
		ResultStatus rs = resultStatus == null ? null : ResultStatus.getItem(resultStatus);
		YesNoStatus af = allowAutoFollow == null ? null : YesNoStatus.getItem(allowAutoFollow);
		PlanTicketStatus pts = planTicketStatusValue == null ? null : PlanTicketStatus.getItem(planTicketStatusValue);
		
		Map<String, Object> map;
		try {
			map = lotteryPlanService.getResult(userName,
					planId, lt, null, phase, pt, st,
					plt, planStatusList, us, ps,
					rs, af, pts, sourceId, rbeginDate, rendDate,
					lbeginDate, lendDate, null, null, null, null, this.getOrderStr(), this.getOrderView(), super.getPageBean(),null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询距发起时间方案，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {			
			plans = (List<Plan>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
//		Map<String, Object> statisticsMap;
//		try {
//			statisticsMap = this.lotteryPlanService.lotteryPlanStatistics(userName,
//					planId, lt, phase, pt, st,
//					plt, planStatusList, us, ps,
//					rs, af, pts, rbeginDate, rendDate,
//					lbeginDate, lendDate, null, null);
//		} catch (ApiRemoteCallFailedException e) {
//			logger.error(e.getMessage(),e);
//			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
//			return "failure";
//		}
//		
//		if (statisticsMap != null) {
//			Object amountObj = statisticsMap.get(Global.API_MAP_KEY_AMOUNT);
//			Object prizePostTaxObj = statisticsMap.get(Global.API_MAP_KEY_POSTTAXPRIZE);
//			if (amountObj != null) {
//				double amountDou = 0;
//				try {
//					amountDou = Double.parseDouble(amountObj.toString());
//				} catch (Exception e) {
//					logger.error("方案总金额转换成double类型异常amount={}", amountObj);
//				}
//				totalAmount = CoreNumberUtil.formatNumBy2Digits(amountDou);
//			} else {
//				logger.error("方案总金额为空");
//			}
//			
//			if (prizePostTaxObj != null) {
//				double prizePostTaxDou = 0;
//				try {
//					prizePostTaxDou = Double.parseDouble(prizePostTaxObj.toString());
//				} catch (Exception e) {
//					logger.error("税后奖金金额转换成double类型异常prizePostTax={}", prizePostTaxObj);
//				}
//				totalPrizePostTax = CoreNumberUtil.formatNumBy2Digits(prizePostTaxDou);
//			} else {
//				logger.error("税后奖金金额为空");
//			}
//		} else {
//			logger.error("统计方案金额失败");
//			super.setErrorMessage("统计方案金额失败");
//			return "failure";
//		}
		logger.info("查询距发起时间方案统计结束");
		return "outOfTimelist";
	}
	
	@SuppressWarnings("unchecked")
	public String view() {
		logger.info("进入方案详情查询");
        if (plan == null || StringUtils.isBlank(plan.getId())) {
            logger.error("查询方案详情异常，未指定方案号");
            super.setErrorMessage("查询方案详情异常，未指定方案号");
            return "failure";
        }

        String planId = plan.getId().trim();

        try {
            plan = lotteryPlanService.get(planId);
        } catch (ApiRemoteCallFailedException e) {
            logger.error("查询方案详情，api调用异常，{}", e.getMessage());
            super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
            return "failure";
        }

        if (plan == null) {
            logger.error("查询方案详情异常，未找到方案，plan_id={}", planId);
            super.setErrorMessage("查询方案详情异常，未找到方案" + planId);
            return "failure";
        }

        HttpServletRequest request = ServletActionContext.getRequest();
        orderStr = PlanOrder.ORDER_CREATED_TIME;
        orderView = ApiConstant.API_REQUEST_ORDER_DESC;

        Map<String, Object> map;
        try {
            Phase phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(plan.getPhaseType(), plan.getPhase());
            if (phase != null) {
                result = FetcherLotteryDrawConverter.convertResultJsonString2ShowString(phase.getResult());
            }
            map = lotteryPlanOrderService.getResult(null, null,
                    plan.getId(), null, null, null, null,
                    null, null, null, orderStr, orderView, null, null, super.getPageBean());
        } catch (ApiRemoteCallFailedException e) {
            logger.error("查询方案订单，api调用异常，{}", e.getMessage());
            super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
            return "failure";
        }
        if (map != null) {
            planOrders = (List<PlanOrder>)map.get(Global.API_MAP_KEY_LIST);
            PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
            super.setPageString(PageUtil.getPageString(request, pageBean));
            super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
        }

		logger.info("查询方案详情结束");
		return "view";
	}
	
	public String setTop() {
		boolean rs = false;
		HttpServletResponse response = ServletActionContext.getResponse();
		if (plan != null && plan.getId() != null && !"".equals(plan.getId())) {
			try {
				rs = lotteryPlanService.updateTopStatus(plan, top);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
			}
		}
		JSONObject obj = new JSONObject();
		if (rs) {		
			obj.put("rs", top);
		} else {
			obj.put("rs", -1);
		}
		writeRs(response, obj);
		
		return Action.NONE;
	}
	
	public String parseContent() {
		logger.info("进入解析方案内容");
		JSONObject object = new JSONObject();
		object.put("code", 1);
		object.put("content", "解析方案内容失败");
		if (planId == null || "".equals(planId)) {
			object.put("content", "请指定方案Id");
		} 
//		else if ((LotteryType.SFC.getValue()+"").equals(lotteryTypeValue)
//				|| (LotteryType.DC_SFP.getValue()+"").equals(lotteryTypeValue)
//				|| (LotteryType.DC_BF.getValue()+"").equals(lotteryTypeValue)
//				|| (LotteryType.DC_JQS.getValue()+"").equals(lotteryTypeValue)
//				|| (LotteryType.DC_BCSFP.getValue()+"").equals(lotteryTypeValue)
//				|| (LotteryType.DC_SXDS.getValue()+"").equals(lotteryTypeValue)
//				) {
//			object.put("content", "目前不支持足彩");
//		}
		else {
			try {
				String parseContent = lotteryPlanService.parseContent(planId);
				if (parseContent != null && !parseContent.isEmpty()) {
					object.put("code", 0);
					object.put("content", parseContent);
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("解析方案内容，api调用异常，{}", e.getMessage());
			}
		}
		writeRs(ServletActionContext.getResponse(), object);
		logger.info("解析方案内容结束");
		return null;
	}
	
	public void fetchPlayTypes() {
		logger.info("进入获取玩法类型列表");
		HttpServletResponse response = ServletActionContext.getResponse();
		List<PlayType> list = null;
		if (lotteryTypeId != null && lotteryTypeId != LotteryType.ALL.getValue()) {
			list = PlayType.getItemsByLotteryType(LotteryType.getItem(lotteryTypeId));
		}
		JSONArray ja = null;
		if (list != null && list.size() > 0) {
			ja = JSONArray.fromObject(list);
		}
		if (ja == null) {
			writeRs(response, "[]");
		}else{		
			writeRs(response, ja);
		}
		logger.info("获取玩法类型列表结束");
	}
	
	@SuppressWarnings("unchecked")
	public String getOverdues() {
		logger.info("进入过期方案查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		Map<String, Object> map;
		try {
			map = lotteryPlanService.getOverduePlans(super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询过期方案，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			plans = (List<Plan>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询过期方案结束");
		return "overdueList";
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
	public Integer getPhaseTypeId() {
		return phaseTypeId;
	}
	public void setPhaseTypeId(Integer phaseTypeId) {
		this.phaseTypeId = phaseTypeId;
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
	public Integer getPlanStatus() {
		return planStatus;
	}
	public void setPlanStatus(Integer planStatus) {
		this.planStatus = planStatus;
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
	public List<PhaseType> getPhaseTypes() {
		return PhaseType.getItems();
	}
	public List<PlanType> getPlanTypes() {
		return PlanType.getItems();
	}
	public List<SelectType> getSelectTypes() {
		return SelectType.getItems();
	}
	public List<PlanStatus> getPlanStatuses() {
		return PlanStatus.getItems();
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
	public Integer getMinuteCount() {
		if (minuteCount == null || minuteCount == 0) {
			minuteCount = 5;
		}
		return minuteCount;
	}
	public void setMinuteCount(Integer minuteCount) {
		this.minuteCount = minuteCount;
	}
	public Integer getSeconds() {
		if (seconds == null) {
			seconds = 60;
		}
		return seconds;
	}
	public void setSeconds(Integer seconds) {
		this.seconds = seconds;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Date getPbeginDate() {
		return pbeginDate;
	}
	public void setPbeginDate(Date pbeginDate) {
		this.pbeginDate = pbeginDate;
	}
	public Date getPendDate() {
		return pendDate;
	}
	public void setPendDate(Date pendDate) {
		this.pendDate = pendDate;
	}
	public PlanType getHmPlanType() {
		return PlanType.HM;
	}
	public PlanStatus getRecruitingPlanStatus() {
		return PlanStatus.RECRUITING;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public String getLotteryTypeValue() {
		return lotteryTypeValue;
	}
	public void setLotteryTypeValue(String lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getWritePhase() {
		return writePhase;
	}
	public void setWritePhase(String writePhase) {
		if (writePhase != null && !writePhase.isEmpty()) {
			setPhase(writePhase);
		}
		this.writePhase = writePhase;
	}

	public List<PlanTicketStatus> getPlanTicketStatus() {
		return PlanTicketStatus.getItems();
	}

	public Integer getPlanTicketStatusValue() {
		return planTicketStatusValue;
	}

	public void setPlanTicketStatusValue(Integer planTicketStatusValue) {
		this.planTicketStatusValue = planTicketStatusValue;
	}

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public List<PlanCreateType> getPlanCreateTypes(){
		return PlanCreateType.getItems();
	}

	public Integer getPlanCreateTypeId() {
		return planCreateTypeId;
	}

	public void setPlanCreateTypeId(Integer planCreateTypeId) {
		this.planCreateTypeId = planCreateTypeId;
	}
}
