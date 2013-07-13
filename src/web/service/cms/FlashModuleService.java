package web.service.cms;

import java.util.List;

import com.lehecai.admin.web.domain.cms.FlashModule;

public interface FlashModuleService {

	List<FlashModule> moduleList(String filename);

	void manage(List<FlashModule> moduleList, String filename);

}
