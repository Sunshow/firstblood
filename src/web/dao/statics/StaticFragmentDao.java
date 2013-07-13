package web.dao.statics;

import java.util.List;

import com.lehecai.admin.web.domain.statics.StaticFragment;

public interface StaticFragmentDao {

	/**
	 * 查询所有的碎片
	 */
	List<StaticFragment> findList();
	
	/**
	 * 根据碎片编码查询碎片
	 */
	StaticFragment get(Long id);
	
	/**
	 * 根据碎片名称查询碎片
	 */
	StaticFragment getByName(String fragmentName);
	
	/**
	 * 更新静态碎片
	 */
	void merge(StaticFragment staticFragment);
}
