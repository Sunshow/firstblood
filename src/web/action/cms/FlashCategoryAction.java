package web.action.cms;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.FlashCategory;
import com.lehecai.admin.web.service.cms.FlashCategoryService;
import com.lehecai.core.type.cooperator.Cooperator;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FlashCategoryAction extends BaseAction{

    private static final long serialVersionUID = 9143648313639803265L;
    private FlashCategoryService flashCategoryService;

    protected static String JSON_PATH = "flashnews/";
    protected static String JSON_FILE = "flashCategory";
    protected static String JSON_FILE_SUFFIX = ".json";

	private List<FlashCategory> categoryList;
	
	private FlashCategory flashCategory;
	
	private String staticDir;
	
	private String update;

    private Cooperator cooperator;

    protected String getFlashCategoryListFilename() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String path = request.getSession().getServletContext().getRealPath("/")  + staticDir + JSON_PATH;
        String filename = JSON_FILE;
        if (cooperator != null) {
            filename += "_" + StringUtils.uncapitalize(cooperator.getName());
        }
        filename += JSON_FILE_SUFFIX;

        return path + filename;
    }

    protected String getFlashCategoryFilename(FlashCategory flashCategory) {
        HttpServletRequest request = ServletActionContext.getRequest();
        return request.getSession().getServletContext().getRealPath("/") + this.getFocusCategoryFilepath(flashCategory);
    }

    protected String getFocusCategoryFilepath(FlashCategory flashCategory) {
        String filename = flashCategory.getCodename();
        if (cooperator != null) {
            filename += "_" + StringUtils.uncapitalize(cooperator.getName());
        }
        filename += JSON_FILE_SUFFIX;
        return staticDir + JSON_PATH + filename;
    }

    protected String getFlashCategoryForwardUrl() {
        return this.getContextURI();
    }

    public String getFlashModuleAction() {
        if (cooperator == null) {
            return "/cms/flashModule.do";
        }
        return "/cms/" + StringUtils.uncapitalize(cooperator.getName()) + StringUtils.capitalize("flashModule.do");
    }


    public String handle() {
		logger.info("进入查询Flash模块列表");
		categoryList = flashCategoryService.categoryList(this.getFlashCategoryListFilename());
		return "listCategories";
	}
	
	public String manage() {
		logger.info("进入更新Flash模块");
		if (flashCategory != null) {
			if (flashCategory.getName() == null || "".equals(flashCategory.getName())) {
				logger.info("模块名称为空");
				super.setErrorMessage("模块名称不能为空");
				return "failure";
			}
			if (flashCategory.getCodename() == null || "".equals(flashCategory.getCodename())) {
				logger.info("模块代号为空");
				super.setErrorMessage("模块代号不能为空");
				return "failure";
			}
			categoryList = flashCategoryService.categoryList(this.getFlashCategoryListFilename());
			for (int i = 0; i < categoryList.size(); i++) {
				FlashCategory fc = (FlashCategory)categoryList.get(i);
				if (flashCategory.getCodename().equals(fc.getCodename())) {
					if (update !=null && update.equals("1")) {
						flashCategory.setPath(fc.getPath());
						fc = flashCategory;
						categoryList.remove(i);
						categoryList.add(fc);
						flashCategoryService.manage(categoryList, this.getFlashCategoryListFilename());
						
						super.setForwardUrl(this.getFlashCategoryForwardUrl());
						return "success";
					} else {
						logger.info("模块代号已存在");
						super.setErrorMessage("模块代号已存在");
						return "failure";
					}
				}
			}
			flashCategory.setPath(this.getFocusCategoryFilepath(flashCategory));
			categoryList.add(flashCategory);
			flashCategoryService.manage(categoryList, this.getFlashCategoryListFilename());

            CoreFileUtils.createFile(this.getFlashCategoryFilename(flashCategory), "", CharsetConstant.CHARSET_UTF8);
		} else {
			logger.info("更新模块错误，提交表单为空");
			super.setErrorMessage("更新模块错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl(this.getFlashCategoryForwardUrl());
		logger.info("更新Flash模块结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入Flash模块信息");
		if (flashCategory != null) {
			if (flashCategory.getCodename() != null) {
				categoryList = flashCategoryService.categoryList(this.getFlashCategoryListFilename());
                for (FlashCategory category : categoryList) {
					if (flashCategory.getCodename().equals(category.getCodename())) {
						flashCategory = category;
                        break;
					}
				}
			}
		}
		return "inputForm";
	}

	public String del() {
		logger.info("进入删除Flash模块");
		if (flashCategory != null) {
			if (flashCategory.getCodename() != null) {
				categoryList = flashCategoryService.categoryList(this.getFlashCategoryListFilename());
				for (int i = 0;i<categoryList.size();i++) {
					FlashCategory fc = categoryList.get(i);
					if (flashCategory.getCodename().equals(fc.getCodename())) {
						categoryList.remove(i);
						flashCategoryService.delFile(fc.getPath());
					}
				}
				flashCategoryService.manage(categoryList, this.getFlashCategoryListFilename());
			}
		}
		super.setForwardUrl(this.getFlashCategoryForwardUrl());
		logger.info("删除Flash模块结束");
		return "success";
	}
	
	public void check() {
		logger.info("进入检验Flash模块代号");
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean flag = true;
		categoryList = flashCategoryService.categoryList(this.getFlashCategoryListFilename());
		if (update != null && "1".equals(update.trim())) {
			flag = true;
		} else {
			for (int i = 0;i < categoryList.size();i++) {
				FlashCategory fc = categoryList.get(i);
				if (fc.getCodename().equals(flashCategory.getCodename())) {
					flag = false;
					break;
				}
			}
		}
		PrintWriter out = null;
		response.setContentType("text/html; charset=utf-8");
		try {
			out = response.getWriter();
			out.print(flag);
			out.flush();
			out.close();
		} catch (IOException e) {
            logger.error(e.getMessage(), e);
		}
	}

	public void setFlashCategoryService(FlashCategoryService flashCategoryService) {
		this.flashCategoryService = flashCategoryService;
	}

	public List<FlashCategory> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<FlashCategory> categoryList) {
		this.categoryList = categoryList;
	}

	public FlashCategory getFlashCategory() {
		return flashCategory;
	}

	public void setFlashCategory(FlashCategory flashCategory) {
		this.flashCategory = flashCategory;
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
