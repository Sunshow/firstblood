package web.service.statics;

import java.util.List;

import com.lehecai.admin.web.domain.statics.DataConsolidation;
import com.lehecai.admin.web.domain.statics.DataConsolidationItem;

public interface DataConsolidationService {
	void manage(DataConsolidation dataConsolidation, List<DataConsolidationItem> dataConsolidationItemList);
	List<DataConsolidation> list(); 
	DataConsolidation get(Long id);
	void add(DataConsolidationItem dataConsolidationItem);
	List<DataConsolidationItem> list(Long dataId);
	void del(Long dataId);
}