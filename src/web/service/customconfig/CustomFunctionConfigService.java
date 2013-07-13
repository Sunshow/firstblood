package web.service.customconfig;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.customconfig.CustomFunctionConfig;

public interface CustomFunctionConfigService {
	public CustomFunctionConfig get(Long ID);
	public void manage(CustomFunctionConfig customFunctionConfig);
	public void del(CustomFunctionConfig customFunctionConfig);
	public CustomFunctionConfig update(CustomFunctionConfig customFunctionConfig);
	public List<CustomFunctionConfig> list(CustomFunctionConfig customFunctionConfig, PageBean pageBean);
	public PageBean getPageBean(CustomFunctionConfig customFunctionConfig, PageBean pageBean);
}
