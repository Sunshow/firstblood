package web.action.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.AliasMatchBean;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.enums.AliasDataProvider;
import com.lehecai.admin.web.service.alias.AliasService;
import com.lehecai.admin.web.service.lottery.Fb46MatchService;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.ticket.TicketService;
import com.lehecai.core.api.lottery.Fb46Match;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseStatus;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.PlanStatus;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreStringUtils;
import com.opensymphony.xwork2.Action;

public class Fb46MatchAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(Fb46MatchAction.class);

	private PhaseService phaseService;
	private Fb46MatchService fb46MatchService;
	private LotteryPlanService lotteryPlanService;
	private AliasService aliasService;
	private TicketService ticketService;
	
	private static final int PLAN_COUNT_PER_QUERY = 200;
	
	private Integer count = 10;
	
	private PhaseType phaseType;;
	
	private List<Phase> phaseNoList;		//彩期号列表
	
	private Phase phase;		//指定彩期
	
	private String phaseNo;
	
	private int type;
	
	private int fetcherType;
	
	private List<Fb46Match> fb46Matchs;
	
	private Fb46Match fb46Match;
	
	private static final String QUERY_ALL_PHASE = "-1";// 查询所有彩期
	
	private String matchIds;//对阵ID，用于匹配比赛时间
	
	public String handle() {
		logger.info("进入获取46场对阵球队信息列表");
		try {//如果没有指定彩期，将当前期设为指定期
			if (phaseNo == null || phaseNo.isEmpty() || phaseNo.equals(QUERY_ALL_PHASE)) {
				//查询当前期
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
				if (phaseNo == null) {
					phaseNo = "";
				}
			} else {
				phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
			}
			
			searchPhaseList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (phase != null) {
			phaseNo = phase.getPhase();
			fb46Matchs = fb46MatchService.getFb46MatchListByPhase(phaseNo, phaseType);
		}
		logger.info("获取46场对阵球队信息列表结束");
		return "list";
	}

	//获得彩期列表
	private void searchPhaseList() throws ApiRemoteCallFailedException{
		phaseNoList = phaseService.getAppointPhaseList(phaseType, phaseNo, count);
		if (phaseNoList != null) {
			//指定彩期去重
			removeRepeat(phaseNoList);
		}
	}

	/**
	 * 指定彩期列表去重
	 * @param phases
	 */
	private void removeRepeat(List<Phase> phases) {
		//去重
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
		//排序
		for (int i = 0; i < phases.size(); i++) {
			for (int j = i; j < phases.size(); j++) {
				if (Long.parseLong(phases.get(i).getPhase()) < Long.parseLong(phases.get(j).getPhase())) {
					Phase temppPhase = phases.get(i);
					phases.set(i, phases.get(j));
					phases.set(j, temppPhase);
				}
			}
		}
	}
	
	public String getPhaseInfo() {
		logger.info("进入获取彩期信息");
		try {
			searchPhaseList();
			this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩期信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		fb46Matchs = fb46MatchService.getFb46MatchListByPhase(phaseNo, phaseType);
		logger.info("获取彩期信息结束");
		return "list";
	}
	
	public String fetchFb46Matchs() {
		logger.info("进入获取46场比赛数据");
		try {
			searchPhaseList();
			this.phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取46场比赛数据，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		fb46Matchs = fb46MatchService.fetchFb46MatchListByPhase(phaseNo, phaseType, FetcherType.getItem(fetcherType));
		if (fb46Matchs == null || fb46Matchs.isEmpty()) {
			logger.error("比赛数据为空");
			super.setErrorMessage("未抓取到比赛数据，请尝试其它抓取");
		} else {
			List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
			for (Fb46Match fb46Match : fb46Matchs) {
				AliasMatchBean match = new AliasMatchBean();
				match.setLeagueLongName(fb46Match.getMatch());
				match.setMatchTime(CoreDateUtils.formatDate(fb46Match.getMatchDate(), CoreDateUtils.DATETIME));
				match.setHomeTeamLongName(fb46Match.getHomeTeam());
				match.setAwayTeamLongName(fb46Match.getGuestTeam());
				match.setMatchId(fb46Match.getMatchId());
				matchs.add(match);
			}
			try {
				matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
				for (int i = 0 ;i < fb46Matchs.size() ; i++) {
					if (matchs.get(i) != null){
						if(matchs.get(i).getAwayTeamShortName() != null && !"".equals(matchs.get(i).getAwayTeamShortName())) {
							fb46Matchs.get(i).setGuestTeam(matchs.get(i).getAwayTeamShortName());
						}
						if (matchs.get(i).getHomeTeamShortName() != null && !"".equals(matchs.get(i).getHomeTeamShortName())) {
							fb46Matchs.get(i).setHomeTeam(matchs.get(i).getHomeTeamShortName());
						}
						if (matchs.get(i).getLeagueShortName() != null && !"".equals(matchs.get(i).getLeagueShortName())) {
							fb46Matchs.get(i).setMatch(matchs.get(i).getLeagueShortName());
						}
						if (matchs.get(i).getMatchId() != null && matchs.get(i).getMatchId() != 0) {
							fb46Matchs.get(i).setMatchId(matchs.get(i).getMatchId());
						} else {
							fb46Matchs.get(i).setMatchId(0);
						}
					}
				}
			} catch (Exception e) {
				logger.error("别名服务失败" + aliasService.getClass() + "getAlias" + "参数" + AliasDataProvider.PLOT);
			}
		}
		logger.info("获取46场比赛数据结束");
		return "list";
	}
	
	//全部比赛匹配短名
	public String matchAliasAll() {
		try {//如果没有指定彩期，将当前期设为指定期
			if (phaseNo == null || phaseNo.isEmpty() || phaseNo.equals(QUERY_ALL_PHASE)) {
				//查询当前期
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
				if (phaseNo == null) {
					phaseNo = "";
				}
			} else {
				phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
			}
			
			searchPhaseList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (phase != null) {
			phaseNo = phase.getPhase();
			fb46Matchs = fb46MatchService.getFb46MatchListByPhase(phaseNo, phaseType);
		}
		if (fb46Matchs == null || fb46Matchs.isEmpty()) {
			logger.error("比赛数据为空");
			super.setErrorMessage("未抓取到比赛数据，请尝试其它抓取");
		} else {
			List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
			for (Fb46Match fb46Match : fb46Matchs) {
				AliasMatchBean match = new AliasMatchBean();
				match.setLeagueLongName(fb46Match.getMatch());
				match.setMatchTime(CoreDateUtils.formatDate(fb46Match.getMatchDate(), CoreDateUtils.DATETIME));
				match.setHomeTeamLongName(fb46Match.getHomeTeam());
				match.setAwayTeamLongName(fb46Match.getGuestTeam());
				match.setMatchId(fb46Match.getMatchId());
				matchs.add(match);
			}
			try {
				matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
				for (int i = 0 ;i < fb46Matchs.size() ; i++) {
					if (matchs.get(i) != null){
						if(matchs.get(i).getAwayTeamShortName() != null && !"".equals(matchs.get(i).getAwayTeamShortName())) {
							fb46Matchs.get(i).setGuestTeam(matchs.get(i).getAwayTeamShortName());
						}
						if (matchs.get(i).getHomeTeamShortName() != null && !"".equals(matchs.get(i).getHomeTeamShortName())) {
							fb46Matchs.get(i).setHomeTeam(matchs.get(i).getHomeTeamShortName());
						}
						if (matchs.get(i).getLeagueShortName() != null && !"".equals(matchs.get(i).getLeagueShortName())) {
							fb46Matchs.get(i).setMatch(matchs.get(i).getLeagueShortName());
						}
						if (matchs.get(i).getMatchId() != null && matchs.get(i).getMatchId() != 0) {
							fb46Matchs.get(i).setMatchId(matchs.get(i).getMatchId());
						} else {
							fb46Matchs.get(i).setMatchId(0);
						}
					}
				}
			} catch (Exception e) {
				logger.error("别名服务失败" + aliasService.getClass() + "getAlias" + "参数" + AliasDataProvider.PLOT);
			}
		}
		logger.info("获取46场对阵球队信息列表结束");
		return "list";
	}
	
	//个别比赛匹配短名
	public String matchAlias() {
		
		logger.info("进入个别场次别名匹配");
		String msg = "";
		JSONObject rs = new JSONObject();
		if (fb46Match == null) {
			msg = "比赛信息为空";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		
		List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
		
		AliasMatchBean match = new AliasMatchBean();
		match.setLeagueLongName(fb46Match.getMatch());
		match.setMatchTime(CoreDateUtils.formatDate(fb46Match.getMatchDate(), CoreDateUtils.DATETIME));
		match.setHomeTeamLongName(fb46Match.getHomeTeam());
		match.setAwayTeamLongName(fb46Match.getGuestTeam());
		match.setMatchId(fb46Match.getMatchId());
		matchs.add(match);
		
		matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
		
		if (matchs != null && matchs.size() > 0) {
			match = matchs.get(0);
			if (match.getMatchId() != null && match.getMatchId() != 0) {
				msg = "匹配成功";
				fb46Match.setMatchId(match.getMatchId());
			} else {
				msg = "匹配失败";
				rs.put("message", msg);
				writeRs(ServletActionContext.getResponse(), rs);
				return Action.NONE;
			}
			msg = msg + "\n主队短名：" + match.getHomeTeamShortName() + "\n客队短名：" + match.getAwayTeamShortName()
					+ "\n联赛名短名：" + match.getLeagueShortName() + "\n分析ID：" + match.getMatchId();
			fb46Match.setGuestTeam(match.getAwayTeamShortName());
			fb46Match.setHomeTeam(match.getHomeTeamShortName());
			fb46Match.setMatch(match.getLeagueShortName());
			rs.put("message", msg);
			rs.put("fb46Match", JSONObject.fromObject(fb46Match));
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		} else {
			msg = "匹配失败";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
	}
	
	public String updateFb46Match() {
		logger.info("进入更新46场比赛数据");
		//初始化彩种
		initPhaseType();
		ResultBean resultBean = fb46MatchService.batchCreatePhase(fb46Matchs);
		try {
			phaseNo = fb46Matchs.get(0).getPhase();
			searchPhaseList();
			this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩期，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		fb46Matchs = fb46MatchService.getFb46MatchListByPhase(phaseNo, phaseType);
		super.setErrorMessage(resultBean.getMessage());
		logger.info("更新46场比赛数据结束");
		return "list";
	}
	
	public String updatePhaseDate() {
		logger.info("进入更新彩期时间");
		ResultBean resultBean = null;
		JSONObject rs = new JSONObject();
		//修改胜负彩彩期时间
		phase.setPhaseType(phaseType);
		try {
			resultBean = phaseService.updatePhase(phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("更新彩期时间，api调用异常，{}", e.getMessage());
			rs.put("msg", "api调用异常，请联系技术人员!原因:" + e.getMessage());
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		if (resultBean.isResult()) {
			rs.put("msg", "更新彩期时间成功");
			logger.info("更新彩期时间成功");
		} else {
			rs.put("msg", "更新彩期时间失败请重试");
			logger.error("更新彩期时间失败!code:{},msg:{}",resultBean.getCode(),resultBean.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更新彩期时间结束");
		return null;
	}
	
	/**
	 * 重算出票截止时间和销售截止时间
	 * @return
	 */
	public String resetDeadline() {
		JSONObject json = new JSONObject();
		int rc = 0;
		String message = "操作成功";

		if (phase == null) {
			logger.error("[重算截止]参数为空");
			super.setErrorMessage("[重算截止]参数不能为空");
			rc = 1;
			message = "[重算截止]参数不能为空";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}

		List<String> planIds = new ArrayList<String>();
		List<String> planTicketIds = new ArrayList<String>();

		logger.info("[重算截止]方案状态为：招募中和未支付,待出票");
		List<PlanStatus> planStatusList = new ArrayList<PlanStatus>();
		planStatusList.add(PlanStatus.RECRUITING);
		planStatusList.add(PlanStatus.PRINT_WAITING);
		planStatusList.add(PlanStatus.PAID_NOT);
		planStatusList.add(PlanStatus.PRINTING);

		PageBean pageBean = super.getPageBean();
		pageBean.setPageSize(PLAN_COUNT_PER_QUERY);// 每次查询条数

		int i = 1;
		while (true) {
			List<Plan> tmpPlanList = null;
			pageBean.setPage(i);
			try {
				tmpPlanList = lotteryPlanService.findByPhaseType(phaseType,
						phase.getPhase(), planStatusList, pageBean);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
			}
			if (tmpPlanList == null || tmpPlanList.size() == 0) {
				logger.error("[重算截止]查询方案结果为空,phase:{}", phase.getPhase());
				break;
			}
			for (Plan p : tmpPlanList) {
				planIds.add(p.getId());
				if (p.getPlanStatus() == PlanStatus.PRINTING) {
                	planTicketIds.add(p.getId());
                }
			}

			if (tmpPlanList.size() < pageBean.getPageSize()) {
				logger.info("[重算截止]当前查询结果小于每页条数，说明已经到最后一页，则跳出循环");
				break;
			}
			i++;
		}

		if (planIds == null || planIds.size() == 0) {
			logger.info("[重算截止]没有方案包含phase:{}", phase.getPhase());
			super.setSuccessMessage("没有方案包含phase:" + phase.getPhase()
					+ "，无需重新计算截止");
			rc = 1;
			message = "[重算截止]没有方案包含phase:" + phase.getPhase() + "，无需重新计算截止";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}

		//List<String> successList = new ArrayList<String>();
		List<String> changedList = new ArrayList<String>();
		List<String> noChangedList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		
		for (String planId : planIds) {
			boolean updateResult = false;
			try {
				updateResult = lotteryPlanService.updateDeadline(planId, phase.getEndTicketTime(), phase.getEndSaleTime());
				if (updateResult) {
					changedList.add(planId);
				} else {
					noChangedList.add(planId);
				}
			} catch (ApiRemoteCallFailedException e) {
				failureList.add(planId);
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因" + e.getMessage());
				rc = 1;
				message = "重算截止失败";
				json.put("code", rc);
				json.put("msg", message);
				writeRs(ServletActionContext.getResponse(), json);
				return Action.NONE;
			}
		}
		
		//更新已拆票，正在出票，票状态为1,2,3的票截止日期
		try {
			if (planTicketIds != null && planTicketIds.size() > 0) {
				ticketService.updateTerminateTimeByPlanIds(planTicketIds, changedList);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因" + e.getMessage());
			rc = 1;
			message = "更新已拆票订单截止时间失败";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			rc = 1;
			message = "更新已拆票订单截止时间失败";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}
		
		logger.info("结束重算截止,phase:{}", phase.getPhase());
		rc = 0;
		message = "重算截止:已修改方案"
			+ changedList.size()
			+ "个,未修改方案"
			+ noChangedList.size()
			+ "个,失败方案"
			+ failureList.size()
			+ "个"
			+ (failureList.size() > 0 ? ",编码:"
					+ CoreStringUtils.join(failureList, ",") : "");
		json.put("code", rc);
		json.put("msg", message);
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("重算截止结束");
		return Action.NONE;
	}
	
	public String manualEntry() {
		logger.info("进入手工录入赛程");
		try {
			searchPhaseList();
			this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩期，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		int count = 0;
		if (phaseType.getValue() == LotteryType.JQC.getValue()) {
			count = 4;
		} else if (phaseType.getValue() == LotteryType.BQC.getValue()) {
			count = 6;
		}
		
		fb46Matchs = new ArrayList<Fb46Match>();
		for (int i = 1; i <= count ; i++) {
			Fb46Match fb46Match = new Fb46Match();
			fb46Match.setMatchNum(i);
			fb46Match.setPhase(phaseNo);
			fb46Matchs.add(fb46Match);
		}
		logger.info("手工录入赛程结束");
		return "list";
	}
	

	//匹配全部比赛时间
	public String matchFb46MatchTime() {
		
		logger.info("进入个别场次别名匹配");
		String msg = "";
		JSONObject rs = new JSONObject();
		rs.put("flag", "0");
		if (StringUtils.isEmpty(matchIds)) {
			msg = "获取分析id为空";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		String[] matchArray = StringUtils.split(matchIds, ",");
		
		List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
		matchs = aliasService.getFootballMatchTimeByIds(matchArray);
		JSONArray jsonArray = new JSONArray();
		if (matchs != null && matchs.size() > 0) {
			for (AliasMatchBean bean : matchs) {
				JSONObject obj = new JSONObject();
				obj.put("matchId", bean.getMatchId() + "");
				obj.put("matchTime", bean.getMatchTime());
				jsonArray.add(obj);
			}
			rs.put("message", msg);
			rs.put("data", jsonArray.toString());
			rs.put("flag", 1);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		} else {
			msg = "匹配失败";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
	}
	
	private void initPhaseType() {
		for (Fb46Match fb46Match : fb46Matchs) {
			fb46Match.setType(phaseType);
		}
	}
	
	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public PhaseType getPhaseType() {
		return phaseType;
	}

	public void setPhaseType(PhaseType phaseType) {
		this.phaseType = phaseType;
	}

	public List<Phase> getPhaseNoList() {
		return phaseNoList;
	}

	public void setPhaseNoList(List<Phase> phaseNoList) {
		this.phaseNoList = phaseNoList;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public String getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}
	
	public List<Fb46Match> getFb46Matchs() {
		return fb46Matchs;
	}

	public void setFb46Matchs(List<Fb46Match> fb46Matchs) {
		this.fb46Matchs = fb46Matchs;
	}

	public Fb46MatchService getFb46MatchService() {
		return fb46MatchService;
	}

	public void setFb46MatchService(Fb46MatchService fb46MatchService) {
		this.fb46MatchService = fb46MatchService;
	}

	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}

	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
		if (type == 4) {
			phaseType = PhaseType.getItem(LotteryType.JQC);
		} else if (type == 6) {
			phaseType = PhaseType.getItem(LotteryType.BQC);
		}
	}

	public List<FetcherType> getFetchers() {
		return FetcherType.getItems();
	}

	public PhaseStatus getOpenStatus() {
		return PhaseStatus.OPEN;
	}
	
	public int getFetcherType() {
		return fetcherType;
	}

	public void setFetcherType(int fetcherType) {
		this.fetcherType = fetcherType;
	}

	public AliasService getAliasService() {
		return aliasService;
	}

	public void setAliasService(AliasService aliasService) {
		this.aliasService = aliasService;
	}

	public Fb46Match getFb46Match() {
		return fb46Match;
	}

	public void setFb46Match(Fb46Match fb46Match) {
		this.fb46Match = fb46Match;
	}

	public void setMatchIds(String matchIds) {
		this.matchIds = matchIds;
	}

	public String getMatchIds() {
		return matchIds;
	}

	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	public TicketService getTicketService() {
		return ticketService;
	}
}
