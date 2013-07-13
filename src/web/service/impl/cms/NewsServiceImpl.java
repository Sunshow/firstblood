package web.service.impl.cms;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.NewsDao;
import com.lehecai.admin.web.dao.cms.NewsHistoryDao;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.domain.cms.NewsHistory;
import com.lehecai.admin.web.service.cms.CategoryService;
import com.lehecai.admin.web.service.cms.NewsService;

public class NewsServiceImpl implements NewsService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.NewsService#add()
	 */
	private NewsDao newsDao;
	private CategoryService categoryService;
	private NewsHistoryDao newsHistoryDao;
	
	public News manage(News news){
		return newsDao.merge(news);
	}

	public NewsDao getNewsDao() {
		return newsDao;
	}

	public void setNewsDao(NewsDao newsDao) {
		this.newsDao = newsDao;
	}

	@Override
	public List<News> list(News news, PageBean pageBean) {
		return newsDao.list(news, pageBean);
	}
	@Override
	public List<News> listByCateId(List<Long> cateIds, PageBean pageBean) {
		return newsDao.listByCateId(cateIds, pageBean);
	}
	
	@Override
	public List<News> listByCateIdOrderView(List<Long> cateIds, PageBean pageBean) {
		return newsDao.listByCateIdOrderView(cateIds, pageBean);
	}

	@Override
	public News get(Long ID) {
		return newsDao.get(ID);
	}

	@Override
	public void del(News news) {
		newsDao.del(news);
	}

	@Override
	public PageBean getPageBean(News news, PageBean pageBean) {
		return newsDao.getPageBean(news, pageBean);
	}

	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@Override
	public PageBean countByCateId(List<Long> cateIds, PageBean pageBean) {
		return newsDao.countByCateId(cateIds, pageBean);
	}

	@Override
	public List<News> listByCondition(News news,List<Long> cateIds,Date beginTime,Date endTime,String orderStr,String orderView,PageBean pageBean) {
		return newsDao.listByCondition(news,cateIds,beginTime,endTime,orderStr,orderView,pageBean);
	}

	@Override
	public PageBean getPageBeanByCondition(News news,List<Long> cateIds,Date beginTime,Date endTime,String orderStr,String orderView,PageBean pageBean) {
		return newsDao.getPageBeanByCondition(news,cateIds,beginTime,endTime,orderStr,orderView,pageBean);
	}
	
	@Override
	public Long getClickByCondition(News news, List<Long> cateIds,Date beginTime,Date endTime,String orderStr,String orderView){
		return newsDao.getClickByCondition(news, cateIds, beginTime, endTime, orderStr, orderView);
	}

	@Override
	public boolean addHistory(News news, Long userId, StringBuffer message) {
		if (news == null || news.getId() == null || news.getId() == 0) {
			message.append("添加新闻到历史版本错误，新闻为空");
			return false;
		}
		long version = getMaxVersion(news.getId());
		NewsHistory newsHistory = new NewsHistory();
		newsHistory.setAuthor(news.getAuthor());
		newsHistory.setCateID(news.getCateID());
		newsHistory.setContent(news.getContent());
		newsHistory.setContentType(news.getContentType());
		newsHistory.setDisableEditor(news.getDisableEditor());
		newsHistory.setCurrentNew(news.isCurrentNew());
		newsHistory.setEditor(news.getEditor());
		newsHistory.setFromPlace(news.getFromPlace());
		newsHistory.setHeadNews(news.isHeadNews());
		newsHistory.setImageUrl(news.getImageUrl());
		newsHistory.setKeyword(news.getKeyword());
		newsHistory.setLink(news.getLink());
		newsHistory.setName(news.getName());
		newsHistory.setNewsId(news.getId());
		newsHistory.setOrderView(news.getOrderView());
		newsHistory.setShortContent(news.getShortContent());
		newsHistory.setTitle(news.getTitle());
		newsHistory.setVersion(version + 1);
		newsHistory.setCreateRecordUserId(userId);
		newsHistory.setLastUpdateUserId(news.getLastUpdateUserId());
		newsHistoryDao.merge(newsHistory);
		return true;
	}
	
	private long getMaxVersion(Long newsId) {
		List<NewsHistory> list = newsHistoryDao.listByCount(newsId, 1);
		if (list == null || list.size() == 0) {
			return 0;
		}
		return list.get(0).getVersion();
	}
	
	@Override
	public List<NewsHistory> getHistoryListByCount(Long newsId,
			Integer count) {
		return newsHistoryDao.listByCount(newsId, count);
	}

	@Override
	public PageBean getHistoryPageBean(NewsHistory newsHistory, PageBean pageBean) {
		return newsHistoryDao.getPageBean(newsHistory, pageBean);
	}

	@Override
	public List<NewsHistory> historyList(NewsHistory newsHistory,
			PageBean pageBean) {
		return newsHistoryDao.list(newsHistory, pageBean);
	}
	
	@Override
	public NewsHistory getHistory(Long id) {
		return newsHistoryDao.get(id);
	}
	
	@Override
	public void delHistory(NewsHistory newsHistory) {
		newsHistoryDao.del(newsHistory);
	}

	@Override
	public NewsHistory manageNewsHistory (NewsHistory newsHistory) {
		return newsDao.mergeHistory(newsHistory);
	}
	
	public NewsHistoryDao getNewsHistoryDao() {
		return newsHistoryDao;
	}

	public void setNewsHistoryDao(NewsHistoryDao newsHistoryDao) {
		this.newsHistoryDao = newsHistoryDao;
	}

}
