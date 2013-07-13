package web.service.impl.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.business.DeductService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.FreezeLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.FrozenStatus;
import com.lehecai.core.lottery.WalletType;


public class DeductServiceImpl implements DeductService {
	private final Logger logger = LoggerFactory.getLogger(DeductServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	private MemberService memberService;
	private PermissionService permissionService;
	
	@Override
	public Map<String, Object> getResult(String username, Long frozen_id,
			FrozenStatus status, WalletType walletType, Date beginDate,
			Date endDate, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询冻结数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_WALLET_FROZEN_LIST);
		//通过用户名获取用户ID异常!
		if(username != null && !"".equals(username)){
			Long uid = null;
			try {
				uid = memberService.getIdByUserName(username);
			} catch (Exception e) {
				logger.error("通过用户名获取用户ID异常!");
			}
			if(uid != null && uid.longValue() != 0){
				request.setParameter(FreezeLog.QUERY_UID, String.valueOf(uid.longValue()));
			} else {
				logger.info("用户名不存在！返回空记录！");
				return null;
			}
		}
		if (frozen_id != null && frozen_id != 0L) {
			request.setParameter(FreezeLog.QUERY_FROZEN_ID, frozen_id + "");
		}
		if (status != null && status != FrozenStatus.ALL) {
			request.setParameter(FreezeLog.QUERY_FROZEN_STATUS, status.getValue() + "");
		}
		if (walletType != null && walletType != WalletType.ALL) {
			request.setParameter(FreezeLog.QUERY_WALLET_TYPE, walletType.getValue() + "");
		}
		if(beginDate != null){
			request.setParameterBetween(FreezeLog.QUERY_TIMELINE, DateUtil.formatDate(beginDate,DateUtil.DATETIME),null);
		}
		if(endDate != null){
			request.setParameterBetween(FreezeLog.QUERY_TIMELINE, null,DateUtil.formatDate(endDate,DateUtil.DATETIME));
		}
		
		if (orderStr != null && !"".equals(orderStr) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取冻结数据失败");
			throw new ApiRemoteCallFailedException("API获取冻结数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取冻结数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取冻结数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取冻结数据为空, message={}", response.getMessage());
			return null;
		}
		List<FreezeLog> list = FreezeLog.convertFromJSONArray(response.getData());
		if(pageBean != null){
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if(pageBean.getPageSize() != 0) {
				pageCount = totalCount / pageBean.getPageSize();
				if(totalCount % pageBean.getPageSize() != 0) {
					pageCount ++;
				}
			}
			pageBean.setPageCount(pageCount);
		}
		if (list != null) {
			Map<Long, User> userMap = permissionService.userMapping();
			for (FreezeLog freezeLog : list) {
				if (freezeLog != null) {
					if (freezeLog.getAdmin_uid() > 0L) {
						User adminUser = userMap.get(freezeLog.getAdmin_uid());
						if (adminUser != null) {
							freezeLog.setAdminUserName(adminUser.getName());
						} else {
							freezeLog.setAdminTimeLine(null);
						}
					} else {
						freezeLog.setAdminTimeLine(null);
					}
					if (freezeLog.getFrozenUid() > 0L) {
						User frozenUser = userMap.get(freezeLog.getFrozenUid());
						if (frozenUser != null) {
							freezeLog.setFrozenUserName(frozenUser.getName());
						}
					}
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}

	@Override
	public void deduct(Long deduct_id, Long user_id)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API执行扣款");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_WALLET_DEDUCT);
		request.setParameter(FreezeLog.QUERY_DEDUCT_ID, String.valueOf(deduct_id));
		request.setParameterForUpdate(FreezeLog.SET_ADMIN_UID, String.valueOf(user_id));
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API执行扣款操作失败");
			throw new ApiRemoteCallFailedException("API执行扣款操作失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API执行扣款操作请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API执行扣款操作请求异常");
		}
	}
	@Override
	public void unfreeze(Long deduct_id, Long user_id)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API执行冻结转正常");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_WALLET_UNFREEZE);
		request.setParameter(FreezeLog.QUERY_DEDUCT_ID, String.valueOf(deduct_id));
		request.setParameterForUpdate(FreezeLog.SET_ADMIN_UID, String.valueOf(user_id));
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API执行冻结转正常操作失败");
			throw new ApiRemoteCallFailedException("API执行冻结转正常操作失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API执行冻结转正常操作请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API执行冻结转正常操作请求异常");
		}
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}
	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
}
