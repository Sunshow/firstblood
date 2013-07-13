package web.service.impl.event;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.EuroCupService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.event.EuroCupChat;
import com.lehecai.core.api.event.EuroCupDraw;
import com.lehecai.core.api.event.EuroCupMatch;
import com.lehecai.core.api.event.EuroCupOrder;
import com.lehecai.core.api.event.EuroCupTeam;
import com.lehecai.core.event.EuroCupOrderStatus;
import com.lehecai.core.event.EuroCupType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 欧洲杯信息管理业务逻辑层实现类
 * @author chirowong
 *
 */
public class EuroCupServiceImpl implements EuroCupService {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public Map<String, Object> findTeamList(PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询欧洲杯球队数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_TEAM);
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取欧洲杯球队数据失败");
			throw new ApiRemoteCallFailedException("API获取欧洲杯球队数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取欧洲杯球队数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取欧洲杯球队数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取欧洲杯球队数据为空, message={}", response.getMessage());
			return null;
		}
		List<EuroCupTeam> euroCupTeamList = EuroCupTeam.convertFromJSONArray(response.getData());
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, euroCupTeamList);
		return map;
	}

	@Override
	public boolean updateTeamSp(EuroCupTeam euroCupTeam)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新欧洲杯球队SP值");
		if(euroCupTeam == null){
			logger.error("修改的球队SP信息为空");
			return false;
		}
		
		if(euroCupTeam.getTeamId() == 0){
			logger.error("球队编码为空");
			return false;
		}
		ApiRequest request = new ApiRequest();
		try {
			request.setUrl(ApiConstant.API_URL_EURO_CUP_TEAM_UPDATE);
			request.setParameter(EuroCupTeam.QUERY_TEAM_ID, String.valueOf(euroCupTeam.getTeamId()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP1_STATUS,String.valueOf(euroCupTeam.getTop1Status()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP1_SP,String.valueOf(euroCupTeam.getTop1Sp()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP1_SP_TREND,String.valueOf(euroCupTeam.getTop1SpTrend()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP4_STATUS,String.valueOf(euroCupTeam.getTop4Status()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP4_SP,String.valueOf(euroCupTeam.getTop4Sp()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP4_SP_TREND,String.valueOf(euroCupTeam.getTop4SpTrend()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP8_STATUS,String.valueOf(euroCupTeam.getTop8Status()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP8_SP,String.valueOf(euroCupTeam.getTop8Sp()));
			request.setParameterForUpdate(EuroCupTeam.SET_TOP8_SP_TREND,String.valueOf(euroCupTeam.getTop8SpTrend()));
			request.setParameterForUpdate(EuroCupTeam.SET_ADMIN_ID, String.valueOf(euroCupTeam.getUserId()));
		} catch (Exception e) {
			logger.error("更新球队{}SP信息解析失败!",euroCupTeam.getTeamName());
			return false;
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API响应结果为空");
			return false;
		}
		logger.info("更新球队SP信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API更新球队{}SP值请求异常", euroCupTeam.getTeamName());
			return false;
		}
		logger.info("球队{}SP信息更新成功!", euroCupTeam.getTeamName());
		return true;
	}
	
	@Override
	public ResultBean batchUpdateTeamSp(List<EuroCupTeam> euroCupTeams) {
		ResultBean resultBean = new ResultBean();
		StringBuffer faildTeam = null;
		
		for (EuroCupTeam euroCupTeam : euroCupTeams) {
			try {
				if (!updateTeamSp(euroCupTeam)) {
					if (faildTeam == null) {
						faildTeam = new StringBuffer();
					}
					faildTeam.append(euroCupTeam.getTeamName()).append(",");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API调用更新单场结果Sp值异常！{}", e.getMessage());
				if (faildTeam == null) {
					faildTeam = new StringBuffer();
				}
				faildTeam.append(euroCupTeam.getTeamName()).append(",");
			}
		}
		
		if (faildTeam != null) {
			resultBean.setResult(false);
			resultBean.setMessage("球队：["+faildTeam.substring(0, faildTeam.length()-1)+"]结果SP值更新失败！");
		} else {
			resultBean.setResult(true);
			resultBean.setMessage("批量更新欧洲杯球队SP值成功！");
		}
		return resultBean;
	}
	
	@Override
	public Map<String, Object> findMatchList(PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询欧洲杯球队数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_MATCH);
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取欧洲杯赛程数据失败");
			throw new ApiRemoteCallFailedException("API获取欧洲杯赛程数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取欧洲杯赛程数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取欧洲杯赛程数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取欧洲杯赛程数据为空, message={}", response.getMessage());
			return null;
		}
		List<EuroCupMatch> euroCupMatchList = EuroCupMatch.convertFromJSONArray(response.getData());
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, euroCupMatchList);
		return map;
	}
	
	@Override
	public boolean updateMatch(EuroCupMatch euroCupMatch)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新欧洲杯赛程信息");
		if(euroCupMatch == null){
			logger.error("修改的赛程信息为空");
			return false;
		}
		
		if(euroCupMatch.getMatchNum() == 0){
			logger.error("赛程编码为空");
			return false;
		}
		ApiRequest request = new ApiRequest();
		try {
			request.setUrl(ApiConstant.API_URL_EURO_CUP_MATCH_UPDATE);
			request.setParameter(EuroCupMatch.QUERY_MATCH_NUM, String.valueOf(euroCupMatch.getMatchNum()));
			request.setParameterForUpdate(EuroCupMatch.SET_MATCH_DATE, String.valueOf(euroCupMatch.getMatchDate()));
			request.setParameterForUpdate(EuroCupMatch.SET_HOME_TEAM, String.valueOf(euroCupMatch.getHomeTeam()));
			request.setParameterForUpdate(EuroCupMatch.SET_HOME_TEAM_ID, String.valueOf(euroCupMatch.getHomeTeamId()));
			request.setParameterForUpdate(EuroCupMatch.SET_AWAY_TEAM, String.valueOf(euroCupMatch.getAwayTeam()));
			request.setParameterForUpdate(EuroCupMatch.SET_AWAY_TEAM_ID, String.valueOf(euroCupMatch.getAwayTeamId()));
			request.setParameterForUpdate(EuroCupMatch.SET_HANDICAP, String.valueOf(euroCupMatch.getHandicap()));
			request.setParameterForUpdate(EuroCupMatch.SET_FINAL_SCORE, String.valueOf(euroCupMatch.getFinalScore()));
			request.setParameterForUpdate(EuroCupMatch.SET_STATUS, String.valueOf(euroCupMatch.getStatus()));
			request.setParameterForUpdate(EuroCupMatch.SET_CHATROOM_STATUS, String.valueOf(euroCupMatch.getChatroomStatus()));
			request.setParameterForUpdate(EuroCupMatch.SET_FX_ID, String.valueOf(euroCupMatch.getFxId()));
			request.setParameterForUpdate(EuroCupMatch.SET_DC_PHASE, euroCupMatch.getDcPhase());
			request.setParameterForUpdate(EuroCupMatch.SET_DC_MATCH_NUM, euroCupMatch.getDcMatchNum());
			request.setParameterForUpdate(EuroCupMatch.SET_JC_MATCH_NUM, euroCupMatch.getJcMatchNum());
			request.setParameterForUpdate(EuroCupMatch.SET_JC_HANDICAP, euroCupMatch.getJcHandicap());
			request.setParameterForUpdate(EuroCupMatch.SET_JC_FX_ID, String.valueOf(euroCupMatch.getJcFxId()));
			request.setParameterForUpdate(EuroCupMatch.SET_VIDEO_URL, String.valueOf(euroCupMatch.getVideoUrl()));
			request.setParameterForUpdate(EuroCupMatch.SET_TEXT_URL, String.valueOf(euroCupMatch.getTextUrl()));
			request.setParameterForUpdate(EuroCupMatch.SET_ADMIN_ID, String.valueOf(euroCupMatch.getUserId()));
		} catch (Exception e) {
			logger.error("更新赛程{}信息解析失败!",euroCupMatch.getMatchNum());
			return false;
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API响应结果为空");
			return false;
		}
		logger.info("更新赛程信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API更新赛程{}SP值请求异常", euroCupMatch.getMatchNum());
			return false;
		}
		logger.info("赛程{}信息更新成功!", euroCupMatch.getMatchNum());
		return true;
	}
	
	@Override
	public ResultBean batchUpdateMatch(List<EuroCupMatch> euroCupMatchs) {
		ResultBean resultBean = new ResultBean();
		StringBuffer faildTeam = null;
		
		for (EuroCupMatch euroCupMatch : euroCupMatchs) {
			try {
				if (!updateMatch(euroCupMatch)) {
					if (faildTeam == null) {
						faildTeam = new StringBuffer();
					}
					faildTeam.append(euroCupMatch.getMatchNum()).append(",");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API调用更新欧洲杯单场赛程信息异常！{}", e.getMessage());
				if (faildTeam == null) {
					faildTeam = new StringBuffer();
				}
				faildTeam.append(euroCupMatch.getMatchNum()).append(",");
			}
		}
		
		if (faildTeam != null) {
			resultBean.setResult(false);
			resultBean.setMessage("赛程：["+faildTeam.substring(0, faildTeam.length()-1)+"]更新失败！");
		} else {
			resultBean.setResult(true);
			resultBean.setMessage("批量更新欧洲杯赛程信息成功！");
		}
		return resultBean;
	}
	
	@Override
	public ResultBean updateMatchSp(EuroCupMatch euroCupMatch)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新欧洲杯赛程SP值");
		ResultBean resultBean = new ResultBean();
		if(euroCupMatch == null){
			logger.error("修改的赛程SP信息为空");
			resultBean.setResult(false);
			resultBean.setMessage("修改的赛程SP信息为空！");
			return resultBean;
		}
		
		if(euroCupMatch.getMatchNum() == 0){
			logger.error("赛程编码为空");
			resultBean.setResult(false);
			resultBean.setMessage("赛程编码为空！");
			return resultBean;
		}
		ApiRequest request = new ApiRequest();
		try {
			request.setUrl(ApiConstant.API_URL_EURO_CUP_UPDATE_SP);
			request.setParameter(EuroCupMatch.QUERY_MATCH_NUM, String.valueOf(euroCupMatch.getMatchNum()));
			request.setParameterForUpdate(EuroCupMatch.SET_ADMIN_ID, String.valueOf(euroCupMatch.getUserId()));
		} catch (Exception e) {
			logger.error("更新赛程{}SP信息解析失败!",euroCupMatch.getMatchNum());
			resultBean.setResult(false);
			resultBean.setMessage("更新赛程{}SP信息解析失败!"+e.getMessage());
			return resultBean;
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API响应结果为空");
			resultBean.setResult(false);
			resultBean.setMessage("API响应结果为空!");
			return resultBean;
		}
		logger.info("更新赛程SP信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API更新赛程{}SP值请求异常", euroCupMatch.getMatchNum());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
			return resultBean;
		}
		logger.info("赛程{}SP信息更新成功!", euroCupMatch.getMatchNum());
		resultBean.setResult(true);
		return resultBean;
	}
	
	@Override
	public ResultBean prize(String euroCupTeamIds,Integer type){
		logger.info("进入调用API欧洲杯开奖");
		ResultBean resultBean = new ResultBean();
		ApiRequest request = new ApiRequest();
		switch(type.intValue()){
		case 1:
			request.setUrl(ApiConstant.API_URL_EURO_CUP_PRIZE_TOP1);
			break;
		case 2:
			request.setUrl(ApiConstant.API_URL_EURO_CUP_PRIZE_TOP4);
			break;
		case 3:
			request.setUrl(ApiConstant.API_URL_EURO_CUP_PRIZE_TOP8);
			break;
		}
		String[] arrEuroCupTeamIds = euroCupTeamIds.split(",");
		JSONArray arr = new JSONArray();
		for(String s : arrEuroCupTeamIds){
			arr.add(s);
		}
		request.setParameterForUpdate("team_ids", arr.toString());
		logger.info("Request Query String: {}",request.toQueryString());
		ApiResponse response = null;
		try{
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		}catch(ApiRemoteCallFailedException e){
			logger.error("API调用更新单场结果Sp值异常！{}", e.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage("API调用更新单场结果Sp值异常！"+e.getMessage());
			return resultBean;
		}
		if(response == null){
			logger.error("API欧洲杯开奖失败");
			resultBean.setResult(false);
			resultBean.setMessage("API欧洲杯开奖失败");
			return resultBean;
		}
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("调用API获取欧洲杯开奖数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage("调用API获取欧洲杯开奖数据请求异常！"+response.getMessage());
			return resultBean;
		}else{
			JSONArray array = response.getData();
			Boolean result = (Boolean)array.get(0);
			resultBean.setResult(result);
			resultBean.setMessage(response.getMessage());
			return resultBean;
		}
	}

	@Override
	public Map<String, Object> findOrderList(EuroCupOrder euroCupOrder, Date beginDate, Date endDate,
			String orderStr, String orderView, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询欧洲杯订单数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_ORDER_QUERY);
		Long _orderId = euroCupOrder.getOrderId(); 
		if(_orderId != null && !"".equals(_orderId)){
			request.setParameter(EuroCupOrder.QUERY_ORDER_ID, euroCupOrder.getOrderId()+"");
		}
		Long _uid = euroCupOrder.getUserId(); 
		if(_uid != null && _uid.longValue() != 0){
			request.setParameter(EuroCupOrder.QUERY_USER_ID, euroCupOrder.getUserId()+"");
		}
		EuroCupType _type = euroCupOrder.getType(); 
		if(_type != null && _type != EuroCupType.ALL){
			request.setParameter(EuroCupOrder.QUERY_TYPE, _type.getValue()+"");
		}
		EuroCupOrderStatus _status = euroCupOrder.getPrizeStatus(); 
		if(_status != null && _status != EuroCupOrderStatus.ALL){
			request.setParameter(EuroCupOrder.QUERY_PRIZE_STATUS, _status.getValue()+"");
		}
		if (beginDate != null) {
			request.setParameterBetween(EuroCupOrder.QUERY_CREATE_TIME, DateUtil.formatDate(beginDate,DateUtil.DATETIME),null);
		}
		if (endDate != null) {
			request.setParameterBetween(EuroCupOrder.QUERY_CREATE_TIME, null,DateUtil.formatDate(endDate,DateUtil.DATETIME));
		}
		if (orderStr != null && !"".equals(orderView) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);
		}else{
			request.addOrder(EuroCupOrder.QUERY_CREATE_TIME,ApiConstant.API_REQUEST_ORDER_DESC);
		}
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取欧洲杯订单数据失败");
			throw new ApiRemoteCallFailedException("API获取欧洲杯订单数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取欧洲杯订单数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取欧洲杯订单数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取欧洲杯订单数据为空, message={}", response.getMessage());
			return null;
		}
		List<EuroCupOrder> euroCupOrderList = EuroCupOrder.convertFromJSONArray(response.getData());
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, euroCupOrderList);
		return map;
	}
	
	@Override
	public ResultBean payout(){
		logger.info("进入调用API欧洲杯派奖");
		ResultBean resultBean = new ResultBean();
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_PAYOUT);
		logger.info("Request Query String: {}",request.toQueryString());
		ApiResponse response = null;
		try{
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		}catch(ApiRemoteCallFailedException e){
			logger.error("API调用欧洲杯派奖异常！{}", e.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage("API调用欧洲杯派奖异常！"+e.getMessage());
			return resultBean;
		}
		if(response == null){
			logger.error("API欧洲杯派奖失败");
			resultBean.setResult(false);
			resultBean.setMessage("API欧洲杯派奖失败");
			return resultBean;
		}
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("调用API获取欧洲杯派奖数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage("调用API获取欧洲杯派奖数据请求异常！"+response.getMessage());
			return resultBean;
		}else{
			JSONArray array = response.getData();
			Boolean result = (Boolean)array.get(0);
			resultBean.setResult(result);
			return resultBean;
		}
	}

	@Override
	public ResultBean prizeSfp(EuroCupMatch euroCupMatch)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API单场金币竞猜胜平负开奖");
		ResultBean resultBean = new ResultBean();
		if(euroCupMatch == null){
			logger.error("赛程信息为空");
			resultBean.setResult(false);
			resultBean.setMessage("赛程信息为空！");
			return resultBean;
		}
		
		if(euroCupMatch.getMatchNum() == 0){
			logger.error("赛程编码为空");
			resultBean.setResult(false);
			resultBean.setMessage("赛程编码为空！");
			return resultBean;
		}
		ApiRequest request = new ApiRequest();
		try {
			request.setUrl(ApiConstant.API_URL_EURO_CUP_PRIZE_SFP);
			request.setParameterForUpdate(EuroCupMatch.QUERY_MATCH_NUM, String.valueOf(euroCupMatch.getMatchNum()));
		} catch (Exception e) {
			logger.error("单场金币竞猜胜平负开奖失败!，原因：{}",euroCupMatch.getMatchNum());
			resultBean.setResult(false);
			resultBean.setMessage("单场金币竞猜胜平负开奖失败!，原因：{}"+e.getMessage());
			return resultBean;
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API响应结果为空");
			resultBean.setResult(false);
			resultBean.setMessage("API响应结果为空!");
			return resultBean;
		}
		logger.info("单场金币竞猜胜平负开奖,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API单场[{}]金币竞猜胜平负开奖请求异常", euroCupMatch.getMatchNum());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
			return resultBean;
		}else{
			JSONArray array = response.getData();
			Boolean result = (Boolean)array.get(0);
			resultBean.setResult(result);
			return resultBean;
		}
	}

	@Override
	public ResultBean prizeBf(EuroCupMatch euroCupMatch)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API单场金币竞猜比分开奖");
		ResultBean resultBean = new ResultBean();
		if(euroCupMatch == null){
			logger.error("赛程信息为空");
			resultBean.setResult(false);
			resultBean.setMessage("赛程信息为空！");
			return resultBean;
		}
		
		if(euroCupMatch.getMatchNum() == 0){
			logger.error("赛程编码为空");
			resultBean.setResult(false);
			resultBean.setMessage("赛程编码为空！");
			return resultBean;
		}
		ApiRequest request = new ApiRequest();
		try {
			request.setUrl(ApiConstant.API_URL_EURO_CUP_PRIZE_BF);
			request.setParameterForUpdate(EuroCupMatch.QUERY_MATCH_NUM, String.valueOf(euroCupMatch.getMatchNum()));
		} catch (Exception e) {
			logger.error("单场金币竞猜比分开奖失败!，原因：{}",euroCupMatch.getMatchNum());
			resultBean.setResult(false);
			resultBean.setMessage("单场金币竞猜比分开奖失败!，原因：{}"+e.getMessage());
			return resultBean;
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API响应结果为空");
			resultBean.setResult(false);
			resultBean.setMessage("API响应结果为空!");
			return resultBean;
		}
		logger.info("单场金币竞猜比分开奖,api response code = {}, message = {}", response.getCode(), response.getMessage());
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API单场[{}]金币竞猜比分开奖请求异常", euroCupMatch.getMatchNum());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
			return resultBean;
		}else{
			JSONArray array = response.getData();
			Boolean result = (Boolean)array.get(0);
			resultBean.setResult(result);
			return resultBean;
		}
	}

	@Override
	public EuroCupDraw getPrizeResult() throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取欧洲杯开奖状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_RESULT_RPIZE);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取欧洲杯开奖状态失败");
			throw new ApiRemoteCallFailedException("API获取欧洲杯开奖状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取欧洲杯开奖状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取欧洲杯开奖状态请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取欧洲杯开奖状态为空, message={}", response.getMessage());
			return null;
		}
		List<EuroCupDraw> euroCupDrawList = EuroCupDraw.convertFromJSONArray(response.getData());
		if(euroCupDrawList != null && euroCupDrawList.size() > 0){
			return euroCupDrawList.get(0);
		}else{
			return null;
		}
	}

	@Override
	public ResultBean presentCoin(Integer amount,Long adminId,String remark,Long userId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API派送金币");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_PRESENT_COIN);
		request.setParameterForUpdate("amount", String.valueOf(amount));
		request.setParameterForUpdate("admin_id", String.valueOf(adminId));
		request.setParameterForUpdate("remark", remark);
		request.setParameter("uid", String.valueOf(userId));
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		ResultBean resultBean = new ResultBean();
		if (response == null) {
			logger.error("API派送金币失败");
			throw new ApiRemoteCallFailedException("API派送金币失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API派送金币失败请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API派送金币失败请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取欧洲杯赛程数据为空, message={}", response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
		}
		JSONArray array = response.getData();
		Boolean result = (Boolean)array.get(0);
		resultBean.setResult(result);
		return resultBean;
	}
	
	@Override
	public ResultBean flushPrize() throws ApiRemoteCallFailedException {
		logger.info("进入调用API清空开奖状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_PRIZE_FLUSH);
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		ResultBean resultBean = new ResultBean();
		if (response == null) {
			logger.error("API清空开奖状态失败");
			throw new ApiRemoteCallFailedException("API清空开奖状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API清空开奖状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API清空开奖状态失败请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API清空开奖状态程数据为空, message={}", response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
		}
		JSONArray array = response.getData();
		Boolean result = (Boolean)array.get(0);
		resultBean.setResult(result);
		return resultBean;
	}

	@Override
	public ResultBean getMode() throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取模式");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_GET_MODE);
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		ResultBean resultBean = new ResultBean();
		if (response == null) {
			logger.error("API获取模式失败");
			throw new ApiRemoteCallFailedException("API获取模式失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取模式请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取模式请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取模式数据为空, message={}", response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
			return resultBean;
		}
		JSONArray array = response.getData();
		String data = (String)array.get(0);
		resultBean.setData(data);
		return resultBean;
	}

	@Override
	public ResultBean setMode(String mode) throws ApiRemoteCallFailedException {
		logger.info("进入调用API设置模式");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_SET_MODE);
		request.setParameterForUpdate("mode", mode);
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		ResultBean resultBean = new ResultBean();
		if (response == null) {
			logger.error("API设置模式失败");
			throw new ApiRemoteCallFailedException("API设置模式失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API设置模式请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API设置模式请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API设置模式数据为空, message={}", response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
		}
		JSONArray array = response.getData();
		Boolean result = (Boolean)array.get(0);
		resultBean.setResult(result);
		return resultBean;
	}

	@Override
	public ResultBean chatAdd(Long userId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API增加禁言用户");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_CHAT_ADD);
		request.setParameterForUpdate(EuroCupChat.SET_UID, String.valueOf(userId));
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		ResultBean resultBean = new ResultBean();
		if (response == null) {
			logger.error("API增加禁言用户失败");
			throw new ApiRemoteCallFailedException("API增加禁言用户失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API增加禁言用户请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API增加禁言用户请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API增加禁言用户数据为空, message={}", response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
			return resultBean;
		}
		JSONArray array = response.getData();
		Boolean data = (Boolean)array.get(0);
		resultBean.setResult(data);
		return resultBean;
	}

	@Override
	public ResultBean chatDelete(Long userId)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除禁言用户");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_CHAT_DELETE);
		request.setParameter(EuroCupChat.SET_UID, String.valueOf(userId));
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		ResultBean resultBean = new ResultBean();
		if (response == null) {
			logger.error("API删除禁言用户失败");
			throw new ApiRemoteCallFailedException("API删除禁言用户失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除禁言用户请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API删除禁言用户请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API删除禁言用户数据为空, message={}", response.getMessage());
			resultBean.setResult(false);
			resultBean.setMessage(response.getMessage());
			return resultBean;
		}
		JSONArray array = response.getData();
		Boolean data = (Boolean)array.get(0);
		resultBean.setResult(data);
		return resultBean;
	}

	@Override
	public Map<String, Object> chatList(Long userId, String userName, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取禁言用户列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EURO_CUP_CHAT_LIST);
		if(userId != null){
			request.setParameter(EuroCupChat.QUERY_UID, String.valueOf(userId));
		}
		if(!StringUtils.isEmpty(userName)){
			request.setParameter(EuroCupChat.QUERY_USERNAME, userName);
		}
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取禁言用户列表失败");
			throw new ApiRemoteCallFailedException("API获取禁言用户列表失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取禁言用户列表请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取禁言用户列表请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取禁言用户列表为空, message={}", response.getMessage());
			return null;
		}
		List<EuroCupChat> euroCupChatList = EuroCupChat.convertFromJSONArray(response.getData());
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, euroCupChatList);
		return map;
	}
	
	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}
}
