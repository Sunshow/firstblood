package web.action.partner;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.partner.PartnerService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.partner.Partner;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class PartnerAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(PartnerAction.class);
	
	private PartnerService partnerService;
	
	private Partner partner = new Partner();
	
	private List<Partner> partners;
	
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入查询渠道合作商列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map;
		try {
			map = partnerService.getResult(partner.getPartnerId(), partner.getPartnerName(), super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){			
			partners = (List<Partner>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		logger.info("查询渠道合作商列表结束");
		return "list";
	}

	public String manage() {
		logger.info("进入更新渠道合作商信息");
		boolean b = false;
		try {
			if (partner.getPartnerId() != null) {
				b = partnerService.update(partner);
			} else {
				b = partnerService.create(partner);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (b) {
			super.setSuccessMessage("渠道合作商信息更新成功");
			logger.info("渠道合作商信息更新成功");
		} else {
			super.setErrorMessage("渠道合作商信息更新失败");
			logger.error("渠道合作商信息更新失败");
		}
		
		partner.clear();
		logger.info("更新渠道合作商信息结束");
		return handle();
	}
	
	@SuppressWarnings("unchecked")
	public String input() {
		logger.info("进入输入渠道合作商信息");
		if (partner != null && partner.getPartnerId() != null) {
			Map<String, Object> map;
			try {
				map = partnerService.getResult(partner.getPartnerId(), partner.getPartnerName(), super.getPageBean());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			if(map != null){	
				partners = (List<Partner>)map.get(Global.API_MAP_KEY_LIST);
				partner = partners != null ? partners.get(0) : null;
			}
		}
		return "input";
	}
	
	public PartnerService getPartnerService() {
		return partnerService;
	}

	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

	public List<Partner> getPartners() {
		return partners;
	}

	public void setPartners(List<Partner> partners) {
		this.partners = partners;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}
}
