/**
 * 
 */
package web.service.impl.statics;

import java.util.List;

import com.lehecai.admin.web.dao.statics.DataConsolidationDao;
import com.lehecai.admin.web.domain.statics.DataConsolidation;
import com.lehecai.admin.web.domain.statics.DataConsolidationItem;
import com.lehecai.admin.web.service.statics.DataConsolidationService;

/**
 * @author qatang
 *
 */
public class DataConsolidationServiceImpl implements DataConsolidationService {
	private DataConsolidationDao dataConsolidationDao;
	
	@Override
	public void del(Long dataId) {
		dataConsolidationDao.del(dataId);
	}

	@Override
	public DataConsolidation get(Long id) {
		return dataConsolidationDao.get(id);
	}

	@Override
	public List<DataConsolidation> list() {
		return dataConsolidationDao.list();
	}

	@Override
	public List<DataConsolidationItem> list(Long dataId) {
		return dataConsolidationDao.list(dataId);
	}

	@Override
	public void manage(DataConsolidation dataConsolidation, List<DataConsolidationItem> dataConsolidationItemList) {
		DataConsolidation dataConsolidationTmp = dataConsolidationDao.merge(dataConsolidation);
		
		if (dataConsolidation.getId() != null) {
			dataConsolidationDao.del(dataConsolidation.getId());
		}
		
		if (dataConsolidationItemList != null && dataConsolidationItemList.size() > 0) {
			for (DataConsolidationItem item : dataConsolidationItemList) {
				if (item == null) {
					continue;
				}
				item.setDataId(dataConsolidationTmp.getId());
				dataConsolidationDao.add(item);
			}
		}
	}

	@Override
	public void add(DataConsolidationItem dataConsolidationItem) {
		dataConsolidationDao.add(dataConsolidationItem);
	}

	public void setDataConsolidationDao(DataConsolidationDao dataConsolidationDao) {
		this.dataConsolidationDao = dataConsolidationDao;
	}

}
