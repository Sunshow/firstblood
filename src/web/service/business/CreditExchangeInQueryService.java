/**
 * 
 */
package web.service.business;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.user.CreditExchangeLogIn;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public interface CreditExchangeInQueryService {
	/**
	 * 积分互换平台日志查询
	 * @return
	 */
	public Map<String, Object> queryCreditExchangeInList(CreditExchangeLogIn creditExchangeLogIn, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
}
