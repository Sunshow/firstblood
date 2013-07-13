package web.service.impl.lottery;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.lottery.JclqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.JclqDynamicDrawStatus;
import com.lehecai.core.lottery.JclqRaceStatus;
import com.lehecai.core.lottery.JclqStaticDrawStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreJSONUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JclqRaceServiceImpl implements JclqRaceService {
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private PhaseService phaseService;

	@Override
	public JclqRace getRaceByMatchNum(String matchNum)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩篮球场次信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_DETAIL);
		request.setParameter(JclqRace.QUERY_MATCH_NUM, matchNum);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩篮球赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩篮球赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩篮球赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取竞彩篮球赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩篮球赛程数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<JclqRace> races = JclqRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races.get(0);
		}
		return null;
	}
	
	@Override
	public List<JclqRace> getRaceListByDateAndStatus(String phaseNo, List<JclqRaceStatus> statuses, boolean isToday) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩篮球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_SEARCH);

        if (phaseNo != null) {
		    request.setParameter(JclqRace.QUERY_PHASE, phaseNo);
        }
		
		if (statuses != null && statuses.size() > 0) {
			List<String> list = new ArrayList<String>();
			for (JclqRaceStatus status : statuses) {
				list.add(status.getValue() + "");
			}
			request.setParameterIn(JclqRace.QUERY_STATUS, list);
		}
		request.setPage(1);
		request.setPagesize(1000);
		request.addOrder(JclqRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩篮球赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩篮球赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩篮球赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取竞彩篮球赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩篮球赛程数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<JclqRace> races = JclqRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races;
		}
		return null;
	}

	@Override
	public List<JclqRace> findJclqRacesByMatchDate(Date matchDate, PageBean pageBean) {
		logger.info("进入调用API根据比赛开始准确时间查询竞彩篮球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_SEARCH);
		request.setParameter(JclqRace.QUERY_MATCH_DATE, CoreDateUtils.formatDate(matchDate, CoreDateUtils.DATETIME));

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		ApiResponse response = null;
		List<JclqRace> list = null;

		logger.info("查询竞彩篮球赛程信息列表", request.toQueryString());

		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩篮球赛程异常!{}", e.getMessage());
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = JclqRace.convertFromJSONArray(response.getData());
			if (list != null && !list.isEmpty()) {
				if (pageBean != null) {
					pageBean.setCount(response.getTotal());
				}
				return list;
			}
		} catch (Exception e) {
			logger.error("解析返回竞彩篮球赛程数据错误!");
		}
		return null;
	}

	@Override
	public List<JclqRace> findJclqRacesBySpecifiedMatchNum(List<String> mactchNumList, PageBean pageBean) {
		logger.info("进入调用API根据指定的场次号查询竞彩篮球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_SEARCH);
		request.setParameterIn(JclqRace.ORDER_MATCH_NUM, mactchNumList);

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		ApiResponse response = null;
		List<JclqRace> list = null;

		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩篮球赛程异常!{}", e.getMessage());
		}
		if (response == null || response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = JclqRace.convertFromJSONArray(response.getData());
			if (list != null && !list.isEmpty()) {
				if (pageBean != null) {
					pageBean.setCount(response.getTotal());
				}
				return list;
			}
		} catch (Exception e) {
			logger.error("解析返回竞彩篮球赛程数据错误!");
		}
		return null;
	}

    private Map<String, JclqRace> packDbRace(List<JclqRace> races) throws ApiRemoteCallFailedException {
        if (races == null || races.isEmpty()) {
            return null;
        }
        Set<String> phaseNoSet = new HashSet<String>();
        for (JclqRace r : races) {
            phaseNoSet.add(r.getPhase());
        }
        List<JclqRace> dbRaces = new ArrayList<JclqRace>();
        for (String d : phaseNoSet) {
            List<JclqRace> raceList = getRaceListByDateAndStatus(d, null, false);
            if (raceList != null && raceList.size() > 0) {
                dbRaces.addAll(raceList);
            }
        }
        if (dbRaces != null && !dbRaces.isEmpty()) {
            Map<String, JclqRace> dbRaceMap = new HashMap<String, JclqRace>();
            for (JclqRace dbRace : dbRaces) {
                dbRaceMap.put(dbRace.getMatchNum(), dbRace);
            }
            return dbRaceMap;
        }
        return null;
    }

    private boolean compareRace(JclqRace pageRace, JclqRace dbRace) {
		if (dbRace == null) {
			return saveRace(pageRace);
		} else {
			dbRace.setHomeTeam(pageRace.getHomeTeam());
			dbRace.setAwayTeam(pageRace.getAwayTeam());
			dbRace.setMatchName(pageRace.getMatchName());
			dbRace.setMatchDate(pageRace.getMatchDate());
			dbRace.setEndSaleTime(pageRace.getEndSaleTime());
			dbRace.setPriority(pageRace.getPriority());
			dbRace.setDynamicHandicap(pageRace.getDynamicHandicap());
			dbRace.setStaticHandicap(pageRace.getStaticHandicap());
			dbRace.setDynamicPresetScore(pageRace.getDynamicPresetScore());
			dbRace.setStaticPresetScore(pageRace.getStaticPresetScore());
			dbRace.setStaticSaleSfStatus(pageRace.getStaticSaleSfStatus());
			dbRace.setDynamicSaleSfStatus(pageRace.getDynamicSaleSfStatus());
			dbRace.setStaticSaleRfsfStatus(pageRace.getStaticSaleRfsfStatus());
			dbRace.setDynamicSaleRfsfStatus(pageRace.getDynamicSaleRfsfStatus());
			dbRace.setStaticSaleSfcStatus(pageRace.getStaticSaleSfcStatus());
			dbRace.setDynamicSaleSfcStatus(pageRace.getDynamicSaleSfcStatus());
			dbRace.setStaticSaleDxfStatus(pageRace.getStaticSaleDxfStatus());
			dbRace.setDynamicSaleDxfStatus(pageRace.getDynamicSaleDxfStatus());
			dbRace.setFxId(pageRace.getFxId());
			
			return updateRace(dbRace);
		}
	}
	private boolean compareRaceSp(JclqRace pageRace, JclqRace dbRace) {
		if (dbRace == null) {
			return false;
		}
		dbRace.setFirstQuarter(pageRace.getFirstQuarter());
		dbRace.setSecondQuarter(pageRace.getSecondQuarter());
		dbRace.setThirdQuarter(pageRace.getThirdQuarter());
		dbRace.setFourthQuarter(pageRace.getFourthQuarter());
		dbRace.setFinalScore(pageRace.getFinalScore());
		dbRace.setPrizeSf(pageRace.getPrizeSf());
		dbRace.setPrizeRfsf(pageRace.getPrizeRfsf());
		dbRace.setPrizeSfc(pageRace.getPrizeSfc());
		dbRace.setPrizeDxf(pageRace.getPrizeDxf());
		
		return updateRaceSp(dbRace);
	}
	
	@Override
	public ResultBean batchCreate(List<JclqRace> races) throws ApiRemoteCallFailedException {
		ResultBean resultBean = new ResultBean();
		StringBuffer failedTeam = null;
		
		Map<String, JclqRace> dbRaceMap = packDbRace(races);

		for (JclqRace race : races) {
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
			resultBean.setMessage("批量生成竞彩篮球对阵成功！");
		}
		return resultBean;
	}
	@Override
	public ResultBean batchCreateSp(List<JclqRace> races) throws ApiRemoteCallFailedException {
		ResultBean resultBean = new ResultBean();
		StringBuffer failedTeam = null;
		
		Map<String, JclqRace> dbRaceMap = packDbRace(races);

		for (JclqRace race : races) {
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
			resultBean.setMessage("批量生成竞彩篮球对阵成功！");
		}
		return resultBean;
	}
	
	@Override
	public boolean saveRace(JclqRace race) {
		logger.info("进入调用API生成竞彩篮球对阵");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_CREATE);
			createRequest.setParameterForUpdate(JclqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JclqRace.SET_PHASE, race.getPhase());
			createRequest.setParameterForUpdate(JclqRace.SET_OFFICIAL_DATE, DateUtil.formatDate(race.getOfficialDate()));
			createRequest.setParameterForUpdate(JclqRace.SET_OFFICIAL_NUM, race.getOfficialNum() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_END_SALE_DATE, DateUtil.formatDate(race.getEndSaleTime(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(JclqRace.SET_MATCH_DATE, DateUtil.formatDate(race.getMatchDate(),DateUtil.DATETIME));
			createRequest.setParameterForUpdate(JclqRace.SET_HOME_TEAM, race.getHomeTeam());
			createRequest.setParameterForUpdate(JclqRace.SET_AWAY_TEAM, race.getAwayTeam());
			createRequest.setParameterForUpdate(JclqRace.SET_MATCH_NAME, race.getMatchName());
			createRequest.setParameterForUpdate(JclqRace.SET_STATUS, race.getStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_PRIORITY, race.getPriority() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_EXT, race.getExt());
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_HANDICAP, race.getDynamicHandicap());
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_HANDICAP, race.getStaticHandicap());
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_PRESET_SCORE, race.getDynamicPresetScore());
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_PRESET_SCORE, race.getStaticPresetScore());
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_DRAW_STATUS, race.getStaticDrawStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_DRAW_STATUS, race.getDynamicDrawStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_FX_ID, race.getFxId() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_SALE_STATUS_SF, race.getDynamicSaleSfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_SALE_STATUS_SF, race.getStaticSaleSfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_SALE_STATUS_RFSF, race.getDynamicSaleRfsfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_SALE_STATUS_RFSF, race.getStaticSaleRfsfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_SALE_STATUS_SFC, race.getDynamicSaleSfcStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_SALE_STATUS_SFC, race.getStaticSaleSfcStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_SALE_STATUS_DXF, race.getDynamicSaleDxfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_SALE_STATUS_DXF, race.getStaticSaleDxfStatus().getValue() + "");
			
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!{}",race.getMatchNum(), e.getMessage());
			return false;
		}
		
		logger.info("生成竞彩篮球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API保存竞彩篮球数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("生成竞彩篮球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());

        if (response.getCode() != ApiConstant.RC_SUCCESS || response.getTotal() == 0) {
            logger.error("第{}场对阵信息存储失败, api response code = {}, message = {}, query = {}",
                    new Object[]{race.getMatchNum(), response.getCode(), response.getMessage(), createRequest.toQueryString()});
            return false;
        }
        logger.info("第{}场对阵信息存储成功!", race.getMatchNum());
		return true;
	}

	@Override
	public boolean updateRace(JclqRace race) {
		logger.info("进入调用API更新竞彩篮球对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_UPDATE);
			createRequest.setParameter(JclqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JclqRace.SET_HOME_TEAM, race.getHomeTeam());
			createRequest.setParameterForUpdate(JclqRace.SET_AWAY_TEAM, race.getAwayTeam());
			createRequest.setParameterForUpdate(JclqRace.SET_MATCH_NAME, race.getMatchName());
			createRequest.setParameterForUpdate(JclqRace.SET_MATCH_DATE, DateUtil.formatDate(race.getMatchDate(), DateUtil.DATETIME));
			createRequest.setParameterForUpdate(JclqRace.SET_END_SALE_DATE, DateUtil.formatDate(race.getEndSaleTime(), DateUtil.DATETIME));
			createRequest.setParameterForUpdate(JclqRace.SET_PRIORITY, race.getPriority() + "");
			createRequest.setParameterForUpdate(DcRace.SET_EXT, race.getExt());
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_HANDICAP, race.getDynamicHandicap());
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_HANDICAP, race.getStaticHandicap());
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_PRESET_SCORE, race.getDynamicPresetScore());
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_PRESET_SCORE, race.getStaticPresetScore());
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_SALE_STATUS_SF, race.getDynamicSaleSfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_SALE_STATUS_SF, race.getStaticSaleSfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_SALE_STATUS_RFSF, race.getDynamicSaleRfsfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_SALE_STATUS_RFSF, race.getStaticSaleRfsfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_SALE_STATUS_SFC, race.getDynamicSaleSfcStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_SALE_STATUS_SFC, race.getStaticSaleSfcStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_SALE_STATUS_DXF, race.getDynamicSaleDxfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_SALE_STATUS_DXF, race.getStaticSaleDxfStatus().getValue() + "");
			createRequest.setParameterForUpdate(JclqRace.SET_FX_ID, race.getFxId() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩篮球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩篮球数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩篮球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateRaceSp(JclqRace race) {
		logger.info("进入调用API更新竞彩篮球对阵信息");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_UPDATE);
			createRequest.setParameter(JclqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JclqRace.SET_FIRST_QUARTER, race.getFirstQuarter());
			createRequest.setParameterForUpdate(JclqRace.SET_SECOND_QUARTER, race.getSecondQuarter());
			createRequest.setParameterForUpdate(JclqRace.SET_THIRD_QUARTER, race.getThirdQuarter());
			createRequest.setParameterForUpdate(JclqRace.SET_FOURTH_QUARTER, race.getFourthQuarter());
			createRequest.setParameterForUpdate(JclqRace.SET_FINAL_SCORE, race.getFinalScore());
			createRequest.setParameterForUpdate(JclqRace.SET_PRIZE_SF, race.getPrizeSf());
			createRequest.setParameterForUpdate(JclqRace.SET_PRIZE_RFSF, race.getPrizeRfsf());
			createRequest.setParameterForUpdate(JclqRace.SET_PRIZE_SFC, race.getPrizeSfc());
			createRequest.setParameterForUpdate(JclqRace.SET_PRIZE_DXF, race.getPrizeDxf());
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败!",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩篮球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩篮球数据异常!{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩篮球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败!", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功!", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateRaceStatus(JclqRace race) {
		logger.info("进入调用API更新竞彩篮球状态");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_UPDATE);
			createRequest.setParameter(JclqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JclqRace.SET_STATUS, race.getStatus().getValue() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败！",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩篮球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩篮球数据异常！{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩篮球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败！", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功！", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateRaceStaticDrawStatus(JclqRace race) {
		logger.info("进入调用API更新竞彩篮球固定奖金开奖状态");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_UPDATE);
			createRequest.setParameter(JclqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JclqRace.SET_STATIC_DRAW_STATUS, race.getStaticDrawStatus().getValue() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败！",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩篮球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩篮球数据异常！{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩篮球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败！", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功！", race.getMatchNum());
		return true;
	}
	
	@Override
	public boolean updateRaceDynamicDrawStatus(JclqRace race) {
		logger.info("进入调用API更新竞彩篮球浮动奖金开奖状态");
		ApiRequest createRequest = new ApiRequest(); 
		try {
			createRequest.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_UPDATE);
			createRequest.setParameter(JclqRace.SET_MATCH_NUM, race.getMatchNum());
			createRequest.setParameterForUpdate(JclqRace.SET_DYNAMIC_DRAW_STATUS, race.getDynamicDrawStatus().getValue() + "");
		} catch (Exception e) {
			logger.error("第{}场对阵信息解析失败！",race.getMatchNum());
			return false;
		}
		
		logger.info("更新竞彩篮球对阵,api request String: {}", createRequest.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(createRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改竞彩篮球数据异常！{}", e.getMessage());
			return false;
		}
		logger.info("更新竞彩篮球对阵,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode()!=ApiConstant.RC_SUCCESS){
			logger.error("第{}场对阵信息更新失败！", race.getMatchNum());
			return false;
		}
		logger.info("第{}场对阵信息更新成功！", race.getMatchNum());
		return true;
	}
	
	@Override
	public List<JclqRace> getJclqRaceDrawReadyList(String phaseNo, List<JclqRaceStatus> statuses, JclqStaticDrawStatus staticDrawStatus, JclqDynamicDrawStatus dynamicDrawStatus, int max)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩篮球赛程数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_SEARCH);
		
		if(!StringUtils.isEmpty(phaseNo)){
			request.setParameter(JczqRace.QUERY_PHASE, phaseNo);
		}
		
		if (statuses != null && statuses.size() > 0) {
			List<String> list = new ArrayList<String>();
			for (JclqRaceStatus status : statuses) {
				list.add(status.getValue() + "");
			}
			request.setParameterIn(JclqRace.QUERY_STATUS, list);
		}
		if(staticDrawStatus != null){
			request.setParameter(JclqRace.QUERY_STATIC_DRAW_STATUS, String.valueOf(staticDrawStatus.getValue()));
		}
		if(dynamicDrawStatus != null){
			request.setParameter(JclqRace.QUERY_DYNAMIC_DRAW_STATUS, String.valueOf(dynamicDrawStatus.getValue()));
		}
		
		// order
		request.addOrder(JclqRace.ORDER_MATCH_NUM, ApiConstant.API_REQUEST_ORDER_ASC);
		
		request.setPage(1);
		request.setPagesize(max);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取竞彩篮球赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取竞彩篮球赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取竞彩篮球赛程数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取竞彩篮球赛程数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取竞彩篮球赛程数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<JclqRace> races = JclqRace.convertFromJSONArray(response.getData());
		if (races != null && races.size() > 0) {
			return races;
		}
		return null;
	}
	
	@Override
	public List<JclqRace> searchJclqRaceList(List<JclqRaceStatus> statusList, String orderStr, String orderView, Integer size) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩篮球对阵信息列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_SEARCH);
		//比赛状态
		if (statusList != null && !statusList.isEmpty()) {
			List<String> statusValueList = new ArrayList<String>();
			for (JclqRaceStatus status : statusList) {
				statusValueList.add(String.valueOf(status.getValue()));
			}
			request.setParameterIn(JclqRace.QUERY_STATUS, statusValueList);
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
		List<JclqRace> list = null;
		
		logger.info("根据比赛状态获取竞彩篮球对阵信息列表",request.toQueryString());
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩篮球对阵异常!{}", e.getMessage());
			throw new ApiRemoteCallFailedException("根据比赛状态获取竞彩篮球对阵信息列表发生API请求错误");
		}
		if(response == null || response.getCode() != ApiConstant.RC_SUCCESS){
			logger.info("API响应结果为空");
			return null;
		}
		try {
			list = JclqRace.convertFromJSONArray(response.getData());
		} catch (Exception e) {
			logger.error("解析返回竞彩篮球对阵数据错误!");
		}
		return list;
	}
	
	@Override
	public List<JclqRace> recommendJclqRace(Integer size){
		List<JclqRace> jclqRaces = null;
		if (size == null || size <= 0) {
			return jclqRaces; 
		}
		
		List<JclqRaceStatus> jclqRaceStatus = new ArrayList<JclqRaceStatus>();
		jclqRaceStatus.add(JclqRaceStatus.OPEN);
		jclqRaceStatus.add(JclqRaceStatus.UNOPEN);
		
		try {
			jclqRaces = searchJclqRaceList(jclqRaceStatus, JclqRace.ORDER_MATCH_DATE, ApiConstant.API_REQUEST_ORDER_ASC, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩篮球对阵异常！{}", e.getMessage());
			return null;
		}
		
		/**
		 * 已开售，未开售不够size场，取已结束的场次补全
		 */
		if (jclqRaces == null) {
			jclqRaces = new ArrayList<JclqRace>();
		}
		if (jclqRaces.size() < size) {
			try {
				jclqRaceStatus.clear();
				jclqRaceStatus.add(JclqRaceStatus.CLOSE);
				jclqRaceStatus.add(JclqRaceStatus.DRAW);
				jclqRaceStatus.add(JclqRaceStatus.REWARD);
				jclqRaceStatus.add(JclqRaceStatus.RESULT_SET);
				List<JclqRace> tempJclqRaces = searchJclqRaceList(jclqRaceStatus, JclqRace.ORDER_MATCH_DATE, ApiConstant.API_REQUEST_ORDER_DESC, size - jclqRaces.size());
				//倒序
				if (tempJclqRaces != null && !tempJclqRaces.isEmpty()) {
					Collections.reverse(tempJclqRaces);
					tempJclqRaces.addAll(jclqRaces);
					jclqRaces = tempJclqRaces;
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询竞彩篮球对阵异常！{}", e.getMessage());
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
	public int getJclqRaceSaleCount() {
		List<JclqRace> jclqRaces = null;
		
		List<JclqRaceStatus> jclqRaceStatus = new ArrayList<JclqRaceStatus>();
		jclqRaceStatus.add(JclqRaceStatus.OPEN);
		
		try {
			jclqRaces = searchJclqRaceList(jclqRaceStatus, JczqRace.ORDER_MATCH_DATE, ApiConstant.API_REQUEST_ORDER_ASC, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩足球对阵异常！{}", e.getMessage());
			return 0;
		}
		if (jclqRaces != null) {
			return jclqRaces.size();
		}
		return 0;
	}
	
	//按优先级排序
	private List<JclqRace> orderPriority(List<JclqRace> jclqRaces) {
		if (jclqRaces == null || jclqRaces.isEmpty()) {
			return null;
		}
		for (int i = 0; i < jclqRaces.size(); i++) {
			for (int j = i; j < jclqRaces.size(); j++) {
				//优先级高排在前面，否则场次靠前排前面
				if (jclqRaces.get(i).getPriority() < jclqRaces.get(j).getPriority()) {
					JclqRace tempDcRace = jclqRaces.get(i);
					jclqRaces.set(i, jclqRaces.get(j));
					jclqRaces.set(j, tempDcRace);
				} else if (jclqRaces.get(j).getOfficialDate().before(jclqRaces.get(i).getOfficialDate())) {
					JclqRace tempDcRace = jclqRaces.get(i);
					jclqRaces.set(i, jclqRaces.get(j));
					jclqRaces.set(j, tempDcRace);
				} else if (jclqRaces.get(j).getOfficialDate().equals((jclqRaces.get(i).getOfficialDate()))) {
					if (jclqRaces.get(j).getOfficialNum() < jclqRaces.get(i).getOfficialNum()) {
						JclqRace tempDcRace = jclqRaces.get(i);
						jclqRaces.set(i, jclqRaces.get(j));
						jclqRaces.set(j, tempDcRace);
					}
				}
			}
		}
		return jclqRaces;
	}
	
	@Override
	public Map<String, Map<String, String>> getJclqCurrentStaticSp(List<String> matchNums, LotteryType lotteryType) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询竞彩篮球胜负固定奖金及时sp数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERYDATA_JCLQ_STATIC_SP);
		request.setParameter("lottery_type", lotteryType.getValue() + "");
		if (matchNums != null && matchNums.size() > 0) {
			request.setParameterIn(JclqRace.QUERY_MATCH_NUM, matchNums);
		}
		
		ApiResponse response = null;
		
		logger.info("根据比赛状态获取竞彩篮球对阵信息列表",request.toQueryString());
		try {
			response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询竞彩篮球对阵异常！{}", e.getMessage());
			throw new ApiRemoteCallFailedException("根据比赛状态获取竞彩篮球对阵信息列表发生API请求错误");
		}
		if(response == null || response.getCode() != ApiConstant.RC_SUCCESS){
			logger.info("API响应结果为空");
			return null;
		}
		if (response.getData() == null || response.getData().size() == 0) {
			logger.warn("API查询竞彩篮球对阵数据为空!");
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
	
	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
}
