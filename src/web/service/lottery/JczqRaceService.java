package web.service.lottery;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.lottery.JczqChampionRace;
import com.lehecai.core.api.lottery.JczqChampionSecondRace;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.JczqDynamicDrawStatus;
import com.lehecai.core.lottery.JczqRaceStatus;
import com.lehecai.core.lottery.JczqStaticDrawStatus;
import com.lehecai.core.lottery.LotteryType;

public interface JczqRaceService {
	/**
	 * 根据比赛编码查询数据库中竞彩足球对阵信息
	 * @param matchNum
	 * @return JczqRace
	 */
	JczqRace getRaceByMatchNum(String matchNum) throws ApiRemoteCallFailedException;
	/**
	 * 根据彩期和状态查询数据库中竞彩足球对阵信息
	 * @param phaseNo
	 * @param List<JczqRaceStatus>
	 * @param isToday matchDate为服务器当前时间则为true，否则为false
	 * @return List<JczqRace>
	 */
	List<JczqRace> getRaceListByDateAndStatus(String phaseNo, List<JczqRaceStatus> statuses, boolean isToday) throws ApiRemoteCallFailedException;

	/**
	 * 根据比赛开始准确时间,查询竞彩足球对阵
	 * @param matchDate 比赛时间
	 * @return
	 */
	List<JczqRace> findJczqRacesByMatchDate(Date matchDate, PageBean pageBean);

	/**
	 * 根据指定的场次号获取场次对象
	 */
	List<JczqRace> findJczqRacesBySpecifiedMatchNum(List<String> mactchNumList, PageBean pageBean);

	/**
	 * 批量生成竞彩足球对阵信息
	 * @param List<JczqRace>
	 * @return ResultBean
	 */
	ResultBean batchCreate(List<JczqRace> races) throws ApiRemoteCallFailedException;
	/**
	 * 批量生成竞彩足球结果sp信息
	 * @param List<JczqRace>
	 * @return ResultBean
	 */
	ResultBean batchCreateSp(List<JczqRace> races) throws ApiRemoteCallFailedException;

	/**
	 * 更新竞彩足球某场对阵信息
	 * @param JczqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRace(JczqRace race);
	/**
	 * 更新竞彩足球某场结果sp信息
	 * @param JczqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRaceSp(JczqRace race);
	/**
	 * 更新竞彩足球赛程状态
	 * @param JczqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRaceStatus(JczqRace race);
	/**
	 * 更新竞彩足球赛程固定开奖状态
	 * @param JczqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRaceStaticDrawStatus(JczqRace race);
	/**
	 * 更新竞彩足球赛程浮动开奖状态
	 * @param JczqRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateRaceDynamicDrawStatus(JczqRace race);
	
	/**
	 * 添加竞彩足球某场对阵信息
	 * @param JczqRace
	 * @return
	 */
	public boolean saveRace(JczqRace race);
	
	/**
	 * 根据日志获取指定
	 * @param startOfficialDate 开始官方日期
	 * @param endOfficialDate 结束官方日期
	 * @param statuses 竞彩足球状态列表,null则忽略
	 * @param max 最大数据条数
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public List<JczqRace> getJczqRaceDrawReadyList(Date startOfficialDate, Date endOfficialDate, List<JczqRaceStatus> statuses, JczqStaticDrawStatus staticDrawStatus, JczqDynamicDrawStatus dynamicDrawStatus, int max) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据彩期获取开奖准备的赛程
	 * @param phaseNo
	 * @param statuses
	 * @param staticDrawStatus
	 * @param dynamicDrawStatus
	 * @param max
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public List<JczqRace> getJczqRaceDrawReadyList(String phaseNo, List<JczqRaceStatus> statuses, JczqStaticDrawStatus staticDrawStatus, JczqDynamicDrawStatus dynamicDrawStatus, int max) throws ApiRemoteCallFailedException;
	/**
	 * 获取竞猜足球赛事列表
	 * @param statusList
	 * @return
	 */
	public List<JczqRace> searchJczqRaceList(List<JczqRaceStatus> statusList, String orderStr, String orderView, Integer size) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取推荐赛程
	 * @param size
	 * @return
	 */
	public List<JczqRace> recommendJczqRace(Integer size);
	/**
	 * 获取已开启比赛数量
	 * @param size
	 * @return
	 */
	public int getJczqRaceSaleCount();
	/**
	 * 获取推荐赛程固定奖金胜负sp
	 * @param matchNums
	 * @param lotteryType
	 * @return
	 */
	public Map<String, Map<String, String>> getJczqCurrentStaticSp(List<String> matchNums, LotteryType lotteryType) throws ApiRemoteCallFailedException;
	
	/**
	 * 查询数据库中竞彩足球猜冠军对阵信息
	 * @param phase
	 * @param statuses
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public List<JczqChampionRace> getChampionRaceList(String phase, List<JczqRaceStatus> statuses) throws ApiRemoteCallFailedException;
	/**
	 * 查询数据库中竞彩足球猜冠亚军对阵信息
	 * @param phase
	 * @param statuses
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public List<JczqChampionSecondRace> getChampionSecondRaceList(String phase, List<JczqRaceStatus> statuses) throws ApiRemoteCallFailedException;
	/**
	 * 添加竞彩足球猜冠军某场对阵信息
	 * @param race
	 * @return
	 */
	public boolean saveChampionRace(JczqChampionRace race);
	/**
	 * 添加竞彩足球猜冠亚军某场对阵信息
	 * @param race
	 * @return
	 */
	public boolean saveChampionSecondRace(JczqChampionSecondRace race);
	/**
	 * 更新竞彩足球猜冠军某场对阵信息
	 * @param race                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateChampionRace(JczqChampionRace race);
	/**
	 * 更新竞彩足球猜冠亚军某场对阵信息
	 * @param race                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateChampionSecondRace(JczqChampionSecondRace race);
	/**
	 * 批量生成竞彩足球猜冠军对阵信息
	 * @param races
	 * @return ResultBean
	 */
	ResultBean batchCreateChampion(List<JczqChampionRace> races) throws ApiRemoteCallFailedException;
	/**
	 * 批量生成竞彩足球猜冠亚军对阵信息
	 * @param races
	 * @return ResultBean
	 */
	ResultBean batchCreateChampionSecond(List<JczqChampionSecondRace> races) throws ApiRemoteCallFailedException;
	
	/**
	 * 更新竞彩足球猜冠军赛程状态
	 * @param race                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateChampionRaceStatus(JczqChampionRace race);
	/**
	 * 更新竞彩足球猜冠亚军赛程状态
	 * @param race                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateChampionSecondRaceStatus(JczqChampionSecondRace race);
}
