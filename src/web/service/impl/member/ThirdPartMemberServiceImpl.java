package web.service.impl.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.ThirdPartMemberService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.ThirdPartMember;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ThirdPartMemberType;


public class ThirdPartMemberServiceImpl implements ThirdPartMemberService {
	private final Logger logger = LoggerFactory.getLogger(ThirdPartMemberServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	
	@Override
	public Map<String, Object> queryResult(Long uid, Long ruid, String rusername, ThirdPartMemberType memberType, PageBean pageBean) throws ApiRemoteCallFailedException {
		// TODO Auto-generated method stub
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_THIRD_PART_MEMBER_SEARCH);
		//通过用户名获取用户ID异常!
		if (uid != null && uid != 0) {
			request.setParameter(ThirdPartMember.QUERY_UID, String.valueOf(uid.longValue()));
		} 
		if(ruid != null && ruid != 0){
			request.setParameter(ThirdPartMember.QUERY_RUID, String.valueOf(ruid));
		}
		if(rusername != null && !"".equals(rusername)){
			request.setParameter(ThirdPartMember.QUERY_RUSERNAME, rusername);
		}
		if(memberType != null && memberType.getValue() != ThirdPartMemberType.ALL.getValue()){
			request.setParameter(ThirdPartMember.QUERY_TYPE, memberType.getValue() + "");
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员数据失败");
			throw new ApiRemoteCallFailedException("API获取会员数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API获取会员数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取会员数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<ThirdPartMember> list = ThirdPartMember.convertFromJSONArray(response.getData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}
}
