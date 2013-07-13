package web.service.lottery;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.LotteryConfig;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.UnsupportedLotteryConfigException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseStatus;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.TerminalStatus;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.lotterydraw.ILotteryDrawFetcher;
import com.lehecai.core.lottery.fetcher.lotterydraw.LotteryDraw;
import com.lehecai.core.lottery.fetcher.lotterydraw.LotteryDrawPrizeItem;

/**
 * 彩期服务
 * @author leiming
 *
 */
public interface PhaseService {
	
	/**
	 * 按彩种获取指定彩期
	 * @param lotteryType
	 * @param phase
	 * @return
	 */
	public Phase get(LotteryType lotteryType, String phase);
	/**
	 * @param phaseType
	 * @param phase
	 * @return
	 */
	public Phase get(PhaseType phaseType, String phase);
	
	public List<Phase> findByPhaseNoBetween(LotteryType lotteryType, String beginPhase, String endPhase,
                                            PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 更新彩期的相关时间设置
	 * @param phase
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean updatePhaseTime(Phase phase) throws ApiRemoteCallFailedException;
	/**
	 * 根据条件获得彩期列表
	 * @param condition
	 * @param pageBean
	 * @return Map<String, Object> key:list | pageBean | resultBean  value:List<Phase> | PageBean | ResultBean
	 */
	public Map<String, Object> getPhases(Map<String, Object> condition, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 根据彩期类型和彩期号获得彩期
	 * @param phaseType
	 * @param phaseNo
	 * @return Phase
	 */
	public Phase getPhaseByPhaseTypeAndPhaseNo(PhaseType phaseType, String phaseNo) throws ApiRemoteCallFailedException;
	/**
	 * 批量生成彩期
	 * @param phaseType
	 * @param count
	 * @param batchStartTime
	 * @return ResultBean
	 * @throws ApiRemoteCallFailedException
	 */
	public ResultBean batchCreatePhase(PhaseType phaseType, Integer count, Date batchStartTime) throws ApiRemoteCallFailedException; 
	/**
	 * 创建指定彩期号的彩期
	 * @param phaseType
	 * @param phaseNo
	 * @param startSaleTime  开始销售时间 可空
	 * @param endSaleTime    结束销售时间 可空
	 * @param endTicketTime  停止出票时间 可空
	 * @param drawTime       开奖时间 可空
	 * @return
	 */
	public ResultBean createAssignPhase(PhaseType phaseType, String phaseNo, Date startSaleTime, Date endSaleTime, Date endTicketTime, Date drawTime) throws ApiRemoteCallFailedException;
	/**
	 * 创建指定彩期号的彩期
	 * @param phaseType
	 * @param phaseNo
	 * @return
	 */
	public ResultBean createAssignPhase(PhaseType phaseType, String phaseNo) throws ApiRemoteCallFailedException;
	/**
	 * 设置彩期状态
	 * @param phase
	 * @return
	 */
	public ResultBean modifyPhaseStatus(PhaseType phaseType, String phaseNo, PhaseStatus phaseStatus) throws ApiRemoteCallFailedException;
	/**
	 * 设置销售状态
	 * @param phase
	 * @return
	 */
	public ResultBean modifyForsaleStatus(PhaseType phaseType, String phaseNo, YesNoStatus forsaleStatus) throws ApiRemoteCallFailedException;
	/**
	 * 修改彩期终端状态
	 * @param phase
	 * @return
	 */
	public ResultBean modifyPhaseTerminalStatus(PhaseType phaseType, String phaseNo, TerminalStatus terminalStatus) throws ApiRemoteCallFailedException;
	/**
	 * 更新彩期
	 * @param phase
	 * @return
	 */
	public ResultBean updatePhase(Phase phase) throws ApiRemoteCallFailedException;
	/**
	 * 指定数量获得彩票类型的最新彩期列表 降序
	 * @param lotteryType
	 * @param count null或负数查询全部
	 * @return
	 */
	public List<Phase> getPhaseListByPhaseTypeAndCount(PhaseType phaseType, Integer count) throws ApiRemoteCallFailedException;
	/**
	 * 默认数量获得彩票类型的最新彩期列表 降序
	 * @param phaseType
	 * @return
	 */
	public List<Phase> getPhaseListByPhaseType(PhaseType phaseType) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取当前时间前后count期的彩期列表
	 * @param phaseType
	 * @param count
	 * @return
	 */
	public List<Phase> batchGetPhase(PhaseType phaseType, Integer count) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取指定彩种开始发售时间在当前之后的count期
	 * @param phaseType
	 * @param count
	 * @return
	 */
	public List<Phase> getPhaseListAfter(PhaseType phaseType, Integer count) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取指定彩种开始发售时间在当前之前的count期
	 * @param phaseType
	 * @param count
	 * @return
	 */
	public List<Phase> getPhaseListBefore(PhaseType phaseType, Integer count) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据彩种获取当前期
	 * @param phaseType
	 * @return
	 */
	public Phase getCurrentPhase(PhaseType phaseType) throws ApiRemoteCallFailedException;
	/**
	 * 获取当前期所在的页
	 * @param phaseType
	 * @param pageSize
	 * @return 当前期所在页数
	 * @throws ApiRemoteCallFailedException
	 */
	public PageBean getPageOfCurrentPhase(Map<String, Object> condition, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 获取彩种指定彩期的前后count期
	 * @param phaseType
	 * @param phase
	 * @param count
	 * @return
	 */
	public List<Phase> getAppointPhaseList(PhaseType phaseType, String phase, Integer count) throws ApiRemoteCallFailedException;
	/**
	 * 获取彩种指定彩期的之后的count期
	 * @param phaseType
	 * @param phase
	 * @param count
	 * @return
	 */
	public List<Phase> getAppointPhaseListAfter(PhaseType phaseType, String phase, Integer count) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取彩种指定彩期的之前的count期
	 * @param phaseType
	 * @param phase
	 * @param count
	 * @return
	 */
	public List<Phase> getAppointPhaseListBefore(PhaseType phaseType, String phase, Integer count) throws ApiRemoteCallFailedException;
	/**
	 * 获得某彩种离指定时间最近一期彩期
	 * @param phaseType
	 * @param appointTime  null表示当前时间
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Phase getNearestPhase(PhaseType phaseType, Date appointTime) throws ApiRemoteCallFailedException;
	
	/**
	 * 按彩种获取最新的状态为一开奖的彩期
	 * @param phaseType
	 * @param num 最近n期
	 * @return
	 * @throws ApiRemoteCallFailedException 
	 */
	public List<Phase> getLatestDrawedPhase(PhaseType phaseType, Integer num) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取截止日期为指定日期的彩期列表
	 * @param deadline
	 * @return
	 * @throws ApiRemoteCallFailedException 
	 */
	public List<Phase> findPhaseListByDeadline(Date deadline, List<LotteryType> lotteryTypeList) throws ApiRemoteCallFailedException;
	
	/**
	 * 将制定彩期设置为当前期
	 * @param phaseType
	 * @param phase
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public ResultBean setPhaseCurrent(PhaseType phaseType, String phase)throws ApiRemoteCallFailedException;
	/**
	 * 得到指定彩期的下一期
	 * @param phaseType
	 * @param phase
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Phase getNextPhase(PhaseType phaseType, String phase)throws ApiRemoteCallFailedException;
	/**
	 * 得到14场赛程列表
	 */
	Map<String, Object> getSfpMatches(Map<String, Object> condition, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 得到4,6场赛程列表
	 */
	Map<String, Object> getFb46Matches(Map<String, Object> condition, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 抓取开奖结果
	 * @param lotteryType
	 * @param fetcherType
	 * @param phaseNo
	 * @return
	 * @throws Exception
	 */
	public Phase fetchLotteryDraw(LotteryType lotteryType, FetcherType fetcherType, String phaseNo) throws Exception;
	/**
	 * 抓取开奖结果，返回本站奖级中文名定义的开奖结果
	 * @param fetcher
	 * @return
	 */
	public LotteryDraw fetchLotteryDraw(ILotteryDrawFetcher fetcher, FetcherType fetcherType, String phaseNo) throws Exception;
	
	/**
	 * 转换抓取的开奖结果
	 * @param fetchedResult
	 * @return
	 */
	public String convertFetchedResult(String fetchedResult,
                                       LotteryConfig lotteryConfig) ;
	/**
	 * 抓取福彩3D试机号
	 * @param lotteryType
	 * @param fetcherType
	 * @param phaseNo
	 * @return
	 * @throws Exception
	 */
	public Phase fetchFC3DSJH(LotteryType lotteryType, FetcherType fetcherType, String phaseNo) throws Exception;
	
	/**
	 * 转换抓取的开奖详情
	 * @param fetchedResult
	 * @return
	 */
	public String convertFetchedResultDetail(
            List<LotteryDrawPrizeItem> fetchedResultDetail, LotteryConfig lotteryConfig) throws UnsupportedLotteryConfigException ;
	/**
	 * 将抓取的开奖结果转换成彩期
	 * @param lotteryDraw
	 * @return
	 */
	public Phase convertToPhase(LotteryDraw lotteryDraw) ;
	
	/**
	 * 获取当前销售的彩期列表
	 */
	public List<Phase> findVenderOnSalePhases(PhaseType phaseType, String processCode) throws ApiRemoteCallFailedException;
	
	public ApiResponse invokePhaseHandlerRemoteCall(LotteryType lotteryType, String processCode) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取当前销售的彩期列表
	 * @author chirowong
	 */
	public List<Phase> findOnSalePhases(PhaseType phaseType, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 删除彩期
	 * 合并原有的江西时时彩、北单、胜负彩等彩期删除功能
	 * @param lotteryType
	 * @param phases
	 * @return
	 * 
	 * @author chirowong
	 */
	public ResultBean deletePhases(LotteryType lotteryType, List<String> phases) throws ApiRemoteCallFailedException;
}
