/**
 * 
 */
package web.action.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

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
import com.lehecai.admin.web.service.lottery.DcRaceService;
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.event.EuroCupMatch;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;

/**
 * @author chirowong
 *
 */
public class EuroCupMatchSPAction extends BaseAction {

	private static final long serialVersionUID = 8625207108649371005L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private HttpServletResponse response;
	
	private EuroCupService euroCupService;
	
	private EuroCupMatch euroCupMatch;
	private List<EuroCupMatch> euroCupMatchs;
	
	private PhaseType phaseType = PhaseType.getItem(LotteryType.DC_SFP);
	private Phase phase; // 指定彩期
	private String phaseNo;
	private List<Phase> phaseNoList; // 彩期号列表
	private PhaseService phaseService;
	private Integer count = 10;
	private DcRaceService dcRaceService;
	private JczqRaceService jczqRaceService;
	private List<DcRace> dcRaces;
	private List<JczqRace> jczqRaces;
	private Date jcDate;//竞彩比赛时间 用于获取竞彩场次
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("进入获取欧洲杯赛程信息开始");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		try{
			map = euroCupService.findMatchList(super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取欧洲杯赛程信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("获取欧洲杯赛程信息，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}		
		if (map != null) {
			euroCupMatchs = (List<EuroCupMatch>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		try {// 如果没有指定彩期，将当前期设为指定期
			phase = phaseService.getCurrentPhase(phaseType);
			if (phase != null) {
				phaseNo = phase.getPhase();
			} else {
				// 获取离当前时间最近一期
				phase = phaseService.getNearestPhase(phaseType, new Date());
				if (phase != null) {
					phaseNo = phase.getPhase();
				}
			}
			phaseNoList = phaseService.getAppointPhaseList(phaseType, phaseNo,count);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}

		if (phaseNoList != null) {
			// 指定彩期去重
			removeRepeat(phaseNoList);
		}
		logger.info("进入获取欧洲杯赛程信息结束");
		return "matchList";
	}
	
	public String manage(){
		logger.info("更新赛程信息开始");
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		User user = userSessionBean.getUser();
		for(EuroCupMatch euroCupMatch : euroCupMatchs){
			euroCupMatch.setUserId(user.getId());
			/*String dcPhaseNo = euroCupMatch.getDcPhase();
			String dcMatchNum = euroCupMatch.getDcMatchNum();
			List<DcRace> _dcRaces = dcRaceService.getDcRaceListByPhase(dcPhaseNo);
			for(int i = 0; i < _dcRaces.size(); i++){
				DcRace _dcRace = _dcRaces.get(i);
				if(_dcRace.getMatchNum() == Integer.valueOf(dcMatchNum)){
					euroCupMatch.setFxId(_dcRace.getFxId());
				}
			}*/
		}
		ResultBean resultBean = euroCupService.batchUpdateMatch(euroCupMatchs);
		if(resultBean.isResult()){
			logger.info("更新赛程信息结束");
			super.setForwardUrl("/event/euroCupMatchSP.do");
			return "success";
		}else{
			super.setErrorMessage(resultBean.getMessage());
			logger.info("更新赛程信息结束");
			return "failure";
		}
	}
	
	public String updateSp(){
		logger.info("更新赛程SP信息开始");
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		User user = userSessionBean.getUser();
		euroCupMatch.setUserId(user.getId());
		ResultBean resultBean = new ResultBean();
		try {
			resultBean = euroCupService.updateMatchSp(euroCupMatch);
		} catch (ApiRemoteCallFailedException e) {
			
		}
		JSONObject rs = new JSONObject();
		rs.put("result", resultBean.isResult());
		rs.put("message",resultBean.getMessage());
		response = ServletActionContext.getResponse();
		writeRs(response,rs);
		return null;
	}
	
	public String prizeSfp(){
		logger.info("单场金币竞猜胜平负开奖开始");
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		User user = userSessionBean.getUser();
		euroCupMatch.setUserId(user.getId());
		ResultBean resultBean = new ResultBean();
		try {
			resultBean = euroCupService.prizeSfp(euroCupMatch);
		} catch (ApiRemoteCallFailedException e) {
			
		}
		JSONObject rs = new JSONObject();
		rs.put("result", resultBean.isResult());
		rs.put("msg",resultBean.getMessage());
		response = ServletActionContext.getResponse();
		writeRs(response,rs);
		return null;
	}
	
	public String prizeBf(){
		logger.info("单场金币竞猜比分开奖开始");
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		User user = userSessionBean.getUser();
		euroCupMatch.setUserId(user.getId());
		ResultBean resultBean = new ResultBean();
		try {
			resultBean = euroCupService.prizeBf(euroCupMatch);
		} catch (ApiRemoteCallFailedException e) {
			
		}
		JSONObject rs = new JSONObject();
		rs.put("result", resultBean.isResult());
		rs.put("msg",resultBean.getMessage());
		response = ServletActionContext.getResponse();
		writeRs(response,rs);
		return null;
	}
	
	public String getDcMatchNum(){
		logger.info("进入获取场次列表");
		JSONObject rs = new JSONObject();
		response = ServletActionContext.getResponse();
		dcRaces = dcRaceService.getDcRaceListByPhase(phaseNo);
		rs.put("dcRaces", DcRace.toJSONArray(dcRaces));
		writeRs(response, rs);
		logger.info("获取场次列表结束");
		return null;
	}
	
	public String getJczqMatchNum(){
		logger.info("进入获取竞彩足球场次列表");
		JSONObject rs = new JSONObject();
		response = ServletActionContext.getResponse();
		/*try {
			jczqRaces = jczqRaceService.getRaceListByDateAndStatus(jcDate, null, CoreDateUtils.formatDate(jcDate).equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
		}*/
		rs.put("jczqRaces", JczqRace.toJSONArray(jczqRaces));
		writeRs(response, rs);
		logger.info("获取竞彩场次列表结束");
		return null;
	}
	
	/**
	 * 指定彩期列表去重
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param phases
	 */
	private void removeRepeat(List<Phase> phases) {
		// 去重
		boolean flag = false;
		for (int i = 0; i < phases.size(); i++) {
			Phase p = phases.get(i);
			if (phaseNo.equals(p.getPhase())) {
				if (flag) {
					phases.remove(i);
				}
				flag = true;
			}
		}
		// 排序
		for (int i = 0; i < phases.size(); i++) {
			for (int j = i; j < phases.size(); j++) {
				if (Long.parseLong(phases.get(i).getPhase()) < Long
						.parseLong(phases.get(j).getPhase())) {
					Phase temppPhase = phases.get(i);
					phases.set(i, phases.get(j));
					phases.set(j, temppPhase);
				}
			}
		}
	}

	public EuroCupService getEuroCupService() {
		return euroCupService;
	}

	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}

	public EuroCupMatch getEuroCupMatch() {
		return euroCupMatch;
	}

	public void setEuroCupMatch(EuroCupMatch euroCupMatch) {
		this.euroCupMatch = euroCupMatch;
	}

	public List<EuroCupMatch> getEuroCupMatchs() {
		return euroCupMatchs;
	}

	public void setEuroCupMatchs(List<EuroCupMatch> euroCupMatchs) {
		this.euroCupMatchs = euroCupMatchs;
	}

	public PhaseType getPhaseType() {
		return phaseType;
	}

	public void setPhaseType(PhaseType phaseType) {
		this.phaseType = phaseType;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public List<Phase> getPhaseNoList() {
		return phaseNoList;
	}

	public void setPhaseNoList(List<Phase> phaseNoList) {
		this.phaseNoList = phaseNoList;
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public String getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}

	public DcRaceService getDcRaceService() {
		return dcRaceService;
	}

	public void setDcRaceService(DcRaceService dcRaceService) {
		this.dcRaceService = dcRaceService;
	}

	public List<DcRace> getDcRaces() {
		return dcRaces;
	}

	public void setDcRaces(List<DcRace> dcRaces) {
		this.dcRaces = dcRaces;
	}

	public JczqRaceService getJczqRaceService() {
		return jczqRaceService;
	}

	public void setJczqRaceService(JczqRaceService jczqRaceService) {
		this.jczqRaceService = jczqRaceService;
	}

	public List<JczqRace> getJczqRaces() {
		return jczqRaces;
	}

	public void setJczqRaces(List<JczqRace> jczqRaces) {
		this.jczqRaces = jczqRaces;
	}

	public Date getJcDate() {
		return jcDate;
	}

	public void setJcDate(Date jcDate) {
		this.jcDate = jcDate;
	}
	
}
