package web.service.lottery;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.DcRaceStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.fetcher.FetcherType;

public interface DcRaceService {
	/**
	 * 获取本论单场推荐的N场比赛
	 * @param phaseNo TODO
	 * @param count
	 * @return
	 */
	public List<DcRace> getRecommendDcRace(String phaseNo, int count);
	/**
	 * 根据彩期查询单场对阵信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public List<DcRace> getDcRaceListByPhase(String phase);
	/**
	 * 查询单场对阵信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public DcRace getDcRaceByMatchNum(String phase, Long matchNum) throws ApiRemoteCallFailedException;
	
	/**
	 * 批量添加对阵信息
	 * @param dcRaces
	 * @return
	 */
	public ResultBean batchCreatePhase(List<DcRace> dcRaces);
	
	/**
	 * 根据彩期抓取单场对阵信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public List<DcRace> fetchDcRaceListByPhase(String phase, FetcherType fecherType);
	
	/**
	 * 根据彩期抓取单场sp和比分信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public List<DcRace> fetchDcRaceSpListByPhase(String phase, FetcherType fecherType);
	
	/**
	 * 更新单场某场对阵信息(不建议直接使用, 请使用compareDcRace)
	 * @param dcRace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateDcRace(DcRace dcRace);
	
	/**
	 * 
	 * @param dcRaceList
	 * @param successList
	 * @param failureList
	 * @return
	 */
	public boolean updateBatchDcRace(List<DcRace> dcRaceList, List<String> successList, List<String> failureList, Map<Integer, DcRace> dcRaceMap);
	
	
	/**
	 * 更新单场结果Sp值
	 * @param dcRace
	 * @return
	 */
	public boolean updateDcRaceSp(DcRace dcRace) throws ApiRemoteCallFailedException;
	
	/**
	 * 添加单场某场对阵信息(不建议直接使用, 请使用compareDcRace)
	 * @param dcRace
	 * @return
	 */
	public boolean saveDcRace(DcRace dcRace);
	

	/**
	 * 批量添加单场对阵信息
	 * @param dcRace
	 * @param successList
	 * @param failureList
	 * @return
	 */
	public boolean saveBatchDcRace(List<DcRace> dcRaceList, List<String> successList, List<String> failureList);
	
	/**
	 * 对比数据进行添加或跟新对阵信息
	 * @param pageDcRace
	 * @param dbDcRace
	 * @return
	 */
	public boolean compareDcRace(DcRace pageDcRace, DcRace dbDcRace);
	
	/**
	 * 根据赛程流水id修改对阵状态
	 * @param id 流水号id
	 * @param status
	 * @return
	 */
	public boolean updateStatus(String id, DcRaceStatus status);
	
	/**
	 * 批量修改结果sp值信息
	 * @param dcRaces
	 * @return
	 */
	public ResultBean batchUpdateDcRaceSp(List<DcRace> dcRaces);
	/**
	 * 根据状态列表查询指定彩期的单场对阵
	 * @param statusList
	 * @param phaseNo
	 * @return
	 */
	public List<DcRace> findDcRaceByStatus(List<String> dcStatusList, String phaseNo);
	
	/**
	 * 查询比赛日期查询单场对阵
	 */
	List<DcRace> findDcRacesByMatchDate(Date matchDate);

	/**
	 * 指定彩期,比赛开始准确时间,查询单场对阵
	 * @param matchDate 比赛时间
	 * @param phase 期号
	 * @return
	 */
	List<DcRace> findDcRacesByMatchDateAndPhase(Date matchDate, String phase, PageBean pageBean);

	/**
	 * 指定彩期,场次号获取场次对象
	 * @param matchDate 比赛时间
	 * @param phase 期号
	 * @return
	 */
	List<DcRace> findDcRacesBySpecifiedMatchNumAndPhase(List<String> matchNumList, String phase, PageBean pageBean);

	/**
	 * 根据Excel表获取指定彩期的赛程
	 * @param excelFile
	 * @param phase
	 * @param phaseNo
	 * @return
	 */
	public List<Object> getDcRaceByExcel(File excelFile, Phase phase, String phaseNo);
	/**
	 * 根据指定彩期的在售单场对阵数量
	 * @param excelFile
	 * @param phaseNo
	 * @return
	 */
	public int getDcRaceSaleCount(String phaseNo);
	
	/**
	 * 获取指定场次的当前即时SP值
	 * @param matchIdList
	 * @param lotteryType
	 * @return matchId:{投注项:SP值}
	 */
	public Map<String, Map<String, String>> getDcCurrentInstantSP(List<String> matchIdList, LotteryType lotteryType) throws ApiRemoteCallFailedException;
}
