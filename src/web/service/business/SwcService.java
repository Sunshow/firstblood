package web.service.business;

import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.Swc;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 后台敏感词业务逻辑层接口
 * @author yanweijie
 *
 */
public interface SwcService {

	/**
	 * 多条件并分页查询所有敏感词
	 * @param name 敏感词
	 * @param status 状态
	 * @param pageBean 分页
	 */
	List<Swc> findSwcList(String name, int status, PageBean pageBean);
	
	/**
	 * 封装多条件查询分页对象
	 * @param pageBean	分页对象
	 * @param name	敏感词
	 * @param status 状态
	 * @return
	 */
	PageBean getPageBean(PageBean pageBean, String name, int status);
	
	/**
	 * 根据敏感词编号查询敏感词对象
	 * @param id 敏感词编号
	 */
	Swc getById(Long id);
	
	/**
	 * 根据敏感词查询敏感词对象
	 * @param name 敏感词
	 */
	Swc getByName(String name);
	
	/**
	 * 添加/修改敏感词
	 * @param adminSwc 敏感词对象
	 */
	void merge(Swc swc);
	
	/**
	 * 修改敏感词状态
	 * @param swc 敏感词对象
	 */
	public void save(Swc swc) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除敏感词
	 * @param adminSwc
	 */
	void del(Swc swc) throws ApiRemoteCallFailedException;
	
	/**
	 * 检测某串是否有敏感词
	 * @param str 字符串
	 * @param flag 过滤选项
	 */
	public Map<String, String> check(String str, int flag) throws ApiRemoteCallFailedException;
	
	/**
	 * 批量插入敏感词
	 * @param swcList
	 */
	void mergeBatch(List<Swc> swcList);
}
