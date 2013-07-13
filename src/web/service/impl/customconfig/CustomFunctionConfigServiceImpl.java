package web.service.impl.customconfig;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.customconfig.CustomFunctionConfigDao;
import com.lehecai.admin.web.domain.customconfig.CustomFunctionConfig;
import com.lehecai.admin.web.service.customconfig.CustomFunctionConfigService;

public class CustomFunctionConfigServiceImpl implements CustomFunctionConfigService {
	
	private CustomFunctionConfigDao customFunctionConfigDao;
	
	@Override
	public void del(CustomFunctionConfig customFunctionConfig) {
		customFunctionConfigDao.del(customFunctionConfig);
	}

	@Override
	public CustomFunctionConfig get(Long ID) {
		return customFunctionConfigDao.get(ID);
	}

	@Override
	public void manage(CustomFunctionConfig customFunctionConfig) {
		customFunctionConfigDao.merge(customFunctionConfig);
	}
	
	@Override
	public CustomFunctionConfig update(CustomFunctionConfig customFunctionConfig) {
		return customFunctionConfigDao.merge(customFunctionConfig);
	}
	
	@Override
	public List<CustomFunctionConfig> list(CustomFunctionConfig customFunctionConfig,PageBean pageBean){
		return customFunctionConfigDao.list(customFunctionConfig, pageBean);
	}
	
	@Override
	public PageBean getPageBean(CustomFunctionConfig customFunctionConfig,
			PageBean pageBean) {
		return customFunctionConfigDao.getPageBean(customFunctionConfig, pageBean);
	}

	public CustomFunctionConfigDao getCustomFunctionConfigDao() {
		return customFunctionConfigDao;
	}

	public void setCustomFunctionConfigDao(
			CustomFunctionConfigDao customFunctionConfigDao) {
		this.customFunctionConfigDao = customFunctionConfigDao;
	}
}
