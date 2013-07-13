package web.service.impl.member;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.api.user.FreezeLog;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.MemberBasic;
import com.lehecai.core.api.user.Wallet;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.MemberStatus;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.service.setting.SettingService;
import com.lehecai.core.util.CoreJSONUtils;
import com.lehecai.core.util.CoreStringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class MemberServiceImpl implements MemberService {
	private final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private SettingService settingService;
	private MemberService memberService;
	
	@Override
	public Map<String, Object> getResult(String userName, String name, String phone,
			String email, Date rbeginDate, Date rendDate,
			Date lbeginDate, Date lendDate, String source, String orderStr, String orderView, boolean rechargered, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API多条件查询会员信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_QUERY);
		//通过用户名获取用户ID异常!
		if(userName != null && !"".equals(userName)){
			request.setParameter(Member.QUERY_UID, userName);
		}
		if(name != null && !"".equals(name)){
			request.setParameterLike(Member.QUERY_REALNAME, name);
		}
		if(phone != null && !"".equals(phone)){
			request.setParameterLike(Member.QUERY_PHONE, phone);
		}
		if(email != null && !"".equals(email)){
			request.setParameterLike(Member.QUERY_EMAIL, email);
		}
		if(source != null && !"".equals(source)){
			request.setParameter(Member.QUERY_SOURCE_ID, source);
		}
		if(rbeginDate != null){
			request.setParameterBetween(Member.QUERY_REG_TIME, DateUtil.formatDate(rbeginDate,DateUtil.DATETIME),null);
		}
		if(rendDate != null){
			request.setParameterBetween(Member.QUERY_REG_TIME, null,DateUtil.formatDate(rendDate,DateUtil.DATETIME));
		}
		if(lbeginDate != null){
			request.setParameterBetween(Member.QUERY_LAST_LOGIN_TIME, DateUtil.formatDate(lbeginDate,DateUtil.DATETIME),null);
		}
		if(lendDate != null){
			request.setParameterBetween(Member.QUERY_LAST_LOGIN_TIME, null,DateUtil.formatDate(lendDate,DateUtil.DATETIME));
		}
		if(rechargered){
			request.setParameterGreater(Member.QUERY_RECHARGE_COUNT, String.valueOf(0));
		}
		if (orderStr != null && !"".equals(orderStr) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
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
			throw new ApiRemoteCallFailedException("调用API获取会员数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		List<Member> list = Member.convertFromJSONArray(response.getData());
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
	
	@Override
	public Map<String, Object> fuzzyQueryResult(Long userid, String userName, String name, String phone,
			String email, String idData, Date rbeginDate,
			Date rendDate, Date lbeginDate, Date lendDate, String source, String orderStr, String orderView, boolean rechargered, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API模糊查询会员信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_SEARCH);
		//通过用户名获取用户ID异常!
		if (userid != null && userid != 0) {
			request.setParameter(Member.QUERY_UID, String.valueOf(userid.longValue()));
		} else {
			if(userName != null && !"".equals(userName)){
				request.setParameter(Member.QUERY_USERNAME, userName);
			}
		}
		if(name != null && !"".equals(name)){
			request.setParameterLike(Member.QUERY_REALNAME, name);
		}
		if(phone != null && !"".equals(phone)){
			request.setParameterLike(Member.QUERY_PHONE, phone);
		}
		if(email != null && !"".equals(email)){
			request.setParameterLike(Member.QUERY_EMAIL, email);
		}
		if(idData != null && !"".equals(idData)){
			request.setParameterLike(Member.QUERY_ID_DATA, idData);
		}
		if(source != null && !"".equals(source)){
			request.setParameter(Member.QUERY_SOURCE_ID, source);
		}
		if(rbeginDate != null){
			request.setParameterBetween(Member.QUERY_REG_TIME, DateUtil.formatDate(rbeginDate,DateUtil.DATETIME),null);
		}
		if(rendDate != null){
			request.setParameterBetween(Member.QUERY_REG_TIME, null,DateUtil.formatDate(rendDate,DateUtil.DATETIME));
		}
		if(lbeginDate != null){
			request.setParameterBetween(Member.QUERY_LATEST_LOGIN_TIME, DateUtil.formatDate(lbeginDate,DateUtil.DATETIME),null);
		}
		if(lendDate != null){
			request.setParameterBetween(Member.QUERY_LATEST_LOGIN_TIME, null,DateUtil.formatDate(lendDate,DateUtil.DATETIME));
		}
		if(rechargered){
			request.setParameterGreater(Member.QUERY_RECHARGE_COUNT, String.valueOf(0));
		}
		if (orderStr != null && !"".equals(orderStr) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
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
			throw new ApiRemoteCallFailedException("调用API获取会员数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		
		//模糊查询返回特殊数据结构，需要手工解析
		JSONArray jsonArray = null;
		int total = 0;
		
		JSONArray jsonData = response.getData();
		
		try {
			jsonArray = jsonData.getJSONObject(0).getJSONArray("data");
			total = jsonData.getJSONObject(0).getInt("total");
		} catch (Exception e) {
			logger.warn("API查询会员数据为空", e);
			return null;
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
	
	@Override
	public Member get(Long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据会员编码查询会员信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_QUERY);
		request.setParameter(Member.QUERY_UID, String.valueOf(uid));
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员数据失败");
			throw new ApiRemoteCallFailedException("API获取会员数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		List<Member> members = Member.convertFromJSONArray(response.getData());
		if(members != null && members.size() > 0){
			return members.get(0);
		}
		return null;
	}
	
	@Override
	public Member get(String userName) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据会员用户名查询会员信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_QUERY);
		request.setParameter(Member.QUERY_USERNAME, userName);
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员数据失败");
			throw new ApiRemoteCallFailedException("API获取会员数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		List<Member> members = Member.convertFromJSONArray(response.getData());
		if(members != null && members.size() > 0){
			return members.get(0);
		}
		return null;
	}
	@Override
	public List<Wallet> getWallets(Long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员钱包列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_QUERY);
		request.setParameter(Member.QUERY_UID, String.valueOf(uid));
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员钱包数据失败");
			throw new ApiRemoteCallFailedException("API获取会员钱包数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员钱包数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员钱包数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员钱包数据为空, message={}", response.getMessage());
			return null;
		}
		List<Member> members = Member.convertFromJSONArray(response.getData());
		if(members != null && members.size() > 0){
			Member member = members.get(0);
			List<Wallet> wallets = member.getWalletList();
			return wallets;
		}
		return null;
	}
	
	@Override
	public Wallet getWallet(Long uid, WalletType walletType) throws ApiRemoteCallFailedException{
		logger.info("进入调用API查询会员钱包");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_QUERY);
		request.setParameter(Member.QUERY_UID, String.valueOf(uid));
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员钱包数据失败");
			throw new ApiRemoteCallFailedException("API获取会员钱包数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员钱包数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员钱包数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员钱包数据为空, message={}", response.getMessage());
			return null;
		}
		List<Member> members = Member.convertFromJSONArray(response.getData());
		if(members != null && members.size() > 0){
			Member member = members.get(0);
			List<Wallet> wallets = member.getWalletList();
			for(Wallet wallet : wallets){
				if(wallet.getType().getValue() == walletType.getValue()){
					return wallet;
				}
			}
		}
		return null;
	}

	@Override
	public Long freezeWallet(Long uid, WalletType walletType, Double amount, String remark, Long frozenUid)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API执行冻结操作");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_WALLET_FREEZE);
		request.setParameter(FreezeLog.QUERY_UID, String.valueOf(uid));
		request.setParameter(FreezeLog.QUERY_WALLET_TYPE, String.valueOf(walletType.getValue()));
		request.setParameterForUpdate(FreezeLog.SET_AMOUNT, String.valueOf(amount));
		request.setParameterForUpdate(FreezeLog.SET_REMARK, String.valueOf(remark));
		request.setParameterForUpdate(FreezeLog.SET_FROZEN_UID, String.valueOf(frozenUid));
	
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API执行冻结操作请求失败");
			throw new ApiRemoteCallFailedException("API执行冻结操作请求失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API执行冻结操作请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API执行冻结操作请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API执行冻结操作返回数据为空, message={}", response.getMessage());
			return null;
		}

        JSONObject jsonObject = response.getData().getJSONObject(0);
        return CoreJSONUtils.getLong(jsonObject, FreezeLog.QUERY_DEDUCT_ID);
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

	@Override
	public ResultBean update(Long uid, String prop, String value, MemberStatus ms) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改会员信息");
		ApiRequest request = new ApiRequest();
		ResultBean resultBean = new ResultBean();
		request.setUrl(ApiConstant.API_URL_MEMBER_UPDATE);
		request.setParameter(Member.SET_UID, String.valueOf(uid));
		if (prop.equals(Member.SET_REALNAME)) {
			request.setParameterForUpdate(Member.SET_REALNAME, value);
        } else if (prop.equals(Member.SET_ID_TYPE)) {
            request.setParameterForUpdate(Member.SET_ID_TYPE, value);
		} else if (prop.equals(Member.SET_ID_DATA)) {
			request.setParameterForUpdate(Member.SET_ID_DATA, value);
		} else if (prop.equals(Member.SET_PHONE)) {
			request.setParameterForUpdate(Member.SET_PHONE, value);   
		} else if (prop.equals(Member.SET_EMAIL)) {
			request.setParameterForUpdate(Member.SET_EMAIL, value);
		} else if (prop.equals(Member.SET_STATUS)) {
			request.setParameterForUpdate(Member.SET_STATUS, ms.getValue() + "");
		} else if (prop.equals(Member.SET_PHONE_CHECKED)) {
			request.setParameterForUpdate(Member.SET_PHONE_CHECKED, value);
		} else if (prop.equals(Member.SET_EMAIL_CHECKED)) {
			request.setParameterForUpdate(Member.SET_EMAIL_CHECKED, value);
		}

		logger.info("修改会员信息：" + request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if(response == null ){
			logger.error("API修改会员信息失败!");
			resultBean.setResult(false);
			resultBean.setCode(ApiConstant.API_FALSE);
			resultBean.setMessage("修改会员信息失败，API响应结果为空");
			return resultBean;
		}
		
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("修改会员信息失败!{}", response.getMessage());
			resultBean.setResult(false);
		}else{
			logger.info("修改会员信息成功!{}", response.getMessage());
			resultBean.setResult(true);
		}
		resultBean.setMessage(response.getMessage());
		resultBean.setCode(response.getCode());
		
		return resultBean;
	}

	@Override
	public String resetPassword(Long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API重置会员信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_PASSWORD_RESET);
		request.setParameter(Member.SET_UID, String.valueOf(uid));
		String pwd = this.geneResetPassword();
		request.setParameterForUpdate(Member.SET_PASSWORD, pwd);
		
		logger.info("重置会员信息：" + request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if( response == null){
			logger.error("API重置会员信息失败");
			throw new ApiRemoteCallFailedException("API重置会员信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API重置会员信息请求异常");
			throw new ApiRemoteCallFailedException("API重置会员信息请求异常");
		}
		return pwd;
	}
	
	/**
	 * 生成6为随机密码
	 * @return String 生成的6位密码
	 */
	private String geneResetPassword(){
		StringBuffer pwdBuf = new StringBuffer();
		Random r = new Random(System.currentTimeMillis());
		for( int i = 0; i < 6; i++){
			int randPwd = Math.abs(r.nextInt()%10);
			pwdBuf.append(randPwd);
		}
		return pwdBuf.toString();
	}
	
	/**
	 * 生成的8位密码,需包含数字与字母
	 * @return String 
	 */
	private String geneResetPayPassword(){
		StringBuffer pwdBuf = new StringBuffer();
		Random r = new Random(System.currentTimeMillis());
		for( int i = 0; i < 6; i++){
			int randPwd = Math.abs(r.nextInt()%10);
			if (i % 3 == 1) {
				pwdBuf.append(geneRandomChar());
			}
			pwdBuf.append(randPwd);
		}
		return pwdBuf.toString();
	}
	
	private String geneRandomChar(){
		int random=(int) Math.round(Math.random()*25+97);
		char temp=(char) random;
		return String.valueOf(temp);
	}


	@Override
	public String getUserNameById(Long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员名");
		if(uid == null || uid == 0){
			logger.info("无效的uid");
			return null;
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_UID_TO_USERNAME);
		request.setParameter(Member.QUERY_UID, uid.toString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员名数据失败");
			throw new ApiRemoteCallFailedException("API获取会员名数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员名数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员名数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员名数据为空, message={}", response.getMessage());
			return null;
		}
		JSONObject object = response.getData().getJSONObject(0);
		return object.getString(uid.toString());
	}

	@Override
	public Long getIdByUserName(String userName) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员编码");
		if (StringUtils.isBlank(userName)) {
			logger.info("无效的userName");
			return null;
		}
        userName = StringUtils.trim(userName);

		//用户名转为小写
		userName = CoreStringUtils.lowerCaseAscII(userName);
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_USERNAME_TO_UID);
		request.setParameter(Member.QUERY_USERNAME, userName);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员编码数据失败");
			throw new ApiRemoteCallFailedException("API获取会员编码数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员编码数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员编码数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员编码数据为空, message={}", response.getMessage());
			return null;
		}
		JSONObject object = response.getData().getJSONObject(0);
		return CoreJSONUtils.getLong(object, userName);
	}

	@Override
	public void updateSpecialMember(List<String> userNames) throws ApiRemoteCallFailedException {
		String group = SettingConstant.GROUP_ADMIN;
		String item = SettingConstant.ITEM_HIGHLIGHT_UID_LIST;
		
		settingService.add(group, item);
		JSONArray array = new JSONArray();
		for(String userName : userNames){
			Long uid = this.getIdByUserName(userName);
			if(uid == null || uid == 0){
				continue;
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(uid.toString(), userName);
			array.add(jsonObject);
		}
		settingService.update(group, item, array.toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MemberBasic> listSpecialMembers() throws ApiRemoteCallFailedException {
		String group = SettingConstant.GROUP_ADMIN;
		String item = SettingConstant.ITEM_HIGHLIGHT_UID_LIST;
		
		settingService.add(group, item);
		String value = settingService.get(group, item);
		if(value == null || "".equals(value)){
			logger.info("group:{},item:{},未查询到结果",group, item);
			return null;
		}
		List<MemberBasic> list = new ArrayList<MemberBasic>();
		JSONArray array = JSONArray.fromObject(value);
		for (Iterator iterator = array.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			list.add(MemberBasic.convertFromJSONObject(obj));
		}
		return list;
	}

	@Override
	public List<Member> getSpecialMemberWallet(List<MemberBasic> list) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询特殊会员钱包列表");
		if(list == null || list.size() < 1){
			logger.error("传递的参数为空");
			return null;
		}
		List<String> uidList = new ArrayList<String>();
		for(MemberBasic memberBasic : list){
			uidList.add(memberBasic.getUid()+"");
		}
		if(uidList.size() < 1){
			logger.error("会员编码列表为空");
			return null;
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_QUERY);
		
		request.setParameterIn(Member.QUERY_UID, uidList);
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取特殊会员数据失败");
			throw new ApiRemoteCallFailedException("API获取特殊会员数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取特殊会员数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取特殊会员数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取特殊会员数据为空, message={}", response.getMessage());
			return null;
		}
		List<Member> memberList = Member.convertFromJSONArray(response.getData());
		if(memberList == null || memberList.size() < 1){
			logger.error("会员列表为空");
			return null;
		}
		
		return memberList;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	@Override
	public List<Member> getMembersByUids(List<String> uids)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员数据列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_QUERY);
		request.setParameterIn(Member.QUERY_UID, uids);
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员数据失败");
			throw new ApiRemoteCallFailedException("API获取会员数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		List<Member> members = Member.convertFromJSONArray(response.getData());
		if(members != null && members.size() > 0){
			return members;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUidsByUsernames(List<String> usernames)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员编码列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_USERNAME_TO_UID);
		request.setParameterIn(Member.QUERY_USERNAME, usernames);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员编码数据失败");
			throw new ApiRemoteCallFailedException("API获取会员编码数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员编码数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员编码数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员编码数据为空, message={}", response.getMessage());
			return null;
		}
		List<String> uids = new ArrayList<String>();
		JSONArray array = response.getData();
		for (Iterator iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			for (Iterator iterator2 = object.keySet().iterator(); iterator2.hasNext();) {
				String username = (String) iterator2.next();
				uids.add(object.getString(username));
			}
		}
		return uids;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getUidsByUsernamesForConvert(List<String> usernames)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员编码列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_USERNAME_TO_UID);
		request.setParameterIn(Member.QUERY_USERNAME, usernames);
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员编码数据失败");
			throw new ApiRemoteCallFailedException("API获取会员编码数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员编码数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员编码数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员编码数据为空, message={}", response.getMessage());
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		JSONArray array = response.getData();
		for (Iterator iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			for (Iterator iterator2 = object.keySet().iterator(); iterator2.hasNext();) {
				String username = (String) iterator2.next();
				String uid = object.getString(username);
				map.put(username, uid);
			}
		}
		return map;
	}

	@Override
	public String resetPayPassword(Long uid)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API重置会员支付密码");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_PAY_PASSWORD_RESET);
		request.setParameter(Member.SET_UID, String.valueOf(uid));
		String pwd = this.geneResetPayPassword();
		request.setParameterForUpdate(Member.SET_PASSWORD, pwd);
		
		logger.info("重置会员支付密码：" + request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if( response == null){
			logger.error("API重置会员支付密码失败");
			throw new ApiRemoteCallFailedException("API重置会员支付密码失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API重置会员支付密码请求异常,原因：{}", response.getMessage());
			throw new ApiRemoteCallFailedException("API重置会员支付密码请求异常，原因" + response.getMessage());
		}
		return pwd;
	}

	@Override
	public Map<String, Object> fuzzyQueryResultByCreditLevel(Long userid,
			String userName, String name, String phone, String email,
			String idData, Date rbeginDate, Date rendDate, Date lbeginDate,
			Date lendDate, String source, String orderStr, String orderView,
			boolean rechargered, Integer level, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_CREDIT_LEVEL_QUERY);
		if (level == null || level <= 0 || level > 15) {
			logger.error("按彩贝等级查询时level错误，level={}", level);
			throw new ApiRemoteCallFailedException("按彩贝等级查询时level错误，level=" + level);
		}
		request.setParameter("level", level + "");
		//通过用户名获取用户ID异常!
		if (userid != null && userid != 0) {
			request.setParameter(Member.QUERY_UID, String.valueOf(userid.longValue()));
		} else {
			if(userName != null && !"".equals(userName)){
				request.setParameter(Member.QUERY_USERNAME, userName);
			}
		}
		if(name != null && !"".equals(name)){
			request.setParameterLike(Member.QUERY_REALNAME, name);
		}
		if(phone != null && !"".equals(phone)){
			request.setParameterLike(Member.QUERY_PHONE, phone);
		}
		if(email != null && !"".equals(email)){
			request.setParameterLike(Member.QUERY_EMAIL, email);
		}
		if(idData != null && !"".equals(idData)){
			request.setParameterLike(Member.QUERY_ID_DATA, idData);
		}
		if(source != null && !"".equals(source)){
			request.setParameter(Member.QUERY_SOURCE_ID, source);
		}
		if(rbeginDate != null){
			request.setParameterBetween(Member.QUERY_REG_TIME, DateUtil.formatDate(rbeginDate,DateUtil.DATETIME),null);
		}
		if(rendDate != null){
			request.setParameterBetween(Member.QUERY_REG_TIME, null,DateUtil.formatDate(rendDate,DateUtil.DATETIME));
		}
		if(lbeginDate != null){
			request.setParameterBetween(Member.QUERY_LATEST_LOGIN_TIME, DateUtil.formatDate(lbeginDate,DateUtil.DATETIME),null);
		}
		if(lendDate != null){
			request.setParameterBetween(Member.QUERY_LATEST_LOGIN_TIME, null,DateUtil.formatDate(lendDate,DateUtil.DATETIME));
		}
		if(rechargered){
			request.setParameterGreater(Member.QUERY_RECHARGE_COUNT, String.valueOf(0));
		}
		if (orderStr != null && !"".equals(orderStr) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
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
			throw new ApiRemoteCallFailedException("调用API获取会员数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员数据为空, message={}", response.getMessage());
			return null;
		}
		
		//模糊查询返回特殊数据结构，需要手工解析
		JSONArray jsonArray = null;
		int total = 0;
		
		JSONArray jsonData = response.getData();
		
		try {
			jsonArray = jsonData.getJSONObject(0).getJSONArray("data");
			total = jsonData.getJSONObject(0).getInt("total");
		} catch (Exception e) {
			logger.warn("API查询会员数据为空", e);
			return null;
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

	public SettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(SettingService settingService) {
		this.settingService = settingService;
	}

}
