package web.action.cms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.util.WebUtils;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.cms.Category;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.domain.cms.NewsHistory;
import com.lehecai.admin.web.domain.cms.Resource;
import com.lehecai.admin.web.domain.user.Role;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.admin.web.enums.StaticPageType;
import com.lehecai.admin.web.export.NewsExport;
import com.lehecai.admin.web.service.cms.CategoryService;
import com.lehecai.admin.web.service.cms.NewsService;
import com.lehecai.admin.web.service.cms.ResourceService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.FileUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.admin.web.utils.UploadUtil;
import com.lehecai.admin.web.utils.VelocityUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.type.cooperator.Cooperator;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreFileUtils;

public class NewsAction extends BaseAction {
	private static final long serialVersionUID = 7784237096599220596L;

	private static final int NEWS_HISTORY_COUNT = 5;
	private static final String KEY_ORDER_BY_ID = "id";
	private static final String KEY_ORDER_BY_CREATETIME = "createTime";
	private static final String KEY_ORDER_BY_UPDATETIME = "updateTime";
	
	private Cooperator cooperator;
	
	private NewsService newsService;
	private CategoryService categoryService;
	private PermissionService permissionService;
	private ResourceService resourceService;
	
	private News news;
	private NewsHistory newsHistory;
	private Category category;
	private Category parentCategory;
	
	private List<News> newsList;
	private List<NewsHistory> newsHistoryList;
	private List<Category> categories;
	
	
	
	private String createTime;
	private Integer contentTypeId;
	private Integer disableEditorValue;
	
	private String defaultTemplatePath;
	private String defaultTargetPath;
	private Integer defaultStaticPageType;
	
	private String queryFlag;
	
	private VelocityUtil velocityUtil;
	
	private String userName;
	private Date beginTime;
	private Date endTime;
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	private String orderStr;
	private String orderView;
	private Long totalClick;
	
	private String variable;
	private String domain; 
	
	//数据导出文件名称
	private String fileName;
	private InputStream inputStream;
	
	
	/**
	 * 判断当前用户是否拥有此栏目的权限
	 * @param cateId
	 * @return
	 */
	protected boolean validCategory(Long cateId) {
		if (cateId == null || cateId == 0) {
			return false;
		}
		// 只有设置了合作商时才进行判断
		if (cooperator == null) {
			return true;
		}
		Category cate = categoryService.get(cateId);
		if (cate.getRoleId() == null) {
			logger.error("合作商只允许编辑经过授权的栏目, category id={}", cateId);
			return false;
		}
		UserSessionBean userSessionBean = (UserSessionBean)super.getSession().get(Global.USER_SESSION);
		if (userSessionBean == null) {
			logger.error("用户session为空");
			return false;
		}
		Role role = userSessionBean.getRole();
		if (role == null || role.getId() == null) {
			logger.error("用户角色为空");
			return false;
		}
		if (cate.getRoleId().longValue() == role.getId().longValue()) {
			return true;
		}
		return false;
	}
	
	protected String getNewsForwardUrl() {
		return this.getContextURI();
	}
	
	public String getNewsHistoryAction() {
		if (cooperator == null) {
			return "/cms/newsHistory.do";
		}
		return "/cms/" + StringUtils.uncapitalize(cooperator.getName()) + StringUtils.capitalize("newsHistory.do");
	}
	
	public String handle() {
		logger.info("进入查询新闻栏目");
		
		if (cooperator != null) {
			UserSessionBean userSessionBean = (UserSessionBean)super.getSession().get(Global.USER_SESSION);
			if (userSessionBean == null) {
				logger.error("用户session为空");
				super.setErrorMessage("用户session为空");
				return "failure";
			}
			Role role = userSessionBean.getRole();
			if (role == null || role.getId() == null) {
				logger.error("用户角色为空");
				super.setErrorMessage("用户角色为空");
				return "failure";
			}
			if (category == null) {
				category = new Category();
			}
			category.setRoleId(role.getId());
		}
		
		List<Category> list = categoryService.list(category);
		categories = new ArrayList<Category>();
		Map<Long, Category> parentMap = new HashMap<Long, Category>();
		if(list != null && list.size() != 0){
			for(Category sc : list){
				if(sc.getCaLevel() == 1){
					parentMap.put(sc.getId(), sc);			
					categories.add(sc);				
				} else {
					Category parent = parentMap.get(sc.getParentID());
					if (parent != null) {
						List<Category> children = parent.getChildren();
						children.add(sc);
					}
				}
			}
		}
		logger.info("查询新闻栏目结束");
		return "listCategories";
	}
	
	public String list() {
		logger.info("进入查询新闻列表");
		
		if (!validCategory(news.getCateID())) {
			logger.error("您没有权限操作栏目编码为{}的栏目", news.getCateID());
			super.setErrorMessage("您没有权限操作栏目编码为" + news.getCateID() + "的栏目");
			return "failure";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		newsList = newsService.list(news, super.getPageBean());
		PageBean pageBean = newsService.getPageBean(news, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		if (news != null && news.getCateID() != null) {		
			category = categoryService.get(news.getCateID());
			if (category != null && category.getCaLevel() == 2) {
				parentCategory = categoryService.get(category.getParentID());
			}
		}
		logger.info("查询新闻列表结束");
		return "list";
	}

	private boolean make(String templatePath, String targetPath, News news){
		Map<String, Object> context = new HashMap<String, Object>();
		category = categoryService.get(news.getCateID());
		context.put("news", news);
		context.put("category", category);
		
		// 相关新闻，取同栏目下最近的七条记录，剔除自身这条
		PageBean pageBean = new PageBean();
		pageBean.setPageSize(8);
		
		List<Long> cateIds = new ArrayList<Long>();
		cateIds.add(news.getCateID());
		
		List<News> latestNews = this.getNewsService().listByCateId(cateIds, pageBean);
		List<News> relatedNews = new ArrayList<News>();
		if (latestNews != null) {
			int i = 0;
			for (News n : latestNews) {
				if (n.getId().longValue() == news.getId().longValue()) {
					continue;
				}
				relatedNews.add(n);
				i ++;
				if (i == 7) {
					break;
				}
			}
		}
		
		context.put("relatedNews", relatedNews);

		try {
			String templateStr = CoreFileUtils.readFile(templatePath, CharsetConstant.CHARSET_UTF8);
			velocityUtil.build(targetPath, templateStr,
					context);
			logger.info("结束生成页面");
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	
	public String manage() {
		logger.info("进入添加新闻");
		
		if (news != null) {
			if (!validCategory(news.getCateID())) {
				logger.error("您没有权限操作栏目编码为{}的栏目", news.getCateID());
				super.setErrorMessage("您没有权限操作栏目编码为" + news.getCateID() + "的栏目");
				return "failure";
			}
			if(news.getTitle() == null || "".equals(news.getTitle())) {
				logger.error("新闻名称为空");
				super.setErrorMessage("新闻名称不能为空");
				return "failure";
			}

			YesNoStatus disableEditor = YesNoStatus.getItem(disableEditorValue);
			logger.info("diableEditorValue = {}, YesNoStatus = {}", disableEditorValue, disableEditor != null ? disableEditor.getValue() : "null");
			news.setDisableEditor(disableEditor);
			if(news.getId() != null){
				news.setCreateTime(newsService.get(news.getId()).getCreateTime());
				news.setUserId(newsService.get(news.getId()).getUserId());
			}
			news.setContentType(ContentType.getItem(contentTypeId));
			Long userId = news.getUserId();
			HttpServletRequest request = ServletActionContext.getRequest();
			UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
			if(userId == null){
				news.setUserId(userSessionBean.getUser().getId());
			}
			news.setContent(UploadUtil.replaceContentToDB(news.getContent()));
			news.setLastUpdateUserId(userSessionBean.getUser().getId());
			News newsTmp = newsService.manage(news);
			
			String url = "";
			String webRoot = "";
			String templatePath = "";
			String targetPath = "";
			try {
				webRoot = WebUtils.getRealPath(ServletActionContext.getServletContext(), "");
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
			if (newsTmp.getContentType().getValue() == ContentType.LINKTYPE.getValue()) {			 
				url = newsTmp.getLink();
			} else {
				Category category = categoryService.get(newsTmp.getCateID());
				
				if (category.getTemplatePath() == null || "".equals(category.getTemplatePath())) {
					templatePath = defaultTemplatePath.replace('/', File.separatorChar);
				} else {
					templatePath = category.getTemplatePath().replace('/', File.separatorChar);
				}
				if (category.getTargetPath() == null || "".equals(category.getTargetPath())) {
					Calendar c = Calendar.getInstance();
					if (newsTmp.getCreateTime() != null) {
						c.setTime(newsTmp.getCreateTime());
					}
					targetPath = defaultTargetPath + c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/";
				} else {
					targetPath = category.getTargetPath();
				}
				if (newsTmp.getName() != null && !"".equals(newsTmp.getName())) {
					targetPath = targetPath + newsTmp.getName();
				} else {
					targetPath = targetPath + newsTmp.getId();
				}
				targetPath = targetPath + "." + (category.getStaticPageType().getValue() == StaticPageType.DEFAULTTYPE.getValue() ? StaticPageType.getItem(defaultStaticPageType).getName() : category.getStaticPageType().getName());
				//获取已经存入数据库中的content值，将其中的{variable}替换为domain
				newsTmp.setContent(UploadUtil.replaceContentFromDB(newsTmp.getContent()));
				boolean flag = make(webRoot + templatePath, webRoot + targetPath, newsTmp);
				if (!flag) {
					logger.error("生成静态页面失败");
					super.setErrorMessage("生成静态页面失败,newsId:"+ newsTmp.getId());
					return "failure";
				}
				newsTmp.setContent(news.getContent());
				url = targetPath;
			}
			newsTmp.setUrl(url);
			newsService.manage(newsTmp);
		} else {
			logger.error("添加新闻错误，提交表单为空");
			super.setErrorMessage("添加新闻错误，提交表单为空");
			return "failure";
		}
		super.setForwardUrl(this.getNewsForwardUrl() + "?action=list&news.cateID=" + news.getCateID());
		logger.info("添加新闻结束");
		return "success";
	}
	
	
	public String updateAll() throws Exception {
		logger.info("进入更新全部新闻");
		final String webRoot = WebUtils.getRealPath(ServletActionContext.getServletContext(), "");
		PageBean page = new PageBean();
		page.setPageFlag(false);
		final Map<String, Resource> resourceList = UploadUtil.resourceMapping(resourceService.list(null, page));
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				PageBean pageBean = new PageBean();
				pageBean.setPageSize(200);
				int page = 1;
				while (true) {
					pageBean.setPage(page);
					
					List<News> list = newsService.list(null, pageBean);
					if (list == null || list.isEmpty()) {
						break;
					}
					for(News n : list){
						String url = "";
						String content = n.getContent();
						String templatePath = "";
						String targetPath = "";
						
						if(n.getContentType().getValue() == ContentType.LINKTYPE.getValue()){			 
							url = n.getLink();
						}else{
							Category category = categoryService.get(n.getCateID());
							
							if(category == null || category.getTemplatePath() == null || "".equals(category.getTemplatePath())){
								templatePath = defaultTemplatePath.replace('/', File.separatorChar);
							}else{
								templatePath = category.getTemplatePath().replace('/', File.separatorChar);
							}
							if(category == null || category.getTargetPath() == null || "".equals(category.getTargetPath())){
								Calendar c = Calendar.getInstance();
								if (n.getCreateTime() != null) {
									c.setTime(n.getCreateTime());
								}
								targetPath = defaultTargetPath + c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/";
							}else{
								targetPath = category.getTargetPath();
							}
							if(n.getName() != null && !"".equals(n.getName())){
								targetPath = targetPath + n.getName();
							}else{
								targetPath = targetPath + n.getId();
							}
							if(category == null){		
								targetPath = targetPath + "." +  StaticPageType.getItem(defaultStaticPageType).getName();
							}else{
								targetPath = targetPath + "." + (category.getStaticPageType().getValue() == StaticPageType.DEFAULTTYPE.getValue() ? StaticPageType.getItem(defaultStaticPageType).getName() : category.getStaticPageType().getName());			
							}
							n.setContent(UploadUtil.replaceContentForMakeHtml(resourceList, n.getContent()));
							boolean flag = make(webRoot + templatePath, webRoot + targetPath, n);
							if(!flag){
								continue;
							}
							url = targetPath;
						}
						n.setContent(content);
						n.setUrl(url);
						n.setUpdateTime(new Date());
						newsService.manage(n);
					}
					
					if (list.size() < pageBean.getPageSize()) {
						break;
					}
					
					// 准备读取下一页
					page ++;
				}
				
			}
			
		});
		thread.start();
		super.setForwardUrl("/cms/news.do");
		logger.info("更新全部新闻结束");
		return "success";
	}
	
	public String replaceImgPath() throws Exception {
		logger.info("进入更新全部新闻路径");
		PageBean page = new PageBean();
		page.setPageFlag(false);
		final Map<String, Resource> resourceList = UploadUtil.resourceMapping(resourceService.list(null, page));
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				PageBean pageBean = new PageBean();
				pageBean.setPageSize(200);
				int page = 1;
				while (true) {
					pageBean.setPage(page);
					List<News> list = newsService.list(new News(), pageBean);
					if (list == null || list.isEmpty()) {
						break;
					}
					for(News n : list){
						if(n.getContentType().getValue() == ContentType.NEWSTYPE.getValue()){
							String newContent = UploadUtil.replaceResourceOldData(resourceList, n.getContent());
							if (newContent != null && n.getContent() != null && !newContent.equals(n.getContent())) {
								n.setContent(newContent);
								newsService.manage(n);
							}
							
						}
					}
					
					if (list.size() < pageBean.getPageSize()) {
						break;
					}
					// 准备读取下一页
					page ++;
				}
				
			}
			
		});
		thread.start();
		super.setForwardUrl("/cms/news.do");
		logger.info("更新全部新闻结束");
		return "success";
	}
	

	public String replaceImgPathHistory() throws Exception {
		logger.info("进入更新历史版本新闻路径");
		PageBean page = new PageBean();
		page.setPageFlag(false);
		final Map<String, Resource> resourceList = UploadUtil.resourceMapping(resourceService.list(null, page));
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				PageBean pageBean = new PageBean();
				pageBean.setPageSize(200);
				int page = 1;
				while (true) {
					pageBean.setPage(page);
					List<NewsHistory> list = newsService.historyList(new NewsHistory(), pageBean);
					if (list == null || list.isEmpty()) {
						break;
					}
					for(NewsHistory n : list){
						if(n.getContentType().getValue() == ContentType.NEWSTYPE.getValue()){
							String newContent = UploadUtil.replaceResourceOldData(resourceList, n.getContent());
							if (newContent != null && n.getContent() != null && !newContent.equals(n.getContent())) {
								n.setContent(newContent);
								newsService.manageNewsHistory(n);
							}
							
						}
					}
					
					if (list.size() < pageBean.getPageSize()) {
						break;
					}
					// 准备读取下一页
					page ++;
				}
				
			}
			
		});
		thread.start();
		super.setForwardUrl("/cms/news.do");
		logger.info("更新历史版本新闻路径结束");
		return "success";
	}
	
	public String audit() {
		logger.info("进入审核新闻");
		News newsTmp = null;
		if (news != null && news.getId() != null) {
			newsTmp = newsService.get(news.getId());
			
			if (!validCategory(newsTmp.getCateID())) {
				logger.error("您没有权限操作栏目编码为{}的栏目", news.getCateID());
				super.setErrorMessage("您没有权限操作栏目编码为" + news.getCateID() + "的栏目");
				return "failure";
			}
			
			newsTmp.setValid(news.isValid());
			newsService.manage(newsTmp);
		} else {
			logger.error("审核新闻，编码为空");
			super.setErrorMessage("审核新闻，编码不能为空");
			return "failure";
		}
		logger.info("审核新闻结束");
		super.setForwardUrl(this.getNewsForwardUrl() + "?action=list&news.cateID=" + newsTmp.getCateID());
		return "success";
	}
	
	public String input() {
		logger.info("进入输入新闻信息");
		if (news != null) {
			if (news.getId() != null) {
				news = newsService.get(news.getId());
				contentTypeId = news.getContentType().getValue();
				news.setContentStatic(UploadUtil.replaceContentFromDB(news.getContent()));
			} else {
				news.setValid(true);
				news.setOrderView(0);
			}
			if (news.getCateID() != null) {
				if (!validCategory(news.getCateID())) {
					logger.error("您没有权限操作栏目编码为{}的栏目", news.getCateID());
					super.setErrorMessage("您没有权限操作栏目编码为" + news.getCateID() + "的栏目");
					return "failure";
				}
				category = categoryService.get(news.getCateID());
				if (category != null && category.getCaLevel() == 2) {
					parentCategory = categoryService.get(category.getParentID());
				}
			}
			if (news.getDisableEditor() == null) {
				disableEditorValue = YesNoStatus.NO.getValue();
			} else {
				disableEditorValue = news.getDisableEditor().getValue();
			}
			newsHistoryList = newsService.getHistoryListByCount(news.getId(), NEWS_HISTORY_COUNT);
		}
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看新闻详细信息");
		if (news != null && news.getId() != null) {
			news = newsService.get(news.getId());
			if (news != null && news.getCateID() != null) {
				if (!validCategory(news.getCateID())) {
					logger.error("您没有权限操作栏目编码为{}的栏目", news.getCateID());
					super.setErrorMessage("您没有权限操作栏目编码为" + news.getCateID() + "的栏目");
					return "failure";
				}
				category = categoryService.get(news.getCateID());
				if (category != null && category.getCaLevel() == 2) {
					parentCategory = categoryService.get(category.getParentID());
				}
			}
		} else {
			logger.info("查看新闻详细信息，编码为空");
			super.setErrorMessage("查看新闻详细信息，编码不能为空");
			return "failure";
		}
		logger.info("查看新闻详细信息结束");
		return "view";
	}
	
	public String preview() {
		logger.info("开始预览新闻");
		news.setCreateTimeStr(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:dd"));
		if (contentTypeId != null && contentTypeId == ContentType.LINKTYPE.getValue()) {
			super.setForwardUrl(news.getLink());
			return "forward";
		}
		String templatePath = "";
		
		if (!validCategory(news.getCateID())) {
			logger.error("您没有权限操作栏目编码为{}的栏目", news.getCateID());
			super.setErrorMessage("您没有权限操作栏目编码为" + news.getCateID() + "的栏目");
			return "failure";
		}
		
		category = categoryService.get(news.getCateID());
		
		if (category.getTemplatePath() == null || "".equals(category.getTemplatePath())) {
			templatePath = defaultTemplatePath;
		} else {
			templatePath = category.getTemplatePath();
		}
		super.setForwardUrl(templatePath);
		logger.info("预览新闻结束");
		return "vm";
	}
	
	public String viewNews() {
		logger.info("进入查看新闻详细信息");
		if (news != null && news.getId() != null) {
			news = newsService.get(news.getId());
			if (!validCategory(news.getCateID())) {
				logger.error("您没有权限操作栏目编码为{}的栏目", news.getCateID());
				super.setErrorMessage("您没有权限操作栏目编码为" + news.getCateID() + "的栏目");
				return "failure";
			}
			if (news.getContentType() != null && news.getContentType().getValue() == ContentType.LINKTYPE.getValue()) {
				super.setForwardUrl(news.getLink());
				return "forward";
			}
		} else {
			return "failure";
		}
		super.setForwardUrl(news.getUrl());
		logger.info("查看新闻详细信息结束");
		return "forward";
	}
	
	public String del() {
		logger.info("进入删除新闻");
		String webRoot = "";
		try {
			webRoot = WebUtils.getRealPath(ServletActionContext.getServletContext(), "");
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		if (news != null && news.getId() != null) {
			news = newsService.get(news.getId());
			if (!validCategory(news.getCateID())) {
				logger.error("您没有权限操作栏目编码为{}的栏目", news.getCateID());
				super.setErrorMessage("您没有权限操作栏目编码为" + news.getCateID() + "的栏目");
				return "failure";
			}
			newsService.del(news);
			FileUtil.rm(webRoot + news.getUrl().replace('/', File.separatorChar));
		} else {
			logger.error("删除新闻，编码为空");
			super.setErrorMessage("删除新闻，编码不能为空");
			return "failure";
		}
		super.setForwardUrl(this.getNewsForwardUrl() + "?action=list&news.cateID=" + news.getCateID());
		logger.info("删除新闻结束");
		return "forward";
	}
	
	//按条件查询
	//added by wangzl
	public String conditionQuery(){
		logger.info("进入按条件查询新闻");
		List<Category> list = categoryService.list(category);
		categories = new ArrayList<Category>();
		if(list != null && list.size() != 0){
			for(Category sc : list){
				if(sc.getCaLevel() == 1){
					categories.add(sc);				
				}
			}
		}
		if(queryFlag != null && queryFlag.equals("ifConditionQuery")){
			HttpServletRequest request = ServletActionContext.getRequest();
			List<Long> cateIds = null;
			Long cateID = news.getCateID();
			if(cateID != null && cateID != 0){
				cateIds = new ArrayList<Long>();
				Category queryCate = categoryService.get(cateID);
				cateIds.add(queryCate.getId());
				Category parentCategory = new Category();
				parentCategory.setParentID(queryCate.getId());
				List<Category> queryCateChildren = categoryService.list(parentCategory);
				for(Category ca : queryCateChildren){
					cateIds.add(ca.getId());
				}
			}
			if(userName != null && !"".equals(userName.trim())){
				User user = permissionService.getByUserName(userName);
				if(user != null){
					news.setUserId(user.getId());
				}else{
					news.setUserId(new Long(0));
				}
			}
			if(orderStr == null || orderStr.equals("")){
				orderStr = KEY_ORDER_BY_ID;
			}
			if(orderView == null || orderView.equals("")){
				orderView = ApiConstant.API_REQUEST_ORDER_DESC;
			}
			newsList = newsService.listByCondition(news, cateIds,beginTime,endTime,orderStr,orderView,super.getPageBean());
			PageBean pageBean = newsService.getPageBeanByCondition(news, cateIds,beginTime,endTime,orderStr,orderView,super.getPageBean());
			super.setPageString(PageUtil.getPageString(request, pageBean));
			totalClick = newsService.getClickByCondition(news, cateIds, beginTime, endTime, orderStr, orderView);
		}
		logger.info("按条件查询新闻结束");
		return "conditionQuery";
	}
	
	//按条件查询并导出
	//added by hewang
	public String conditionQueryExport(){
		logger.info("进入按条件查询新闻");
	
		if(queryFlag != null && queryFlag.equals("ifConditionQuery")){
			List<Long> cateIds = null;
			Long cateID = news.getCateID();
			if(cateID != null && cateID != 0){
				cateIds = new ArrayList<Long>();
				Category queryCate = categoryService.get(cateID);
				cateIds.add(queryCate.getId());
				Category parentCategory = new Category();
				parentCategory.setParentID(queryCate.getId());
				List<Category> queryCateChildren = categoryService.list(parentCategory);
				for(Category ca : queryCateChildren){
					cateIds.add(ca.getId());
				}
			}
			if(userName != null && !"".equals(userName.trim())){
				User user = permissionService.getByUserName(userName);
				if(user != null){
					news.setUserId(user.getId());
				}else{
					news.setUserId(new Long(0));
				}
			}
			if(orderStr == null || orderStr.equals("")){
				orderStr = KEY_ORDER_BY_ID;
			}
			if(orderView == null || orderView.equals("")){
				orderView = ApiConstant.API_REQUEST_ORDER_DESC;
			}
			newsList = newsService.listByCondition(news, cateIds,beginTime,endTime,orderStr,orderView,null);
			if (newsList != null && newsList.size() > 0) {
				try {
					Workbook workBook = null;
					HttpServletRequest request = ServletActionContext.getRequest();
					String baseUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath();
					workBook = NewsExport.exportConditonQueryData(newsList, baseUrl);
					fileName = "LHC_news_" + CoreDateUtils.formatDate(new Date()) + ".xls";
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					workBook.write(os);
					setInputStream(new ByteArrayInputStream(os.toByteArray()));
				} catch (Exception e) {
					logger.error("新闻数据导出错误，原因：{}", e.getMessage());
					super.setErrorMessage("新闻数据导出错误，原因：" + e.getMessage());
					return "failure";
				}
			} else {
				super.setErrorMessage("按条件查询新闻未能获取数据");
				return "failure";
			}
		}
		logger.info("按条件查询新闻结束");
		return "export";
	}
	
	public String saveHistory() {
		logger.info("进入添加新闻到历史版本库");
		if (news == null || news.getId() == null || news.getId() == 0) {
			logger.error("添加新闻到历史版本错误，新闻为空");
			super.setErrorMessage("添加新闻到历史版本错误，新闻为空");
			return "failure";
		}
		ContentType ct = contentTypeId == null ? ContentType.NEWSTYPE : ContentType.getItem(contentTypeId);
		YesNoStatus editorYesNo = disableEditorValue == null ? YesNoStatus.NO : YesNoStatus.getItem(disableEditorValue);
		news.setContentType(ct);
		news.setDisableEditor(editorYesNo);
		StringBuffer message = new StringBuffer();
			
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		Long userId = userSessionBean.getUser().getId();
		
		boolean flag = newsService.addHistory(news, userId, message);
		if(flag) {
			message.append("添加新闻到历史版本库成功");
			super.setSuccessMessage(message.toString());
			super.setForwardUrl("/cms/news.do?action=list&news.cateID="+news.getCateID());
			logger.info("结束添加新闻到历史版本库");
			return "success";
		} else {
			super.setErrorMessage(message.toString());
			super.setForwardUrl("/cms/news.do?action=list&news.cateID="+news.getCateID());
			logger.info("结束添加新闻到历史版本库");
			return "failure";
		}
	}
	
	public String restoreFromHistory() {
		logger.info("进入从版本库恢复新闻");
		if (newsHistory == null || newsHistory.getId() == null || newsHistory.getId() == 0) {
			logger.error("从版本库恢复新闻错误，ID为空");
			super.setErrorMessage("从版本库恢复新闻错误，ID为空");
			return "failure";
		}
		newsHistory = newsService.getHistory(newsHistory.getId());
		news = newsService.get(newsHistory.getNewsId());
		if (newsHistory.getCateID() != null && newsHistory.getCateID() != 0) {
			category = categoryService.get(newsHistory.getCateID());
			if (category.getCaLevel() == 1) {
				parentCategory = category;
			} else {
				parentCategory = categoryService.get(category.getParentID());
			}
		}
		
		if (news == null) {
			news = new News();
			news.setCateID(newsHistory.getCateID());
		}
		news.setAuthor(newsHistory.getAuthor());
		news.setCateID(newsHistory.getCateID());
		news.setContent(newsHistory.getContent());
		news.setContentType(newsHistory.getContentType());
		news.setEditor(newsHistory.getEditor());
		news.setFromPlace(newsHistory.getFromPlace());
		news.setImageUrl(newsHistory.getImageUrl());
		news.setKeyword(newsHistory.getKeyword());
		news.setLink(newsHistory.getLink());
		news.setName(newsHistory.getName());
		news.setValid(false);
		news.setOrderView(newsHistory.getOrderView());
		news.setShortContent(newsHistory.getShortContent());
		news.setTitle(newsHistory.getTitle());
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		Long userId = userSessionBean.getUser().getId();
		news.setLastUpdateUserId(userId);
		if (newsHistory.getDisableEditor() == null) {
			disableEditorValue = YesNoStatus.NO.getValue();
		}else {
			disableEditorValue = newsHistory.getDisableEditor().getValue();
		}
		contentTypeId = newsHistory.getContentType().getValue();
		
		newsHistoryList = newsService.getHistoryListByCount(news.getId(), NEWS_HISTORY_COUNT);
		
		return "inputForm";
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

	public List<News> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<News> newsList) {
		this.newsList = newsList;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(Integer contentTypeId) {
		this.contentTypeId = contentTypeId;
	}
	public List<ContentType> getContentTypes(){
		return ContentType.list;
	}
	public ContentType getLinkContentType(){
		return ContentType.LINKTYPE;
	}

	public String getDefaultTemplatePath() {
		return defaultTemplatePath;
	}

	public void setDefaultTemplatePath(String defaultTemplatePath) {
		this.defaultTemplatePath = defaultTemplatePath;
	}

	public String getDefaultTargetPath() {
		return defaultTargetPath;
	}

	public void setDefaultTargetPath(String defaultTargetPath) {
		this.defaultTargetPath = defaultTargetPath;
	}

	public Integer getDefaultStaticPageType() {
		return defaultStaticPageType;
	}

	public void setDefaultStaticPageType(Integer defaultStaticPageType) {
		this.defaultStaticPageType = defaultStaticPageType;
	}
	
	public List<YesNoStatus> getYesNoStatusList() {
		return YesNoStatus.getItems();
	}

	public Integer getDisableEditorValue() {
		return disableEditorValue;
	}

	public void setDisableEditorValue(Integer disableEditorValue) {
		this.disableEditorValue = disableEditorValue;
	}

	public VelocityUtil getVelocityUtil() {
		return velocityUtil;
	}

	public void setVelocityUtil(VelocityUtil velocityUtil) {
		this.velocityUtil = velocityUtil;
	}

	public String getQueryFlag() {
		return queryFlag;
	}

	public void setQueryFlag(String queryFlag) {
		this.queryFlag = queryFlag;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public List<NewsHistory> getNewsHistoryList() {
		return newsHistoryList;
	}

	public void setNewsHistoryList(List<NewsHistory> newsHistoryList) {
		this.newsHistoryList = newsHistoryList;
	}

	public NewsHistory getNewsHistory() {
		return newsHistory;
	}

	public void setNewsHistory(NewsHistory newsHistory) {
		this.newsHistory = newsHistory;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(KEY_ORDER_BY_ID, "文章编号");
		orderStrMap.put(KEY_ORDER_BY_CREATETIME, "发布时间");
		orderStrMap.put(KEY_ORDER_BY_UPDATETIME, "修改时间");
		return orderStrMap;
	}

	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}

	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		return orderViewMap;
	}

	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}

	public String getOrderStr() {
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getOrderView() {
		return orderView;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	
	public void setCooperatorId(Integer cooperatorId) {
		if (cooperatorId != null) {
			this.cooperator = Cooperator.getItem(cooperatorId);
		}
	}

	public Cooperator getCooperator() {
		return cooperator;
	}

	public Long getTotalClick() {
		return totalClick;
	}

	public void setTotalClick(Long totalClick) {
		this.totalClick = totalClick;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getVariable() {
		variable = UploadUtil.IMG_STATIC;
		return variable;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		domain = UploadUtil.IMG_URL;
		return domain;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
}
