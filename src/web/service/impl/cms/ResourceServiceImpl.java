package web.service.impl.cms;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.ResourceDao;
import com.lehecai.admin.web.domain.cms.Resource;
import com.lehecai.admin.web.service.cms.ResourceService;

public class ResourceServiceImpl implements ResourceService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.ResourceService#add()
	 */
	private ResourceDao resourceDao;
	
	public void manage(Resource resource){
		resourceDao.merge(resource);
	}

	public ResourceDao getResourceDao() {
		return resourceDao;
	}

	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	@Override
	public List<Resource> list(Resource resource, PageBean pageBean) {
		// TODO Auto-generated method stub
		return resourceDao.list(resource, pageBean);
	}

	@Override
	public Resource get(Long ID) {
		// TODO Auto-generated method stub
		return resourceDao.get(ID);
	}

	@Override
	public void del(Resource resource) {
		// TODO Auto-generated method stub
		resourceDao.del(resource);
	}

	@Override
	public PageBean getPageBean(Resource resource, PageBean pageBean) {
		// TODO Auto-generated method stub
		return resourceDao.getPageBean(resource, pageBean);
	}
}
