package web.action.cms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.Resource;
import com.lehecai.admin.web.domain.cms.ResourceCategory;
import com.lehecai.admin.web.service.cms.ResourceCategoryService;
import com.lehecai.admin.web.service.cms.ResourceService;
import com.lehecai.admin.web.utils.FileUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.admin.web.utils.UploadUtil;

public class ResourceAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;

	private Logger logger = LoggerFactory.getLogger(ResourceAction.class);
	
	private ResourceService resourceService;
	private ResourceCategoryService resourceCategoryService;
	
	private Resource resource;
	private ResourceCategory resourceCategory;
	private ResourceCategory parentResourceCategory;
	
	private List<Resource> resourceList;
	private List<ResourceCategory> categories;
	
	private String saveDir;
	
	private File file;
	private String fileFileName;//File xxx; xxxFileName
	//upload的子目录
	private static final String RESOURCE_PATH = "resources";
	
	public String handle() {
		logger.info("进入查询资源类别列表");
		List<ResourceCategory> list = resourceCategoryService.list(resourceCategory);
		categories = new ArrayList<ResourceCategory>();
		Map<Long, ResourceCategory> parentMap = new HashMap<Long, ResourceCategory>();
		if (list != null && list.size() != 0) {
			for (ResourceCategory sc : list) {
				if (sc.getReLevel() == 1) {
					parentMap.put(sc.getId(), sc);			
					categories.add(sc);				
				} else {
					ResourceCategory parent = parentMap.get(sc.getParentID());
					if (parent != null) {
						List<ResourceCategory> children = parent.getChildren();
						children.add(sc);
					}
				}
			}
		}
		logger.info("查询资源类别列表结束");
		return "listCategories";
	}
	
	public String list() {
		logger.info("进入查询资源列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		resourceList = resourceService.list(resource, super.getPageBean());
		PageBean pageBean = resourceService.getPageBean(resource, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		if (resource != null && resource.getCateID() != null) {		
			resourceCategory = resourceCategoryService.get(resource.getCateID());
			if (resourceCategory != null && resourceCategory.getReLevel() == 2) {
				parentResourceCategory = resourceCategoryService.get(resourceCategory.getParentID());
			}
		}
		logger.info("查询资源列表结束");
		return "list";
	}
	
	public String manage() {
		logger.info("进入添加资源");
		if (resource != null) {
			try {
				HttpServletRequest request = ServletActionContext.getRequest();
				String webRoot = WebUtils.getRealPath(request.getSession().getServletContext(), "");
				
				String categoryPath = "";
				ResourceCategory rc = resourceCategoryService.get(resource.getCateID());
				if (rc != null) {
					categoryPath = rc.getDirectory() == null ? "" : rc.getDirectory() + "/";
				}
				String fileUrl = saveDir + (saveDir.endsWith("/") ? "" : "/") + RESOURCE_PATH + "/" + categoryPath + (new Date()).getTime() +  fileFileName.substring(fileFileName.lastIndexOf(".")).trim().toLowerCase();
 				String pathName = saveDir + (saveDir.endsWith("/") ? "" : "/") + RESOURCE_PATH + "/" + categoryPath;
				FileUtil.uploadFileAndMkdir(file, (webRoot + fileUrl), webRoot + pathName);
				
				resource.setPath(UploadUtil.IMG_STATIC + fileUrl);
				resourceService.manage(resource);
			} catch (FileNotFoundException e) {
				logger.error("添加资源，未找到文件，{}", e);
				super.setErrorMessage("未找到文件");
				return "failure";
			} catch (IllegalStateException e) {
				logger.error("添加资源，上传文件错误，{}", e);
				super.setErrorMessage("上传文件错误");
				return "failure";
			} catch (IOException e) {
				logger.error("添加资源，上传文件错误，{}", e);
				super.setErrorMessage("上传文件错误");
				return "failure";
			}
		} else {
			logger.error("添加资源，提交表单为空");
			super.setErrorMessage("添加资源错误，提交表单为空");
			return "failure";
		}
		super.setForwardUrl("/cms/resource.do?action=list&resource.cateID="+resource.getCateID());
		logger.info("添加资源结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入资源信息");
		if (resource != null) {
			if (resource.getId() != null) {
				resource = resourceService.get(resource.getId());
			} else {
				resource.setValid(true);
			}
			if (resource.getCateID() != null) {		
				resourceCategory = resourceCategoryService.get(resource.getCateID());
				if (resourceCategory != null && resourceCategory.getReLevel() == 2) {
					parentResourceCategory = resourceCategoryService.get(resourceCategory.getParentID());
				}
			}
		}
		return "inputForm";
	}
	
	public String preview() {
		logger.info("进入预览资源");
		if (resource != null && resource.getId() != null) {
			resource = resourceService.get(resource.getId());
		} else {
			logger.error("预览资源，编码为空");
			super.setErrorMessage("预览资源，编码不能为空");
			return "failure";
		}
		super.setForwardUrl(UploadUtil.replacePathForPreview(resource.getPath()));
		logger.info("预览资源结束");
		return "forward";
	}
	
	public String del() {
		logger.info("进入删除资源");
		if (resource != null && resource.getId() != null) {
			HttpServletRequest request = ServletActionContext.getRequest();
			String webRoot;
			try {
				webRoot = WebUtils.getRealPath(request.getSession().getServletContext(), "");
				resource = resourceService.get(resource.getId());
				File f = new File(webRoot+resource.getPath());
				if(f.exists()){
					f.delete();
				}
				resourceService.del(resource);
			} catch (FileNotFoundException e) {
				logger.error("删除资源，未找到文件，{}",e);
				super.setErrorMessage("删除资源，未找到文件");
				return "failure";
			}
		} else {
			logger.error("删除资源，编码为空");
			super.setErrorMessage("删除资源，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/cms/resource.do?action=list&resource.cateID="+resource.getCateID());
		logger.info("删除资源结束");
		return "forward";
	}
	
	public ResourceService getResourceService() {
		return resourceService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public List<Resource> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<Resource> resourceList) {
		this.resourceList = resourceList;
	}

	public ResourceCategory getResourceCategory() {
		return resourceCategory;
	}

	public void setResourceCategory(ResourceCategory resourceCategory) {
		this.resourceCategory = resourceCategory;
	}

	public ResourceCategoryService getResourceCategoryService() {
		return resourceCategoryService;
	}

	public void setResourceCategoryService(ResourceCategoryService resourceCategoryService) {
		this.resourceCategoryService = resourceCategoryService;
	}

	public List<ResourceCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<ResourceCategory> categories) {
		this.categories = categories;
	}

	public ResourceCategory getParentResourceCategory() {
		return parentResourceCategory;
	}

	public void setParentResourceCategory(ResourceCategory parentResourceCategory) {
		this.parentResourceCategory = parentResourceCategory;
	}

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

}
