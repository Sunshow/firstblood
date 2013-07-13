package web.dao.cms;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.domain.cms.NewsHistory;

public interface NewsDao {
	News merge(News news);
	List<News> list(News news, PageBean pageBean);
	List<News> listByCateId(List<Long> cateIds, PageBean pageBean);
	List<News> listByCateIdOrderView(List<Long> cateIds, PageBean pageBean);
	PageBean countByCateId(List<Long> cateIds, PageBean pageBean);
	PageBean getPageBean(News news, PageBean pageBean);
	News get(Long ID);
	void del(News news);
	List<News> listByCondition(News news, List<Long> cateIds, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean);
	PageBean getPageBeanByCondition(News news, List<Long> cateIds, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean);
	Long getClickByCondition(News news, List<Long> cateIds, Date beginTime, Date endTime, String orderStr, String orderView);
	
	/**
	 * 保存历史版本，用于更新img链接
	 * @param newsHistory
	 * @return
	 */
	NewsHistory mergeHistory(NewsHistory newsHistory);
}