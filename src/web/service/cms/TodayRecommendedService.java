package web.service.cms;

import com.lehecai.admin.web.domain.cms.TodayRecommendedItem;

import java.util.List;

/**
 * 今日推荐业务逻辑层接口
 * @author yanweijie
 *
 */
public interface TodayRecommendedService {
	List<TodayRecommendedItem> itemList(String filename);
	void itemManage(List<TodayRecommendedItem> todayRecommendedItemList, String filename);
}
