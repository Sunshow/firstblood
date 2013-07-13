package web.dao.cms;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.Resource;

public interface ResourceDao {
	void merge(Resource resource);
	List<Resource> list(Resource resource, PageBean pageBean);
	PageBean getPageBean(Resource resource, PageBean pageBean);
	Resource get(Long ID);
	void del(Resource resource);
}