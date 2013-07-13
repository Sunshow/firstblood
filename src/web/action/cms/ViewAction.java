package web.action.cms;

import java.util.List;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.admin.web.service.cms.NewsService;
import com.lehecai.admin.web.utils.DateUtil;

public class ViewAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	
	private NewsService newsService;
	
	private News news;
	
	private Long id;
	private String createTime;
	private String templateName;
	
	public String handle(){
		if(id != null){
			news = newsService.get(id);
			if(news == null){
				super.setErrorMessage("未找到id为"+id+"的新闻");
				return "failure";
			}
			createTime = DateUtil.formatDate(news.getCreateTime(), "yyyy-MM-dd HH:mm:dd");
			if(news.getContentType() != null && news.getContentType().getValue() == ContentType.LINKTYPE.getValue()){
				super.setForwardUrl(news.getLink());
				return "forward";
			}
		}else{
			super.setErrorMessage("传递参数id不能为空");
			return "failure";
		}
		if(templateName != null && !"".equals(templateName)){
			super.setForwardUrl("/WEB-INF/vm/"+templateName+".vm");
		}else{
			super.setForwardUrl("/WEB-INF/vm/news.vm");
		}
		return "vm";
	}
	
	public NewsService getNewsService() {
		return newsService;
	}

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}
	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public List<ContentType> getContentTypes(){
		return ContentType.list;
	}
	public ContentType getLinkContentType(){
		return ContentType.LINKTYPE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
