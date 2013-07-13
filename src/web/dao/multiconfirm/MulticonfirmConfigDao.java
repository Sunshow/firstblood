package web.dao.multiconfirm;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfigType;


public interface MulticonfirmConfigDao {

	MulticonfirmConfig get(String configKey);

	MulticonfirmConfig get(Long id);

	MulticonfirmConfig manageConfig(MulticonfirmConfig multiconfirmConfig);

	List<MulticonfirmConfig> getConfigList(Long id, String configKey,
                                           String configName, MulticonfirmConfigType mct, Date createTimeFrom, Date createTimeTo, PageBean pageBean);

	void del(MulticonfirmConfig multiconfirmConfig);

	PageBean getConfigPageBean(Long id, String configKey, String configName,
                               MulticonfirmConfigType mct, Date createTimeFrom, Date createTimeTo, PageBean pageBean);
		
}