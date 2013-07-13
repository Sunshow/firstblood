package web.action.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.CouponService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.event.Coupon;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.CouponStatus;
import com.lehecai.core.lottery.CouponType;
import com.lehecai.core.util.CoreNumberUtil;
import com.opensymphony.xwork2.Action;

public class CouponAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private CouponService couponService; 
	
	private Coupon coupon;
	
	private List<Coupon> coupons;
	
	private Integer couponTypeValue;
	private Integer couponStatusValue;
	private Long cpId;
	private String cpIdStr;
	private Long uid;
	private Integer eventId;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	private String orderStr;
	private String orderView;
	
	private String amount;

	public String handle(){
		logger.info("首次进入查询充值券");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入查询充值券");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		
		try {
			map = couponService.findCouponList(super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (map != null) {
			coupons = (List<Coupon>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		try {
			map = couponService.getAmount(cpId, null, null, uid, eventId, orderStr, orderView);
			if (map != null) {
				Object amountObj = map.get(Global.API_MAP_KEY_AMOUNT);
				if (amountObj != null) {
					double amountValue = 0;
					try{
						amountValue = Double.parseDouble(amountObj.toString());
					} catch (Exception e) {
						logger.error("转换double类型异常，amount={}", amountObj);
					}
					amount = CoreNumberUtil.formatNumBy2Digits(amountValue);
				}else {
					logger.error("方案总金额为空");
				}
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询充值券金额，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		logger.info("查询充值券结束");
		
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String list(){
		logger.info("进入查询充值券");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		
		CouponType type = couponTypeValue == null ? null : CouponType.getItem(couponTypeValue);
		CouponStatus status = couponStatusValue == null ? null : CouponStatus.getItem(couponStatusValue);
		
		PageBean pb = super.getPageBean();
		pb.setPageSize(100);
		try {
			map = couponService.findCouponListByCondition(cpId, type, status, uid, eventId, orderStr, orderView, pb);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (map != null) {
			coupons = (List<Coupon>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		
		try {
			map = couponService.getAmount(cpId, type, status, uid, eventId, orderStr, orderView);
			if (map != null) {
				Object amountObj = map.get(Global.API_MAP_KEY_AMOUNT);
				if (amountObj != null) {
					double amountValue = 0;
					try{
						amountValue = Double.parseDouble(amountObj.toString());
					} catch (Exception e) {
						logger.error("转换double类型异常，amount={}", amountObj);
					}
					amount = CoreNumberUtil.formatNumBy2Digits(amountValue);
				}
				else {
					logger.error("方案总金额为空");
				}
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		logger.info("查询充值券结束");
		
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String disable() {
		logger.info("进入禁用充值券");
		
		JSONObject json = new JSONObject();
		int rc = 0;
		String msg = "操作成功";
		
		List<Long> cpIds = new ArrayList();
		if (cpIdStr != null && !"".equals(cpIdStr)) {
			String [] str = cpIdStr.split(",");
			for (int i = 0; i < str.length ;i++) {
				cpIds.add(Long.parseLong(str[i]));
			}
		}
		if (cpIds != null && cpIds.size() > 0) {
			for (int i = 0; i < cpIds.size(); i++) {
				if (cpIds.get(i) != null && cpIds.get(i) != 0) {
					try {
						couponService.disable(cpIds.get(i), super.getPageBean());
					} catch (ApiRemoteCallFailedException e) {
						logger.error(e.getMessage(),e);
						super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
						rc = 1;
						msg = "api调用异常，请联系技术人员!";
						json.put("code", rc);
						json.put("msg", msg);
						writeRs(ServletActionContext.getResponse(), json);
						return Action.NONE;
					}
				} else {
					logger.error("ID为空或者不正确");
					super.setErrorMessage("ID为空或者不正确");
					rc = 1;
					msg = "ID为空或者不正确";
					json.put("code", rc);
					json.put("msg", msg);
					writeRs(ServletActionContext.getResponse(), json);
					return Action.NONE;
				}
			}
		} else {
			if (coupon != null) {
				if (coupon.getCpId() == null || coupon.getCpId() == 0) {
					logger.error("ID为空或者不正确");
					super.setErrorMessage("ID为空或者不正确");
					rc = 1;
					msg = "ID为空或者不正确";
					json.put("code", rc);
					json.put("msg", msg);
					writeRs(ServletActionContext.getResponse(), json);
					return Action.NONE;
				}
				
				try {
					couponService.disable(coupon.getCpId(), super.getPageBean());
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(),e);
					super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
					rc = 1;
					msg = "api调用异常，请联系技术人员!";
					json.put("code", rc);
					json.put("msg", msg);
					writeRs(ServletActionContext.getResponse(), json);
					return Action.NONE;
				}
			}
		}
		
		rc = 0;
		msg = "操作成功";
		json.put("code", rc);
		json.put("msg", msg);
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("禁用充值券结束");
		return Action.NONE;
	}
	
	@SuppressWarnings("unchecked")
	public String enable() {
		logger.info("进入启用充值券");
		
		JSONObject json = new JSONObject();
		int rc = 0;
		String msg = "操作成功";
		
		List<Long> cpIds = new ArrayList();
		if (cpIdStr != null && !"".equals(cpIdStr)) {
			String [] str = cpIdStr.split(",");
			for (int i = 0; i < str.length ;i++) {
				cpIds.add(Long.parseLong(str[i]));
			}
		}
		if (cpIds != null && cpIds.size() > 0) {
			for (int i = 0; i < cpIds.size(); i++) {
				if (cpIds.get(i) != null && cpIds.get(i) != 0) {
					try {
						couponService.enable(cpIds.get(i), super.getPageBean());
					} catch (ApiRemoteCallFailedException e) {
						logger.error(e.getMessage(),e);
						super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
						rc = 1;
						msg = "api调用异常，请联系技术人员!";
						json.put("code", rc);
						json.put("msg", msg);
						writeRs(ServletActionContext.getResponse(), json);
						return Action.NONE;
					}
				} else {
					logger.error("ID为空或者不正确");
					super.setErrorMessage("ID为空或者不正确");
					rc = 1;
					msg = "ID为空或者不正确";
					json.put("code", rc);
					json.put("msg", msg);
					writeRs(ServletActionContext.getResponse(), json);
					return Action.NONE;
				}
			}
		} else {
			if (coupon != null) {
				if (coupon.getCpId() == null || coupon.getCpId() == 0) {
					logger.error("ID为空或者不正确");
					super.setErrorMessage("ID为空或者不正确");
					rc = 1;
					msg = "ID为空或者不正确";
					json.put("code", rc);
					json.put("msg", msg);
					writeRs(ServletActionContext.getResponse(), json);
					return Action.NONE;
				}
				
				try {
					couponService.enable(coupon.getCpId(), super.getPageBean());
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(),e);
					super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
					rc = 1;
					msg = "api调用异常，请联系技术人员!";
					json.put("code", rc);
					json.put("msg", msg);
					writeRs(ServletActionContext.getResponse(), json);
					return Action.NONE;
				}
			}
		}
		
		rc = 0;
		msg = "操作成功";
		json.put("code", rc);
		json.put("msg", msg);
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("启用充值券结束");
		return Action.NONE;
	}
	
	public String del() {
		logger.info("进入删除充值券");
		
		JSONObject json = new JSONObject();
		int rc = 0;
		String msg = "操作成功";
		if (coupon.getCpId() == null || coupon.getCpId() == 0) {
			logger.error("ID为空或者不正确");
			super.setErrorMessage("ID为空或者不正确");
			rc = 1;
			msg = "ID为空或者不正确";
			json.put("code", rc);
			json.put("message", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}
		
		try {
			couponService.delCoupon(coupon.getCpId(), super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			rc = 1;
			msg = "api调用异常，请联系技术人员!";
			json.put("code", rc);
			json.put("message", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}
		
		rc = 0;
		msg = "操作成功";
		json.put("code", rc);
		json.put("message", msg);
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("删除充值券结束");
		return Action.NONE;
		
	}

	public CouponService getCouponService() {
		return couponService;
	}

	public void setCouponService(CouponService couponService) {
		this.couponService = couponService;
	}

	public List<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}
	
	public List<CouponType> getCouponTypes() {
		return CouponType.getItems();
	}

	public Integer getCouponTypeValue() {
		return couponTypeValue;
	}

	public void setCouponTypeValue(Integer couponTypeValue) {
		this.couponTypeValue = couponTypeValue;
	}

	public Long getCpId() {
		return cpId;
	}

	public void setCpId(Long cpId) {
		this.cpId = cpId;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(Coupon.ORDER_CLAIM_TIME, "领取时间");
		orderStrMap.put(Coupon.ORDER_COUPON_ID, "充值券ID");
		orderStrMap.put(Coupon.ORDER_EXPIRE_TIME, "截止时间");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}

	public String getOrderStr() {
		if (orderStr == null || "".equals(orderStr)) {
			orderStr = Coupon.ORDER_COUPON_ID;
		}
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getOrderView() {
		if (orderView == null || "".equals(orderView)) {
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
	
	public CouponStatus getCouponStatusAvailable() {
		return CouponStatus.COUPON_STATUS_AVAILABLE;
	}

	public CouponStatus getCouponStatusDisable() {
		return CouponStatus.COUPON_STATUS_DISABLED;
	}
	
	public CouponStatus getCouponStatusNotinuse() {
		return CouponStatus.COUPON_STATUS_NOTINUSE;
	}
	
	public CouponStatus getCouponStatusDelete() {
		return CouponStatus.COUPON_STATUS_DELETED;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public List<CouponStatus> getCouponStatus() {
		return CouponStatus.getItems();
	}

	public Integer getCouponStatusValue() {
		return couponStatusValue;
	}

	public void setCouponStatusValue(Integer couponStatusValue) {
		this.couponStatusValue = couponStatusValue;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCpIdStr() {
		return cpIdStr;
	}

	public void setCpIdStr(String cpIdStr) {
		this.cpIdStr = cpIdStr;
	}

}
