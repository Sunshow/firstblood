package web.service.business;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.StopChaseType;

/**
 * 等待追号业务逻辑层接口
 * @author yanweijie
 *
 */
public interface WaitChaseService {
	
	/**
	 * 多条件分页查询正在追号的
	 * @param chaseId		追号ID
	 * @param lotteryType	彩种
	 * @param phase			彩期
	 * @param stopChaseType 追号停止类型
	 * @param pageBean		分页类
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> getWaitResult(String chaseId, LotteryType lotteryType, String phase,
                                             StopChaseType stopChaseType, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 执行追号
	 * @param id 等待追号编号
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean executeWaitChase(String id) throws ApiRemoteCallFailedException;
}