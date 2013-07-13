package web.service.cms;

import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.domain.cms.BasketballAnalysisData;
import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.type.cooperator.Cooperator;

/**
 * 推荐赛程业务逻辑层接口
 * @author yanweijie
 *
 */
public interface RecommendRaceService {


	/**
	 * 更新推荐赛程
	 */
	void merge(RecommendRace recommendRace);
	
	/**
	 * 删除推荐赛程
	 */
	void delete(Long recommendRaceId);
	
	/**
	 * 查询所有赛程
	 */
	List<RecommendRace> findList(Cooperator cooperator, LotteryType lotteryType);
	
	/**
	 * 根据matchIds获取分析数据Map，目前仅支持平均欧赔
	 * @param matchIds
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<Long, BasketballAnalysisData> getBasketballAnalysisData(String matchIds) throws ApiRemoteCallFailedException ;
}
