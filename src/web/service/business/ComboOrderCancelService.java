package web.service.business;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface ComboOrderCancelService {
	
	/**
	 * 取消套餐订单
	 * @return
	 */
	public boolean comboOrderCancel(Long comboOrderId) throws ApiRemoteCallFailedException;

}
