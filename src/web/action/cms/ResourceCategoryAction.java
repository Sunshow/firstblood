package web.action.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.ResourceCategory;
import com.lehecai.admin.web.service.cms.ResourceCategoryService;
import com.lehecai.core.util.CoreDateUtils;

public class ResourceCategoryAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(ResourceCategoryAction.class);
	
	private ResourceCategoryService resourceCategoryService;
	
	private ResourceCategory resourceCategory;
	private ResourceCategory parentResourceCategory;
	
	private List<ResourceCategory> categories;
	
	public String handle() {
		logger.info("进入查询资源类别");
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
		logger.info("查询资源类别结束");
		return "list";
	}
	
	public String manage() {
		logger.info("进入添加资源类别");
		if (resourceCategory != null) {
			if (resourceCategory.getName() == null || "".equals(resourceCategory.getName())) {
				logger.error("资源类别名称为空");
				super.setErrorMessage("资源类别名称不能为空");
				return "failure";
			}
			if (resourceCategory.getParentID() == null) {
				resourceCategory.setParentID(0L);
			}
			resourceCategoryService.manage(resourceCategory);
		} else {
			logger.error("添加资源类别错误，提交表单为空");
			super.setErrorMessage("添加资源类别错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/cms/resourceCategory.do");
		logger.info("添加资源类别结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入资源类别信息");
		if (resourceCategory != null) {
			if (resourceCategory.getId() != null) {				
				resourceCategory = resourceCategoryService.get(resourceCategory.getId());
			} else {
				resourceCategory.setValid(true);
				resourceCategory.setDirectory(CoreDateUtils.formatDate(new Date(), "yyyyMMdd"));
				resourceCategory.setOrderView(0);
			}
		}
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看资源类别详细信息");
		if (resourceCategory != null && resourceCategory.getId() != null) {
			resourceCategory = resourceCategoryService.get(resourceCategory.getId());
			if (resourceCategory.getParentID() != null && resourceCategory.getParentID() != 0) {				
				parentResourceCategory = resourceCategoryService.get(resourceCategory.getParentID());
			}
		} else {
			logger.error("查看资源类别详细信息，编码为空");
			super.setErrorMessage("查看资源类别详细信息，编码不能为空");
			return "failure";
		}
		logger.info("查看资源类别详细信息结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除资源类别");
		if (resourceCategory != null && resourceCategory.getId() != null) {
			resourceCategory = resourceCategoryService.get(resourceCategory.getId());
			resourceCategoryService.del(resourceCategory);
		} else {
			logger.error("删除资源类别，编码为空");
			super.setErrorMessage("删除资源类别，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/cms/resourceCategory.do");
		logger.info("删除资源类别结束");
		return "forward";
	}
	
	public ResourceCategoryService getResourceCategoryService() {
		return resourceCategoryService;
	}

	public void setResourceCategoryService(ResourceCategoryService resourceCategoryService) {
		this.resourceCategoryService = resourceCategoryService;
	}

	public ResourceCategory getResourceCategory() {
		return resourceCategory;
	}

	public void setResourceCategory(ResourceCategory resourceCategory) {
		this.resourceCategory = resourceCategory;
	}

	public List<ResourceCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<ResourceCategory> resourceCategorys) {
		this.categories = resourceCategorys;
	}

	public ResourceCategory getParentResourceCategory() {
		return parentResourceCategory;
	}

	public void setParentResourceCategory(ResourceCategory parentResourceCategory) {
		this.parentResourceCategory = parentResourceCategory;
	}
}
