package web.action.cms;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.FlashModule;
import com.lehecai.admin.web.service.cms.FlashModuleService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.type.cooperator.Cooperator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class FlashModuleAction extends BaseAction{

    private static final long serialVersionUID = -6838652698149443384L;
    private FlashModuleService flashModuleService;

    protected static String JSON_PATH = "flashnews/";

    private String staticDir;

	private List<FlashModule> moduleList;
	
	private FlashModule flashModule;
	
	private String codename;
	
	private String jsonPath;
	
	private String update;

    private Cooperator cooperator;

    protected String getFlashModuleFilename() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String path = request.getSession().getServletContext().getRealPath("/")  + staticDir + JSON_PATH;
        String filename = codename;
        if (cooperator != null) {
            filename += "_" + StringUtils.uncapitalize(cooperator.getName());
        }
        filename += ".json";

        return path + filename;
    }

    protected String getFlashModuleForwardUrl() {
        return this.getContextURI() + "?codename=" + codename;
    }

    public String getFlashCategoryAction() {
        if (cooperator == null) {
            return "/cms/flashCategory.do";
        }
        return "/cms/" + StringUtils.uncapitalize(cooperator.getName()) + StringUtils.capitalize("flashCategory.do");
    }

	public String handle() {
		logger.info("进入查询Flash新闻列表");
		moduleList = flashModuleService.moduleList(this.getFlashModuleFilename());
		return "listCategories";
	}
	
	public String manage() {
		logger.info("进入更新Flash新闻信息");
		if (flashModule != null) {
			if (flashModule.getOrderView() == null) {
				logger.error("排序值为空");
				super.setErrorMessage("排序值不能为空");
				return "failure";
			}
			if (flashModule.getTitle() == null || "".equals(flashModule.getTitle().trim())) {
				logger.error("新闻标题为空");
				super.setErrorMessage("新闻标题不能为空");
				return "failure";
			}
			if (flashModule.getImgPath() == null || "".equals(flashModule.getImgPath().trim())) {
				logger.error("图片地址为空");
				super.setErrorMessage("图片地址不能为空");
				return "failure";
			}
			if (flashModule.getLink() == null || "".equals(flashModule.getLink().trim())) {
				logger.error("链接地址为空");
				super.setErrorMessage("链接地址不能为空");
				return "failure";
			}
			moduleList = flashModuleService.moduleList(this.getFlashModuleFilename());
			for(int i = 0;i<moduleList.size();i++) {
				FlashModule fc = (FlashModule)moduleList.get(i);
				if (fc.getId().equals(flashModule.getId())) {
					if (update !=null && update.equals("1")) {
						fc = flashModule;
						moduleList.remove(i);
						moduleList.add(fc);
						flashModuleService.manage(moduleList, this.getFlashModuleFilename());
						
						super.setForwardUrl(this.getFlashModuleForwardUrl());
						return "success";
					} else {
						logger.error("模块代号已存在");
						super.setErrorMessage("模块代号已存在");
						return "failure";
					}
				}
			}
			
			flashModule.setId(String.valueOf(DateUtil.getTimtstamp()));//用时间戳做id
			moduleList.add(flashModule);
			flashModuleService.manage(moduleList, this.getFlashModuleFilename());
		} else {
			logger.error("更新新闻信息错误，提交表单为空");
			super.setErrorMessage("更新新闻信息错误，提交表单为空");
			return "failure";
		}
		super.setForwardUrl(this.getFlashModuleForwardUrl());
		logger.info("更新Flash新闻信息结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入Flash新闻信息");
		if (flashModule != null) {
			if (flashModule.getId() != null) {
				moduleList = flashModuleService.moduleList(this.getFlashModuleFilename());
                for (FlashModule module : moduleList) {
					if (flashModule.getId().equals(module.getId())) {
						flashModule = module;
                        break;
					}
				}
			}
		}
		return "inputForm";
	}

	public String del() {
		logger.info("进入删除Flash新闻");
		if (flashModule != null) {
			if (flashModule.getId() != null) {
				moduleList = flashModuleService.moduleList(this.getFlashModuleFilename());
				for(int i = 0;i<moduleList.size();i++) {
					FlashModule fc = moduleList.get(i);
					if (flashModule.getId().equals(fc.getId())) {
						moduleList.remove(i);
					}
				}
				flashModuleService.manage(moduleList, this.getFlashModuleFilename());
			}
		}
		super.setForwardUrl(this.getFlashModuleForwardUrl());
		logger.info("删除Flash新闻结束");
		return "success";
	}

	public String getCodename() {
		return codename;
	}

	public void setCodename(String codename) {
		this.codename = codename;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public List<FlashModule> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<FlashModule> moduleList) {
		this.moduleList = moduleList;
	}

	public FlashModule getFlashModule() {
		return flashModule;
	}

	public void setFlashModule(FlashModule flashModule) {
		this.flashModule = flashModule;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setFlashModuleService(FlashModuleService flashModuleService) {
		this.flashModuleService = flashModuleService;
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
