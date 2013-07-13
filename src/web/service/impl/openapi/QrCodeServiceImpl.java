package web.service.impl.openapi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.QrCodeService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.openapi.QrCode;
import com.lehecai.core.api.openapi.QrCodeScan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 2013-05-09
 * @author He Wang
 *
 */
public class QrCodeServiceImpl implements QrCodeService {
	private Logger logger = LoggerFactory.getLogger(QrCodeServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 分页并多条件查询应用
	 */
	public Map<String, Object> queryQrCodeList(Long qrCodeId, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入API查询二维码信息");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_QRCODE_SEARCH);
		if (qrCodeId != null && qrCodeId != 0L) {							                    //应用编码
			request.setParameter(QrCode.QUERY_QR_ID, qrCodeId + "");
		}
		if (pageBean != null && pageBean.isPageFlag()) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API查询二维码信息失败");
			throw new ApiRemoteCallFailedException("API查询二维码信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询二维码信息请求异常");
			throw new ApiRemoteCallFailedException("API查询二维码信息请求异常");
		}
		if (response.getData() == null) {
			logger.error("API查询二维码信息响应数据为空");
			return null;
		}
		
		List<QrCode> list = QrCode.convertFromJSONArray(response.getData());
		
		if (pageBean != null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;// 页数
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
	
	@SuppressWarnings("unchecked")
	public QrCode getQrCode(Long qrCodeId) throws ApiRemoteCallFailedException{
		QrCode qrCode = null;
		Map<String, Object> map = queryQrCodeList(qrCodeId, null);
		if (map.get(Global.API_MAP_KEY_LIST) != null) {
			List<QrCode> qrCodeList = (List<QrCode>)map.get(Global.API_MAP_KEY_LIST);
			if (qrCodeList.size() == 1) {
				qrCode = qrCodeList.get(0);
			}
		}
		return qrCode;
	}
	
	/**
	 * 添加二维码
	 */
	@Override
	public Long addQrCode(QrCode qrCode) throws ApiRemoteCallFailedException{
		logger.info("进入API添加二维码信息");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_QRCODE_ADD);
		request.setParameterForUpdate(QrCode.SET_QR_NAME, qrCode.getName());
		request.setParameterForUpdate(QrCode.SET_QR_URL, qrCode.getUrl());
		request.setParameterForUpdate(QrCode.SET_QR_ANDROID_URL, qrCode.getAndroidUrl());
		request.setParameterForUpdate(QrCode.SET_QR_ANDROID_VERSION, qrCode.getAndroidVersion());
		request.setParameterForUpdate(QrCode.SET_QR_IPHONE_URL, qrCode.getIphoneUrl());		
		request.setParameterForUpdate(QrCode.SET_QR_IPHONE_VERSION, qrCode.getIphoneVersion());
		request.setParameterForUpdate(QrCode.SET_QR_IPAD_URL, qrCode.getIpadUrl());
		request.setParameterForUpdate(QrCode.SET_QR_IPAD_VERSION, qrCode.getIpadVersion());
		request.setParameterForUpdate(QrCode.SET_QR_WAP_URL, qrCode.getWapUrl());
		request.setParameterForUpdate(QrCode.SET_QR_WAP_VERSION, qrCode.getWapVersion());
		request.setParameterForUpdate(QrCode.SET_QR_REMARK, qrCode.getRemark());
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API添加二维码信息失败");
			throw new ApiRemoteCallFailedException("API添加二维码信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API添加二维码信息请求异常");
			
		}
		if (response.getData() == null) {
			logger.error("API添加二维码信息响应数据为空");
			return null;
		}
		JSONArray dataArray = response.getData();
		Integer newId = (Integer)dataArray.get(0);
		return Long.valueOf(newId);
	}
	
	/**
	 * 编辑应用
	 */
	@Override
	public boolean updateQrCode(QrCode qrCode) throws ApiRemoteCallFailedException {
		logger.info("进入API编辑二维码信息推荐");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_QRCODE_UPDATE);
		request.setParameter(QrCode.QUERY_QR_ID, qrCode.getId() + "");
		request.setParameterForUpdate(QrCode.SET_QR_NAME, qrCode.getName());
		request.setParameterForUpdate(QrCode.SET_QR_URL, qrCode.getUrl());
		request.setParameterForUpdate(QrCode.SET_QR_ANDROID_URL, qrCode.getAndroidUrl());
		request.setParameterForUpdate(QrCode.SET_QR_ANDROID_VERSION, qrCode.getAndroidVersion());		
		request.setParameterForUpdate(QrCode.SET_QR_IPHONE_URL, qrCode.getIphoneUrl());
		request.setParameterForUpdate(QrCode.SET_QR_IPHONE_VERSION, qrCode.getIphoneVersion());
		request.setParameterForUpdate(QrCode.SET_QR_IPAD_URL, qrCode.getIpadUrl());
		request.setParameterForUpdate(QrCode.SET_QR_IPAD_VERSION, qrCode.getIpadVersion());
		request.setParameterForUpdate(QrCode.SET_QR_WAP_URL, qrCode.getWapUrl());
		request.setParameterForUpdate(QrCode.SET_QR_WAP_VERSION, qrCode.getWapVersion());
		request.setParameterForUpdate(QrCode.SET_QR_REMARK, qrCode.getRemark());
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API编辑二维码信息推荐失败");
			throw new ApiRemoteCallFailedException("API编辑二维码信息推荐失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API编辑二维码信息推荐请求异常");
			return false;
		}
		return true;
	}
	
	@Override
	public Map<String, Object> queryQrCodeScanList(Long qrCodeId, Date beginDate, Date endDate, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入API查询二维码扫描信息");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_QRCODE_LOG_SEARCH);
		
		if (beginDate != null) {
			request.setParameterBetween(QrCodeScan.QUERY_SCAN_DATE, CoreDateUtils.formatDate(beginDate, CoreDateUtils.DATETIME), null);
		}
		if (endDate != null) {
			request.setParameterBetween(QrCodeScan.QUERY_SCAN_DATE, null, CoreDateUtils.formatDate(endDate, CoreDateUtils.DATETIME));
		}
		
		if (qrCodeId != null && qrCodeId != 0L) {													//应用编码
			request.setParameter(QrCodeScan.QUERY_QR_CODE_ID, qrCodeId + "");
		}
		if (pageBean != null && pageBean.isPageFlag()) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API查询二维码扫描信息失败");
			throw new ApiRemoteCallFailedException("API查询二维码扫描信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询二维码扫描信息请求异常");
			throw new ApiRemoteCallFailedException("API查询二维码扫描信息请求异常");
		}
		if (response.getData() == null) {
			logger.error("API查询二维码扫描信息响应数据为空");
			return null;
		}
		
		List<QrCodeScan> list = QrCodeScan.convertFromJSONArray(response.getData());
		
		if (pageBean != null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;// 页数
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