package web.service.impl.agent;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.agent.AgentDao;
import com.lehecai.admin.web.domain.agent.AgentLink;
import com.lehecai.admin.web.domain.agent.AgentLinkType;
import com.lehecai.admin.web.service.agent.AgentService;
import com.lehecai.admin.web.utils.HttpUtil;
import com.lehecai.admin.web.utils.VelocityUtil;

public class AgentServiceImpl implements AgentService {
	private AgentDao agentDao;
	
	private VelocityUtil velocityUtil;
	
	@Override
	public void delLinkType(AgentLinkType agentLinkType) {
		agentDao.delLinkType(agentLinkType);
	}

	@Override
	public AgentLinkType getLinkType(Long ID) {
		return agentDao.getLinkType(ID);
	}

	@Override
	public List<AgentLinkType> listLinkType(AgentLinkType agentLinkType) {
		return agentDao.listLinkType(agentLinkType);
	}

	@Override
	public void manageLinkType(AgentLinkType agentLinkType) {
		agentDao.mergeLinkType(agentLinkType);
	}
	@Override
	public void delLink(AgentLink agentLink) {
		agentDao.delLink(agentLink);
	}

	@Override
	public AgentLink getLink(Long ID) {
		return agentDao.getLink(ID);
	}

	@Override
	public List<AgentLink> listLink(Long linkTypeId, Date fromCreateDate, Date toCreateDate, Date fromUpdateDate, Date toUpdateDate, String url, PageBean pageBean) {
		return agentDao.listLink(linkTypeId, fromCreateDate, toCreateDate, fromUpdateDate, fromUpdateDate, url, pageBean);
	}

	@Override
	public PageBean getLinkPageBean(Long linkTypeId, Date fromCreateDate,
			Date toCreateDate, Date fromUpdateDate, Date toUpdateDate, String url, PageBean pageBean) {
		return agentDao.getLinkPageBean(linkTypeId, fromCreateDate, toCreateDate, fromUpdateDate, fromUpdateDate, url, pageBean);
	}

	@Override
	public void manageLink(AgentLink agentLink, String path) throws Exception{
		agentLink = agentDao.mergeLink(agentLink);
		AgentLinkType agentLinkType = agentDao.getLinkType(agentLink.getLinkTypeId());
		Map<String, Object> contextValue = new HashMap<String, Object>();
		contextValue.put("agentLink", agentLink);
		velocityUtil.build(path + File.separator + agentLink.getId() +".jsp",HttpUtil.getHttpUrlContent(agentLinkType.getTemplate()), contextValue);
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public VelocityUtil getVelocityUtil() {
		return velocityUtil;
	}

	public void setVelocityUtil(VelocityUtil velocityUtil) {
		this.velocityUtil = velocityUtil;
	}

}
