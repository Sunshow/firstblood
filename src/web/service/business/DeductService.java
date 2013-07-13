package web.service.business;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.FrozenStatus;
import com.lehecai.core.lottery.WalletType;

public interface DeductService {
	Map<String, Object> getResult(String username, Long deduct_id,
                                  FrozenStatus status, WalletType walletType, Date beginDate,
                                  Date endDate, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	void deduct(Long deduct_id, Long user_id) throws ApiRemoteCallFailedException;
	void unfreeze(Long deduct_id, Long user_id) throws ApiRemoteCallFailedException;
}
