package web.service.impl.lottery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.business.PhaseDeleteAction;
import com.lehecai.admin.web.action.include.statics.lottery.PhaseDrawResultRangeStaticAction;
import com.lehecai.admin.web.action.lottery.PhaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.BIService;
import com.lehecai.admin.web.service.config.EngineAddressConfigService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConfig;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.DirectApiRequest;
import com.lehecai.core.api.EngineApiUrlConstant;
import com.lehecai.core.api.lottery.Fb46Match;
import com.lehecai.core.api.lottery.LotteryConfig;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.SfpArrange;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.UnmatchedLotteryDrawResultException;
import com.lehecai.core.exception.UnsupportedFetcherTypeException;
import com.lehecai.core.exception.UnsupportedLotteryConfigException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseStatus;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.TerminalStatus;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.ext.ILotteryExtendFetcher;
import com.lehecai.core.lottery.fetcher.ext.LotteryExtendItem;
import com.lehecai.core.lottery.fetcher.ext.impl.FC3DExtendSJHFetcher;
import com.lehecai.core.lottery.fetcher.lotterydraw.ILotteryDrawFetcher;
import com.lehecai.core.lottery.fetcher.lotterydraw.LotteryDraw;
import com.lehecai.core.lottery.fetcher.lotterydraw.LotteryDrawPrizeItem;
import com.lehecai.core.service.lottery.LotteryCommonService;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.lottery.FetcherLotteryDrawConverter;

public class PhaseServiceImpl implements PhaseService{
	private final Logger logger = LoggerFactory.getLogger(PhaseServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	private BIService bIService;
	
	private LotteryCommonService lotteryCommonService;
	
	private EngineAddressConfigService engineAddressConfigService;
	
	/**
	 * 彩种和抓取器的绑定
	 */
	private Map<Integer, ILotteryDrawFetcher> fetcherBinder;
	
	private final String CURRENT_PHASE_POSITION = "currentPhasePosition";//定位当前期,同时获取
	private final String CURRENT_PHASE_END_SALE_TIME = "currentPhaseEndSaleTime";//定位当前期销售截止时间
	private final String CURRENT_PHASE_PHASENO = "currentPhasePhaseNo";//定位当前期,期号
	private final String ORDER_STR = "orderStr";//排序字段
	private final String ORDER_VIEW = "orderView";//排序方式

	@Override
	public Phase getPhaseByPhaseTypeAndPhaseNo(PhaseType phaseType,String phaseNo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取彩期");
		ApiRequest request = new ApiRequest();
		Phase phase = null;
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		request.setParameter(Phase.QUERY_PHASE, phaseNo);
		logger.info("获取指定彩期,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API获取彩期异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null) {
			logger.error("API获取彩期失败");
			throw new ApiRemoteCallFailedException("API获取彩期失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API获取彩期请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取彩期请求异常");
		}
		if (response.getData() == null) {
			logger.warn("API获取彩期为空");
			return null;
		}
		logger.info("获取指定彩期,api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<Phase> list = Phase.convertFromJSONArray(response.getData());
		if (list!=null&&list.size()>0) {
			phase = list.get(0);
		}
		return phase;
	}
	
	@Override
	public ResultBean batchCreatePhase(PhaseType phaseType, Integer count, Date batchStartTime) throws ApiRemoteCallFailedException {
		logger.info("进入调用API批量生成彩期");
		ApiRequest createRequest = new ApiRequest();
		ResultBean resultBean = new ResultBean();
		createRequest.setUrl(ApiConstant.API_URL_PHASE_CREATE);
		createRequest.setParameterForUpdate(Phase.SET_PHASETYPE, String.valueOf(phaseType.getValue()));//指定初始化彩票类型
		createRequest.setParameterForUpdate(Phase.SET_CREATE_NUM, String.valueOf(count));//创建count期
		if (batchStartTime != null) {
			createRequest.setParameterForUpdate(Phase.SET_CREATE_START_TIME, DateUtil.formatDate(batchStartTime,DateUtil.DATETIME));//批量创建的起始时间
		}
		logger.info("批量生成彩期,api request String: {}", createRequest.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API批量生成彩期异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("批量生成彩期,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage()+",影响数据条数:"+response.getTotal());
		return resultBean;
	}

	@Override
	public ResultBean createAssignPhase(PhaseType phaseType, String phaseNo,Date startSaleTime,Date endSaleTime,Date endTicketTime,Date drawTime) throws ApiRemoteCallFailedException {
		logger.info("进入调用API创建彩期");
		ApiRequest initRequest = new ApiRequest();
		ResultBean resultBean = new ResultBean();
		initRequest.setUrl(ApiConstant.API_URL_PHASE_INIT_UPDATE);
		initRequest.setParameterForUpdate(Phase.SET_PHASETYPE, String.valueOf(phaseType.getValue()));
		initRequest.setParameterForUpdate(Phase.SET_PHASE, phaseNo);//
		
		//开始销售时间
		if (startSaleTime!=null) {
			initRequest.setParameterForUpdate(Phase.SET_TIME_STARTSALE, DateUtil.formatDate(startSaleTime,DateUtil.DATETIME));
		}
		//结束销售时间
		if (endSaleTime!=null) {
			initRequest.setParameterForUpdate(Phase.SET_TIME_ENDSALE, DateUtil.formatDate(endSaleTime,DateUtil.DATETIME));
		}
		//停止出票时间
		if (endTicketTime!=null) {
			initRequest.setParameterForUpdate(Phase.SET_TIME_ENDTICKET, DateUtil.formatDate(endTicketTime,DateUtil.DATETIME));
		}
		//开奖时间
		if (drawTime!=null) {
			initRequest.setParameterForUpdate(Phase.SET_TIME_DRAW, DateUtil.formatDate(drawTime,DateUtil.DATETIME));
		}
		
		logger.info("创建指定彩期,api request String: {}", initRequest.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(initRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API创建彩期异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("创建指定彩期,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
			resultBean.setCode(response.getCode());
			resultBean.setMessage(response.getMessage()+",影响数据条数:"+response.getTotal());
			return resultBean;
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage()+",影响数据条数:"+response.getTotal());
		/* delete by lm php端已接受四个时间参数,无需再使用获取并更新的方式
		//获取刚才创建的彩期
		Phase phase = getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
		
		if (startSaleTime!=null) {
			phase.setStartSaleTime(startSaleTime);
		}
		if (endSaleTime!=null) {
			phase.setEndSaleTime(endSaleTime);	
		}
		if (endTicketTime!=null) {
			phase.setEndTicketTime(endTicketTime);
		}
		if (drawTime!=null) {
			phase.setDrawTime(drawTime);
		}
		//更新时间
		ResultBean updateResultBean = updatePhase(phase);
		if (!updateResultBean.isResult()) {
			resultBean.setMessage(resultBean.getMessage()+updateResultBean.getMessage());
		}*/
		return resultBean;
	}
	
	@Override
	public ResultBean createAssignPhase(PhaseType phaseType,String phaseNo) throws ApiRemoteCallFailedException {
		return createAssignPhase(phaseType, phaseNo, null, null, null, null);
	}
	
	/**
	 * 根据条件获得彩期列表
	 * @param condition
	 * @param pageBean
	 * @return Map<String, Object> key:list | pageBean | resultBean  value:List<Phase> | PageBean | ResultBean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getPhases(Map<String, Object> condition, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询彩期");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		
		String param = null;
		if (condition != null) {
			boolean matchPhaseFlag = false;
			
			if (condition.containsKey(Phase.QUERY_PHASETYPE)) {
				param = (String)condition.get(Phase.QUERY_PHASETYPE);
				if (param != null && !param.equals("")) {
					request.setParameter(Phase.QUERY_PHASETYPE, param);//彩期类型
				}
			}
			if (condition.containsKey(PhaseAction.QUERY_MATCH_PHASE_NO)) {
				param = (String)condition.get(PhaseAction.QUERY_MATCH_PHASE_NO);
				if (param != null && !param.equals("")) {
					matchPhaseFlag = true;
					request.setParameter(Phase.QUERY_PHASE, param);//期数
				}
			}
			if (!matchPhaseFlag && condition.containsKey(Phase.QUERY_PHASE)) {
				param = (String)condition.get(Phase.QUERY_PHASE);
				if (param != null && !param.equals("")) {
					request.setParameter(Phase.QUERY_PHASE, param);//期数
				}
			}
			if (condition.containsKey(Phase.QUERY_STATUS)) {
				List<String> list = (List<String>)condition.get(Phase.QUERY_STATUS);
				request.setParameterIn(Phase.QUERY_STATUS, list);//彩期状态
			}
 			if (condition.containsKey(Phase.QUERY_FORSALE)) {
				param = (String)condition.get(Phase.QUERY_FORSALE);
				request.setParameter(Phase.QUERY_FORSALE, param);//是否销售
			}
			if (condition.containsKey(Phase.QUERY_IS_CURRENT)) {
				param = (String)condition.get(Phase.QUERY_IS_CURRENT);
				request.setParameter(Phase.QUERY_IS_CURRENT, param);//是否当前期
			}
			String create_at_start = null;
			String create_at_end = null;
			//创建起始时间
			if (condition.containsKey(PhaseAction.QUERY_CREATETIME_START)) {
				Date startDate = (Date)condition.get(PhaseAction.QUERY_CREATETIME_START);
				create_at_start = DateUtil.formatDate(startDate,DateUtil.DATETIME);
			}
			//创建结束时间
			if (condition.containsKey(PhaseAction.QUERY_CREATETIME_END)) {
				Date endDate = (Date)condition.get(PhaseAction.QUERY_CREATETIME_END);
				create_at_end = DateUtil.formatDate(endDate,DateUtil.DATETIME);
			}
			if (create_at_start != null || create_at_end != null) {
				request.setParameterBetween(Phase.QUERY_CREATETIME, create_at_start, create_at_end);
			}
			String beginSaleTime = null;
			String endSaleTime = null;
			if (condition.containsKey(PhaseDeleteAction.QUERY_BEGINSALETIME)) {
				Date beginSaleDate = (Date)condition.get(PhaseDeleteAction.QUERY_BEGINSALETIME);
				beginSaleTime = DateUtil.formatDate(beginSaleDate, DateUtil.DATETIME);
			}
			if (condition.containsKey(PhaseDeleteAction.QUERY_ENDSALETIME)) {
				Date endSaleDate = (Date)condition.get(PhaseDeleteAction.QUERY_ENDSALETIME);
				endSaleTime = DateUtil.formatDate(endSaleDate, DateUtil.DATETIME);	
			}
			if (beginSaleTime != null || endSaleTime != null) {
				request.setParameterBetween(Phase.QUERY_TIME_STARTSALE, beginSaleTime, endSaleTime);
			}
			
			String draw_start = null;
			String draw_end = null;
			//开奖起始时间
			if (condition.containsKey(PhaseDrawResultRangeStaticAction.QUERY_DRAWTIME_START)) {
				Date startDate = (Date)condition.get(PhaseDrawResultRangeStaticAction.QUERY_DRAWTIME_START);
				draw_start = DateUtil.formatDate(startDate,DateUtil.DATETIME);
			}
			//开奖结束时间
			if (condition.containsKey(PhaseDrawResultRangeStaticAction.QUERY_DRAWTIME_END)) {
				Date endDate = (Date)condition.get(PhaseDrawResultRangeStaticAction.QUERY_DRAWTIME_END);
				draw_end = DateUtil.formatDate(endDate,DateUtil.DATETIME);
			}
			if (draw_start != null || draw_end != null) {
				request.setParameterBetween(Phase.QUERY_TIME_DRAW, draw_start, draw_end);
			}
			
			//查询当前期所在页  该部分与getPageOfCurrentPhase()方法相关
			if (condition.containsKey(CURRENT_PHASE_POSITION)) {
				if (condition.containsKey(CURRENT_PHASE_PHASENO)) {
					param = (String)condition.get(CURRENT_PHASE_PHASENO);
					//当前期存在,获取大于当前起的数量,定位
					request.setParameterGreater(Phase.QUERY_PHASE, param);
				}
				if (condition.containsKey(CURRENT_PHASE_END_SALE_TIME)) {
					param = (String)condition.get(CURRENT_PHASE_END_SALE_TIME);
					//当前期不存在,获取结束销售时间最接近当前时间的彩期,结束销售时间>= 当前时间
					request.setParameterBetween(Phase.QUERY_TIME_ENDSALE, param, null);
				}
			}
			
			if (condition.containsKey(ORDER_STR) && condition.containsKey(ORDER_VIEW)) {
				String orderStr = (String)condition.get(ORDER_STR);//排序字段
				String orderView = (String)condition.get(ORDER_VIEW);//排序方式
				request.addOrder(orderStr,orderView);
			}
			/*
			else{
				request.addOrder(Phase.ORDER_PHASE, ApiConstant.API_REQUEST_ORDER_DESC);
			}
			*/
		}
		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("查询彩期,api request String: {}", request.toQueryString());
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("调用API根据条件查询彩期列表异常!{}",e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("查询彩期,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		ResultBean resultBean = new ResultBean();
		if (response == null) {
			logger.error("API查询彩期列表失败");
			throw new ApiRemoteCallFailedException("API查询彩期列表失败");
		}
		resultBean.setCode(response.getCode());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询彩期列表的请求异常 !rc={}, message={}", response.getCode(), response.getMessage());
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setMessage(response.getMessage());
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API查询彩期为空");
		}
		
		List<Phase> list = Phase.convertFromJSONArray(response.getData());
		
		if (pageBean == null) {
			pageBean = new PageBean();
		}
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
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		map.put(Global.API_MAP_KEY_RESULTBEAN, resultBean);
		return map;
	}

	@Override
	public Map<String, Object> getSfpMatches(Map<String, Object> condition,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询胜负彩赛程");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFP_LIST);
		String param = null;
		if (condition!=null) {
			//期数
			if (condition.containsKey(Phase.QUERY_PHASE)) {
				param = (String)condition.get(Phase.QUERY_PHASE);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Phase.QUERY_PHASE, param);
				}
			}
			
			//排序方式
			if (condition.containsKey(ORDER_STR) && condition.containsKey(ORDER_VIEW)) {
				String orderStr = (String)condition.get(ORDER_STR);
				String orderView = (String)condition.get(ORDER_VIEW);
				request.addOrder(orderStr,orderView);
			}
			
		}
		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("查询赛程,api request String: {}", request.toQueryString());
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询胜负彩赛程异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("查询赛程,api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<SfpArrange> list = SfpArrange.convertFromJSONArray(response.getData());
		ResultBean resultBean = new ResultBean();
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询胜负彩赛程请求异常!rc={}, message={}", response.getCode(), response.getMessage());
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		
		if (pageBean==null) {
			pageBean = new PageBean();
		}
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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		map.put(Global.API_MAP_KEY_RESULTBEAN, resultBean);
		return map;
	}
	
	@Override
	public Map<String, Object> getFb46Matches(Map<String, Object> condition,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询46场赛程");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_FB46_LIST);
		String param = null;
		if (condition!=null) {
			//彩期类型
			if (condition.containsKey(Fb46Match.QUERY_TYPE)) {
				param = (String)condition.get(Fb46Match.QUERY_TYPE);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Fb46Match.QUERY_TYPE, param);
				}
			}
			//期数
			if (condition.containsKey(Fb46Match.QUERY_PHASE)) {
				param = (String)condition.get(Fb46Match.QUERY_PHASE);
				if (param!=null&&!param.equals("")) {
					request.setParameter(Fb46Match.QUERY_PHASE, param);
				}
			}
			
			//排序方式
			if (condition.containsKey(ORDER_STR) && condition.containsKey(ORDER_VIEW)) {
				String orderStr = (String)condition.get(ORDER_STR);
				String orderView = (String)condition.get(ORDER_VIEW);
				request.addOrder(orderStr,orderView);
			}
			
		}
		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("查询赛程,api request String: {}", request.toQueryString());
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询46场赛程异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("查询赛程,api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<Fb46Match> list = Fb46Match.convertFromJSONArray(response.getData());
		ResultBean resultBean = new ResultBean();
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询46场赛程请求异常!rc={}, message={}", response.getCode(), response.getMessage());
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		
		if (pageBean==null) {
			pageBean = new PageBean();
		}
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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		map.put(Global.API_MAP_KEY_RESULTBEAN, resultBean);
		return map;
	}
	
	@Override
	/**
	 * 设置彩期状态
	 * @param phase
	 * @return
	 */
	public ResultBean modifyPhaseStatus(PhaseType phaseType,String phaseNo,PhaseStatus phaseStatus) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改彩期状态");
		ApiRequest request = new ApiRequest();
		ResultBean resultBean = new ResultBean();
		request.setUrl(ApiConstant.API_URL_PHASE_UPDATE);
		request.setParameter(Phase.SET_PHASETYPE, String.valueOf(phaseType.getValue()));//指定彩期类型
		request.setParameter(Phase.SET_PHASE, phaseNo);//指定彩期号
		request.setParameterForUpdate(Phase.SET_STATUS, String.valueOf(phaseStatus.getValue()));//状态值
		logger.info("修改彩期状态,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改彩期状态异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("修改彩期状态,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		return resultBean;
	}

	@Override
	public ResultBean modifyForsaleStatus(PhaseType phaseType, String phaseNo,
			YesNoStatus forsaleStatus) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改销售状态");
		ApiRequest request = new ApiRequest();
		ResultBean resultBean = new ResultBean();
		request.setUrl(ApiConstant.API_URL_PHASE_UPDATE);
		request.setParameter(Phase.SET_PHASETYPE, String.valueOf(phaseType.getValue()));//指定彩期类型
		request.setParameter(Phase.SET_PHASE, phaseNo);//指定彩期号
		request.setParameterForUpdate(Phase.SET_FORSALE, String.valueOf(forsaleStatus.getValue()));//状态值
		logger.info("修改销售状态,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改销售状态异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("修改销售状态,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		return resultBean;
	}
	/**
	 * 修改彩期终端状态
     * @param phaseType
     * @param phaseNo
     * @param terminalStatus
     * @return
     * @throws ApiRemoteCallFailedException
     */
	@Override
	public ResultBean modifyPhaseTerminalStatus(PhaseType phaseType,String phaseNo,TerminalStatus terminalStatus) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改彩期终端状态");
		ApiRequest request = new ApiRequest();
		ResultBean resultBean = new ResultBean();
		request.setUrl(ApiConstant.API_URL_PHASE_UPDATE);
		request.setParameter(Phase.SET_PHASETYPE, String.valueOf(phaseType.getValue()));//指定彩期类型
		request.setParameter(Phase.SET_PHASE, phaseNo);//指定彩期号
		request.setParameterForUpdate(Phase.SET_TERMINAL_STATUS, String.valueOf(terminalStatus.getValue()));//状态值
		logger.info("修改彩期终端状态,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改彩期终端状态异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("修改彩期终端状态,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		return resultBean;
	}
	
	@Override
	public ResultBean updatePhase(Phase phase) throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新彩期");
		ApiRequest request = new ApiRequest();
		ResultBean resultBean = new ResultBean();
		request.setUrl(ApiConstant.API_URL_PHASE_UPDATE);
		request.setParameter(Phase.SET_PHASETYPE, String.valueOf(phase.getPhaseType().getValue()));//指定彩期类型
		request.setParameter(Phase.SET_PHASE, phase.getPhase());//指定彩期号
		//开奖结果
		if (phase.getResult()!=null) {
			request.setParameterForUpdate(Phase.SET_RESULT, phase.getResult());
		}
		//开奖详情
		if (phase.getResultDetail()!=null) {
			request.setParameterForUpdate(Phase.SET_RESULT_DETAIL, phase.getResultDetail());
		}
		//开奖时间
		if (phase.getDrawTime()!=null) {
			request.setParameterForUpdate(Phase.SET_TIME_DRAW, DateUtil.formatDate(phase.getDrawTime(),DateUtil.DATETIME));
		}
		//本期投注
		if (phase.getSaleAmount()!=null) {
			request.setParameterForUpdate(Phase.SET_SALE_AMOUNT, phase.getSaleAmount());
		}
		//下期奖池
		if (phase.getPoolAmount()!=null) {
			request.setParameterForUpdate(Phase.SET_POOL_AMOUNT, phase.getPoolAmount());
		}
		//开始销售时间
		if (phase.getStartSaleTime()!=null) {
			request.setParameterForUpdate(Phase.SET_TIME_STARTSALE, DateUtil.formatDate(phase.getStartSaleTime(),DateUtil.DATETIME));
		}
		//结束销售时间
		if (phase.getEndSaleTime()!=null) {
			request.setParameterForUpdate(Phase.SET_TIME_ENDSALE, DateUtil.formatDate(phase.getEndSaleTime(),DateUtil.DATETIME));
		}
		//停止出票时间
		if (phase.getEndTicketTime()!=null) {
			request.setParameterForUpdate(Phase.SET_TIME_ENDTICKET, DateUtil.formatDate(phase.getEndTicketTime(),DateUtil.DATETIME));
		}
		//福彩3D试机号
		if (phase.getFc3dSjh()!=null) {
			request.setParameterForUpdate(Phase.SET_FC3D_SJH, phase.getFc3dSjh());
		}
		//设置开奖状态
		if (phase.getFordraw() != null) {
			request.setParameterForUpdate(Phase.SET_FORDRAW, phase.getFordraw().getValue() + "");
		}
		
		logger.info("更新彩期,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API更新彩期!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("更新彩期,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		return resultBean;
	}
	
	@Override
	public List<Phase> getPhaseListByPhaseTypeAndCount(PhaseType phaseType,Integer count) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据彩期类型查询指定数量彩期列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		
		if (count!=null&&count>=0) {
			request.setPagesize(count);
		}
		logger.info("根据彩期类型获取指定数量彩期列表,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API根据彩期类型查询指定数量彩期列表!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		logger.info("根据彩期类型获取指定数量彩期列表,api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<Phase> list = Phase.convertFromJSONArray(response.getData());
		return list;
	}
	
	@Override
	public List<Phase> getPhaseListByPhaseType(PhaseType phaseType) throws ApiRemoteCallFailedException{
		Integer count = 20;//默认20
		return getPhaseListByPhaseTypeAndCount(phaseType,count);
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
	
	@Override
	public List<Phase> batchGetPhase(PhaseType phaseType, Integer count) throws ApiRemoteCallFailedException {
		List<Phase> list = new ArrayList<Phase>();
		List<Phase> afterList = this.getPhaseListAfter(phaseType, count);
		List<Phase> beforeList = this.getPhaseListBefore(phaseType, count);
		
		if (afterList != null) {
			list.addAll(afterList);
		}
		if (beforeList != null) {
			list.addAll(beforeList);
		}
		return list;
	}
	
	@Override
	public List<Phase> getPhaseListAfter(PhaseType phaseType, Integer count) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据彩期phaseType查询当前时间后的count期数量的彩期列表");
		if ( count == null || count <= 0) {
			count = 10;
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		request.setPagesize(count);
		ApiResponse response;
		List<Phase> list;
		
		request.setParameterGreater(Phase.QUERY_TIME_STARTSALE, DateUtil.formatDate(new Date(),DateUtil.DATETIME));
		logger.info("根据彩期phaseType获取当前时间后的count期数量的彩期列表",request.toQueryString());
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API根据彩期phaseType查询当前时间后的count期数量的彩期列表异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		list = Phase.convertFromJSONArray(response.getData());
		return list;
	}
	
	@Override
	public List<Phase> getPhaseListBefore(PhaseType phaseType, Integer count) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据彩期phaseType查询当前时间后的count期数量的彩期列表");
		if ( count == null || count <= 0) {
			count = 10;
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		request.setPagesize(count);
		ApiResponse response;
		List<Phase> list;
		
		request.setParameterLess(Phase.QUERY_TIME_STARTSALE, DateUtil.formatDate(new Date(),DateUtil.DATETIME));
		logger.info("根据彩期phaseType获取当前时间后的count期数量的彩期列表",request.toQueryString());
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API根据彩期phaseType查询当前时间后的count期数量的彩期列表异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		list = Phase.convertFromJSONArray(response.getData());
		return list;
	}
	
	/**
	 * 获取当前期
	 */
	@Override
	public Phase getCurrentPhase(PhaseType phaseType) throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取当前期");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		Phase phase = null;
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		request.setParameter(Phase.QUERY_IS_CURRENT, String.valueOf(YesNoStatus.YES.getValue()));
		logger.info("获取{}当前彩期,api request String: {}", phaseType.getName(), request.toQueryString());
		ApiResponse response;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API获取当前期异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		logger.info("获取指定彩期,api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<Phase> list = Phase.convertFromJSONArray(response.getData());
		if (list!=null&&list.size()>0) {
			phase = list.get(0);
		}
		return phase;
	}
	@Override
	public PageBean getPageOfCurrentPhase(Map<String, Object> condition, PageBean pageBean) throws ApiRemoteCallFailedException{
		PhaseType currentPhaseType = null;
		if (condition == null) {
			return pageBean;
		}
		if (condition.containsKey(Phase.QUERY_PHASETYPE)) {
			currentPhaseType = PhaseType.getItem(Integer.parseInt((String)condition.get(Phase.QUERY_PHASETYPE), 10));
		}
		if (currentPhaseType == null) {
			return pageBean;
		}
		if (pageBean == null) {
			return null;
		}
		int totalCount = pageBean.getCount();
		//获取当前期
		Phase currentPhase = getCurrentPhase(currentPhaseType);
		condition.put(CURRENT_PHASE_POSITION, "1");//加入查询当前器标志
		//当前期不存在,获取结束销售时间最接近当前时间的彩期,结束销售时间>= 当前时间
		if (currentPhase == null) {
			Date nowDate = new Date();
			condition.put(CURRENT_PHASE_END_SALE_TIME, CoreDateUtils.formatDateTime(nowDate));
		}
		//当前期存在,获取大于当前起的数量,定位
		else{
			condition.put(CURRENT_PHASE_PHASENO, currentPhase.getPhase());
		}
		int validCount = 0;//符合查询当前期的有效条数
		Map<String,Object> resultMap = getPhases(condition, pageBean);
		if (resultMap != null) {
			PageBean resultPageBean = (PageBean)resultMap.get(Global.API_MAP_KEY_PAGEBEAN);
			validCount = resultPageBean.getCount();
		}
		//计算当前期所在页码
		int currentPhasePageNum = (validCount)/pageBean.getPageSize() + 1;
		pageBean.setPage(currentPhasePageNum);
		pageBean.setCount(totalCount);
		condition.remove(CURRENT_PHASE_POSITION);
		return pageBean;
	}
	
	@Override
	public List<Phase> getAppointPhaseList(PhaseType phaseType, String phase,
			Integer count) throws ApiRemoteCallFailedException {
		List<Phase> list = new ArrayList<Phase>();
		
		List<Phase> afterList = this.getAppointPhaseListAfter(phaseType, phase, count);
		List<Phase> beforeList;
		if (afterList == null) {//未查询到after就取双倍的before返回
			count = count + count;
			return this.getAppointPhaseListBefore(phaseType, phase, count);
		} else if (afterList.size() != count) {//afterList数量不够时用beforeList补充，以保证列表数量固定
			count = count+count - afterList.size();
			beforeList = this.getAppointPhaseListBefore(phaseType, phase, count);
			if (beforeList == null || beforeList.isEmpty()) {
				return afterList;
			}
			beforeList.addAll(afterList);
			return beforeList;
		}
		beforeList = this.getAppointPhaseListBefore(phaseType, phase, count);
		if (beforeList == null) {//未查询到before就取双倍的after返回
			count = count + count;
			return this.getAppointPhaseListAfter(phaseType, phase, count);
		}else if (beforeList.size() < count) {//beforeList数量不够时用afterList补充，以保证列表数量固定
			count = count+count-beforeList.size();
			afterList = this.getAppointPhaseListAfter(phaseType, phase, count);
			if (afterList == null || afterList.isEmpty()) {
				return beforeList;
			}
			afterList.addAll(beforeList);
			return afterList;
		}
		
		if (beforeList == null) {
			count = count + count;
		} else if (beforeList.size() != count) {
			count += count - afterList.size();
		}
		
		if (afterList != null) {
			list.addAll(afterList);
		}
		if (beforeList != null) {
			list.addAll(beforeList);
		}
		return list;
	}
	
	@Override
	public List<Phase> getAppointPhaseListAfter(PhaseType phaseType,
			String phase, Integer count) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询指定期数之后的彩期数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		request.setParameterBetween(Phase.SET_PHASE, phase ,null);
		request.addOrder(Phase.ORDER_PHASE, ApiConstant.API_REQUEST_ORDER_ASC);
		request.setPage(1);
		request.setPagesize(count);
		ApiResponse response;
		List<Phase> list;
		
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询指定期数之后的彩期数据异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		list = Phase.convertFromJSONArray(response.getData());
		return list;
	}
	
	@Override
	public List<Phase> getAppointPhaseListBefore(PhaseType phaseType,
			String phase, Integer count) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询指定期数之前的彩期数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		request.setParameterBetween(Phase.SET_PHASE, null ,phase);
		request.addOrder(Phase.ORDER_PHASE, ApiConstant.API_REQUEST_ORDER_DESC);
		request.setPage(1);
		request.setPagesize(count);
		ApiResponse response;
		List<Phase> list;
		
		
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询指定期数之前的彩期数据异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		list = Phase.convertFromJSONArray(response.getData());
		return list;
	}
	
	@Override
	public List<Phase> getLatestDrawedPhase(PhaseType phaseType,Integer num) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询最新期开奖数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		List<String> statusList = new ArrayList<String>();
		statusList.add(String.valueOf(PhaseStatus.REWARD.getValue()));		//已派奖
		statusList.add(String.valueOf(PhaseStatus.RESULT_SET.getValue()));	//结果已公布
		statusList.add(String.valueOf(PhaseStatus.DRAW.getValue()));		//已开奖
		request.setParameterIn(Phase.QUERY_STATUS, statusList);
		request.addOrder(Phase.ORDER_PHASE,ApiConstant.API_REQUEST_ORDER_DESC);
		request.setPage(1);
		if(num == null || num < 1){
			num = 1;
		}
		request.setPagesize(num); //取1条记录
		
		ApiResponse response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取最新期开奖数据失败");
			throw new ApiRemoteCallFailedException("API获取最新期开奖数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API获取最新期开奖数据请求异常!rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取最新期开奖数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取最新期开奖数据为空, message={}", response.getMessage());
			return null;
		}
		List<Phase> list = Phase.convertFromJSONArray(response.getData());
		if ( list.size() < 1 ) {
			logger.error("没有取到彩种" + phaseType.getName() + "的最新开奖结果信息");
			return null;
		}
		return list;	
	}
	
	@Override
	public List<Phase> findPhaseListByDeadline(Date deadline, List<LotteryType> lotteryTypeList) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询指定时间的彩期");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		request.setParameterBetween(Phase.QUERY_TIME_ENDSALE, DateUtil.formatDate(deadline, "yyyy-MM-dd 00:00:00"), DateUtil.formatDate(deadline, "yyyy-MM-dd 23:59:59"));
		
		if (lotteryTypeList != null) {
			List<String> phaseTypeIdList = new ArrayList<String>();
			for (LotteryType lotteryType : lotteryTypeList) {
				phaseTypeIdList.add(PhaseType.getItem(lotteryType).getValue() + "");
			}
			request.setParameterIn(Phase.QUERY_PHASETYPE, phaseTypeIdList);
		}
		
		logger.info("请求：" + request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取指定时间的彩期失败");
			throw new ApiRemoteCallFailedException("API获取指定时间的彩期失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取指定时间的彩期请求异常!rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取指定时间的彩期请求异常");
		}
		if (response.getData() == null) {
			logger.warn("API获取指定时间的彩期为空");
			return null;
		}
		List<Phase> list = Phase.convertFromJSONArray(response.getData());
		if (list == null || list.size() == 0) {
			return null;
		}
		
		return list;
	}
	
	@Override
	public ResultBean setPhaseCurrent(PhaseType phaseType, String phase ) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改彩期状态");
		ApiRequest request = new ApiRequest();
		ResultBean resultBean = new ResultBean();
		request.setUrl(ApiConstant.API_URL_PHASE_CURRENT_SET);
		request.setParameter(Phase.SET_PHASETYPE, String.valueOf(phaseType.getValue()));//指定彩期类型
		request.setParameter(Phase.SET_PHASE,phase);//指定彩期号
		logger.info("设置彩期是否为当前期,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改彩期状态异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("修改彩期状态,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode()!=ApiConstant.RC_SUCCESS) {
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		resultBean.setCode(response.getCode());
		resultBean.setMessage(response.getMessage());
		return resultBean;
	}
	
	@Override
	public Phase getNearestPhase(PhaseType phaseType,Date appointTime) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询最近彩期");
		Phase phase = null;
		Date time = appointTime;
		if (time == null) {
			time = new Date();
		}
		// 查大于指定时间的,如果有获得第一个,升序
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		request.setParameterBetween(Phase.QUERY_TIME_DRAW, CoreDateUtils.formatDateTime(time), null);
		request.addOrder(Phase.ORDER_PHASE, ApiConstant.API_REQUEST_ORDER_ASC);
		logger.info("获取{}最近彩期(大于指定时间),api request String: {}", phaseType.getName(), request.toQueryString());
		ApiResponse response;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		logger.info("获取最近彩期(大于指定时间),api response code = {}, message = {}", response.getCode(), response.getMessage());
		List<Phase> list = Phase.convertFromJSONArray(response.getData());
		if (list!=null&&list.size()>0) {
			phase = list.get(0);
		}
		// 如果没有大于指定时间的,查询小于指定时间的,降序
		if (phase == null) {
			request = new ApiRequest();
			request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
			
			request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
			request.setParameterBetween(Phase.QUERY_TIME_DRAW, null, CoreDateUtils.formatDateTime(time));
			request.addOrder(Phase.ORDER_PHASE, ApiConstant.API_REQUEST_ORDER_DESC);
			logger.info("获取{}最近彩期(查询小于指定时间),api request String: {}", phaseType.getName(), request.toQueryString());
			try {
				response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage());
				throw new ApiRemoteCallFailedException();
			}
			if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
				logger.info("API响应结果为空");
				return null;
			}
			logger.info("获取最近彩期(查询小于指定时间),api response code = {}, message = {}", response.getCode(), response.getMessage());
			list = Phase.convertFromJSONArray(response.getData());
			if (list!=null&&list.size()>0) {
				phase = list.get(0);
			}
		}
		
		return phase;
	}
	@Override
	public Phase fetchLotteryDraw(LotteryType lotteryType,FetcherType fetcherType,String phaseNo) throws Exception{
		String msg = null;
		// 获取开奖器
		ILotteryDrawFetcher fetcher = this.getFetcher(lotteryType);
		if (fetcher == null) {
			msg = "未找到彩种("+lotteryType.getName()+")的抓取器";
			throw new Exception(msg);
		}
		
		// 抓取开奖结果
		
		LotteryDraw lotteryDraw = fetchLotteryDraw(fetcher, fetcherType , phaseNo);
		if (lotteryDraw == null) {
			msg = "抓取彩种("+lotteryType.getName()+")的开奖结果失败";
			logger.error(msg);
			throw new Exception(msg);
		}
		
		logger.info("抓取到({})开奖结果, phase={}", lotteryType.getName(), lotteryDraw.getPhase());
		logger.info("result={}", lotteryDraw.getResult());
		logger.info("timeDraw={}", lotteryDraw.getTimeDraw());
		
		
		LotteryConfig lotteryConfig = lotteryCommonService.getLotteryConfigFromCache(lotteryType);
		if (lotteryConfig == null) {
			msg = "未取到彩种("+lotteryType.getName()+")的彩种配置";
			throw new Exception(msg);
		}
		
		String result = convertFetchedResult(lotteryDraw.getResult(), lotteryConfig);
		String resultDetail = null;
		try {
			resultDetail = convertFetchedResultDetail(lotteryDraw.getResultDetail(),lotteryConfig);
		} catch (UnsupportedLotteryConfigException e) {
			msg = "抓取("+lotteryType.getName()+")第<"+lotteryDraw.getPhase()+">期开奖结果发生错误,未配置彩种对应的彩票配置项,转换开奖详情出错,本次抓取失败";
			throw new Exception(msg);
		}
		
		// 转换成抓取到的彩期
		Phase fetchedPhase = convertToPhase(lotteryDraw);
		fetchedPhase.setResult(result);
		fetchedPhase.setResultDetail(resultDetail);
		
		
		return fetchedPhase;
	}
	
	@Override
	public LotteryDraw fetchLotteryDraw(ILotteryDrawFetcher fetcher, FetcherType fetcherType, String phaseNo) throws Exception{
		String msg = null;
		// 按配置的抓取类型
		if (fetcherType == null) {
			throw new Exception("抓取类型为null");
		}
		
		// 抓取该抓取类型的页面当前期结果
		
		try {
			LotteryDraw lotteryDraw = fetcher.fetch(phaseNo, fetcherType);
			if (lotteryDraw == null) {
				msg = "抓取类型"+fetcherType.getName()+"抓取结果出错，请确认抓取彩期为已开奖彩期！";
				throw new Exception(msg);
			}
			lotteryDraw = FetcherLotteryDrawConverter.convertPrizeItem(fetcherType,lotteryDraw);
			return lotteryDraw;
		} catch (UnsupportedFetcherTypeException e) {
			logger.warn("不支持的抓取类型, fetcherName={}, fetcherId={}", fetcherType.getName(), fetcherType.getValue());
			msg = "不支持的抓取类型, fetcherName="+fetcherType.getName()+", fetcherId="+fetcherType.getValue();
			throw new UnsupportedFetcherTypeException(msg);
		}

	}
	/**
	 * 根据绑定器获取指定彩种的抓取器
	 * @param lotteryType
	 * @return
	 */
	protected ILotteryDrawFetcher getFetcher(LotteryType lotteryType) {
		return this.fetcherBinder.get(lotteryType.getValue());
	}
	
	@Override
	public String convertFetchedResult(String fetchedResult,
			LotteryConfig lotteryConfig) {
		String convertResult = null;
		try {
			convertResult = FetcherLotteryDrawConverter.convertFetchResult2DBJsonString(fetchedResult, lotteryConfig);
		} catch (UnmatchedLotteryDrawResultException e) {
			logger.error(e.getMessage(), e);
			convertResult = fetchedResult;
		}
		return convertResult;
	}

	@Override
	public String convertFetchedResultDetail(
			List<LotteryDrawPrizeItem> fetchedResultDetail,LotteryConfig lotteryConfig) throws UnsupportedLotteryConfigException {
		List<LotteryDrawPrizeItem> convertResultDetail = null;
		try {
			convertResultDetail = FetcherLotteryDrawConverter.convertFetchResultDetailPrizeItemName2Key(fetchedResultDetail, lotteryConfig);
		} catch (UnsupportedLotteryConfigException e) {
			logger.error(e.getMessage(),e);
			throw new UnsupportedLotteryConfigException(e.getMessage());
		}
		return LotteryDrawPrizeItem.convertListToDBJsonString(convertResultDetail);
	}
	
	/**
	 * 获取当前销售的彩期列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> findVenderOnSalePhases(PhaseType phaseType, String processCode) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询在售的彩期列表");
		try {
			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put(BIService.PROCESS_CODE, processCode);
			requestMap.put(Phase.QUERY_PHASETYPE, phaseType.getValue() + "");
			
			Map<String, String> responseMap = bIService.request(requestMap);
			if (responseMap == null || responseMap.size() == 0) {
				logger.error("查询在售的彩期列表响应为空");
				throw new ApiRemoteCallFailedException("查询在售的彩期列表响应为空");
			}
			if (!responseMap.get(BIService.RESP_CODE).equals("0000")) {
				logger.error(requestMap.get(BIService.RESP_MESG));
				throw new ApiRemoteCallFailedException(requestMap.get(BIService.RESP_MESG));
			}
			if (responseMap.get(BIService.RESP_DATA) == null) {
				logger.error("查询在售的彩期列表响应数据为空");
				throw new ApiRemoteCallFailedException("查询在售的彩期列表响应数据为空");
			}
			
			List<Phase> phaseList = new ArrayList<Phase>();
			JSONArray array = JSONArray.fromObject(responseMap.get(BIService.RESP_DATA));
			for (Iterator iterator = array.iterator(); iterator.hasNext();) {
				JSONObject object = (JSONObject) iterator.next();
				Phase phase = new Phase();
				phase.setPhase(object.getString(Phase.JSON_KEY_PHASE));																			//彩种
				phase.setPhaseType(PhaseType.getItem(object.getInt(Phase.JSON_KEY_PHASETYPE_VALUE)));											//彩期类型
				phase.setStartSaleTime(CoreDateUtils.parseDate(object.getString(Phase.JSON_KEY_STARTSALETIME),CoreDateUtils.DATETIME));			//开始销售时间
				phase.setEndSaleTime(CoreDateUtils.parseDate(object.getString(Phase.JSON_KEY_ENDSALETIME),CoreDateUtils.DATETIME));				//结束销售时间
				phase.setEndTicketTime(CoreDateUtils.parseDate(object.getString(Phase.JSON_KEY_ENDTICKETTIME),CoreDateUtils.DATETIME));			//出票时间
				phase.setDrawTime(CoreDateUtils.parseDate(object.getString(Phase.JSON_KEY_DRAWTIME),CoreDateUtils.DATETIME));					//开奖时间
				
				phaseList.add(phase);
			}
			if (phaseList == null || phaseList.size() == 0) {
				logger.info("在售的彩期列表为空");
			}
			return phaseList;
		} catch (Exception e) {
			logger.error("查询在售的彩期列表异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException("查询在售的彩期列表响应为空");
		}
	}
	
	/**
	 * 删除彩期
	 * @param phases
	 * @return
	 */
	@Override
	public ResultBean deletePhases(LotteryType lotteryType,List<String> phases) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除彩期");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_DELETE);
		request.setParameter(Phase.SET_PHASETYPE, PhaseType.getItem(lotteryType).getValue()+"");
		request.setParameterIn(Phase.SET_PHASES,phases);//指定彩期号list
		
		logger.info("删除彩期,api request String: {}", request.toQueryString());
		
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("调用api删除彩期异常!{}" + e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		if (response == null) {
			logger.error("调用API删除彩期失败");
			throw new ApiRemoteCallFailedException("调用API删除彩期失败");
		}
		
		ResultBean resultBean = new ResultBean();
		resultBean.setCode(response.getCode());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除彩期请求异常");
			resultBean.setResult(false);
		} else {
			resultBean.setResult(true);
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("调用API删除彩期成功失败数据为空");
			throw new ApiRemoteCallFailedException("调用API删除彩期成功失败数据为空");
		}
		
		JSONArray successArray = response.getData().getJSONObject(0).getJSONArray("success");
		JSONArray failArray = response.getData().getJSONObject(0).getJSONArray("fail");
		String msg = "删除彩期,成功：" + successArray.toString() + "；失败：" + failArray.toString();
		resultBean.setMessage(msg);
		
		return resultBean;
	}
	
	@Override
	public Phase fetchFC3DSJH(LotteryType lotteryType, FetcherType fetcherType,
			String phaseNo) throws Exception {
		ILotteryExtendFetcher fetcher = new FC3DExtendSJHFetcher();
		LotteryExtendItem lotteryExtendItem = fetcher.fetch(phaseNo, FetcherType.T_ZJOL);
		
		JSONObject rs = LotteryExtendItem.toJSON(lotteryExtendItem);
		Phase phase = new Phase();
		if (rs.get("phase") != null && !"".equals(rs.get("phase")) && rs.get("phase").equals(phaseNo)) {	
			phase.setPhase(rs.get("phase").toString());
		} else {
			return null;
		}
		if (rs.get("sjh") != null && !"".equals(rs.get("sjh"))) {
			phase.setFc3dSjh(rs.get("sjh").toString().replace("\"", "").replace("[","").replace("]", ""));
		}
		phase.setPhase(phaseNo);
		phase.setPhaseType(PhaseType.getItem(lotteryType.getValue()));
		phase.setExt(rs.getString("machine") + "," + rs.getString("ball"));
		return phase;
	}
	
	@Override
	public Phase convertToPhase(LotteryDraw lotteryDraw) {
		Phase phase = null;
		phase = FetcherLotteryDrawConverter.convertFromLotteryDraw(lotteryDraw);
		return phase;
	}
	public Map<Integer, ILotteryDrawFetcher> getFetcherBinder() {
		return fetcherBinder;
	}
	public void setFetcherBinder(Map<Integer, ILotteryDrawFetcher> fetcherBinder) {
		this.fetcherBinder = fetcherBinder;
	}
	
	@Override
	public Phase getNextPhase(PhaseType phasetype, String phase) {
		logger.info("进入调用API查询下一彩期");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_NEXT_QUERY);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phasetype.getValue()));
		request.setParameter(Phase.QUERY_PHASE, phase);
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage());
			return null;
		}
		if (response == null) {
			logger.error("API获取下一彩期失败");
			return null;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API获取下一彩期请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return null;
		}
		
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API获取下一彩期为空, message={}", response.getMessage());
			return null;
		}
		
		return Phase.convertFromJSONArray(response.getData()).get(0);
	}

	@Override
	public ApiResponse invokePhaseHandlerRemoteCall(LotteryType lotteryType, String processCode) throws ApiRemoteCallFailedException {
		// 先获取该彩种对应彩期守护的engine地址
		String address = null;
		try {
			address = engineAddressConfigService.getLotteryAddress(lotteryType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (address == null) {
			logger.error("获取engine地址出错, {}", lotteryType);
			throw new ApiRemoteCallFailedException("获取engine地址出错");
		}
		
		ApiConfig apiConfig = new ApiConfig();
		apiConfig.setBaseUrl(address);
		
		DirectApiRequest request = new DirectApiRequest();
		request.setUrl(EngineApiUrlConstant.PHASE_HANDLER);
		request.setParameter("lotteryType", String.valueOf(lotteryType.getValue()));
		request.setParameter("processCode", processCode);
		
		return apiRequestService.request(apiConfig, request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
	}
	

	/**
	 * 获取竞彩足球当前销售的彩期列表
	 */
	@Override
	public List<Phase> findOnSalePhases(PhaseType phaseType, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询彩期");
		List<Phase> list = null;
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
        request.setParameter(Phase.QUERY_FORSALE, String.valueOf(YesNoStatus.YES.getValue()));
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));//彩期类型

        request.addOrder(Phase.QUERY_PHASE, ApiConstant.API_REQUEST_ORDER_ASC);

		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("查询彩期,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("调用API根据条件查询彩期列表异常!{}",e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("查询彩期,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询彩期列表的请求异常 !rc={}, message={}", response.getCode(), response.getMessage());
		} else {
			if (response.getData() == null || response.getData().isEmpty()) {
				logger.error("API查询彩期为空");
			}else{
				list = Phase.convertFromJSONArray(response.getData());
			}
		}
		return list;
	}
	
	public BIService getbIService() {
		return bIService;
	}

	public void setbIService(BIService bIService) {
		this.bIService = bIService;
	}

	public LotteryCommonService getLotteryCommonService() {
		return lotteryCommonService;
	}

	public void setLotteryCommonService(LotteryCommonService lotteryCommonService) {
		this.lotteryCommonService = lotteryCommonService;
	}

	public void setEngineAddressConfigService(EngineAddressConfigService engineAddressConfigService) {
		this.engineAddressConfigService = engineAddressConfigService;
	}

	@Override
	public List<Phase> findByPhaseNoBetween(LotteryType lotteryType,
			String beginPhase, String endPhase, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		if (lotteryType == null) {
			logger.error("未指定彩票类型");
			return null;
		}
		
		if (beginPhase == null && endPhase == null) {
			throw new ApiRemoteCallFailedException("必须指定开始期号或结束期号");
		}
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_QUERY);
		
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(PhaseType.getItem(lotteryType.getValue()).getValue()));
		
		request.setParameterBetween(Phase.QUERY_PHASE, beginPhase, endPhase);
		request.addOrder(Phase.ORDER_PHASE, ApiConstant.API_REQUEST_ORDER_ASC);
		
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("根据彩种和开始期号或结束期号查询彩期列表,api request String: {}", request.toQueryString());
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("根据彩种和开始期号或结束期号查询彩期列表,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("根据彩种和开始期号或结束期号查询彩期列表请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return null;
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.info("根据彩种和开始期号或结束期号查询彩期列表为空, message={}", response.getMessage());
			return null;
		}
		List<Phase> list = Phase.convertFromJSONArray(response.getData());
		
		if (pageBean != null) {
			pageBean.setCount(response.getTotal());
		}
		return list;
	}

	@Override
	public Phase get(LotteryType lotteryType, String phase) {
		return get(PhaseType.getItem(lotteryType), phase);
	}
	@Override
	public Phase get(PhaseType phaseType, String phase) {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_GET);
		request.setParameter(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		request.setParameter(Phase.QUERY_PHASE, phase);
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage());
			return null;
		}
		if (response == null) {
			logger.error("API获取彩期失败");
			return null;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API获取彩期请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return null;
		}
		
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API获取彩期为空, message={}", response.getMessage());
			return null;
		}
		
		return Phase.convertFromJSONArray(response.getData()).get(0);
	}
	@Override
	public boolean updatePhaseTime(Phase phase)
			throws ApiRemoteCallFailedException {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PHASE_UPDATE);
		
		request.setParameter(Phase.SET_PHASETYPE, String.valueOf(phase.getPhaseType().getValue()));//指定彩期类型
		request.setParameter(Phase.SET_PHASE, phase.getPhase());//指定彩期号
		
		//开奖时间
		if (phase.getDrawTime()!=null){
			request.setParameterForUpdate(Phase.SET_TIME_DRAW, CoreDateUtils.formatDateTime(phase.getDrawTime()));
		}
		//开始销售时间
		if (phase.getStartSaleTime() != null) {
			request.setParameterForUpdate(Phase.SET_TIME_STARTSALE, CoreDateUtils.formatDateTime(phase.getStartSaleTime()));
		}
		//结束销售时间
		if (phase.getEndSaleTime() != null) {
			request.setParameterForUpdate(Phase.SET_TIME_ENDSALE, CoreDateUtils.formatDateTime(phase.getEndSaleTime()));
		}
		//停止出票时间
		if (phase.getEndTicketTime() != null) {
			request.setParameterForUpdate(Phase.SET_TIME_ENDTICKET, CoreDateUtils.formatDateTime(phase.getEndTicketTime()));
		}
		
		logger.info("更新彩期,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage());
			throw new ApiRemoteCallFailedException();
		}
		logger.info("更新彩期,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS){
			return false;
		}
		return true;
	}
	
}
