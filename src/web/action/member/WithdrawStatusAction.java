package web.action.member;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.member.WithdrawService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.WithdrawLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WithdrawStatus;
import com.opensymphony.xwork2.Action;

/**
 * 查询会员提款状态
 * @author He Wang
 *
 */
public class WithdrawStatusAction extends BaseAction {
	private static final long serialVersionUID = 3426161159465353828L;
	private final Logger logger = LoggerFactory.getLogger(WithdrawStatusAction.class);
	
	private WithdrawService withdrawService;
	private String userName;
	private Double amount;
	private String id;

	public String handle() {
		logger.info("进入查询会员提款状态查询");
		return "list";
	}
	
	public String query() {
		logger.info("进入查询审核状态");
		JSONObject rs = new JSONObject();
		//如果id不为空
		//查看审核状态
		Map<String, Object> map = null;
		String tempUserName = userName;
		String tempAmount = amount + "";
		String tempStatus = "";
		if (!StringUtils.isEmpty(id)) {
			try {
				map = withdrawService.queryAuditStatus(id, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				rs.put("msg", "获取状态失败");
			}
		} else {
			if (StringUtils.isEmpty(userName)) {
				rs.put("code", 1);
				rs.put("msg", "用户名不能为空");
				super.writeRs(ServletActionContext.getResponse(), rs);
				return Action.NONE;
			}
			if (amount == null) {
				rs.put("code", 1);
				rs.put("msg", "提款金额不能为空");
				super.writeRs(ServletActionContext.getResponse(), rs);
				return Action.NONE;
			}
			
			try {
				map = withdrawService.queryAuditStatus(null, userName, amount);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				rs.put("msg", "获取状态失败");
			}
		}
		if (map == null) {
			rs.put("code", 1);
			rs.put("msg", "查询 " + userName + " 审核状态异常");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		if (map.get(ApiConstant.API_RESPONSE_CODE_NAME) == null) {
			rs.put("code", 1);
			rs.put("msg", "查询 " + userName + " 审核状态异常");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		JSONArray jsonArray = (JSONArray)map.get(ApiConstant.API_RESPONSE_DATA_NAME);
		if (jsonArray != null && jsonArray.size() > 0) {
			Object obj = jsonArray.get(0);
			JSONObject jsonObj = (JSONObject)obj;
			if (jsonObj.containsKey(WithdrawLog.QUERY_USERNAME)) {
				tempUserName = jsonObj.getString(WithdrawLog.QUERY_USERNAME);
			}
			if (jsonObj.containsKey(WithdrawLog.QUERY_AMOUNT)) {
				tempAmount = jsonObj.getString(WithdrawLog.QUERY_AMOUNT);
			}
			if (jsonObj.containsKey(WithdrawLog.QUERY_STATUS)) {
				tempStatus = WithdrawStatus.getItem(jsonObj.getInt(WithdrawLog.QUERY_STATUS)) == null ? "" : WithdrawStatus.getItem(jsonObj.getInt(WithdrawLog.QUERY_STATUS)).getName();
			}
		}
		rs.put("id", id);
		rs.put("userName", tempUserName);
		rs.put("status", tempStatus);
		rs.put("amount", tempAmount);
		rs.put("msg", (String)map.get(ApiConstant.API_RESPONSE_MESSAGE_NAME));
		rs.put("code", 0);
		super.writeRs(ServletActionContext.getResponse(), rs);
		return Action.NONE;
	}

	public List<YesNoStatus> getYesNoStatus () {
		return YesNoStatus.getItemsForQuery();
	}
	
	public WithdrawService getWithdrawService() {
		return withdrawService;
	}
	public void setWithdrawService(WithdrawService withdrawService) {
		this.withdrawService = withdrawService;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	
}
