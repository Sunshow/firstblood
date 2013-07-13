package web.service.impl.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.business.GroupKeywords;
import com.lehecai.admin.web.service.business.GroupKeywordsService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.ApiResponseBatchUpdateParser;
import com.lehecai.core.api.SimpleApiBatchUpdateItem;
import com.lehecai.core.api.SimpleApiRequestBatchUpdate;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 分组关键词
 * @author He Wang
 *
 */
public class GroupKeywordsServiceImpl implements GroupKeywordsService {
	private Logger logger = LoggerFactory.getLogger(GroupKeywordsServiceImpl.class);

	private ApiRequestService apiWriteRequestService;
	private ApiRequestService apiRequestService;
	
	public Map<String, Object> queryGroupKeywordsList(GroupKeywords groupKeywords, PageBean pageBean) throws ApiRemoteCallFailedException {
		 logger.info("进入调用API查询分组关键词");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_GROUP_KEYWORDS_GET);
       
        if (groupKeywords.getGroupType() != null) {
        	request.setParameter(GroupKeywords.SET_GROUP_ID, groupKeywords.getGroupType().getValue() + "");
        }              
        if (!StringUtils.isEmpty(groupKeywords.getKeywords())) {
        	request.setParameter(GroupKeywords.SET_KEYWORDS, groupKeywords.getKeywords());
        }
       
        if (pageBean != null) {
            request.setPage(pageBean.getPage());
            request.setPagesize(pageBean.getPageSize());
        }
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
        
        if (response == null) {
            logger.error("调用API查询分组关键词失败");
            throw new ApiRemoteCallFailedException("调用API查询分组关键词失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API查询分组关键词请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API查询分组关键词请求出错," + response.getMessage());
        }
        logger.info("结束调用查询分组关键词API");
        List<GroupKeywords> groupKeywordsList = GroupKeywords.convertFromJSONArray(response.getData());

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
        map.put(Global.API_MAP_KEY_LIST, groupKeywordsList);
        return map;
	}
	
	/**
	 * 添加分组关键词
	 */
	public void save(GroupKeywords groupKeywords) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加分组关键词");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GROUP_KEYWORDS_ADD);
		request.setParameterForUpdate(GroupKeywords.SET_KEYWORDS, groupKeywords.getKeywords());
		request.setParameterForUpdate(GroupKeywords.SET_GROUP_ID, groupKeywords.getGroupType().getValue() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加分组关键词对象失败");
			throw new ApiRemoteCallFailedException("API添加分组关键词对象失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取分组关键词数据请求异常, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API获取分组关键词数据请求异常,原因：" + response.getMessage());
		}
		logger.info("API添加分组关键词对象成功, rc={}, message={}", response.getCode(), response.getMessage());
	}
	
	/**
	 * 调用api删除分组关键词，修改分组关键词状态为已禁用
	 * @param adminSwc
	 * @throws ApiRemoteCallFailedException 
	 */
	public void del(GroupKeywords groupKeywords) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除分组关键词");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GROUP_KEYWORDS_DELETE);
		request.setParameter(GroupKeywords.QUERY_ID, groupKeywords.getId() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除分组关键词失败");
			throw new ApiRemoteCallFailedException("API删除分组关键词失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除分组关键词请求异常, message={}", response.getMessage());
			throw new ApiRemoteCallFailedException("API删除分组关键词请求异常");
		}
		logger.info("API删除分组关键词对象成功, rc={}, message={}", response.getCode(), response.getMessage());
	}

	@Override
	public boolean batchSave(List<GroupKeywords> groupKeywordsList, List<String> successList, List<String> failureList, Map<String,String> insertIdMap) throws ApiRemoteCallFailedException {

		logger.info("进入调用API批量添加分组关键词");
		//批量更新专用request对象
		SimpleApiRequestBatchUpdate request = new SimpleApiRequestBatchUpdate();
		request.setUrl(ApiConstant.API_URL_GROUP_KEYWORDS_BATCH_ADD);
		if (groupKeywordsList != null) {
			for (int i=0; i<groupKeywordsList.size(); i++) {
				GroupKeywords groupKeywords = groupKeywordsList.get(i);
				try {
					SimpleApiBatchUpdateItem simpleApiBatchUpdateItem = new SimpleApiBatchUpdateItem();
					//新增时以序号作为id
					simpleApiBatchUpdateItem.setKey(i+"");
					simpleApiBatchUpdateItem.setParameterForUpdate(GroupKeywords.SET_GROUP_ID, groupKeywords.getGroupType().getValue() + "");
					simpleApiBatchUpdateItem.setParameterForUpdate(GroupKeywords.SET_KEYWORDS, groupKeywords.getKeywords());
					request.add(simpleApiBatchUpdateItem);
				} catch (Exception e) {
					logger.error("批量导入分组关键词失败！e={}", e.getMessage());
					return false;
				}
			}
		}
		
		logger.info("批量新增北京单场对阵,api request String: {}", request.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API批量新增北京单场数据异常!{}", e.getMessage());
			return false;
		}
		
		if (response == null) {
			logger.error("API批量新增北京单场数据失败");
			return false;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API批量新增北京单场数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return ApiResponseBatchUpdateParser.processResult(response, successList, failureList, insertIdMap);
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
