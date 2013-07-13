package web.action.lottery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lehecai.core.lottery.cache.HighFrequencyLottery;
import com.lehecai.core.lottery.cache.NumericLottery;
import com.lehecai.core.lottery.cache.SportsLottery;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.enums.LotteryAmountType;
import com.lehecai.admin.web.service.lottery.LotteryHmSetTopService;
import com.lehecai.admin.web.service.lottery.LotteryPlanOrderService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.user.UserLevelService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.api.user.MemberLevel;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.PlanStatus;
import com.lehecai.core.lottery.PlanType;
import com.lehecai.core.lottery.PublicStatus;
import com.lehecai.core.lottery.ResultStatus;
import com.lehecai.core.lottery.SelectType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.util.CoreNumberUtil;
import com.opensymphony.xwork2.Action;

public class LotteryHmSetTopAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(LotteryHmSetTopAction.class);
	
	private static final String GPC_IMG_URL= "/images/zhanji/gpc_";
	private static final String JJC_IMG_URL= "/images/zhanji/jjc_";
	private static final String SZC_IMG_URL= "/images/zhanji/szc_";
	
	private LotteryHmSetTopService lotteryHmSetTopService;
	private PhaseService phaseService;
	private LotteryPlanOrderService lotteryPlanOrderService;
	private UserLevelService userLevelService;
	
	private Plan plan;
	private String result;
	
	private List<Plan> plans;
	private List<MemberLevel> mlList;
	private List<PlanOrder> planOrders;
	
	private String userName;
	private String planId;
	private Integer lotteryTypeId;
	private String phase;
	private Integer amount;
	
	
	private String lotteryTypeValue;
	private String content;
	
	private int top;
	
	private Integer minuteCount;//距方案发起时间
	private Integer seconds;//倒计时
	
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
	
	class InnerBean{
		private Plan plan;
		private MemberLevel memberLevel;
		
		public Plan getPlan() {
			return plan;
		}
		public void setPlan(Plan plan) {
			this.plan = plan;
		}
		public MemberLevel getMemberLevel() {
			return memberLevel;
		}
		public void setMemberLevel(MemberLevel memberLevel) {
			this.memberLevel = memberLevel;
		}
		
		public int getLevel() {
			LotteryType lt = this.getPlan().getLotteryType();
			if (HighFrequencyLottery.contains(lt)) {
				return this.getMemberLevel().getHighfreqLevel();
			}
			if (SportsLottery.contains(lt)) {
				return this.getMemberLevel().getSportsLevel();
			}
			if (NumericLottery.contains(lt)) {
				return this.getMemberLevel().getNumericLevel();
			}
			return 0;
		}
		public String getLevelImgURL() {
			LotteryType lt = this.getPlan().getLotteryType();
			if (HighFrequencyLottery.contains(lt)) {
				return GPC_IMG_URL + this.getMemberLevel().getHighfreqLevel() + ".gif";
			}
			if (SportsLottery.contains(lt)) {
				return JJC_IMG_URL + this.getMemberLevel().getSportsLevel() + ".gif";
			}
			if (NumericLottery.contains(lt)) {
				return SZC_IMG_URL + this.getMemberLevel().getNumericLevel() + ".gif";
			}
			return "";
		}
		public String getPhase() {
			return this.getPlan().getPhase();
		}
		public String getId() {
			return this.getPlan().getId();
		}
		public LotteryType getLotteryType() {
			return this.getPlan().getLotteryType();
		}
		public PhaseType getPhaseType() {
			return this.getPlan().getPhaseType();
		}
		public long getUid() {
			return this.getPlan().getUid();
		}
		public String getUsername() {
			return this.getPlan().getUsername();
		}
		public long getAmount() {
			return this.getPlan().getAmount();
		}
		public long getParts() {
			return this.getPlan().getParts();
		}
		public long getFounderParts() {
			return this.getPlan().getFounderParts();
		}
		public Date getCreatedTime() {
			return this.getPlan().getCreatedTime();
		}

		public long getReservedParts() {
			return this.getPlan().getReservedParts();
		}

		public long getSoldParts() {
			return this.getPlan().getSoldParts();
		}

		public Date getDeadline() {
			return this.getPlan().getDeadline();
		}
		public int getPlanTop() {
			return this.getPlan().getTop();
		}

		public PlanStatus getPlanStatus() {
			return this.getPlan().getPlanStatus();
		}
		public PlanType getPlanType() {
			return this.getPlan().getPlanType();
		}
    }
	
	private List<InnerBean> innerBeans;
	
	public String handle() {
		logger.info("进入合买方案查询");
		nowDate = DateUtil.formatDate(new Date());
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入合买方案查询");
		nowDate = DateUtil.formatDate(new Date());
		HttpServletRequest request = ServletActionContext.getRequest();
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
		
		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		
		Map<String, Object> map;
		try {
			map = lotteryHmSetTopService.getResult(userName,
					planId, lt, phase, amount,
					rbeginDate, rendDate,
					lbeginDate, lendDate, this.getOrderStr(), this.getOrderView(), super.getPageBean());
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
		
		if (plans == null || plans.isEmpty()) {
			return "list";
		}
		
		Set<String> uids = new HashSet<String>();
		for (Plan plan : plans) {
			uids.add(String.valueOf(plan.getUid()));
		}
		
		Map<String, Object> levelMap;
		if (uids != null && uids.size() > 0) {
			try {
				levelMap = userLevelService.getUsersLevel(new ArrayList<String>(uids));
				mlList = (List<MemberLevel>) levelMap.get(Global.API_MAP_KEY_LIST);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		}
		
		Map<String, MemberLevel> userLevelMap = new HashMap<String, MemberLevel>();
		if (mlList != null) {
			for (MemberLevel level : mlList) {
				userLevelMap.put(String.valueOf(level.getUid()), level);
			}
		}
		
		innerBeans = new ArrayList<InnerBean>();
		for (Plan plan : plans) {
			InnerBean ib = new InnerBean();
			
			ib.setPlan(plan);
			ib.setMemberLevel(userLevelMap.get(String.valueOf(plan.getUid())));
			
			innerBeans.add(ib);
		}
		
		Map<String, Object> statisticsMap;
		try {
			statisticsMap = this.lotteryHmSetTopService.lotteryPlanStatistics(userName,
					planId, lt, phase, amount, rbeginDate, rendDate,
					lbeginDate, lendDate);
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
		logger.info("查询合买方案结束");
		return "list";
	}
	
	public String setTop() {
		logger.info("进入置顶");
		boolean rs = false;
		HttpServletResponse response = ServletActionContext.getResponse();
		if (plan != null && plan.getId() != null && !"".equals(plan.getId())) {
			try {
				rs = lotteryHmSetTopService.updateTopStatus(plan, top);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
			}
			
		}
		JSONObject obj = new JSONObject();
		obj.put("rs", top);
		obj.put("success", rs);
		
		writeRs(response, obj);
		logger.info("置顶结束");
		return Action.NONE;
	}
	
	public String getOrderStr() {
		if (orderStr == null || "".equals(orderStr)) {
			orderStr = Plan.ORDER_PROGRESS;
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
		orderStrMap.put(Plan.ORDER_AMOUNT, "方案金额");
		orderStrMap.put(Plan.ORDER_PROGRESS, "进度");
		orderStrMap.put(Plan.ORDER_TOP, "置顶值");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
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
	public List<LotteryAmountType> getLotteryAmountTypes() {
		List<LotteryAmountType> lotteryAmountTypes = new ArrayList<LotteryAmountType>();
		lotteryAmountTypes.add(LotteryAmountType.F0T50);
		lotteryAmountTypes.add(LotteryAmountType.F50T100);
		lotteryAmountTypes.add(LotteryAmountType.F100T200);
		lotteryAmountTypes.add(LotteryAmountType.F200T500);
		lotteryAmountTypes.add(LotteryAmountType.F500T1000);
		lotteryAmountTypes.add(LotteryAmountType.F1000T10000);
		lotteryAmountTypes.add(LotteryAmountType.F10000T100000);
		lotteryAmountTypes.add(LotteryAmountType.F100000);
		
		return lotteryAmountTypes;
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

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getAmount() {
		return amount;
	}

	public LotteryHmSetTopService getLotteryHmSetTopService() {
		return lotteryHmSetTopService;
	}

	public void setLotteryHmSetTopService(
			LotteryHmSetTopService lotteryHmSetTopService) {
		this.lotteryHmSetTopService = lotteryHmSetTopService;
	}

	public UserLevelService getUserLevelService() {
		return userLevelService;
	}

	public void setUserLevelService(UserLevelService userLevelService) {
		this.userLevelService = userLevelService;
	}

	public List<MemberLevel> getMlList() {
		return mlList;
	}

	public void setMlList(List<MemberLevel> mlList) {
		this.mlList = mlList;
	}

	public List<InnerBean> getInnerBeans() {
		return innerBeans;
	}

	public void setInnerBeans(List<InnerBean> innerBeans) {
		this.innerBeans = innerBeans;
	}

}
