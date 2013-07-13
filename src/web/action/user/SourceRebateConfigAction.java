package web.action.user;

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
import com.lehecai.admin.web.service.user.SourceRebateConfigService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.SourceRebateConfig;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

/**
 * 分成配置Action
 * @author yanweijie
 *
 */
public class SourceRebateConfigAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(SourceRebateConfigAction.class);
	
	private SourceRebateConfigService sourceRebateConfigService;
	
	private SourceRebateConfig sourceRebateConfig;
	
	private List<SourceRebateConfig> sourceRebateConfigList;
	
	private int lotteryTypeId;
	
	private Integer isUpdate;	//0:添加  1:修改
	
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入查询分成比例");
		if (sourceRebateConfig == null || (sourceRebateConfig.getPartnerId() == null 
				|| sourceRebateConfig.getPartnerId() == 0) || (sourceRebateConfig.getSource() == null || sourceRebateConfig.getSource() == 0)) {
			logger.error("查询分成配置，所需的参数为空");
			super.setErrorMessage("查询分成配置，所需的参数为空");
			return "failure";
		}

		Map<String, Object> map = null;
		try {
			map = sourceRebateConfigService.findSourceRebateConfigList(sourceRebateConfig, super.getPageBean());//查询对应渠道对应来源的所有分成配置
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询所有分成配置，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){
			sourceRebateConfigList = (List<SourceRebateConfig>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			HttpServletRequest request = ServletActionContext.getRequest();
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		SourceRebateConfig tempSourceRebateConfig = null;
		try {
			tempSourceRebateConfig = sourceRebateConfigService.getSourceRebateConfig(sourceRebateConfig);//查询对应渠道对应来源默认比例的分成比例配置
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询默认比例的分成比例配置，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (tempSourceRebateConfig == null) {//如果默认比例没有对应的分成比例配置
			isUpdate = 0;//添加分成比例配置
		} else {//如果默认比例有对应的分成比例配置
			sourceRebateConfig.setRebate(tempSourceRebateConfig.getRebate());
			isUpdate = 1;//修改分成比例配置
		}
		logger.info("查询分成比例结束");
		return "inputForm";
	}
	
	/**
	 * 添加/修改分成配置
	 * @return
	 */
	public String manage(){
		logger.info("进入更新分成比例");
		if (sourceRebateConfig == null || (sourceRebateConfig.getPartnerId() == null || sourceRebateConfig.getPartnerId() ==0) 
				|| (sourceRebateConfig.getSource() == null || sourceRebateConfig.getSource() == 0L) 
				|| (sourceRebateConfig.getRebate() == 0L)) {
			logger.error("修改的分成配置，所需参数为空");
			super.setErrorMessage("修改分成配置，所需参数为空");
			return "failure";
		}
		
		boolean result = false;
		try {
			sourceRebateConfig.setLotteryType(LotteryType.getItem(lotteryTypeId));
			if (isUpdate == 1) {//修改分成配置
				result = sourceRebateConfigService.update(sourceRebateConfig);
			} else if (isUpdate == 0){//添加分成配置
				result = sourceRebateConfigService.add(sourceRebateConfig);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("更新分成配置，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (!result) {
			logger.error("更新分成配置失败");
			super.setErrorMessage("更新分成配置失败");
			return "failure";
		}
		super.setForwardUrl("/agent/sourceRebateConfig.do?sourceRebateConfig.partnerId=" + sourceRebateConfig.getPartnerId() + "&sourceRebateConfig.source=" + sourceRebateConfig.getSource());
		logger.info("更新分成比例结束");
		return "forward";
	}
	
	/**
	 * ajax异步调用取得对应分成比例
	 * @return
	 */
	public String getRebate () {
		logger.info("进入查询分成比例");
		if (sourceRebateConfig == null || (sourceRebateConfig.getPartnerId() == null || sourceRebateConfig.getPartnerId() ==0) 
				|| (sourceRebateConfig.getSource() == null || sourceRebateConfig.getSource() == 0L)) {
			logger.error("查询分成比例，所需参数为空");
			super.setErrorMessage("查询分成比例,所需参数为空");
			return "failure";
		}
		
		try {
			sourceRebateConfig.setLotteryType(LotteryType.getItem(lotteryTypeId));
			sourceRebateConfig = sourceRebateConfigService.getSourceRebateConfig(sourceRebateConfig);//查询分成配置
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询分成比例，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		JSONObject rs = new JSONObject();
		if (sourceRebateConfig != null) {
			rs.put("rebate", sourceRebateConfig.getRebate());
		}
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("查询分成比例结束");
		return null;
	}
	
	public SourceRebateConfigService getPartnerRebateConfigService() {
		return sourceRebateConfigService;
	}
	public void setPartnerRebateConfigService(
			SourceRebateConfigService sourceRebateConfigService) {
		this.sourceRebateConfigService = sourceRebateConfigService;
	}
	public SourceRebateConfigService getSourceRebateConfigService() {
		return sourceRebateConfigService;
	}
	public void setSourceRebateConfigService(
			SourceRebateConfigService sourceRebateConfigService) {
		this.sourceRebateConfigService = sourceRebateConfigService;
	}
	public SourceRebateConfig getSourceRebateConfig() {
		return sourceRebateConfig;
	}
	public void setSourceRebateConfig(SourceRebateConfig sourceRebateConfig) {
		this.sourceRebateConfig = sourceRebateConfig;
	}
	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.get();
	}
	public List<SourceRebateConfig> getSourceRebateConfigList() {
		return sourceRebateConfigList;
	}
	public void setSourceRebateConfigList(
			List<SourceRebateConfig> sourceRebateConfigList) {
		this.sourceRebateConfigList = sourceRebateConfigList;
	}
	public int getLotteryTypeId() {
		return lotteryTypeId;
	}
	public void setLotteryTypeId(int lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
	public Integer getIsUpdate() {
		return isUpdate;
	}
	public void setIsUpdate(Integer isUpdate) {
		this.isUpdate = isUpdate;
	}
}
