package web.service.impl.statics;

import java.util.List;

import com.lehecai.admin.web.dao.statics.StaticFragmentDao;
import com.lehecai.admin.web.domain.statics.StaticFragment;
import com.lehecai.admin.web.service.statics.StaticFragmentService;

public class StaticFragmentServiceImpl implements StaticFragmentService{

	private StaticFragmentDao staticFragmentDao;
	
	/**
	 * 查询所有的碎片
	 */
	public List<StaticFragment> findList() {
		return staticFragmentDao.findList();
	}
	
	/**
	 * 根据碎片编码查询碎片
	 */
	public StaticFragment get(Long id) {
		return staticFragmentDao.get(id);
	}
	
	/**
	 * 根据碎片名称查询碎片
	 */
	public StaticFragment getByName(String fragmentName) {
		return staticFragmentDao.getByName(fragmentName);
	}
	
	/**
	 * 更新静态碎片
	 */
	public void merge(StaticFragment staticFragment) { 
		staticFragmentDao.merge(staticFragment);
	}

	public StaticFragmentDao getStaticFragmentDao() {
		return staticFragmentDao;
	}

	public void setStaticFragmentDao(StaticFragmentDao staticFragmentDao) {
		this.staticFragmentDao = staticFragmentDao;
	}
}
