package web.service.impl.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.business.PayPassword;
import com.lehecai.admin.web.domain.business.PayPasswordLog;
import com.lehecai.admin.web.service.business.PayPasswordService;
import com.lehecai.admin.web.service.impl.member.MemberServiceImpl;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class PayPasswordServiceImpl implements PayPasswordService {
	
	private final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	
	@Override
	public PayPassword get(Long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据会员编码查询会员信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PAYPASSWORD_QUERY);
		if (uid != null){
			request.setParameter(Member.QUERY_UID, String.valueOf(uid));
		}
		PayPassword payPassword = new PayPassword();
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员数据失败");
			throw new ApiRemoteCallFailedException("API获取会员数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("message=" + response.getMessage());
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		String result = response.getData().getString(0);
		JSONObject obj = JSONObject.fromObject(result);
		payPassword = PayPassword.convertFromJSONObject(obj);
		payPassword.setUid(uid);
		return payPassword;
	}
	
	public Map<String, Object> searchLog(Long uid,Date startDate,Date endDate,Integer passType,PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据会员编码查询会员信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SEARCHLOG_QUERY);
		if (uid != null){
			request.setParameter(Member.QUERY_UID, String.valueOf(uid));
		}
		if (passType != null && passType != -1){
			request.setParameter(PayPassword.QUERY_TYPE, String.valueOf(passType));
		}
		String startTime = null;
		String endTime = null;
		if (startDate != null) {
			startTime = DateUtil.formatDate(startDate,DateUtil.DATETIME);
		}
		if (endDate != null){
			endTime = DateUtil.formatDate(endDate, DateUtil.DATETIME);
		}
		if (startTime != null || endTime != null) {
			request.setParameterBetween(PayPassword.QUERY_TIMELINE,startTime,endTime);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员数据失败");
			throw new ApiRemoteCallFailedException("API获取会员数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("message=" + response.getMessage());
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		List<PayPasswordLog> payPasswordLogList = PayPasswordLog.convertFromJSONArray(response.getData());
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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, payPasswordLogList);
		return map;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

}
