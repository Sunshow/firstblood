package web.service.lottery;

import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.lottery.SfpArrange;
import com.lehecai.core.lottery.fetcher.FetcherType;

public interface SfpArrangeService {
	
	/**
	 * 根据彩期查询14场（任9场）对阵信息
	 * @param phase
	 * @return
	 */
	public List<SfpArrange> getSfpArrangeListByPhase(String phase);
	
	/**
	 * 批量添加对阵信息
	 * @param sfpArranges
	 * @return
	 */
	public ResultBean batchCreatePhase(List<SfpArrange> sfpArranges);
	
	/**
	 * 根据彩期抓取14场（任9场）对阵信息
	 * @param phase
	 * @param fetcherType TODO
	 * @return
	 */
	public List<SfpArrange> fetchSfpArrangeListByPhase(String phase, FetcherType fetcherType);
	
	/**
	 * 更新14场某场对阵信息(不建议直接使用, 请使用compareSfpArrange)
	 * @param sfpArrange                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * @return
	 */
	public boolean updateSfpArrange(SfpArrange sfpArrange);
	
	/**
	 * 批量保存14场信息
	 * @param sfpArrangeList
	 * @param successList
	 * @param failureList
	 * @param insertIdMap
	 * @return
	 */
	public boolean saveBatchSfpArrange(List<SfpArrange> sfpArrangeList, List<String> successList, List<String> failureList, Map<String, String> insertIdMap);
	
	/**
	 * 批量更新14场信息
	 * @param sfpArrangeList
	 * @param successList
	 * @param failureList
	 * @param sfpArrangeMap
	 * @return
	 */
	public boolean updateBatchSfpArrange(List<SfpArrange> sfpArrangeList, List<String> successList, List<String> failureList, Map<Integer, SfpArrange> sfpArrangeMap);
	
	/**
	 * 添加14场某场对阵信息(不建议直接使用, 请使用compareSfpArrange)
	 * @param sfpArrange
	 * @return
	 */
	public boolean saveSfpArrange(SfpArrange sfpArrange);
	
	/**
	 * 对比数据进行添加或跟新对阵信息
	 * @param pageSfpArrange
	 * @return
	 */
	public boolean compareSfpArrange(SfpArrange pageSfpArrange, SfpArrange dbSfpArrange);
	
}
