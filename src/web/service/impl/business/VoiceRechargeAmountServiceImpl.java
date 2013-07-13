package web.service.impl.business;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.VoiceRechargeAmountService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.business.VoiceRechargeAmount;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 语音充值限额管理
 * @author He Wang
 *
 */
public class VoiceRechargeAmountServiceImpl implements VoiceRechargeAmountService {
	private Logger logger = LoggerFactory.getLogger(VoiceRechargeAmountServiceImpl.class);

	private ApiRequestService apiWriteRequestService;
	private ApiRequestService apiRequestService;
	
	public Map<String, Object> queryVoiceRechargeAmountList(VoiceRechargeAmount voiceRechargeAmount, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询语音充值限额");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_VOICE_RECHARGE_AMOUNT_QUERY);
        if (voiceRechargeAmount.getId() != null) {
        	request.setParameter(VoiceRechargeAmount.QUERY_ID, voiceRechargeAmount.getId() + "");
        }
        if (voiceRechargeAmount.getWalletType() != null && voiceRechargeAmount.getWalletType().getValue() > 0) {
        	request.setParameter(VoiceRechargeAmount.QUERY_WALLET_TYPE, voiceRechargeAmount.getWalletType().getValue() + "");
        }
        if (!StringUtils.isEmpty(voiceRechargeAmount.getUserName())) {
        	request.setParameter(VoiceRechargeAmount.QUERY_USER_NAME, voiceRechargeAmount.getUserName());
        }
        if (voiceRechargeAmount.getStatus() != null && voiceRechargeAmount.getStatus().getValue() >= 0) {
        	request.setParameter(VoiceRechargeAmount.QUERY_STATUS, voiceRechargeAmount.getStatus().getValue() + "");
        }              
       
        if (pageBean != null) {
            request.setPage(pageBean.getPage());
            request.setPagesize(pageBean.getPageSize());
        }
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
        
        if (response == null) {
            logger.error("调用API查询语音充值限额失败");
            throw new ApiRemoteCallFailedException("调用API查询语音充值限额失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API查询语音充值限额请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API查询语音充值限额请求出错," + response.getMessage());
        }
        logger.info("结束调用查询语音充值限额API");
        List<VoiceRechargeAmount> voiceRechargeAmountList = VoiceRechargeAmount.convertFromJSONArray(response.getData());

        if (pageBean != null && pageBean.isPageFlag()) {
            int totalCount = response.getTotal();
            int pageCount = 0;
            pageBean.setCount(totalCount);
            
            if (pageBean.getPageSize() != 0 ) {
                pageCount = totalCount / pageBean.getPageSize();
                
                if (totalCount % pageBean.getPageSize() != 0) {
                    pageCount ++;
                }
            }
            pageBean.setPageCount(pageCount);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
        map.put(Global.API_MAP_KEY_LIST, voiceRechargeAmountList);
        return map;
	}
	
	/**
	 * 新增语音充值限额
	 */
	public boolean add(VoiceRechargeAmount voiceRechargeAmount) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加语音充值限额");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_VOICE_RECHARGE_AMOUNT_ADD);
		request.setParameterForUpdate(VoiceRechargeAmount.QUERY_ID, "");
		request.setParameterForUpdate(VoiceRechargeAmount.SET_STATUS, voiceRechargeAmount.getStatus().getValue() + "");
		request.setParameterForUpdate(VoiceRechargeAmount.SET_AMOUNT, voiceRechargeAmount.getAmount() + "");
		request.setParameterForUpdate(VoiceRechargeAmount.SET_USER_NAME, voiceRechargeAmount.getUserName());
		request.setParameterForUpdate(VoiceRechargeAmount.SET_WALLET_TYPE, voiceRechargeAmount.getWalletType().getValue() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加语音充值限额对象失败");
			throw new ApiRemoteCallFailedException("API添加语音充值限额对象失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取语音充值限额数据请求异常, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取语音充值限额数据请求异常,原因：" + response.getMessage());
		}
		logger.error("API添加语音充值限额对象成功, rc={}, message={}", response.getCode(), response.getMessage());
		return true;
	}
	

	/**
	 * 更改语音充值限额
	 */
	@Override
	public boolean update(VoiceRechargeAmount voiceRechargeAmount) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加语音充值限额");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_VOICE_RECHARGE_AMOUNT_UPDATE);
		request.setParameter(VoiceRechargeAmount.QUERY_ID, voiceRechargeAmount.getId() + "");
		request.setParameter(VoiceRechargeAmount.SET_USER_NAME, voiceRechargeAmount.getUserName());
		
		request.setParameterForUpdate(VoiceRechargeAmount.SET_STATUS, voiceRechargeAmount.getStatus().getValue() + "");
		if (voiceRechargeAmount.getAmount() != null && voiceRechargeAmount.getAmount() > 0) {
			request.setParameterForUpdate(VoiceRechargeAmount.SET_AMOUNT, voiceRechargeAmount.getAmount() + "");
		}

		if (voiceRechargeAmount.getWalletType() != null && voiceRechargeAmount.getWalletType().getValue() > 0) {
			request.setParameterForUpdate(VoiceRechargeAmount.SET_WALLET_TYPE, voiceRechargeAmount.getWalletType().getValue() + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加语音充值限额对象失败");
			throw new ApiRemoteCallFailedException("API添加语音充值限额对象失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取语音充值限额数据请求异常, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取语音充值限额数据请求异常,原因：" + response.getMessage());
		}
		logger.info("API添加语音充值限额对象成功, rc={}, message={}", response.getCode(), response.getMessage());
		return true;
	}
	
	@Override
	public VoiceRechargeAmount get(Long id) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询语音充值限额");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_VOICE_RECHARGE_AMOUNT_QUERY);
		if (id == null || id <= 0) {
			logger.error("调用API查询语音充值限额时id为空");
			throw new ApiRemoteCallFailedException("调用API查询语音充值限额时id为空");
		} else {
			request.setParameter(VoiceRechargeAmount.QUERY_ID, id + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
		    logger.error("调用API查询语音充值限额失败");
		    throw new ApiRemoteCallFailedException("调用API查询语音充值限额失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
		    logger.error("调用API查询语音充值限额请求出错, rc={}, message={}", response.getCode(), response.getMessage());
		    throw new ApiRemoteCallFailedException("调用API查询语音充值限额请求出错," + response.getMessage());
		}
		
		List<VoiceRechargeAmount> voiceRechargeAmountList = VoiceRechargeAmount.convertFromJSONArray(response.getData());
		if (voiceRechargeAmountList == null) {
			logger.error("调用API查询语音充值限额请求出错, 返回结果为空");
		    throw new ApiRemoteCallFailedException("调用API查询语音充值限额请求出错, 返回结果为空");
		} else if (voiceRechargeAmountList.size() != 1){
			logger.error("调用API查询语音充值限额请求出错, 返回结果异常，size={}", voiceRechargeAmountList.size());
		    throw new ApiRemoteCallFailedException("调用API查询语音充值限额请求出错, 返回结果异常，size=" + voiceRechargeAmountList.size());
		} else {
			logger.info("结束调用查询语音充值限额API");
			return voiceRechargeAmountList.get(0);
		}
	}

	@Override
	public boolean batchOperate(String[] ids, YesNoStatus status) throws ApiRemoteCallFailedException {
		logger.info("进入调用API语音充值限额批量操作");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_VOICE_RECHARGE_AMOUNT_USER_CONTROLLED);
		if (ids == null || ids.length == 0) {
			logger.error("调用API查询语音充值限额时ids为空");
			throw new ApiRemoteCallFailedException("调用API查询语音充值限额时ids为空");
		} else {
			request.setParameterIn(VoiceRechargeAmount.QUERY_IDS, Arrays.asList(ids));
		}
		if (status == null || status.getValue() == YesNoStatus.ALL.getValue()) {
			logger.error("调用API查询语音充值限额时处理状态(status)为空");
			throw new ApiRemoteCallFailedException("调用API查询语音充值限额时处理状态(status)为空");
		} else {
			request.setParameterForUpdate(VoiceRechargeAmount.SET_STATUS, status.getValue() + "");
		}
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API批量操作语音充值限额失败");
			throw new ApiRemoteCallFailedException("API批量操作语音充值限额失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API批量操作语音充值限额失败, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API批量操作语音充值限额数据请求异常,原因：" + response.getMessage());
		}
		logger.info("结束调用语音充值限额批量操作API");
		return false;
	}
	
	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}
	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

}
