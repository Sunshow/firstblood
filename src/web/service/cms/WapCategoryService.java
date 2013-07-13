package web.service.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.WapCategory;

public interface WapCategoryService {
	void manage(WapCategory wapCategory);
	List<WapCategory> list(WapCategory wapCategory); 
	WapCategory get(Long ID);
	void del(WapCategory wapCategory);
}