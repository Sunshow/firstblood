package web.service.impl.lottery;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.DcRaceService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.ExcelUtil;
import com.lehecai.core.api.*;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.bean.PairValue;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.UnsupportedFetcherTypeException;
import com.lehecai.core.lottery.DcRaceStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.dc.DcLotteryDrawItem;
import com.lehecai.core.lottery.fetcher.dc.DcScheduleItem;
import com.lehecai.core.lottery.fetcher.dc.impl.BaseDcLotteryDrawFetcher;
import com.lehecai.core.lottery.fetcher.dc.impl.BaseDcScheduleFetcher;
import com.lehecai.core.util.CoreDateUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class DcRaceServiceImpl implements DcRaceService {
	private final Logger logger = LoggerFactory.getLogger(DcRaceServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private BaseDcScheduleFetcher dcScheduleFetcher;
	private BaseDcLotteryDrawFetcher dcLotteryDrawFetcher;
	private List<String> matchPriorityList;
	
	private int dcListMaxSize = 500;
	
	@Override
	public List<DcRace> getRecommendDcRace(String phaseNo, int count) {
		List<DcRace> recommendDcRaces = null;
		if (count <= 0) {
			return recommendDcRaces;
		}
		//查询本期单场所有比赛
		List<DcRace> dcRaces = getDcRaceListByPhase(phaseNo);
		if (dcRaces != null && !dcRaces.isEmpty()) {
			//过滤推荐count场赛事
			recommendDcRaces = filterDcRaces(dcRaces, count);
		}
		
		return recommendDcRaces;
	}
	@Override
	public int getDcRaceSaleCount(String phaseNo) {
		if ("".equals(phaseNo)) {
			return 0;
		}
		//查询本期单场所有比赛
		List<DcRace> dcRaces = getDcRaceListByPhase(phaseNo);
		if (dcRaces != null && !dcRaces.isEmpty()) {
			List<DcRace> canSellDcRaces = filterCanSell(dcRaces, DcRaceStatus.CAN_BUY);
			if (canSellDcRaces != null) {
				return canSellDcRaces.size();
			}
		}
		return 0;
	}
	//过滤推荐count场赛事
	private List<DcRace> filterDcRaces(List<DcRace> dcRaces, int count) {
		//过滤状态可售场次
		List<DcRace> canSellDcRaces = filterCanSell(dcRaces, DcRaceStatus.CAN_BUY);
		
		//1、未开赛不足count场，取全部中最后count场
		if (canSellDcRaces.size() < count) {
			if (dcRaces != null && dcRaces.size() > count) {
				canSellDcRaces = dcRaces.subList(dcRaces.size()-count, dcRaces.size());
			}
		} else {//2、未开赛够count场，按优先级排序
			canSellDcRaces = orderPriority(canSellDcRaces);
			canSellDcRaces = canSellDcRaces.subList(0, count);
		}
		
		return canSellDcRaces;
	}

	//按优先级排序
	private List<DcRace> orderPriority(List<DcRace> canSellDcRaces) {
		if (canSellDcRaces == null || canSellDcRaces.isEmpty()) {
			return null;
		}
		for (int i = 0; i < canSellDcRaces.size(); i++) {
			for (int j = i; j < canSellDcRaces.size(); j++) {
				//优先级高排在前面，否则场次靠前排前面
				if (canSellDcRaces.get(i).getPriority() < canSellDcRaces.get(j).getPriority()) {
					DcRace tempDcRace = canSellDcRaces.get(i);
					canSellDcRaces.set(i, canSellDcRaces.get(j));
					canSellDcRaces.set(j, tempDcRace);
				} else if (canSellDcRaces.get(i).getMatchNum() > canSellDcRaces.get(j).getMatchNum()) {
					DcRace tempDcRace = canSellDcRaces.get(i);
					canSellDcRaces.set(i, canSellDcRaces.get(j));
					canSellDcRaces.set(j, tempDcRace);
				}
			}
		}
		return canSellDcRaces;
	}

	//过滤
	private List<DcRace> filterCanSell(List<DcRace> dcRaces, DcRaceStatus status) {
		if (dcRaces == null || dcRaces.isEmpty()) {
			return null;
		}
		List<DcRace> canSellDcRaces = new ArrayList<DcRace>();
		Date nowDate = new Date();
		for (DcRace dcRace : dcRaces) {
			try {
				if (dcRace.getStatus().getValue() == status.getValue() && nowDate.before(dcRace.getMatchDate())){
					canSellDcRaces.add(dcRace);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return canSellDcRaces;
	}

	@Override
	public ResultBean batchCreatePhase(List<DcRace> dcRaces) {
		ResultBean resultBean = new ResultBean();
		
		Map<Integer, DcRace> dbDcRaceMap = null;
		dbDcRaceMap = packDcRace(dcRaces, dbDcRaceMap);
		Map<String, DcRace> msgDcRaceMap = new HashMap<String, DcRace>();
		
		//新增赛程
		List<List<DcRace>> dcRaceMultCreateList = new ArrayList<List<DcRace>>();
		//更新赛程
		List<List<DcRace>> dcRaceMultUpdateList = new ArrayList<List<DcRace>>();
		if (dcRaces == null) {
			resultBean.setResult(false);
			resultBean.setMessage("批量操作北京单场赛程数据为空！");
		}
		//分页
		for (DcRace dcRace : dcRaces) {
			if (dbDcRaceMap == null || dbDcRaceMap.get(dcRace.getMatchNum()) == null) {
				List<DcRace> dcRaceList = null;
				if (dcRaceMultCreateList.size() == 0) {
					dcRaceList = new ArrayList<DcRace>();
					dcRaceMultCreateList.add(dcRaceList);
				} else {
					List<DcRace> tempList = dcRaceMultCreateList.get(dcRaceMultCreateList.size()-1);
					if (tempList.size() == Global.BATCH_DEAL_NUM.intValue()) {
						dcRaceList = new ArrayList<DcRace>();
						dcRaceMultCreateList.add(dcRaceList);
					} else {
						dcRaceList = tempList;
					}
				}
				dcRaceList.add(dcRace);
			} else {
				List<DcRace> dcRaceList = null;
				if (dcRaceMultUpdateList.size() == 0) {
					dcRaceList = new ArrayList<DcRace>();
					dcRaceMultUpdateList.add(dcRaceList);
				} else {
					List<DcRace> tempList = dcRaceMultUpdateList.get(dcRaceMultUpdateList.size()-1);
					if (tempList.size() == Global.BATCH_DEAL_NUM.intValue()) {
						dcRaceList = new ArrayList<DcRace>();
						dcRaceMultUpdateList.add(dcRaceList);
					} else {
						dcRaceList = tempList;
					}
				}
				dcRaceList.add(dcRace);
			}
			if (dcRace.getId() != null) {
				msgDcRaceMap.put(dcRace.getId()+"", dcRace);
			}
			
		}
		
		List<String> successList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		if (dcRaceMultCreateList.size() > 0 && dcRaceMultCreateList.get(0).size() > 0) {
			for (List<DcRace> dcRaceList : dcRaceMultCreateList) {
				saveBatchDcRace(dcRaceList, successList, failureList);
			}
		}
		if (dcRaceMultUpdateList.size() > 0 && dcRaceMultUpdateList.get(0).size() > 0) {
			for (List<DcRace> dcRaceList : dcRaceMultUpdateList) {
				updateBatchDcRace(dcRaceList, successList, failureList, dbDcRaceMap);
			}
		}
		
		if (successList.size() == dcRaces.size()) {
			resultBean.setResult(true);
			resultBean.setMessage("批量操作北京单场赛程成功！");
		} else {
			StringBuffer sb = new StringBuffer();
			//更新时key为id
			for (int i=0; i<failureList.size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
				String id = failureList.get(i);
				if (msgDcRaceMap != null) {
					DcRace dcRace = msgDcRaceMap.get(id);
					if (dcRace != null) {
						sb.append(dcRace.getMatchNum());
					} else {
						sb.append(failureList.get(i));
					}
				} else {
					sb.append(id);
				}
			}
			
			resultBean.setResult(false);
			resultBean.setMessage("场次：[" + sb.toString() + "]对阵更新失败!");
		}
		return resultBean;
	}
	
	@Override
	public ResultBean batchUpdateDcRaceSp(List<DcRace> dcRaces) {
		ResultBean resultBean = new ResultBean();
		
		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_MULTIPLE_UPDATE);
		Map<String, DcRace> dcRaceSpMap = new HashMap<String, DcRace>();
		boolean flag = true;
		if(dcRaces != null){
			for(DcRace dcRace : dcRaces){
				try {
					SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
					simpleApiBatchUpdateItem.setKey(dcRace.getId());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_WHOLE_SCORE, dcRace.getWholeScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_HALF_SCORE, dcRace.getHalfScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_SP_SFP, dcRace.getSpSfp());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_SP_SXDS, dcRace.getSpSxds());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_SP_JQS, dcRace.getSpJqs());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_SP_BF, dcRace.getSpBf());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_SP_BCSFP, dcRace.getSpBcsfp());
					dcRaceSpMap.put(dcRace.getId(), dcRace);
					request.add(simpleApiBatchUpdateItem);
					
				} catch (Exception e) {
					logger.error("第{}场对阵信息解析失败！",dcRace.getMatchNum());
				}
			}
		}
		
		logger.info("更新北京单场对阵,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API批量更新北京单场数据异常!{}", e.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage("API批量更新北京单场数据异常!");
			return resultBean;
		}
		
		if (response == null) {
			logger.error("API批量更新北京单场数据失败");
			resultBean.setResult(false);
			resultBean.setMessage("API批量更新北京单场数据异常!");
			return resultBean;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API批量更新北京单场数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage("API批量更新北京单场数据异常!");
			return resultBean;
		}
		List<String> successList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		flag = ApiResponseBatchUpdateParser.processResult(response, successList, failureList);
		if (flag) {
			if (successList.size() == dcRaces.size()) {
				resultBean.setResult(true);
				resultBean.setMessage("批量操作北京单场赛程结果SP值成功!");
			} else {
				StringBuffer sb = new StringBuffer();
				
				//更新时key为id
				for (int i=0; i<failureList.size(); i++) {
					if (i > 0) {
						sb.append(",");
					}
					String id = failureList.get(i);
					DcRace dcRace = dcRaceSpMap.get(id);
					sb.append(dcRace.getMatchNum());
				}
				
				resultBean.setResult(false);
				resultBean.setMessage("场次：[" + sb.toString() + "]对阵更新失败!");
			}
		} else {
			resultBean.setResult(false);
			resultBean.setMessage("API批量更新北京单场即时sp响应数据为空");
		}
		
		return resultBean;
	}

	@Override
	public boolean updateDcRaceSp(DcRace dcRace) throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新北京单场SP信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_UPDATE);
			createRequest.setParameterForUpdate(DcRace.SET_WHOLE_SCORE, dcRace.getWholeScore());
			createRequest.setParameterForUpdate(DcRace.SET_HALF_SCORE, dcRace.getHalfScore());
			createRequest.setParameterForUpdate(DcRace.SET_SP_SFP, dcRace.getSpSfp());
			createRequest.setParameterForUpdate(DcRace.SET_SP_SXDS, dcRace.getSpSxds());
			createRequest.setParameterForUpdate(DcRace.SET_SP_JQS, dcRace.getSpJqs());
			createRequest.setParameterForUpdate(DcRace.SET_SP_BF, dcRace.getSpBf());
			createRequest.setParameterForUpdate(DcRace.SET_SP_BCSFP, dcRace.getSpBcsfp());
			createRequest.setParameter(DcRace.SET_ID, dcRace.getId());
		} catch (Exception e) {
			logger.error("更新第{}场SP信息解析失败!",dcRace.getMatchNum());
			return false;
		}
		
		logger.info("更新北京单场SP信息,api request String: {}", createRequest.toQueryString());
		ApiResponse response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if(response == null){
			logger.error("API响应结果为空");
			return false;
		}
		logger.info("更新北京单场SP信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if (response.getCode()!=ApiConstant.RC_SUCCESS) {
			logger.error("第{}场SP信息更新失败!", dcRace.getMatchNum());
			return false;
		}
		logger.info("第{}场SP信息更新成功!", dcRace.getMatchNum());
		return true;
	}
	
	private Map<Integer, DcRace> packDcRace(
			List<DcRace> dcRaces,
			Map<Integer, DcRace> dbDcRaceMap) {
		if (dcRaces == null || dcRaces.isEmpty()) {
			return null;
		}
		//查询本期单场所有比赛
		List<DcRace> dbDcRaces = getDcRaceListByPhase(dcRaces.get(0).getPhase());
		if (dbDcRaces != null && !dbDcRaces.isEmpty()) {
			dbDcRaceMap = new HashMap<Integer, DcRace>();
			for (DcRace dbDcRace : dbDcRaces) {
				dbDcRaceMap.put(dbDcRace.getMatchNum(), dbDcRace);
			}
		}
		return dbDcRaceMap;
	}

	@Override
	public boolean compareDcRace(DcRace pageDcRace, DcRace dbDcRace) {
		if (dbDcRace == null) {
			return saveDcRace(pageDcRace);
		} else {
			dbDcRace.setMatchDate(pageDcRace.getMatchDate());
			dbDcRace.setHomeTeam(pageDcRace.getHomeTeam());
			dbDcRace.setAwayTeam(pageDcRace.getAwayTeam());
			dbDcRace.setHandicap(pageDcRace.getHandicap());
			dbDcRace.setMatchName(pageDcRace.getMatchName());
			dbDcRace.setEndSaleTime(pageDcRace.getEndSaleTime());
			dbDcRace.setPrizeTime(pageDcRace.getPrizeTime());
			dbDcRace.setPriority(pageDcRace.getPriority());
			dbDcRace.setExt(pageDcRace.getExt());
//			dbDcRace.setStatus(pageDcRace.getStatus());
//			dbDcRace.setWholeScore(pageDcRace.getWholeScore());
//			dbDcRace.setHalfScore(pageDcRace.getHalfScore());
//			dbDcRace.setSpSfp(pageDcRace.getSpSfp());
//			dbDcRace.setSpSxds(pageDcRace.getSpSxds());
//			dbDcRace.setSpJqs(pageDcRace.getSpJqs());
//			dbDcRace.setSpBf(pageDcRace.getSpBf());
//			dbDcRace.setSpBcsfp(pageDcRace.getSpBcsfp());
//			dbDcRace.setCatchId(pageDcRace.getCatchId());
			dbDcRace.setFxId(pageDcRace.getFxId());
			
			return updateDcRace(dbDcRace);
		}
	}
	
	@Override
	public List<DcRace> fetchDcRaceSpListByPhase(String phase,
			FetcherType fetcherType) {
		List<DcLotteryDrawItem> dcLotteryDrawItems = null;
		
		try {
			dcLotteryDrawItems = dcLotteryDrawFetcher.fetch(phase, fetcherType);
		} catch (UnsupportedFetcherTypeException e) {
			logger.error("不支持的抓取北京单场sp和比分的抓取类型:{}", fetcherType.getName());
			return null;
		} catch (Exception e) {
			logger.error("抓取北京单场sp和比分失败！{}", e.getMessage());
			return null;
		}
		
		if (dcLotteryDrawItems == null || dcLotteryDrawItems.isEmpty()) {
			return null;
		}
		
		List<DcRace> dcRaces = new ArrayList<DcRace>();
		
		for (DcLotteryDrawItem lotteryDrawItem : dcLotteryDrawItems) {
			DcRace dcRace = new DcRace();
			dcRace.setPhase(phase);
			dcRace.setMatchNum(lotteryDrawItem.getMatchIndex());
			dcRace.setMatchName(lotteryDrawItem.getLeague());
			dcRace.setHomeTeam(lotteryDrawItem.getHomeTeam());
			dcRace.setAwayTeam(lotteryDrawItem.getAwayTeam());
			dcRace.setHalfScore(lotteryDrawItem.getHalfTimeResult());
			dcRace.setWholeScore(lotteryDrawItem.getFullTimeResult());
			dcRace.setSpSfp(lotteryDrawItem.getSpSFP());
			dcRace.setSpSxds(lotteryDrawItem.getSpSXDS());
			dcRace.setSpJqs(lotteryDrawItem.getSpJQS());
			dcRace.setSpBf(lotteryDrawItem.getSpBF());
			dcRace.setSpBcsfp(lotteryDrawItem.getSpBCSFP());
			dcRaces.add(dcRace);
		}
		return dcRaces;
	}

	@Override
	public List<DcRace> fetchDcRaceListByPhase(String phase, FetcherType fetcherType) {
		List<DcScheduleItem> dcScheduleItems = null;
		
		try {
			dcScheduleItems = dcScheduleFetcher.fetch(phase, fetcherType);
		} catch (UnsupportedFetcherTypeException e) {
			logger.error("不支持的抓取北京单场对阵的抓取类型:{}", fetcherType.getName());
			return null;
		} catch (Exception e) {
			logger.error("抓取北京单场对阵失败！{}", e.getMessage());
			return null;
		}
		
		if (dcScheduleItems == null || dcScheduleItems.isEmpty()) {
			return null;
		}
		
		List<DcRace> dcRaces = new ArrayList<DcRace>();
		
		for (DcScheduleItem scheduleItem : dcScheduleItems) {
			try {
				DcRace dcRace = new DcRace();
				dcRace.setPhase(scheduleItem.getPhase());
				dcRace.setMatchNum(scheduleItem.getMatchIndex());
				dcRace.setMatchName(scheduleItem.getLeague());
				dcRace.setMatchDate(scheduleItem.getMatchTime());
				
				//大于5点小于十点的都设置成5点
				String [] matchDateArray = DateUtil.formatDate(scheduleItem.getMatchTime(), DateUtil.DATETIME).split(" ");
				String endSaleTime = DateUtil.formatDate(scheduleItem.getMatchTime(), DateUtil.DATETIME);
				Calendar cal = Calendar.getInstance();
				cal.setTime(scheduleItem.getMatchTime());
				
				if ((cal.get(Calendar.HOUR_OF_DAY) >= 6 && cal.get(Calendar.HOUR_OF_DAY) <9) || (cal.get(Calendar.HOUR_OF_DAY) == 9 && cal.get(Calendar.MINUTE) <= 30)) {
					endSaleTime = matchDateArray[0] + " 06:00:00";
				}
				dcRace.setEndSaleTime(DateUtil.parseDate(endSaleTime, DateUtil.DATETIME));
				
				dcRace.setHomeTeam(scheduleItem.getHomeTeam());
				dcRace.setAwayTeam(scheduleItem.getAwayTeam());
				dcRace.setHandicap(scheduleItem.getHandicap());
				dcRace.setStatus(DcRaceStatus.NO_BUY);
				if (matchPriorityList.contains(scheduleItem.getLeague())) {
					dcRace.setPriority(100);
				} else {
					dcRace.setPriority(0);
				}
				dcRaces.add(dcRace);
			} catch (Exception e) {
				logger.error("北京单场抓取转换失败！{}", e.getMessage());
			}
		}
		
		return dcRaces;
	}

	@Override
	public List<DcRace> getDcRaceListByPhase(String phase) {
		logger.info("进入调用API根据彩期获取北京单场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_LIST);
		request.setParameter(DcRace.QUERY_PHASE, phase);
		//按场次正序
		request.addOrder(DcRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		ApiResponse response = null;
		List<DcRace> list = null;
		
		logger.info("根据彩期获取北京单场对阵球队信息列表",request.toQueryString());
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询单场对阵异常!{}", e.getMessage());
		}
		if(response == null || response.getCode() != ApiConstant.RC_SUCCESS){
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = DcRace.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回北京单场数据错误!");
		}
		return list;
	}

    /**
     * 查询单场对阵信息
     * @param phase
     * @param matchNum
     * @return
     * @throws ApiRemoteCallFailedException
     */
	@Override
	public DcRace getDcRaceByMatchNum(String phase, Long matchNum) throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取北京单场对阵球队信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_SEARCH);
		request.setParameter(DcRace.QUERY_PHASE, phase + "");
		request.setParameter(DcRace.QUERY_MATCH_NUM, matchNum + "");
		
		ApiResponse response = null;
		
		logger.info("获取北京单场对阵球队信息",request.toQueryString());
		
		response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("获取北京单场对阵球队信息失败");
			throw new ApiRemoteCallFailedException("获取北京单场对阵球队信息失败");
		}
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("获取北京单场对阵球队信息请求异常");
			throw new ApiRemoteCallFailedException("获取北京单场对阵球队信息请求异常");
		}
		if (response.getData() == null || response.getData().size() == 0) {
			logger.error("北京单场对阵球队信息为空");
			return null;
		}
		
		return DcRace.convertFromJSONObject(response.getData().getJSONObject(0));
	}
	
	@Override
	public boolean saveDcRace(DcRace dcRace) {
		logger.info("进入调用API生成北京单场对阵");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_CREATE);
			createRequest.setParameterForUpdate(DcRace.SET_PHASE, dcRace.getPhase());
			createRequest.setParameterForUpdate(DcRace.SET_MATCH_NUM, String.valueOf(dcRace.getMatchNum()));
			createRequest.setParameterForUpdate(DcRace.SET_MATCH_DATE, DateUtil.formatDate(dcRace.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(DcRace.SET_END_SALE_DATE, DateUtil.formatDate(dcRace.getEndSaleTime(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(DcRace.SET_PRIZE_TIME, dcRace.getPrizeTime() == null ? null : DateUtil.formatDate(dcRace.getPrizeTime(), DateUtil.DATETIME));
			createRequest.setParameterForUpdate(DcRace.SET_HOME_TEAM, dcRace.getHomeTeam());
			createRequest.setParameterForUpdate(DcRace.SET_AWAY_TEAM, dcRace.getAwayTeam());
			createRequest.setParameterForUpdate(DcRace.SET_HANDICAP, dcRace.getHandicap());
			createRequest.setParameterForUpdate(DcRace.SET_MATCH_NAME, dcRace.getMatchName());
			createRequest.setParameterForUpdate(DcRace.SET_STATUS, String.valueOf(dcRace.getStatus().getValue()));
			createRequest.setParameterForUpdate(DcRace.SET_PRIORITY, String.valueOf(dcRace.getPriority()));
			createRequest.setParameterForUpdate(DcRace.SET_EXT, dcRace.getExt());
//			createRequest.setParameterForUpdate(DcRace.SET_WHOLE_SCORE, dcRace.getWholeScore());
//			createRequest.setParameterForUpdate(DcRace.SET_HALF_SCORE, dcRace.getHalfScore());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_SFP, dcRace.getSpSfp());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_SXDS, dcRace.getSpSxds());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_JQS, dcRace.getSpJqs());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_BF, dcRace.getSpBf());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_BCSFP, dcRace.getSpBcsfp());
//			createRequest.setParameterForUpdate(DcRace.SET_CATCH_ID, String.valueOf(dcRace.getCatchId()));
//			createRequest.setParameterForUpdate(DcRace.SET_FX_ID, String.valueOf(dcRace.getFxId()));
			
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!{}",dcRace.getMatchNum(), e.getMessage());
			return false;
		}
		
		logger.info("生成北京单场对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API保存单场数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("生成北京单场对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS || response.getTotal()==0){
			logger.error("第{}场对阵信息存储失败!", dcRace.getMatchNum());
			return false;
		}
		logger.error("第{}场对阵信息存储成功!", dcRace.getMatchNum());
		return true;
	}
	
	@Override
	public boolean saveBatchDcRace(List<DcRace> dcRaceList, List<String> successList, List<String> failureList) {
		logger.info("进入调用API批量新增北京单场对阵");

		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_MULTIPLE_CREATE);
		if (dcRaceList != null) {
			for (int i=0; i<dcRaceList.size(); i++) {
				DcRace dcRace = dcRaceList.get(i);
				try {
					SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
					simpleApiBatchUpdateItem.setKey(dcRace.getMatchNum()+"");
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_PHASE, dcRace.getPhase());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_MATCH_NUM, String.valueOf(dcRace.getMatchNum()));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_MATCH_DATE, dcRace.getMatchDate() == null ? null : DateUtil.formatDate(dcRace.getMatchDate(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_END_SALE_DATE, dcRace.getEndSaleTime() == null ? null : DateUtil.formatDate(dcRace.getEndSaleTime(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_PRIZE_TIME, dcRace.getPrizeTime() == null ? null : DateUtil.formatDate(dcRace.getPrizeTime(), DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_HOME_TEAM, dcRace.getHomeTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_AWAY_TEAM, dcRace.getAwayTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_HANDICAP, dcRace.getHandicap());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_MATCH_NAME, dcRace.getMatchName());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_STATUS, String.valueOf(dcRace.getStatus().getValue()));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_PRIORITY, String.valueOf(dcRace.getPriority()));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_EXT, dcRace.getExt());
					
					request.add(simpleApiBatchUpdateItem);
					
				} catch (Exception e) {
					logger.error("第{}场对阵信息解析失败！e={}",dcRace.getMatchNum(), e.getStackTrace());
					return false;
				}
			}
		}
		
		logger.info("批量新增北京单场对阵,api request String: {}", request.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API批量新增北京单场数据异常!{}", e.getMessage());
			return false;
		}
		
		if (response == null) {
			logger.error("API批量新增北京单场数据失败");
			return false;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API批量新增北京单场数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		Map<String,String> insertIdMap = new HashMap<String, String>();
		return ApiResponseBatchUpdateParser.processResult(response, successList, failureList, insertIdMap);
	}

	@Override
	public boolean updateDcRace(DcRace dcRace) {
		logger.info("进入调用API更新北京单场对阵");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_UPDATE);
			createRequest.setParameterForUpdate(DcRace.SET_MATCH_DATE, DateUtil.formatDate(dcRace.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(DcRace.SET_END_SALE_DATE, DateUtil.formatDate(dcRace.getEndSaleTime(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(DcRace.SET_PRIZE_TIME, dcRace.getPrizeTime() == null ? null : DateUtil.formatDate(dcRace.getPrizeTime(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(DcRace.SET_MATCH_NAME, dcRace.getMatchName());
			createRequest.setParameterForUpdate(DcRace.SET_MATCH_NUM, String.valueOf(dcRace.getMatchNum()));
			createRequest.setParameterForUpdate(DcRace.SET_HOME_TEAM, dcRace.getHomeTeam());
			createRequest.setParameterForUpdate(DcRace.SET_AWAY_TEAM, dcRace.getAwayTeam());
			createRequest.setParameterForUpdate(DcRace.SET_HANDICAP, dcRace.getHandicap());
			createRequest.setParameterForUpdate(DcRace.SET_PRIORITY, String.valueOf(dcRace.getPriority()));
//			createRequest.setParameterForUpdate(DcRace.SET_EXT, dcRace.getExt());
//			createRequest.setParameterForUpdate(DcRace.SET_WHOLE_SCORE, dcRace.getWholeScore());
//			createRequest.setParameterForUpdate(DcRace.SET_HALF_SCORE, dcRace.getHalfScore());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_SFP, dcRace.getSpSfp());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_SXDS, dcRace.getSpSxds());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_JQS, dcRace.getSpJqs());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_BF, dcRace.getSpBf());
//			createRequest.setParameterForUpdate(DcRace.SET_SP_BCSFP, dcRace.getSpBcsfp());
//			createRequest.setParameterForUpdate(DcRace.SET_CATCH_ID, String.valueOf(dcRace.getCatchId()));
			createRequest.setParameterForUpdate(DcRace.SET_FX_ID, String.valueOf(dcRace.getFxId()));
			createRequest.setParameter(DcRace.SET_ID, dcRace.getId());
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败！",dcRace.getMatchNum());
			return false;
		}
		
		logger.info("更新北京单场对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改单场数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新北京单场对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", dcRace.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", dcRace.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateBatchDcRace(List<DcRace> dcRaceList, List<String> successList, List<String> failureList, Map<Integer, DcRace> dcRaceMap) {
		logger.info("进入调用API批量更新北京单场对阵");

		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_MULTIPLE_UPDATE);
		if(dcRaceList != null){
			for(DcRace dcRace : dcRaceList){
				try {
					DcRace dbDcRace = dcRaceMap.get(dcRace.getMatchNum());
					dbDcRace.setMatchDate(dcRace.getMatchDate());
					dbDcRace.setHomeTeam(dcRace.getHomeTeam());
					dbDcRace.setAwayTeam(dcRace.getAwayTeam());
					dbDcRace.setHandicap(dcRace.getHandicap());
					dbDcRace.setMatchName(dcRace.getMatchName());
					dbDcRace.setEndSaleTime(dcRace.getEndSaleTime());
					dbDcRace.setPrizeTime(dcRace.getPrizeTime());
					dbDcRace.setPriority(dcRace.getPriority());
					dbDcRace.setExt(dcRace.getExt());
					dbDcRace.setFxId(dcRace.getFxId());
					
					SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
					simpleApiBatchUpdateItem.setKey(dbDcRace.getId());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_MATCH_DATE, dbDcRace.getMatchDate() == null ? null : DateUtil.formatDate(dbDcRace.getMatchDate(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_END_SALE_DATE, dbDcRace.getEndSaleTime() == null ? null : DateUtil.formatDate(dbDcRace.getEndSaleTime(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_PRIZE_TIME, dbDcRace.getPrizeTime() == null ? null : DateUtil.formatDate(dbDcRace.getPrizeTime(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_MATCH_NAME, dbDcRace.getMatchName());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_MATCH_NUM, String.valueOf(dbDcRace.getMatchNum()));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_HOME_TEAM, dbDcRace.getHomeTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_AWAY_TEAM, dbDcRace.getAwayTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_HANDICAP, dbDcRace.getHandicap());
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_PRIORITY, String.valueOf(dbDcRace.getPriority()));
					simpleApiBatchUpdateItem.setParameterForUpdate(DcRace.SET_FX_ID, String.valueOf(dbDcRace.getFxId()));
					request.add(simpleApiBatchUpdateItem);
					
				} catch (Exception e) {
					logger.error("第{}场对阵信息解析失败！e={}",dcRace.getMatchNum(),e.getStackTrace());
					return false;
				}
			}
		}
		
		logger.info("更新北京单场对阵,api request String: {}", request.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API批量更新北京单场数据异常!{}", e.getMessage());
			return false;
		}
		
		if (response == null) {
			logger.error("API批量更新北京单场数据失败");
			return false;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API批量更新北京单场数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return ApiResponseBatchUpdateParser.processResult(response, successList, failureList);
	}
	
	@Override
	public boolean updateStatus(String id, DcRaceStatus status) {
		logger.info("进入调用API更新北京单场对阵状态");
		ApiRequest createRequest = new ApiRequest();
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_UPDATE);
			createRequest.setParameterForUpdate(DcRace.SET_STATUS, String.valueOf(status.getValue()));
			createRequest.setParameter(DcRace.SET_ID, id);
		} catch (Exception e) {
			logger.error("北京单场对阵信息解析失败！");
			return false;
		}
		
		logger.info("更新北京单场对阵状态,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改单场状态异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新北京单场对阵状态,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("北京单场对阵状态更新失败!");
			return false;
		}
		logger.info("北京单场对阵状态更新成功!");
		return true;
	}

	@Override
	public List<DcRace> findDcRaceByStatus(List<String> dcStatusList,String phaseNo) {
		logger.info("进入调用API根据状态及彩期获取北京单场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_SEARCH);
		request.setParameter(DcRace.QUERY_PHASE, phaseNo);
		if(dcStatusList != null && dcStatusList.size() >0){
			request.setParameterIn(DcRace.QUERY_STATUS, dcStatusList);
		}
		request.setPagesize(dcListMaxSize);
		request.addOrder(DcRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		ApiResponse response = null;
		List<DcRace> list = null;
		
		logger.info("根据状态及彩期获取北京单场对阵球队信息列表",request.toQueryString());
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询单场对阵异常!{}", e.getMessage());
		}
		if(response == null || response.getCode() != ApiConstant.RC_SUCCESS){
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = DcRace.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回北京单场数据错误!");
		}
		return list;
	}
	
	/**
	 * 查询比赛日期查询单场对阵
	 */
	public List<DcRace> findDcRacesByMatchDate(Date matchDate) {
		logger.info("进入调用API查询单场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_SEARCH);
		request.setParameterBetween(DcRace.QUERY_MATCH_DATE, CoreDateUtils.formatDate(matchDate, CoreDateUtils.DATETIME), 
				CoreDateUtils.formatDate(matchDate, CoreDateUtils.DATETIME));
		
		ApiResponse response = null;
		List<DcRace> list = null;
		
		logger.info("查询单场对阵球队信息列表",request.toQueryString());
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询单场对阵异常!{}", e.getMessage());
		}
		if(response == null || response.getCode() != ApiConstant.RC_SUCCESS){
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = DcRace.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回单场数据错误!");
		}
		return list;
	}

	public List<DcRace> findDcRacesByMatchDateAndPhase(Date matchDate, String phase, PageBean pageBean) {
		logger.info("进入调用API根据彩期号与比赛开始准确时间查询单场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_SEARCH);
		request.setParameter(DcRace.QUERY_PHASE, phase);
		request.setParameter(DcRace.QUERY_MATCH_DATE, CoreDateUtils.formatDate(matchDate, CoreDateUtils.DATETIME));

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		ApiResponse response = null;
		List<DcRace> list = null;

		logger.info("查询单场对阵球队信息列表", request.toQueryString());

		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询单场对阵异常!{}", e.getMessage());
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = DcRace.convertFromJSONArray(response.getData());
			if (list != null && !list.isEmpty()) {
				if (pageBean != null) {
					pageBean.setCount(response.getTotal());
				}
				return list;
			}
		} catch (Exception e) {
			logger.error("解析返回单场数据错误!");
		}
		return null;
	}

	@Override
	public List<DcRace> findDcRacesBySpecifiedMatchNumAndPhase(List<String> matchNumList, String phase, PageBean pageBean) {
		logger.info("进入调用API根据指定的场次号查询单场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_SEARCH);
		request.setParameter(DcRace.QUERY_PHASE, phase);
		request.setParameterIn(DcRace.QUERY_MATCH_NUM, matchNumList);

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		ApiResponse response = null;
		List<DcRace> list = null;

		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询单场对阵异常!{}", e.getMessage());
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = DcRace.convertFromJSONArray(response.getData());
			if (list != null && !list.isEmpty()) {
				if (pageBean != null) {
					pageBean.setCount(response.getTotal());
				}
				return list;
			}
		} catch (Exception e) {
			logger.error("解析返回单场数据错误!");
		}
		return null;
	}

    protected Date generateDatetimeWithoutYear(Calendar calendar, String value) {
        Date date = null;

        // 处理可能存在的跨年问题
        date = CoreDateUtils.parseDate(calendar.get(Calendar.YEAR) + value, "yyyyMM月dd日HH:mm");

        // 傻逼中心有时候会带着年
        if (date == null) {
            date = CoreDateUtils.parseDate(value, "yyyy年MM月dd日HH:mm");
            if (date != null) {
                return date;
            }

            return null;
        }

        if ((calendar.getTimeInMillis() - date.getTime()) > (3600000L * 24 * 30)) {
            date = CoreDateUtils.parseDate((calendar.get(Calendar.YEAR) + 1) + value, "yyyyMM月dd日HH:mm");
        }
        if ((calendar.getTimeInMillis() - date.getTime()) < -(3600000L * 24 * 200)) {
            date = CoreDateUtils.parseDate((calendar.get(Calendar.YEAR) - 1) + value, "yyyyMM月dd日HH:mm");
        }

        return date;
    }

	@Override
	public List<Object> getDcRaceByExcel(File excelFile, final Phase phase, String phaseNo) {
		List<DcRace> dcRaces = new ArrayList<DcRace>();
		//boolean flag = true;
		String strError = null;


        Workbook workbook = null;
        try {
            workbook = ExcelUtil.createWorkbook(excelFile);
            if (workbook == null) {
                throw new RuntimeException("上传的excel文件格式不对");
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("获取Excel表Sheet错误！");
            }

            Calendar calendar = Calendar.getInstance();

            // 彩期区解析开始
            {
                PairValue<Integer, int[]> phaseColumnData = ExcelUtil.findRowIndexByColumnName(sheet, new String[] {"期号", "开始销售时间", "结束销售时间"});
                if (phaseColumnData == null || phaseColumnData.getLeft() == null || phaseColumnData.getLeft() < 0) {
                    throw new RuntimeException("未找到彩期区数据");
                }

                Row phaseRow = sheet.getRow(phaseColumnData.getLeft() + 1);
                int[] phaseColumnIndex = phaseColumnData.getRight();

                String findPhaseNo = ExcelUtil.getCellValue(phaseRow.getCell(phaseColumnIndex[0])).trim().replace("期", "");

                if (!findPhaseNo.equals(phaseNo)) {
                    throw new RuntimeException("选择彩期与Excel中彩期不一致，请确认, phase=" + phaseNo + ", found=" + findPhaseNo);
                }

                phase.setStartSaleTime(generateDatetimeWithoutYear(calendar, ExcelUtil.getCellValue(phaseRow.getCell(phaseColumnIndex[1]))));

                Calendar endSalePhaseCalendar = Calendar.getInstance();
                endSalePhaseCalendar.setTime(generateDatetimeWithoutYear(calendar, ExcelUtil.getCellValue(phaseRow.getCell(phaseColumnIndex[2]))));
                // 官方提前5分钟停售，这里不带提前量
                endSalePhaseCalendar.add(Calendar.MINUTE, 5);

                phase.setEndSaleTime(endSalePhaseCalendar.getTime());
                phase.setEndTicketTime(phase.getEndSaleTime());
            }

            Map<String, DcRace> dcRaceMap = new HashMap<String, DcRace>();

            int handicapStartRowNum = 0;
            int matchNumColumnIndex = 0;
            int handicapColumnIndex = 0;
            // 获取场次信息
            {
                PairValue<Integer, int[]> matchColumnData = ExcelUtil.findRowIndexByColumnName(sheet,
                        new String[] {"序号", "比赛", "主队", "客队", "比赛开始时间", "结束销售时间"});
                if (matchColumnData == null || matchColumnData.getLeft() == null || matchColumnData.getLeft() < 0) {
                    throw new RuntimeException("未找到赛程区数据");
                }

                int row = matchColumnData.getLeft() + 1;
                int[] matchColumnIndex = matchColumnData.getRight();

                String matchName = null;

                while (true) {
                    // 读取当前行
                    Row rowObj = sheet.getRow(row);

                    // 退出条件：没读到序号
                    if (rowObj == null) {
                        break;
                    }
                    Cell indexCell = rowObj.getCell(matchColumnIndex[0]);
                    if (indexCell == null || StringUtils.isEmpty(ExcelUtil.getCellValue(indexCell).trim())) {
                        break;
                    }

                    String matchNumCellValue = ExcelUtil.getCellValue(indexCell).trim();
                    int matchNum = 0;
                    try {
                        matchNum = Integer.parseInt(matchNumCellValue);
                    } catch (NumberFormatException e) {
                        logger.error("转换场次编码出错，输入内容为: {}", matchNumCellValue);
                        logger.error(e.getMessage(), e);

                        break;
                    }

                    DcRace dcRace = new DcRace();
                    dcRace.setPhase(phaseNo);
                    dcRace.setMatchNum(matchNum);

                    // 赛事名称
                    Cell matchNameCell = rowObj.getCell(matchColumnIndex[1]);
                    if (matchNameCell != null && StringUtils.isNotEmpty(ExcelUtil.getCellValue(matchNameCell))) {
                        String matchNameNew = matchNameCell.getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(matchNameNew)) {
                            matchName = matchNameNew;
                        }
                    }
                    dcRace.setMatchName(matchName);

                    // 主队
                    dcRace.setHomeTeam(ExcelUtil.getCellValue(rowObj.getCell(matchColumnIndex[2])).trim());
                    // 客队
                    dcRace.setAwayTeam(ExcelUtil.getCellValue(rowObj.getCell(matchColumnIndex[3])).trim());
                    // 比赛时间
                    dcRace.setMatchDate(generateDatetimeWithoutYear(calendar, ExcelUtil.getCellValue(rowObj.getCell(matchColumnIndex[4])).trim()));
                    // 停售时间
                    Calendar endSaleCalendar = Calendar.getInstance();
                    endSaleCalendar.setTime(generateDatetimeWithoutYear(calendar, ExcelUtil.getCellValue(rowObj.getCell(matchColumnIndex[5])).trim()));
                    // 官方提前5分钟停售，这里不带提前量
                    endSaleCalendar.add(Calendar.MINUTE, 5);

                    int endHour = endSaleCalendar.get(Calendar.HOUR_OF_DAY);
                    int endMinute = endSaleCalendar.get(Calendar.MINUTE);
                    //改成6点到9点半都设成6点
                    if ((endHour >= 6 && endHour < 9) || (endHour == 9 && endMinute <= 30)) {
                        endSaleCalendar.set(Calendar.HOUR_OF_DAY, 6);
                        endSaleCalendar.set(Calendar.MINUTE, 0);
                    }

                    dcRace.setEndSaleTime(endSaleCalendar.getTime());

                    dcRaces.add(dcRace);
                    dcRaceMap.put(String.valueOf(matchNum), dcRace);

                    row ++;
                }

                // 将最终行作为让分区的起始行
                handicapStartRowNum = row + 1;
                matchNumColumnIndex = matchColumnIndex[0];
                handicapColumnIndex = matchColumnIndex[5];
            }

            int drawStartRowNum = 0;
            // 获取让分信息
            {
                PairValue<Integer, int[]> handicapColumnData = ExcelUtil.findRowIndexByColumnName(sheet,
                        new String[] {"序号", "让球"});
                if (handicapColumnData == null || handicapColumnData.getLeft() == null || handicapColumnData.getLeft() < 0) {
                    // 未找到列定义的让分区，使用备选方案
                    // 跳过让分数据之间的无效行
                    while (true) {
                        // 读取当前行
                        Row rowObj = sheet.getRow(handicapStartRowNum);

                        if (rowObj == null || rowObj.getCell(matchNumColumnIndex) == null || StringUtils.isBlank(ExcelUtil.getCellValue(rowObj.getCell(matchNumColumnIndex)))) {
                            handicapStartRowNum ++;
                            continue;
                        }

                        int matchNum = 0;
                        try {
                            matchNum = Integer.parseInt(ExcelUtil.getCellValue(rowObj.getCell(matchNumColumnIndex)));
                        } catch (Exception e) {
                            handicapStartRowNum ++;
                            continue;
                        }

                        // 读取到有效场次号
                        if (dcRaceMap.containsKey(String.valueOf(matchNum))) {
                            break;
                        }

                        handicapStartRowNum ++;
                    }
                } else {
                    handicapStartRowNum = handicapColumnData.getLeft() + 1;
                    matchNumColumnIndex = handicapColumnData.getRight()[0];
                    handicapColumnIndex = handicapColumnData.getRight()[1];
                }

                while (true) {
                    // 读取当前行
                    Row rowObj = sheet.getRow(handicapStartRowNum);

                    // 退出条件：没读到序号
                    if (rowObj == null) {
                        break;
                    }
                    Cell indexCell = rowObj.getCell(matchNumColumnIndex);
                    if (indexCell == null || StringUtils.isEmpty(ExcelUtil.getCellValue(indexCell).trim())) {
                        break;
                    }

                    String matchNumCellValue = ExcelUtil.getCellValue(indexCell).trim();

                    int matchNum = 0;
                    try {
                        matchNum = Integer.parseInt(matchNumCellValue);
                    } catch (NumberFormatException e) {
                        logger.error("转换场次编码出错，输入内容为: {}", matchNumCellValue);
                        logger.error(e.getMessage(), e);

                        break;
                    }

                    DcRace dcRace = dcRaceMap.get(matchNumCellValue);
                    if (dcRace == null) {
                        throw new RuntimeException("让分区数据不正确，序号为" + matchNum + "的场次不存在");
                    }

                    Cell handicapCell = rowObj.getCell(handicapColumnIndex);

                    if (handicapCell == null) {
                        dcRace.setHandicap("0");
                    } else {
                        // 替换掉+号
                        String handicap = StringUtils.replace(ExcelUtil.getCellValue(handicapCell).trim(), "+", "");
                        if (StringUtils.isEmpty(handicap)) {
                            dcRace.setHandicap("0");
                        } else {
                            dcRace.setHandicap(handicap);
                        }
                    }

                    handicapStartRowNum ++;
                }

                // 遍历一次，将所有没有让分的比赛都设为0
                for (DcRace dcRace : dcRaces) {
                    if (StringUtils.isEmpty(dcRace.getHandicap())) {
                        dcRace.setHandicap("0");
                    }
                }

                // 将最后的行号设置为开奖信息的查找开始行
                drawStartRowNum = handicapStartRowNum + 1;
            }

            // 获取开奖时间信息
            {
                boolean foundDrawTime = false;
                while (true) {
                    // 读取当前行
                    Row rowObj = sheet.getRow(drawStartRowNum);

                    // 退出条件
                    if (rowObj == null) {
                        if (!foundDrawTime && drawStartRowNum <= 3000) {
                            // 还未找到开奖时间信息，继续遍历
                            drawStartRowNum ++;
                            continue;
                        } else {
                            break;
                        }
                    }

                    // 遍历所有单元格查找到{n}-{m}场格式的字符串
                    Cell matchRegionCell = null;
                    Cell drawTimeCell = null;

                    Iterator<Cell> cellIterator = rowObj.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell =  cellIterator.next();
                        String cellValue = ExcelUtil.getCellValue(cell);
                        if (StringUtils.isEmpty(cellValue)) {
                            continue;
                        }

                        if (cellValue.indexOf("场") > 0) {
                            matchRegionCell = cell;
                            // 查找开奖时间字段
                            int columnIndex = matchRegionCell.getColumnIndex() + 1;
                            while (true) {
                                Cell findDrawTimeCell = rowObj.getCell(columnIndex);
                                if (findDrawTimeCell == null) {
                                    break;
                                }
                                if (StringUtils.isNotBlank(ExcelUtil.getCellValue(findDrawTimeCell))) {
                                    drawTimeCell = findDrawTimeCell;
                                    break;
                                }
                                columnIndex ++;
                            }
                            if (drawTimeCell != null) {
                                break;
                            }
                            foundDrawTime = true;
                        }
                    }

                    if (matchRegionCell != null && drawTimeCell != null) {
                        int start, end;
                        String[] matchRegion = StringUtils.split(ExcelUtil.getCellValue(matchRegionCell), "-");
                        if (matchRegion.length == 1) {
                            start = Integer.parseInt(StringUtils.replace(matchRegion[0], "场", ""));
                            end = start;
                        } else {
                            start = Integer.parseInt(matchRegion[0]);
                            end = Integer.parseInt(StringUtils.replace(matchRegion[1], "场", ""));
                        }

                        // 开奖时间
                        Date drawTime = generateDatetimeWithoutYear(calendar, ExcelUtil.getCellValue(drawTimeCell).trim());
                        for (DcRace dcRace : dcRaces) {
                            if (dcRace.getMatchNum() >= start && dcRace.getMatchNum() <= end) {
                                dcRace.setPrizeTime(drawTime);
                            }
                        }
                    }

                    drawStartRowNum ++;
                    System.out.println(drawStartRowNum);
                }
            }


        } catch (Exception e) {
            logger.error("处理Excel数据出错", e);
            strError = e.getMessage();
        } finally {
            if (workbook != null) {
                ExcelUtil.closeExcel();
            }
        }

		List<Object> rtnList = new ArrayList<Object>();
		rtnList.add(dcRaces);
		rtnList.add(strError);
		return rtnList;
	}

	@Override
	public Map<String, Map<String, String>> getDcCurrentInstantSP(List<String> matchIdList, LotteryType lotteryType) throws ApiRemoteCallFailedException {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_DC_SP_GET);
		request.setParameter("lottery_type", lotteryType.getValue() + "");
		if (matchIdList != null && matchIdList.size() > 0) {
			request.setParameterIn(DcRace.QUERY_ID, matchIdList);
		}
		
		ApiResponse response = null;
		
		logger.info("获取指定id的北单赛事即时SP值",request.toQueryString());
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取指定id的北单赛事即时SP值异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException("获取指定id的北单赛事即时SP值发生API请求错误");
		}
		if(response == null || response.getCode() != ApiConstant.RC_SUCCESS){
			logger.info("API响应结果为空");
			return null;
		}
		if (response.getData() == null || response.getData().size() == 0) {
			logger.warn("API查询竞彩蓝球对阵数据为空!");
			return null;
		}
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		JSONObject obj = response.getData().getJSONObject(0);
		
		if (obj == null || obj.isNullObject()) {
			return null;
		}
		for (Iterator<?> iterator = obj.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			
			JSONObject object = obj.getJSONObject(key);
			if (object == null || object.isNullObject()) {
				continue;
			}
			Map<String, String> mapSp = new HashMap<String, String>();
			for (Iterator<?> spIterator = object.keySet().iterator(); spIterator.hasNext();) {
				String spKey = (String) spIterator.next();
				String spVal = object.get(spKey) + "";
				mapSp.put(spKey, spVal);
			}
			map.put(key, mapSp);
		}
		
		return map;
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

	public BaseDcScheduleFetcher getDcScheduleFetcher() {
		return dcScheduleFetcher;
	}

	public void setDcScheduleFetcher(BaseDcScheduleFetcher dcScheduleFetcher) {
		this.dcScheduleFetcher = dcScheduleFetcher;
	}

	public BaseDcLotteryDrawFetcher getDcLotteryDrawFetcher() {
		return dcLotteryDrawFetcher;
	}

	public void setDcLotteryDrawFetcher(
			BaseDcLotteryDrawFetcher dcLotteryDrawFetcher) {
		this.dcLotteryDrawFetcher = dcLotteryDrawFetcher;
	}

	public List<String> getMatchPriorityList() {
		return matchPriorityList;
	}

	public void setMatchPriorityList(List<String> matchPriorityList) {
		this.matchPriorityList = matchPriorityList;
	}
	
}
