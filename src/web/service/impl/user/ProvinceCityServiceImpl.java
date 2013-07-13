package web.service.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.user.ProvinceCityService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.City;
import com.lehecai.core.api.user.Province;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class ProvinceCityServiceImpl implements ProvinceCityService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiRequestService;
	
	@Override
	public Map<String, Object> getCityListByProvince(Integer province)
			throws ApiRemoteCallFailedException {

		logger.info("进入调用API根据省信息查询市信息信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_AREA_GET_CITY_LIST_BY_PROVINCE);
		if (province == null || province == 0) {
			logger.error("省ID为0");
			throw new ApiRemoteCallFailedException("省ID为0");
		} else {
			request.setParameter(Province.SET_PROVINCE_ID, province + "");
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API根据省信息查询市信息失败");
			throw new ApiRemoteCallFailedException("API根据省信息查询市信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API根据省信息查询市信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API根据省信息查询市信息请求出错");
		}
		List<City> list = City.convertFromJSONObjectMap(JSONObject.fromObject(response.getData().get(0)));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_LIST, list);
		logger.info("结束调用API根据省信息查询市信息");
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getProvinceList()
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询省信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_AREA_GET_PROVINCE_LIST);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取省信息失败");
			throw new ApiRemoteCallFailedException("API获取省信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取省信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取省信息请求出错");
		}
		List<Province> list = Province.convertFromJSONObjectMap(JSONObject.fromObject(response.getData().get(0)));
		
		for(Province p : list) {
			Map<String, Object> map = getCityListByProvince(p.getProvinceId());
			if (map != null && map.size() > 0) {
				List<City> cities = (List<City>) map.get(Global.API_MAP_KEY_LIST);
				p.setCities(cities);
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_LIST, list);
		logger.info("结束调用API查询省信息");
		return map;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

}
