package web.service.lottery;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.JclqDynamicDrawStatus;
import com.lehecai.core.lottery.JclqRaceStatus;
import com.lehecai.core.lottery.JclqStaticDrawStatus;
import com.lehecai.core.lottery.LotteryType;

public interface JclqRaceService {
	/**
	 * 根据比赛编码查询数据库中竞彩篮球对阵信息
	 * @param matchNum
	 * @return JclqRace
	 */
	JclqRace getRaceByMatchNum(String matchNum) throws ApiRemoteCallFailedException;
	/**
	 * 根据比赛日期和状态查询数据库中竞彩篮球对阵信息
	 * @param matchDate
	 * @param List<JclqRaceStatus>
	 * @param isToday matchDate为服务器当前时间则为true，否则为false
	 * @return List<JclqRace>
	 */
	List<JclqRace> getRaceListByDateAndStatus(String phaseNo, List<JclqRaceStatus> statuses, boolean isToday) throws ApiRemoteCallFailedException;

	/**
	 * 根据比赛开始准确时间,查询竞彩篮球对阵
	 * @param matchDate 比赛时间
	 * @return
	 */
	List<JclqRace> findJclqRacesByMatchDate(Date matchDate, PageBean pageBean);

	/**
	 * 根据指定的场次号获取场次对象
	 */
	List<JclqRace> findJclqRacesBySpecifiedMatchNum(List<String> mactchNumList, PageBean pageBean);

	/**
	 * 批量生成竞彩篮球对阵信息
	 * @param List<JclqRace>
	 * @return ResultBean
	 */
	ResultBean batchCreate(List<JclqRace> races) throws ApiRemoteCallFailedException;
	/**
	 * 批量生成竞彩篮球结果sp信息
	 * @param List<JclqRace>
	 * @return ResultBean
	 */
	ResultBean batchCreateSp(List<JclqRace> races) throws ApiRemoteCallFailedException;

	/**
	 * 更新竞彩篮球某场对阵信息
	 * @param JclqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRace(JclqRace race);
	/**
	 * 更新竞彩篮球某场结果sp信息
	 * @param JclqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRaceSp(JclqRace race);
	/**
	 * 更新竞彩篮球赛程状态
	 * @param JclqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRaceStatus(JclqRace race);
	/**
	 * 更新竞彩篮球赛程固定开奖状态
	 * @param JclqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRaceStaticDrawStatus(JclqRace race);
	/**
	 * 更新竞彩篮球赛程浮动开奖状态
	 * @param JclqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRaceDynamicDrawStatus(JclqRace race);
	
	/**
	 * 添加竞彩篮球某场对阵信息
	 * @param JclqRace
	 * @return
	 */
	public boolean saveRace(JclqRace race);
	
	/**
	 * 根据日志获取指定
	 * @param startOfficialDate 开始官方日期
	 * @param endOfficialDate 结束官方日期
	 * @param statuses 竞彩篮球状态列表,null则忽略
	 * @param max 最大数据条数
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public List<JclqRace> getJclqRaceDrawReadyList(String phaseNo, List<JclqRaceStatus> statuses, JclqStaticDrawStatus staticDrawStatus, JclqDynamicDrawStatus dynamicDrawStatus, int max) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取竞猜篮球赛事列表
	 * @param statusList
	 * @return
	 */
	public List<JclqRace> searchJclqRaceList(List<JclqRaceStatus> statusList, String orderStr, String orderView, Integer size) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取推荐赛程
	 * @param size
	 * @return
	 */
	public List<JclqRace> recommendJclqRace(Integer size);
	/**
	 * 获取已开启比赛数量
	 * @param size
	 * @return
	 */
	public int getJclqRaceSaleCount();
	/**
	 * 获取推荐赛程固定奖金胜负sp
	 * @param matchNums
	 * @param lotteryType
	 * @return
	 */
	public Map<String, Map<String, String>> getJclqCurrentStaticSp(List<String> matchNums, LotteryType lotteryType) throws ApiRemoteCallFailedException;
	
}
