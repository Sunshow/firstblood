/**
 * 
 */
package web.service.business;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.user.CreditExchangeLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public interface CreditExchangeQueryService {
	/**
	 * 积分互换平台日志查询
	 * @return
	 */
	public Map<String, Object> queryCreditExchangeList(CreditExchangeLog creditExchangeLog, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
}
