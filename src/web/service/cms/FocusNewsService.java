package web.service.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.FocusLine;

public interface FocusNewsService {
	List<FocusLine> focusLineList(String filename);
	void lineManage(List<FocusLine> lineList, String filename);
}
