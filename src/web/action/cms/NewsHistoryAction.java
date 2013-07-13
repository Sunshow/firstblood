package web.action.cms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.NewsHistory;
import com.lehecai.admin.web.service.cms.NewsService;
import com.lehecai.admin.web.utils.PageUtil;

public class NewsHistoryAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(NewsHistoryAction.class);
	
	private NewsService newsService;
	private List<NewsHistory> newsHistoryList;
	
	private NewsHistory newsHistory;
	
	
	public String handle() {
		return "list";
	}
	
	public String query() {
		logger.info("进入按条件查询历史版本新闻");
		HttpServletRequest request = ServletActionContext.getRequest();
		newsHistoryList = newsService.historyList(newsHistory, super.getPageBean());
		PageBean pageBean = newsService.getHistoryPageBean(newsHistory, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		
		logger.info("按条件查询历史版本新闻结束");
		return "list";
	}
	
	public String del() {
		if (newsHistory == null || newsHistory.getId() == null || newsHistory.getId() == 0) {
			logger.error("删除新闻历史记录错误，新闻或新闻id为空");
			super.setErrorMessage("删除新闻历史记录错误，新闻或新闻id为空");
			super.setForwardUrl("/cms/newsHistory.do");
			return "failure";
		}
		newsHistory = newsService.getHistory(newsHistory.getId());
		newsService.delHistory(newsHistory);
		logger.info("删除新闻历史记录成功");
		super.setForwardUrl("/cms/newsHistory.do?action=query&newsHistory.newsId=" + newsHistory.getNewsId());
		return "success";
	}
	
	public String view() {
		if (newsHistory == null || newsHistory.getId() == null || newsHistory.getId() == 0) {
			logger.error("查看新闻历史记录错误，新闻或新闻id为空");
			super.setErrorMessage("查看新闻历史记录错误，新闻或新闻id为空");
			super.setForwardUrl("/cms/newsHistory.do");
			return "failure";
		}
		newsHistory = newsService.getHistory(newsHistory.getId());
		logger.info("查询新闻历史记录成功");
		return "view";
	}

	public NewsService getNewsService() {
		return newsService;
	}

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	public NewsHistory getNewsHistory() {
		return newsHistory;
	}

	public void setNewsHistory(NewsHistory newsHistory) {
		this.newsHistory = newsHistory;
	}

	public List<NewsHistory> getNewsHistoryList() {
		return newsHistoryList;
	}

	public void setNewsHistoryList(List<NewsHistory> newsHistoryList) {
		this.newsHistoryList = newsHistoryList;
	}
	
}