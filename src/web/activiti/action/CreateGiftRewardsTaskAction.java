/**
 * 
 */
package web.activiti.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.form.GiftRewardsTaskForm;
import com.lehecai.admin.web.activiti.task.giftrewards.StartGiftRewardsTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.customconfig.CustomFunctionConfig;
import com.lehecai.admin.web.domain.customconfig.FunctionType;
import com.lehecai.admin.web.service.customconfig.CustomFunctionConfigService;
import com.lehecai.admin.web.service.event.EventService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.event.EventInfoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WalletType;

/**
 * @author chirowong
 *
 */
public class CreateGiftRewardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private StartGiftRewardsTask startGiftRewardsTask;
	@Autowired
	private MemberService memberService;
	@Autowired
	private EventService eventService;
	@Autowired
	private CustomFunctionConfigService customFunctionConfigService;
	
	private GiftRewardsTaskForm giftRewardsTaskForm;
	private List<EventInfo> eventInfoList;
	private Integer walletTypeValue;
	
	public String handle() {
		logger.info("创建彩金赠送工单");
		return "inputForm";
	}
	
	public String manage() {
		logger.info("提交彩金赠送工单，启动彩金赠送工作流程");
		if (giftRewardsTaskForm == null) {
			logger.error("提交彩金赠送工单失败，原因：GiftRewardsTaskForm为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：GiftRewardsTaskForm为空");
			return "inputForm";
		}
		if (StringUtils.isEmpty(giftRewardsTaskForm.getGiftRewardsTask().getUsername())) {
			logger.error("提交彩金赠送工单失败，原因：赠予用户名username为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：赠予用户名username为空");
			return "inputForm";
		}
		if (giftRewardsTaskForm.getGiftRewardsTask().getAmount() == null || giftRewardsTaskForm.getGiftRewardsTask().getAmount() <= 0.00D) {
			logger.error("提交彩金赠送工单失败，原因：赠予金额amount为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：赠予金额amount为空");
			return "inputForm";
		}
		if (StringUtils.isEmpty(giftRewardsTaskForm.getGiftRewardsTask().getReason())) {
			logger.error("提交彩金赠送工单失败，原因：赠予原因reason为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：赠予原因reason为空");
			return "inputForm";
		}
		Member member = null;
		try {
			member = memberService.get(giftRewardsTaskForm.getGiftRewardsTask().getUsername());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		if (member == null) {
			logger.error("提交彩金赠送工单失败，原因：[{}]用户名不存在", giftRewardsTaskForm.getGiftRewardsTask().getUsername());
			super.setErrorMessage("提交彩金赠送工单失败，原因：[" + giftRewardsTaskForm.getGiftRewardsTask().getUsername() + "]用户名不存在");
			return "inputForm";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		giftRewardsTaskForm.getGiftRewardsTask().setInitiator(userSessionBean.getUser().getUserName());
		if(walletTypeValue == null){
			walletTypeValue = WalletType.GIFT.getValue();
		}
		giftRewardsTaskForm.getGiftRewardsTask().setWalletType(walletTypeValue);
		Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("giftRewardsTaskForm", giftRewardsTaskForm);
		
	    startGiftRewardsTask.start(variables);
	    
	    super.setSuccessMessage("创建彩金赠送工单成功！");
		return "success";
	}

	public GiftRewardsTaskForm getGiftRewardsTaskForm() {
		return giftRewardsTaskForm;
	}

	public void setGiftRewardsTaskForm(GiftRewardsTaskForm giftRewardsTaskForm) {
		this.giftRewardsTaskForm = giftRewardsTaskForm;
	}

	@SuppressWarnings("unchecked")
	public List<EventInfo> getEventInfoList() {
		Map<String, Object> map = null;
		try{
			map = eventService.findEventInfoListByCondition(null, null, null, 
					null, null, null, EventInfoStatus.OPEN, super.getPageBean());//条件查询所有活动
		}catch(ApiRemoteCallFailedException e){
			logger.error("查询抽奖活动，api调用异常，{}", e.getMessage());
		}
		if (map != null) {
			eventInfoList = (List<EventInfo>)map.get(Global.API_MAP_KEY_LIST);
		}
		return eventInfoList;
	}

	public void setEventInfoList(List<EventInfo> eventInfoList) {
		this.eventInfoList = eventInfoList;
	}
	
	public List<WalletType> getWalletTypes(){
		List<WalletType> walletTypes = new ArrayList<WalletType>();
		walletTypes.add(WalletType.GIFT);
		walletTypes.add(WalletType.CASH);
		return walletTypes;
	}
	/**
	 * 是否可选择现金钱包/彩金钱包
	 * @return
	 */
	public boolean getAdminAuthority(){
		FunctionType functionType = FunctionType.GIFTREWARDSCHECK;
		CustomFunctionConfig config = new CustomFunctionConfig();
		config.setFunctionType(functionType);
		List<CustomFunctionConfig> customFunctionConfigList = customFunctionConfigService.list(config, null);
		if(customFunctionConfigList != null && customFunctionConfigList.size() > 0){
			config = customFunctionConfigList.get(0);
			HttpServletRequest request = ServletActionContext.getRequest();
			UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
			String roles = config.getRoles();
			if(roles != null && !"".equals(roles)){
				String[] arrRole = roles.split(",");
				for(String roleId : arrRole){
					if(userSessionBean.getRole().getId().longValue() == Long.parseLong(roleId.trim())){
						return true;
					}
				}
			}
		}
		return false;
	}

	public Integer getWalletTypeValue() {
		return walletTypeValue;
	}

	public void setWalletTypeValue(Integer walletTypeValue) {
		this.walletTypeValue = walletTypeValue;
	}
}
