package web.service.impl.process;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.process.CouponGenerateService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.event.Coupon;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.CouponType;
import com.lehecai.core.lottery.WalletType;

public class CouponGenerateServiceImpl implements CouponGenerateService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String KEY_TYPE = "type";
	private static final String KEY_WALLET_TYPE = "wallet_type";
	private static final String KEY_AMOUNT = "amount";
	private static final String KEY_NUMBER = "number";
	private static final String KEY_EXPIRE_TIME = "expire_at";
	private static final String KEY_UID = "uid";
	private static final String KEY_EVENT_ID = "event_id";
	private static final int MAX_TIMES = 10;

	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;

	@Override
	public void couponGenerate(Coupon coupon,int number) throws ApiRemoteCallFailedException{
		logger.info("调用彩金卡生成API");
		if(coupon == null){
			logger.error("调用彩金卡生成API失败,彩金卡新消息为空");
			throw new ApiRemoteCallFailedException("调用彩金卡生成API失败,彩金卡新消息为空");
		}
		if(number <= 0){
			logger.error("调用彩金卡生成API失败,数量为空");
			throw new ApiRemoteCallFailedException("调用彩金卡生成API失败,数量为空");
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_GENERATE);
		//设置彩金卡的类型为"未绑定"
		CouponType ct = coupon.getType();
		if(ct == null || ct.getValue() == 0) {
			ct = CouponType.COUPON_TYPE_BASIC;
		}
		request.setParameterForUpdate(KEY_TYPE,ct.getValue()+"");
		//设置钱包类型为"彩金"
		request.setParameterForUpdate(KEY_WALLET_TYPE, WalletType.GIFT.getValue()+"");
		Double amount = coupon.getAmount();
		if(amount == null || amount <= 0){
			logger.error("调用彩金卡生成API失败,生成彩金卡金额应大于零");
			throw new ApiRemoteCallFailedException("调用彩金卡生成API失败,生成彩金金额应大于零");
		}
		request.setParameterForUpdate(KEY_AMOUNT, amount+"");
		if(number <= 0){
			logger.error("调用彩金卡生成API失败,生成彩金卡金额应大于零");
			throw new ApiRemoteCallFailedException("调用彩金卡生成API失败,生成彩金卡数量应大于零");
		}
		request.setParameterForUpdate(KEY_NUMBER, number+"");
		Date expireTime = coupon.getExpireTime();
		if(expireTime == null) {
			logger.error("调用彩金卡生成API失败,彩金卡截止时间为空");
			throw new ApiRemoteCallFailedException("调用彩金卡生成API失败,彩金卡截止时间为空");
		}
		request.setParameterForUpdate(KEY_EXPIRE_TIME, DateUtil.formatDate(expireTime));
		Long uid = coupon.getUid();
		if(ct == CouponType.COUPON_TYPE_USER_BOUND && uid != null) {
			request.setParameterForUpdate(KEY_UID, uid+"");
		}else {
			request.setParameterForUpdate(KEY_UID, "0");
		}
		if (coupon.getProcessId() == null) {
			logger.error("调用彩金卡生成API失败,流程编码为空");
			throw new ApiRemoteCallFailedException("调用彩金卡生成API失败,流程编码为空");
		}
		request.setParameterForUpdate(Coupon.QUERY_PROCESS_ID, coupon.getProcessId());
		Integer eventId = coupon.getEventId();
		if(eventId == null) {
			eventId = 0;
		}
		request.setParameterForUpdate(KEY_EVENT_ID, eventId+"");
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用彩金卡生成API失败");
			throw new ApiRemoteCallFailedException("调用彩金卡生成API失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用彩金卡生成API请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用彩金卡生成API请求出错," + response.getMessage());
		}
		logger.info("结束调用彩金卡生成API");
	}

	@Override
	public Map<String, Object> queryCouponList(String processId, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		if (processId == null) {
			logger.error("调用彩金卡查询API失败,流程编码为空");
			throw new ApiRemoteCallFailedException("调用彩金卡查询API失败,流程编码为空");
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_EXPORT);
		request.setParameter(Coupon.QUERY_PROCESS_ID, processId);
		request.addOrder(Coupon.QUERY_COUPON_ID, ApiConstant.API_REQUEST_ORDER_ASC);
		if (pageBean != null) {
            request.setPage(pageBean.getPage());
            request.setPagesize(pageBean.getPageSize());
        }
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用彩金卡查询API失败");
			throw new ApiRemoteCallFailedException("调用彩金卡查询API失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用彩金卡查询API失败, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用彩金卡查询API失败," + response.getMessage());
		}
		List<Coupon> couponList = Coupon.convertFromJSONArray(response.getData());
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
        map.put(Global.API_MAP_KEY_LIST, couponList);
        return map;
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

	@Override
	public Coupon enableCouponByProcessId(String processId)
			throws ApiRemoteCallFailedException {
		if (processId == null) {
			logger.error("调用彩金卡批量激活API失败,流程编码为空");
			throw new ApiRemoteCallFailedException("调用彩金卡批量激活API失败,流程编码为空");
		}
		Coupon coupon = new Coupon();
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_ENABLE_BY_PROCESSID);
		request.setParameter(Coupon.QUERY_PROCESS_ID, processId);
		int i=0;
		int enableNum = 0 ;
		while (i < MAX_TIMES) {
			logger.info("Request Query String: {}", request.toQueryString());
			ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
			if (response == null) {
				logger.error("调用彩金卡批量激活API失败");
				throw new ApiRemoteCallFailedException("调用彩金卡批量激活API失败");
			}
			if (response.getCode() != ApiConstant.RC_SUCCESS) {
				logger.error("调用彩金卡批量激活API请求出错, rc={}, message={}", response.getCode(), response.getMessage());
				throw new ApiRemoteCallFailedException("调用彩金卡批量激活API请求出错," + response.getMessage());
			}
			List<Coupon> couponList = Coupon.convertFromJSONArrayForDealNum(response.getData());
			if (couponList == null || couponList.size()<=0) {
				logger.error("调用彩金卡批量激活API返回为空");
				throw new ApiRemoteCallFailedException("调用彩金卡批量激活API返回为空");
			}
			Coupon cp = couponList.get(0);
			if (cp.getEnableNum() == null) {
				logger.error("调用彩金卡批量激活API返回激活条目数据为空");
				throw new ApiRemoteCallFailedException("调用彩金卡批量激活API返回激活条目数据为空");
			}
			if (cp.getTotalNum() == null) {
				logger.error("调用彩金卡批量激活API返回待激活条目数据为空");
				throw new ApiRemoteCallFailedException("调用彩金卡批量激活API返回待激活条目数据为空");
			}
			enableNum += cp.getEnableNum();
			if (cp.getTotalNum() - cp.getEnableNum() <= 0) {
				break;
			}
			i++;
		}
		coupon.setEnableNum(enableNum);
		return coupon;
	}

}
