package web.service.impl.cms;

import java.util.List;

import com.lehecai.admin.web.dao.cms.ResourceCategoryDao;
import com.lehecai.admin.web.domain.cms.ResourceCategory;
import com.lehecai.admin.web.service.cms.ResourceCategoryService;

public class ResourceCategoryServiceImpl implements ResourceCategoryService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.ResourceCategoryService#add()
	 */
	private ResourceCategoryDao resourceCategoryDao;
	
	public void manage(ResourceCategory resourceCategory){
		resourceCategoryDao.merge(resourceCategory);
	}

	public ResourceCategoryDao getResourceCategoryDao() {
		return resourceCategoryDao;
	}

	public void setResourceCategoryDao(ResourceCategoryDao resourceCategoryDao) {
		this.resourceCategoryDao = resourceCategoryDao;
	}

	@Override
	public List<ResourceCategory> list(ResourceCategory resourceCategory) {
		// TODO Auto-generated method stub
		return resourceCategoryDao.list(resourceCategory);
	}

	@Override
	public ResourceCategory get(Long ID) {
		// TODO Auto-generated method stub
		return resourceCategoryDao.get(ID);
	}

	@Override
	public void del(ResourceCategory resourceCategory) {
		// TODO Auto-generated method stub
		resourceCategoryDao.del(resourceCategory);
	}
}
