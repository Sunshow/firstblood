package web.service.impl.cms;

import java.util.List;

import com.lehecai.admin.web.dao.cms.WapCategoryDao;
import com.lehecai.admin.web.domain.cms.WapCategory;
import com.lehecai.admin.web.service.cms.WapCategoryService;

public class WapCategoryServiceImpl implements WapCategoryService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.CategoryService#add()
	 */
	private WapCategoryDao wapCategoryDao;
	
	public void manage(WapCategory wapCategory){
		wapCategoryDao.merge(wapCategory);
	}

	public WapCategoryDao getWapCategoryDao() {
		return wapCategoryDao;
	}

	public void setWapCategoryDao(WapCategoryDao wapCategoryDao) {
		this.wapCategoryDao = wapCategoryDao;
	}

	@Override
	public List<WapCategory> list(WapCategory wapCategory) {
		return wapCategoryDao.list(wapCategory);
	}

	@Override
	public WapCategory get(Long ID) {
		return wapCategoryDao.get(ID);
	}

	@Override
	public void del(WapCategory wapCategory) {
		wapCategoryDao.del(wapCategory);
	}
}
