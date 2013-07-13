package web.action.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lehecai.core.lottery.cache.ComboLottery;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.ComboService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.lottery.Combo;
import com.lehecai.core.api.lottery.ComboConfig;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.AdditionType;
import com.lehecai.core.lottery.ComboSaleStatus;
import com.lehecai.core.lottery.ComboType;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlayType;
import com.opensymphony.xwork2.Action;

public class ComboAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

	private Long comboId;
	private List<Combo> comboList;
	private ComboService comboService;
	private Combo combo;
	private Integer lotteryTypeValue;
	private List<PlayType> playTypes;
	private Integer comboStatusValue;

	public String handle() {
		logger.info("进入查询套餐列表");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询套餐列表");

		HttpServletRequest request = ServletActionContext.getRequest();

		Map<String, Object> map = null;
		try {
			map = comboService.queryComboList(comboId, null, null, super
					.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询套餐列表,api调用异常" + e.getMessage());
			super.setErrorMessage("查询套餐列表,api调用异常" + e.getMessage());
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API查询套餐列表为空");
			super.setErrorMessage("API查询套餐列表为空");
			return "failure";
		}
		comboList = (List<Combo>) map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));

		logger.info("查询套餐列表结束");
		return "list";
	}

	public String manage() {
		logger.info("进入更新彩票套餐");
		if (combo != null) {
			if (comboStatusValue == null || comboStatusValue == ComboSaleStatus.ALL.getValue()) {
				logger.error("套餐状态不能为空或者全部");
				super.setErrorMessage("套餐状态不能为空或者全部");
				return "failure";
			}
			ComboSaleStatus css = comboStatusValue == null ? null
					: ComboSaleStatus.getItem(comboStatusValue);
			combo.setStatus(css);
			if (combo.getComboConfigList() != null
					&& combo.getComboConfigList().size() != 0) {
				for (int i = 0; i < combo.getComboConfigList().size(); i++) {
					ComboConfig cc = combo.getComboConfigList().get(i);
					if (cc.getComboTypeValues() != null
							&& cc.getComboTypeValues().size() > 0) {
						List<ComboType> comboTypeList = new ArrayList<ComboType>();
						for (int j = 0; j < cc.getComboTypeValues().size(); j++) {
							comboTypeList.add(ComboType.getItem(cc
									.getComboTypeValues().get(j)));
						}
						cc.setComboTypes(comboTypeList);
					}
					cc.setLotteryType(LotteryType.getItem(cc
							.getLotteryTypeValue()));
					cc.setPlayType(PlayType.getItem(cc.getPlayTypeValue()));
				}
				combo.setConfig(ComboConfig.toJSONObject(
						combo.getComboConfigList()).toString());
			}
			if (combo.getComboId() != null && !"".equals(combo.getComboId())) {
				try {
					comboService.updateCombo(combo);
				} catch (ApiRemoteCallFailedException e) {
					logger.error("更新彩票套餐,api调用异常" + e.getMessage());
					super.setErrorMessage("更新彩票套餐,api调用异常" + e.getMessage());
					return "failure";
				}
			} else {
				try {
					comboService.addCombo(combo);
				} catch (ApiRemoteCallFailedException e) {
					logger.error("添加彩票套餐,api调用异常" + e.getMessage());
					super.setErrorMessage("添加彩票套餐,api调用异常" + e.getMessage());
					return "failure";
				}
			}
		}
		super.setForwardUrl("/business/combo.do");
		return "success";
	}

	@SuppressWarnings("unchecked")
	public String input() {
		if (comboId != null && comboId != 0) {
			Map<String, Object> map = null;
			try {
				map = comboService.queryComboList(comboId, null, null, super
						.getPageBean());
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询套餐列表,api调用异常" + e.getMessage());
				super.setErrorMessage("查询套餐列表,api调用异常" + e.getMessage());
				return "failure";
			}
			if (map == null || map.size() == 0) {
				logger.error("API查询套餐列表为空");
				super.setErrorMessage("API查询套餐列表为空");
				return "failure";
			}
			comboList = (List<Combo>) map.get(Global.API_MAP_KEY_LIST);
			if (comboList != null && comboList.size() > 0) {
				combo = comboList.get(0);
			}
		} else {
			playTypes = new ArrayList<PlayType>();
			playTypes.add(PlayType.ALL);
		}
		return "inputForm";
	}

	public String getPlayTypeByLotteryType() {
		logger.info("进入根据彩种信息查询玩法信息");
		int rc = 0;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (lotteryTypeValue == null || lotteryTypeValue == 0) {
			logger.error("查询玩法信息异常，彩种ID为空 ");
			rc = 1;
			msg = "查询玩法信息异常，彩种ID为空  ";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}

		LotteryType lotteryType = lotteryTypeValue == null ? null : LotteryType
				.getItem(lotteryTypeValue);
		playTypes = PlayType.getItemsByLotteryType(lotteryType);

		JSONArray jsonArray = new JSONArray();
		for (PlayType p : playTypes) {
			JSONObject j = new JSONObject();
			j.put("key", p.getValue());
			j.put("name", p.getName());
			jsonArray.add(j);
		}
		json.put("code", rc);
		json.put("msg", msg);
		json.put("data", jsonArray.toString());
		writeRs(response, json);
		return Action.NONE;
	}

	public Long getComboId() {
		return comboId;
	}

	public void setComboId(Long comboId) {
		this.comboId = comboId;
	}

	public List<Combo> getComboList() {
		return comboList;
	}

	public void setComboList(List<Combo> comboList) {
		this.comboList = comboList;
	}

	public ComboService getComboService() {
		return comboService;
	}

	public void setComboService(ComboService comboService) {
		this.comboService = comboService;
	}

	public Combo getCombo() {
		return combo;
	}

	public void setCombo(Combo combo) {
		this.combo = combo;
	}

	public List<LotteryType> getLotteryTypes() {
        return ComboLottery.getList();
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public List<PlayType> getPlayTypes() {
		return playTypes;
	}

	public void setPlayTypes(List<PlayType> playTypes) {
		this.playTypes = playTypes;
	}

	public List<ComboType> getComboCheckTypes() {
		return ComboType.getCheckItems();
	}

	public List<ComboSaleStatus> getComboSaleStatuses() {
		return ComboSaleStatus.getItems();
	}

	public Integer getComboStatusValue() {
		return comboStatusValue;
	}

	public void setComboStatusValue(Integer comboStatusValue) {
		this.comboStatusValue = comboStatusValue;
	}

	public List<AdditionType> getAdditionTypes() {
		return AdditionType.getItems();
	}
}
