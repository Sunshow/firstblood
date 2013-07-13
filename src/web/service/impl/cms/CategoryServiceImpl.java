package web.service.impl.cms;

import java.util.List;

import com.lehecai.admin.web.dao.cms.CategoryDao;
import com.lehecai.admin.web.domain.cms.Category;
import com.lehecai.admin.web.service.cms.CategoryService;

public class CategoryServiceImpl implements CategoryService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.CategoryService#add()
	 */
	private CategoryDao categoryDao;
	
	public void manage(Category category){
		categoryDao.merge(category);
	}

	public CategoryDao getCategoryDao() {
		return categoryDao;
	}

	public void setCategoryDao(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	@Override
	public List<Category> list(Category category) {
		return categoryDao.list(category);
	}

	@Override
	public Category get(Long ID) {
		return categoryDao.get(ID);
	}

	@Override
	public void del(Category category) {
		categoryDao.del(category);
	}
}
