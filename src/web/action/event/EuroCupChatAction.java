/**
 * 
 */
package web.action.event;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.EuroCupService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.event.EuroCupChat;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class EuroCupChatAction extends BaseAction {

	private static final long serialVersionUID = 8625207108649371005L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private EuroCupService euroCupService;
	private MemberService memberService;
	
	private EuroCupChat euroCupChat;
	private List<EuroCupChat> euroCupChats;
	
	private Long userId;
	private String userName;
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("进入获取欧洲杯禁言用户列表开始");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		try{
			map = euroCupService.chatList(userId, userName, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取欧洲杯禁言用户列表，api调用异常，{}", e.getMessage());
			super.setErrorMessage("获取欧洲杯禁言用户列表，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}		
		if (map != null) {
			euroCupChats = (List<EuroCupChat>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("进入欧洲杯禁言用户列表结束");
		return "chatList";
	}
	
	public String chatAdd(){
		logger.info("增加欧洲杯禁言用户开始");
		return "input";
	}
	
	public String chatManage(){
		logger.info("增加欧洲杯禁言用户开始");
		if(userName == null){
			logger.info("增加欧洲杯禁言用户错误，用户名不能为空");
			super.setErrorMessage("增加欧洲杯禁言用户错误，用户名不能为空");
			return "failure";
		}
		Member member = null;
		try {
			member = memberService.get(userName);
		} catch (ApiRemoteCallFailedException e1) {
			logger.info("增加欧洲杯禁言用户错误，获取用户信息错误");
			super.setErrorMessage("增加欧洲杯禁言用户错误，获取用户信息错误");
			return "failure";
		}
		if(member == null){
			logger.info("增加欧洲杯禁言用户错误，用户不存在");
			super.setErrorMessage("增加欧洲杯禁言用户错误，用户不存在");
			return "failure";
		}
		ResultBean resultBean = new ResultBean();
		try {
			resultBean = euroCupService.chatAdd(member.getUid());
		} catch (ApiRemoteCallFailedException e) {
			logger.info("增加欧洲杯禁言用户结束");
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		if(resultBean.isResult()){
			logger.info("增加欧洲杯禁言用户结束");
			super.setForwardUrl("/event/euroCupChat.do");
			return "success";
		}else{
			super.setErrorMessage(resultBean.getMessage());
			logger.info("增加欧洲杯禁言用户结束");
			super.setForwardUrl("/event/euroCupChat.do");
			return "failure";
		}
	}
	
	public String chatDelete(){
		logger.info("删除欧洲杯禁言用户开始");
		ResultBean resultBean = new ResultBean();
		try {
			resultBean = euroCupService.chatDelete(userId);
		} catch (ApiRemoteCallFailedException e) {
			logger.info("删除欧洲杯禁言用户结束");
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		if(resultBean.isResult()){
			logger.info("删除欧洲杯禁言用户结束");
			super.setForwardUrl("/event/euroCupChat.do");
			return "success";
		}else{
			super.setErrorMessage(resultBean.getMessage());
			logger.info("删除欧洲杯禁言用户结束");
			return "failure";
		}
	}
	
	public EuroCupService getEuroCupService() {
		return euroCupService;
	}

	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}

	public EuroCupChat getEuroCupChat() {
		return euroCupChat;
	}

	public void setEuroCupChat(EuroCupChat euroCupChat) {
		this.euroCupChat = euroCupChat;
	}

	public List<EuroCupChat> getEuroCupChats() {
		return euroCupChats;
	}

	public void setEuroCupChats(List<EuroCupChat> euroCupChats) {
		this.euroCupChats = euroCupChats;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
}
