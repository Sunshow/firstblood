package web.dao.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.type.cooperator.Cooperator;

/**
 * 推荐赛程数据访问层接口
 * @author yanweijie
 *
 */
public interface RecommendRaceDao {

	/**
	 * 更新推荐赛程
	 */
	void merge(RecommendRace recommendRace);
	
	/**
	 * 根据Id获取赛程
	 */
	RecommendRace getById(Long id);
	
	/**
	 * 删除推荐赛程
	 */
	void delete(RecommendRace recommendRace);
	
	/**
	 * 查询所有赛程
	 */
	List<RecommendRace> findList(Cooperator cooperator, LotteryType lotteryType);
	
}
