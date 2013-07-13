package web.dao.cms;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.NewsHistory;

public interface NewsHistoryDao {
	NewsHistory merge(NewsHistory newsHistory);
	NewsHistory get(Long id);
	void del(NewsHistory newsHistory);
	List<NewsHistory> listByCount(Long newsId, Integer count);
	List<NewsHistory> list(NewsHistory newsHistory, PageBean pageBean);
	PageBean getPageBean(NewsHistory newsHistory, PageBean pageBean);
}