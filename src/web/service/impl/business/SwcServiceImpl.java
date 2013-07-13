package web.service.impl.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.dao.business.SwcDao;
import com.lehecai.admin.web.domain.business.Swc;
import com.lehecai.admin.web.service.business.SwcService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 后台敏感词业务逻辑层实现类
 * @author yanweijie
 *
 */
public class SwcServiceImpl implements SwcService {
	private Logger logger = LoggerFactory.getLogger(SwcServiceImpl.class);

	private SwcDao swcDao;
	private ApiRequestService apiWriteRequestService;
	
	private static final int API_MANAGE_SWC_SUCCESS = 1;
	
	/**
	 * 多条件并分页查询所有敏感词
	 * @param name 敏感词
	 * @param status 状态
	 * @param pageBean 分页
	 */
	public List<Swc> findSwcList(final String name, final int status, final PageBean pageBean) {
		return swcDao.findSwcList(name, status, pageBean);
	}
	
	/**
	 * 封装多条件查询分页对象
	 * @param pageBean	分页对象
	 * @param name	敏感词
	 * @param status 状态
	 * @return
	 */
	public PageBean getPageBean(final PageBean pageBean, final String name, final int status) {
		return swcDao.getPageBean(pageBean, name, status);
	}
	
	/**
	 * 根据敏感词编号查询敏感词对象
	 * @param id 敏感词编号
	 */
	public Swc getById(Long id) {
		return swcDao.getById(id);
	}
	
	/**
	 * 根据敏感词查询敏感词对象
	 * @param name 敏感词
	 */
	public Swc getByName(final String name) {
		return swcDao.getByName(name);
	}
	
	/**
	 * 添加/修改敏感词
	 * @param adminSwc 敏感词对象
	 */
	public void merge(Swc adminSwc) {
		swcDao.merge(adminSwc);
	}
	
	/**
	 * 添加/修改敏感词
	 * @param adminSwc 敏感词对象
	 */
	public void mergeBatch(List<Swc> swcList) {
		for(Swc swc : swcList){
			Swc tempswc = getByName(swc.getName());//查询敏感词是否存在
			if(tempswc == null){
				swcDao.merge(swc);
			}
		}
	}
	
	/**
	 * 调用api添加敏感词，并且添加到数据库/修改状态为已启用
	 * @param swc 敏感词对象
	 */
	public void save(Swc swc) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加敏感词");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SWC_ADD);
		request.setParameterForUpdate(Swc.SET_WORD, swc.getName());
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加敏感词对象失败");
			throw new ApiRemoteCallFailedException("API添加敏感词对象失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取敏感词数据请求异常, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取敏感词数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API获取敏感词数据为空, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取敏感词数据为空");
		}
		if (response.getData().getJSONArray(0) == null || response.getData().getJSONArray(0).isEmpty()) {
			logger.error("API获取添加的敏感词数据为空, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取添加的敏感词数据为空");
		}
		if (response.getData().getJSONArray(0).getInt(1) != API_MANAGE_SWC_SUCCESS) {
			logger.error("API添加敏感词对象失败, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API添加敏感词对象失败");
		}
		
		logger.error("API添加敏感词对象成功, rc={}, message={}", response.getCode(), response.getMessage());
		swcDao.merge(swc);	//添加到数据库
	}
	
	/**
	 * 调用api删除敏感词，修改敏感词状态为已禁用
	 * @param adminSwc
	 * @throws ApiRemoteCallFailedException 
	 */
	public void del(Swc swc) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除敏感词");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SWC_DELETE);
		request.setParameterForUpdate(Swc.SET_WORD, swc.getName());
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除敏感词失败");
			throw new ApiRemoteCallFailedException("API删除敏感词失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除敏感词请求异常, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API删除敏感词请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API获取敏感词数据为空, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取敏感词数据为空");
		}
		if (response.getData().getJSONArray(0) == null || response.getData().getJSONArray(0).isEmpty()) {
			logger.error("API获取敏感词数据为空, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取敏感词数据为空");
		}
		if (response.getData().getJSONArray(0).getInt(1) != API_MANAGE_SWC_SUCCESS) {
			logger.error("API删除敏感词对象失败, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API删除敏感词对象失败");
		}
		
		logger.error("API删除敏感词对象成功, rc={}, message={}", response.getCode(), response.getMessage());
		swcDao.merge(swc);
	}

	
	/**
	 * 检测某串是否有敏感词
	 * @param str 字符串
	 * @param flag 过滤选项
	 */
	public Map<String, String> check(String str , int flag) throws ApiRemoteCallFailedException {
		logger.info("进入调用API检测敏感词");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SWC_CHECK);
		request.setParameterForUpdate(Swc.SET_WORD, str);
		request.setParameterForUpdate(Swc.SET_FLAG, flag + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API检测敏感词失败");
			throw new ApiRemoteCallFailedException("API检测敏感词失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API检测敏感词请求异常, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API检测敏感词请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API获取敏感词数据为空, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取敏感词数据为空");
		}

		Map<String,String> map = new HashMap<String,String>();
		map.put(Global.API_MAP_KEY_WORD, response.getData().getString(1));
		map.put(Global.API_MAP_KEY_FILTER_STRING, response.getData().getString(2));
		
		return map;
	}
	
	public SwcDao getSwcDao() {
		return swcDao;
	}

	public void setSwcDao(SwcDao swcDao) {
		this.swcDao = swcDao;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}
	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
}
