/**
 * 
 */
package web.action.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.event.EuroCupService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 派送金币
 * @author chirowong
 *
 */
public class EuroCupPresentCoinAction extends BaseAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 696597673852750839L;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private EuroCupService euroCupService;
	private MemberService memberService;
	
	private Long userId; // 需要充值用户
	private Integer amount;//派送金额
	private String remark;//备注
	
	public String handle(){
		logger.info("进入欧洲杯派送金币开始");
		return "presentCoin";	
	}
	
	public String manage(){
		logger.info("更新欧洲杯派送金币开始");
		if(userId != null){
			Member member = new Member();
			try {
				member = memberService.get(userId);
			} catch (ApiRemoteCallFailedException e) {
				logger.info("获取用户信息失败，请联系管理员");
				super.setErrorMessage("获取用户信息失败，请联系管理员");
				super.setForwardUrl("/event/euroCupPresentCoin.do");
				return "failure";
			}
			if(member == null){
				logger.info("欧洲杯派送金币失败，失败原因：不存在该用户");
				super.setErrorMessage("欧洲杯派送金币失败，失败原因：不存在该用户");
				super.setForwardUrl("/event/euroCupPresentCoin.do");
				return "failure";
			}
		}else{
			logger.info("欧洲杯派送金币失败，失败原因：用户编码为空");
			super.setErrorMessage("欧洲杯派送金币失败，失败原因：用户编码为空");
			super.setForwardUrl("/event/euroCupPresentCoin.do");
			return "failure";
		}
		if(amount == null){
			logger.info("欧洲杯派送金币失败，失败原因：数量为空");
			super.setErrorMessage("欧洲杯派送金币失败，失败原因：数量为空");
			super.setForwardUrl("/event/euroCupPresentCoin.do");
			return "failure";
		}
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		User admin = userSessionBean.getUser();
		ResultBean resultBean = new ResultBean();
		try {
			resultBean = euroCupService.presentCoin(amount, admin.getId(), remark, userId);
		} catch (ApiRemoteCallFailedException e) {
			logger.info("欧洲杯派送金币失败，失败原因："+e.getMessage());
			super.setErrorMessage(e.getMessage());
			super.setForwardUrl("/event/euroCupPresentCoin.do");
			return "failure";
		}
		if(resultBean.isResult()){
			logger.info("欧洲杯派送金币成功");
			super.setForwardUrl("/event/euroCupPresentCoin.do");
			return "success";
		}else{
			logger.info("更新欧洲杯派送金币结束");
			super.setErrorMessage(resultBean.getMessage());
			super.setForwardUrl("/event/euroCupPresentCoin.do");
			return "failure";
		}
	}
	
	public EuroCupService getEuroCupService() {
		return euroCupService;
	}

	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	
}
