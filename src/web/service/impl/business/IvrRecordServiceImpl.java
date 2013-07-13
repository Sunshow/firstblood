package web.service.impl.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.IvrRecordService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.IvrRecord;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class IvrRecordServiceImpl implements IvrRecordService{
	private Logger logger = LoggerFactory.getLogger(IvrRecordServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 根据用户编码查询绑定银行卡
	 */
	public List<IvrRecord> findIvrRecordList (Long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询用户银行卡");
		ApiRequest request = new ApiRequest();
		
		request.setUrl(ApiConstant.API_URL_USER_IVR_GET);
		request.setParameter(IvrRecord.QUERY_UID, uid + "");
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API查询用户银行卡异常");
			throw new ApiRemoteCallFailedException("API查询用户银行卡异常");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询用户银行卡失败");
			throw new ApiRemoteCallFailedException("API查询用户银行卡失败");
		}
		if (response.getData() == null) {
			logger.info("API查询用户银行卡为空");
			return null;
		}
		
		return IvrRecord.convertFromJSONArray(response.getData());
	}
	
	/**
	 * 解绑用户银行卡
	 */
	public boolean unlock (IvrRecord record) throws ApiRemoteCallFailedException {
		logger.info("进入调用API解绑用户银行卡");
		
		ApiRequest request = new ApiRequest();
		
		request.setUrl(ApiConstant.API_URL_USER_IVR_UNLOCK);
		request.setParameter(IvrRecord.QUERY_UID, record.getUid() + "");
		request.setParameter(IvrRecord.QUERY_CARDNO, record.getCardno() + "");
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API解绑用户银行卡异常");
			throw new ApiRemoteCallFailedException("API解绑用户银行卡异常");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API解绑用户银行卡失败");
			return false;
		}
		return true;
		
	}
	
	/**
	 * 修改用户银行卡
	 */
	public boolean updateBank (IvrRecord record) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改用户银行卡");
		
		ApiRequest request = new ApiRequest();
		
		request.setUrl(ApiConstant.API_URL_USER_IVR_UPDATE);
		request.setParameter(IvrRecord.QUERY_UID, record.getUid() + "");
		request.setParameter(IvrRecord.QUERY_CARDNO, record.getCardno() + "");
		
		request.setParameterForUpdate(IvrRecord.SET_BANK_TYPE, record.getBankType().getValue() + "");
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API修改用户银行卡异常");
			throw new ApiRemoteCallFailedException("API修改用户银行卡异常");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API修改用户银行卡失败");
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
}
