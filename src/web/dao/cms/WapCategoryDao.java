package web.dao.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.WapCategory;

public interface WapCategoryDao {
	void merge(WapCategory wapCategory);
	List<WapCategory> list(WapCategory wapCategory);
	WapCategory get(Long ID);
	void del(WapCategory wapCategory);
}