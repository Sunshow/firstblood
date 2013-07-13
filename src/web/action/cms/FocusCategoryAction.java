package web.action.cms;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.FocusCategory;
import com.lehecai.admin.web.service.cms.FocusCategoryService;
import com.lehecai.core.type.cooperator.Cooperator;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFileUtils;

public class FocusCategoryAction extends BaseAction {

	private static final long serialVersionUID = -1533455492089730466L;

    protected static String JSON_PATH = "focusnews/";
    protected static String JSON_FILE = "focusCategory";
    protected static String JSON_FILE_SUFFIX = ".json";
	
	private FocusCategoryService focusCategoryService;
	private List<FocusCategory> categoryList;
	private FocusCategory focusCategory;
	
	private String update;
	private String staticDir;
	
	private Cooperator cooperator;

	protected String getFocusCategoryListFilename() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String path = request.getSession().getServletContext().getRealPath("/")  + staticDir + JSON_PATH;
		String filename = JSON_FILE;
		if (cooperator != null) {
			filename += "_" + StringUtils.uncapitalize(cooperator.getName());
		}
		filename += JSON_FILE_SUFFIX;
		
		return path + filename;
	}

	protected String getFocusCategoryFilename(FocusCategory focusCategory) {
		HttpServletRequest request = ServletActionContext.getRequest();
		return request.getSession().getServletContext().getRealPath("/") + this.getFocusCategoryFilepath(focusCategory);
	}
	
	protected String getFocusCategoryFilepath(FocusCategory focusCategory) {
		String filename = focusCategory.getCodename();
		if (cooperator != null) {
			filename += "_" + StringUtils.uncapitalize(cooperator.getName());
		}
		filename += JSON_FILE_SUFFIX;
		return staticDir + JSON_PATH + filename;
	}
	
	protected String getFocusCategoryForwardUrl() {
		return this.getContextURI();
	}
	
	public String getFocusNewsAction() {
		if (cooperator == null) {
			return "/cms/focusNews.do";
		}
		return "/cms/" + StringUtils.uncapitalize(cooperator.getName()) + StringUtils.capitalize("focusNews.do");
	}
	
	public String handle() {
		logger.info("进入查询焦点新闻模块列表");
		categoryList = focusCategoryService.categoryList(this.getFocusCategoryListFilename());
		return "listCategories";
	}
	
	public String manage() {
		logger.info("进入更新焦点新闻模块");
		if (focusCategory != null) {
			if (focusCategory.getName() == null || "".equals(focusCategory.getName())) {
				logger.error("新闻块名称为空");
				super.setErrorMessage("新闻块名称不能为空");
				return "failure";
			}
			if (focusCategory.getCodename() == null || "".equals(focusCategory.getCodename())) {
				logger.error("新闻块代号为空");
				super.setErrorMessage("新闻块代号不能为空");
				return "failure";
			}
			categoryList = focusCategoryService.categoryList(this.getFocusCategoryListFilename());
			for (int i = 0;i<categoryList.size();i++) {
				FocusCategory fc = (FocusCategory)categoryList.get(i);
				if (focusCategory.getCodename().equals(fc.getCodename())) {
					if (update != null && update.equals("1")) {
						focusCategory.setPath(fc.getPath());
						fc = focusCategory;
						categoryList.remove(i);
						categoryList.add(fc);
						focusCategoryService.manage(categoryList, this.getFocusCategoryListFilename());

						super.setForwardUrl(this.getFocusCategoryForwardUrl());
						return "success";
					} else {
						logger.error("新闻块代号已存在");
						super.setErrorMessage("新闻块代号已存在");
						return "failure";
					}
				}
			}
			focusCategory.setPath(this.getFocusCategoryFilepath(focusCategory));
			categoryList.add(focusCategory);
			focusCategoryService.manage(categoryList, this.getFocusCategoryListFilename());
			
			CoreFileUtils.createFile(this.getFocusCategoryFilename(focusCategory), "", CharsetConstant.CHARSET_UTF8);
		} else {
			logger.info("更新模块错误，提交表单为空");
			super.setErrorMessage("更新模块错误，提交表单为空");
			return "failure";
		}
		super.setForwardUrl(this.getFocusCategoryForwardUrl());
		logger.info("更新焦点新闻模块结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入焦点新闻模块信息");
		if (focusCategory != null) {
			if (focusCategory.getCodename() != null) {
				categoryList = focusCategoryService.categoryList(this.getFocusCategoryListFilename());
				for (int i = 0; i < categoryList.size(); i++) {
					FocusCategory f = (FocusCategory) categoryList.get(i);
					if (focusCategory.getCodename().equals(f.getCodename())) {
						focusCategory = f;
					}
				}
			}
		}
		return "inputForm";
	}

	public String del() {
		logger.info("进入删除焦点新闻模块");
		if (focusCategory != null) {
			if (focusCategory.getCodename() != null) {
				categoryList = focusCategoryService.categoryList(this.getFocusCategoryListFilename());
				for (int i = 0;i<categoryList.size();i++) {
					FocusCategory fc = (FocusCategory)categoryList.get(i);
					if (focusCategory.getCodename().equals(fc.getCodename())) {
						categoryList.remove(i);
						focusCategoryService.delFile(fc.getPath());
					}
				}
				focusCategoryService.manage(categoryList, this.getFocusCategoryListFilename());
			}
		}
		super.setForwardUrl(this.getFocusCategoryForwardUrl());
		logger.info("删除焦点新闻模块结束");
		return "success";
	}
	
	public void check() {
		logger.info("进入检验焦点新闻块代号");
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean flag = true;
		categoryList = focusCategoryService.categoryList(this.getFocusCategoryListFilename());
		for (int i = 0;i<categoryList.size();i++) {
			FocusCategory fc = (FocusCategory)categoryList.get(i);
			if (focusCategory.getCodename().equals(fc.getCodename())) {
				if (update !=null && update.equals("0")) {
					flag = false;
				}
			}
		}
		
		PrintWriter out = null;
		response.setContentType("text/html; charset=utf-8");
		try {
			out = response.getWriter();
			//不能用println，会多打出一个换行
			out.print(flag);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public FocusCategoryService getFocusCategoryService() {
		return focusCategoryService;
	}
	
	public void setFocusCategoryService(FocusCategoryService focusCategoryService) {
		this.focusCategoryService = focusCategoryService;
	}

	public List<FocusCategory> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<FocusCategory> categoryList) {
		this.categoryList = categoryList;
	}

	public FocusCategory getFocusCategory() {
		return focusCategory;
	}

	public void setFocusCategory(FocusCategory focusCategory) {
		this.focusCategory = focusCategory;
	}
	
	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getStaticDir() {
		return staticDir;
	}

	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}

	public void setCooperatorId(Integer cooperatorId) {
		if (cooperatorId != null) {
			this.cooperator = Cooperator.getItem(cooperatorId);
		}
	}
	
}
