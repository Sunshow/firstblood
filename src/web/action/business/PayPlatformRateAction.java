package web.action.business;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.business.PayPlatformRate;
import com.lehecai.admin.web.service.business.PayPlatformService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PayPlatformType;
import com.lehecai.core.lottery.PlatformType;
import com.lehecai.core.lottery.WithIvrType;

/**
 * 支付平台管理
 * @author He Wang
 *
 */
public class PayPlatformRateAction extends BaseAction {
	
	private static final long serialVersionUID = 4181777008905094487L;
	
	private static final String SETTING_GROUP = "pay_platform_rate";
	private static final String DEFAULT_VALUE = "default";
	private static final String DEFAULT_NAME = "默认比例";
	private PayPlatformService payPlatformService;
	
	private String itemId;
	private String itemName;
	private List<PlatformType> platformTypeList;
	private List<PayPlatformType> payPlatformTypeList;
	private List<PayPlatformRate> payPlatformRateList;
	private PayPlatformRate payPlatformRate;
	private List<String> rateArray;
	private List<String> payPlatformIdArray;
	private String siteId;
	
	private List<YesNoStatus> defaultList;
	private String defaultValue;
	private String defaultName;
	private Integer ifDefault;

	/**
	 * 查询站点支付平台比例
	 * @return
	 */
	public String handle () {
		try {
			payPlatformRateList = payPlatformService.getPayPlatFormRateList(SETTING_GROUP, null);
			if (payPlatformRateList != null && payPlatformRateList.size() > 0) {
				Map<PayPlatformType, Integer> defaultMap = new LinkedHashMap<PayPlatformType, Integer>();
				for (PayPlatformRate pr : payPlatformRateList) {
					if (pr.getPlatformType().equals(DEFAULT_VALUE)) {
						defaultMap = pr.getPayPlatformTypeMap();
						break;
					}
				}
				for (PayPlatformRate pr : payPlatformRateList) {
					if (pr.getPlatformType().equals(DEFAULT_VALUE)) {
						pr.setPlatformTypeName(DEFAULT_NAME);
					} else {
						try {
							Integer value = Integer.valueOf(pr.getPlatformType());
							pr.setPlatformTypeName(PlatformType.getItem(value).getName());
							if (pr.getUserDefault() == YesNoStatus.YES && defaultMap.keySet() != null && defaultMap.keySet().size() > 0) {
								pr.setPayPlatformTypeMap(defaultMap);
							}
						} catch (Exception e) {
							logger.error("站点数据错误");
						}
					}
				}
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询支付平台比例控制异常，{}", e.getMessage());
			super.setErrorMessage("API查询支付平台比例控制异常，请联系技术人员!");
			return "failure";
		} catch (Exception e) {
			logger.error("API查询支付平台比例控制解析数据异常，{}", e.getMessage());
			super.setErrorMessage("API查询支付平台比例控制解析数据异常，请联系技术人员!");
			return "failure";
		}
		return "list";
	}
	
	public String input () {
		platformTypeList = PlatformType.getItemsForRate();
		payPlatformTypeList = PayPlatformType.getItems();
		defaultList = YesNoStatus.getItems();
		if (siteId != null && !siteId.equals("")) {
			try {
				payPlatformRateList = payPlatformService.getPayPlatFormRateList(SETTING_GROUP, siteId + "");
				if (payPlatformRateList == null) {
					logger.error("API查询支付平台比例控制异常,未能查询到该数据");
					super.setErrorMessage("API查询支付平台比例控制异常，payPlatformRateList为null!");
					return "failure";
				}
				if (payPlatformRateList.size() != 1) {
					logger.error("API查询支付平台比例控制异常,查询到超过1条数据");
					super.setErrorMessage("API查询支付平台比例控制异常,查询到超过1条数据");
					return "failure";
				}
				payPlatformRate = payPlatformRateList.get(0);
				itemId = payPlatformRate.getPlatformType() + "";
				ifDefault = payPlatformRate.getUserDefault() == null ? 0 : payPlatformRate.getUserDefault().getValue();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询支付平台比例控制异常，{}", e.getMessage());
				super.setErrorMessage("API查询支付平台比例控制异常，请联系技术人员!");
				return "failure";
			}
		} else {
			Map<PayPlatformType, Integer> typeMap = new LinkedHashMap<PayPlatformType, Integer>();
			for (PayPlatformType platformType : payPlatformTypeList) {
				typeMap.put(platformType, 0);
			}
			payPlatformRate = new PayPlatformRate();
			payPlatformRate.setPayPlatformTypeMap(typeMap);
		}
		return "inputForm";
	}

	public String manage () {
		if (itemId == null || itemId.equals("")) {
			logger.error("支付平台比例控制站点编号为空");
			super.setErrorMessage("支付平台比例控制站点平台编号为空");
			return "failure";
		}
		if (rateArray == null || rateArray.size() ==0) {
			logger.error("支付平台比例控制比例设置为空");
			super.setErrorMessage("支付平台比例控制比例设置为空");
			return "failure";
		}
		if (payPlatformIdArray == null || payPlatformIdArray.size() ==0) {
			logger.error("支付平台为空");
			super.setErrorMessage("支付平台为空");
			return "failure";
		}
		if (payPlatformIdArray.size() != rateArray.size()) {
			logger.error("支付平台与支付比例数目不一致");
			super.setErrorMessage("支付平台与支付比例数目不一致");
			return "failure";
		}
		if (ifDefault == null) {
			logger.error("是否默认值为空");
			super.setErrorMessage("是否默认值为空");
			return "failure";
		}
		
		//使用默认比例时不进行计算
		if (ifDefault == YesNoStatus.NO.getValue()) {
			int total = 0 ;
			try {
				for (String rateStr : rateArray) {
					Integer rate = Integer.valueOf(rateStr);
					total = total + rate;
				}
			} catch (Exception e) {
				logger.error("比例设置有误，原因{}", e.getMessage());
				super.setErrorMessage("比例设置有误");
				return "failure";
			}
			if (total != 100) {
				logger.error("比例设置总和应为100");
				super.setErrorMessage("比例设置总和应为100");
				return "failure";
			}
		}
		
		JSONObject jsonObjAll = new JSONObject();
		JSONObject jsonObj = new JSONObject();
		for (int i=0;i<payPlatformIdArray.size();i++) {
			Integer id = Integer.valueOf(payPlatformIdArray.get(i));
			String name = PayPlatformType.getItem(id).getName();
			if (ifDefault == YesNoStatus.NO.getValue()) {
				jsonObj.put(name, Integer.valueOf(rateArray.get(i)));
			} else {
				jsonObj.put(name, 0);
			}
		}
		if (ifDefault == YesNoStatus.YES.getValue()) {
			jsonObjAll.put("use_default", true);
		} else {
			jsonObjAll.put("use_default", false);
		}
		jsonObjAll.put("rate", jsonObj);
		if (payPlatformService.updateItemSettings(SETTING_GROUP, itemId, jsonObjAll.toString())) {
			super.setForwardUrl("/business/payPlatformRate.do");
			return "success";
		} else {
			logger.error("支付平台比例控制更新出错");
			super.setErrorMessage("支付平台比例控制更新出错");
			return "failure";
		}
	}
	
	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	
	public WithIvrType getWithIvrTypeBind() {
		return WithIvrType.BIND;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemName() {
		return itemName;
	}


	public void setPlatformTypeList(List<PlatformType> platformTypeList) {
		this.platformTypeList = platformTypeList;
	}

	public List<PlatformType> getPlatformTypeList() {
		return platformTypeList;
	}

	public void setPayPlatformTypeList(List<PayPlatformType> payPlatformTypeList) {
		this.payPlatformTypeList = payPlatformTypeList;
	}

	public List<PayPlatformType> getPayPlatformTypeList() {
		return payPlatformTypeList;
	}

	public void setPayPlatformRate(PayPlatformRate payPlatformRate) {
		this.payPlatformRate = payPlatformRate;
	}

	public PayPlatformRate getPayPlatformRate() {
		return payPlatformRate;
	}

	public List<String> getRateArray() {
		return rateArray;
	}

	public void setRateArray(List<String> rateArray) {
		this.rateArray = rateArray;
	}

	public List<String> getPayPlatformIdArray() {
		return payPlatformIdArray;
	}

	public void setPayPlatformIdArray(List<String> payPlatformIdArray) {
		this.payPlatformIdArray = payPlatformIdArray;
	}

	public void setPayPlatformService(PayPlatformService payPlatformService) {
		this.payPlatformService = payPlatformService;
	}

	public PayPlatformService getPayPlatformService() {
		return payPlatformService;
	}

	public void setPayPlatformRateList(List<PayPlatformRate> payPlatformRateList) {
		this.payPlatformRateList = payPlatformRateList;
	}

	public List<PayPlatformRate> getPayPlatformRateList() {
		return payPlatformRateList;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		this.defaultValue = DEFAULT_VALUE;
		return defaultValue;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public String getDefaultName() {
		this.defaultName = DEFAULT_NAME;
		return defaultName;
	}

	public void setDefaultList(List<YesNoStatus> defaultList) {
		this.defaultList = defaultList;
	}

	public List<YesNoStatus> getDefaultList() {
		return defaultList;
	}

	public void setIfDefault(Integer ifDefault) {
		this.ifDefault = ifDefault;
	}

	public Integer getIfDefault() {
		return ifDefault;
	}

}
