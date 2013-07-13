package web.action.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.Category;
import com.lehecai.admin.web.domain.user.Role;
import com.lehecai.admin.web.enums.StaticPageType;
import com.lehecai.admin.web.service.cms.CategoryService;
import com.lehecai.admin.web.service.user.PermissionService;

public class CategoryAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(CategoryAction.class);
	
	private CategoryService categoryService;
	private PermissionService permissionService;
	
	private Category category;
	private Category parentCategory;
	
	private List<Category> categories;
	private List<Role> roles;
	
	private Integer staticPageTypeId;
	
	public String handle() {
		logger.info("进入查询栏目");
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
		logger.info("查询栏目结束");
		return "list";
	}
	
	public String manage() {
		logger.info("进入更新栏目信息");
		if (category != null) {
			if (category.getName() == null || "".equals(category.getName())) {
				logger.error("栏目名称为空");
				super.setErrorMessage("栏目名称不能为空");
				return "failure";
			}
			if (category.getParentID() == null) {
				category.setParentID(0L);
			}
			if (staticPageTypeId != null) {
				category.setStaticPageType(StaticPageType.getItem(staticPageTypeId));
			}
			categoryService.manage(category);
		} else {
			logger.error("更新栏目错误，提交表单为空");
			super.setErrorMessage("更新栏目错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/cms/category.do");
		logger.info("更新栏目信息结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入栏目信息");
		if (category != null) {
			if (category.getId() != null) {				
				category = categoryService.get(category.getId());
				staticPageTypeId = category.getStaticPageType().getValue();
			} else {
				category.setValid(true);
				category.setOrderView(0);
			}
		}
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看栏目详细信息");
		if (category != null && category.getId() != null) {
			category = categoryService.get(category.getId());
			if (category.getParentID() != null && category.getParentID() != 0) {				
				parentCategory = categoryService.get(category.getParentID());
			}
		} else {
			logger.error("查看栏目详细信息，编码为空");
			super.setErrorMessage("查看栏目详细信息，编码不能为空");
			return "failure";
		}
		logger.info("查看栏目详细信息结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除栏目");
		if (category != null && category.getId() != null) {
			category = categoryService.get(category.getId());
			categoryService.del(category);
		} else {
			logger.error("删除栏目，编码为空");
			super.setErrorMessage("删除栏目，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/cms/category.do");
		logger.info("删除栏目结束");
		return "forward";
	}
	public String authorizeInput() {
		logger.info("进入栏目授权页面");
		if (category != null && category.getId() != null) {
			category = categoryService.get(category.getId());
			roles = permissionService.listRoles(null);
		} else {
			logger.error("栏目授权页面，编码为空");
			super.setErrorMessage("栏目授权页面，编码不能为空");
			return "failure";
		}
		logger.info("栏目授权页面结束");
		return "authorizeInput";
	}
	public String authorize() {
		logger.info("进入栏目授权");
		if (category != null) {
			if (category.getId() == null || "".equals(category.getId())) {
				logger.error("栏目编码为空");
				super.setErrorMessage("栏目编码不能为空");
				return "failure";
			}
			if (category.getRoleId() == null) {
				logger.error("角色编码为空");
				super.setErrorMessage("角色编码不能为空");
				return "failure";
			}
			Category ca = categoryService.get(category.getId());
			ca.setRoleId(category.getRoleId());
			categoryService.manage(ca);
		} else {
			logger.error("更新栏目错误，提交表单为空");
			super.setErrorMessage("更新栏目错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/cms/category.do");
		logger.info("更新栏目信息结束");
		return "success";
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categorys) {
		this.categories = categorys;
	}

	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}
	public List<StaticPageType> getStaticPageTypes(){
		return StaticPageType.list;
	}

	public Integer getStaticPageTypeId() {
		return staticPageTypeId;
	}

	public void setStaticPageTypeId(Integer staticPageTypeId) {
		this.staticPageTypeId = staticPageTypeId;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
