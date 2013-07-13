package web.service.impl.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.lottery.SfpArrangeService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.ApiResponseBatchUpdateParser;
import com.lehecai.core.api.SimpleApiBatchUpdateItem;
import com.lehecai.core.api.SimpleApiRequestBatchUpdate;
import com.lehecai.core.api.lottery.SfpArrange;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.UnsupportedFetcherTypeException;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.football.FootballAverageSPItem;
import com.lehecai.core.lottery.fetcher.football.FootballScheduleItem;
import com.lehecai.core.lottery.fetcher.football.impl.BaseFootballAverageSPFetcher;
import com.lehecai.core.lottery.fetcher.football.impl.BaseFootballScheduleFetcher;
import com.lehecai.core.lottery.fetcher.football.impl.FootballAverageSPFetcher7;
import com.lehecai.core.lottery.fetcher.football.impl.FootballScheduleFetcher7;

public class SfpArrangeServiceImpl implements SfpArrangeService {
	private final Logger logger = LoggerFactory.getLogger(SfpArrangeServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private BaseFootballScheduleFetcher footballScheduleFetcher7;
	private BaseFootballAverageSPFetcher footballAverageSPFetcher7;
	
	@Override
	public List<SfpArrange> getSfpArrangeListByPhase(String phase) {
		logger.info("进入调用API根据彩期查询14场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFP_LIST);
		request.setParameter(SfpArrange.QUERY_PHASE, phase);
		ApiResponse response;
		List<SfpArrange> list = null;
		
		logger.info("根据彩期获取14场对阵球队信息列表",request.toQueryString());
		try {
			response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.info("API根据彩期查询胜负彩对阵信息异常!{}", e.getMessage());
			return null;
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = SfpArrange.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回14场数据错误!");
		}
		return list;
	}

	@Override
	public ResultBean batchCreatePhase(List<SfpArrange> sfpArranges) {
		ResultBean resultBean = new ResultBean();
		Map<Integer, SfpArrange> dbSfpArrangeMap = null;
		
		dbSfpArrangeMap = packSfpArrange(sfpArranges, dbSfpArrangeMap);
		//目前最多14场，不用按Global.BATCH_DEAL_NUM分页
		List<String> successList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		Map<String, String> insertIdMap = new HashMap<String, String>();
		if (sfpArranges == null || sfpArranges.size() == 0) {
			resultBean.setResult(false);
			resultBean.setMessage("批量生成14场数据为空!");
			return resultBean;
		}
		List<SfpArrange> createSfpArrangeList = new ArrayList<SfpArrange>();
		List<SfpArrange> updateSfpArrangeList = new ArrayList<SfpArrange>();
		for (SfpArrange sfpArrange : sfpArranges) {
			if (dbSfpArrangeMap == null || dbSfpArrangeMap.get(sfpArrange.getMatchNum()) == null) {
				createSfpArrangeList.add(sfpArrange);
			} else {
				updateSfpArrangeList.add(sfpArrange);
			}
		}
		if (createSfpArrangeList != null && createSfpArrangeList.size() > 0) {
			saveBatchSfpArrange(createSfpArrangeList, successList, failureList, insertIdMap);
		}
		if (updateSfpArrangeList != null && updateSfpArrangeList.size() > 0) {
			updateBatchSfpArrange(updateSfpArrangeList, successList, failureList, dbSfpArrangeMap);
		}
		if (successList.size() == sfpArranges.size()) {
			resultBean.setResult(true);
			resultBean.setMessage("批量生成14场对阵成功!");
		} else {
			StringBuffer sb = new StringBuffer();
			//更新时key为id
			for (int i=0; i<failureList.size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
				String id = failureList.get(i);
				
				if (dbSfpArrangeMap != null) {
					SfpArrange sfp = dbSfpArrangeMap.get(id);
					if (sfp != null) {
						sb.append(sfp.getMatchNum());
					} else {
						sb.append(failureList.get(i));
					}
				} else {
					sb.append(failureList.get(i));
				}
			}
			
			resultBean.setResult(false);
			resultBean.setMessage("场次：[" + sb.toString() + "]对阵更新失败!");
		}
		
		return resultBean;
	}
	
	@Override
	public boolean saveBatchSfpArrange(List<SfpArrange> sfpArrangeList, List<String> successList, List<String> failureList, Map<String,String> insertIdMap) {
		logger.info("进入调用API批量新增北京单场对阵");

		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFP_MULTIPLE_CREATE);
		if (sfpArrangeList != null) {
			for (int i=0; i<sfpArrangeList.size(); i++) {
				SfpArrange sfpArrange = sfpArrangeList.get(i);
				try {
					
					SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
					//新增时以场次编号作为id
					simpleApiBatchUpdateItem.setKey(sfpArrange.getMatchNum()+"");
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_PHASE, sfpArrange.getPhase());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_MATCH_NUM, String.valueOf(sfpArrange.getMatchNum()));
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_MATCH_ID, String.valueOf(sfpArrange.getMatchId()));
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_MATCH_DATE, sfpArrange.getMatchDate() == null ? null : DateUtil.formatDate(sfpArrange.getMatchDate(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_HOME_TEAM, sfpArrange.getHomeTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_GUEST_TEAM, sfpArrange.getGuestTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_MATCH, sfpArrange.getMatch());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_HALF_SCORE, sfpArrange.getHalfScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_FINAL_SCORE, sfpArrange.getFinalScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_AVERAGE_INDEX, sfpArrange.getAverageIndex());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_EXT, sfpArrange.getExt());
					request.add(simpleApiBatchUpdateItem);
					
				} catch (Exception e) {
					logger.error("第{}场对阵信息解析失败！e={}",sfpArrange.getMatchNum(),e.getMessage());
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
		return ApiResponseBatchUpdateParser.processResult(response, successList, failureList, insertIdMap);
	}
	
	@Override
	public boolean updateBatchSfpArrange(List<SfpArrange> sfpArrangeList, List<String> successList, List<String> failureList, Map<Integer, SfpArrange> sfpArrangeMap) {
		logger.info("进入调用API批量更新北京单场对阵");

		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFP_MULTIPLE_UPDATE);
		if(sfpArrangeList != null){
			for(SfpArrange sfpArrange : sfpArrangeList){
				SfpArrange dbSfpArrange = sfpArrangeMap.get(sfpArrange.getMatchNum());
				try {
					dbSfpArrange.setMatchId(sfpArrange.getMatchId());
					dbSfpArrange.setMatchDate(sfpArrange.getMatchDate());
					dbSfpArrange.setHomeTeam(sfpArrange.getHomeTeam());
					dbSfpArrange.setGuestTeam(sfpArrange.getGuestTeam());
					dbSfpArrange.setMatch(sfpArrange.getMatch());
					dbSfpArrange.setHalfScore(sfpArrange.getHalfScore());
					dbSfpArrange.setFinalScore(sfpArrange.getFinalScore());
					dbSfpArrange.setAverageIndex(sfpArrange.getAverageIndex());
					dbSfpArrange.setExt(sfpArrange.getExt());
					
					SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
					simpleApiBatchUpdateItem.setKey(dbSfpArrange.getId());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_MATCH_ID, String.valueOf(dbSfpArrange.getMatchId()));
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_MATCH_DATE, dbSfpArrange.getMatchDate() == null ? null : DateUtil.formatDate(dbSfpArrange.getMatchDate(),DateUtil.DATETIME));
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_HOME_TEAM, dbSfpArrange.getHomeTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_GUEST_TEAM, dbSfpArrange.getGuestTeam());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_MATCH, dbSfpArrange.getMatch());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_HALF_SCORE, dbSfpArrange.getHalfScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_FINAL_SCORE, dbSfpArrange.getFinalScore());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_AVERAGE_INDEX, dbSfpArrange.getAverageIndex());
					simpleApiBatchUpdateItem.setParameterForUpdate(SfpArrange.SET_EXT, dbSfpArrange.getExt());
					request.add(simpleApiBatchUpdateItem);
					
				} catch (Exception e) {
					logger.error("第{}场对阵信息解析失败！e={}", dbSfpArrange.getMatchNum(), e.getStackTrace());
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

	private Map<Integer, SfpArrange> packSfpArrange(
			List<SfpArrange> sfpArranges,
			Map<Integer, SfpArrange> dbSfpArrangeMap) {
		List<SfpArrange> dbSfpArranges = getSfpArrangeListByPhase(sfpArranges.get(0).getPhase());
		if (dbSfpArranges != null && !dbSfpArranges.isEmpty()) {
			dbSfpArrangeMap = new HashMap<Integer, SfpArrange>();
			for (SfpArrange dbSfpArrange : dbSfpArranges) {
				dbSfpArrangeMap.put(dbSfpArrange.getMatchNum(), dbSfpArrange);
			}
		}
		return dbSfpArrangeMap;
	}
	
	//改 2011-4-8 修改增加赛程抓到sp抓不到的情况
	@Override
	public List<SfpArrange> fetchSfpArrangeListByPhase(String phase, FetcherType fetcherType) {
		List<FootballScheduleItem> footballScheduleItems = null;
		List<FootballAverageSPItem> footballScheduleSpItems = null;
		try {
			footballScheduleItems = footballScheduleFetcher7.fetch(phase, fetcherType);
		} catch (Exception e) {
			logger.error("抓取14场对阵赛程失败！");
			return null;
		}
		
		if (footballScheduleItems == null || footballScheduleItems.isEmpty() || footballScheduleItems.size() != 14) {
			return null;
		}
		
		List<SfpArrange> sfpArranges = new ArrayList<SfpArrange>();
		Map<Integer, SfpArrange> sfpArrangeMap = new HashMap<Integer, SfpArrange>();
		for (FootballScheduleItem scheduleItem : footballScheduleItems) {
			SfpArrange sfpArrange = new SfpArrange();
			sfpArrange.setPhase(scheduleItem.getPhase());
			sfpArrange.setMatchNum(scheduleItem.getMatchIndex());
			sfpArrange.setMatch(scheduleItem.getLeague());
			sfpArrange.setMatchDate(scheduleItem.getMatchTime());
			sfpArrange.setHomeTeam(scheduleItem.getHomeTeam());
			sfpArrange.setGuestTeam(scheduleItem.getAwayTeam());
			sfpArrangeMap.put(scheduleItem.getMatchIndex(), sfpArrange);
		}
		
		try {
			footballScheduleSpItems = footballAverageSPFetcher7.fetch(phase, fetcherType);
		} catch (UnsupportedFetcherTypeException e) {
			logger.error("抓取14场对阵欧赔失败！");	
		}
		
		for (int i = 1; i<=sfpArrangeMap.size(); i++) {
			sfpArranges.add(sfpArrangeMap.get(i));
		}
		
		if (footballScheduleSpItems != null && !footballScheduleSpItems.isEmpty()) {
			for (FootballAverageSPItem spItem : footballScheduleSpItems) {
				SfpArrange sfpArrange = sfpArrangeMap.get(spItem.getMatchIndex());
				if (sfpArrange != null) {
					StringBuffer sb = new StringBuffer(spItem.getAverageSP_S());
					sb.append("^").append(spItem.getAverageSP_P()).append("^").append(spItem.getAverageSP_F());
					sfpArrange.setAverageIndex(sb.toString());
				}
			}
		} else if (sfpArrangeMap != null && sfpArrangeMap.size() == 14) {
			for (int i = 1; i <= sfpArrangeMap.size(); i++) {
				SfpArrange sfpArrange = sfpArrangeMap.get(i);
				StringBuffer sb = new StringBuffer("--");
				sb.append("^").append("--").append("^").append("--");
				sfpArrange.setAverageIndex(sb.toString());
			}
		}
		
		return sfpArranges;
	}
	
	@Override
	public boolean compareSfpArrange(SfpArrange pageSfpArrange,
			SfpArrange dbSfpArrange) {
		if (dbSfpArrange == null) {
			return saveSfpArrange(pageSfpArrange);
		} else {
			dbSfpArrange.setMatchId(pageSfpArrange.getMatchId());
			dbSfpArrange.setMatchDate(pageSfpArrange.getMatchDate());
			dbSfpArrange.setHomeTeam(pageSfpArrange.getHomeTeam());
			dbSfpArrange.setGuestTeam(pageSfpArrange.getGuestTeam());
			dbSfpArrange.setMatch(pageSfpArrange.getMatch());
			dbSfpArrange.setHalfScore(pageSfpArrange.getHalfScore());
			dbSfpArrange.setFinalScore(pageSfpArrange.getFinalScore());
			dbSfpArrange.setAverageIndex(pageSfpArrange.getAverageIndex());
			dbSfpArrange.setExt(pageSfpArrange.getExt());
			return updateSfpArrange(dbSfpArrange);
		}
	}
	
	@Override
	public boolean saveSfpArrange(SfpArrange sfpArrange) {
		logger.info("进入调用API添加胜负彩对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFP_CREATE);
			createRequest.setParameterForUpdate(SfpArrange.SET_PHASE, sfpArrange.getPhase());
			createRequest.setParameterForUpdate(SfpArrange.SET_MATCH_NUM, String.valueOf(sfpArrange.getMatchNum()));
			createRequest.setParameterForUpdate(SfpArrange.SET_MATCH_ID, String.valueOf(sfpArrange.getMatchId()));
			createRequest.setParameterForUpdate(SfpArrange.SET_MATCH_DATE, DateUtil.formatDate(sfpArrange.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(SfpArrange.SET_HOME_TEAM, sfpArrange.getHomeTeam());
			createRequest.setParameterForUpdate(SfpArrange.SET_GUEST_TEAM, sfpArrange.getGuestTeam());
			createRequest.setParameterForUpdate(SfpArrange.SET_MATCH, sfpArrange.getMatch());
			createRequest.setParameterForUpdate(SfpArrange.SET_HALF_SCORE, sfpArrange.getHalfScore());
			createRequest.setParameterForUpdate(SfpArrange.SET_FINAL_SCORE, sfpArrange.getFinalScore());
			createRequest.setParameterForUpdate(SfpArrange.SET_AVERAGE_INDEX, sfpArrange.getAverageIndex());
			createRequest.setParameterForUpdate(SfpArrange.SET_EXT, sfpArrange.getExt());
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",sfpArrange.getMatchNum());
			return false;
		}
		
		logger.info("生成胜负彩对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API添加胜负彩对阵信息异常!{}", e.getMessage());
			return false;
		}
		logger.info("生成胜负彩对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if (response.getCode()!=ApiConstant.RC_SUCCESS || response.getTotal()==0) {
			logger.error("第{}场对阵信息存储失败!原因：{}", new Object[]{sfpArrange.getMatchNum(), response.getMessage()});
			return false;
		}
		logger.info("第{}场对阵信息存储成功!", sfpArrange.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateSfpArrange(SfpArrange sfpArrange) {
		logger.info("进入调用API更新胜负彩对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFP_UPDATE);
			createRequest.setParameterForUpdate(SfpArrange.SET_MATCH_ID, String.valueOf(sfpArrange.getMatchId()));
			createRequest.setParameterForUpdate(SfpArrange.SET_MATCH_DATE, DateUtil.formatDate(sfpArrange.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(SfpArrange.SET_HOME_TEAM, sfpArrange.getHomeTeam());
			createRequest.setParameterForUpdate(SfpArrange.SET_GUEST_TEAM, sfpArrange.getGuestTeam());
			createRequest.setParameterForUpdate(SfpArrange.SET_MATCH, sfpArrange.getMatch());
			createRequest.setParameterForUpdate(SfpArrange.SET_HALF_SCORE, sfpArrange.getHalfScore());
			createRequest.setParameterForUpdate(SfpArrange.SET_FINAL_SCORE, sfpArrange.getFinalScore());
			createRequest.setParameterForUpdate(SfpArrange.SET_AVERAGE_INDEX, sfpArrange.getAverageIndex());
			createRequest.setParameterForUpdate(SfpArrange.SET_EXT, sfpArrange.getExt());
			createRequest.setParameter(SfpArrange.SET_ID, sfpArrange.getId());
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",sfpArrange.getMatchNum());
			return false;
		}
		
		logger.info("更新胜负彩对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API更新胜负彩对阵信息异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新胜负彩对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if (response.getCode()!=ApiConstant.RC_SUCCESS) {
			logger.error("第{}场对阵信息存储失败!原因：{}", new Object[]{sfpArrange.getMatchNum(), response.getMessage()});
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", sfpArrange.getMatchNum());
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

	public void setFootballScheduleFetcher7(
			FootballScheduleFetcher7 footballScheduleFetcher7) {
		this.footballScheduleFetcher7 = footballScheduleFetcher7;
	}

	public void setFootballAverageSPFetcher7(
			FootballAverageSPFetcher7 footballAverageSPFetcher7) {
		this.footballAverageSPFetcher7 = footballAverageSPFetcher7;
	}
}
