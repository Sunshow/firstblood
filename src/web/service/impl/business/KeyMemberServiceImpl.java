package web.service.impl.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.dao.business.KeyMemberDao;
import com.lehecai.admin.web.domain.business.KeyMember;
import com.lehecai.admin.web.service.business.KeyMemberService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 重点会员业务逻辑层实现类
 * @author yanweijie
 *
 */
public class KeyMemberServiceImpl implements KeyMemberService {
	private final Logger logger = LoggerFactory.getLogger(KeyMemberServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private MemberService memberService;
	
	private KeyMemberDao keyMemberDao;
	
	/**
	 * 查询所有重点会员(会员编码、会员用户名)
	 */
	public List<KeyMember> findList() {
		return keyMemberDao.findList();
	}
	
	/**
	 * 多条件并分页查询重点会员
	 * @param keyMemberUids	所有重点会员会员编码
	 * @param uid			会员编码
	 * @param userName		会员用户名
	 * @param rbeginDate	注册起始时间
	 * @param rendDate		注册结束时间
	 * @param lbeginDate	最后登录起始时间
	 * @param lendDate		最后登录结束时间
	 * @param orderStr		排序字段
	 * @param orderView		排序方式
	 * @param pageBean		分页对象
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> fuzzyQueryResult(List<String> keyMemberUids, Long uid, String userName, Date rbeginDate, Date rendDate, 
			Date lbeginDate, Date lendDate, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询重点会员信息");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_BATCH_UID_SEARCH);
		
		request.setParameterIn(Member.QUERY_UID, keyMemberUids);	//重点会员会员编号(必要的查询条件)
		if (uid != null && uid != 0) {
			request.setParameter(Member.QUERY_UID, String.valueOf(uid.longValue()));	//添加会员编号查询条件
		} else {
			if(userName != null && !"".equals(userName)){
				Long tempUid = null;
				try {
					tempUid = memberService.getIdByUserName(userName);
				} catch (Exception e) {
					logger.error("通过用户名获取用户ID异常!");
				}
				if(tempUid != null && tempUid != 0){
					request.setParameter(Member.QUERY_UID, String.valueOf(tempUid.longValue()));//添加会员编号查询条件
				} else {
					logger.info("用户名不存在！返回空记录！");
					return null;
				}
			}
		}
		if(rbeginDate != null){
			request.setParameterBetween(Member.QUERY_REG_TIME, DateUtil.formatDate(rbeginDate,DateUtil.DATETIME),null);	//添加注册起始时间
		}
		if(rendDate != null){
			request.setParameterBetween(Member.QUERY_REG_TIME, null,DateUtil.formatDate(rendDate,DateUtil.DATETIME));	//添加注册结束时间
		}
		if(lbeginDate != null){
			request.setParameterBetween(Member.QUERY_LAST_LOGIN_TIME, DateUtil.formatDate(lbeginDate,DateUtil.DATETIME),null);	//添加最后登录起始时间
		}
		if(lendDate != null){
			request.setParameterBetween(Member.QUERY_LAST_LOGIN_TIME, null,DateUtil.formatDate(lendDate,DateUtil.DATETIME));	//添加最后登录结束时间
		}
		if (orderStr != null && !"".equals(orderStr) && orderView != null && !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);	//添加排序字段/排序方式
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员数据失败");
			throw new ApiRemoteCallFailedException("API获取会员数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取会员数据请求出错");
		}

		//模糊查询返回特殊数据结构，需要手工解析
		JSONArray jsonArray = null;
		int total = 0;
		try {
			JSONArray jsonData = response.getData();
			if (jsonData != null && jsonData.size() > 0) {
				try {
					jsonArray = jsonData.getJSONObject(0).getJSONArray("data");
					total = jsonData.getJSONObject(0).getInt("total");
				} catch (Exception e) {
					logger.warn("API查询会员数据为空", e);
					return null;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (jsonArray == null) {
			logger.warn("API查询会员数据为空");
			return null;
		}
		List<Member> list = Member.convertFromJSONArray(jsonArray);
		if(pageBean != null){
			int totalCount = response.getTotal();
			pageBean.setCount(total);
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
	
	/**
	 * 根据会员编号查询重点会员
	 * @param uid 会员编号
	 */
	public KeyMember getByUid(Long uid) {
		return keyMemberDao.getByUid(uid);
	}
	
	/**
	 * 根据会员用户名查询重点会员
	 * @param userName 会员用户名
	 */
	public KeyMember getByUserName(String userName) {
		return keyMemberDao.getByUserName(userName);
	}
	
	/**
	 * 根据重点会员编号查询重点会员信息
	 * @param id 重点会员编号
	 */
	public KeyMember getById(Long id) {
		return keyMemberDao.getById(id);
	}
	
	/**
	 * 修改重点会员备注
	 * @param keyMember 重点会员
	 */
	public void mergeKeyMember(KeyMember keyMember) {
		keyMemberDao.mergeKeyMember(keyMember);
	}
	
	/**
	 * 删除重点会员
	 * @param id 重点会员编号
	 */
	public void deleteKeyMember(Long id) {
		keyMemberDao.deleteKeyMember(keyMemberDao.getById(id));
	}

	public KeyMemberDao getKeyMemberDao() {
		return keyMemberDao;
	}

	public void setKeyMemberDao(KeyMemberDao keyMemberDao) {
		this.keyMemberDao = keyMemberDao;
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
