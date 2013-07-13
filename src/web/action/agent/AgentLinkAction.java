package web.action.agent;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.agent.AgentLink;
import com.lehecai.admin.web.domain.agent.AgentLinkType;
import com.lehecai.admin.web.service.agent.AgentService;
import com.lehecai.admin.web.utils.PageUtil;

public class AgentLinkAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private AgentService agentService;
	
	private String saveDir;
	private String clientUrl;
	
	private AgentLink agentLink;
	
	private List<AgentLink> agentLinkList;
	private List<AgentLinkType> agentLinkTypeList;
	
	private Long linkTypeId;
	private Date fromCreateDate;
	private Date toCreateDate;
	private Date fromUpdateDate;
	private Date toUpdateDate;
	private String url;
	
	public String handle() {
		logger.info("进入查询推广链接列表");
		agentLinkTypeList = agentService.listLinkType(null);
		HttpServletRequest request = ServletActionContext.getRequest();
		agentLinkList = agentService.listLink(linkTypeId, fromCreateDate, toCreateDate, fromUpdateDate, fromUpdateDate, url, super.getPageBean());
		PageBean pageBean = agentService.getLinkPageBean(linkTypeId, fromCreateDate, toCreateDate, fromUpdateDate, fromUpdateDate, url, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		logger.info("查询推广链接列表结束");
		return "list";
	}
	
	public String manage() {
		logger.info("进入更新推广链接");
		String webRoot = "";
		if (agentLink != null) {
			if (agentLink.getLinkTypeId() == null) {
				logger.error("推广链接类型为空");
				super.setErrorMessage("推广链接类型不能为空");
				return "failure";
			}
			agentLink.setUpdateTime(new Date());
			agentLink.setValid(true);
			try {
				webRoot = WebUtils.getRealPath(ServletActionContext.getServletContext(), "");
				webRoot = webRoot + saveDir;
				agentService.manageLink(agentLink, webRoot);
			} catch (Exception e) {
				logger.error("添加推广链接错误，请检查远程模板是否正确，{}", e);
				super.setErrorMessage("添加推广链接错误，请检查远程模板是否正确");
				return "failure";
			}
		} else {
			logger.error("添加推广链接错误，提交的表单为空");
			super.setErrorMessage("添加推广链接错误，提交的表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/agent/agentLink.do");
		logger.info("更新推广链接结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入推广链接详细信息");
		if (agentLink != null) {
			if (agentLink.getId() != null) {
				agentLink = agentService.getLink(agentLink.getId());
			}
		}
		agentLinkTypeList = agentService.listLinkType(null);
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看推广链接详细信息");
		if (agentLink != null && agentLink.getId() != null) {
			agentLink = agentService.getLink(agentLink.getId());
			logger.info(saveDir + "/" + agentLink.getId() + ".jsp");
			super.setForwardUrl(saveDir + "/" + agentLink.getId() + ".jsp");
		} else {
			logger.error("查看链接详细信息，编码为空");
			super.setErrorMessage("查看链接详细信息，编码不能为空");
			return "failure";
		}
		logger.info("查看推广链接详细信息结束");
		return "forward";
	}
	
	public String del() {
		logger.info("进入删除推广链接");
		if (agentLink != null && agentLink.getId() != null) {
			agentLink = agentService.getLink(agentLink.getId());
			agentService.delLink(agentLink);
		} else {
			logger.error("删除链接，编码为空");
			super.setErrorMessage("删除链接，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/agent/agentLink.do");
		logger.info("删除推广链接结束");
		return "forward";
	}

	public AgentService getAgentService() {
		return agentService;
	}

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public AgentLink getAgentLink() {
		return agentLink;
	}

	public void setAgentLink(AgentLink agentLink) {
		this.agentLink = agentLink;
	}

	public List<AgentLink> getAgentLinkList() {
		return agentLinkList;
	}

	public void setAgentLinkList(List<AgentLink> agentLinkList) {
		this.agentLinkList = agentLinkList;
	}

	public List<AgentLinkType> getAgentLinkTypeList() {
		return agentLinkTypeList;
	}

	public void setAgentLinkTypeList(List<AgentLinkType> agentLinkTypeList) {
		this.agentLinkTypeList = agentLinkTypeList;
	}

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public Long getLinkTypeId() {
		return linkTypeId;
	}

	public void setLinkTypeId(Long linkTypeId) {
		this.linkTypeId = linkTypeId;
	}

	public Date getFromCreateDate() {
		return fromCreateDate;
	}

	public void setFromCreateDate(Date fromCreateDate) {
		this.fromCreateDate = fromCreateDate;
	}

	public Date getToCreateDate() {
		return toCreateDate;
	}

	public void setToCreateDate(Date toCreateDate) {
		this.toCreateDate = toCreateDate;
	}

	public Date getFromUpdateDate() {
		return fromUpdateDate;
	}

	public void setFromUpdateDate(Date fromUpdateDate) {
		this.fromUpdateDate = fromUpdateDate;
	}

	public Date getToUpdateDate() {
		return toUpdateDate;
	}

	public void setToUpdateDate(Date toUpdateDate) {
		this.toUpdateDate = toUpdateDate;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClientUrl() {
		return clientUrl;
	}

	public void setClientUrl(String clientUrl) {
		this.clientUrl = clientUrl;
	}
}
