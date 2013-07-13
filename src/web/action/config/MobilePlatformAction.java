package web.action.config;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.config.MobilePlatformInfo;
import com.lehecai.admin.web.service.config.MobileSettingService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

public class MobilePlatformAction extends BaseAction {

    private static final long serialVersionUID = 29000882176118486L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private Map<String, String> mobilePlatformTypeMap;
    private Map<String, String> operatorsMap;
    private List<YesNoStatus> yesNoStatusList;
    private String mobilePlatformId;
    private Integer isValidValue;
    private YesNoStatus isValid;
    private MobilePlatformInfo mobilePlatformInfo;
    private MobileSettingService mobileSettingService;
    private String flag;
    private List<MobilePlatformInfo> mobilePlatformInfoList;
    private List<LotteryType> lotteryTypeList;
    private Integer[] lotteryTypeArray;
    
	public String handle() {
        logger.info("进入移动平台查询");
        mobilePlatformInfoList = mobileSettingService.mget();
        if (mobilePlatformInfoList == null) {
        	logger.info("没有查询到数据！");
			super.setErrorMessage("没有查询到移动平台！");
			super.setForwardUrl("/config/mobilePlatform.do");
			return "failure";
        }
        logger.info("移动平台查询结束！");
        return "list";
	}
	
	/**
	 * 添加和修改移动平台的基本信息
	 */
	public String input() {
		if (flag.equals("add")) {
			logger.info("进入添加移动平台的基本信息");
			mobilePlatformInfo = new MobilePlatformInfo();
		}
		yesNoStatusList = YesNoStatus.getItems();
		lotteryTypeList = getLotteryTypes();
		if (flag.equals("update")) {
			logger.info("进入修改移动平台的基本信息");
			try {
				if (mobilePlatformId == null) {
					logger.info("你修改的移动平台不存在！");
					super.setErrorMessage("你修改的移动平台不存在！");
					super.setForwardUrl("/config/mobilePlatform.do");
					return "failure";
				} else {
					logger.info("进入修改移动平台的基本信息页面！");
					mobilePlatformInfo = mobileSettingService.get(SettingConstant.APP_VERSION_LOTTERY_STOP_SELL, mobilePlatformId);
					mobilePlatformInfo.setMobilePlatformName(getMobilePlatformTypeMap().get(mobilePlatformId));
					isValidValue = mobilePlatformInfo.getStatus().getValue();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.info("没有查找到移动平台");
			}
		}
        return "input";
    }
	
	/**
	 * 保存添加和修改移动平台的基本信息
	 */
	public String manage() {
    	logger.info("进入保存移动平台的基本信息");
    	if (flag.equals("add")) {
    		try {
    			MobilePlatformInfo mobilePlatformInfoExsit = mobileSettingService.get(SettingConstant.APP_VERSION_LOTTERY_STOP_SELL, mobilePlatformInfo.getMobilePlatformId());
				if (mobilePlatformInfoExsit == null) {
					mobilePlatformInfo.setLotteryTypeArray(lotteryTypeArray);
					mobilePlatformInfo.setStatus(YesNoStatus.getItem(isValidValue));
					mobilePlatformInfo.setGroup(SettingConstant.APP_VERSION_LOTTERY_STOP_SELL);
					mobileSettingService.merge(mobilePlatformInfo);
					logger.info("移动平台的基本信息添加成功！");
					super.setSuccessMessage("移动平台的基本信息添加成功！");
				} else {
					logger.error("您添加的移动平台已经存在！");
					super.setErrorMessage("您添加的移动平台已经存在！返回请重新添加！");
					super.setForwardUrl("/config/mobilePlatform.do");
					return "failure";
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("添加出现错误！原因：" + e.getMessage());
				super.setForwardUrl("/config/mobilePlatform.do");
				return "failure";
			}
    	} else if(flag.equals("update")) {
			try {
				mobilePlatformInfo.setLotteryTypeArray(lotteryTypeArray);
				mobilePlatformInfo.setStatus(YesNoStatus.getItem(isValidValue));
				mobilePlatformInfo.setGroup(SettingConstant.APP_VERSION_LOTTERY_STOP_SELL);
				boolean flag = mobileSettingService.update(mobilePlatformInfo);
				if(flag) {
					logger.info("移动平台的基本信息修改成功！");
					super.setSuccessMessage("移动平台的基本信息修改成功！");
				} else {
					logger.info("您修改的移动平台的基本信息失败！");
					super.setSuccessMessage("您修改的移动平台的基本信息失败！");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("修改出现错误！原因：" + e.getMessage());
				super.setForwardUrl("/config/mobilePlatform.do");
				return "failure";
			}
    	} else {
    		logger.info("您修改的移动平台已经存在！");
			super.setErrorMessage("您修改的移动平台已经存在！");
			super.setForwardUrl("/config/mobilePlatform.do");
			return "failure";
    	}
		logger.info("保存移动平台的基本信息结束");
		super.setForwardUrl("/config/mobilePlatform.do");
		return "success";
    }
	
	public List<LotteryType> getLotteryTypes() {
		List<LotteryType> list = new ArrayList<LotteryType>();
		list.addAll(OnSaleLotteryList.get());
		return list;
	}
	
	public Map<String, String> getOperatorsMap() {
		operatorsMap = SettingConstant.getOperatorsMap();
		return operatorsMap;
	}
    public Map<String, String> getMobilePlatformTypeMap() {
    	mobilePlatformTypeMap = SettingConstant.getPlatformMap();
    	return mobilePlatformTypeMap;
	}

	public void setYesNoStatusList(List<YesNoStatus> yesNoStatusList) {
		this.yesNoStatusList = yesNoStatusList;
	}

	public List<YesNoStatus> getYesNoStatusList() {
		return yesNoStatusList;
	}

	public void setMobilePlatformInfo(MobilePlatformInfo mobilePlatformInfo) {
		this.mobilePlatformInfo = mobilePlatformInfo;
	}

	public MobilePlatformInfo getMobilePlatformInfo() {
		return mobilePlatformInfo;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setMobilePlatformInfoList(List<MobilePlatformInfo> mobilePlatformInfoList) {
		this.mobilePlatformInfoList = mobilePlatformInfoList;
	}

	public List<MobilePlatformInfo> getMobilePlatformInfoList() {
		return mobilePlatformInfoList;
	}

	public void setIsValidValue(Integer isValidValue) {
		this.isValidValue = isValidValue;
	}

	public Integer getIsValidValue() {
		return isValidValue;
	}

	public void setIsValid(YesNoStatus isValid) {
		this.isValid = isValid;
	}

	public YesNoStatus getIsValid() {
		return isValid;
	}

	public void setMobilePlatformId(String mobilePlatformId) {
		this.mobilePlatformId = mobilePlatformId;
	}

	public String getMobilePlatformId() {
		return mobilePlatformId;
	}

	public void setMobileSettingService(MobileSettingService mobileSettingService) {
		this.mobileSettingService = mobileSettingService;
	}

	public MobileSettingService getMobileSettingService() {
		return mobileSettingService;
	}

	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}

	public List<LotteryType> getLotteryTypeList() {
		return lotteryTypeList;
	}

	public Integer[] getLotteryTypeArray() {
		return lotteryTypeArray;
	}

	public void setLotteryTypeArray(Integer[] lotteryTypeArray) {
		this.lotteryTypeArray = lotteryTypeArray;
	}
}
