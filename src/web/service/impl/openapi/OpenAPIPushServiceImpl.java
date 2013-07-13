package web.service.impl.openapi;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.OpenAPIPushService;

import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.openapi.OpenAPIAppType;
import com.lehecai.core.api.openapi.OpenAPIPush;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAPIPushServiceImpl implements OpenAPIPushService {

    private Logger logger = LoggerFactory.getLogger(OpenAPIPushServiceImpl.class);

    private ApiRequestService apiRequestService;
    private ApiRequestService apiWriteRequestService;
    
    @Override
    public Map<String, Object> findOpenAPIPushList(String id, String title, YesNoStatus status, OpenAPIAppType openAPIAppType, Date addTimeFrom, Date addTimeEnd, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
        logger.info("进入API查询openAPIPush信息");

        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_OPENAPI_PUSH_SEARCH);

        if (!StringUtils.isEmpty(id)) {
            request.setParameter(OpenAPIPush.QUERY_PUSH_ID, id);
        }
        if (!StringUtils.isEmpty(title)) {
            request.setParameterLike(OpenAPIPush.QUERY_PUSH_TITLE, title);
        }
        if (status != null && status.getValue() != YesNoStatus.ALL.getValue()) {
            request.setParameter(OpenAPIPush.QUERY_PUSH_STATUS, status.getValue() + "");
        }
        if (openAPIAppType != null && openAPIAppType.getValue() != OpenAPIAppType.ALL.getValue()) {
            request.setParameter(OpenAPIPush.QUERY_PUSH_APP_TYPE, openAPIAppType.getValue() + "");
        }
        if (addTimeFrom != null) {
            request.setParameterBetween(OpenAPIPush.QUERY_PUSH_TIME_ADD, CoreDateUtils.formatDate(addTimeFrom, CoreDateUtils.DATETIME), null);
        }
        if (addTimeEnd != null) {
            request.setParameterBetween(OpenAPIPush.QUERY_PUSH_TIME_ADD, null, CoreDateUtils.formatDate(addTimeEnd, CoreDateUtils.DATETIME));
        }
        if (!StringUtils.isEmpty(orderStr) && !StringUtils.isEmpty(orderView)) {
            request.addOrder(orderStr,orderView);
        } else {
        	request.addOrder(OpenAPIPush.ORDER_PUSH_TIME_ADD, ApiConstant.API_REQUEST_ORDER_DESC);
        }
        if (pageBean != null && pageBean.isPageFlag()) {
            request.setPage(pageBean.getPage());
            request.setPagesize(pageBean.getPageSize());
        }
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

        if (response == null) {
            logger.error("API查询openAPIPush信息失败");
            throw new ApiRemoteCallFailedException("API查询openAPIPush信息失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("API查询openAPIPush信息请求异常");
            throw new ApiRemoteCallFailedException("API查询openAPIPush信息请求异常");
        }
        if (response.getData() == null) {
            logger.error("API查询openAPIPush信息响应数据为空");
            return null;
        }
        List<OpenAPIPush> list = OpenAPIPush.convertFromJSONArray(response.getData());

        if (pageBean != null) {
            int totalCount = response.getTotal();
            pageBean.setCount(totalCount);
            int pageCount = 0;//页数

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

    @Override
    public boolean updateOpenAPIPush(OpenAPIPush openAPIPush) throws ApiRemoteCallFailedException {
        logger.info("进入API编辑openAPIPush信息");

        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_OPENAPI_PUSH_UPDATE);
        request.setParameter(OpenAPIPush.QUERY_PUSH_ID, openAPIPush.getId());
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_TITLE, openAPIPush.getTitle());//Push标题

        if (openAPIPush.getAppType() != null && openAPIPush.getAppType().getValue() != OpenAPIAppType.ALL.getValue()) {
            request.setParameterForUpdate(OpenAPIPush.SET_PUSH_APP_TYPE, openAPIPush.getAppType().getValue() + "");//平台类型
        }
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_APP_VERSION, openAPIPush.getAppVersion() + "");//版本号
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_SOURCE, openAPIPush.getSource());//排除的渠道
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_NEWS_ID, openAPIPush.getNewsId() + "");//对应活动ID
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_STATUS, openAPIPush.getStatus().getValue() + "");//状态
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_TIME, CoreDateUtils.formatDate(openAPIPush.getPushTime(), CoreDateUtils.DATETIME));//Push通知时间
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_CONTENT, openAPIPush.getContent());//Push内容
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_URL, openAPIPush.getUrl());//Push所对应的链接

        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

        if (response == null) {
            logger.error("API编辑openAPIPush信息失败");
            throw new ApiRemoteCallFailedException("API编辑openAPIPush信息失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("API编辑openAPIPush信息请求异常");
            return false;
        }
        return true;
    }

    @Override
    public boolean addOpenAPIPush(OpenAPIPush openAPIPush) throws ApiRemoteCallFailedException {
        logger.info("进入API添加openAPIPush信息");

        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_OPENAPI_PUSH_ADD);
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_TITLE, openAPIPush.getTitle());//push标题
        
        if (openAPIPush.getAppType() != null && openAPIPush.getAppType().getValue() != OpenAPIAppType.ALL.getValue()) {
            request.setParameterForUpdate(OpenAPIPush.SET_PUSH_APP_TYPE, openAPIPush.getAppType().getValue() + "");//平台类型
        }
    	request.setParameterForUpdate(OpenAPIPush.SET_PUSH_APP_VERSION, openAPIPush.getAppVersion());//版本号
    	request.setParameterForUpdate(OpenAPIPush.SET_PUSH_SOURCE, openAPIPush.getSource());//排除的渠道
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_NEWS_ID, openAPIPush.getNewsId());//对应活动ID
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_STATUS, openAPIPush.getStatus().getValue() + "");//状态
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_TIME, CoreDateUtils.formatDate(openAPIPush.getPushTime(), CoreDateUtils.DATETIME));//push通知时间
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_CONTENT, openAPIPush.getContent());//push内容
        request.setParameterForUpdate(OpenAPIPush.SET_PUSH_URL, openAPIPush.getUrl());//push所对应的链接
        
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

        if (response == null) {
            logger.error("API添加openAPIPush信息失败");
            throw new ApiRemoteCallFailedException("API添加openAPIPush信息失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("API添加openAPIPush信息请求异常");
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
