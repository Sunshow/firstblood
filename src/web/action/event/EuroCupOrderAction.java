/**
 * 
 */
package web.action.event;

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
import com.lehecai.admin.web.service.event.EuroCupService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.event.EuroCupOrder;
import com.lehecai.core.event.EuroCupOrderStatus;
import com.lehecai.core.event.EuroCupType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class EuroCupOrderAction extends BaseAction {

	private static final long serialVersionUID = 8625207108649371005L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private EuroCupService euroCupService;
	
	private EuroCupOrder euroCupOrder;
	private List<EuroCupOrder> euroCupOrders;
	private Date beginDate;
	private Date endDate;
	private String orderStr;
	private String orderView;
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	private int status = EuroCupOrderStatus.ALL.getValue();
	private int type = EuroCupType.ALL.getValue();
	
	public String handle(){
		logger.info("进入欧洲杯订单查询");
		return "orderList";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入获取欧洲杯订单信息开始");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		try{
			if(euroCupOrder == null) euroCupOrder = new EuroCupOrder();
			EuroCupOrderStatus euroCupOrderStatus = EuroCupOrderStatus.getItem(status);
			euroCupOrder.setPrizeStatus(euroCupOrderStatus);
			EuroCupType euroCupType = EuroCupType.getItem(type);
			euroCupOrder.setType(euroCupType);
			if (beginDate == null) {
				beginDate = getDefaultQueryBeginDate();
			}
			map = euroCupService.findOrderList(euroCupOrder,beginDate,endDate,orderStr,orderView,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取欧洲杯订单信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("获取欧洲杯订单信息，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}		
		if (map != null) {
			euroCupOrders = (List<EuroCupOrder>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("进入获取欧洲杯订单信息结束");
		return "orderList";
	}

	public EuroCupService getEuroCupService() {
		return euroCupService;
	}

	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}

	public EuroCupOrder getEuroCupOrder() {
		return euroCupOrder;
	}

	public void setEuroCupOrder(EuroCupOrder euroCupOrder) {
		this.euroCupOrder = euroCupOrder;
	}

	public List<EuroCupOrder> getEuroCupOrders() {
		return euroCupOrders;
	}

	public void setEuroCupOrders(List<EuroCupOrder> euroCupOrders) {
		this.euroCupOrders = euroCupOrders;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
		orderStrMap.put(EuroCupOrder.ORDER_CREATE_TIME, "创建时间");
		orderStrMap.put(EuroCupOrder.ORDER_ORDER_ID, "订单号");
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
	
	public List<EuroCupOrderStatus> getEuroCupOrderStatusList(){
		return EuroCupOrderStatus.getItems();
	}
	
	public List<EuroCupType> getEuroCupTypeList(){
		return EuroCupType.getItems();
	}
}
