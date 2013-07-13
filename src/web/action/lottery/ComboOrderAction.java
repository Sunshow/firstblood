package web.action.lottery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.ComboService;
import com.lehecai.admin.web.service.lottery.ComboOrderService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.lottery.ComboOrder;
import com.lehecai.core.api.lottery.ComboOrderDetail;
import com.lehecai.core.api.lottery.ComboOrderDetailRecord;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.FinishComboStatus;
import com.lehecai.core.lottery.FinishComboType;
import com.lehecai.core.lottery.LotteryType;

public class ComboOrderAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	private List<ComboOrder> comboOrderList;
	private ComboOrderService comboOrderService;
	private List<ComboOrderDetail> comboOrderDetailList;
	private List<ComboOrderDetailRecord> comboOrderRecordList;
	private Long comboOrderId;
	private Long comboId;
	private Long uid;
	private Long comborevId;
	private Integer statusValue;
	private Integer lotteryTypeId;
	private ComboService comboService;
	private String phase;
	private Long planId;
	private Long orderId;
	
	public String handle() {
		logger.info("进入查询套餐订单列表");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询套餐订单列表");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		
		FinishComboStatus fcs = statusValue == null ? null : FinishComboStatus.getItem(statusValue);
		
		Map<String, Object> map = null;
		try {
			map = comboOrderService.queryComboOrderList(comboOrderId, comboId, uid, comborevId, fcs,  super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取套餐订单信息,api调用异常" + e.getMessage());
			super.setErrorMessage("获取套餐订单信息,api调用异常" + e.getMessage());
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API获取套餐订单信息为空");
			super.setErrorMessage("API获取套餐订单信息为空");
			return "failure";
		}
		comboOrderList = (List<ComboOrder>) map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		logger.info("查询套餐订单信息结束");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String viewComboOrderInfo() {
		logger.info("进入查询套餐订单详情");
		
		if (comboOrderId == null || comboOrderId == 0) {
			logger.error("套餐订单ID不能为空");
			super.setErrorMessage("套餐订单ID不能为空");
			return "failure";
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		
		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		FinishComboStatus fcs = statusValue == null ? null : FinishComboStatus.getItem(statusValue);
		
		Map<String, Object> map = null;
		try {
			map = comboOrderService.queryComboOrderInfo(comboOrderId, comboId, uid, lt, fcs, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询单一套餐详情信息,api调用异常" + e.getMessage());
			super.setErrorMessage("查询单一套餐详情信息失败,api调用异常" + e.getMessage());
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API查询单一套餐详情信息为空");
			super.setErrorMessage("API查询单一套餐详情信息为空");
			return "failure";
		}
		comboOrderDetailList = (List<ComboOrderDetail>) map.get(Global.API_MAP_KEY_LIST);
		if (comboOrderDetailList != null && comboOrderDetailList.size() > 0) {
			uid = comboOrderDetailList.get(0).getUid();
			comboId = Long.parseLong(comboOrderDetailList.get(0).getComboId());
		}
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		return "detailList";
	}
	
	@SuppressWarnings("unchecked")
	public String viewRecord() {
		logger.info("进入查询套餐执行记录");
		
		if (comboOrderId == null || comboOrderId == 0) {
			logger.error("套餐订单ID不能为空");
			super.setErrorMessage("套餐订单ID不能为空");
			return "failure";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		LotteryType lotteryType = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		
		try {
			map = comboOrderService.queryComboOrderRecord(comboOrderId, comboId, uid, lotteryType, phase, planId, orderId, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询套餐执行记录异常" + e.getMessage());
			super.setErrorMessage("API查询套餐执行记录异常");
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API查询套餐执行记录为空");
			super.setErrorMessage("API查询套餐执行记录为空");
			return "failure";
		}
		
		comboOrderRecordList = (List<ComboOrderDetailRecord>) map.get(Global.API_MAP_KEY_LIST);
		if (comboOrderRecordList != null && comboOrderRecordList.size() > 0) {
			comboId = comboOrderRecordList.get(0).getComboId();
			uid = comboOrderRecordList.get(0).getUid();
		} else {
			try {
				map = comboOrderService.queryComboOrderInfo(comboOrderId, comboId, uid, null, null, super.getPageBean());
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询单一套餐详情信息,api调用异常" + e.getMessage());
				super.setErrorMessage("查询单一套餐详情信息失败,api调用异常" + e.getMessage());
				return "failure";
			}
			if (map == null || map.size() == 0) {
				logger.error("API查询单一套餐详情信息为空");
				super.setErrorMessage("API查询单一套餐详情信息为空");
				return "failure";
			}
			comboOrderDetailList = (List<ComboOrderDetail>) map.get(Global.API_MAP_KEY_LIST);
			if (comboOrderDetailList != null && comboOrderDetailList.size() > 0) {
				uid = comboOrderDetailList.get(0).getUid();
				comboId = Long.parseLong(comboOrderDetailList.get(0).getComboId());
			}
		}
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		return "recordList";
	}

	public List<ComboOrderDetail> getComboOrderDetailList() {
		return comboOrderDetailList;
	}

	public void setComboOrderDetailList(List<ComboOrderDetail> comboOrderDetailList) {
		this.comboOrderDetailList = comboOrderDetailList;
	}

	public List<ComboOrderDetailRecord> getComboOrderRecordList() {
		return comboOrderRecordList;
	}

	public void setComboOrderRecordList(List<ComboOrderDetailRecord> comboOrderRecordList) {
		this.comboOrderRecordList = comboOrderRecordList;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public ComboOrderService getComboOrderService() {
		return comboOrderService;
	}

	public void setComboOrderService(ComboOrderService comboOrderService) {
		this.comboOrderService = comboOrderService;
	}

	public List<ComboOrder> getComboOrderList() {
		return comboOrderList;
	}

	public void setComboOrderList(List<ComboOrder> comboOrderList) {
		this.comboOrderList = comboOrderList;
	}
	
	public FinishComboType getPlanWin () {
		return FinishComboType.PLAN_WIN;
	}

	public Long getComboOrderId() {
		return comboOrderId;
	}

	public void setComboOrderId(Long comboOrderId) {
		this.comboOrderId = comboOrderId;
	}

	public ComboService getComboService() {
		return comboService;
	}

	public void setComboService(ComboService comboService) {
		this.comboService = comboService;
	}

	public Long getComboId() {
		return comboId;
	}

	public void setComboId(Long comboId) {
		this.comboId = comboId;
	}

	public Long getComborevId() {
		return comborevId;
	}

	public void setComborevId(Long comborevId) {
		this.comborevId = comborevId;
	}

	public Integer getStatusValue() {
		return statusValue;
	}

	public void setStatusValue(Integer statusValue) {
		this.statusValue = statusValue;
	}
	
	public List<FinishComboStatus> getFinishComboStatuses () {
		return FinishComboStatus.getItems();
	}
	
	public List<LotteryType> getLotteryTypes () {
		return LotteryType.getItems();
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	public LotteryType getRecordLotteryType () {
		LotteryType lotteryType = null;
		if (lotteryTypeId != null) {
			lotteryType = LotteryType.getItem(lotteryTypeId); 
		}
		return lotteryType;
	}
}
