package web.action.user;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.user.SourceService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.Source;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.SourceStatus;

public class SourceAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(SourceAction.class);
	
	private SourceService sourceService;
	
	private Integer isUpdate;	//0:create  1:update
	private Long id;
	private Long partnerId;
	private String name;
	private Integer status;
	
	private Source source = new Source();
	private List<Source> sources;
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("进入查询会员来源列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map;
		try {
			map = sourceService.getResult(null, partnerId, name, status, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){
			sources = (List<Source>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		logger.info("查询会员来源列表结束");
		return "list";
	}
	
	public String manage() {
		logger.info("进入更新会员来源");
		boolean b = false;
		if (id == null) {
			logger.error("来源编号为空");
			super.setErrorMessage("来源编号为空");
			return "failure";
		}
		if (id == 0) {
			logger.error("来源编号不能为0");
			super.setErrorMessage("来源编号不能为0");
			return "failure";
		}
		if (partnerId == null) {
			logger.error("合作商编号为空");
			super.setErrorMessage("合作商编号为空");
			return "failure";
		}
		if (name == null) {
			logger.error("合作商编号为空");
			super.setErrorMessage("合作商编号为空");
			return "failure";
		}
		if (status == null || status == SourceStatus.ALL.getValue()) {
			logger.error("合作商编号为空");
			super.setErrorMessage("合作商编号为空");
			return "failure";
		}
		try {
			if (isUpdate == 1) {
				b = sourceService.update(id, partnerId, name, status);
			} else {
				b = sourceService.create(id, partnerId, name, status);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (b) {
			logger.info("会员来源信息更新成功");
			super.setSuccessMessage("会员来源信息更新成功");
		} else {
			logger.error("会员来源信息更新失败，请检查来源编号是否正确");
			super.setErrorMessage("会员来源信息更新失败，请检查来源编号是否正确");
		}
		
		logger.info("渠道合作商管理，退出更新会员来源");
//		source.clear();
		id = null;
		name = null;
		status = null;
		return handle();
	}
	
	@SuppressWarnings("unchecked")
	public String input(){
		logger.info("进入输入会员来源信息");
		if (id != null) {
			Map<String, Object> map;
			try {
				map = sourceService.getResult(id, null, null, null, super.getPageBean());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			if(map != null){
				sources = (List<Source>)map.get(Global.API_MAP_KEY_LIST);
				source = sources != null ? sources.get(0) : null;
			}
		}
		return "input";
	}
	
	@SuppressWarnings("unchecked")
	public String manageExistSource() {
		logger.info("进入更新会员来源");
		boolean b = false;
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map;
		if (id == null) {
			logger.error("来源编号为空");
			super.setErrorMessage("来源编号为空");
			return "failure";
		}
		if (id == 0) {
			logger.error("来源编号不能为0");
			super.setErrorMessage("来源编号不能为0");
			return "failure";
		}
		if (partnerId == null) {
			logger.error("合作商编号为空");
			super.setErrorMessage("合作商编号为空");
			return "failure";
		}
		try {
			map = sourceService.getResult(id, null, null, null, super.getPageBean());
			if(map != null){
				List<Source> exsitSources = (List<Source>)map.get(Global.API_MAP_KEY_LIST);
				if (exsitSources != null && exsitSources.size() > 0) {
					b = sourceService.createExistSource(id, partnerId);
				} else {
					logger.error("该来源编号不存在");
					super.setErrorMessage("该来源编号不存在");
					return "failure";
				}
				PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
				super.setPageString(PageUtil.getPageString(request, pageBean));
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (b) {
			logger.info("会员来源信息更新成功");
			super.setSuccessMessage("会员来源信息更新成功");
		} else {
			logger.error("会员来源信息更新失败，请检查来源编号是否正确");
			super.setErrorMessage("会员来源信息更新失败，请检查来源编号是否正确");
		}
		
		logger.info("渠道合作商管理，退出更新会员来源");
//		source.clear();
		id = null;
		name = null;
		status = null;
		return handle();
	}
	
	public String inputExistSource(){
		logger.info("进入输入会员来源信息");
		return "inputExistSource";
	}

	public List<SourceStatus> getStatusList() {
		return SourceStatus.getItems();
	}
	
	public Long getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Long partnerId) {
		source.setPartnerId(partnerId);
		this.partnerId = partnerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<Source> getSources() {
		return sources;
	}

	public void setSources(List<Source> sources) {
		this.sources = sources;
	}

	public SourceService getSourceService() {
		return sourceService;
	}

	public void setSourceService(SourceService sourceService) {
		this.sourceService = sourceService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Integer getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Integer isUpdate) {
		this.isUpdate = isUpdate;
	}
}
