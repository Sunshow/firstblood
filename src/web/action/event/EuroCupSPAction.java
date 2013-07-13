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
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.event.EuroCupService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.event.EuroCupTeam;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class EuroCupSPAction extends BaseAction {

	private static final long serialVersionUID = 8625207108649371005L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private EuroCupService euroCupService;
	
	private EuroCupTeam euroCupTeam;
	private List<EuroCupTeam> euroCupTeams;
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("进入获取欧洲杯球队信息开始");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		try{
			map = euroCupService.findTeamList(super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取欧洲杯球队信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("获取欧洲杯球队信息，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}		
		if (map != null) {
			euroCupTeams = (List<EuroCupTeam>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("进入获取欧洲杯球队信息结束");
		return "teamList";
	}
	
	public String manage(){
		logger.info("更新球队SP信息开始");
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		User user = userSessionBean.getUser();
		for(EuroCupTeam euroCupTeam : euroCupTeams){
			euroCupTeam.setUserId(user.getId());
		}
		ResultBean resultBean = euroCupService.batchUpdateTeamSp(euroCupTeams);
		if(resultBean.isResult()){
			logger.info("更新球队SP信息结束");
			super.setForwardUrl("/event/euroCupSP.do");
			return "success";
		}else{
			super.setErrorMessage(resultBean.getMessage());
			logger.info("更新球队SP信息结束");
			return "failure";
		}
	}

	public EuroCupService getEuroCupService() {
		return euroCupService;
	}

	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}

	public List<EuroCupTeam> getEuroCupTeams() {
		return euroCupTeams;
	}

	public void setEuroCupTeams(List<EuroCupTeam> euroCupTeams) {
		this.euroCupTeams = euroCupTeams;
	}

	public EuroCupTeam getEuroCupTeam() {
		return euroCupTeam;
	}

	public void setEuroCupTeam(EuroCupTeam euroCupTeam) {
		this.euroCupTeam = euroCupTeam;
	}
}
