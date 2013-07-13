/**
 * 
 */
package web.dao.customconfig;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.customconfig.CustomFunctionConfig;
/**
 * @author chirowong
 *
 */
public interface CustomFunctionConfigDao {
	public CustomFunctionConfig get(Long ID);
	public void save(CustomFunctionConfig customFunctionConfig);
	public void del(CustomFunctionConfig customFunctionConfig);
	public CustomFunctionConfig merge(CustomFunctionConfig customFunctionConfig);
	public List<CustomFunctionConfig> list(CustomFunctionConfig customFunctionConfig, PageBean pageBean);
	public PageBean getPageBean(CustomFunctionConfig customFunctionConfig, PageBean pageBean);
}
