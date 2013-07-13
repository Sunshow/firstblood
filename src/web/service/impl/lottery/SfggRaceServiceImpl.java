package web.service.impl.lottery;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.lottery.SfggRaceService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.ExcelUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.SfggRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.UnsupportedFetcherTypeException;
import com.lehecai.core.lottery.DcRaceStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.dc.SfggLotteryDrawItem;
import com.lehecai.core.lottery.fetcher.dc.SfggScheduleItem;
import com.lehecai.core.lottery.fetcher.dc.impl.BaseSfggLotteryDrawFetcher;
import com.lehecai.core.lottery.fetcher.dc.impl.BaseSfggScheduleFetcher;
import com.lehecai.core.lottery.fetcher.dc.impl.CommonSfggLotteryDrawFetcher;
import com.lehecai.core.lottery.fetcher.dc.impl.CommonSfggScheduleFetcher;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreNumberUtil;

public class SfggRaceServiceImpl implements SfggRaceService {
	private final Logger logger = LoggerFactory.getLogger(SfggRaceServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private List<String> matchPriorityList;
	
	private int dcListMaxSize = 500;
	
	@Override
	public List<SfggRace> getRecommendSfggRace(String phaseNo, int count) {
		List<SfggRace> recommendSfggRaces = null;
		if (count <= 0) {
			return recommendSfggRaces;
		}
		//查询本期单场所有比赛
		List<SfggRace> sfggRaces = getSfggRaceListByPhase(phaseNo);
		if (sfggRaces != null && !sfggRaces.isEmpty()) {
			//过滤推荐count场赛事
			recommendSfggRaces = filterSfggRaces(sfggRaces, count);
		}
		
		return recommendSfggRaces;
	}
	@Override
	public int getSfggRaceSaleCount(String phaseNo) {
		if ("".equals(phaseNo)) {
			return 0;
		}
		//查询本期单场所有比赛
		List<SfggRace> sfggRaces = getSfggRaceListByPhase(phaseNo);
		if (sfggRaces != null && !sfggRaces.isEmpty()) {
			List<SfggRace> canSellSfggRaces = filterCanSell(sfggRaces, DcRaceStatus.CAN_BUY);
			if (canSellSfggRaces != null) {
				return canSellSfggRaces.size();
			}
		}
		return 0;
	}
	//过滤推荐count场赛事
	private List<SfggRace> filterSfggRaces(List<SfggRace> sfggRaces, int count) {
		//过滤状态可售场次
		List<SfggRace> canSellSfggRaces = filterCanSell(sfggRaces, DcRaceStatus.CAN_BUY);
		
		//1、未开赛不足count场，取全部中最后count场
		if (canSellSfggRaces.size() < count) {
			if (sfggRaces != null && sfggRaces.size() > count) {
				canSellSfggRaces = sfggRaces.subList(sfggRaces.size()-count, sfggRaces.size());
			}
		} else {//2、未开赛够count场，按优先级排序
			canSellSfggRaces = orderPriority(canSellSfggRaces);
			canSellSfggRaces = canSellSfggRaces.subList(0, count);
		}
		
		return canSellSfggRaces;
	}

	//按优先级排序
	private List<SfggRace> orderPriority(List<SfggRace> canSellSfggRaces) {
		if (canSellSfggRaces == null || canSellSfggRaces.isEmpty()) {
			return null;
		}
		for (int i = 0; i < canSellSfggRaces.size(); i++) {
			for (int j = i; j < canSellSfggRaces.size(); j++) {
				//优先级高排在前面，否则场次靠前排前面
				if (canSellSfggRaces.get(i).getPriority() < canSellSfggRaces.get(j).getPriority()) {
					SfggRace tempSfggRace = canSellSfggRaces.get(i);
					canSellSfggRaces.set(i, canSellSfggRaces.get(j));
					canSellSfggRaces.set(j, tempSfggRace);
				} else if (canSellSfggRaces.get(i).getMatchNum() > canSellSfggRaces.get(j).getMatchNum()) {
					SfggRace tempSfggRace = canSellSfggRaces.get(i);
					canSellSfggRaces.set(i, canSellSfggRaces.get(j));
					canSellSfggRaces.set(j, tempSfggRace);
				}
			}
		}
		return canSellSfggRaces;
	}

	//过滤
	private List<SfggRace> filterCanSell(List<SfggRace> sfggRaces, DcRaceStatus status) {
		if (sfggRaces == null || sfggRaces.isEmpty()) {
			return null;
		}
		List<SfggRace> canSellSfggRaces = new ArrayList<SfggRace>();
		Date nowDate = new Date();
		for (SfggRace sfggRace : sfggRaces) {
			try {
				if (sfggRace.getStatus().getValue() == status.getValue() && nowDate.before(sfggRace.getMatchDate())){
					canSellSfggRaces.add(sfggRace);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return canSellSfggRaces;
	}

	@Override
	public ResultBean batchCreatePhase(List<SfggRace> sfggRaces) {
		ResultBean resultBean = new ResultBean();
		StringBuffer faildTeam = null;
		
		Map<Integer, SfggRace> dbSfggRaceMap = null;
		dbSfggRaceMap = packSfggRace(sfggRaces, dbSfggRaceMap);
		
		for (SfggRace sfggRace : sfggRaces) {
			if (dbSfggRaceMap == null || dbSfggRaceMap.isEmpty()) {
				if (!compareSfggRace(sfggRace, null)) {
					if (faildTeam == null) {
						faildTeam = new StringBuffer();
					}
					faildTeam.append(sfggRace.getMatchNum()).append(",");
				}
			} else {
				if (!compareSfggRace(sfggRace, dbSfggRaceMap.get(sfggRace.getMatchNum()))) {
					if (faildTeam == null) {
						faildTeam = new StringBuffer();
					}
					faildTeam.append(sfggRace.getMatchNum()).append(",");
				}
			}
		}
		if (faildTeam != null) {
			resultBean.setResult(false);
			resultBean.setMessage("场次：["+faildTeam.substring(0, faildTeam.length()-1)+"]对阵更新失败！");
		} else {
			resultBean.setResult(true);
			resultBean.setMessage("批量生成奥运胜负过关对阵成功！");
		}
		return resultBean;
	}
	
	@Override
	public ResultBean batchUpdateSfggRaceSp(List<SfggRace> sfggRaces) {
		ResultBean resultBean = new ResultBean();
		StringBuffer faildTeam = null;
		
		for (SfggRace sfggRace : sfggRaces) {
			try {
				if (!updateSfggRaceSp(sfggRace)) {
					if (faildTeam == null) {
						faildTeam = new StringBuffer();
					}
					faildTeam.append(sfggRace.getMatchNum()).append(",");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API调用更新单场结果Sp值异常！{}", e.getMessage());
				if (faildTeam == null) {
					faildTeam = new StringBuffer();
				}
				faildTeam.append(sfggRace.getMatchNum()).append(",");
			}
		}
		
		if (faildTeam != null) {
			resultBean.setResult(false);
			resultBean.setMessage("场次：["+faildTeam.substring(0, faildTeam.length()-1)+"]结果SP值更新失败！");
		} else {
			resultBean.setResult(true);
			resultBean.setMessage("批量更新奥运胜负过关结果SP值成功！");
		}
		return resultBean;
	}

	@Override
	public boolean updateSfggRaceSp(SfggRace sfggRace) throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新奥运胜负过关SP信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_UPDATE);
			createRequest.setParameterForUpdate(SfggRace.SET_FINAL_SCORE, sfggRace.getFinalScore());
			createRequest.setParameterForUpdate(SfggRace.SET_SP_SF, sfggRace.getSpSf());
			createRequest.setParameter(SfggRace.SET_ID, sfggRace.getId());
		} catch (Exception e) {
			logger.error("更新第{}场SP信息解析失败!",sfggRace.getMatchNum());
			return false;
		}
		
		logger.info("更新奥运胜负过关SP信息,api request String: {}", createRequest.toQueryString());
		ApiResponse response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if(response == null){
			logger.error("API响应结果为空");
			return false;
		}
		logger.info("更新奥运胜负过关SP信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场SP信息更新失败!", sfggRace.getMatchNum());
			return false;
		}
		logger.info("第{}场SP信息更新成功!", sfggRace.getMatchNum());
		return true;
	}
	
	private Map<Integer, SfggRace> packSfggRace(
			List<SfggRace> sfggRaces,
			Map<Integer, SfggRace> dbSfggRaceMap) {
		if (sfggRaces == null || sfggRaces.isEmpty()) {
			return null;
		}
		List<SfggRace> dbSfggRaces = getSfggRaceListByPhase(sfggRaces.get(0).getPhase());
		if (dbSfggRaces != null && !dbSfggRaces.isEmpty()) {
			dbSfggRaceMap = new HashMap<Integer, SfggRace>();
			for (SfggRace dbSfggRace : dbSfggRaces) {
				dbSfggRaceMap.put(dbSfggRace.getMatchNum(), dbSfggRace);
			}
		}
		return dbSfggRaceMap;
	}

	@Override
	public boolean compareSfggRace(SfggRace pageSfggRace, SfggRace dbSfggRace) {
		if (dbSfggRace == null) {
			return saveSfggRace(pageSfggRace);
		} else {
			dbSfggRace.setMatchDate(pageSfggRace.getMatchDate());
			dbSfggRace.setHomeTeam(pageSfggRace.getHomeTeam());
			dbSfggRace.setAwayTeam(pageSfggRace.getAwayTeam());
			dbSfggRace.setHandicap(pageSfggRace.getHandicap());
			dbSfggRace.setMatchName(pageSfggRace.getMatchName());
			dbSfggRace.setMatchDesc(pageSfggRace.getMatchDesc());
			dbSfggRace.setEndSaleTime(pageSfggRace.getEndSaleTime());
			dbSfggRace.setPriority(pageSfggRace.getPriority());
			dbSfggRace.setExt(pageSfggRace.getExt());
//			dbSfggRace.setStatus(pageSfggRace.getStatus());
//			dbSfggRace.setWholeScore(pageSfggRace.getWholeScore());
//			dbSfggRace.setHalfScore(pageSfggRace.getHalfScore());
//			dbSfggRace.setSpSfp(pageSfggRace.getSpSfp());
//			dbSfggRace.setSpSxds(pageSfggRace.getSpSxds());
//			dbSfggRace.setSpJqs(pageSfggRace.getSpJqs());
//			dbSfggRace.setSpBf(pageSfggRace.getSpBf());
//			dbSfggRace.setSpBcsfp(pageSfggRace.getSpBcsfp());
//			dbSfggRace.setCatchId(pageSfggRace.getCatchId());
			dbSfggRace.setFxId(pageSfggRace.getFxId());
			
			return updateSfggRace(dbSfggRace);
		}
	}
	
	@Override
	public List<SfggRace> fetchSfggRaceSpListByPhase(String phase,
			FetcherType fetcherType) {
		List<SfggLotteryDrawItem> sfggLotteryDrawItems = null;
		
		BaseSfggLotteryDrawFetcher sfggLotteryDrawFetcher = new CommonSfggLotteryDrawFetcher();
		try {
			sfggLotteryDrawItems = sfggLotteryDrawFetcher.fetch(phase, fetcherType);
		} catch (UnsupportedFetcherTypeException e) {
			logger.error("不支持的抓取奥运胜负过关sp和比分的抓取类型:{}", fetcherType.getName());
			return null;
		} catch (Exception e) {
			logger.error("抓取奥运胜负过关sp和比分失败！{}", e.getMessage());
			return null;
		}
		
		if (sfggLotteryDrawItems == null || sfggLotteryDrawItems.isEmpty()) {
			return null;
		}
		
		List<SfggRace> sfggRaces = new ArrayList<SfggRace>();
		
		for (SfggLotteryDrawItem lotteryDrawItem : sfggLotteryDrawItems) {
			SfggRace sfggRace = new SfggRace();
			sfggRace.setPhase(phase);
			sfggRace.setMatchNum(lotteryDrawItem.getMatchIndex());
			sfggRace.setMatchName(lotteryDrawItem.getLeague());
			sfggRace.setHomeTeam(lotteryDrawItem.getHomeTeam());
			sfggRace.setAwayTeam(lotteryDrawItem.getAwayTeam());
			sfggRace.setMatchDesc(lotteryDrawItem.getMatchDesc());
			sfggRace.setFinalScore(lotteryDrawItem.getFullTimeResult());
			sfggRace.setSpSf(lotteryDrawItem.getSpSF());
			sfggRaces.add(sfggRace);
		}
		return sfggRaces;
	}

	@Override
	public List<SfggRace> fetchSfggRaceListByPhase(String phase, FetcherType fetcherType) {
		List<SfggScheduleItem> sfggScheduleItems = null;
		
		BaseSfggScheduleFetcher sfggScheduleFetcher = new CommonSfggScheduleFetcher();
		try {
			sfggScheduleItems = sfggScheduleFetcher.fetch(phase, fetcherType);
		} catch (UnsupportedFetcherTypeException e) {
			logger.error("不支持的抓取奥运胜负过关对阵的抓取类型:{}", fetcherType.getName());
			return null;
		} catch (Exception e) {
			logger.error("抓取奥运胜负过关对阵失败！{}", e.getMessage());
			return null;
		}
		
		if (sfggScheduleItems == null || sfggScheduleItems.isEmpty()) {
			return null;
		}
		
		List<SfggRace> sfggRaces = new ArrayList<SfggRace>();
		
		for (SfggScheduleItem scheduleItem : sfggScheduleItems) {
			try {
				SfggRace sfggRace = new SfggRace();
				sfggRace.setPhase(scheduleItem.getPhase());
				sfggRace.setMatchNum(scheduleItem.getMatchIndex());
				sfggRace.setMatchName(scheduleItem.getLeague());
				sfggRace.setMatchDate(scheduleItem.getMatchTime());
				
				String [] matchDateArray = DateUtil.formatDate(scheduleItem.getMatchTime(), DateUtil.DATETIME).split(" ");
				String endSaleTime = DateUtil.formatDate(scheduleItem.getMatchTime(), DateUtil.DATETIME);
				Calendar cal = Calendar.getInstance();
				cal.setTime(scheduleItem.getMatchTime());
				
				if ((cal.get(Calendar.HOUR_OF_DAY) >= 6 && cal.get(Calendar.HOUR_OF_DAY) <9) || (cal.get(Calendar.HOUR_OF_DAY) == 9 && cal.get(Calendar.MINUTE) <= 30)) {
					endSaleTime = matchDateArray[0] + " 06:00:00";
				}
				sfggRace.setEndSaleTime(DateUtil.parseDate(endSaleTime, DateUtil.DATETIME));
				
				sfggRace.setHomeTeam(scheduleItem.getHomeTeam());
				sfggRace.setAwayTeam(scheduleItem.getAwayTeam());
				sfggRace.setMatchDesc(scheduleItem.getMatchDesc());
				sfggRace.setHandicap(scheduleItem.getHandicap());
				sfggRace.setStatus(DcRaceStatus.NO_BUY);
				
				if (matchPriorityList != null && matchPriorityList.contains(scheduleItem.getLeague())) {
					sfggRace.setPriority(100);
				} else {
					sfggRace.setPriority(0);
				}
				sfggRaces.add(sfggRace);
			} catch (Exception e) {
				logger.error("奥运胜负过关抓取转换失败！{}", e.getMessage());
			}
		}
		
		return sfggRaces;
	}

	@Override
	public List<SfggRace> getSfggRaceListByPhase(String phase) {
		logger.info("进入调用API根据彩期获取奥运胜负过关对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_LIST);
		request.setParameter(SfggRace.QUERY_PHASE, phase);
		//按场次正序
		request.addOrder(SfggRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		ApiResponse response = null;
		List<SfggRace> list = null;
		
		logger.info("根据彩期获取奥运胜负过关对阵球队信息列表",request.toQueryString());
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
			list = SfggRace.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回奥运胜负过关数据错误!");
		}
		return list;
	}

	/**
	 * 查询单场对阵信息
	 * @param phase
	 * @param type
	 * @return
	 */
	@Override
	public SfggRace getSfggRaceByMatchNum(String phase, Long matchNum) throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取奥运胜负过关对阵球队信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_SEARCH);
		request.setParameter(SfggRace.QUERY_PHASE, phase + "");
		request.setParameter(SfggRace.QUERY_MATCH_NUM, matchNum + "");
		
		ApiResponse response = null;
		
		logger.info("获取奥运胜负过关对阵球队信息",request.toQueryString());
		
		response = apiRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("获取奥运胜负过关对阵球队信息失败");
			throw new ApiRemoteCallFailedException("获取奥运胜负过关对阵球队信息失败");
		}
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("获取奥运胜负过关对阵球队信息请求异常");
			throw new ApiRemoteCallFailedException("获取奥运胜负过关对阵球队信息请求异常");
		}
		if (response.getData() == null || response.getData().size() == 0) {
			logger.error("奥运胜负过关对阵球队信息为空");
			return null;
		}
		
		return SfggRace.convertFromJSONObject(response.getData().getJSONObject(0));
	}
	
	@Override
	public boolean saveSfggRace(SfggRace sfggRace) {
		logger.info("进入调用API生成奥运胜负过关对阵");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_CREATE);
			createRequest.setParameterForUpdate(SfggRace.SET_PHASE, sfggRace.getPhase());
			createRequest.setParameterForUpdate(SfggRace.SET_MATCH_NUM, String.valueOf(sfggRace.getMatchNum()));
			createRequest.setParameterForUpdate(SfggRace.SET_MATCH_DATE, DateUtil.formatDate(sfggRace.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(SfggRace.SET_END_SALE_DATE, DateUtil.formatDate(sfggRace.getEndSaleTime(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(SfggRace.SET_HOME_TEAM, sfggRace.getHomeTeam());
			createRequest.setParameterForUpdate(SfggRace.SET_AWAY_TEAM, sfggRace.getAwayTeam());
			createRequest.setParameterForUpdate(SfggRace.SET_HANDICAP, sfggRace.getHandicap());
			createRequest.setParameterForUpdate(SfggRace.SET_MATCH_NAME, sfggRace.getMatchName());
			createRequest.setParameterForUpdate(SfggRace.SET_MATCH_DESC, sfggRace.getMatchDesc());
			createRequest.setParameterForUpdate(SfggRace.SET_STATUS, String.valueOf(sfggRace.getStatus().getValue()));
			createRequest.setParameterForUpdate(SfggRace.SET_PRIORITY, String.valueOf(sfggRace.getPriority()));
			createRequest.setParameterForUpdate(SfggRace.SET_EXT, sfggRace.getExt());
//			createRequest.setParameterForUpdate(SfggRace.SET_WHOLE_SCORE, sfggRace.getWholeScore());
//			createRequest.setParameterForUpdate(SfggRace.SET_HALF_SCORE, sfggRace.getHalfScore());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_SFP, sfggRace.getSpSfp());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_SXDS, sfggRace.getSpSxds());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_JQS, sfggRace.getSpJqs());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_BF, sfggRace.getSpBf());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_BCSFP, sfggRace.getSpBcsfp());
//			createRequest.setParameterForUpdate(SfggRace.SET_CATCH_ID, String.valueOf(sfggRace.getCatchId()));
//			createRequest.setParameterForUpdate(SfggRace.SET_FX_ID, String.valueOf(sfggRace.getFxId()));
			
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!{}",sfggRace.getMatchNum(), e.getMessage());
			return false;
		}
		
		logger.info("生成奥运胜负过关对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API保存单场数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("生成奥运胜负过关对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS || response.getTotal()==0){
			logger.error("第{}场对阵信息存储失败!", sfggRace.getMatchNum());
			return false;
		}
		logger.error("第{}场对阵信息存储成功!", sfggRace.getMatchNum());
		return true;
	}

	@Override
	public boolean updateSfggRace(SfggRace sfggRace) {
		logger.info("进入调用API更新奥运胜负过关对阵");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_UPDATE);
			createRequest.setParameterForUpdate(SfggRace.SET_MATCH_DATE, DateUtil.formatDate(sfggRace.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(SfggRace.SET_END_SALE_DATE, DateUtil.formatDate(sfggRace.getEndSaleTime(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(SfggRace.SET_MATCH_NAME, sfggRace.getMatchName());
			createRequest.setParameterForUpdate(SfggRace.SET_MATCH_NUM, String.valueOf(sfggRace.getMatchNum()));
			createRequest.setParameterForUpdate(SfggRace.SET_HOME_TEAM, sfggRace.getHomeTeam());
			createRequest.setParameterForUpdate(SfggRace.SET_AWAY_TEAM, sfggRace.getAwayTeam());
			createRequest.setParameterForUpdate(SfggRace.SET_MATCH_DESC, sfggRace.getMatchDesc());
			createRequest.setParameterForUpdate(SfggRace.SET_HANDICAP, sfggRace.getHandicap());
			createRequest.setParameterForUpdate(SfggRace.SET_PRIORITY, String.valueOf(sfggRace.getPriority()));
//			createRequest.setParameterForUpdate(SfggRace.SET_EXT, sfggRace.getExt());
//			createRequest.setParameterForUpdate(SfggRace.SET_WHOLE_SCORE, sfggRace.getWholeScore());
//			createRequest.setParameterForUpdate(SfggRace.SET_HALF_SCORE, sfggRace.getHalfScore());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_SFP, sfggRace.getSpSfp());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_SXDS, sfggRace.getSpSxds());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_JQS, sfggRace.getSpJqs());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_BF, sfggRace.getSpBf());
//			createRequest.setParameterForUpdate(SfggRace.SET_SP_BCSFP, sfggRace.getSpBcsfp());
//			createRequest.setParameterForUpdate(SfggRace.SET_CATCH_ID, String.valueOf(sfggRace.getCatchId()));
			createRequest.setParameterForUpdate(SfggRace.SET_FX_ID, String.valueOf(sfggRace.getFxId()));
			createRequest.setParameter(SfggRace.SET_ID, sfggRace.getId());
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败！",sfggRace.getMatchNum());
			return false;
		}
		
		logger.info("更新奥运胜负过关对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改单场数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新奥运胜负过关对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", sfggRace.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", sfggRace.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateStatus(String id, DcRaceStatus status) {
		logger.info("进入调用API更新奥运胜负过关对阵状态");
		ApiRequest createRequest = new ApiRequest();
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_UPDATE);
			createRequest.setParameterForUpdate(SfggRace.SET_STATUS, String.valueOf(status.getValue()));
			createRequest.setParameter(SfggRace.SET_ID, id);
		} catch (Exception e) {
			logger.error("奥运胜负过关对阵信息解析失败！");
			return false;
		}
		
		logger.info("更新奥运胜负过关对阵状态,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改单场状态异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新奥运胜负过关对阵状态,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("奥运胜负过关对阵状态更新失败!");
			return false;
		}
		logger.info("奥运胜负过关对阵状态更新成功!");
		return true;
	}

	@Override
	public List<SfggRace> findSfggRaceByStatus(List<String> sfggStatusList,String phaseNo) {
		logger.info("进入调用API根据状态及彩期获取奥运胜负过关对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_SEARCH);
		request.setParameter(SfggRace.QUERY_PHASE, phaseNo);
		if(sfggStatusList != null && sfggStatusList.size() >0){
			request.setParameterIn(SfggRace.QUERY_STATUS, sfggStatusList);
		}
		request.setPagesize(dcListMaxSize);
		request.addOrder(SfggRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		ApiResponse response = null;
		List<SfggRace> list = null;
		
		logger.info("根据状态及彩期获取奥运胜负过关对阵球队信息列表",request.toQueryString());
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
			list = SfggRace.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回奥运胜负过关数据错误!");
		}
		return list;
	}
	
	/**
	 * 查询比赛日期查询单场对阵
	 */
	public List<SfggRace> findSfggRacesByMatchDate(Date matchDate) {
		logger.info("进入调用API查询单场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_SEARCH);
		request.setParameterBetween(SfggRace.QUERY_MATCH_DATE, CoreDateUtils.formatDate(matchDate, CoreDateUtils.DATETIME), 
				CoreDateUtils.formatDate(matchDate, CoreDateUtils.DATETIME));
		
		ApiResponse response = null;
		List<SfggRace> list = null;
		
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
			list = SfggRace.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回单场数据错误!");
		}
		return list;
	}

	public List<SfggRace> findSfggRacesByMatchDateAndPhase(Date matchDate, String phase, PageBean pageBean) {
		logger.info("进入调用API根据彩期号与比赛开始准确时间查询单场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_SEARCH);
		request.setParameter(SfggRace.QUERY_PHASE, phase);
		request.setParameter(SfggRace.QUERY_MATCH_DATE, CoreDateUtils.formatDate(matchDate, CoreDateUtils.DATETIME));

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		ApiResponse response = null;
		List<SfggRace> list = null;

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
			list = SfggRace.convertFromJSONArray(response.getData());
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
	public List<SfggRace> findSfggRacesBySpecifiedMatchNumAndPhase(List<String> matchNumList, String phase, PageBean pageBean) {
		logger.info("进入调用API根据指定的场次号查询单场对阵球队信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_SEARCH);
		request.setParameter(SfggRace.QUERY_PHASE, phase);
		request.setParameterIn(SfggRace.QUERY_MATCH_NUM, matchNumList);

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		ApiResponse response = null;
		List<SfggRace> list = null;

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
			list = SfggRace.convertFromJSONArray(response.getData());
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

	/**
	 * 彩期位置坐标
	 */
	protected static int[] COORD_PHASE = new int[]{5, 0};
	
	protected static int[] COORD_MATCH = new int[]{8, 0};

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
	public List<Object> getSfggRaceByExcel(File excelFile, final Phase phase, String phaseNo) {
		List<SfggRace> sfggRaces = new ArrayList<SfggRace>();
		
		Workbook workbook = ExcelUtil.createWorkbook(excelFile);
		if (workbook == null) {
			logger.error("上传的excel文件格式不对");
			return null;
		}
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			logger.error("获取Excel表Sheet错误！");
			return null;
		}

		// 彩期区解析开始
		if (!sheet.getRow(COORD_PHASE[0]).getCell(COORD_PHASE[1]).getStringCellValue().replace("期", "").trim().equals(phaseNo)) {
			logger.error("选择彩期与Excel中彩期不一致，请确认");
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();

		phase.setStartSaleTime(generateDatetimeWithoutYear(calendar, sheet.getRow(COORD_PHASE[0]).getCell(COORD_PHASE[1] + 3).getStringCellValue()));
		
		Calendar endSalePhaseCalendar = Calendar.getInstance();
		endSalePhaseCalendar.setTime(generateDatetimeWithoutYear(calendar, sheet.getRow(COORD_PHASE[0]).getCell(COORD_PHASE[1] + 6).getStringCellValue()));
		// 官方提前5分钟停售，这里不带提前量
		endSalePhaseCalendar.add(Calendar.MINUTE, 5);
		phase.setEndSaleTime(endSalePhaseCalendar.getTime());
		phase.setEndTicketTime(phase.getEndSaleTime());
		// 彩期区解析完毕
		
		// 读取赛程
		int row = COORD_MATCH[0];
		int column = COORD_MATCH[1];
		
		Map<String, SfggRace> sfggRaceMap = new HashMap<String, SfggRace>();
		
		String matchName = null;
		String matchDesc = null;
		
		while (true) {
			// 读取当前行
			Row rowObj = sheet.getRow(row);
			
			// 退出条件
			if (rowObj == null || rowObj.getCell(column) == null || StringUtils.isEmpty(rowObj.getCell(column).toString().trim())) {
				break;
			}
			
			int matchNum = Integer.parseInt(CoreNumberUtil.formatNumBy0Digits(rowObj.getCell(column).getNumericCellValue()));
			
			SfggRace sfggRace = new SfggRace();
			sfggRace.setPhase(phaseNo);
			sfggRace.setMatchNum(matchNum);
			
			// 赛事名称
			Cell matchNameCell = rowObj.getCell(column + 2);
			if (matchNameCell != null && matchNameCell.getStringCellValue() != null) {
				String matchNameNew = matchNameCell.getStringCellValue().trim();
				if (StringUtils.isNotEmpty(matchNameNew)) {
					matchName = matchNameNew;
				}
			}
			sfggRace.setMatchName(matchName);
			
			// 赛事描述
			Cell matchDescCell = rowObj.getCell(column + 3);
			if (matchDescCell != null && matchDescCell.getStringCellValue() != null) {
				String matchDescNew = matchDescCell.getStringCellValue().trim();
				if (StringUtils.isNotEmpty(matchDescNew)) {
					matchDesc = matchDescNew;
				}
			}
			sfggRace.setMatchDesc(matchDesc);
			
			// 比赛时间
			sfggRace.setMatchDate(generateDatetimeWithoutYear(calendar, rowObj.getCell(column + 4).getStringCellValue()));
			
			// 停售时间
			Calendar endSaleCalendar = Calendar.getInstance();
			endSaleCalendar.setTime(generateDatetimeWithoutYear(calendar, rowObj.getCell(column + 8).getStringCellValue()));
			// 官方提前5分钟停售，这里不带提前量
			endSaleCalendar.add(Calendar.MINUTE, 5);
			sfggRace.setEndSaleTime(endSaleCalendar.getTime());
			
			// 主队
			sfggRace.setHomeTeam(rowObj.getCell(column + 5).getStringCellValue().trim());
			// 客队
			sfggRace.setAwayTeam(rowObj.getCell(column + 7).getStringCellValue().trim());
			
			sfggRaces.add(sfggRace);
			sfggRaceMap.put(String.valueOf(matchNum), sfggRace);
			
			row ++;
		}

		// 跳过让分数据之间的无效行
		while (true) {
			// 读取当前行
			Row rowObj = sheet.getRow(row);
			
			if (rowObj == null || rowObj.getCell(column) == null || StringUtils.isEmpty(rowObj.getCell(column).toString().trim())) {
				row ++;
				continue;
			}

			int matchNum = 0;
			try {
				matchNum = Integer.parseInt(CoreNumberUtil.formatNumBy0Digits(rowObj.getCell(column).getNumericCellValue()));
			} catch (Exception e) {
				row ++;
				continue;
			}
			
			// 读取到有效场次号
			if (sfggRaceMap.containsKey(String.valueOf(matchNum))) {
				break;
			}
			
			row ++;
		}
		
		while (true) {
			// 读取当前行
			Row rowObj = sheet.getRow(row);
			
			// 退出条件
			if (rowObj == null || rowObj.getCell(column) == null || StringUtils.isEmpty(rowObj.getCell(column).toString().trim())) {
				break;
			}
			
			int matchNum = Integer.parseInt(CoreNumberUtil.formatNumBy0Digits(rowObj.getCell(column).getNumericCellValue()));
			
			SfggRace sfggRace = sfggRaceMap.get(String.valueOf(matchNum));
			
			// 只需要处理让分
			Cell handicapCell = rowObj.getCell(column + 8);
			if (handicapCell == null) {
				sfggRace.setHandicap("0");
			} else {
				// 替换掉+号
				String handicap = StringUtils.replace(ExcelUtil.getCellValue(handicapCell).trim(), "+", "");
				if (StringUtils.isEmpty(handicap)) {
					sfggRace.setHandicap("0");
				} else {
					sfggRace.setHandicap(handicap);
				}
			}
			
			
			row ++;
		}
		
		for (SfggRace sfggRace : sfggRaces) {
			if (matchPriorityList != null && matchPriorityList.contains(sfggRace.getMatchName())) {
				sfggRace.setPriority(100);
			} else {
				sfggRace.setPriority(0);
			}
		} 
		
		ExcelUtil.closeExcel();
		
		List<Object> rtnList = new ArrayList<Object>();
		rtnList.add(sfggRaces);
		rtnList.add(null);
		return rtnList;
	}

	
	@Override
	public Map<String, Map<String, String>> getCurrentInstantSP(List<String> matchIdList, LotteryType lotteryType) throws ApiRemoteCallFailedException {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_SFGG_SP_GET);
		request.setParameter("lottery_type", lotteryType.getValue() + "");
		if (matchIdList != null && matchIdList.size() > 0) {
			request.setParameterIn(SfggRace.QUERY_ID, matchIdList);
		}
		
		ApiResponse response = null;
		
		logger.info("获取指定id的胜负过关赛事即时SP值",request.toQueryString());
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取指定id的胜负过关赛事即时SP值异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException("获取指定id的胜负过关赛事即时SP值发生API请求错误");
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

	public List<String> getMatchPriorityList() {
		return matchPriorityList;
	}

	public void setMatchPriorityList(List<String> matchPriorityList) {
		this.matchPriorityList = matchPriorityList;
	}
}
