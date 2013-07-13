/**
 * 
 */
package web.action.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.game.GameOrderService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.game.GameOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.game.GameOrderStatus;
import com.lehecai.core.game.GameRechargeType;
import com.opensymphony.xwork2.Action;

/**
 * 
 * @author He Wang
 *
 */
public class GameOrderExceptionHandleAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<GameOrder> gameOrderList;
	private GameOrder gameOrder;
	private Date beginTime;
	private Date endTime;
	private String orderStr;
	private String orderView;
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	private int status = GameOrderStatus.RECHARGE.getValue();
	private int type = GameRechargeType.ALL.getValue();
	private String cashTotal;
	private String creditTotal;
	
	private GameOrderService gameOrderService;
	
	public String handle(){
		logger.info("进入游戏充值订单查询");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入游戏异常订单查询");
		Map<String, Object> map = null;
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			if(gameOrder == null) {
				gameOrder = new GameOrder();
			}
			GameOrderStatus gameOrderStatus = GameOrderStatus.getItem(status);
			gameOrder.setGameOrderStatus(gameOrderStatus);
			GameRechargeType gameRechargeType = GameRechargeType.getItem(type);
			gameOrder.setGameRechargeType(gameRechargeType);
			
			map = gameOrderService.queryGameOrderList(gameOrder,beginTime,endTime,orderStr,orderView,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询游戏充值订单,api调用异常" + e.getMessage());
			super.setErrorMessage("查询游戏充值订单,api调用异常" + e.getMessage());
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API查询游戏充值订单为空");
			super.setErrorMessage("API查询游戏充值订单为空");
			return "failure";
		}
		gameOrderList = (List<GameOrder>) map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		
		logger.info("查询游戏充值订单结束");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String updateOrderSuccess(){
		logger.info("更改订单状态为成功开始");
		JSONObject rs = new JSONObject();
		Map<String, Object> map = null;
		rs.put("flag", "0");
		try {
			if(gameOrder == null) {
				logger.error("获取订单信息异常");
				rs.put("msg", "获取订单信息异常");
			}
			if (gameOrder.getOrderId() == null || gameOrder.getOrderId().equals("")) {
				logger.error("获取订单ID异常");
				rs.put("msg", "获取订单ID异常");
			}
			gameOrder.setGameOrderStatus(GameOrderStatus.RECHARGE);
			map = gameOrderService.queryGameOrderList(gameOrder,null,null,null,null,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询游戏状态为充值中的订单,api调用异常" + e.getMessage());
			rs.put("msg", "查询游戏状态为充值中的订单,api调用异常" + e.getMessage());
		}
		if (map == null || map.size() == 0) {
			logger.error("通过该订单号未能获取到订单状态为“三方充值中”的订单");
			rs.put("msg","通过该订单号未能获取到订单状态为“三方充值中”的订单");
		}
		List<GameOrder> orderList = (List<GameOrder>) map.get(Global.API_MAP_KEY_LIST);
		GameOrder orderForUpdate = new GameOrder();
		if (orderList != null && orderList.size() == 1) {
			orderForUpdate = orderList.get(0);
			orderForUpdate.setGameOrderStatus(GameOrderStatus.RECHARGED);
		}
		try {
			gameOrderService.updateGameOrder(orderForUpdate);
			rs.put("flag", "1");
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询游戏状态为充值中的订单,api调用异常" + e.getMessage());
			rs.put("msg", "查询游戏状态为充值中的订单,api调用异常" + e.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更改订单状态为成功结束");
		return Action.NONE;
	}
	
	@SuppressWarnings("unchecked")
	public String updateOrderFailure(){
		logger.info("更改订单状态为失败开始");
		JSONObject rs = new JSONObject();
		Map<String, Object> map = null;
		rs.put("flag", "0");
		try {
			if(gameOrder == null) {
				logger.error("获取订单信息异常");
				rs.put("msg", "获取订单信息异常");
			}
			if (gameOrder.getOrderId() == null || gameOrder.getOrderId().equals("")) {
				logger.error("获取订单ID异常");
				rs.put("msg", "获取订单ID异常");
			}
			gameOrder.setGameOrderStatus(GameOrderStatus.RECHARGE);
			map = gameOrderService.queryGameOrderList(gameOrder,null,null,null,null,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询游戏状态为充值中的订单,api调用异常" + e.getMessage());
			rs.put("msg", "查询游戏状态为充值中的订单,api调用异常" + e.getMessage());
		}
		if (map == null || map.size() == 0) {
			logger.error("通过该订单号未能获取到订单状态为“三方充值中”的订单");
			rs.put("msg","通过该订单号未能获取到订单状态为“三方充值中”的订单");
		}
		List<GameOrder> orderList = (List<GameOrder>) map.get(Global.API_MAP_KEY_LIST);
		GameOrder orderForUpdate = new GameOrder();
		if (orderList != null && orderList.size() == 1) {
			orderForUpdate = orderList.get(0);
			orderForUpdate.setGameOrderStatus(GameOrderStatus.ERROR);
			try {
				gameOrderService.updateGameOrder(orderForUpdate);
				rs.put("flag", "1");
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询游戏状态为充值中的订单,api调用异常" + e.getMessage());
				rs.put("msg", "查询游戏状态为充值中的订单,api调用异常" + e.getMessage());
			}
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更改订单状态为失败结束");
		return Action.NONE;
	}

	public List<GameOrder> getGameOrderList() {
		return gameOrderList;
	}

	public void setGameOrderList(List<GameOrder> gameOrderList) {
		this.gameOrderList = gameOrderList;
	}

	public GameOrder getGameOrder() {
		return gameOrder;
	}

	public void setGameOrder(GameOrder gameOrder) {
		this.gameOrder = gameOrder;
	}

	public GameOrderService getGameOrderService() {
		return gameOrderService;
	}

	public void setGameOrderService(GameOrderService gameOrderService) {
		this.gameOrderService = gameOrderService;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getOrderStr() {
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getOrderView() {
		if (StringUtils.isEmpty(orderView)) {
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}

	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(GameOrder.ORDER_CREATETIME, "创建时间");
		orderStrMap.put(GameOrder.ORDER_ID, "订单号");
		orderStrMap.put(GameOrder.ORDER_AMOUNT, "金额");
		return orderStrMap;
	}

	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}

	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		return orderViewMap;
	}

	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public List<GameOrderStatus> getGameOrderStatusList(){
		List<GameOrderStatus> statusList = new ArrayList<GameOrderStatus>();
		statusList.add(GameOrderStatus.RECHARGE);
		return statusList;
	}
	
	public List<GameRechargeType> getGameRechargeTypeList(){
		return GameRechargeType.getItems();
	}

	public String getCashTotal() {
		return cashTotal;
	}

	public void setCashTotal(String cashTotal) {
		this.cashTotal = cashTotal;
	}

	public String getCreditTotal() {
		return creditTotal;
	}

	public void setCreditTotal(String creditTotal) {
		this.creditTotal = creditTotal;
	}
}
