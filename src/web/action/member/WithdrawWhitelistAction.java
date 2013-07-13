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
import com.lehecai.core.api.user.WithdrawWhitelist;
import com.lehecai.core.api.user.WithdrawWhitelistType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.opensymphony.xwork2.Action;

public class WithdrawWhitelistAction extends BaseAction {
	private static final long serialVersionUID = 3898257030653848931L;
	private final Logger logger = LoggerFactory.getLogger(WithdrawWhitelistAction.class);

	private WithdrawService withdrawService;

	private String detail;
	private Integer whitelistTypeValue;
	private List<WithdrawWhitelist> whitelistList;
	private List<WithdrawWhitelistType> typeList;
	private WithdrawWhitelist whitelist;
	
	public String handle() {
		logger.info("进入白名单列表查询");
		typeList = WithdrawWhitelistType.getItemsForQuery();
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入白名单列表查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		typeList = WithdrawWhitelistType.getItemsForQuery();
		WithdrawWhitelistType type= null;
		if (whitelistTypeValue != null && whitelistTypeValue != WithdrawWhitelistType.ALL.getValue()) {
			type = WithdrawWhitelistType.getItem(whitelistTypeValue);
		}
		Map<String, Object> map;
		try {
			PageBean pageBean = super.getPageBean();
			map = withdrawService.getWhitelistResult(detail, type, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			whitelistList = (List<WithdrawWhitelist>) map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		logger.info("查询白名单列表结束");
		return "list";
	}
	
	public String input() {
		logger.info("进入新增白名单");
		typeList = WithdrawWhitelistType.getItems();
		logger.info("新增白名单结束");
		return "inputForm";
	}
	
	public String manage() {
		logger.info("进入保存白名单");
		try {
			if (whitelistTypeValue == null) {
				logger.error("保存白名单失败,原因：白名单类型为空。");
	            super.setErrorMessage("保存白名单失败,原因：白名单类型为空。");
	            return "failure";
			}
			WithdrawWhitelistType type = WithdrawWhitelistType.getItem(whitelistTypeValue);
			whitelist.setType(type);
			withdrawService.addWhitelist(whitelist);
			clearInfo();
        } catch (ApiRemoteCallFailedException e) {
            logger.error("保存白名单失败,api调用异常。原因：" + e.getMessage());
            super.setErrorMessage("保存白名单失败,api调用异常。原因：" + e.getMessage());
            return "failure";
        }
		logger.info("保存白名单结束");
		super.setSuccessMessage("保存白名单成功");
		super.setForwardUrl("/member/withdrawWhitelist.do?action=query");
		return "success";
	}

	public String delete() {
		logger.info("进入删除白名单");
		JSONObject rs = new JSONObject();
		rs.put("flag", "0");
		try {
			if (whitelistTypeValue == null) {
				logger.error("删除白名单失败,原因：白名单类型为空。");
				rs.put("msg", "删除白名单失败,原因：白名单类型为空。");
	            return Action.NONE;
			}
			WithdrawWhitelistType type = WithdrawWhitelistType.getItem(whitelistTypeValue);
			whitelist.setType(type);
            withdrawService.deleteWhitelist(whitelist);
            clearInfo();
            rs.put("flag", "1");
        } catch (ApiRemoteCallFailedException e) {
			logger.error("删除白名单,api调用异常" + e.getMessage());
			rs.put("msg", "删除白名单,api调用异常" + e.getMessage());
        }
        writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除白名单结束");
		return Action.NONE;
	}
	
	private void clearInfo () {
		whitelistTypeValue = -1;
		detail = null;
	}
	

	public List<WithdrawWhitelist> getWhitelistList() {
		return whitelistList;
	}

	public void setWhitelistList(List<WithdrawWhitelist> whitelistList) {
		this.whitelistList = whitelistList;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getWhitelistTypeValue() {
		return whitelistTypeValue;
	}

	public void setWhitelistTypeValue(Integer whitelistTypeValue) {
		this.whitelistTypeValue = whitelistTypeValue;
	}

	public void setTypeList(List<WithdrawWhitelistType> typeList) {
		this.typeList = typeList;
	}

	public List<WithdrawWhitelistType> getTypeList() {
		return typeList;
	}

	public void setWithdrawService(WithdrawService withdrawService) {
		this.withdrawService = withdrawService;
	}

	public WithdrawService getWithdrawService() {
		return withdrawService;
	}

	public void setWhitelist(WithdrawWhitelist whitelist) {
		this.whitelist = whitelist;
	}

	public WithdrawWhitelist getWhitelist() {
		return whitelist;
	}
	
}
