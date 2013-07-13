package web.action.member;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.WithdrawService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.WithdrawBlacklist;
import com.lehecai.core.api.user.WithdrawBlacklistType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.opensymphony.xwork2.Action;

public class WithdrawBlacklistAction extends BaseAction {
	private static final long serialVersionUID = 4246116530653833424L;
	private final Logger logger = LoggerFactory.getLogger(WithdrawBlacklistAction.class);

	private WithdrawService withdrawService;

	private String detail;
	private Integer blacklistTypeValue;
	private List<WithdrawBlacklist> blacklistList;
	private List<WithdrawBlacklistType> typeList;
	private WithdrawBlacklist blacklist;
	
	public String handle() {
		logger.info("进入黑名单列表查询");
		typeList = WithdrawBlacklistType.getItemsForQuery();
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入黑名单列表查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		typeList = WithdrawBlacklistType.getItemsForQuery();
		WithdrawBlacklistType type= null;
		if (blacklistTypeValue != null && blacklistTypeValue != WithdrawBlacklistType.ALL.getValue()) {
			type = WithdrawBlacklistType.getItem(blacklistTypeValue);
		}
		Map<String, Object> map;
		try {
			PageBean pageBean = super.getPageBean();
			map = withdrawService.getBlacklistResult(detail, type, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			blacklistList = (List<WithdrawBlacklist>) map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		logger.info("查询黑名单列表结束");
		return "list";
	}
	
	public String input() {
		logger.info("进入新增黑名单");
		typeList = WithdrawBlacklistType.getItems();
		logger.info("新增黑名单结束");
		return "inputForm";
	}
	
	public String manage() {
		logger.info("进入保存黑名单");
		try {
			if (blacklistTypeValue == null) {
				logger.error("保存黑名单失败,原因：黑名单类型为空。");
	            super.setErrorMessage("保存黑名单失败,原因：黑名单类型为空。");
	            return "failure";
			}
			WithdrawBlacklistType type = WithdrawBlacklistType.getItem(blacklistTypeValue);
			blacklist.setType(type);
			withdrawService.addBlacklist(blacklist);
			clearInfo();
        } catch (ApiRemoteCallFailedException e) {
            logger.error("保存黑名单失败,api调用异常。原因：" + e.getMessage());
            super.setErrorMessage("保存黑名单失败,api调用异常。原因：" + e.getMessage());
            return "failure";
        }
    	super.setSuccessMessage("保存黑名单成功");
		super.setForwardUrl("/member/withdrawBlacklist.do?action=query");
		logger.info("保存黑名单结束");
		return "success";
	}

	public String delete() {
		logger.info("进入删除黑名单");
		JSONObject rs = new JSONObject();
		rs.put("flag", "0");
		try {
			if (blacklistTypeValue == null) {
				logger.error("删除黑名单失败,原因：黑名单类型为空。");
				rs.put("msg", "删除黑名单失败,原因：黑名单类型为空。");
	            return Action.NONE;
			}
			WithdrawBlacklistType type = WithdrawBlacklistType.getItem(blacklistTypeValue);
            blacklist.setType(type);
            withdrawService.deleteBlacklist(blacklist);
            clearInfo();
            rs.put("flag", "1");
        } catch (ApiRemoteCallFailedException e) {
			logger.error("删除黑名单,api调用异常" + e.getMessage());
			rs.put("msg", "删除黑名单,api调用异常" + e.getMessage());
        }
        writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除黑名单结束");
		return Action.NONE;
	}
	
	private void clearInfo () {
		blacklistTypeValue = -1;
		detail = null;
	}
	
	public void setBlacklistList(List<WithdrawBlacklist> blacklistList) {
		this.blacklistList = blacklistList;
	}

	public List<WithdrawBlacklist> getBlacklistList() {
		return blacklistList;
	}
	
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getBlacklistTypeValue() {
		return blacklistTypeValue;
	}

	public void setBlacklistTypeValue(Integer blacklistTypeValue) {
		this.blacklistTypeValue = blacklistTypeValue;
	}

	public void setTypeList(List<WithdrawBlacklistType> typeList) {
		this.typeList = typeList;
	}

	public List<WithdrawBlacklistType> getTypeList() {
		return typeList;
	}

	public void setBlacklist(WithdrawBlacklist blacklist) {
		this.blacklist = blacklist;
	}

	public WithdrawBlacklist getBlacklist() {
		return blacklist;
	}

	public void setWithdrawService(WithdrawService withdrawService) {
		this.withdrawService = withdrawService;
	}

	public WithdrawService getWithdrawService() {
		return withdrawService;
	}
	
}
