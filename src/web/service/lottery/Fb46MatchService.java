package web.service.lottery;

import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.lottery.Fb46Match;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.fetcher.FetcherType;

public interface Fb46MatchService {
	
	/**
	 * 根据彩期查询46场对阵信息
	 * @param phase
	 * @param type
	 * @return
	 */
	public List<Fb46Match> getFb46MatchListByPhase(String phase, PhaseType type);
	
	/**
	 * 批量添加对阵信息
	 * @param fb46Matchs
	 * @return
	 */
	public ResultBean batchCreatePhase(List<Fb46Match> fb46Matchs);
	
	/**
	 * 根据彩期抓取46场对阵信息
	 * @param phase
	 * @param type
	 * @param fetcherType TODO
	 * @return
	 */
	public List<Fb46Match> fetchFb46MatchListByPhase(String phase, PhaseType type, FetcherType fetcherType);
	
	/**
	 * 更新46场某场对阵信息(不建议直接使用, 请使用compareFb46Match)
	 * @param fb46Match                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateFb46Match(Fb46Match fb46Match);
	
	/**
	 * 批量新增46长对阵信息
	 * @param fb46MatchList
	 * @param successList
	 * @param failureList
	 * @param insertIdMap
	 * @return
	 */
	public boolean saveBatchFb46Match(List<Fb46Match> fb46MatchList, List<String> successList, List<String> failureList, Map<String, String> insertIdMap);
	
	/**
	 * 批量更新46长对阵信息
	 * @param fb46MatchList
	 * @param successList
	 * @param failureList
	 * @param fb46MatchMap
	 * @return
	 */
	public boolean updateBatchFb46Match(List<Fb46Match> fb46MatchList, List<String> successList, List<String> failureList, Map<Integer, Fb46Match> fb46MatchMap);
	/**
	 * 添加46场某场对阵信息(不建议直接使用, 请使用compareFb46Match)
	 * @param fb46Match
	 * @return
	 */
	public boolean saveFb46Match(Fb46Match fb46Match);
	
	/**
	 * 对比数据进行添加或跟新对阵信息
	 * @param pageFb46Match
	 * @param dbFb46Match
	 * @return
	 */
	public boolean compareFb46Match(Fb46Match pageFb46Match, Fb46Match dbFb46Match);
}
