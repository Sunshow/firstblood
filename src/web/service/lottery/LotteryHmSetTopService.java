package web.service.lottery;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;

public interface LotteryHmSetTopService {
	public static final String REWARD_PLAN_STATUS= "reward_plan_status";//可派奖  方案状态  in方式的key  4,6,8
	public static final String REWARD_RESULT_STATUS= "reward_result_status";//可派奖  方案  结果状态，是否已中奖等
	
	public Map<String, Object> getResult(String userName, String planId,
                                         LotteryType lotteryType, String phase, Integer amount, Date rbeginDate, Date rendDate,
                                         Date lbeginDate, Date lendDate,
                                         String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	public Plan get(String id) throws ApiRemoteCallFailedException;

	
	/**
	 * 方案查询统计，返回方案总金额，和税后中奖总金额
	 * @param pbeginDate TODO
	 * @param pendDate TODO
	 * @return
	 */
	public Map<String,Object> lotteryPlanStatistics(String userName,
                                                    String planId, LotteryType lotteryTypeId, String phase,
                                                    Integer amount, Date rbeginDate, Date rendDate,
                                                    Date lbeginDate, Date lendDate) throws ApiRemoteCallFailedException;
	/**
	 * 设置置顶
	 * @param plan
	 * @param status
	 * @return
	 */
	public boolean updateTopStatus(Plan plan, int top) throws ApiRemoteCallFailedException;
}