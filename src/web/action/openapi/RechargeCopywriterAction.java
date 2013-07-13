package web.action.openapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.RechargeCopywriterService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.openapi.DocPosition;
import com.lehecai.core.api.openapi.RechargeCopywriter;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
/**
 * 2013-07-01
 * 充值文案管理
 * @author likunpeng
 * 
 */
public class RechargeCopywriterAction extends BaseAction {

	private static final long serialVersionUID = 1803042499820675627L;
	
	private Integer isValid;
	private Integer docPlace;
	private String orderStr;
	private String orderView;
	private List<YesNoStatus> yesNoStatusList;
	private List<DocPosition> docPositionList;
	private List<RechargeCopywriter> rechargeCopywriterList;
	private RechargeCopywriter rechargeCopywriter;
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	private String flag;
	
	private RechargeCopywriterService rechargeCopywriterService;

	/**
	 * 充值文案管理查询
	 * @return
	 */
	public String handle() {
		logger.info("进入充值文案管理");
		yesNoStatusList = YesNoStatus.getItemsForQuery();
		docPositionList = DocPosition.getItemsForQuery();
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入充值文案管理查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		yesNoStatusList = YesNoStatus.getItemsForQuery();
		docPositionList = DocPosition.getItemsForQuery();
		if (rechargeCopywriter == null) {
			rechargeCopywriter = new RechargeCopywriter();
		}
		rechargeCopywriter.setIsValid(isValid);
		rechargeCopywriter.setDocPlace(docPlace);
		rechargeCopywriter.setQueryStr(this.getOrderStr());
		rechargeCopywriter.setQueryOrder(this.getOrderView());
		Map<String, Object> map = null;
		try {
			map = rechargeCopywriterService.getList(rechargeCopywriter, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询充值文案，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			rechargeCopywriterList = (List<RechargeCopywriter>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		logger.info("充值文案管理查询结束");
		return "list";
	}
	
	public String input() {
		if (rechargeCopywriter == null) {
			logger.info("进入添加充值文案信息");
			rechargeCopywriter = new RechargeCopywriter();
		}
		yesNoStatusList = YesNoStatus.getItems();
		docPositionList = DocPosition.getItems();
		if (rechargeCopywriter.getId() != null && rechargeCopywriter.getId() != 0L) { 
			logger.info("进入修改充值文案信息！");
			try {
				if (rechargeCopywriter.getId() == null) {
					logger.info("你修改的充值文案信息不存在！");
					super.setErrorMessage("你修改的充值文案信息不存在！");
					super.setForwardUrl("/openapi/rechargeCopywriter.do");
					return "failure";
				} else {
					logger.info("进入修改的充值文案信息页面！");
					rechargeCopywriter = rechargeCopywriterService.get(rechargeCopywriter.getId());
					isValid = rechargeCopywriter.getStatus().getValue();
					docPlace = rechargeCopywriter.getDocPosition().getValue();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.info("没有查找到修改的充值文案信息");
				super.setForwardUrl("/openapi/rechargeCopywriter.do");
				return "failure";
			}
		} 
		logger.info("添加/修改充值文案信息结束");
		return "inputForm";
	}
	
	/**
	 * 保存添加和修改充值文案信息
	 */
	public String manage() {
    	logger.info("进入保存充值文案信息");
		Long newId = -1L;
		boolean result = false;
		if (rechargeCopywriter.getId() != null && rechargeCopywriter.getId() != 0L) { 
			try {
	  			rechargeCopywriter.setDocPlace(docPlace);
    			rechargeCopywriter.setIsValid(isValid);
				result = rechargeCopywriterService.updateRechargeCopywriterInfo(rechargeCopywriter);
				newId = rechargeCopywriter.getId();
			} catch (Exception e) {
				logger.error("充值文案信息更新异常，{}", e.getMessage(), e);
				super.setErrorMessage("API调用异常，请联系技术人员!"  + e.getMessage());
				return "failure";
			}
		} else {
    		try {
    			rechargeCopywriter.setDocPlace(docPlace);
    			rechargeCopywriter.setIsValid(isValid);
    			newId = rechargeCopywriterService.addRechargeCopywriterInfo(rechargeCopywriter);
				if (newId > 0) {
					result = true;
				}
			} catch (Exception e) {
				logger.error("充值文案信息添加异常，{}", e.getMessage(), e);
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				super.setForwardUrl("/openapi/rechargeCopywriter.do");
				return "failure";
			}
		}
    	
		super.setForwardUrl("/openapi/rechargeCopywriter.do?action=handle");
		if (result) {
			logger.info("操作成功");
			super.setErrorMessage("操作成功");
			return "success";
		} else {
			logger.error("操作失败");
			super.setErrorMessage("操作失败");
			return "failure";
		}
    }
	
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(RechargeCopywriter.ORDER_ID, "序号");
		orderStrMap.put(RechargeCopywriter.ORDER_UPDATE_TIME, "更新时间");
		orderStrMap.put(RechargeCopywriter.ORDER_ADD_TIME, "新增时间");
		orderStrMap.put(RechargeCopywriter.ORDER_RECHARGE_TYPE, "编号");
		return orderStrMap;
	}
	
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}

	public String getOrderStr() {
		if (orderStr == null || "".equals(orderStr)) {
			orderStr = RechargeCopywriter.ORDER_ID;
		}
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getOrderView() {
		if (orderView == null || "".equals(orderView)) {
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}

	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}

	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}

	public List<YesNoStatus> getYesNoStatusList() {
		return yesNoStatusList;
	}

	public void setYesNoStatusList(List<YesNoStatus> yesNoStatusList) {
		this.yesNoStatusList = yesNoStatusList;
	}

	public void setRechargeCopywriterService(RechargeCopywriterService rechargeCopywriterService) {
		this.rechargeCopywriterService = rechargeCopywriterService;
	}

	public RechargeCopywriterService getRechargeCopywriterService() {
		return rechargeCopywriterService;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	public Integer getIsValid() {
		return isValid;
	}

	public void setDocPositionList(List<DocPosition> docPositionList) {
		this.docPositionList = docPositionList;
	}

	public List<DocPosition> getDocPositionList() {
		return docPositionList;
	}

	public void setRechargeCopywriterList(List<RechargeCopywriter> rechargeCopywriterList) {
		this.rechargeCopywriterList = rechargeCopywriterList;
	}

	public List<RechargeCopywriter> getRechargeCopywriterList() {
		return rechargeCopywriterList;
	}

	public void setRechargeCopywriter(RechargeCopywriter rechargeCopywriter) {
		this.rechargeCopywriter = rechargeCopywriter;
	}

	public RechargeCopywriter getRechargeCopywriter() {
		return rechargeCopywriter;
	}

	public void setDocPlace(Integer docPlace) {
		this.docPlace = docPlace;
	}

	public Integer getDocPlace() {
		return docPlace;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}
}
