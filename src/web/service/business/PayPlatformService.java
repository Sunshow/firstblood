package web.service.business;

import java.util.List;

import com.lehecai.admin.web.domain.business.PayPlatformRate;
import com.lehecai.core.exception.ApiRemoteCallFailedException;


/**
 * 支付平台比例设置
 * @author He Wang
 *
 */
public interface PayPlatformService {
	
	/**
	 * 根据itemId获取比例列表
	 * @param settingGroup
	 * @param itemId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public List<PayPlatformRate> getPayPlatFormRateList(String settingGroup, String itemId) throws ApiRemoteCallFailedException;
	
	/**
	 * 更新比例
	 * @param group
	 * @param item
	 * @param value
	 * @return
	 */
	public boolean updateItemSettings(String group, String item, String value) ;
}
