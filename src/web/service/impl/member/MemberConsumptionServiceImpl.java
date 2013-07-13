package web.service.impl.member;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberConsumptionService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.MemberConsumption;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;

/**
 * 会员消费业务逻辑层实现类
 * @author yanweijie
 *
 */
public class MemberConsumptionServiceImpl implements MemberConsumptionService {
	private final Logger logger = LoggerFactory.getLogger(MemberConsumptionServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private MemberService memberService;
	
	@Override
	public Map<String, Object> getResult(LotteryType lotteryType, Long userid, String username, Date beginDate, Date endDate, String orderStr,
			String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员消费数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_CONSUMPTION_QUERY);
		if(lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()){
			request.setParameter(MemberConsumption.QUERY_LOTTERY_TYPE, lotteryType.getValue()+"");
		}
		//通过用户名获取用户ID异常!
		if (userid != null && userid != 0) {
			request.setParameter(MemberConsumption.QUERY_UID, String.valueOf(userid.longValue()));
		} else {
			if (username != null && !"".equals(username)) {
				Long uid = null;
				try {
					uid = memberService.getIdByUserName(username);
				} catch (Exception e) {
					logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
				}
				if (uid != null && uid.longValue() != 0) {
					request.setParameter(MemberConsumption.QUERY_UID, String.valueOf(uid.longValue()));
				} else {
					logger.info("用户名不存在！返回空记录！");
					return null;
				}
			}
		}
		if(beginDate != null){
			request.setParameterBetween(MemberConsumption.QUERY_TIMELINE, DateUtil.formatDate(beginDate,DateUtil.DATETIME),null);
		}
		if(endDate != null){
			request.setParameterBetween(MemberConsumption.QUERY_TIMELINE, null,DateUtil.formatDate(endDate,DateUtil.DATETIME));
		}
		if(orderStr != null && !"".equals(orderStr) && orderView != null && !"".equals(orderView)){		
			request.addOrder(orderStr,orderView);
		}
		if(pageBean != null){		
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		//ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		ApiResponse response = apiRequestService.request(request, 20000);
		
		if (response == null) {
			logger.error("API获取会员消费数据失败");
			throw new ApiRemoteCallFailedException("API获取会员消费数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员消费数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员消费数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员消费数据为空, message={}", response.getMessage());
			return null;
		}
		List<MemberConsumption> list = MemberConsumption.convertFromJSONArray(response.getData());
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
		return map;
	}
	
	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}
	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
}
