/**
 * 
 */
package web.action.game;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.lehecai.core.util.CoreNumberUtil;

/**
 * @author chirowong
 *
 */
public class GameOrderQueryAction extends BaseAction {
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
	private int status = GameOrderStatus.ALL.getValue();
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
		logger.info("进入游戏充值订单查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		try {
			if(gameOrder == null) gameOrder = new GameOrder();
			GameOrderStatus gameOrderStatus = GameOrderStatus.getItem(status);
			gameOrder.setGameOrderStatus(gameOrderStatus);
			GameRechargeType gameRechargeType = GameRechargeType.getItem(type);
			gameOrder.setGameRechargeType(gameRechargeType);
			if (beginTime == null) {
				beginTime = getDefaultQueryBeginDate();
			}
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
		
		Map<String, Object> sumMap = null;
		try {
			if(gameOrder == null) gameOrder = new GameOrder();
			GameOrderStatus gameOrderStatus = GameOrderStatus.getItem(status);
			gameOrder.setGameOrderStatus(gameOrderStatus);
			GameRechargeType gameRechargeType = GameRechargeType.getItem(type);
			gameOrder.setGameRechargeType(gameRechargeType);
			sumMap = gameOrderService.queryGameOrderTotal(gameOrder,beginTime,endTime);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询游戏充值汇总,api调用异常" + e.getMessage());
			super.setErrorMessage("查询游戏充值汇总,api调用异常" + e.getMessage());
			return "failure";
		}
		if (sumMap == null || sumMap.size() == 0) {
			logger.error("API查询游戏充值汇总为空");
			super.setErrorMessage("API查询游戏充值汇总为空");
			return "failure";
		}
		
		if (sumMap != null) {
			Object cashObj = sumMap.get("cash");
			double cashDou = 0.00D;
			try {
				cashDou = Double.parseDouble(String.valueOf(cashObj));
			} catch (Exception e) {
				logger.error("现金总金额转换成double类型异常",e);
				super.setErrorMessage("现金总金额转换成double类型异常");
				return "failure";
			}
			cashTotal = CoreNumberUtil.formatNumBy2Digits(cashDou);
			if (cashTotal == null || "".equals(cashTotal)) {
				logger.error("格式化现金总金额异常");
				super.setErrorMessage("格式化现金总金额异常");
				return "failure";
			}
		}
		if (sumMap != null) {
			Object creditObj = sumMap.get("credit");
			double creditDou = 0.00D;
			try {
				creditDou = Double.parseDouble(String.valueOf(creditObj));
			} catch (Exception e) {
				logger.error("彩贝总金额转换成double类型异常",e);
				super.setErrorMessage("彩贝总金额转换成double类型异常");
				return "failure";
			}
			creditTotal = CoreNumberUtil.formatNumBy2Digits(creditDou);
			if (creditTotal == null || "".equals(creditTotal)) {
				logger.error("格式化彩贝总金额异常");
				super.setErrorMessage("格式化彩贝总金额异常");
				return "failure";
			}
		}
		logger.info("查询游戏充值订单结束");
		return "list";
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
		return GameOrderStatus.getItems();
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
