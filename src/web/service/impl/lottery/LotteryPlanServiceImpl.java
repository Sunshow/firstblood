package web.service.impl.lottery;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.ticket.TicketService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.*;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.*;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketStatus;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LotteryPlanServiceImpl implements LotteryPlanService {
	private final Logger logger = LoggerFactory.getLogger(LotteryPlanServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private MemberService memberService;

    private TicketService ticketService;
	
	@Override
	public Map<String, Object> getResult(String userName,
			String planId, LotteryType lotteryType, PhaseType phaseType, String phase, PlanType planType, SelectType selectType,
			PlayType playType, List<String> planStatus, YesNoStatus uploadStatus, PublicStatus publicStatus,
			ResultStatus resultStatus, YesNoStatus allowAutoFollow, PlanTicketStatus planTicketStatus, Long sourceId, Date rbeginDate, Date rendDate,
			Date lbeginDate, Date lendDate, Date pbeginDate, Date pendDate, Date lastMatchTimeFrom, Date lastMatchTimeTo, String orderStr, String orderView, PageBean pageBean, PlanCreateType planCreateType) throws ApiRemoteCallFailedException {
		logger.info("进入调用API多条件查询方案列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_LIST_QUERY);
		
		if (userName != null && !"".equals(userName)) {
			Member member = memberService.get(userName);
			if (member != null) {				
				request.setParameter(Plan.QUERY_UID, member.getUid() + "");
			} else {
				request.setParameter(Plan.QUERY_UID, "");
			}
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_LOTTERY_TYPE, lotteryType.getValue()+"");
		}
		if (phaseType != null && phaseType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PHASE_TYPE, phaseType.getValue()+"");
		}
		if (phase != null && !"-1".equals(phase)) {
			request.setParameter(Plan.QUERY_PHASE, phase);
		}
		if (planType != null && planType.getValue() != PlanType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PLAN_TYPE, planType.getValue()+"");
		}
		if (selectType != null && selectType.getValue() != SelectType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_SELECT_TYPE, selectType.getValue()+"");
		}
		if (playType != null && playType.getValue() != PlayType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PLAY_TYPE, playType.getValue()+"");
		}
		if (planStatus != null && !planStatus.isEmpty()) {
			request.setParameterIn(Plan.QUERY_PLAN_STATUS, planStatus);
		}
		if (uploadStatus != null && uploadStatus.getValue() != YesNoStatus.ALL.getValue()) {
			if (uploadStatus.getValue() == YesNoStatus.YES.getValue()) {
				request.setParameterGreater(Plan.QUERY_UPLOAD_ID, "0");
			} else {
				request.setParameter(Plan.QUERY_UPLOAD_ID, "0");
			}
		}
		if (publicStatus != null && publicStatus.getValue() != PublicStatus.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PUBLIC_STATUS, publicStatus.getValue()+"");
		}
		if (resultStatus != null && resultStatus.getValue() != ResultStatus.ALL.getValue()) {
			request.setParameter(Plan.QUERY_RESULT_STATUS, resultStatus.getValue() + "");
		}
		if (allowAutoFollow != null && allowAutoFollow.getValue() != YesNoStatus.ALL.getValue()) {
			request.setParameter(Plan.QUERY_ALLOW_AUTO_FOLLOW, allowAutoFollow.getValue()+"");
		}
		if (planTicketStatus != null && planTicketStatus.getValue() != PlanTicketStatus.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PLANTICKETSTATUS, planTicketStatus.getValue() + "");
		}
		if (planCreateType != null && planCreateType.getValue() != PlanCreateType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PLAN_CREATE_TYPE, planCreateType.getValue() + "");
		}
		if (sourceId != null) {
			request.setParameter(Plan.QUERY_SOURCE_ID, sourceId + "");
		}

        if (rbeginDate != null || rendDate != null) {
            request.setParameterBetween(Plan.QUERY_CREATED_TIME, CoreDateUtils.formatDateTime(rbeginDate), CoreDateUtils.formatDateTime(rendDate));
        }

        if (StringUtils.isBlank(planId)) {
            request.setParameterIdRange(Plan.QUERY_ID, rbeginDate, rendDate);
        } else {
            request.setParameter(Plan.QUERY_ID, planId);
        }

		if (lbeginDate != null || lendDate != null) {
			request.setParameterBetween(Plan.QUERY_DEAD_LINE, CoreDateUtils.formatDateTime(lbeginDate), CoreDateUtils.formatDateTime(lendDate));
		}
		if (pbeginDate != null || pendDate != null) {
			request.setParameterBetween(Plan.QUERY_PRINT_TIME, CoreDateUtils.formatDateTime(pbeginDate), CoreDateUtils.formatDateTime(pendDate));
		}
		if (lastMatchTimeFrom != null || lastMatchTimeTo != null) {
			request.setParameterBetween(Plan.QUERY_LAST_MATCH_TIME, CoreDateUtils.formatDateTime(lastMatchTimeFrom), CoreDateUtils.formatDateTime(lastMatchTimeTo));
		}
		if (orderStr != null && !"".equals(orderStr) && orderView != null && !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);
		}
		if (pageBean != null) {		
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API获取方案数据失败");
			throw new ApiRemoteCallFailedException("API获取方案数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取方案数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案数据为空, message={}", response.getMessage());
			return null;
		}
		List<Plan> list = Plan.convertFromJSONArray(response.getData());
		if (pageBean != null) {		
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}
	
	@Override
	public List<Plan> findByPhaseType(PhaseType phaseType, String phaseNo,
			List<PlanStatus> planStatusList, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据彩期类型查询方案列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_LIST_QUERY);
		
		if (phaseType != null) {
			request.setParameter(Plan.QUERY_PHASE_TYPE, phaseType.getValue()+"");
		}
		if (phaseNo != null && !"-1".equals(phaseNo)) {
			request.setParameter(Plan.QUERY_PHASE, phaseNo);
		}
		if (planStatusList != null && planStatusList.size() > 0) {
			List<String> str = new ArrayList<String>();
			for (PlanStatus p : planStatusList) {
				str.add(p.getValue() + "");
			}
			request.setParameterIn(Plan.QUERY_PLAN_STATUS, str);
		}
		
		request.addOrder(Plan.ORDER_ID, ApiConstant.API_REQUEST_ORDER_ASC);
		if (pageBean != null) {		
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API获取方案数据失败");
			throw new ApiRemoteCallFailedException("API获取方案数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取方案数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案数据为空, message={}", response.getMessage());
			return null;
		}
		List<Plan> list = Plan.convertFromJSONArray(response.getData());
		if (list != null && list.size() > 0) {		
			return list;
		}
		return null;
	}

	@Override
	public Plan get(String id) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询方案");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_DETAIL_QUERY);
		request.setParameter(Plan.QUERY_ID, id);
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取方案数据失败");
			throw new ApiRemoteCallFailedException("API获取方案数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取方案数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案数据为空, message={}", response.getMessage());
			return null;
		}
		List<Plan> plans = Plan.convertFromJSONArray(response.getData());
		if (plans != null && plans.size() > 0) {			
			return plans.get(0);
		}
		return null;
	}
	@Override
	public boolean updateTopStatus(Plan plan, int top) throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新置顶值");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_EXT_UPDATE);
		request.setParameter(Plan.QUERY_ID, plan.getId());
		
		request.setParameterForUpdate(Plan.SET_TOP, top + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新置顶值失败");
			throw new ApiRemoteCallFailedException("API更新置顶值失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新置顶值请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getLotteryPlans(Map<String, Object> condition,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API多条件查询方案列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_LIST_QUERY);
		
		String param = null;
		if (condition!=null) {
			//方案id
			if (condition.containsKey(Plan.QUERY_ID)) {
				param = (String)condition.get(Plan.QUERY_ID);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_ID, param);
				}
			}
			//彩期类型
			if (condition.containsKey(Plan.QUERY_LOTTERY_TYPE)) {
				param = (String)condition.get(Plan.QUERY_LOTTERY_TYPE);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_LOTTERY_TYPE, param);
				}
			}
			//期数
			if (condition.containsKey(Plan.QUERY_PHASE)) {
				param = (String)condition.get(Plan.QUERY_PHASE);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_PHASE, param);
				}
			}
			//方案类型
			if (condition.containsKey(Plan.QUERY_PLAN_TYPE)) {
				param = (String)condition.get(Plan.QUERY_PLAN_TYPE);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_PLAN_TYPE, param);
				}
			}
			//选号类型
			if (condition.containsKey(Plan.QUERY_SELECT_TYPE)) {
				param = (String)condition.get(Plan.QUERY_SELECT_TYPE);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_SELECT_TYPE, param);
				}
			}
			//玩法
			if (condition.containsKey(Plan.QUERY_PLAY_TYPE)) {
				param = (String)condition.get(Plan.QUERY_PLAY_TYPE);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_PLAY_TYPE, param);
				}
			}
			//方案状态
			if (condition.containsKey(Plan.QUERY_PLAN_STATUS)) {
				param = (String)condition.get(Plan.QUERY_PLAN_STATUS);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_PLAN_STATUS, param);
				}
			}
			//结果状态，是否已中奖等
			if (condition.containsKey(Plan.QUERY_RESULT_STATUS)) {
				param = (String)condition.get(Plan.QUERY_RESULT_STATUS);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_RESULT_STATUS, param);
				}
			}
			//北单扩展字段：方案中最后一个场次的索引顺序
			if (condition.containsKey(Plan.QUERY_DC_LAST_NUM)) {
				param = (String)condition.get(Plan.QUERY_DC_LAST_NUM);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Plan.QUERY_DC_LAST_NUM, param);
				}
			}
			//特殊 可派奖
			//方案状态  in方式
			if (condition.containsKey(LotteryPlanService.REWARD_PLAN_STATUS)) {
				List<String> inList = (List<String>)condition.get(LotteryPlanService.REWARD_PLAN_STATUS);
				if (inList!=null&&inList.size()>0) {
					request.setParameterIn(Plan.QUERY_PLAN_STATUS, inList);
				}
			}
			//方案  结果状态
			if (condition.containsKey(LotteryPlanService.REWARD_RESULT_STATUS)) {
				List<String> inList = (List<String>)condition.get(LotteryPlanService.REWARD_RESULT_STATUS);
				if (inList!=null&&inList.size()>0) {
					request.setParameterIn(Plan.QUERY_RESULT_STATUS, inList);
				}
			}
			//end特殊
			
			//排序  id升序
			request.addOrder(Plan.ORDER_ID, ApiConstant.API_REQUEST_ORDER_ASC);
		}
		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("查询方案,api request String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null ) {
			logger.info("API查询方案失败");
			return null;
		}
		logger.info("查询方案,api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<Plan> list = Plan.convertFromJSONArray(response.getData());
		ResultBean resultBean = new ResultBean();
		if (response.getCode()!=ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		
		if (pageBean!=null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		map.put(Global.API_MAP_KEY_RESULTBEAN, resultBean);
		return map;
	}

	@Override
	public Map<String, Object> find4RewardPlanByLotteryTypeAndPhaseNo(LotteryType lotteryType, String phaseNo, List<String> planStatusInList, List<String> resultStatusInList, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据彩票类型和期号分页查询要派奖的方案");
		if (lotteryType == null || phaseNo == null) {
			return null;
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_LIST_QUERY);
		
		//方案状态
		if (planStatusInList == null) {
			planStatusInList = new ArrayList<String>();
			planStatusInList.add(String.valueOf(PlanStatus.PRINTED.getValue()));//已出票
			//planStatusInList.add(String.valueOf(PlanStatus.ABORTED.getValue()));//未满员撤单
			planStatusInList.add(String.valueOf(PlanStatus.PRINTED_PARTIAL.getValue()));//部分出票
		}
		
		
		//结果状态
		if (resultStatusInList == null) {
			resultStatusInList = new ArrayList<String>();
			resultStatusInList.add(String.valueOf(ResultStatus.WON.getValue()));//已中奖
			resultStatusInList.add(String.valueOf(ResultStatus.REWARDED.getValue()));//已派奖
		}
		
		/* 弃用 调用本地方法,直接自己调用api 
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put(Plan.QUERY_LOTTERY_TYPE, String.valueOf(lotteryType.getValue()));
		condition.put(Plan.QUERY_PHASE, phaseNo);
		condition.put(LotteryPlanService.REWARD_PLAN_STATUS, planStatusInList);
		condition.put(LotteryPlanService.REWARD_RESULT_STATUS, planStatusInList);
		*/
		request.setParameter(Plan.QUERY_LOTTERY_TYPE,String.valueOf(lotteryType.getValue()));
		request.setParameter(Plan.QUERY_PHASE,phaseNo);
		request.setParameterIn(Plan.QUERY_PLAN_STATUS,planStatusInList);
		request.setParameterIn(Plan.QUERY_RESULT_STATUS,resultStatusInList);
		
		request.addOrder(Plan.ORDER_RESULT_STATUS, ApiConstant.API_REQUEST_ORDER_ASC);
		request.addOrder(Plan.ORDER_ID, ApiConstant.API_REQUEST_ORDER_DESC);
		
		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("按彩票类型和期号分页查询要派奖的方案,api request String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null ) {
			logger.info("API根据彩票类型和期号分页查询要派奖的方案失败");
			return null;
		}
		logger.info("按彩票类型和期号分页查询要派奖的方案,api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<Plan> list = Plan.convertFromJSONArray(response.getData());
		ResultBean resultBean = new ResultBean();
		if (response.getCode()!=ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		
		if (pageBean!=null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		map.put(Global.API_MAP_KEY_RESULTBEAN, resultBean);
		return map;
	}
	
	@Override
	public Map<String, Object> find4RewardPlan(LotteryType lotteryType, String phaseNo, String planId, PlanType planType, ResultStatus resultStatus,
			Date beginDate, Date endDate, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询要派奖的方案");
		if (lotteryType == null || phaseNo == null) {
			return null;
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_LIST_QUERY);
		
		if (planId != null && !"".equals(planId)) {
			request.setParameter(Plan.QUERY_ID, planId);
		}
		if (planType != null && planType.getValue() != PlanType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PLAN_TYPE, planType.getValue()+"");
		}
		

		request.setParameter(Plan.QUERY_LOTTERY_TYPE, String.valueOf(lotteryType.getValue()));
		
		request.setParameter(Plan.QUERY_PHASE, phaseNo);
		
		//方案状态
		List<String> planStatusInList = new ArrayList<String>();
		planStatusInList.add(String.valueOf(PlanStatus.PRINTED.getValue()));//已出票
		//planStatusInList.add(String.valueOf(PlanStatus.ABORTED.getValue()));//未满员撤单
		planStatusInList.add(String.valueOf(PlanStatus.PRINTED_PARTIAL.getValue()));//部分出票
		
		request.setParameterIn(Plan.QUERY_PLAN_STATUS,planStatusInList);
		
		//结果状态
		if (resultStatus != null) {
			request.setParameter(Plan.QUERY_RESULT_STATUS, String.valueOf(resultStatus.getValue()));
		}
		
		if (beginDate != null) {
			request.setParameterBetween(Plan.QUERY_CREATED_TIME, DateUtil.formatDate(beginDate,DateUtil.DATETIME),null);
		}
		if (endDate != null) {
			request.setParameterBetween(Plan.QUERY_CREATED_TIME, null,DateUtil.formatDate(endDate,DateUtil.DATETIME));
		}
		
		//request.addOrder(Plan.ORDER_RESULT_STATUS, ApiConstant.API_REQUEST_ORDER_ASC);
		//request.addOrder(Plan.ORDER_ID, ApiConstant.API_REQUEST_ORDER_DESC);
		// 覆盖掉默认的排序条件
		request.addOrder(Plan.ORDER_LOTTERY_TYPE, ApiConstant.API_REQUEST_ORDER_ASC);
		request.addOrder(Plan.ORDER_PHASE, ApiConstant.API_REQUEST_ORDER_ASC);
		
		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("按彩票类型和期号分页查询要派奖的方案,api request String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null ) {
			logger.info("API查询要派奖的方案失败");
			return null;
		}
		logger.info("按彩票类型和期号分页查询要派奖的方案,api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<Plan> list = Plan.convertFromJSONArray(response.getData());
		ResultBean resultBean = new ResultBean();
		if (response.getCode()!=ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		
		if (pageBean!=null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		map.put(Global.API_MAP_KEY_RESULTBEAN, resultBean);
		return map;
	}

	@Override
	public Map<String, Object> lotteryPlanStatistics(String userName,
			String planId, LotteryType lotteryType, PhaseType phaseType, String phase,
			PlanType planTypeId, SelectType selectTypeId, PlayType playTypeId,
			List<String> planStatus, YesNoStatus uploadStatus, PublicStatus publicStatus,
			ResultStatus resultStatus, YesNoStatus allowAutoFollow, PlanTicketStatus planTicketStatus, Long sourceId, Date rbeginDate,
			Date rendDate, Date lbeginDate, Date lendDate, Date pbeginDate, Date pendDate, Date lastMatchTimeFrom, Date lastMatchTimeTo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询方案统计数据");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_STATISTICS);
		
		if (userName != null && !"".equals(userName)) {
			Member member = null;
			try {
				member = memberService.get(userName);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage());
			}
			if (member != null) {				
				request.setParameter(Plan.QUERY_UID, member.getUid() + "");
			} else {
				request.setParameter(Plan.QUERY_UID, "");
			}
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_LOTTERY_TYPE, lotteryType.getValue() + "");
		}
		if (phaseType != null && phaseType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PHASE_TYPE, phaseType.getValue() + "");
		}
		if (phase != null && !"-1".equals(phase)) {
			request.setParameter(Plan.QUERY_PHASE, phase);
		}
		if (planTypeId != null && planTypeId.getValue() != PlanType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PLAN_TYPE, planTypeId.getValue() + "");
		}
		if (selectTypeId != null && selectTypeId.getValue() != SelectType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_SELECT_TYPE, selectTypeId.getValue() + "");
		}
		if (playTypeId != null && playTypeId.getValue() != PlayType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PLAY_TYPE, playTypeId.getValue() + "");
		}
		if (planStatus != null && !planStatus.isEmpty()) {
			request.setParameterIn(Plan.QUERY_PLAN_STATUS, planStatus);
		}
		if (uploadStatus != null && uploadStatus.getValue() != YesNoStatus.ALL.getValue()) {
			if (uploadStatus.getValue() == YesNoStatus.YES.getValue()) {
				request.setParameterGreater(Plan.QUERY_UPLOAD_ID, "0");
			} else {
				request.setParameter(Plan.QUERY_UPLOAD_ID, "0");
			}
		}
		if (publicStatus != null && publicStatus.getValue() != PublicStatus.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PUBLIC_STATUS, publicStatus.getValue() + "");
		}
		if (resultStatus != null && resultStatus.getValue() != ResultStatus.ALL.getValue()) {
			request.setParameter(Plan.QUERY_RESULT_STATUS, resultStatus.getValue() + "");
		}
		if (allowAutoFollow != null && allowAutoFollow.getValue() != YesNoStatus.ALL.getValue()) {
			request.setParameter(Plan.QUERY_ALLOW_AUTO_FOLLOW, allowAutoFollow.getValue() + "");
		}
		if (planTicketStatus != null && planTicketStatus.getValue() != PlanTicketStatus.ALL.getValue()) {
			request.setParameter(Plan.QUERY_PLANTICKETSTATUS, planTicketStatus.getValue() + "");
		}
		if (sourceId != null) {
			request.setParameter(Plan.QUERY_SOURCE_ID, sourceId + "");
		}

        if (rbeginDate != null || rendDate != null) {
            request.setParameterBetween(Plan.QUERY_CREATED_TIME, CoreDateUtils.formatDateTime(rbeginDate), CoreDateUtils.formatDateTime(rendDate));
        }

        if (StringUtils.isBlank(planId)) {
            request.setParameterIdRange(Plan.QUERY_ID, rbeginDate, rendDate);
        } else {
            request.setParameter(Plan.QUERY_ID, planId);
        }

        if (lbeginDate != null || lendDate != null) {
            request.setParameterBetween(Plan.QUERY_DEAD_LINE, CoreDateUtils.formatDateTime(lbeginDate), CoreDateUtils.formatDateTime(lendDate));
        }
        if (pbeginDate != null || pendDate != null) {
            request.setParameterBetween(Plan.QUERY_PRINT_TIME, CoreDateUtils.formatDateTime(pbeginDate), CoreDateUtils.formatDateTime(pendDate));
        }
        if (lastMatchTimeFrom != null || lastMatchTimeTo != null) {
            request.setParameterBetween(Plan.QUERY_LAST_MATCH_TIME, CoreDateUtils.formatDateTime(lastMatchTimeFrom), CoreDateUtils.formatDateTime(lastMatchTimeTo));
        }
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API获取方案统计数据失败");
			throw new ApiRemoteCallFailedException("API获取方案统计数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案统计数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取方案统计数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案统计数据为空, message={}", response.getMessage());
			return null;
		}
		Map<String, Object> map = new HashMap<String,Object>();
		JSONObject jsonObj = response.getData().getJSONObject(0);
		
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("amount") != null) {
			map.put(Global.API_MAP_KEY_AMOUNT, jsonObj.get("amount"));
		} else {
			map.put(Global.API_MAP_KEY_AMOUNT, "0");
		}
	
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("prize_posttax") != null) {
			map.put(Global.API_MAP_KEY_POSTTAXPRIZE, jsonObj.get("prize_posttax"));
		} else {
			map.put(Global.API_MAP_KEY_POSTTAXPRIZE, "0");
		}	
		
		return map;
	}

	@Override
	public boolean updateStatus(Plan plan, PlanStatus status) {
		logger.info("进入调用API更新方案状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_UPDATE);
		request.setParameter(Plan.QUERY_ID, plan.getId());
		
		request.setParameterForUpdate(Plan.SET_PLAN_STATUS, String.valueOf(status.getValue()));
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API更新方案状态请求异常!{}", e.getMessage());
			return false;
		}
		if (response == null) {
			logger.error("API更新方案状态失败");
			return false;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新方案状态失败, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updateStatusAndTicketStatus(Plan plan, PlanStatus status, PlanTicketStatus ticketStatus) {
		logger.info("进入调用API更新方案状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_UPDATE);
		request.setParameter(Plan.QUERY_ID, plan.getId());
		
		request.setParameterForUpdate(Plan.SET_PLAN_STATUS, String.valueOf(status.getValue()));
		request.setParameterForUpdate(Plan.SET_PLANTICKETSTATUS, ticketStatus.getValue() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API更新方案状态请求异常!{}", e.getMessage());
			return false;
		}
		if (response == null) {
			logger.error("API更新方案状态失败");
			return false;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新方案状态失败, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updateTicketStatus(Plan plan, PlanTicketStatus status) {
		logger.info("进入调用API更新方案票状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_UPDATE);
		request.setParameter(Plan.QUERY_ID, plan.getId());
		
		request.setParameterForUpdate(Plan.SET_PLANTICKETSTATUS, status.getValue() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API更新方案票状态请求异常!{}", e.getMessage());
			return false;
		}
		if (response == null) {
			logger.error("API更新方案状态票失败");
			return false;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新方案状态票失败, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 更新方案出票截止时间和销售截止时间
	 * @param planId 方案号
	 * @param deadline 截止时间
     * @param saleDeadline 销售截止时间
	 * @return
	 */
	public boolean updateDeadline(String planId, Date deadline, Date saleDeadline) throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新方案截止时间和销售截止时间");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_UPDATE);
		
		request.setParameter(Plan.QUERY_ID, planId);
		request.setParameterForUpdate(Plan.SET_DEADLINE, CoreDateUtils.formatDate(deadline, CoreDateUtils.DATETIME));
		request.setParameterForUpdate(Plan.SET_SALE_DEADLINE, CoreDateUtils.formatDate(saleDeadline, CoreDateUtils.DATETIME));
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

		if (response == null) {
			logger.error("API更新更新出票截止时间和销售截止时间失败");
			throw new ApiRemoteCallFailedException("API更新更新出票截止时间和销售截止时间失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新出票截止时间和销售截止时间请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API更新出票截止时间和销售截止时间请求异常");
		}
		int total = response.getTotal();
		if (total == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getOverduePlans(PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询要过期未开奖方案");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_LIST_OVERDUE);
		
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API获取过期未开奖方案数据失败");
			throw new ApiRemoteCallFailedException("API获取过期未开奖方案数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API获取过期未开奖方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取过期未开奖方案数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取过期未开奖方案数据为空, message={}", response.getMessage());
			return null;
		}
		//分页计算
		if (pageBean != null) {		
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
				pageCount = totalCount / pageBean.getPageSize();
				if (totalCount % pageBean.getPageSize() != 0) {
					pageCount ++;
				}
			}
			pageBean.setPageCount(pageCount);
		}
		logger.info("#########################PlanId：{} #########################", response.getData());
		
		//根据planId查询方案
		JSONArray planIdArray = response.getData();
		List<String> planIdList = new ArrayList<String>();
		for (Iterator<Long> iterator = planIdArray.iterator(); iterator.hasNext();) {
			planIdList.add(String.valueOf(iterator.next()));
		}
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_LIST_QUERY);
		request.setPage(1);//重置到第一页
		request.setParameterIn(Plan.QUERY_ID, planIdList);
		logger.info("Request Query String: {}", request.toQueryString());
		response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API获取方案数据失败");
			throw new ApiRemoteCallFailedException("API获取方案数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API获取方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取方案数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案数据为空, message={}", response.getMessage());
			return null;
		}
		List<Plan> list = Plan.convertFromJSONArray(response.getData());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}

	@Override
	public String parseContent(String planId)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API解析方案内容");
		String parseContent = "";
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_PARSE_CONTENT);
		if (planId != null && !planId.isEmpty()) {
			request.setParameter(Plan.QUERY_ID, planId);
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API解析方案内容失败");
			throw new ApiRemoteCallFailedException("API解析方案内容失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API解析方案内容请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API解析方案内容请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API解析方案内容为空, message={}", response.getMessage());
			return parseContent;
		}
		
		JSONArray data = response.getData();
		
		if (data != null) {
			parseContent = (String) data.get(0);
		}
		
		return parseContent;
	}

	@Override
	public void resetMatchByPlanId(List<String> planIdList, List<String> changedList, List<String> noChangedList,
			List<String> failureList) throws ApiRemoteCallFailedException, Exception {
		logger.info("进入调用API重算合买截止");
		if (planIdList == null || planIdList.size() == 0) {
			logger.error("[重算合买截止]planIdList为空");
			throw new Exception("[重算合买截止]planIdList为空");
		}
		
		if (changedList == null) {
			logger.error("[重算合买截止]changedList为空");
			throw new Exception("[重算合买截止]changedList为空");
		}
		
		if (noChangedList == null) {
			logger.error("[重算合买截止]noChangedList为空");
			throw new Exception("[重算合买截止]noChangedList为空");
		}

		if (failureList == null) {
			logger.error("[重算合买截止]failureList为空");
			throw new Exception("[重算合买截止]failureList为空");
		}
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_RESET_MATCH);
		
		request.setParameterIn(Plan.QUERY_ID, planIdList);
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API重算合买截止失败");
			throw new ApiRemoteCallFailedException("API重算合买截止失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API重算合买截止请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API重算合买截止请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API重算合买截止返回结果为空, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API重算合买截止返回结果为空");
		}
		
		JSONObject object = (JSONObject) response.getData().get(0);
		
		List<String> successList = new ArrayList<String>();
		JSONArray arraySuccess = object.getJSONArray("success");
		if (arraySuccess != null && arraySuccess.size() > 0) {
			for (Iterator<?> iterSuccess = arraySuccess.iterator(); iterSuccess.hasNext();) {
				String successId = (String) iterSuccess.next();
				successList.add(successId);
				changedList.add(successId);
			}
		}
		JSONArray arrayNoChanged = object.getJSONArray("no_changed");
		if (arrayNoChanged != null && arrayNoChanged.size() > 0) {
			for (Iterator<?> iterNoChanged = arrayNoChanged.iterator(); iterNoChanged.hasNext();) {
				String noChangedId = (String) iterNoChanged.next();
				noChangedList.add(noChangedId);
			}
		}
		changedList.removeAll(noChangedList);
		JSONArray arrayFailed = object.getJSONArray("fail");
		if (arrayFailed != null && arrayFailed.size() > 0) {
			for (Iterator<?> iterFailed = arrayFailed.iterator(); iterFailed.hasNext();) {
				String failedId = (String) iterFailed.next();
				failureList.add(failedId);
			}				
		}
	}

	@Override
	public boolean updateRebate(String planId, double rebate)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新方案");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_UPDATE);
		request.setParameter(Plan.QUERY_ID, planId);
		
		request.setParameterForUpdate(Plan.SET_REBATE, rebate + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新方案失败");
			throw new ApiRemoteCallFailedException("API更新方案失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新方案请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 条件查询合买方案
	 */
	public Map<String, Object> findHMPlan(String username, String planId, LotteryType lotteryType, 
			String phase, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询合买方案");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_LIST_QUERY);
		request.setParameter(Plan.QUERY_PLAN_TYPE, PlanType.HM.getValue() + "");	//设置方案类型
		request.setParameter(Plan.QUERY_PLAN_STATUS, PlanStatus.RECRUITING.getValue() + "");	//设置方案状态
		
		if (planId != null && !planId.equals("")) {
			request.setParameter(Plan.QUERY_ID, planId);//方案编号
		}
		if (username != null && !username.equals("")) {
			Member member = null;
			try {
				member = memberService.get(username);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API根据用户名查询用户ID异常!{}", e.getMessage());
			}
			if (member != null) {				
				request.setParameter(Plan.QUERY_UID, member.getUid() + "");	//发起人
			} else {
				request.setParameter(Plan.QUERY_UID, "");					//发起人
			}
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_LOTTERY_TYPE, lotteryType.getValue() + "");//彩种
		}
		if (phase != null && !phase.equals("") && !phase.equals("-1")) {
			request.setParameter(Plan.QUERY_PHASE, phase);//彩期
		}
		request.addOrder(Plan.ORDER_ID, ApiConstant.API_REQUEST_ORDER_DESC);//排序  id降序
		
		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		
		logger.info("查询合买方案,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null ) {
			logger.error("调用API查询合买方案失败");
			throw new ApiRemoteCallFailedException("调用API查询合买方案失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API查询合买方案请求异常!rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API查询合买方案请求异常");
		}
		if (response.getData() == null) {
			logger.warn("调用API查询合买方案为空");
			return null;
		}
		
		List<Plan> list = Plan.convertFromJSONArray(response.getData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}
	
	
	/**
	 * 修改合买方案标题和内容
	 * @param plan 方案对象
	 */
	public boolean updatePlan(Plan plan) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改合买方案标题和内容");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_EXT_UPDATE);
		request.setParameter(Plan.QUERY_ID, plan.getId());
		
		request.setParameterForUpdate(Plan.SET_TITLE, plan.getTitle());
		request.setParameterForUpdate(Plan.SET_DESCRIPTION, plan.getDescription());
		
		logger.info("修改合买方案,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改合买方案失败");
			throw new ApiRemoteCallFailedException("API修改合买方案失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API修改合买方案请求异常!{}!rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 单方案查询
	 * @param planNo 方案编号
	 */
	
	@Override
	public Plan getPlanById(String planNo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询单方案");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_DETAIL_QUERY);
		request.setParameter(Plan.QUERY_ID, planNo);
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("单方案查询结果为空");
			throw new ApiRemoteCallFailedException("单方案查询结果为空");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API单方案查询请求异常!{}!rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API单方案查询请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案数据为空, message={}", response.getMessage());
			return null;
		}
		List<Plan> plans = Plan.convertFromJSONArray(response.getData());
		
		if (plans != null && plans.size() > 0) {
			return plans.get(0);
		}
		return null;
	}
	
	@Override
	public boolean batchUpdateStatus(List<Plan> plans, List<String> successList, List<String> failureList) throws ApiRemoteCallFailedException {
		logger.info("进入调用API批量更新方案状态");
		
		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_BATCH_UPDATE);

		if(plans != null){
			for(Plan plan : plans){
				SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
				simpleApiBatchUpdateItem.setKey(plan.getId());
				simpleApiBatchUpdateItem.setParameterForUpdate(Plan.SET_PLAN_STATUS, String.valueOf(plan.getPlanStatus().getValue()));
				request.add(simpleApiBatchUpdateItem);
			}
		}
		
		logger.info("批量更新方案状态 Request Query String: {}", request.toQueryString());

		ApiResponse response = null;
		try{
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		}catch(ApiRemoteCallFailedException e){
			logger.error("批量更新方案状态 api远程请求异常,"+e.getMessage());
			throw new ApiRemoteCallFailedException("API批量更新方案状态失败");
		}
		if (response == null) {
			logger.error("API批量更新方案状态失败");
			throw new ApiRemoteCallFailedException("API批量更新方案状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API批量更新方案状态请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API批量更新方案状态请求出错");
		}
		
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API批量更新方案状态为空, message={}", response.getMessage());
		}
		JSONObject object = (JSONObject) response.getData().get(0);
		JSONArray arraySuccess = object.getJSONArray("success");
		if(arraySuccess != null && arraySuccess.size() > 0){
			for (Iterator<?> iterSuccess = arraySuccess.iterator(); iterSuccess.hasNext();) {
				String successId = String.valueOf(iterSuccess.next());
				successList.add(successId);
			}
		}
		JSONArray arrayFailed = object.getJSONArray("fail");
		if(arrayFailed != null && arrayFailed.size() > 0){
			for (Iterator<?> iterFailed = arrayFailed.iterator(); iterFailed.hasNext();) {
				String failedId = String.valueOf(iterFailed.next());
				failureList.add(failedId);
			}				
		}
		return true;
	}

    @Override
    public void returnPlanTicket(List<Plan> planList, String checkMatchNum, List<String> successList, List<String> failureList, List<String> nochangeList) throws ApiRemoteCallFailedException {
        if (successList == null) {
            successList = new ArrayList<String>();
        }
        if (failureList == null) {
            failureList = new ArrayList<String>();
        }
        if (nochangeList == null) {
            nochangeList = new ArrayList<String>();
        }

        List<Plan> toUpdatePlanList = new ArrayList<Plan>();

        for (Plan plan : planList) {
            PlanStatus toPlanStatus = this.checkReturnTicketPlanStatus(plan, checkMatchNum);
            if (toPlanStatus == null) {
                nochangeList.add(plan.getId());
                continue;
            }

            Plan toUpdatePlan = new Plan();
            toUpdatePlan.setId(plan.getId());
            toUpdatePlan.setPlanStatus(toPlanStatus);
            toUpdatePlanList.add(toUpdatePlan);
        }

        this.batchUpdateStatus(toUpdatePlanList, successList, failureList);
    }

    /**
     * 检查方案是否允许退票，如果允许则并返回退票后的方案状态
     * @param plan 方案
     * @param checkMatchNum 要检查的场次编号
     * @return 退票后的方案状态
     */
    protected PlanStatus checkReturnTicketPlanStatus(Plan plan, String checkMatchNum) {

        boolean containsMatchNum = true;

        // 检测比赛场次
        if (StringUtils.isNotBlank(checkMatchNum)) {
            containsMatchNum = false;

            JSONArray jsonArray = null;
            try {
                jsonArray = JSONArray.fromObject(plan.getMatchNums());
            } catch (Exception e) {
                logger.error("[退票]matchNums转换成jsonArray错误, matchNums:{}", plan.getMatchNums());
                logger.error(e.getMessage(), e);
            }
            if (jsonArray == null || jsonArray.isEmpty()) {
                logger.error("没有有效的场次信息，不做处理");
                return null;
            }

            for (Iterator<?> iterator = jsonArray.iterator(); iterator.hasNext();) {
                String planMatchNum = String.valueOf(iterator.next());
                if (planMatchNum.equals(checkMatchNum)) {
                    containsMatchNum = true;
                    break;
                }
            }
        }

        if (!containsMatchNum) {
            return null;
        }

        boolean ticketPrinted = false;
        if (plan.getPlanTicketStatus().getValue() == PlanTicketStatus.SPLIT_COMPLETED.getValue()) {
            // 已经拆过票，需要检查是否已全部出票
            ticketPrinted = true;

            int page = 1;

            PageBean pageBean = new PageBean();
            pageBean.setPageSize(1000);

            while (true) {
                pageBean.setPage(page);

                List<Ticket> ticketList = ticketService.getListByPlanId(plan.getId(), pageBean);

                if (ticketList == null || ticketList.isEmpty()) {
                    break;
                }

                for (Ticket ticket : ticketList) {
                    if (ticket.getStatus().getValue() != TicketStatus.PRINT_SUCCESS.getValue()) {
                        ticketPrinted = false;
                        break;
                    }
                }

                if (!ticketPrinted) {
                    break;
                }

                if (ticketList.size() < pageBean.getPageSize()) {
                    break;
                }

                page ++;
            }
        }

        if (ticketPrinted) {
            // 方案下所有的票均已出票，为避免损失，在代码层禁止退票
            logger.error("方案({})下所有的票均已出票，为避免损失，禁止退票", plan.getId());
            return null;
        }

        return PlanStatus.getRecycleProcessingStatus(plan);
    }

    public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}
	
	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}
	
	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}
	
	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
	
	public MemberService getMemberService() {
		return memberService;
	}
	
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

    public TicketService getTicketService() {
        return ticketService;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }
}