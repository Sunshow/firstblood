/**
 * 
 */
package web.service.impl.game;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.game.GameOrderService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.game.GameOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.game.GameOrderStatus;
import com.lehecai.core.game.GameRechargeType;

/**
 * @author chirowong
 *
 */
public class GameOrderServiceImpl implements GameOrderService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ApiRequestService gameApiWriteRequestService;
	
	private ApiRequestService gameApiRequestService;
	
	@Override
	public Map<String, Object> queryGameOrderList(
			GameOrder gameOrder, Date beginTime, Date endTime,
			String orderStr, String orderView, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用游戏大厅订单查询API");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GAME_ORDER_QUERY);
		String _orderId = gameOrder.getOrderId(); 
		if(_orderId != null && !"".equals(_orderId)){
			request.setParameter(GameOrder.QUERY_ORDER_ID, gameOrder.getOrderId());
		}
		String _username = gameOrder.getUserName(); 
		if(_username != null && !"".equals(_username)){
			request.setParameter(GameOrder.QUERY_USER_NAME, gameOrder.getUserName());
		}
		Integer _game_id = gameOrder.getGameId();
		if(_game_id != null && _game_id.intValue() != 0){
			request.setParameter(GameOrder.QUERY_GAME_ID, gameOrder.getGameId()+"");
		}
		GameOrderStatus _status = gameOrder.getGameOrderStatus(); 
		if(_status != null && _status != GameOrderStatus.ALL){
			request.setParameter(GameOrder.QUERY_ORDER_STATUS, _status.getValue()+"");
		}
		Long _source = gameOrder.getSource();
		if(_source != null && _source != 0){
			request.setParameter(GameOrder.QUERY_SOURCE, gameOrder.getSource()+"");
		}
		GameRechargeType _type = gameOrder.getGameRechargeType();
		if(_type != null && _type != GameRechargeType.ALL){
			request.setParameter(GameOrder.QUERY_RECHARGE_TYPE, _type.getValue()+"");
		}
		if (beginTime != null) {
			request.setParameterBetween(GameOrder.QUERY_CREATETIME, DateUtil.formatDate(beginTime,DateUtil.DATETIME),null);
		}
		if (endTime != null) {
			request.setParameterBetween(GameOrder.QUERY_CREATETIME, null,DateUtil.formatDate(endTime,DateUtil.DATETIME));
		}
		if (orderStr != null && !"".equals(orderView) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);
		}else{
			request.addOrder(GameOrder.ORDER_CREATETIME,ApiConstant.API_REQUEST_ORDER_DESC);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = gameApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用游戏大厅订单查询API失败");
			throw new ApiRemoteCallFailedException("调用游戏大厅订单查询API失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用游戏大厅订单查询API请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用游戏大厅订单查询API请求出错," + response.getMessage());
		}
		logger.info("结束调用游戏大厅订单查询API");
		List<GameOrder> gameOrderList = GameOrder.convertFromJSONArray(response.getData());
		
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
		map.put(Global.API_MAP_KEY_LIST, gameOrderList);
		
		return map;
	}
	
	@Override
	public Map<String, Object> queryGameOrderTotal(
			GameOrder gameOrder, Date beginTime, Date endTime)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用游戏大厅汇总查询API");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GAME_ORDER_SUM);
		String _orderId = gameOrder.getOrderId(); 
		if(_orderId != null && !"".equals(_orderId)){
			request.setParameter(GameOrder.QUERY_ORDER_ID, gameOrder.getOrderId());
		}
		String _username = gameOrder.getUserName(); 
		if(_username != null && !"".equals(_username)){
			request.setParameter(GameOrder.QUERY_USER_NAME, gameOrder.getUserName());
		}
		Integer _game_id = gameOrder.getGameId();
		if(_game_id != null && _game_id.intValue() != 0){
			request.setParameter(GameOrder.QUERY_GAME_ID, gameOrder.getGameId()+"");
		}
		GameOrderStatus _status = gameOrder.getGameOrderStatus(); 
		if(_status != null && _status != GameOrderStatus.ALL){
			request.setParameter(GameOrder.QUERY_ORDER_STATUS, _status.getValue()+"");
		}
		Long _source = gameOrder.getSource();
		if(_source != null && _source != 0){
			request.setParameter(GameOrder.QUERY_SOURCE, gameOrder.getSource()+"");
		}
		GameRechargeType _type = gameOrder.getGameRechargeType();
		if(_type != null && _type != GameRechargeType.ALL){
			request.setParameter(GameOrder.QUERY_RECHARGE_TYPE, _type.getValue()+"");
		}
		if (beginTime != null) {
			request.setParameterBetween(GameOrder.QUERY_CREATETIME, DateUtil.formatDate(beginTime,DateUtil.DATETIME),null);
		}
		if (endTime != null) {
			request.setParameterBetween(GameOrder.QUERY_CREATETIME, null,DateUtil.formatDate(endTime,DateUtil.DATETIME));
		}

		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = gameApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用游戏大厅汇总查询API失败");
			throw new ApiRemoteCallFailedException("调用游戏大厅汇总查询API失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用游戏大厅汇总查询API请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用游戏大厅汇总查询API请求出错," + response.getMessage());
		}
		logger.info("结束调用游戏大厅汇总查询API");
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObj = response.getData().getJSONObject(0);
		
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("cash") != null) {
			map.put("cash", jsonObj.get("cash"));
		} else {
			map.put("cash", "0");
		}
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("credit") != null) {
			map.put("credit", jsonObj.get("credit"));
		} else {
			map.put("credit", "0");
		}
		return map;
	}

	@Override
	public void updateGameOrder(GameOrder gameOrder)
			throws ApiRemoteCallFailedException {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GAME_ORDER_UPDATE);
		if (gameOrder.getOrderId() != null || !gameOrder.getOrderId().equals("")) {
			request.setParameter(GameOrder.ORDER_ID, gameOrder.getOrderId());
		} else {
			logger.error("订单ID为空");
			throw new ApiRemoteCallFailedException("订单ID为空");
		}
		if (gameOrder.getGameOrderStatus() != null && (gameOrder.getGameOrderStatus().getValue() == GameOrderStatus.RECHARGED.getValue() || gameOrder.getGameOrderStatus().getValue() == GameOrderStatus.ERROR.getValue())) {
			request.setParameterForUpdate(GameOrder.QUERY_ORDER_STATUS,gameOrder.getGameOrderStatus().getValue() + "");
		} else {
			logger.error("订单状态错误,只允许更改为“三方充值成功”或“三方充值失败”");
			throw new ApiRemoteCallFailedException("订单状态错误,只允许更改为“三方充值成功”或“三方充值失败”");
		}
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = gameApiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更改订单状态失败");
			throw new ApiRemoteCallFailedException("API更改订单状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API更改订单状态请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API更改订单状态请求出错," + response.getMessage());
		}
		logger.info("结束调用API取消套餐列表");
	}

	public void setGameApiRequestService(ApiRequestService gameApiRequestService) {
		this.gameApiRequestService = gameApiRequestService;
	}

	public ApiRequestService getGameApiRequestService() {
		return gameApiRequestService;
	}

	public void setGameApiWriteRequestService(ApiRequestService gameApiWriteRequestService) {
		this.gameApiWriteRequestService = gameApiWriteRequestService;
	}

	public ApiRequestService getGameApiWriteRequestService() {
		return gameApiWriteRequestService;
	}
}
