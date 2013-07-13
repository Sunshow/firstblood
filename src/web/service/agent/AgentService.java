package web.service.agent;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.agent.AgentLink;
import com.lehecai.admin.web.domain.agent.AgentLinkType;

public interface AgentService {
	void manageLinkType(AgentLinkType agentLinkType);
	List<AgentLinkType> listLinkType(AgentLinkType agentLinkType);
	AgentLinkType getLinkType(Long ID);
	void delLinkType(AgentLinkType agentLinkType);
	void manageLink(AgentLink agentLink, String path) throws Exception;
	List<AgentLink> listLink(Long linkTypeId, Date fromCreateDate, Date toCreateDate, Date fromUpdateDate, Date toUpdateDate, String url, PageBean pageBean);
	PageBean getLinkPageBean(Long linkTypeId, Date fromCreateDate, Date toCreateDate, Date fromUpdateDate, Date toUpdateDate, String url, PageBean pageBean);
	AgentLink getLink(Long ID);
	void delLink(AgentLink agentLink);
}
