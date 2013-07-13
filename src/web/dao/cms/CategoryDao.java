package web.dao.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.Category;

public interface CategoryDao {
	void merge(Category category);
	List<Category> list(Category category);
	Category get(Long ID);
	void del(Category category);
}