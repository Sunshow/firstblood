package web.service.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.FocusCategory;

public interface FocusCategoryService {
	List<FocusCategory> categoryList(String filename);
	void manage(List<FocusCategory> focusCategory, String filename);
	void delFile(String fileName);
}
