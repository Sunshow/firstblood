package web.service.impl.lottery;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.JczqChampionRace;
import com.lehecai.core.api.lottery.JczqChampionSecondRace;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.JczqDynamicDrawStatus;
import com.lehecai.core.lottery.JczqRaceStatus;
import com.lehecai.core.lottery.JczqStaticDrawStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreJSONUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JczqRaceServiceImpl implements JczqRaceService {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private PhaseService phaseService;

	@Override
	public JczqRace getRaceByMatchNum(String matchNum)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩足球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_DETAIL);
		request.setParameter(JczqRace.QUERY_MATCH_NUM, matchNum);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩足球赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩足球赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩足球赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取竞彩足球赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩足球赛程数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<JczqRace> races = JczqRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races.get(0);
		}
		return null;
	}
	
	@Override
	public List<JczqRace> getRaceListByDateAndStatus(String phaseNo, List<JczqRaceStatus> statuses, boolean isToday) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩足球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_SEARCH);
		
		/*if (officialDate != null) {
			Calendar cd = Calendar.getInstance();
			cd.setTime(officialDate);
			cd.add(Calendar.DATE, 1);
			request.setParameterBetween(JczqRace.QUERY_OFFICIAL_DATE, CoreDateUtils.formatDate(officialDate, CoreDateUtils.DATETIME), null);
			if (!isToday) {
				request.setParameterLess(JczqRace.QUERY_OFFICIAL_DATE, CoreDateUtils.formatDate(cd.getTime(), CoreDateUtils.DATETIME));
			}
		}*/
        if (phaseNo != null) {
		    request.setParameter(JczqRace.QUERY_PHASE, phaseNo);
        }
		
		if (statuses != null && statuses.size() > 0) {
			List<String> list = new ArrayList<String>();
			for (JczqRaceStatus status : statuses) {
				list.add(status.getValue() + "");
			}
			request.setParameterIn(JczqRace.QUERY_STATUS, list);
		}
		request.setPage(1);
		request.setPagesize(1000);
		request.addOrder(JczqRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩足球赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩足球赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩足球赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取竞彩足球赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩足球赛程数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<JczqRace> races = JczqRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races;
		}
		return null;
	}
	
	@Override
	public List<JczqChampionRace> getChampionRaceList(String phase, List<JczqRaceStatus> statuses) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩足球猜冠军赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CHAMPION_SEARCH);
		
		if (!StringUtils.isEmpty(phase)) {
			request.setParameter(JczqChampionRace.QUERY_PHASE, phase);
		}
		
		if (statuses != null && statuses.size() > 0) {
			List<String> list = new ArrayList<String>();
			for (JczqRaceStatus status : statuses) {
				list.add(status.getValue() + "");
			}
			request.setParameterIn(JczqChampionRace.QUERY_STATUS, list);
		}
		request.setPage(1);
		request.setPagesize(1000);
		request.addOrder(JczqChampionRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩足球猜冠军赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩足球猜冠军赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩足球猜冠军赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取竞彩足球猜冠军赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩足球猜冠军赛程数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<JczqChampionRace> races = JczqChampionRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races;
		}
		return null;
	}
	
	@Override
	public List<JczqChampionSecondRace> getChampionSecondRaceList(String phase, List<JczqRaceStatus> statuses) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩足球猜冠亚军赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CHAMPION_SECOND_SEARCH);
		
		if (!StringUtils.isEmpty(phase)) {
			request.setParameter(JczqChampionSecondRace.QUERY_PHASE, phase);
		}
		
		if (statuses != null && statuses.size() > 0) {
			List<String> list = new ArrayList<String>();
			for (JczqRaceStatus status : statuses) {
				list.add(status.getValue() + "");
			}
			request.setParameterIn(JczqChampionSecondRace.QUERY_STATUS, list);
		}
		request.setPage(1);
		request.setPagesize(1000);
		request.addOrder(JczqChampionSecondRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩足球猜冠亚军赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩足球猜冠亚军赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩足球猜冠亚军赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取竞彩足球猜冠亚军赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩足球猜冠亚军赛程数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<JczqChampionSecondRace> races = JczqChampionSecondRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races;
		}
		return null;
	}

	@Override
	public List<JczqRace> findJczqRacesByMatchDate(Date matchDate, PageBean pageBean) {
		logger.info("进入调用API根据指定比赛开始准确时间查询竞彩足球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_SEARCH);
		request.setParameter(JczqRace.QUERY_MATCH_DATE, CoreDateUtils.formatDate(matchDate, CoreDateUtils.DATETIME));

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		ApiResponse response = null;
		List<JczqRace> list = null;

		logger.info("查询竞彩足球赛程信息列表", request.toQueryString());

		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩足球赛程异常!{}", e.getMessage());
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = JczqRace.convertFromJSONArray(response.getData());
			if (pageBean != null) {
				pageBean.setCount(response.getTotal());
			}
			return list;
		} catch (Exception e) {
			logger.error("解析返回竞彩足球赛程数据错误!");
		}
		return null;
	}

	@Override
	public List<JczqRace> findJczqRacesBySpecifiedMatchNum(List<String> mactchNumList, PageBean pageBean) {
		logger.info("进入调用API根据指定场次号查询竞彩足球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_SEARCH);
		request.setParameterIn(JczqRace.ORDER_MATCH_NUM, mactchNumList);

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		ApiResponse response = null;
		List<JczqRace> list = null;

		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩足球赛程异常!{}", e.getMessage());
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = JczqRace.convertFromJSONArray(response.getData());
			if (pageBean != null) {
				pageBean.setCount(response.getTotal());
			}
			return list;
		} catch (Exception e) {
			logger.error("解析返回竞彩足球赛程数据错误!");
		}
		return null;
	}

	private Map<String, JczqRace> packDbRace (List<JczqRace> races) throws ApiRemoteCallFailedException {
		if (races == null || races.isEmpty()) {
			return null;
		}
		Set<String> phaseNoSet = new HashSet<String>();
		for (JczqRace r : races) {
			phaseNoSet.add(r.getPhase());
		}
		List<JczqRace> dbRaces = new ArrayList<JczqRace>();
		for (String d : phaseNoSet) {
			List<JczqRace> raceList = getRaceListByDateAndStatus(d, null, false);
			if (raceList != null && raceList.size() > 0) {
				dbRaces.addAll(raceList);
			}
		}
		if (dbRaces != null && !dbRaces.isEmpty()) {
			Map<String, JczqRace> dbRaceMap = new HashMap<String, JczqRace>();
			for (JczqRace dbRace : dbRaces) {
				dbRaceMap.put(dbRace.getMatchNum(), dbRace);
			}
            return dbRaceMap;
		}
		return null;
	}
	
	private Map<String, JczqChampionRace> packChampionRace (
			List<JczqChampionRace> races,
			Map<String, JczqChampionRace> dbRaceMap) throws ApiRemoteCallFailedException {
		if (races == null || races.isEmpty()) {
			return null;
		}
		List<JczqChampionRace> dbRaces = getChampionRaceList(races.get(0).getPhase(), null);
		if (dbRaces != null && !dbRaces.isEmpty()) {
			dbRaceMap = new HashMap<String, JczqChampionRace>();
			for (JczqChampionRace dbRace : dbRaces) {
				dbRaceMap.put(dbRace.getMatchNum(), dbRace);
			}
		}
		return dbRaceMap;
	}
	private Map<String, JczqChampionSecondRace> packChampionSecondRace (
			List<JczqChampionSecondRace> races,
			Map<String, JczqChampionSecondRace> dbRaceMap) throws ApiRemoteCallFailedException {
		if (races == null || races.isEmpty()) {
			return null;
		}
		List<JczqChampionSecondRace> dbRaces = getChampionSecondRaceList(races.get(0).getPhase(), null);
		if (dbRaces != null && !dbRaces.isEmpty()) {
			dbRaceMap = new HashMap<String, JczqChampionSecondRace>();
			for (JczqChampionSecondRace dbRace : dbRaces) {
				dbRaceMap.put(dbRace.getMatchNum(), dbRace);
			}
		}
		
		return dbRaceMap;
	}
	
	private boolean compareRace(JczqRace pageRace, JczqRace dbRace) {
		if (dbRace == null) {
			return saveRace(pageRace);
		} else {
			dbRace.setHomeTeam(pageRace.getHomeTeam());
			dbRace.setAwayTeam(pageRace.getAwayTeam());
			dbRace.setMatchName(pageRace.getMatchName());
			dbRace.setMatchDate(pageRace.getMatchDate());
			dbRace.setEndSaleTime(pageRace.getEndSaleTime());
			dbRace.setPriority(pageRace.getPriority());
			dbRace.setHandicap(pageRace.getHandicap());
			dbRace.setStaticSaleSpfStatus(pageRace.getStaticSaleSpfStatus());
			dbRace.setDynamicSaleSpfStatus(pageRace.getDynamicSaleSpfStatus());
			dbRace.setStaticSaleBfStatus(pageRace.getStaticSaleBfStatus());
			dbRace.setDynamicSaleBfStatus(pageRace.getDynamicSaleBfStatus());
			dbRace.setStaticSaleJqsStatus(pageRace.getStaticSaleJqsStatus());
			dbRace.setDynamicSaleJqsStatus(pageRace.getDynamicSaleJqsStatus());
			dbRace.setStaticSaleBqcStatus(pageRace.getStaticSaleBqcStatus());
			dbRace.setDynamicSaleBqcStatus(pageRace.getDynamicSaleBqcStatus());
			dbRace.setStaticSaleSpfWrqStatus(pageRace.getStaticSaleSpfWrqStatus());
			dbRace.setDynamicSaleSpfWrqStatus(pageRace.getDynamicSaleSpfWrqStatus());
			dbRace.setFxId(pageRace.getFxId());
			
			return updateRace(dbRace);
		}
	}
	
	private boolean compareChampionRace(JczqChampionRace pageRace, JczqChampionRace dbRace) {
		if (dbRace == null) {
			return saveChampionRace(pageRace);
		} else {
			dbRace.setTeam(pageRace.getTeam());
			dbRace.setFxId(pageRace.getFxId());
			dbRace.setPriority(pageRace.getPriority());
			return updateChampionRace(dbRace);
		}
	}
	
	private boolean compareChampionSecondRace(JczqChampionSecondRace pageRace, JczqChampionSecondRace dbRace) {
		if (dbRace == null) {
			return saveChampionSecondRace(pageRace);
		} else {
			dbRace.setHomeTeam(pageRace.getHomeTeam());
			dbRace.setAwayTeam(pageRace.getAwayTeam());
			dbRace.setFxId(pageRace.getFxId());
			dbRace.setPriority(pageRace.getPriority());
			return updateChampionSecondRace(dbRace);
		}
	}
	
	private boolean compareRaceSp(JczqRace pageRace, JczqRace dbRace) {
		if (dbRace == null) {
			return false;
		}
		dbRace.setFirstHalf(pageRace.getFirstHalf());
		dbRace.setSecondHalf(pageRace.getSecondHalf());
		dbRace.setFinalScore(pageRace.getFinalScore());
		dbRace.setPrizeSpf(pageRace.getPrizeSpf());
		dbRace.setPrizeBf(pageRace.getPrizeBf());
		dbRace.setPrizeJqs(pageRace.getPrizeJqs());
		dbRace.setPrizeBqc(pageRace.getPrizeBqc());
		dbRace.setPrizeSpfWrq(pageRace.getPrizeSpfWrq());
		
		return updateRaceSp(dbRace);
	}
	
	@Override
	public ResultBean batchCreate(List<JczqRace> races) throws ApiRemoteCallFailedException {
		ResultBean resultBean = new ResultBean();
		StringBuffer failedTeam = null;
		
		Map<String, JczqRace> dbRaceMap = packDbRace(races);
		
		for (JczqRace race : races) {
			if (dbRaceMap == null || dbRaceMap.isEmpty()) {
				if (!compareRace(race, null)) {
					if (failedTeam == null) {
						failedTeam = new StringBuffer();
					}
					failedTeam.append(race.getMatchNum()).append(",");
				}
			} else {
				if (!compareRace(race, dbRaceMap.get(race.getMatchNum()))) {
					if (failedTeam == null) {
						failedTeam = new StringBuffer();
					}
					failedTeam.append(race.getMatchNum()).append(",");
				}
			}
		}
		if (failedTeam != null) {
			resultBean.setResult(false);
			resultBean.setMessage("场次：["+failedTeam.substring(0, failedTeam.length()-1)+"]对阵更新失败！");
		} else {
			resultBean.setResult(true);
			resultBean.setMessage("批量生成竞彩足球对阵成功！");
		}
		return resultBean;
	}
	
	@Override
	public ResultBean batchCreateChampion(List<JczqChampionRace> races) throws ApiRemoteCallFailedException {
		ResultBean resultBean = new ResultBean();
		StringBuffer failedTeam = null;
		
		Map<String, JczqChampionRace> dbRaceMap = null;
		dbRaceMap = packChampionRace(races, dbRaceMap);
		
		for (JczqChampionRace race : races) {
			if (dbRaceMap == null || dbRaceMap.isEmpty()) {
				if (!compareChampionRace(race, null)) {
					if (failedTeam == null) {
						failedTeam = new StringBuffer();
					}
					failedTeam.append(race.getMatchNum()).append(",");
				}
			} else {
				if (!compareChampionRace(race, dbRaceMap.get(race.getMatchNum()))) {
					if (failedTeam == null) {
						failedTeam = new StringBuffer();
					}
					failedTeam.append(race.getMatchNum()).append(",");
				}
			}
		}
		if (failedTeam != null) {
			resultBean.setResult(false);
			resultBean.setMessage("场次：["+failedTeam.substring(0, failedTeam.length()-1)+"]对阵更新失败！");
		} else {
			resultBean.setResult(true);
			resultBean.setMessage("批量生成竞彩足球猜冠军对阵成功！");
		}
		return resultBean;
	}
	
	@Override
	public ResultBean batchCreateChampionSecond(List<JczqChampionSecondRace> races) throws ApiRemoteCallFailedException {
		ResultBean resultBean = new ResultBean();
		StringBuffer failedTeam = null;
		
		Map<String, JczqChampionSecondRace> dbRaceMap = null;
		dbRaceMap = packChampionSecondRace(races, dbRaceMap);
		
		for (JczqChampionSecondRace race : races) {
			if (dbRaceMap == null || dbRaceMap.isEmpty()) {
				if (!compareChampionSecondRace(race, null)) {
					if (failedTeam == null) {
						failedTeam = new StringBuffer();
					}
					failedTeam.append(race.getMatchNum()).append(",");
				}
			} else {
				if (!compareChampionSecondRace(race, dbRaceMap.get(race.getMatchNum()))) {
					if (failedTeam == null) {
						failedTeam = new StringBuffer();
					}
					failedTeam.append(race.getMatchNum()).append(",");
				}
			}
		}
		if (failedTeam != null) {
			resultBean.setResult(false);
			resultBean.setMessage("场次：["+failedTeam.substring(0, failedTeam.length()-1)+"]对阵更新失败！");
		} else {
			resultBean.setResult(true);
			resultBean.setMessage("批量生成竞彩足球猜冠亚军对阵成功！");
		}
		return resultBean;
	}
	
	@Override
	public ResultBean batchCreateSp(List<JczqRace> races) throws ApiRemoteCallFailedException {
		ResultBean resultBean = new ResultBean();
		StringBuffer failedTeam = null;
		
		Map<String, JczqRace> dbRaceMap = packDbRace(races);
		
		for (JczqRace race : races) {
			if (!compareRaceSp(race, dbRaceMap.get(race.getMatchNum()))) {
				if (failedTeam == null) {
					failedTeam = new StringBuffer();
				}
				failedTeam.append(race.getMatchNum()).append(",");
			}
		}
		if (failedTeam != null) {
			resultBean.setResult(false);
			resultBean.setMessage("场次：["+failedTeam.substring(0, failedTeam.length()-1)+"]对阵更新失败！");
		} else {
			resultBean.setResult(true);
			resultBean.setMessage("批量生成竞彩足球对阵成功！");
		}
		return resultBean;
	}
	@Override
	public boolean saveRace(JczqRace race) {
		logger.info("进入调用API生成竞彩足球对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CREATE);
			createRequest.setParameterForUpdate(JczqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqRace.SET_PHASE, race.getPhase());
			createRequest.setParameterForUpdate(JczqRace.SET_OFFICIAL_DATE, DateUtil.formatDate(race.getOfficialDate()));
			createRequest.setParameterForUpdate(JczqRace.SET_OFFICIAL_NUM, race.getOfficialNum() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_END_SALE_DATE, DateUtil.formatDate(race.getEndSaleTime(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(JczqRace.SET_MATCH_DATE, DateUtil.formatDate(race.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(JczqRace.SET_HOME_TEAM, race.getHomeTeam());
			createRequest.setParameterForUpdate(JczqRace.SET_AWAY_TEAM, race.getAwayTeam());
			createRequest.setParameterForUpdate(JczqRace.SET_MATCH_NAME, race.getMatchName());
			createRequest.setParameterForUpdate(JczqRace.SET_STATUS, race.getStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_PRIORITY, race.getPriority() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_EXT, race.getExt());
			createRequest.setParameterForUpdate(JczqRace.SET_HANDICAP, race.getHandicap());
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_DRAW_STATUS, race.getStaticDrawStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_DRAW_STATUS, race.getDynamicDrawStatus().getValue() + "");
			
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_SPF, race.getDynamicSaleSpfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_SPF, race.getStaticSaleSpfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_BF, race.getDynamicSaleBfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_BF, race.getStaticSaleBfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_JQS, race.getDynamicSaleJqsStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_JQS, race.getStaticSaleJqsStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_BQC, race.getDynamicSaleBqcStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_BQC, race.getStaticSaleBqcStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_SPF_WRQ, race.getDynamicSaleSpfWrqStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_SPF_WRQ, race.getStaticSaleSpfWrqStatus().getValue() + "");
			
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!{}",race.getMatchNum(), e.getMessage());
			return false;
		}
		
		logger.info("生成竞彩足球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API保存竞彩足球数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("生成竞彩足球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS || response.getTotal()==0){
			logger.error("第{}场对阵信息存储失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息存储成功!", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean saveChampionRace(JczqChampionRace race) {
		logger.info("进入调用API生成竞彩足球猜冠军对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CHAMPION_CREATE);
			createRequest.setParameterForUpdate(JczqChampionRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqChampionRace.SET_PHASE, race.getPhase());
			createRequest.setParameterForUpdate(JczqChampionRace.SET_TEAM, race.getTeam());
			createRequest.setParameterForUpdate(JczqChampionRace.SET_STATUS, race.getStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqChampionRace.SET_FX_ID, race.getFxId() + "");
			createRequest.setParameterForUpdate(JczqChampionRace.SET_PRIORITY, race.getPriority() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!{}",race.getMatchNum(), e.getMessage());
			return false;
		}
		
		logger.info("生成竞彩足球猜冠军对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API保存竞彩足球猜冠军数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("生成竞彩足球猜冠军对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS || response.getTotal()==0){
			logger.error("第{}场对阵信息存储失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息存储成功!", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean saveChampionSecondRace(JczqChampionSecondRace race) {
		logger.info("进入调用API生成竞彩足球猜冠亚军对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CHAMPION_SECOND_CREATE);
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_PHASE, race.getPhase());
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_HOME_TEAM, race.getHomeTeam());
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_AWAY_TEAM, race.getAwayTeam());
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_STATUS, race.getStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_FX_ID, race.getFxId() + "");
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_PRIORITY, race.getPriority() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!{}",race.getMatchNum(), e.getMessage());
			return false;
		}
		
		logger.info("生成竞彩足球猜冠亚军对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API保存竞彩足球猜冠亚军数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("生成竞彩足球猜冠亚军对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS || response.getTotal()==0){
			logger.error("第{}场对阵信息存储失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息存储成功!", race.getMatchNum());
		return true;
	}

	@Override
	public boolean updateRace(JczqRace race) {
		logger.info("进入调用API更新竞彩足球对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_UPDATE);
			createRequest.setParameter(JczqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqRace.SET_HOME_TEAM, race.getHomeTeam());
			createRequest.setParameterForUpdate(JczqRace.SET_AWAY_TEAM, race.getAwayTeam());
			createRequest.setParameterForUpdate(JczqRace.SET_MATCH_NAME, race.getMatchName());
			createRequest.setParameterForUpdate(JczqRace.SET_MATCH_DATE, DateUtil.formatDate(race.getMatchDate(), DateUtil.DATETIME));
			createRequest.setParameterForUpdate(JczqRace.SET_END_SALE_DATE, DateUtil.formatDate(race.getEndSaleTime(), DateUtil.DATETIME));
			createRequest.setParameterForUpdate(JczqRace.SET_PRIORITY, race.getPriority() + "");
			createRequest.setParameterForUpdate(DcRace.SET_EXT, race.getExt());
			createRequest.setParameterForUpdate(JczqRace.SET_HANDICAP, race.getHandicap());
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_SPF, race.getDynamicSaleSpfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_SPF, race.getStaticSaleSpfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_BF, race.getDynamicSaleBfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_BF, race.getStaticSaleBfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_JQS, race.getDynamicSaleJqsStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_JQS, race.getStaticSaleJqsStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_BQC, race.getDynamicSaleBqcStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_BQC, race.getStaticSaleBqcStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_SALE_STATUS_SPF_WRQ, race.getDynamicSaleSpfWrqStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_SALE_STATUS_SPF_WRQ, race.getStaticSaleSpfWrqStatus().getValue() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_FX_ID, race.getFxId() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩足球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateChampionRace(JczqChampionRace race) {
		logger.info("进入调用API更新竞彩足球猜冠军对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CHAMPION_UPDATE);
			createRequest.setParameter(JczqChampionRace.QUERY_ID, race.getId() + "");
			createRequest.setParameterForUpdate(JczqChampionRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqChampionRace.SET_PHASE, race.getPhase());
			createRequest.setParameterForUpdate(JczqChampionRace.SET_TEAM, race.getTeam());
			createRequest.setParameterForUpdate(JczqChampionRace.SET_FX_ID, race.getFxId() + "");
			createRequest.setParameterForUpdate(JczqChampionRace.SET_PRIORITY, race.getPriority() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩足球猜冠军对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球猜冠军数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球猜冠军对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getMatchNum());
		return true;
	}
	@Override
	public boolean updateChampionSecondRace(JczqChampionSecondRace race) {
		logger.info("进入调用API更新竞彩足球猜冠亚军对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CHAMPION_SECOND_UPDATE);
			createRequest.setParameter(JczqChampionSecondRace.QUERY_ID, race.getId() + "");
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_PHASE, race.getPhase());
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_HOME_TEAM, race.getHomeTeam());
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_AWAY_TEAM, race.getAwayTeam());
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_FX_ID, race.getFxId() + "");
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_PRIORITY, race.getPriority() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩足球猜冠亚军对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球猜冠亚军数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球猜冠亚军对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateRaceSp(JczqRace race) {
		logger.info("进入调用API更新竞彩足球对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_UPDATE);
			createRequest.setParameter(JczqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqRace.SET_FIRST_HALF, race.getFirstHalf());
			createRequest.setParameterForUpdate(JczqRace.SET_SECOND_HALF, race.getSecondHalf());
			createRequest.setParameterForUpdate(JczqRace.SET_FINAL_SCORE, race.getFinalScore());
			createRequest.setParameterForUpdate(JczqRace.SET_PRIZE_SPF, race.getPrizeSpf());
			createRequest.setParameterForUpdate(JczqRace.SET_PRIZE_BF, race.getPrizeBf());
			createRequest.setParameterForUpdate(JczqRace.SET_PRIZE_JQS, race.getPrizeJqs());
			createRequest.setParameterForUpdate(JczqRace.SET_PRIZE_BQC, race.getPrizeBqc());
			createRequest.setParameterForUpdate(JczqRace.SET_PRIZE_SPF_WRQ, race.getPrizeSpfWrq());
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩足球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateRaceStatus(JczqRace race) {
		logger.info("进入调用API更新竞彩足球状态");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_UPDATE);
			createRequest.setParameter(JczqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqRace.SET_STATUS, race.getStatus().getValue() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩足球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateChampionRaceStatus(JczqChampionRace race) {
		logger.info("进入调用API更新竞彩足球猜冠军状态");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CHAMPION_UPDATE);
			createRequest.setParameter(JczqChampionRace.QUERY_ID, race.getId() + "");
			createRequest.setParameterForUpdate(JczqRace.SET_STATUS, race.getStatus().getValue() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getId());
			return false;
		}
		
		logger.info("更新竞彩足球猜冠军对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球猜冠军数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球猜冠军对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getId());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getId());
		return true;
	}
	
	@Override
	public boolean updateChampionSecondRaceStatus(JczqChampionSecondRace race) {
		logger.info("进入调用API更新竞彩足球猜冠亚军状态");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_CHAMPION_SECOND_UPDATE);
			createRequest.setParameter(JczqChampionSecondRace.QUERY_ID, race.getId() + "");
			createRequest.setParameterForUpdate(JczqChampionSecondRace.SET_STATUS, race.getStatus().getValue() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getId());
			return false;
		}
		
		logger.info("更新竞彩足球猜冠亚军对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球猜冠亚军数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球猜冠亚军对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getId());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getId());
		return true;
	}
	
	@Override
	public boolean updateRaceStaticDrawStatus(JczqRace race) {
		logger.info("进入调用API更新竞彩足球固定奖金开奖状态");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_UPDATE);
			createRequest.setParameter(JczqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqRace.SET_STATIC_DRAW_STATUS, race.getStaticDrawStatus().getValue() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败！",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩足球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球数据异常！{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败！", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功！", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateRaceDynamicDrawStatus(JczqRace race) {
		logger.info("进入调用API更新竞彩足球浮动奖金开奖状态");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_UPDATE);
			createRequest.setParameter(JczqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JczqRace.SET_DYNAMIC_DRAW_STATUS, race.getDynamicDrawStatus().getValue() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败！",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩足球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩足球数据异常！{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩足球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败！", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功！", race.getMatchNum());
		return true;
	}
	
	@Override
	public List<JczqRace> getJczqRaceDrawReadyList(Date startOfficialDate,
			Date endOfficialDate, List<JczqRaceStatus> statuses, JczqStaticDrawStatus staticDrawStatus, JczqDynamicDrawStatus dynamicDrawStatus, int max)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩足球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_SEARCH);
		
		if(startOfficialDate != null && endOfficialDate != null){
			if(startOfficialDate.getTime() > endOfficialDate.getTime()){
				logger.error("API获取竞彩足球赛程数据参数开奖开始时间大于结束时间,不符合查询规则,查询失败");
				throw new ApiRemoteCallFailedException("API获取竞彩足球赛程数据参数开奖开始时间大于结束时间,不符合查询规则,查询失败");
			}
		}
		String startDate = null;
		String endDate = null;
		if(startOfficialDate != null){
			String startBaseDate = CoreDateUtils.formatDate(startOfficialDate);
			startDate = startBaseDate + " 00:00:00";
		}
		if (endOfficialDate != null) {
			String endBaseDate = CoreDateUtils.formatDate(endOfficialDate);
			endDate = endBaseDate + " 23:59:59";
		}
		if(startDate != null || endDate != null){
			request.setParameterBetween(JczqRace.QUERY_OFFICIAL_DATE, startDate, endDate);
		}
		
		if (statuses != null && statuses.size() > 0) {
			List<String> list = new ArrayList<String>();
			for (JczqRaceStatus status : statuses) {
				list.add(status.getValue() + "");
			}
			request.setParameterIn(JczqRace.QUERY_STATUS, list);
		}
		if(staticDrawStatus != null){
			request.setParameter(JczqRace.QUERY_STATIC_DRAW_STATUS, String.valueOf(staticDrawStatus.getValue()));
		}
		if(dynamicDrawStatus != null){
			request.setParameter(JczqRace.QUERY_DYNAMIC_DRAW_STATUS, String.valueOf(dynamicDrawStatus.getValue()));
		}
		
		// order
		request.addOrder(JczqRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		
		request.setPage(1);
		request.setPagesize(max);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩足球赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩足球赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩足球赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取竞彩足球赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩足球赛程数据为空, message={}", response.getMessage());
			return null;
		}
		List<JczqRace> races = JczqRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races;
		}
		return null;
	}
	
	@Override
	public List<JczqRace> getJczqRaceDrawReadyList(String phaseNo, List<JczqRaceStatus> statuses, JczqStaticDrawStatus staticDrawStatus, JczqDynamicDrawStatus dynamicDrawStatus, int max)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩足球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_SEARCH);
		if(!StringUtils.isEmpty(phaseNo)){
			request.setParameter(JczqRace.QUERY_PHASE, phaseNo);
		}
		
		if (statuses != null && statuses.size() > 0) {
			List<String> list = new ArrayList<String>();
			for (JczqRaceStatus status : statuses) {
				list.add(status.getValue() + "");
			}
			request.setParameterIn(JczqRace.QUERY_STATUS, list);
		}
		if(staticDrawStatus != null){
			request.setParameter(JczqRace.QUERY_STATIC_DRAW_STATUS, String.valueOf(staticDrawStatus.getValue()));
		}
		if(dynamicDrawStatus != null){
			request.setParameter(JczqRace.QUERY_DYNAMIC_DRAW_STATUS, String.valueOf(dynamicDrawStatus.getValue()));
		}
		
		// order
		request.addOrder(JczqRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		
		request.setPage(1);
		request.setPagesize(max);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩足球赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩足球赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩足球赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取竞彩足球赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩足球赛程数据为空, message={}", response.getMessage());
			return null;
		}
		//JSONArray array = JSONArray.fromObject("{\"code\":0,\"data\":[{\"match_num\":\"2012121001001\",\"phase\":\"2011001\",\"official_date\":\"2012-12-10 00:00:00\",\"official_num\":\"001\",\"create_at\":\"2012-12-07 10:48:23\",\"time_endsale\":\"2012-12-11 00:00:00\",\"match_name\":\"意大利甲级联赛\",\"match_date\":\"2012-12-11 02:00:00\",\"home_team\":\"桑普多利亚\",\"away_team\":\"乌迪内斯\",\"first_half\":\"1:1\",\"second_half\":\"1:1\",\"final_score\":\"2:2\",\"status\":\"4\",\"static_draw_status\":\"2\",\"dynamic_draw_status\":\"1\",\"static_sale_status_spf\":\"2\",\"dynamic_sale_status_spf\":\"2\",\"static_sale_status_bf\":\"2\",\"dynamic_sale_status_bf\":\"2\",\"static_sale_status_jqs\":\"2\",\"dynamic_sale_status_jqs\":\"2\",\"static_sale_status_bqc\":\"2\",\"dynamic_sale_status_bqc\":\"2\",\"prize_spf\":\"2\",\"prize_bf\":\"2\",\"prize_jqs\":\"2\",\"prize_bqc\":\"2\",\"fx_id\":\"994360\",\"priority\":\"0\",\"ext\":null,\"handicap\":\"0\"},{\"match_num\":\"2012121001002\",\"phase\":\"2011001\",\"official_date\":\"2012-12-10 00:00:00\",\"official_num\":\"002\",\"create_at\":\"2012-12-07 10:48:23\",\"time_endsale\":\"2012-12-11 00:00:00\",\"match_name\":\"葡萄牙超级联赛\",\"match_date\":\"2012-12-11 02:15:00\",\"home_team\":\"科英布拉大学\",\"away_team\":\"布拉加\",\"first_half\":\"1:1\",\"second_half\":\"1:1\",\"final_score\":\"2:2\",\"status\":\"4\",\"static_draw_status\":\"2\",\"dynamic_draw_status\":\"1\",\"static_sale_status_spf\":\"2\",\"dynamic_sale_status_spf\":\"2\",\"static_sale_status_bf\":\"2\",\"dynamic_sale_status_bf\":\"2\",\"static_sale_status_jqs\":\"2\",\"dynamic_sale_status_jqs\":\"2\",\"static_sale_status_bqc\":\"2\",\"dynamic_sale_status_bqc\":\"2\",\"prize_spf\":\"2\",\"prize_bf\":\"2\",\"prize_jqs\":\"2\",\"prize_bqc\":\"2\",\"fx_id\":\"976019\",\"priority\":\"0\",\"ext\":null,\"handicap\":\"0\"}],\"total\":\"2\",\"message\":\"success\"}");
		//response.setData(array);
		
		List<JczqRace> races = JczqRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races;
		}
		return null;
	}
	
	@Override
	public List<JczqRace> searchJczqRaceList(List<JczqRaceStatus> statusList, String orderStr, String orderView, Integer size) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据比赛状态查询竞彩蓝球对阵信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_SEARCH);
		//比赛状态
		if (statusList != null && !statusList.isEmpty()) {
			List<String> statusValueList = new ArrayList<String>();
			for (JczqRaceStatus status : statusList) {
				statusValueList.add(String.valueOf(status.getValue()));
			}
			request.setParameterIn(JczqRace.QUERY_STATUS, statusValueList);
		}
		
		//排序
		if(orderStr != null && !"".equals(orderStr) && orderView != null && !"".equals(orderView)){		
			request.addOrder(orderStr,orderView);
		}
		
		//数量
		if(size != null){
			request.setPage(1);
			request.setPagesize(size);
		}
		
		ApiResponse response = null;
		List<JczqRace> list = null;
		
		logger.info("根据比赛状态获取竞彩蓝球对阵信息列表：",request.toQueryString());
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩蓝球对阵异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException("根据比赛状态获取竞彩蓝球对阵信息列表发生API请求错误");
		}
		if(response == null || response.getCode() != ApiConstant.RC_SUCCESS){
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = JczqRace.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回竞彩蓝球对阵数据错误!");
		}
		return list;
	}
	
	@Override
	public List<JczqRace> recommendJczqRace(Integer size){
		List<JczqRace> jclqRaces = null;
		if (size == null || size <= 0) {
			return jclqRaces; 
		}
		
		List<JczqRaceStatus> jclqRaceStatus = new ArrayList<JczqRaceStatus>();
		jclqRaceStatus.add(JczqRaceStatus.OPEN);
		jclqRaceStatus.add(JczqRaceStatus.UNOPEN);
		
		try {
			jclqRaces = searchJczqRaceList(jclqRaceStatus, JczqRace.ORDER_MATCH_DATE, ApiConstant.API_REQUEST_ORDER_ASC, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩蓝球对阵异常！{}", e.getMessage());
			return null;
		}
		
		/**
		 * 已开售，未开售不够size场，取已结束的场次补全
		 */
		if (jclqRaces == null) {
			jclqRaces = new ArrayList<JczqRace>();
		}
		if (jclqRaces.size() < size) {
			try {
				jclqRaceStatus.clear();
				jclqRaceStatus.add(JczqRaceStatus.CLOSE);
				jclqRaceStatus.add(JczqRaceStatus.DRAW);
				jclqRaceStatus.add(JczqRaceStatus.REWARD);
				jclqRaceStatus.add(JczqRaceStatus.RESULT_SET);
				List<JczqRace> tempJczqRaces = searchJczqRaceList(jclqRaceStatus, JczqRace.ORDER_MATCH_DATE, ApiConstant.API_REQUEST_ORDER_DESC, size - jclqRaces.size());
				//倒序
				if (tempJczqRaces != null && !tempJczqRaces.isEmpty()) {
					Collections.reverse(tempJczqRaces);
					tempJczqRaces.addAll(jclqRaces);
					jclqRaces = tempJczqRaces;
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询竞彩蓝球对阵异常！{}", e.getMessage());
			}
		}
		
		if (jclqRaces == null || jclqRaces.isEmpty()) {
			return null;
		}
		jclqRaces = orderPriority(jclqRaces);
		if (jclqRaces.size() > size) {
			jclqRaces = jclqRaces.subList(0, size);
		}
		
		return jclqRaces;
	}
	@Override
	public int getJczqRaceSaleCount() {
		List<JczqRace> jclqRaces = null;
		
		List<JczqRaceStatus> jclqRaceStatus = new ArrayList<JczqRaceStatus>();
		jclqRaceStatus.add(JczqRaceStatus.OPEN);
		
		try {
			jclqRaces = searchJczqRaceList(jclqRaceStatus, JczqRace.ORDER_MATCH_DATE, ApiConstant.API_REQUEST_ORDER_ASC, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩蓝球对阵异常！{}", e.getMessage());
			return 0;
		}
		if (jclqRaces != null) {
			return jclqRaces.size();
		}
		return 0;
	}
	
	//按优先级排序
	private List<JczqRace> orderPriority(List<JczqRace> jclqRaces) {
		if (jclqRaces == null || jclqRaces.isEmpty()) {
			return null;
		}
		for (int i = 0; i < jclqRaces.size(); i++) {
			for (int j = i; j < jclqRaces.size(); j++) {
				//优先级高排在前面，否则场次靠前排前面
				if (jclqRaces.get(i).getPriority() < jclqRaces.get(j).getPriority()) {
					JczqRace tempDcRace = jclqRaces.get(i);
					jclqRaces.set(i, jclqRaces.get(j));
					jclqRaces.set(j, tempDcRace);
				} else if (jclqRaces.get(j).getOfficialDate().before(jclqRaces.get(i).getOfficialDate())) {
					JczqRace tempDcRace = jclqRaces.get(i);
					jclqRaces.set(i, jclqRaces.get(j));
					jclqRaces.set(j, tempDcRace);
				} else if (jclqRaces.get(j).getOfficialDate().equals((jclqRaces.get(i).getOfficialDate()))) {
					int _j = 0;
					try {
						_j = Integer.valueOf(jclqRaces.get(j).getOfficialNum());
					} catch (NumberFormatException e) {
						logger.error("排序时officialNum转换错误", e);
					}
					int _i = 0;
					try {
						_i = Integer.valueOf(jclqRaces.get(i).getOfficialNum());
					} catch (NumberFormatException e) {
						logger.error("排序时officialNum转换错误", e);
					}
					if (_j < _i) {
						JczqRace tempDcRace = jclqRaces.get(i);
						jclqRaces.set(i, jclqRaces.get(j));
						jclqRaces.set(j, tempDcRace);
					}
				}
			}
		}
		return jclqRaces;
	}
	
	@Override
	public Map<String, Map<String, String>> getJczqCurrentStaticSp(List<String> matchNums, LotteryType lotteryType) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩足球胜平负固定奖金及时sp数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCZQ_STATIC_SP);
		request.setParameter("lottery_type", lotteryType.getValue() + "");
		if (matchNums != null && matchNums.size() > 0) {
			request.setParameterIn(JczqRace.QUERY_MATCH_NUM, matchNums);
		}
		
		ApiResponse response = null;
		
		logger.info("根据比赛状态获取竞彩蓝球对阵信息列表",request.toQueryString());
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩蓝球对阵异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException("根据比赛状态获取竞彩蓝球对阵信息列表发生API请求错误");
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

            JSONObject object = null;
            try {
                if (obj.get(key) instanceof JSONObject) {
                    object = obj.getJSONObject(key);
                }
            } catch (Exception e) {
                logger.error("解析SP数据出错", e);
                logger.error("解析SP数据(key={})出错，错误数据为：{}", key, CoreJSONUtils.getString(obj, key));
            }
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

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
}
