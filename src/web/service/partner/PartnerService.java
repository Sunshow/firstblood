package web.service.partner;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.partner.Partner;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface PartnerService {
	
	public Map<String, Object> getResult(Integer partnerId, String partnerName, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	public boolean update(Partner partner) throws ApiRemoteCallFailedException;

	public boolean create(Partner partner) throws ApiRemoteCallFailedException;
	
	//获取一段时间某一渠道商的渠道费用
	//add by chirowong
	public Double getTotalAmount(List<String> sourceIds, Date beginDate, Date endDate) throws ApiRemoteCallFailedException;
}
