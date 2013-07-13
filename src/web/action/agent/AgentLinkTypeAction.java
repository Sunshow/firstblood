package web.action.agent;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.agent.AgentLinkType;
import com.lehecai.admin.web.service.agent.AgentService;

public class AgentLinkTypeAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(AgentLinkTypeAction.class);
	
	private AgentService agentService;
	
	private AgentLinkType agentLinkType;
	
	private List<AgentLinkType> agentLinkTypeList;
	
	public String handle() {
		logger.info("进入查询推广链接类型");
		agentLinkTypeList = agentService.listLinkType(agentLinkType);
		return "list";
	}
	
	public String manage() {
		logger.info("进入更新推广链接类型");
		if (agentLinkType != null) {
			if (agentLinkType.getName() == null || "".equals(agentLinkType.getName())) {
				logger.error("名称为空");
				super.setErrorMessage("名称不能为空！");
				return "failure";
			}
			if (agentLinkType.getTemplate() == null || "".equals(agentLinkType.getTemplate())) {
				logger.error("模板为空");
				super.setErrorMessage("模板不能为空！");
				return "failure";
			}
			agentLinkType.setValid(true);
			agentService.manageLinkType(agentLinkType);
		} else {
			logger.error("添加推广链接类型错误，提交表单为空");
			super.setErrorMessage("添加推广链接类型错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/agent/agentLinkType.do");
		logger.info("更新推广链接类型结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入链接类型信息");
		if (agentLinkType != null) {
			if (agentLinkType.getId() != null) {
				agentLinkType = agentService.getLinkType(agentLinkType.getId());
			}
		}	
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看链接类型详细信息");
		if (agentLinkType != null && agentLinkType.getId() != null) {
			agentLinkType = agentService.getLinkType(agentLinkType.getId());
		} else {
			logger.error("查看链接类型详细信息，编码为空");
			super.setErrorMessage("查看链接类型详细信息，编码为空");
			return "failure";
		}
		logger.info("查看链接类型详细信息结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除链接类型");
		if (agentLinkType != null && agentLinkType.getId() != null) {
			agentLinkType = agentService.getLinkType(agentLinkType.getId());
			agentService.delLinkType(agentLinkType);
		} else {
			logger.error("删除链接类型，编码为空");
			super.setErrorMessage("删除链接类型，编码为空");
			return "failure";
		}
		super.setForwardUrl("/agent/agentLinkType.do");
		logger.info("删除链接类型结束");
		return "forward";
	}

	public AgentService getAgentService() {
		return agentService;
	}

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public AgentLinkType getAgentLinkType() {
		return agentLinkType;
	}

	public void setAgentLinkType(AgentLinkType agentLinkType) {
		this.agentLinkType = agentLinkType;
	}

	public List<AgentLinkType> getAgentLinkTypeList() {
		return agentLinkTypeList;
	}

	public void setAgentLinkTypeList(List<AgentLinkType> agentLinkTypeList) {
		this.agentLinkTypeList = agentLinkTypeList;
	}
	
}
