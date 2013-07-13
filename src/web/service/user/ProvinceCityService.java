package web.service.user;

import java.util.Map;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface ProvinceCityService {

	/**
	 * 查询省信息
	 * @return 
	 */
	public Map<String, Object> getProvinceList() throws ApiRemoteCallFailedException;
	
	/**
	 * 根据省信息查询市信息
	 * @return 
	 */
	public Map<String, Object> getCityListByProvince(Integer province) throws ApiRemoteCallFailedException;

}
