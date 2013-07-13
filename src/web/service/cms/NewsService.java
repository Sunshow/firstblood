package web.service.cms;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.domain.cms.NewsHistory;

public interface NewsService {
	News manage(News news);
	List<News> list(News news, PageBean pageBean);
	List<News> listByCateId(List<Long> cateIds, PageBean pageBean);
	List<News> listByCateIdOrderView(List<Long> cateIds, PageBean pageBean);
	PageBean countByCateId(List<Long> cateIds, PageBean pageBean);
	PageBean getPageBean(News news, PageBean pageBean);
	News get(Long ID);
	void del(News news);
	List<News> listByCondition(News news, List<Long> cateIds, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean);
	PageBean getPageBeanByCondition(News news, List<Long> cateIds, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean);

	boolean addHistory(News news, Long userId, StringBuffer message);
	NewsHistory getHistory(Long id);
	void delHistory(NewsHistory newsHistory);
	List<NewsHistory> historyList(NewsHistory newsHistory, PageBean pageBean);
	List<NewsHistory> getHistoryListByCount(Long newsId, Integer count);
	PageBean getHistoryPageBean(NewsHistory newsHistory, PageBean pageBean);
	Long getClickByCondition(News news, List<Long> cateIds, Date beginTime, Date endTime, String orderStr, String orderView);
	
	/**
	 * 更新历史记录，用于img的src替换
	 * @param newsHistory
	 * @return
	 */
	NewsHistory manageNewsHistory(NewsHistory newsHistory);
}