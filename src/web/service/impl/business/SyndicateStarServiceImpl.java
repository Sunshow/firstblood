package web.service.impl.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.SyndicateStarService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.SyndicateStar;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 合买红人业务逻辑层实现类
 * @author yanweijie
 *
 */
public class SyndicateStarServiceImpl implements SyndicateStarService {
	private Logger logger = LoggerFactory.getLogger(SyndicateStarServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private MemberService memberService;
	
	/**
	 * 分页并条件查询合买红人
	 */
	public Map<String, Object> findSyndicateStartList (Long uid, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询合买红人");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SYNDICATE_STAR_SEARCH);
		
		
		if (uid != null && uid != 0L) {
			List<String> uids = new ArrayList<String>();
			uids.add(String.valueOf(uid));
			request.setParameterIn(SyndicateStar.QUERY_UID, uids);
		}
		if (pageBean != null && pageBean.isPageFlag()) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取合买红人数据失败");
			throw new ApiRemoteCallFailedException("API获取合买红人数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取合买红人数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取合买红人数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取合买红人数据为空, message={}", response.getMessage());
			return null;
		}
		List<SyndicateStar> list = SyndicateStar.convertFromJSONArray(response.getData());
		if (pageBean != null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;// 页数
			if (pageBean.getPageSize() != 0) {
				pageCount = totalCount / pageBean.getPageSize();
				if (totalCount % pageBean.getPageSize() != 0) {
					pageCount++;
				}
			}
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}
	
	/**
	 * 添加合买红人
	 */
	public boolean addSyndicateStar (Long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加合买红人");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SYNDICATE_STAR_ADD);
		
		request.setParameterForUpdate(SyndicateStar.SET_UID, uid + "");
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API添加合买红人异常");
			throw new ApiRemoteCallFailedException("调用API添加合买红人异常");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API添加合买红人失败, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * 修改合买红人
	 */
	public boolean updateSyndicateStar (List<String> uids, int priority) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改合买红人优先级");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SYNDICATE_STAR_UPDATE);
		
		request.setParameterIn(SyndicateStar.QUERY_UID, uids);
		request.setParameterForUpdate(SyndicateStar.SET_PRIORITY, priority + "");
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API修改合买红人优先级异常");
			throw new ApiRemoteCallFailedException("API修改合买红人优先级异常");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API修改合买红人优先级失败, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * 删除合买红人
	 */
	public boolean deleteSyndicateStar (Long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除合买红人");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SYNDICATE_STAR_DELETE);
		
		request.setParameter(SyndicateStar.QUERY_UID, uid + "");
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API删除合买红人异常");
			throw new ApiRemoteCallFailedException("API删除合买红人异常");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API删除合买红人失败, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		
		return true;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	
}
