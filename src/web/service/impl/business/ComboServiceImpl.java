package web.service.impl.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.ComboService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.Combo;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ComboSaleStatus;

public class ComboServiceImpl implements ComboService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public Map<String, Object> queryComboList(Long comboId, Long comborevId,
			YesNoStatus yesNoStatus, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询套餐列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GET_COMBO_LIST);
		if (comboId != null && comboId != 0L) {
			request.setParameter(Combo.QUERY_ID, comboId + "");
		}
		if (comborevId != null && comborevId != 0L) {
			request.setParameter(Combo.QUERY_COMBOREV_ID, comborevId + "");
		}
		if (yesNoStatus != null) {
			request.setParameter(Combo.QUERY_COMBO_STATUS, yesNoStatus.getValue() + "");
		}
		request.addOrder(Combo.ORDER_LOTTERYTYPE, ApiConstant.API_REQUEST_ORDER_DESC);
		request.addOrder(Combo.ORDER_ID, ApiConstant.API_REQUEST_ORDER_DESC);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API查询套餐列表失败");
			throw new ApiRemoteCallFailedException("API查询套餐列表失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API查询套餐列表请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API查询套餐列表请求出错");
		}
		List<Combo> list = Combo.convertFromJSONArray(response.getData());
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
		map.put(Global.API_MAP_KEY_LIST, list);
		logger.info("结束调用API查询套餐列表");
		return map;
	}
	
	@Override
	public void addCombo(Combo combo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加套餐列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_ADD_COMBO);
		if (combo == null) {
			logger.error("套餐属性为空");
			return;
		}
		if (combo.getName() != null && !"".equals(combo.getName())) {
			request.setParameterForUpdate(Combo.SET_NAME, combo.getName());
		}
		if (combo.getDescription() != null && !"".equals(combo.getDescription())) {
			request.setParameterForUpdate(Combo.SET_DESCRIPTION, combo.getDescription());
		}
		if (combo.getTotalPhase() != null && !"".equals(combo.getTotalPhase())) {
			request.setParameterForUpdate(Combo.SET_TOTAL_PHASE, combo.getTotalPhase());
		}
		if (combo.getComboLotteryType() != null && !"".equals(combo.getComboLotteryType())) {
			request.setParameterForUpdate(Combo.SET_COMBO_LOTTERYTYPE, combo.getComboLotteryType());
		}
		if (combo.getSelectType() != null && !"".equals(combo.getSelectType())) {
			request.setParameterForUpdate(Combo.SET_SELECT_TYPE, combo.getSelectType());
		}
		if (combo.getDuration() != null && !"".equals(combo.getDuration())) {
			request.setParameterForUpdate(Combo.SET_DURATION, combo.getDuration());
		}
		if (combo.getStatus() != null && combo.getStatus().getValue() != ComboSaleStatus.ALL.getValue()) {
			request.setParameterForUpdate(Combo.SET_STATUS, combo.getStatus().getValue() + "");
		}
		request.setParameterForUpdate(Combo.SET_AMOUNT, combo.getAmount() + "");
		if (combo.getConfig() != null && !"".equals(combo.getConfig())) {
			request.setParameterForUpdate(Combo.SET_CONFIG, combo.getConfig());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加套餐失败");
			throw new ApiRemoteCallFailedException("API添加套餐失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API添加套餐请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API添加套餐请求出错," + response.getMessage());
		}
		logger.info("结束调用API添加套餐列表");
	}

	@Override
	public void updateCombo(Combo combo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改套餐列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_UPDATE_COMBO);
		if (combo == null) {
			logger.error("套餐属性为空");
			return;
		}
		if (combo.getComboId() != null && !"".equals(combo.getComboId())) {
			request.setParameter(Combo.QUERY_ID, combo.getComboId());
		}
		request.setParameter("comborev_id", "16");
		if (combo.getName() != null && !"".equals(combo.getName())) {
			request.setParameterForUpdate(Combo.SET_NAME, combo.getName());
		}
		if (combo.getComboLotteryType() != null && !"".equals(combo.getComboLotteryType())) {
			request.setParameterForUpdate(Combo.SET_COMBO_LOTTERYTYPE, combo.getComboLotteryType());
		}
		if (combo.getDescription() != null && !"".equals(combo.getDescription())) {
			request.setParameterForUpdate(Combo.SET_DESCRIPTION, combo.getDescription());
		}
		if (combo.getTotalPhase() != null && !"".equals(combo.getTotalPhase())) {
			request.setParameterForUpdate(Combo.SET_TOTAL_PHASE, combo.getTotalPhase());
		}
		if (combo.getSelectType() != null && !"".equals(combo.getSelectType())) {
			request.setParameterForUpdate(Combo.SET_SELECT_TYPE, combo.getSelectType());
		}
		if (combo.getDuration() != null && !"".equals(combo.getDuration())) {
			request.setParameterForUpdate(Combo.SET_DURATION, combo.getDuration());
		}
		if (combo.getStatus() != null && combo.getStatus().getValue() != ComboSaleStatus.ALL.getValue()) {
			request.setParameterForUpdate(Combo.SET_STATUS, combo.getStatus().getValue() + "");
		}
		request.setParameterForUpdate(Combo.SET_AMOUNT, combo.getAmount() + "");
		if (combo.getConfig() != null && !"".equals(combo.getConfig())) {
			request.setParameterForUpdate(Combo.SET_CONFIG, combo.getConfig());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改套餐信息失败");
			throw new ApiRemoteCallFailedException("API修改套餐信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API修改套餐信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API修改套餐信息请求出错," + response.getMessage());
		}
		logger.info("结束调用API修改套餐信息");
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
