package web.dao.statics;

import java.util.List;

import com.lehecai.admin.web.domain.statics.DataConsolidation;
import com.lehecai.admin.web.domain.statics.DataConsolidationItem;

public interface DataConsolidationDao {
	DataConsolidation merge(DataConsolidation dataConsolidation);
	List<DataConsolidation> list();
	DataConsolidation get(Long id);
	void add(DataConsolidationItem dataConsolidationItem);
	List<DataConsolidationItem> list(Long dataId);
	void del(Long dataId);
}