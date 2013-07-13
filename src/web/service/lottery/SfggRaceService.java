package web.service.lottery;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.SfggRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.DcRaceStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.fetcher.FetcherType;

public interface SfggRaceService {
	/**
	 * 获取本论单场推荐的N场比赛
	 * @param phaseNo TODO
	 * @param count
	 * @return
	 */
	public List<SfggRace> getRecommendSfggRace(String phaseNo, int count);
	/**
	 * 根据彩期查询单场对阵信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public List<SfggRace> getSfggRaceListByPhase(String phase);
	/**
	 * 查询单场对阵信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public SfggRace getSfggRaceByMatchNum(String phase, Long matchNum) throws ApiRemoteCallFailedException;
	
	/**
	 * 批量添加对阵信息
	 * @param sfggRaces
	 * @return
	 */
	public ResultBean batchCreatePhase(List<SfggRace> sfggRaces);
	
	/**
	 * 根据彩期抓取单场对阵信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public List<SfggRace> fetchSfggRaceListByPhase(String phase, FetcherType fecherType);
	
	/**
	 * 根据彩期抓取单场sp和比分信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public List<SfggRace> fetchSfggRaceSpListByPhase(String phase, FetcherType fecherType);
	
	/**
	 * 更新单场某场对阵信息(不建议直接使用, 请使用compareSfggRace)
	 * @param sfggRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateSfggRace(SfggRace sfggRace);
	
	/**
	 * 更新单场结果Sp值
	 * @param sfggRace
	 * @return
	 */
	public boolean updateSfggRaceSp(SfggRace sfggRace) throws ApiRemoteCallFailedException;
	
	/**
	 * 添加单场某场对阵信息(不建议直接使用, 请使用compareSfggRace)
	 * @param sfggRace
	 * @return
	 */
	public boolean saveSfggRace(SfggRace sfggRace);
	
	/**
	 * 对比数据进行添加或跟新对阵信息
	 * @param pageSfggRace
	 * @param dbSfggRace
	 * @return
	 */
	public boolean compareSfggRace(SfggRace pageSfggRace, SfggRace dbSfggRace);
	
	/**
	 * 根据赛程流水id修改对阵状态
	 * @param id 流水号id
	 * @param status
	 * @return
	 */
	public boolean updateStatus(String id, DcRaceStatus status);
	
	/**
	 * 批量修改结果sp值信息
	 * @param sfggRaces
	 * @return
	 */
	public ResultBean batchUpdateSfggRaceSp(List<SfggRace> sfggRaces);
	/**
	 * 根据状态列表查询指定彩期的单场对阵
	 * @param statusList
	 * @param phaseNo
	 * @return
	 */
	public List<SfggRace> findSfggRaceByStatus(List<String> statusList, String phaseNo);
	
	/**
	 * 查询比赛日期查询单场对阵
	 */
	List<SfggRace> findSfggRacesByMatchDate(Date matchDate);

	/**
	 * 指定彩期,比赛开始准确时间,查询单场对阵
	 * @param matchDate 比赛时间
	 * @param phase 期号
	 * @return
	 */
	List<SfggRace> findSfggRacesByMatchDateAndPhase(Date matchDate, String phase, PageBean pageBean);

	/**
	 * 指定彩期,场次号获取场次对象
	 * @param matchDate 比赛时间
	 * @param phase 期号
	 * @return
	 */
	List<SfggRace> findSfggRacesBySpecifiedMatchNumAndPhase(List<String> matchNumList, String phase, PageBean pageBean);

	/**
	 * 根据Excel表获取指定彩期的赛程
	 * @param excelFile
	 * @param phase
	 * @param phaseNo
	 * @return
	 */
	public List<Object> getSfggRaceByExcel(File excelFile, Phase phase, String phaseNo);
	/**
	 * 根据指定彩期的在售单场对阵数量
	 * @param excelFile
	 * @param phaseNo
	 * @return
	 */
	public int getSfggRaceSaleCount(String phaseNo);
	
	/**
	 * 获取指定场次的当前即时SP值
	 * @param matchIdList
	 * @param lotteryType
	 * @return matchId:{投注项:SP值}
	 */
	public Map<String, Map<String, String>> getCurrentInstantSP(List<String> matchIdList, LotteryType lotteryType) throws ApiRemoteCallFailedException;
}
