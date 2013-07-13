package web.action.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.DeductService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.FreezeLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.FrozenStatus;
import com.lehecai.core.lottery.WalletType;

public class DeductAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private static final Logger logger = LoggerFactory
			.getLogger(DeductAction.class);

	private DeductService deductService;
	
	private List<FreezeLog> freezeLogs;

	private String username;
	private Long deduct_id;
	private Long frozen_id;
	private Integer frozenStatusId;
	private Integer walletTypeId;

	private Date beginDate; // 开始时间
	private Date endDate; // 结束时间

	private String orderStr;
	private String orderView;

	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;

	public String handle() {
		logger.info("进入查询所有冻结钱包");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询所有冻结钱包");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map;
		FrozenStatus fs = FrozenStatus.getItem(this.getFrozenStatusId());
		WalletType wt = WalletType.getItem(this.getWalletTypeId());
		try {
			map = deductService.getResult(username, frozen_id, fs, wt,
					beginDate, endDate, this.getOrderStr(), this.getOrderView(), super
					.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询冻结钱包，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			freezeLogs = (List<FreezeLog>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		logger.info("查询所有冻结钱包结束");
		return "list";
	}
	
	public String deduct() {
		logger.info("进入扣款");
		boolean rs = false;
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		Long user_id = null;
		if (userSessionBean.getUser() != null) {
			user_id = userSessionBean.getUser().getId();
		}
		if (user_id != null) {
			if (deduct_id != null && deduct_id != 0L) {
				try {
					
					deductService.deduct(deduct_id, user_id);
					rs = true;
				} catch (ApiRemoteCallFailedException e) {
					logger.error("扣款，api调用异常，{}", e.getMessage());
				}
			} else {
				logger.error("deduct_id为空或为0");
			}
		} else {
			logger.error("扣款，获取当前用户失败");
		}
		
		JSONObject obj = new JSONObject();
		obj.put("rs", rs);
		writeRs(response, obj);
		logger.info("扣款结束");
		return null;
	}
	
	public String unfreeze() {
		logger.info("进入解冻");
		boolean rs = false;
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		Long user_id = null;
		if (userSessionBean.getUser() != null) {
			user_id = userSessionBean.getUser().getId();
		}
		if (user_id != null) {
			if (deduct_id != null && deduct_id != 0L) {
				try {
					deductService.unfreeze(deduct_id, user_id);
					rs = true;
				} catch (ApiRemoteCallFailedException e) {
					logger.error("解冻，api调用异常，{}", e.getMessage());
				}
			} else {
				logger.error("deduct_id为空或为0");
				super.setErrorMessage("deduct_id不能为空或0");
			}
		} else {
			logger.error("扣款，获取当前用户失败");
		}
		
		JSONObject obj = new JSONObject();
		obj.put("rs", rs);
		writeRs(response, obj);
		logger.info("解冻结束");
		return null;
	}
	
	public List<FreezeLog> getFreezeLogs() {
		return freezeLogs;
	}

	public void setFreezeLogs(List<FreezeLog> freezeLogs) {
		this.freezeLogs = freezeLogs;
	}

	public DeductService getDeductService() {
		return deductService;
	}

	public void setDeductService(DeductService deductService) {
		this.deductService = deductService;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getDeduct_id() {
		return deduct_id;
	}

	public void setDeduct_id(Long deductId) {
		deduct_id = deductId;
	}

	public Integer getFrozenStatusId() {
		if (frozenStatusId == null) {
			frozenStatusId = FrozenStatus.ALL.getValue();
		}
		return frozenStatusId;
	}

	public void setFrozenStatusId(Integer frozenStatusId) {
		this.frozenStatusId = frozenStatusId;
	}

	public Integer getWalletTypeId() {
		if (walletTypeId == null) {
			walletTypeId = WalletType.CASH.getValue();
		}
		return walletTypeId;
	}

	public void setWalletTypeId(Integer walletTypeId) {
		this.walletTypeId = walletTypeId;
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
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(FreezeLog.ORDER_DEDUCT_ID, "编码");
		orderStrMap.put(FreezeLog.ORDER_TIMELINE, "发起时间");
		orderStrMap.put(FreezeLog.ORDER_UID, "用户编码");
		orderStrMap.put(FreezeLog.ORDER_AMOUNT, "冻结金额");
		return orderStrMap;
	}

	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}

	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}

	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}

	public String getOrderStr() {
		if (orderStr == null && !"".equals(orderStr)) {
			orderStr = FreezeLog.ORDER_TIMELINE;
		}
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getOrderView() {
		if (orderView == null && !"".equals(orderView)) {
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public List<FrozenStatus> getFrozenStatusList() {
		return FrozenStatus.getItems();
	}
	public List<WalletType> getWalletTypeList() {
		return WalletType.getItems();
	}
	public FrozenStatus getFrozenFrozenStatus() {
		return FrozenStatus.FROZEN;
	}
	public FrozenStatus getDeductFrozenStatus() {
		return FrozenStatus.DEDUCT;
	}
	public FrozenStatus getUnfrozenFrozenStatus() {
		return FrozenStatus.UNFREZEN;
	}

	public void setFrozen_id(Long frozen_id) {
		this.frozen_id = frozen_id;
	}

	public Long getFrozen_id() {
		return frozen_id;
	}
}
