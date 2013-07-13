package web.service.impl.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.lottery.Fb46MatchService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.ApiResponseBatchUpdateParser;
import com.lehecai.core.api.SimpleApiBatchUpdateItem;
import com.lehecai.core.api.SimpleApiRequestBatchUpdate;
import com.lehecai.core.api.lottery.Fb46Match;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.football.FootballAverageSPItem;
import com.lehecai.core.lottery.fetcher.football.FootballScheduleItem;
import com.lehecai.core.lottery.fetcher.football.impl.BaseFootballAverageSPFetcher;
import com.lehecai.core.lottery.fetcher.football.impl.BaseFootballScheduleFetcher;
import com.lehecai.core.lottery.fetcher.football.impl.FootballAverageSPFetcher10;
import com.lehecai.core.lottery.fetcher.football.impl.FootballAverageSPFetcher9;
import com.lehecai.core.lottery.fetcher.football.impl.FootballScheduleFetcher10;
import com.lehecai.core.lottery.fetcher.football.impl.FootballScheduleFetcher9;

public class Fb46MatchServiceImpl implements Fb46MatchService {
	private final Logger logger = LoggerFactory.getLogger(Fb46MatchServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private BaseFootballScheduleFetcher footballScheduleFetcher9;
	private BaseFootballAverageSPFetcher footballAverageSPFetcher9;
	private BaseFootballScheduleFetcher footballScheduleFetcher10;
	private BaseFootballAverageSPFetcher footballAverageSPFetcher10;
	
	@Override
	public List<Fb46Match> getFb46MatchListByPhase(String phase, PhaseType type) {
		logger.info("进入调用API根据彩期获取46场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_FB46_LIST);
		request.setParameter(Fb46Match.QUERY_PHASE, phase);
		request.setParameter(Fb46Match.QUERY_TYPE, String.valueOf(type.getValue()));
		ApiResponse response;
		List<Fb46Match> list = null;
		
		logger.info("根据彩期获取46场对阵球队信息列表 {}",request.toQueryString());
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API根据彩期查询46场对阵信息异常!{}", e.getMessage());
			return null;
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = Fb46Match.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回46场数据错误!");
		}
		return list;
	}
	
	@Override
	public ResultBean batchCreatePhase(List<Fb46Match> fb46Matchs) {
		ResultBean resultBean = new ResultBean();
		
		Map<Integer, Fb46Match> dbFb46MatchMap = null;
		dbFb46MatchMap = packFb46Match(fb46Matchs, dbFb46MatchMap);
		
		//因为目前最多6场，不用按Global.BATCH_DEAL_NUM分页
		List<String> successList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		Map<String, String> insertIdMap = new HashMap<String, String>();
		if (fb46Matchs == null || fb46Matchs.size() == 0) {
			resultBean.setResult(false);
			resultBean.setMessage("批量生成46场数据为空!");
			return resultBean;
		}
		
		List<Fb46Match> createFb46MatchList = new ArrayList<Fb46Match>();
		List<Fb46Match> updateFb46MatchList = new ArrayList<Fb46Match>();
		for (Fb46Match fb46Match : fb46Matchs) {
			if (dbFb46MatchMap == null || dbFb46MatchMap.get(fb46Match.getMatchNum()) == null) {
				createFb46MatchList.add(fb46Match);
			} else {
				updateFb46MatchList.add(fb46Match);
			}
		}
		
		if (createFb46MatchList != null && createFb46MatchList.size() > 0) {
			saveBatchFb46Match(createFb46MatchList, successList, failureList, insertIdMap);
		}
		if (updateFb46MatchList != null && updateFb46MatchList.size() > 0) {
			updateBatchFb46Match(updateFb46MatchList, successList, failureList, dbFb46MatchMap);
		}
		if (successList.size() == fb46Matchs.size()) {
			resultBean.setResult(true);
			resultBean.setMessage("批量操作46场对阵成功!");
		} else {
			StringBuffer sb = new StringBuffer();
			
			//更新时key为id
			for (int i=0; i<failureList.size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
				String id = failureList.get(i);
				if (dbFb46MatchMap != null) {
					Fb46Match fb46 = dbFb46MatchMap.get(id);
					if (fb46 != null) {
						sb.append(fb46.getMatchNum());
					} else {
						sb.append(id);
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
	public boolean saveBatchFb46Match(List<Fb46Match> fb46MatchList, List<String> successList, List<String> failureList, Map<String,String> insertIdMap) {
		logger.info("进入批量调用API添加46场对阵信息");

		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_FB46_MULTIPLE_CREATE);
		if (fb46MatchList != null) {
			for (int i=0; i<fb46MatchList.size(); i++) {
				Fb46Match fb46Match = fb46MatchList.get(i);
				try {
					SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
					//新增时以场次编号作为id
					simpleApiBatchUpdateItem.setKey(fb46Match.getMatchNum()+"");
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_PHASE, fb46Match.getPhase());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_MATCH_NUM, String.valueOf(fb46Match.getMatchNum()));
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_MATCH_ID, String.valueOf(fb46Match.getMatchId()));
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_MATCH_DATE, DateUtil.formatDate(fb46Match.getMatchDate(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_TYPE, String.valueOf(fb46Match.getType().getValue()));
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_HOME_TEAM, fb46Match.getHomeTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_GUEST_TEAM, fb46Match.getGuestTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_MATCH, fb46Match.getMatch());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_FINAL_SCORE, fb46Match.getFinalScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_HALF_SCORE, fb46Match.getHalfScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_AVERAGE_INDEX, fb46Match.getAverageIndex());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_EXT, fb46Match.getExt());
					request.add(simpleApiBatchUpdateItem);
					
				} catch (Exception e) {
					logger.error("第{}场对阵信息解析失败！",fb46Match.getMatchNum());
					return false;
				}
			}
		}
		
		logger.info("批量更新46场对阵,api request String: {}", request.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API批量新增46场对阵数据异常!{}", e.getMessage());
			return false;
		}
		
		if (response == null) {
			logger.error("API批量新增46场对阵数据失败");
			return false;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API批量新增46场对阵数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return ApiResponseBatchUpdateParser.processResult(response, successList, failureList, insertIdMap);
	}
	
	
	@Override
	public boolean updateBatchFb46Match(List<Fb46Match> fb46MatchList, List<String> successList, List<String> failureList, Map<Integer, Fb46Match> fb46MatchMap) {
		logger.info("进入调用API更新46场对阵信息");

		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_FB46_MULTIPLE_UPDATE);
		if(fb46MatchList != null){
			for(Fb46Match fb46Match : fb46MatchList){
				Fb46Match dbFb46Match = fb46MatchMap.get(fb46Match.getMatchNum());
				try {
					
					dbFb46Match.setMatchId(fb46Match.getMatchId());
					dbFb46Match.setMatchDate(fb46Match.getMatchDate());
					dbFb46Match.setHomeTeam(fb46Match.getHomeTeam());
					dbFb46Match.setGuestTeam(fb46Match.getGuestTeam());
					dbFb46Match.setMatch(fb46Match.getMatch());
					dbFb46Match.setFinalScore(fb46Match.getFinalScore());
					dbFb46Match.setHalfScore(fb46Match.getHalfScore());
					dbFb46Match.setAverageIndex(fb46Match.getAverageIndex());
					dbFb46Match.setExt(fb46Match.getExt());
					
					SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
					simpleApiBatchUpdateItem.setKey(dbFb46Match.getId());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_MATCH_ID, String.valueOf(dbFb46Match.getMatchId()));
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_MATCH_DATE, DateUtil.formatDate(dbFb46Match.getMatchDate(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_HOME_TEAM, dbFb46Match.getHomeTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_GUEST_TEAM, dbFb46Match.getGuestTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_MATCH, dbFb46Match.getMatch());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_FINAL_SCORE, dbFb46Match.getFinalScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_HALF_SCORE, dbFb46Match.getHalfScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_AVERAGE_INDEX, dbFb46Match.getAverageIndex());
					simpleApiBatchUpdateItem.setParameterForUpdate(Fb46Match.SET_EXT, dbFb46Match.getExt());
					request.add(simpleApiBatchUpdateItem);
					
				} catch (Exception e) {
					logger.error("第{}场对阵信息解析失败！",fb46Match.getMatchNum());
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

	private Map<Integer, Fb46Match> packFb46Match(
			List<Fb46Match> fb46Matchs,
			Map<Integer, Fb46Match> dbFb46MatchMap) {
		List<Fb46Match> dbFb46Matchs = getFb46MatchListByPhase(fb46Matchs.get(0).getPhase(), fb46Matchs.get(0).getType());
		if (dbFb46Matchs != null && !dbFb46Matchs.isEmpty()) {
			dbFb46MatchMap = new HashMap<Integer, Fb46Match>();
			for (Fb46Match dbFb46Match : dbFb46Matchs) {
				dbFb46MatchMap.put(dbFb46Match.getMatchNum(), dbFb46Match);
			}
		}
		return dbFb46MatchMap;
	}
	
	//改 2011-4-8 修改增加赛程抓到sp抓不到的情况
	@Override
	public List<Fb46Match> fetchFb46MatchListByPhase(String phase, PhaseType type, FetcherType fetcherType) {
		List<FootballScheduleItem> footballScheduleItems = null;
		List<FootballAverageSPItem> footballScheduleSpItems = null;
		try {
			if (type.getValue() == LotteryType.JQC.getValue()) {
				footballScheduleItems = footballScheduleFetcher9.fetch(phase, fetcherType);
			} else if (type.getValue() == LotteryType.BQC.getValue()) {
				footballScheduleItems = footballScheduleFetcher10.fetch(phase, fetcherType);
			}
		} catch (Exception e) {
			logger.error("抓取46场对阵失败！", e.getMessage());
			return null;
		}
		try {
			if (type.getValue() == LotteryType.JQC.getValue()) {
				footballScheduleSpItems = footballAverageSPFetcher9.fetch(phase, fetcherType);
			} else if (type.getValue() == LotteryType.BQC.getValue()) {
				footballScheduleSpItems = footballAverageSPFetcher10.fetch(phase, fetcherType);
			}
		} catch (Exception e) {
			logger.error("抓取46场sp失败！", e.getMessage());
		}
		if (footballScheduleItems == null || footballScheduleItems.isEmpty()) {
			return null;
		}
		
		List<Fb46Match> fb46Matchs = new ArrayList<Fb46Match>();
		Map<Integer, Fb46Match> fb46MatchMap = new HashMap<Integer, Fb46Match>();
		for (FootballScheduleItem scheduleItem : footballScheduleItems) {
			Fb46Match fb46Match = new Fb46Match();
			fb46Match.setType(type);
			fb46Match.setPhase(scheduleItem.getPhase());
			fb46Match.setMatchNum(scheduleItem.getMatchIndex());
			fb46Match.setMatch(scheduleItem.getLeague());
			fb46Match.setMatchDate(scheduleItem.getMatchTime());
			fb46Match.setHomeTeam(scheduleItem.getHomeTeam());
			fb46Match.setGuestTeam(scheduleItem.getAwayTeam());
			fb46MatchMap.put(scheduleItem.getMatchIndex(), fb46Match);
		}
		
		//sp抓取不为空，设置sp值
		if (footballScheduleSpItems != null && !footballScheduleSpItems.isEmpty()) {
			for (FootballAverageSPItem spItem : footballScheduleSpItems) {
				Fb46Match fb46Match = fb46MatchMap.get((spItem.getMatchIndex()+1)/2);
				if (fb46Match != null) {
					StringBuffer sb = new StringBuffer("");
					if (type.getValue() == LotteryType.JQC.getValue()) {
						sb.append(spItem.getAverageSP_S()).append("^").append(spItem.getAverageSP_P()).append("^").append(spItem.getAverageSP_F());
					} else if (type.getValue() == LotteryType.BQC.getValue()) {
						sb
						.append(spItem.getAverageSP_BC_S()).append("^").append(spItem.getAverageSP_BC_P()).append("^").append(spItem.getAverageSP_BC_F())
						.append("|")
						.append(spItem.getAverageSP_S()).append("^").append(spItem.getAverageSP_P()).append("^").append(spItem.getAverageSP_F());
					}
					fb46Match.setAverageIndex(sb.toString());
				}
			}
		} else if (fb46MatchMap != null && (fb46MatchMap.size() == 4 || fb46MatchMap.size() == 6)) {
			for (int i = 1; i <= fb46MatchMap.size(); i++) {
				Fb46Match fb46Match = fb46MatchMap.get(i);
				StringBuffer sb = new StringBuffer("");
				if (type.getValue() == LotteryType.JQC.getValue()) {
					sb.append("--").append("^").append("--").append("^").append("--");
				} else if (type.getValue() == LotteryType.BQC.getValue()) {
					sb
					.append("--").append("^").append("--").append("^").append("--")
					.append("|")
					.append("--").append("^").append("--").append("^").append("--");
				}
				fb46Match.setAverageIndex(sb.toString());
			}
			
		}
		
		for (int i = 1; i<=fb46MatchMap.size(); i++) {
			fb46Matchs.add(fb46MatchMap.get(i));
		}
		
		return fb46Matchs;
	}
	
	@Override
	public boolean compareFb46Match(Fb46Match pageFb46Match,
			Fb46Match dbFb46Match) {
		if (dbFb46Match == null) {
			return saveFb46Match(pageFb46Match);
		} else {
			dbFb46Match.setMatchId(pageFb46Match.getMatchId());
			dbFb46Match.setMatchDate(pageFb46Match.getMatchDate());
			dbFb46Match.setHomeTeam(pageFb46Match.getHomeTeam());
			dbFb46Match.setGuestTeam(pageFb46Match.getGuestTeam());
			dbFb46Match.setMatch(pageFb46Match.getMatch());
			dbFb46Match.setFinalScore(pageFb46Match.getFinalScore());
			dbFb46Match.setHalfScore(pageFb46Match.getHalfScore());
			dbFb46Match.setAverageIndex(pageFb46Match.getAverageIndex());
			dbFb46Match.setExt(pageFb46Match.getExt());
			return updateFb46Match(dbFb46Match);
		}
	}
	
	@Override
	public boolean saveFb46Match(Fb46Match fb46Match) {
		logger.info("进入调用API添加46场对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_FB46_CREATE);
			createRequest.setParameterForUpdate(Fb46Match.SET_PHASE, fb46Match.getPhase());
			createRequest.setParameterForUpdate(Fb46Match.SET_MATCH_NUM, String.valueOf(fb46Match.getMatchNum()));
			createRequest.setParameterForUpdate(Fb46Match.SET_MATCH_ID, String.valueOf(fb46Match.getMatchId()));
			createRequest.setParameterForUpdate(Fb46Match.SET_MATCH_DATE, DateUtil.formatDate(fb46Match.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(Fb46Match.SET_TYPE, String.valueOf(fb46Match.getType().getValue()));
			createRequest.setParameterForUpdate(Fb46Match.SET_HOME_TEAM, fb46Match.getHomeTeam());
			createRequest.setParameterForUpdate(Fb46Match.SET_GUEST_TEAM, fb46Match.getGuestTeam());
			createRequest.setParameterForUpdate(Fb46Match.SET_MATCH, fb46Match.getMatch());
			createRequest.setParameterForUpdate(Fb46Match.SET_FINAL_SCORE, fb46Match.getFinalScore());
			createRequest.setParameterForUpdate(Fb46Match.SET_HALF_SCORE, fb46Match.getHalfScore());
			createRequest.setParameterForUpdate(Fb46Match.SET_AVERAGE_INDEX, fb46Match.getAverageIndex());
			createRequest.setParameterForUpdate(Fb46Match.SET_EXT, fb46Match.getExt());
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",fb46Match.getMatchNum());
			return false;
		}
		
		logger.info("生成46场对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API添加46场对阵信息异常!{}", e.getMessage());
			return false;
		}
		logger.info("生成46场对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if (response.getCode()!=ApiConstant.RC_SUCCESS || response.getTotal() == 0) {
			logger.error("第{}场对阵信息存储失败!原因：{}", new Object[]{fb46Match.getMatchNum(), response.getMessage()});
			return false;
		}
		logger.error("第{}场对阵信息存储成功!", fb46Match.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateFb46Match(Fb46Match fb46Match) {
		logger.info("进入调用API更新46场对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_FB46_UPDATE);
			createRequest.setParameterForUpdate(Fb46Match.SET_MATCH_ID, String.valueOf(fb46Match.getMatchId()));
			createRequest.setParameterForUpdate(Fb46Match.SET_MATCH_DATE, DateUtil.formatDate(fb46Match.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(Fb46Match.SET_HOME_TEAM, fb46Match.getHomeTeam());
			createRequest.setParameterForUpdate(Fb46Match.SET_GUEST_TEAM, fb46Match.getGuestTeam());
			createRequest.setParameterForUpdate(Fb46Match.SET_MATCH, fb46Match.getMatch());
			createRequest.setParameterForUpdate(Fb46Match.SET_FINAL_SCORE, fb46Match.getFinalScore());
			createRequest.setParameterForUpdate(Fb46Match.SET_HALF_SCORE, fb46Match.getHalfScore());
			createRequest.setParameterForUpdate(Fb46Match.SET_AVERAGE_INDEX, fb46Match.getAverageIndex());
			createRequest.setParameterForUpdate(Fb46Match.SET_EXT, fb46Match.getExt());
			createRequest.setParameter(Fb46Match.SET_ID, fb46Match.getId());
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",fb46Match.getMatchNum());
			return false;
		}
		
		logger.info("更新46场对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改46场对阵信息异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新46场对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if (response.getCode()!=ApiConstant.RC_SUCCESS) {
			logger.error("第{}场对阵信息存储失败!原因：{}", new Object[]{fb46Match.getMatchNum(), response.getMessage()});
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", fb46Match.getMatchNum());
		return true;
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

	public void setFootballScheduleFetcher9(
			FootballScheduleFetcher9 footballScheduleFetcher9) {
		this.footballScheduleFetcher9 = footballScheduleFetcher9;
	}

	public void setFootballAverageSPFetcher9(
			FootballAverageSPFetcher9 footballAverageSPFetcher9) {
		this.footballAverageSPFetcher9 = footballAverageSPFetcher9;
	}

	public void setFootballScheduleFetcher10(
			FootballScheduleFetcher10 footballScheduleFetcher10) {
		this.footballScheduleFetcher10 = footballScheduleFetcher10;
	}

	public void setFootballAverageSPFetcher10(
			FootballAverageSPFetcher10 footballAverageSPFetcher10) {
		this.footballAverageSPFetcher10 = footballAverageSPFetcher10;
	}

}
