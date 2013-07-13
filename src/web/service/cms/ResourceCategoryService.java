package web.service.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.ResourceCategory;

public interface ResourceCategoryService {
	void manage(ResourceCategory resourceCategory);
	List<ResourceCategory> list(ResourceCategory resourceCategory); 
	ResourceCategory get(Long ID);
	void del(ResourceCategory resourceCategory);
}