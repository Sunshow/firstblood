package web.domain.business;

import java.io.Serializable;
import java.util.Map;

import com.lehecai.core.YesNoStatus;
import com.lehecai.core.lottery.PayPlatformType;

/**
 * 站点支付平台比例
 * @author He Wang
 *
 */
public class PayPlatformRate implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//站点id,因为有default于是使用String
	private String platformType;
	private String platformTypeName;
	//是否使用默认比例
	private YesNoStatus userDefault;
	//支付平台比例
    private Map<PayPlatformType, Integer> payPlatformTypeMap;
    
	
	public void setPayPlatformTypeMap(Map<PayPlatformType, Integer> payPlatformTypeMap) {
		this.payPlatformTypeMap = payPlatformTypeMap;
	}
	
	public Map<PayPlatformType, Integer> getPayPlatformTypeMap() {
		return payPlatformTypeMap;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}

	public String getPlatformType() {
		return platformType;
	}

	public void setPlatformTypeName(String platformTypeName) {
		this.platformTypeName = platformTypeName;
	}

	public String getPlatformTypeName() {
		return platformTypeName;
	}

	public void setUserDefault(YesNoStatus userDefault) {
		this.userDefault = userDefault;
	}

	public YesNoStatus getUserDefault() {
		return userDefault;
	}

	
}
