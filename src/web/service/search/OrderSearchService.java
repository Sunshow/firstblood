package web.service.search;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.search.entity.lottery.PlanOrderSearch;

/**
 * 2013-05-09
 * @author He Wang
 *
 */
public interface OrderSearchService {

	/**
	 * 搜索服务-订单统计查询-列表
	 * @param searchEntity
	 * @param param
	 * @param pageBean
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLotteryPlanOrderResult(PlanOrderSearch searchEntity, Map<String, Object> param, PageBean pageBean) throws Exception;
	
	/**
	 * 搜索服务-订单统计查询-统计
	 * @param searchEntity
	 * @param param
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> lotteryPlanOrderStatistics(PlanOrderSearch searchEntity, Map<String, Object> param, String[] field) throws Exception;
	
	/**
	 * 搜索服务-消费统计-列表
	 * @param searchEntity
	 * @param param 过滤条件
	 * @param groupArray 分组
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String,Object> getMemberConsumptionResult(PlanOrderSearch searchEntity, Map<String, Object> param, PageBean pageBean) throws Exception;
	
	

}