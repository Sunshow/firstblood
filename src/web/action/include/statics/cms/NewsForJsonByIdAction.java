package web.action.include.statics.cms;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.service.cms.CategoryService;
import com.lehecai.admin.web.service.cms.NewsService;
import com.opensymphony.xwork2.Action;

public class NewsForJsonByIdAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(NewsForJsonByIdAction.class);
	
	private static final long serialVersionUID = 2524999332385073306L;

	private NewsService newsService;
	private CategoryService categoryService;
	
	private String id;//新闻id
	private Integer title_length = 0;//标题长度

	
	public String handle(){
		logger.info("开始根据id获取新闻json数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject jsonObject = new JSONObject();
		
		if(id == null || "".equals(id)){
			rc = 1;
			message = "新闻id不能为空";
			logger.error("新闻id不能为空");
		}else{
			logger.info("新闻id={}", id);
			Long newsId = -1L;
			try {
				newsId = Long.valueOf(id);
			} catch (Exception e) {
				rc = 1;
				message = "新闻id格式错误";
				logger.error("新闻id格式错误");
			}
			if (rc == 0) {
				News news = newsService.get(newsId);
				if (news != null) {
					logger.info("查询新闻id={}", news.getId());
					jsonObject.put("news_id", news.getId());
					jsonObject.put("cate_id", news.getCateID());
					String viewTitle = news.getTitle() == null ? "" : news.getTitle();
					if(title_length > 0){
						if (title_length < viewTitle.length()) {
							viewTitle = viewTitle.substring(0, title_length) + "..."; 
						}
					}
					jsonObject.put("title", news.getTitle() == null ? "" : news.getTitle());
					jsonObject.put("img_url", news.getImageUrl() == null ? "" : news.getImageUrl());
					jsonObject.put("viewTitle", viewTitle);
					jsonObject.put("author", news.getAuthor() == null ? "" : news.getAuthor());
					jsonObject.put("short_content", news.getShortContent() == null ? "" : news.getShortContent());
					jsonObject.put("keyword", news.getKeyword() == null ? "" : news.getKeyword());
					jsonObject.put("editor", news.getEditor() == null ? "" : news.getEditor());
					//是否推荐
					jsonObject.put("is_recommend", news.isHeadNews() ? 1 : 0);
					jsonObject.put("from_place", news.getFromPlace() == null ? "" : news.getFromPlace());
					jsonObject.put("publish_date", news.getUpdateTimeStr() == null ? "" : news.getUpdateTimeStr());
					jsonObject.put("url", news.getUrl() == null ? "" : news.getUrl());
					jsonObject.put("content", news.getContent() == null ? "" : news.getContent());
					//文件名称
					jsonObject.put("name", news.getName() == null ? "" : news.getName());
					//是否最新
					jsonObject.put("is_current", news.isCurrentNew() ? 1 : 0);
					jsonObject.put("create_time", news.getCreateTimeStr() == null ? "" : news.getCreateTimeStr());
					//内容类型
					jsonObject.put("content_type", news.getContentType() == null ? -1 : news.getContentType().getValue());
					jsonObject.put("link", news.getLink() == null ? "" : news.getLink());
					jsonObject.put("order", news.getOrderView() == null ? 0 : news.getOrderView());
					//是否有效
					jsonObject.put("valid", news.isValid() ? 1 : 0);
					//是否禁用编辑器
					jsonObject.put("ckeditor_valid", news.getDisableEditor() == null ? -1 : news.getDisableEditor().getValue());
					
				} else {
					rc = 1;
					message = "未能获取到新闻";
				}
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", jsonObject);
		
		super.writeRs(response, json);
		
		logger.info("结束根据id获取新闻json数据");
		return Action.NONE;
	}
	
	public NewsService getNewsService() {
		return newsService;
	}
	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setTitle_length(Integer title_length) {
		this.title_length = title_length;
	}

	public Integer getTitle_length() {
		return title_length;
	}

}
