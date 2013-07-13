package web.action.config;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.ConfigParserMapping;
import com.lehecai.core.config.impl.lottery.LotteryStopSellConfigItem;
import com.lehecai.core.config.impl.lottery.LotteryStopSellConfigParser;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlanType;
import com.lehecai.core.lottery.PlatformType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.service.config.ConfigService;
import net.sf.json.JSONObject;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 全局彩种是否停售配置
 * @author yanweijie
 *
 */
public class LotteryStopSellConfigAction extends BaseAction {
	private static final long serialVersionUID = -2225123800707608839L;

	private ConfigService configService;
	
	private Integer lotteryTypeId;
	private LotteryType lotteryType;

    private String configDataStr;

	private LotteryStopSellConfigItem lotteryStopSell;
	
	private Map<Integer,String> planTypesMap;
	private Map<Integer,String> usePlanTypeMap;//按方案停售
	private Map<String,String> creditLevelMap;//是否按彩贝等级停售
	private Map<String,String> minLevelMap;//最低彩贝等级
	
	private Integer originLotteryTypeId;//源彩种
	private Integer goalLotteryTypeId;//目标彩种
	private Integer originPlatformTypeId;//源平台
	private Integer goalPlatformTypeId;//目标平台
	private boolean copyAllConfig;//是否复制完整方案

	private static String getLotteryStopSellConfigGroup() {
		return SettingConstant.GROUP_LOTTERY_STOP_SELL_CONFIG;
	}
	
	private static LotteryStopSellConfigParser getLotteryStopSellConfigParser() {
		return (LotteryStopSellConfigParser) ConfigParserMapping.getByGroup(getLotteryStopSellConfigGroup());
	}
	
	public String copyConfig(){
		if (originPlatformTypeId == null) {
			copyAllConfig = true;
		} else {
			goalLotteryTypeId = originLotteryTypeId;
		}
		return "copyConfig";
	}
	
	public String saveCopyConfig(){
		if (originLotteryTypeId == null) {
			logger.error("源彩种不能为空");
			super.setErrorMessage("源彩种不能为空");
			return "failure";
		}
		LotteryType originLotteryType = LotteryType.getItem(originLotteryTypeId);
		if (originLotteryType == null) {
			logger.error("源彩种编码非法, lotteryTypeId={}", originLotteryTypeId);
			super.setErrorMessage("源彩种编码非法");
			return "failure";
		}

		if (goalLotteryTypeId == null) {
			logger.error("目标彩种不能为空");
			super.setErrorMessage("目标彩种不能为空");
			return "failure";
		}
		LotteryType goalLotteryType = LotteryType.getItem(goalLotteryTypeId);
		if (goalLotteryType == null) {
			logger.error("目标彩种编码非法, lotteryTypeId={}", goalLotteryTypeId);
			super.setErrorMessage("目标彩种编码非法");
			return "failure";
		}
		
		if (!copyAllConfig) {
			if (originPlatformTypeId == null) {
				logger.error("源平台不能为空");
				super.setErrorMessage("源平台不能为空");
				return "failure";
			}

			if (goalPlatformTypeId == null) {
				logger.error("目标平台不能为空");
				super.setErrorMessage("目标平台不能为空");
				return "failure";
			}
		}
		
		LotteryStopSellConfigParser parser = getLotteryStopSellConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getLotteryStopSellConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getLotteryStopSellConfigGroup());
			return "failure";
		}
		
		// 获取原始数据
		LotteryStopSellConfigItem originLotteryStopSell = null;
		try {
			if (originLotteryTypeId.intValue() == LotteryType.ALL.getValue()) {
				originLotteryStopSell = configService.getConfigItem(getLotteryStopSellConfigGroup(), parser.getDefaultItemKey(), LotteryStopSellConfigItem.class);
			} else {
				originLotteryStopSell = configService.getConfigItem(getLotteryStopSellConfigGroup(), parser.getLotteryTypeItemKey(originLotteryType), LotteryStopSellConfigItem.class);
			}
		} catch (Exception e) {
			logger.error("读取源配置出错", e);
			super.setErrorMessage("读取源配置出错, " + e.getMessage());
			return "failure";
		}

		if (originLotteryStopSell == null) {
			logger.error("读取源配置为空");
			super.setErrorMessage("读取源配置为空");
			return "failure";
		}

		if (!copyAllConfig) {
			//复制单个平台
			PlatformType originPlatformType = PlatformType.getItem(originPlatformTypeId);
			PlatformType goalPlatformType = PlatformType.getItem(goalPlatformTypeId);
			List<PlatformType> platformTypesList = originLotteryStopSell.getPlatformTypes();
			if (platformTypesList == null || !platformTypesList.contains(originPlatformType)) {
				logger.error("复制配置失败，源配置信息不存在");
				super.setErrorMessage("复制配置失败，源配置信息不存在");
				return "failure";
			}
			if (!platformTypesList.contains(goalPlatformType)) {
				platformTypesList.add(goalPlatformType);
			}
			Map<PlatformType,YesNoStatus> usePlanTypeMap = originLotteryStopSell.getUsePlanTypeMap();
			if (usePlanTypeMap.get(originPlatformType) != null) {
				usePlanTypeMap.put(goalPlatformType, usePlanTypeMap.get(originPlatformType));
			} else {
				usePlanTypeMap.remove(goalPlatformType);
			}
			Map<PlatformType,List<PlanType>> planTypesMap = originLotteryStopSell.getPlanTypesMap();
			if (planTypesMap.get(originPlatformType) != null) {
				planTypesMap.put(goalPlatformType, planTypesMap.get(originPlatformType));
			} else {
				planTypesMap.remove(goalPlatformType);
			}
			Map<PlatformType,Map<PlanType,YesNoStatus>> creditLevelMap = originLotteryStopSell.getCreditLevelMap();
			if (creditLevelMap.get(originPlatformType) != null) {
				creditLevelMap.put(goalPlatformType, creditLevelMap.get(originPlatformType));
			} else {
				creditLevelMap.remove(goalPlatformType);
			}
			Map<PlatformType,Map<PlanType,Integer>> minLevelMap = originLotteryStopSell.getMinLevelMap();
			if (minLevelMap.get(originPlatformType) != null) {
				minLevelMap.put(goalPlatformType, minLevelMap.get(originPlatformType));
			} else {
				minLevelMap.remove(goalPlatformType);
			}
		}
		boolean updateResult = false;
		try {
			String item = null;
			if (goalLotteryType.getValue() == LotteryType.ALL.getValue()) {
				item = parser.getDefaultItemKey();
			} else {
				item = parser.getLotteryTypeItemKey(goalLotteryType);
			}

			// 更新配置
			updateResult = configService.updateConfigItem(originLotteryStopSell, getLotteryStopSellConfigGroup(), item);
		} catch (Exception e) {
			logger.error("更新全局彩种是否停售配置失败，{}", e);
			super.setErrorMessage("更新全局彩种是否停售配置失败，" + e);
			return "failure";
		}
		
		if (updateResult) {
			logger.info("更新全局彩种是否停售配置成功");
			super.setSuccessMessage("更新全局彩种是否停售配置成功");
			super.setForwardUrl("/config/lotteryStopSellConfig.do");
			return "success";
		} else {
			logger.error("更新全局彩种是否停售配置失败");
			super.setErrorMessage("更新全局彩种是否停售配置失败");
			return "failure";
		}
	}

    public String ajaxGetConfigItem() {
        HttpServletResponse response = ServletActionContext.getResponse();
        JSONObject rs = new JSONObject();

        LotteryStopSellConfigParser parser = getLotteryStopSellConfigParser();
        if (parser == null) {
            logger.error("未配置对应的解析器, {}", getLotteryStopSellConfigGroup());

            rs.put("code", -1);
            rs.put("message", "未配置对应的解析器");
            writeRs(response,rs);
            return null;
        }

        if (lotteryTypeId != null) {
            lotteryType = LotteryType.getItem(lotteryTypeId);
        }
        try {
            if (lotteryType != null) {
                lotteryStopSell = configService.getConfigItem(getLotteryStopSellConfigGroup(), parser.getLotteryTypeItemKey(lotteryType), LotteryStopSellConfigItem.class);
            } else {
                lotteryStopSell = configService.getConfigItem(getLotteryStopSellConfigGroup(), parser.getDefaultItemKey(), LotteryStopSellConfigItem.class);	//全局彩种是否停售配置
            }
        } catch (Exception e) {
            logger.error("得到全局彩种是否停售配置异常，{}", e);
            rs.put("code", 2);
            rs.put("message", "得到全局彩种是否停售配置异常, " + e.getMessage());
            writeRs(response, rs);
            return null;
        }

        if (lotteryStopSell == null) {
            rs.put("code", 1);
            rs.put("message", "得到全局彩种是否停售配置为空, lotteryType=" + lotteryType);
            writeRs(response,rs);
            return null;
        }

        rs.put("code", 0);
        rs.put("data", lotteryStopSell.toJSONObject());
        rs.put("message", "成功");
        writeRs(response, rs);

        return null;
    }
	
	public String handle() {
		logger.info("进入全局彩种是否停售配置彩种列表");
		
		return "list";
	}

	/**
	 * 更新全局彩种是否停售默认及彩种配置
	 * @return
	 */
	public String update() {
		logger.info("进入更新全局彩种是否停售默认及彩种配置");

        HttpServletResponse response = ServletActionContext.getResponse();
        JSONObject rs = new JSONObject();

        LotteryStopSellConfigParser parser = getLotteryStopSellConfigParser();
        if (parser == null) {
            logger.error("未配置对应的解析器, {}", getLotteryStopSellConfigGroup());

            rs.put("code", -1);
            rs.put("message", "未配置对应的解析器");
            writeRs(response,rs);
            return null;
        }

		if (lotteryTypeId == null || lotteryTypeId == 0) {
			logger.error("彩种编码为空");

            rs.put("code", 1);
            rs.put("message", "彩种编码为空");
            writeRs(response,rs);
            return null;
		}
		
		lotteryType = LotteryType.getItem(lotteryTypeId);
		if (lotteryType == null) {
			logger.error("彩种编码非法, lotteryTypeId={}", lotteryTypeId);

            rs.put("code", 2);
            rs.put("message", "彩种编码非法");
            writeRs(response,rs);
            return null;
		}

        JSONObject lotteryStopSellJSONObject;
        try {
            lotteryStopSellJSONObject = JSONObject.fromObject(configDataStr);
        } catch (Exception e) {
            logger.error("数据格式转换错误, data={}", configDataStr);

            rs.put("code", 3);
            rs.put("message", "数据格式转换错误");
            writeRs(response,rs);
            return null;
        }

        lotteryStopSell = LotteryStopSellConfigItem.convertFromJSONObject(lotteryStopSellJSONObject);
        if (lotteryStopSell == null) {
            logger.error("格式转换后数据为空, data={}", configDataStr);

            rs.put("code", 4);
            rs.put("message", "格式转换后数据为空");
            writeRs(response,rs);
            return null;
        }

		boolean updateResult = false;
		try {
			String item = null;
			if (lotteryType.getValue() == LotteryType.ALL.getValue()) {
				item = parser.getDefaultItemKey();
			} else {
				item = parser.getLotteryTypeItemKey(lotteryType);
			}

			// 更新配置
			updateResult = configService.updateConfigItem(lotteryStopSell, getLotteryStopSellConfigGroup(), item);

            rs.put("code", 0);
            rs.put("message", "成功");
            writeRs(response, rs);
		} catch (Exception e) {
			logger.error("更新全局彩种是否停售配置出错，{}", e);
            rs.put("code", 5);
            rs.put("message", "更新全局彩种是否停售配置失败: " + e.getMessage());
            writeRs(response, rs);
		}

		if (updateResult) {
            rs.put("code", 0);
            rs.put("message", "成功");
            writeRs(response, rs);
		} else {
			logger.error("更新全局彩种是否停售配置失败");

            rs.put("code", 6);
            rs.put("message", "更新全局彩种是否停售配置失败");
            writeRs(response, rs);
		}

        return null;
	}

	public List<YesNoStatus> getYesNoStatusItems() {
		return YesNoStatus.getItems();
	}
	
	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.getForQuery();
	}

	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}

	public LotteryStopSellConfigItem getLotteryStopSell() {
		return lotteryStopSell;
	}

	public void setLotteryStopSell(LotteryStopSellConfigItem lotteryStopSell) {
		this.lotteryStopSell = lotteryStopSell;
	}

	public List<PlatformType> getPlatformTypeList() {
		return PlatformType.getSelectItems();
	}
	
	public YesNoStatus getYesStatus() {
		return YesNoStatus.YES;
	}

	public List<PlanType> getPlanTypeList() {
		return PlanType.getSelectItems();
	}

	public Map<Integer, String> getPlanTypesMap() {
		return planTypesMap;
	}

	public void setPlanTypesMap(Map<Integer, String> planTypesMap) {
		this.planTypesMap = planTypesMap;
	}

	public Map<Integer, String> getUsePlanTypeMap() {
		return usePlanTypeMap;
	}

	public void setUsePlanTypeMap(Map<Integer, String> usePlanTypeMap) {
		this.usePlanTypeMap = usePlanTypeMap;
	}

	public Map<String, String> getCreditLevelMap() {
		return creditLevelMap;
	}

	public void setCreditLevelMap(Map<String, String> creditLevelMap) {
		this.creditLevelMap = creditLevelMap;
	}

	public Map<String, String> getMinLevelMap() {
		return minLevelMap;
	}

	public void setMinLevelMap(Map<String, String> minLevelMap) {
		this.minLevelMap = minLevelMap;
	}

	public Integer getOriginLotteryTypeId() {
		return originLotteryTypeId;
	}

	public void setOriginLotteryTypeId(Integer originLotteryTypeId) {
		this.originLotteryTypeId = originLotteryTypeId;
	}

	public Integer getGoalLotteryTypeId() {
		return goalLotteryTypeId;
	}

	public void setGoalLotteryTypeId(Integer goalLotteryTypeId) {
		this.goalLotteryTypeId = goalLotteryTypeId;
	}

	public Integer getOriginPlatformTypeId() {
		return originPlatformTypeId;
	}

	public void setOriginPlatformTypeId(Integer originPlatformTypeId) {
		this.originPlatformTypeId = originPlatformTypeId;
	}

	public Integer getGoalPlatformTypeId() {
		return goalPlatformTypeId;
	}

	public void setGoalPlatformTypeId(Integer goalPlatformTypeId) {
		this.goalPlatformTypeId = goalPlatformTypeId;
	}

	public boolean isCopyAllConfig() {
		return copyAllConfig;
	}

	public void setCopyAllConfig(boolean copyAllConfig) {
		this.copyAllConfig = copyAllConfig;
	}

    public String getConfigDataStr() {
        return configDataStr;
    }

    public void setConfigDataStr(String configDataStr) {
        this.configDataStr = configDataStr;
    }
}
