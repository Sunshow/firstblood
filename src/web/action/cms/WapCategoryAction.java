package web.action.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.WapCategory;
import com.lehecai.admin.web.enums.StaticPageType;
import com.lehecai.admin.web.service.cms.WapCategoryService;

public class WapCategoryAction extends BaseAction {

	private static final long serialVersionUID = 2436161530465382824L;
	private WapCategoryService wapCategoryService;
	
	private WapCategory wapCategory;
	private WapCategory parentWapCategory;
	
	private List<WapCategory> wapCategories;
	
	private Integer staticPageTypeId;
	
	public String handle(){	
		List<WapCategory> list = wapCategoryService.list(wapCategory);
		wapCategories = new ArrayList<WapCategory>();
		Map<Long, WapCategory> parentMap = new HashMap<Long, WapCategory>();
		if(list != null && list.size() != 0){
			for(WapCategory sc : list){
				if(sc.getCaLevel() == 1){
					parentMap.put(sc.getId(), sc);			
					wapCategories.add(sc);				
				} else {
					WapCategory parent = parentMap.get(sc.getParentID());
					if (parent != null) {
						List<WapCategory> children = parent.getChildren();
						children.add(sc);
					}
				}
			}
		}
		return "list";
	}
	
	public String manage(){
		if(wapCategory != null){
			if(wapCategory.getName() == null || "".equals(wapCategory.getName())){
				super.setErrorMessage("栏目名称不能为空！");
				return "failure";
			}
			if(wapCategory.getParentID() == null){
				wapCategory.setParentID(0L);
			}
			//if(staticPageTypeId != null){
				//wapCategory.setStaticPageType(StaticPageType.getItem(staticPageTypeId));
			wapCategory.setStaticPageType(StaticPageType.DEFAULTTYPE);
			//}
			wapCategoryService.manage(wapCategory);
		}else{
			super.setErrorMessage("添加栏目错误，提交表单为空！");
			return "failure";
		}
		super.setForwardUrl("/cms/wapCategory.do");
		return "success";
	}
	
	public String input(){
		if(wapCategory != null){
			if(wapCategory.getId() != null){				
				wapCategory = wapCategoryService.get(wapCategory.getId());
				staticPageTypeId = wapCategory.getStaticPageType().getValue();
			}else{
				wapCategory.setValid(true);
				wapCategory.setOrderView(0);
			}
		}
		return "inputForm";
	}
	public String view(){
		if(wapCategory != null && wapCategory.getId() != null){
			wapCategory = wapCategoryService.get(wapCategory.getId());
			if(wapCategory.getParentID() != null && wapCategory.getParentID() != 0){				
				parentWapCategory = wapCategoryService.get(wapCategory.getParentID());
			}
		}else{
			return "failure";
		}
		return "view";
	}
	public String del(){
		if(wapCategory != null && wapCategory.getId() != null){
			wapCategory = wapCategoryService.get(wapCategory.getId());
			wapCategoryService.del(wapCategory);
		}else{
			return "failure";
		}
		super.setForwardUrl("/cms/wapCategory.do");
		return "forward";
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

	public WapCategoryService getWapCategoryService() {
		return wapCategoryService;
	}

	public void setWapCategoryService(WapCategoryService wapCategoryService) {
		this.wapCategoryService = wapCategoryService;
	}

	public WapCategory getWapCategory() {
		return wapCategory;
	}

	public void setWapCategory(WapCategory wapCategory) {
		this.wapCategory = wapCategory;
	}

	public WapCategory getParentWapCategory() {
		return parentWapCategory;
	}

	public void setParentWapCategory(WapCategory parentWapCategory) {
		this.parentWapCategory = parentWapCategory;
	}

	public List<WapCategory> getWapCategories() {
		return wapCategories;
	}

	public void setWapCategories(List<WapCategory> wapCategories) {
		this.wapCategories = wapCategories;
	}
}
