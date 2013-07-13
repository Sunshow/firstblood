package web.service.cms;

import com.lehecai.admin.web.domain.cms.FlashCategory;

import java.util.List;

public interface FlashCategoryService {
	List<FlashCategory> categoryList(String filename);
	void delFile(String filename);
	void manage(List<FlashCategory> categoryList, String filename);
}
