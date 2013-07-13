package web.service.lottery;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LotteryPlanService {
	public static final String REWARD_PLAN_STATUS= "reward_plan_status";//可派奖  方案状态  in方式的key  4,6,8
	public static final String REWARD_RESULT_STATUS= "reward_result_status";//可派奖  方案  结果状态，是否已中奖等
	
	/**
	 * 
	 * @author chirowong
	 * 2012-11-21 增加投注方式查询条件
	 */
	public Map<String, Object> getResult(String userName,
                                         String planId, LotteryType lotteryType, PhaseType phaseType, String phase, PlanType planType, SelectType selectType,
                                         PlayType playType, List<String> planStatus, YesNoStatus uploadStatus, PublicStatus publicStatus,
                                         ResultStatus resultStatus, YesNoStatus allowAutoFollow, PlanTicketStatus planTicketStatus, Long sourceId, Date rbeginDate, Date rendDate,
                                         Date lbeginDate, Date lendDate, Date pbeginDate, Date pendDate, Date lastMatchTimeFrom, Date lastMatchTimeTo, String orderStr, String orderView, PageBean pageBean, PlanCreateType planCreateType) throws ApiRemoteCallFailedException;
	
	public Plan get(String id) throws ApiRemoteCallFailedException;

	/**
	 * 根据条件获得彩票方案列表
	 * @param condition  方案id,彩期类型,期数,方案类型,选号类型,玩法,<br/>
	 *     方案状态,结果状态，是否已中奖等,北单扩展字段：方案中最后一个场次的索引顺序
	 * @param pageBean
	 * @return Map<String, Object> key:list | pageBean | resultBean  value:List<Plan> | PageBean | ResultBean
	 */
	public Map<String, Object> getLotteryPlans(Map<String, Object> condition, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 按彩票类型和期号分页查询要派奖的方案
	 * @param lotteryType
	 * @param phaseNo
	 * @param planStatusInList  null为默认 已出票  未满员撤单  部分出票
	 * @param resultStatusInList null为默认 已中奖  已派奖
	 * @param pageBean
	 * @return Map<String, Object> key:list | pageBean | resultBean  value:List<Plan> | PageBean | ResultBean
	 */
	public Map<String, Object> find4RewardPlanByLotteryTypeAndPhaseNo(LotteryType lotteryType, String phaseNo, List<String> planStatusInList, List<String> resultStatusInList, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 按彩票类型、期号、方案编码、方案类型分页查询要派奖的方案
	 * @param lotteryType
	 * @param phaseNo
	 * @param planId  
	 * @param planType
	 * @param pageBean
	 * @return Map<String, Object> key:list | pageBean | resultBean  value:List<Plan> | PageBean | ResultBean
	 */
	public Map<String, Object> find4RewardPlan(LotteryType lotteryType, String phaseNo, String planId, PlanType planType, ResultStatus resultStatus,
                                               Date beginDate, Date endDate, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据phasetype，彩期编码，方案状态列表，分页信息查询方案
	 * @param phaseType
	 * @param phaseNo
	 * @param planStatusList
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public List<Plan> findByPhaseType(PhaseType phaseType, String phaseNo, List<PlanStatus> planStatusList, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 方案查询统计，返回方案总金额，和税后中奖总金额
	 * @param pbeginDate TODO
	 * @param pendDate TODO
	 * @return
	 */
	public Map<String,Object> lotteryPlanStatistics(String userName,
                                                    String planId, LotteryType lotteryTypeId, PhaseType phaseType, String phase, PlanType planTypeId, SelectType selectTypeId,
                                                    PlayType playTypeId, List<String> planStatus, YesNoStatus uploadStatus, PublicStatus publicStatus,
                                                    ResultStatus resultStatus, YesNoStatus allowAutoFollow, PlanTicketStatus planTicketStatus, Long sourceId, Date rbeginDate, Date rendDate,
                                                    Date lbeginDate, Date lendDate, Date pbeginDate, Date pendDate, Date lastMatchTimeFrom, Date lastMatchTimeTo) throws ApiRemoteCallFailedException;
	/**
	 * 更新方案状态
	 * @param plan
	 * @param status
	 * @return
	 */
	public boolean updateStatus(Plan plan, PlanStatus status);
	/**
	 * 批量更新方案状态
	 * @param plans
	 * @param successList
     * @param failureList
	 * @return
	 */
	public boolean batchUpdateStatus(List<Plan> plans, List<String> successList, List<String> failureList) throws ApiRemoteCallFailedException;
	/**
	 * 更新方案状态和票状态
	 * @param plan
	 * @param status
	 * @return
	 */
	public boolean updateStatusAndTicketStatus(Plan plan, PlanStatus status, PlanTicketStatus ticketStatus);
	/**
	 * 更新方案票状态
	 * @param plan
	 * @param status
	 * @return
	 */
	public boolean updateTicketStatus(Plan plan, PlanTicketStatus status);
	/**
	 * 更新方案出票截止时间和销售截止时间
	 * @param planId
	 * @param deadline
     * @param saleDeadline
	 * @return
	 */
	public boolean updateDeadline(String planId, Date deadline, Date saleDeadline) throws ApiRemoteCallFailedException;
	/**
	 * 重算合买截止(北单，竞彩篮球，场次发生变化时重新计算)
	 * @param planIdList
     * @param changedList
	 * @param noChangedList
	 * @param failureList
	 * @return
	 */
	public void resetMatchByPlanId(List<String> planIdList, List<String> changedList, List<String> noChangedList, List<String> failureList) throws ApiRemoteCallFailedException, Exception;
	/**
	 * 设置置顶
	 * @param plan
	 * @param top
	 * @return
	 */
	public boolean updateTopStatus(Plan plan, int top) throws ApiRemoteCallFailedException;
	/**
	 * 查询过期未开奖方案
	 * @param pageBean
	 * @return
	 */
	public Map<String, Object> getOverduePlans(PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 根据彩种解析方案内容
	 * @param planId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public String parseContent(String planId) throws ApiRemoteCallFailedException;
	/**
	 * 修改提成比例
	 * @param planId
	 * @param rebate
	 * @return
	 */
	public boolean updateRebate(String planId, double rebate) throws ApiRemoteCallFailedException;
	/**
	 * 条件查询合买方案
	 */
	public Map<String, Object> findHMPlan(String username, String planId, LotteryType lotteryType, String phase, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改合买方案标题和内容
	 * @param plan 方案对象
	 */
	public boolean updatePlan(Plan plan) throws ApiRemoteCallFailedException;
	
	/**
	 * 查询单方案
	 * @param planNo
	 */
	public Plan getPlanById(String planNo) throws ApiRemoteCallFailedException;

    /**
     * 对方案进行退票处理
     * @param planList 要处理的方案
     * @param checkMatchNum 只退包含对应场次号的方案时要检查的场次号
     * @param successList 修改成功的方案编号列表
     * @param failureList 修改失败的方案编号列表
     * @param nochangeList 未做修改的方案编号列表
     * @throws ApiRemoteCallFailedException
     */
    public void returnPlanTicket(List<Plan> planList, String checkMatchNum,
                                 List<String> successList, List<String> failureList, List<String> nochangeList) throws ApiRemoteCallFailedException;
}