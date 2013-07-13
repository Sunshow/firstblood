package web.action.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.lehecai.core.lottery.cache.ComboLottery;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.action.lottery.ChaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.ExecuteComboService;
import com.lehecai.admin.web.service.lottery.ComboOrderService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.lottery.ComboOrderDetailWait;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.FinishComboStatus;
import com.lehecai.core.lottery.LotteryType;

public class ExecuteComboAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(ChaseAction.class);
	
	private ComboOrderService comboOrderService;
	private ExecuteComboService executeComboService;
	private Integer lotteryTypeValue;
	private Long comboOrderId;
	private Long comboId;
	private Long uid;
	private List<ComboOrderDetailWait> comboDetails;
	private String ids;
	private String phase;
	public String handle(){
		return "list";
	}
	/**
	 * 查询等待执行套餐列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入查询等待执行套餐列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		LotteryType lt = lotteryTypeValue == null ? null : LotteryType.getItem(lotteryTypeValue);
//		FinishComboStatus finishComboStatus = finishComboStatusValue == null ? null : FinishComboStatus.getItem(finishComboStatusValue);
		Map<String, Object> map = null;
		//特殊的设置pagesize为20
		PageBean pageBeanTemp = new PageBean();
		pageBeanTemp.setPage(super.getPageBean().getPage());
		pageBeanTemp.setPageSize(20);
		super.setPageBean(pageBeanTemp);
		try {
			map = executeComboService.queryWaitExcuteComboOrderList(uid, comboId, comboOrderId, lt, phase, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询等待执行套餐列表，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){			
			comboDetails = (List<ComboOrderDetailWait>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询等待执行套餐列表结束");
		return "list";
	}
	
	/**
	 * 执行套餐
	 * @return
	 */
	public String executeCombo() {
		logger.info("进入执行套餐");
		if (ids == null || ids.length() == 0) {
			logger.error("执行套餐，编码为空");
			super.setErrorMessage("执行套餐，编码为空");
			return "failure";
		}
		LotteryType lt = null;
		if (lotteryTypeValue != null && lotteryTypeValue != 0) {
			lt = LotteryType.getItem(lotteryTypeValue);
		} else {
			logger.error("执行套餐，彩种为空");
			super.setErrorMessage("执行套餐，彩种为空");
			return "failure";
		}
		
		String[] idArray = ids.split(",");
		List<String> idList = new ArrayList<String>();
		if (idArray != null && idArray.length > 0) {
			for (int i = 0; i < idArray.length; i++) {
				idList.add(idArray[i]);
			}
		}
		
		String data = "";
		try {
			data = executeComboService.comboOrderExecute(idList, lt, phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("执行套餐，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		
		if (data != null && !data.equals("")) {
			JSONObject json = JSONObject.fromObject(data);
			String success = json.getString("success");
			if (success != null && !success.equals("")) {
				String[] successArray = success.split(",");
				success = "";
				for (int i = 0; i < successArray.length; i++) {
					success = success + successArray[i] + "\n";
				}
			}
			String fail = json.getString("fail");
			if (fail != null && !fail.equals("")) {
				String[] failArray = fail.split(",");
				fail = "";
				for (int i = 0; i < failArray.length; i++) {
					fail = fail + failArray[i] + "\n";
				}
			}
			super.setSuccessMessage("执行成功：" + success + "\n执行失败" + fail);
		} else {
			logger.error("执行套餐失败");
			super.setErrorMessage("执行套餐失败");
			return "failure";
		}
		
		super.setForwardUrl("/business/executeCombo.do");
		logger.info("执行套餐结束");
		return "success";
	}

	/**
	 * 获取所有彩票种类
	 * @return
	 */
	public List<LotteryType> getLotteryTypes(){
		List<LotteryType> list = new ArrayList<LotteryType>();
		list.add(LotteryType.ALL);
        list.addAll(ComboLottery.getList());
		return list;
	}
	public List<FinishComboStatus> getFinishComboStatuses(){
		return FinishComboStatus.getItems();
	}

	public ComboOrderService getComboOrderService() {
		return comboOrderService;
	}

	public void setComboOrderService(ComboOrderService comboOrderService) {
		this.comboOrderService = comboOrderService;
	}


	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public Long getComboOrderId() {
		return comboOrderId;
	}

	public void setComboOrderId(Long comboOrderId) {
		this.comboOrderId = comboOrderId;
	}

	public Long getComboId() {
		return comboId;
	}

	public void setComboId(Long comboId) {
		this.comboId = comboId;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public ExecuteComboService getExecuteComboService() {
		return executeComboService;
	}

	public void setExecuteComboService(ExecuteComboService executeComboService) {
		this.executeComboService = executeComboService;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public List<ComboOrderDetailWait> getComboDetails() {
		return comboDetails;
	}
	public void setComboDetails(List<ComboOrderDetailWait> comboDetails) {
		this.comboDetails = comboDetails;
	}


}
