package web.dao.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.ResourceCategory;

public interface ResourceCategoryDao {
	void merge(ResourceCategory resourceCategory);
	List<ResourceCategory> list(ResourceCategory resourceCategory);
	ResourceCategory get(Long ID);
	void del(ResourceCategory resourceCategory);
}