package web.action.schedule;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.AliasMatchBean;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.domain.business.Sms;
import com.lehecai.admin.web.enums.AliasDataProvider;
import com.lehecai.admin.web.enums.StatusType;
import com.lehecai.admin.web.service.alias.AliasService;
import com.lehecai.admin.web.service.business.SmsService;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.lottery.SfggRaceService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.lottery.SfggRace;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.*;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.queue.QueueConstant;
import com.lehecai.core.queue.QueueTaskService;
import com.lehecai.core.queue.sms.SmsQueueTask;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreFileUtils;
import com.lehecai.core.util.CoreStringUtils;
import com.opensymphony.xwork2.Action;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class SfggRaceAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(SfggRaceAction.class);

	private static final int PLAN_COUNT_PER_QUERY = 200;
	private static final String DC_RETURN_TICKET_TEMPLATE = "return_ticket_sms.html";

	private PhaseService phaseService;
	private AliasService aliasService;

	private SfggRaceService sfggRaceService;
	private SmsService smsService;
	private QueueTaskService smsQueueTaskService;
	private MemberService memberService;
	
	private String callbackUrl;
	private String path;
	private String webroot;

	private LotteryPlanService lotteryPlanService;

	private Integer count = 10;

	private PhaseType phaseType = PhaseType.getItem(LotteryType.DC_SFGG);

	private List<Phase> phaseNoList; // 彩期号列表

	private Phase phase; // 指定彩期

	private String phaseNo;

	private String matchNum;// 比赛编码

	private int fetcherType;

	private int fetcherTypeCompare;
	
	private String pageResult;

	private List<SfggRace> sfggRaces;

	private List<SfggRace> compareRaces;
	
	private String isSpOrRace; // ajax更新对阵或SP标记

	private String tag;

	private SfggRace sfggRace;

	private List<PhaseStatus> phaseStatusList;

	private String phaseStatusNo;

	private File excelFile;

	private String strError;
	
	private int statusValue;
	
	private String errorStr;
	
	private static final String QUERY_ALL_PHASE = "-1";// 查询所有彩期

	public SfggRaceAction() {
		super();
		this.phaseStatusList = new ArrayList<PhaseStatus>();
		this.phaseStatusList.add(PhaseStatus.UNOPEN);
		this.phaseStatusList.add(PhaseStatus.CLOSE);
		this.phaseStatusList.add(PhaseStatus.DISABLED);
		this.phaseStatusList.add(PhaseStatus.DRAW);
		this.phaseStatusList.add(PhaseStatus.REWARD);
		this.phaseStatusList.add(PhaseStatus.OPEN);

	}

	public String handle() {
		logger.info("进入查询单场赛程");
		try {// 如果没有指定彩期，将当前期设为指定期
			if (phaseNo == null || phaseNo.isEmpty()
					|| phaseNo.equals(QUERY_ALL_PHASE)) {
				// 查询当前期
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
				phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(
						phaseType, phaseNo);
			}

			searchPhaseList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (phase != null) {
			phaseNo = phase.getPhase();
			sfggRaces = sfggRaceService.getSfggRaceListByPhase(phaseNo);
		}
		if (pageResult != null && !pageResult.trim().isEmpty()) {
			return pageResult;
		}
		logger.info("查询单场赛程结束");
		return "list";
	}

	// 获得彩期列表
	private void searchPhaseList() throws ApiRemoteCallFailedException {
		phaseNoList = phaseService.getAppointPhaseList(phaseType, phaseNo,
				count);
		if (phaseNoList != null) {
			// 指定彩期去重
			removeRepeat(phaseNoList);
		}
	}

	/**
	 * 指定彩期列表去重
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

	public String getPhaseInfo() {
		logger.info("进入查询彩期信息");
		try {
			searchPhaseList();
			this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(
					phaseType, phaseNo);
			if (phase != null) {
				this.phaseStatusNo = String.valueOf(this.phase.getPhaseStatus()
						.getValue());
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
			return "failure";
		}
		sfggRaces = sfggRaceService.getSfggRaceListByPhase(phaseNo);
		if (pageResult != null && !pageResult.trim().isEmpty()) {
			return pageResult;
		}
		logger.info("查询彩期信息结束");
		return "list";
	}

	public String fetchSfggRaces() {
		logger.info("进入获取比赛数据");
		// 比分SP值抓取
		if (pageResult != null && !pageResult.trim().isEmpty()) {
			// TODO 抓取比赛sp和比分
			List<SfggRace> dbSfggRaces = sfggRaceService
					.getSfggRaceListByPhase(phaseNo);
			if (dbSfggRaces != null && !dbSfggRaces.isEmpty()) {
				List<SfggRace> fetchSfggRaces = sfggRaceService
						.fetchSfggRaceSpListByPhase(phaseNo, FetcherType
								.getItem(fetcherType));
				if (fetchSfggRaces == null || fetchSfggRaces.isEmpty()) {
					logger.error("获取比赛数据，赛事结果SP值数据为空");
					super.setErrorMessage("未抓取到赛事结果SP值数据，请尝试其它抓取");
				} else {
					sfggRaces = packSfggRaces(dbSfggRaces, fetchSfggRaces);
//					if (fetcherTypeCompare == fetcherType) {
//						super.setErrorMessage("未比较，抓取地址和比较地址不能为同一地址");
//						logger.error("未比较，抓取地址和比较地址不能为同一地址");
//					} else {
//						List<SfggRace> fetchSfggRacesCompare = sfggRaceService
//							.fetchSfggRaceSpListByPhase(phaseNo, FetcherType
//								.getItem(fetcherTypeCompare));
//						if (fetchSfggRacesCompare == null || fetchSfggRacesCompare.isEmpty()) {
//							logger.error("获取比赛的比较数据，赛事结果SP值数据为空");
//							super.setErrorMessage("未抓取到赛事结果SP值比较数据，请尝试其它抓取");
//						} else {
							//errorList为保存哪一行比较不相同的信息，暂时只是记录未输出
//							List<Integer> errorList = new ArrayList<Integer>();
//							// 抓取数据遍历
//							Map<Integer, SfggRace> sfggRacesMap = new HashMap<Integer, SfggRace>();
//							for (SfggRace sfggRace : fetchSfggRacesCompare) {
//								sfggRacesMap.put(sfggRace.getMatchNum(), sfggRace);
//							}
//							compareRaces = new ArrayList<SfggRace>();
//							for (SfggRace sfggRace : sfggRaces) {
//								SfggRace tempSfggRace = sfggRacesMap.get(sfggRace.getMatchNum());
//								boolean flag = false;
//								compareRaces.add(new SfggRace()); 
//								if (tempSfggRace == null) {
//									flag = true;
//									continue;
//								}
//								//胜负平sp值
//								if ("".equals(sfggRace.getSpSf()) && !"".equals(tempSfggRace.getSpSf())) {
//									flag = true;
//									compareRaces.get(compareRaces.size() - 1).setSpSf(tempSfggRace.getSpSf());
//								} else if (!"".equals(sfggRace.getSpSf()) && "".equals(tempSfggRace.getSpSf())) {
//									flag = true;
//								} else if (!"".equals(sfggRace.getSpSf()) && !"".equals(tempSfggRace.getSpSf())) {
//									if(Double.compare(Double.parseDouble(sfggRace.getSpSf()), Double.parseDouble(tempSfggRace.getSpSf())) != 0) {
//										flag = true;
//										compareRaces.get(compareRaces.size() - 1).setSpSf(tempSfggRace.getSpSf());
//									}
//								}
//								
//								if (flag) {
//									errorList.add(sfggRace.getMatchNum());
//								}
//							}
//							if (errorList != null && errorList.size() > 0) {
//								errorStr = "";
//								for (int i = 0; i < errorList.size(); i++) {
//									errorStr += errorList.get(i) + ",";
//								}
//								if (!"".equals(errorStr)) {
//									errorStr = errorStr.substring(0, errorStr.length() - 1);
//								}
//								//super.setErrorMessage("比较不一致的场次编号：" + errorStr);
//								//logger.error("比较不一致的场次编号：" + errorStr);
//							} else {
//								super.setErrorMessage("彩期" + phaseNo + "所有比赛场次已比较完毕");
//								logger.error("彩期" + phaseNo + "所有比赛场次已比较完毕");
//							}
//						}
//					}
				}
			} else {
				logger.error("获取比赛数据，单场对阵信息为空");
				super.setErrorMessage("单场对阵信息未录入，不能抓取赛事结果SP值");
			}
			try {
				searchPhaseList();
				this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(
						phaseType, phaseNo);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			return pageResult;
		}

		// 对阵抓取
		try {
			searchPhaseList();
			this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(
					phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		sfggRaces = sfggRaceService.fetchSfggRaceListByPhase(phaseNo, FetcherType
				.getItem(fetcherType));
		if (sfggRaces == null || sfggRaces.isEmpty()) {
			super.setErrorMessage("未抓取到比赛数据，请尝试其它抓取");
		} else {
			List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
			for (SfggRace dr : sfggRaces) {
				AliasMatchBean match = new AliasMatchBean();
				match.setLeagueLongName(dr.getMatchName());
				match.setMatchTime(CoreDateUtils.formatDate(dr.getMatchDate(), CoreDateUtils.DATETIME));
				match.setHomeTeamLongName(dr.getHomeTeam());
				match.setAwayTeamLongName(dr.getAwayTeam());
				match.setMatchId(dr.getFxId());
				matchs.add(match);
			}
			try {
				matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
				for (int i = 0 ;i < sfggRaces.size() ; i++) {
					if (matchs.get(i) != null){
						if(matchs.get(i).getAwayTeamShortName() != null && !"".equals(matchs.get(i).getAwayTeamShortName())) {
							sfggRaces.get(i).setAwayTeam(matchs.get(i).getAwayTeamShortName());
						}
						if (matchs.get(i).getHomeTeamShortName() != null && !"".equals(matchs.get(i).getHomeTeamShortName())) {
							sfggRaces.get(i).setHomeTeam(matchs.get(i).getHomeTeamShortName());
						}
						if (matchs.get(i).getLeagueShortName() != null && !"".equals(matchs.get(i).getLeagueShortName())) {
							sfggRaces.get(i).setMatchName(matchs.get(i).getLeagueShortName());
						}
						if (matchs.get(i).getMatchId() != null && matchs.get(i).getMatchId() != 0) {
							sfggRaces.get(i).setFxId(matchs.get(i).getMatchId());
						} else {
							sfggRaces.get(i).setFxId(0);
						}
					}
				}
			} catch (Exception e) {
				logger.error("别名服务失败" + aliasService.getClass() + "getAlias" + "参数" + AliasDataProvider.PLOT);
			}
		}
		
		logger.info("获取比赛数据结束");
		return "list";
	}
	
	//全部比赛匹配短名
	public String matchAliasAll() {
		try {// 如果没有指定彩期，将当前期设为指定期
			if (phaseNo == null || phaseNo.isEmpty()
					|| phaseNo.equals(QUERY_ALL_PHASE)) {
				// 查询当前期
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
				phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(
						phaseType, phaseNo);
			}

			searchPhaseList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (phase != null) {
			phaseNo = phase.getPhase();
			sfggRaces = sfggRaceService.getSfggRaceListByPhase(phaseNo);
		}
		if (sfggRaces == null || sfggRaces.isEmpty()) {
			super.setErrorMessage("未抓取到比赛数据，请尝试其它抓取");
		} else {
			List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
			for (SfggRace dr : sfggRaces) {
				AliasMatchBean match = new AliasMatchBean();
				match.setLeagueLongName(dr.getMatchName());
				match.setMatchTime(CoreDateUtils.formatDate(dr.getMatchDate(), CoreDateUtils.DATETIME));
				match.setHomeTeamLongName(dr.getHomeTeam());
				match.setAwayTeamLongName(dr.getAwayTeam());
				match.setMatchId(dr.getFxId());
				matchs.add(match);
			}
			try {
				matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
				for (int i = 0 ;i < sfggRaces.size() ; i++) {
					if (matchs.get(i) != null){
						if(matchs.get(i).getAwayTeamShortName() != null && !"".equals(matchs.get(i).getAwayTeamShortName())) {
							sfggRaces.get(i).setAwayTeam(matchs.get(i).getAwayTeamShortName());
						}
						if (matchs.get(i).getHomeTeamShortName() != null && !"".equals(matchs.get(i).getHomeTeamShortName())) {
							sfggRaces.get(i).setHomeTeam(matchs.get(i).getHomeTeamShortName());
						}
						if (matchs.get(i).getLeagueShortName() != null && !"".equals(matchs.get(i).getLeagueShortName())) {
							sfggRaces.get(i).setMatchName(matchs.get(i).getLeagueShortName());
						}
						if (matchs.get(i).getMatchId() != null && matchs.get(i).getMatchId() != 0) {
							sfggRaces.get(i).setFxId(matchs.get(i).getMatchId());
						} else {
							sfggRaces.get(i).setFxId(0);
						}
					}
				}
			} catch (Exception e) {
				logger.error("别名服务失败" + aliasService.getClass() + "getAlias" + "参数" + AliasDataProvider.PLOT);
			}
		}
		if (pageResult != null && !pageResult.trim().isEmpty()) {
			return pageResult;
		}
		logger.info("查询单场赛程结束");
		return "list";
	}
	
	//个别比赛匹配短名
	public String matchAlias() {
		
		logger.info("进入个别场次别名匹配");
		String msg = "";
		JSONObject rs = new JSONObject();
		if (sfggRace == null) {
			msg = "比赛信息为空";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		
		List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
		
		AliasMatchBean match = new AliasMatchBean();
		match.setLeagueLongName(sfggRace.getMatchName());
		match.setMatchTime(CoreDateUtils.formatDate(sfggRace.getMatchDate(), CoreDateUtils.DATETIME));
		match.setHomeTeamLongName(sfggRace.getHomeTeam());
		match.setAwayTeamLongName(sfggRace.getAwayTeam());
		match.setMatchId(sfggRace.getFxId());
		matchs.add(match);
		
		matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
		
		if (matchs != null && matchs.size() > 0) {
			match = matchs.get(0);
			if (match.getMatchId() != null && match.getMatchId() != 0) {
				msg = "匹配成功";
				sfggRace.setFxId(match.getMatchId());
			} else {
				msg = "匹配失败";
				rs.put("message", msg);
				writeRs(ServletActionContext.getResponse(), rs);
				return Action.NONE;
			}
			msg = msg + "\n主队短名：" + match.getHomeTeamShortName() + "\n客队短名：" + match.getAwayTeamShortName()
					+ "\n联赛名短名：" + match.getLeagueShortName() + "\n分析ID：" + match.getMatchId();
			sfggRace.setAwayTeam(match.getAwayTeamShortName());
			sfggRace.setHomeTeam(match.getHomeTeamShortName());
			sfggRace.setMatchName(match.getLeagueShortName());
			rs.put("message", msg);
			rs.put("sfggRace", JSONObject.fromObject(sfggRace));
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		} else {
			msg = "匹配失败";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
	}
	
	//对抓取回来的单场信息进行比较
//	private boolean compare(String str1, String str2) {
//		if (str1 == null || "".equals(str1)) {
//			if (str2 != null && !"".equals(str2)) {
//				return false;
//			}
//		} else {
//			if (str2 == null || "".equals(str2)) {
//				return false;
//			} else {
//				if (!str1.equals(str2)) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}

	// 抓取结果SP值数据与对阵信息拼装
	private List<SfggRace> packSfggRaces(List<SfggRace> dbSfggRaces,
			List<SfggRace> fetchSfggRaces) {
		// 抓取数据遍历
		Map<Integer, SfggRace> sfggRacesMap = new HashMap<Integer, SfggRace>();
		for (SfggRace sfggRace : fetchSfggRaces) {
			sfggRacesMap.put(sfggRace.getMatchNum(), sfggRace);
		}

		// 数据库数据遍历
		for (SfggRace sfggRace : dbSfggRaces) {
			SfggRace tempSfggRace = sfggRacesMap.get(sfggRace.getMatchNum());
			if (tempSfggRace == null) {
				continue;
			}
			if (sfggRace.getStatus().getValue() != DcRaceStatus.RETURN_PRIZE
					.getValue()) {
				//sfggRace.setFinalScore(tempSfggRace.getFinalScore());
				sfggRace.setSpSf(tempSfggRace.getSpSf());
			}
		}
		return dbSfggRaces;
	}

	public String updateSfggRaces() {
		logger.info("进入更新单场赛程");
		// 更新单场sp值数据
		if (pageResult != null && !pageResult.trim().isEmpty()) {
			ResultBean resultBean = sfggRaceService.batchUpdateSfggRaceSp(sfggRaces);
			phaseNo = sfggRaces.get(0).getPhase();
			try {
				searchPhaseList();
				this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(
						phaseType, phaseNo);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			sfggRaces = sfggRaceService.getSfggRaceListByPhase(phaseNo);
			super.setErrorMessage(resultBean.getMessage());
			return pageResult;
		}

		// 设置为未销售，并存储赛事数据（不包括sp值，sp值请看上面代码段）
		initStatus(sfggRaces);
		ResultBean resultBean = sfggRaceService.batchCreatePhase(sfggRaces);
		phaseNo = sfggRaces.get(0).getPhase();
		try {
			searchPhaseList();
			this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(
					phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		sfggRaces = sfggRaceService.getSfggRaceListByPhase(phaseNo);
		super.setErrorMessage(resultBean.getMessage());
		logger.info("更新单场赛程结束");
		return "list";
	}

	// 初始化对阵状态
	private void initStatus(List<SfggRace> sfggRaces) {
		for (SfggRace sfggRace : sfggRaces) {
			sfggRace.setStatus(DcRaceStatus.NO_BUY);
		}
	}

	// AjAX修改彩期时间
	public String updatePhaseDate() {
		logger.info("进入更新彩期时间");
		ResultBean resultBean = null;
		JSONObject rs = new JSONObject();
		// 修改彩期时间
		phase.setPhaseType(phaseType);
		try {
			resultBean = phaseService.updatePhase(phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			rs.put("msg", "api调用异常，请联系技术人员!原因:" + e.getMessage());
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}

		if (resultBean.isResult()) {
			rs.put("msg", "更新彩期时间成功");
		} else {
			rs.put("msg", "更新彩期时间失败!请重试");
			logger.error("更新彩期时间失败!code:{},msg:{}", resultBean.getCode(),
					resultBean.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更新彩期时间结束");
		return null;
	}

	// AjAX修改彩期状态
	public String updatePhaseStatus() {
		logger.info("进入更新彩期状态");
		ResultBean resultBean = null;
		JSONObject rs = new JSONObject();
		// 彩期状态
		PhaseStatus phaseStatus = PhaseStatus.getItem(Integer
				.valueOf(this.phaseStatusNo));
		try {
			resultBean = phaseService.modifyPhaseStatus(phaseType, phaseNo,
					phaseStatus);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			rs.put("msg", "api调用异常，请联系技术人员!原因:" + e.getMessage());
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}

		if (resultBean.isResult()) {
			rs.put("msg", "更新彩期状态成功");
		} else {
			rs.put("msg", "更新彩期状态失败请重试");
			logger.error("更新彩期状态失败!code:{},msg:{}", resultBean.getCode(),
					resultBean.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更新彩期状态结束");
		return null;
	}

	// ajax修改彩期是否为当前期
	public String setPhaseCurrent() {
		logger.info("进入设置为当前期");
		ResultBean resultBean = null;
		JSONObject rs = new JSONObject();
		try {
			resultBean = phaseService.setPhaseCurrent(phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			rs.put("msg", "api调用异常，请联系技术人员!原因:" + e.getMessage());
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}

		if (resultBean.isResult()) {
			rs.put("msg", "彩期当前期设置成功");
		} else {
			rs.put("msg", "彩期当前期设置失败!请重试");
			logger.error("彩期当前期设置失败!code:{},msg:{}", resultBean.getCode(),
					resultBean.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("设置为当前期结束");
		return null;
	}

	// AJAX修改单条单场信息
	public String updateSfggRace() {
		logger.info("进入更新单场信息");
		boolean b = false;

		// 更新结果Sp数据
		try {
			if ("sp".equals(isSpOrRace) && sfggRace != null) {
				b = sfggRaceService.updateSfggRaceSp(sfggRace);
			} else if ("sfggRace".equals(isSpOrRace) && sfggRace != null) {
				b = sfggRaceService.updateSfggRace(sfggRace);
			}
		} catch (Exception e) {
			logger.error("更新单场数据失败!{}", e.getMessage());
		}

		JSONObject rs = new JSONObject();
		if (b) {
			rs.put("msg", "更新成功");
		} else {
			rs.put("msg", "更新失败请重试");
			logger.error("更新单场数据，更新失败");
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更新单场信息结束");
		return null;
	}

	// 审核单条单场信息 add by lm
	public String checkSfggRace() {
		boolean b = false;
		// 更新结果Sp数据
		try {
			if ("sp".equals(isSpOrRace) && sfggRace != null) {
				b = sfggRaceService.updateSfggRaceSp(sfggRace);
			} else if ("sfggRace".equals(isSpOrRace) && sfggRace != null) {
				b = sfggRaceService.updateSfggRace(sfggRace);
			}
			// 状态设为已开奖
			DcRaceStatus sfggRaceStatus = DcRaceStatus.getItem(statusValue);
			if (sfggRaceStatus != null && sfggRaceStatus.getValue() == DcRaceStatus.STOP_BUY.getValue()) {//只有在比赛状态未已关闭时才更新为已开奖
				sfggRaceService.updateStatus(sfggRace.getId(), DcRaceStatus.OPEN_BUY);
			}

		} catch (Exception e) {
			logger.error("更新单场状态数据失败!{}", e.getMessage());
		}

		JSONObject rs = new JSONObject();
		if (b) {
			rs.put("msg", "更新成功");
		} else {
			rs.put("msg", "更新失败请重试");
			logger.error("更新北京单场状态数据失败!");
		}
		writeRs(ServletActionContext.getResponse(), rs);
		return null;
	}

	// 重算截止
	@SuppressWarnings("unchecked")
	public String resetMatch() {
		logger.info("进入重算截止,phaseNo:{},matchNum:{}", phaseNo, matchNum);
		JSONObject json = new JSONObject();
		int rc = 0;
		String message = "操作成功";

		if (phaseNo == null || "".equals(phaseNo)) {
			logger.error("[重算截止]phaseNo参数为空");
			super.setErrorMessage("[重算截止]phaseNo参数不能为空");
			rc = 1;
			message = "[重算截止]phaseNo参数不能为空";
			json.put("code", rc);
			json.put("message", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}

		if (matchNum == null || "".equals(matchNum)) {
			logger.error("[重算截止]matchNum参数为空");
			super.setErrorMessage("[重算截止]matchNum参数不能为空");
			rc = 1;
			message = "[重算截止]matchNum参数不能为空";
			json.put("code", rc);
			json.put("message", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}

		List<String> planIds = new ArrayList<String>();

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
						phaseNo, planStatusList, pageBean);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
			}
			if (tmpPlanList == null || tmpPlanList.size() == 0) {
				logger.error("[重算截止]查询方案结果为空,phaseNo:{}", phaseNo);
				break;
			}
			for (Plan p : tmpPlanList) {
				if (p.getMatchNums() == null || "".equals(p.getMatchNums())) {
					logger.error("[重算截止]matchNums字段为空,plan_id:{}", p.getId());
					continue;
				}

				JSONArray jsonArray = null;
				try {
					jsonArray = JSONArray.fromObject(p.getMatchNums());
				} catch (Exception e) {
					logger.error("[重算截止]matchNums转换成jsonArray错误, matchNums:{}", p.getMatchNums(), e);
					super.setErrorMessage("[重算截止]matchNums转换成jsonArray错误, matchNums " + p.getMatchNums());
					rc = 1;
					message = "[重算截止]matchNums转换成jsonArray错误";
					json.put("code", rc);
					json.put("message", message);
					writeRs(ServletActionContext.getResponse(), json);
					return Action.NONE;
				}

				if (jsonArray != null && !jsonArray.isEmpty()) {
					for (Iterator iterator = jsonArray.iterator(); iterator
							.hasNext();) {
						String planMatchNum = (String) iterator.next();
						if (planMatchNum.equals(matchNum)) {
							logger.info("[重算截止]查询到匹配的matchNum,方案编码:{}", p.getId());
							planIds.add(p.getId());
							break;
						}
					}
				}
			}

			if (tmpPlanList.size() < pageBean.getPageSize()) {
				logger.info("[重算截止]当前查询结果小于每页条数，说明已经到最后一页，则跳出循环");
				break;
			}
			i++;
		}

		if (planIds == null || planIds.size() == 0) {
			logger.info("[重算截止]没有方案包含matchNum:{}", matchNum);
			super.setSuccessMessage("没有方案包含matchNum:" + matchNum
					+ "，无需重新计算合买截止");
			rc = 1;
			message = "[重算截止]没有方案包含" + matchNum + "，无需重新计算合买截止";
			json.put("code", rc);
			json.put("message", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}

		List<String> changedList = new ArrayList<String>();
		List<String> noChangedList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		try {
			lotteryPlanService.resetMatchByPlanId(planIds, changedList, noChangedList,
					failureList);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因" + e.getMessage());
			rc = 1;
			message = "计算截止失败";
			json.put("code", rc);
			json.put("message", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			rc = 1;
			message = "计算截止失败";
			json.put("code", rc);
			json.put("message", message);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}
		super.setSuccessMessage("重算截止:已修改方案"
				+ changedList.size()
				+ "个,未修改方案"
				+ noChangedList.size()
				+ "个,失败方案"
				+ failureList.size()
				+ "个"
				+ (failureList.size() > 0 ? ",编码:"
						+ CoreStringUtils.join(failureList, ",") : ""));
		logger.info("结束重算截止,phaseNo:{},matchNum:{}", phaseNo, matchNum);
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
		json.put("message", message);
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("重算截止结束");
		return Action.NONE;
	}
	
	// 退票
	public String returnTicket() {
		logger.info("进入退票,phaseNo:{},matchNum:{}", phaseNo, matchNum);
		JSONObject json = new JSONObject();
		int rc = 0;
		String msg = "操作成功";

		if (phaseNo == null || "".equals(phaseNo)) {
			logger.error("[退票]phaseNo参数为空");
			super.setErrorMessage("[退票]phaseNo参数不能为空");
			rc = 1;
			msg = "[退票]phaseNo参数不能为空";
			json.put("code", rc);
			json.put("message", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}

		if (matchNum == null || "".equals(matchNum)) {
			logger.error("[退票]matchNum参数为空");
			super.setErrorMessage("[退票]matchNum参数不能为空");
			rc = 1;
			msg = "[退票]matchNum参数不能为空";
			json.put("code", rc);
			json.put("message", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}


		logger.info("[退票]方案状态为：招募中和未支付,待出票,正在出票");
		List<PlanStatus> planStatusList = new ArrayList<PlanStatus>();
		planStatusList.add(PlanStatus.RECRUITING);
		planStatusList.add(PlanStatus.PRINT_WAITING);
		planStatusList.add(PlanStatus.PAID_NOT);
		planStatusList.add(PlanStatus.PRINTING);

		PageBean pageBean = super.getPageBean();
		pageBean.setPageSize(PLAN_COUNT_PER_QUERY);// 每次查询条数
		
		List<Plan> plans = new ArrayList<Plan>();

        int i = 1;
        while (true) {
            List<Plan> tmpPlanList = null;
            pageBean.setPage(i);
            try {
                tmpPlanList = lotteryPlanService.findByPhaseType(phaseType,
                        phaseNo, planStatusList, pageBean);
            } catch (ApiRemoteCallFailedException e) {
                logger.error(e.getMessage(), e);
            }
            if (tmpPlanList == null || tmpPlanList.isEmpty()) {
                logger.error("[退票]查询方案结果为空,phaseNo:{}", phaseNo);
                break;
            }

            plans.addAll(tmpPlanList);

            if (tmpPlanList.size() < pageBean.getPageSize()) {
                logger.info("[退票]当前查询结果小于每页条数，说明已经到最后一页，则跳出循环");
                break;
            }

            i++;
        }

        List<String> successList = new ArrayList<String>();
        List<String> failureList = new ArrayList<String>();
        List<String> nochangeList = new ArrayList<String>();

        try {
            lotteryPlanService.returnPlanTicket(plans, matchNum, successList, failureList, nochangeList);
        } catch (ApiRemoteCallFailedException e) {
            logger.error(e.getMessage(), e);
            super.setErrorMessage(e.getMessage());
            rc = 1;
            msg = e.getMessage();
            json.put("code", rc);
            json.put("message", msg);
            writeRs(ServletActionContext.getResponse(), json);
            return Action.NONE;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            super.setErrorMessage(e.getMessage());
            rc = 1;
            msg = e.getMessage();
            json.put("code", rc);
            json.put("message", msg);
            writeRs(ServletActionContext.getResponse(), json);
            return Action.NONE;
        }

        StringBuffer message = new StringBuffer();
        message.append("退票:已成功方案[").append(successList.size())
                .append("]个;\n失败方案[").append(failureList.size()).append("]个;");
        if (!failureList.isEmpty()) {
            message.append("\n失败方案的编号:").append(StringUtils.join(failureList, ",\n"));
        }

        super.setSuccessMessage(message.toString());
        logger.info("结束退票,phaseNo:{},matchNum:{}", phaseNo, matchNum);
        rc = 0;
        msg = message.toString();
		
		String template = "";
		path = webroot + path + DC_RETURN_TICKET_TEMPLATE;
		if (path == null || path.equals("")) {
			return null;
		}
		try {
			template = CoreFileUtils.readFile(path, CharsetConstant.CHARSET_UTF8);
			if (template == null || template.equals("")) {
				logger.error("发送短信,读取短信模板为空");
				super.setErrorMessage("发送短信,读取短信模板为空");
				msg = msg + "发送短信,读取短信模板为空";
				json.put("code", rc);
				json.put("message", msg);
				writeRs(ServletActionContext.getResponse(), json);
				return Action.NONE;
			}
		} catch (Exception e) {
			logger.error("发送短信,读取短信模板失败path={}", path);
			logger.error(e.getMessage(), e);
			super.setErrorMessage("发送短信,读取短信模板失败path=" + path);
			msg = msg + "发送短信,读取短信模板失败path=" + path;
			json.put("code", rc);
			json.put("message", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}
		
		List<String> failureSmsList = new ArrayList<String>();
		
		for (Plan p: plans) {
			if (successList.contains(p.getId())) {
				if (p.getPlanStatus().getValue() != PlanStatus.NOTPAID_OBSOLETING.getValue()) {
					Member m = null;
					try {
						m = memberService.get(p.getUid());
					} catch (ApiRemoteCallFailedException e) {
						logger.error("plan_id={},uid={}会员查询，API调用失败" + e.getMessage(), p.getId(), p.getUid());
						super.setErrorMessage("会员查询，API调用失败");
						failureSmsList.add("方案编号：" + p.getId() + "用户id：" + p.getUid());
						continue;
					}
					if (m == null) {
						logger.error("plan_id={},uid={}会员查询，结果为空", p.getId(), p.getUid());
						super.setErrorMessage("会员查询，结果为空");
						failureSmsList.add("方案编号：" + p.getId() + "用户id：" + p.getUid());
						continue;
					}
					if (m.getPhone() == null || "".equals(m.getPhone())) {
						logger.error("plan_id={},uid={}会员查询，会员电话号码为空", p.getId(), p.getUid());
						super.setErrorMessage("会员查询，会员电话号码为空");
						failureSmsList.add("方案编号：" + p.getId() + "用户id：" + p.getUid());
						continue;
					}
					Sms sms = new Sms();
					sms.setSmsTo(m.getUsername());
					sms.setStatus(StatusType.WAITINGTYPE);
					
					String content = "";
					String[] searchList = new String[]{"%PLAN_TYPE%","%PLAN_ID%", "%PLAN_STATUS%"};
					String[] replacementList = new String[3];
					replacementList[0] = p.getPlanType().getName();
					replacementList[1] = p.getId();
					replacementList[2] = p.getPlanStatus().getName().replace("返款中", "");
					content = StringUtils.replaceEachRepeatedly(template, searchList, replacementList);
					if (content == null || "".equals(content)) {
						logger.error("发送短信,短信内容获取失败");
						super.setErrorMessage("发送短信,短信内容获取失败");
						failureSmsList.add("方案编号：" + p.getId() + "用户id：" + p.getUid());
						continue;
					}

					sms.setContent(content);
					List<Sms> smses = smsService.manage(sms);
					
					Sms smsResult = smses.get(0);
					
					SmsQueueTask task = new SmsQueueTask();
					task.addReceiver(m.getPhone());
					task.setContent(smsResult.getContent());
					task.setCallback(callbackUrl + "sms.id=" + smsResult.getId());
					int code = smsQueueTaskService.postToQueue(task);
					
					if (code == QueueConstant.RC_SUCCESS) {
						smsResult.setStatus(StatusType.SENDINGTYPE);
						smsService.update(smsResult);
					} else {
						logger.error("发送短信,放入队列任务失败");
						super.setErrorMessage("发送短信,放入队列任务失败");
						failureSmsList.add("方案编号：" + p.getId() + "用户id：" + p.getUid());
						continue;
					}
				}
			}
		}
		
		msg = msg + "\n成功退票方案已发送信息，请到短信管理页面查看信息发送结果";
		if (failureSmsList != null && failureSmsList.size() > 0) {
			msg = msg + "\n失败短信列表\n";
			for (String str : failureSmsList) {
				msg = msg + str + "\n";
			}
		}
		
		json.put("code", rc);
		json.put("message", msg);
		writeRs(ServletActionContext.getResponse(), json);
		return Action.NONE;
	}

	public String updateStatus() {
		logger.info("进入修改单场状态");
		JSONObject json = new JSONObject();
		int rc = 0;
		String message = "操作成功";
		if (tag.equals("1")) {// 停止销售
			sfggRaceService.updateStatus(sfggRace.getId(), DcRaceStatus.STOP_BUY);
		} else if (tag.equals("2")) { // 解除停止
			sfggRaceService.updateStatus(sfggRace.getId(), DcRaceStatus.CAN_BUY);
		} else if (tag.equals("3")) { // 恢复未销售
			sfggRaceService.updateStatus(sfggRace.getId(), DcRaceStatus.NO_BUY);
		}
		json.put("code", rc);
		json.put("message", message);
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("修改单场状态结束");
		return Action.NONE;
	}

	// 通过excel获取单场赛程 add by liurd
	@SuppressWarnings("unchecked")
	public String getExcelSfggRaces() {
		logger.info("进入转换excel数据");
		try {
			this.phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType,
					phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("获取彩期数据失败，请确认彩期已存在，" + e.getMessage());
			return "failure";
		}
		if (this.getPhase() == null) {
			logger.error("获取彩期数据为null，请确认彩期已存在");
			super.setErrorMessage("获取彩期数据为null，请确认彩期已存在");
			return "failure";
		}
		
		try {
			searchPhaseList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		//解析Excel文件 返回List包含对阵情况rtnList.get(0)...List<?>和错误信息rtnList.get(1)...String
		List<Object> rtnList = sfggRaceService.getSfggRaceByExcel(excelFile, phase, phaseNo);
		
		if (rtnList == null || rtnList.size() == 0) {
			logger.error("赛事数据为空");
			super.setErrorMessage("未获取取到赛事数据，请确认后重试或请尝试其它方式");
			return "failure";
		}
		
		if (rtnList.get(0) instanceof List<?>) {
			sfggRaces = (List<SfggRace>) rtnList.get(0);
		}
		if (rtnList.get(1) instanceof String) {
			strError =  (String) rtnList.get(1);
		}
		
		if (sfggRaces == null) {
			logger.error("赛事数据为空");
			super.setErrorMessage("未获取取到赛事数据，请确认后重试或请尝试其它方式");
			return "failure";
		}
		
		initStatus(sfggRaces);
		logger.info("转换excel数据结束");

		return "list";
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

	public List<SfggRace> getSfggRaces() {
		return sfggRaces;
	}

	public void setSfggRaces(List<SfggRace> sfggRaces) {
		this.sfggRaces = sfggRaces;
	}

	public SfggRaceService getSfggRaceService() {
		return sfggRaceService;
	}

	public void setSfggRaceService(SfggRaceService sfggRaceService) {
		this.sfggRaceService = sfggRaceService;
	}

	public int getFetcherType() {
		return fetcherType;
	}

	public void setFetcherType(int fetcherType) {
		this.fetcherType = fetcherType;
	}

	public List<FetcherType> getFetchers() {
		List<FetcherType> list = new ArrayList<FetcherType>();
		list.add(FetcherType.T_DEFAULT);
		list.add(FetcherType.T_PENGINEAPI);
		return list;
	}

	public String getPageResult() {
		return pageResult;
	}

	public void setPageResult(String pageResult) {
		this.pageResult = pageResult;
	}

	public String getIsSpOrRace() {
		return isSpOrRace;
	}

	public void setIsSpOrRace(String isSpOrRace) {
		this.isSpOrRace = isSpOrRace;
	}

	public SfggRace getSfggRace() {
		return sfggRace;
	}

	public void setSfggRace(SfggRace sfggRace) {
		this.sfggRace = sfggRace;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<PhaseStatus> getPhaseStatusList() {
		return phaseStatusList;
	}

	public void setPhaseStatusList(List<PhaseStatus> phaseStatusList) {
		this.phaseStatusList = phaseStatusList;
	}

	public String getPhaseStatusNo() {
		return phaseStatusNo;
	}

	public void setPhaseStatusNo(String phaseStatusNo) {
		this.phaseStatusNo = phaseStatusNo;
	}

	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public String getStrError() {
		return strError;
	}

	public void setStrError(String strError) {
		this.strError = strError;
	}

	public DcRaceStatus getStopBuyStatus() {
		return DcRaceStatus.STOP_BUY;
	}
	
	public DcRaceStatus getNoBuyStatus() {
		return DcRaceStatus.NO_BUY;
	}
	
	public DcRaceStatus getCanBuyStatus() {
		return DcRaceStatus.CAN_BUY;
	}

	public DcRaceStatus getOpenBuyStatus() {
		return DcRaceStatus.OPEN_BUY;
	}

	public DcRaceStatus getReturnPrizeStatus() {
		return DcRaceStatus.RETURN_PRIZE;
	}

	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}

	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}

	public String getMatchNum() {
		return matchNum;
	}

	public void setMatchNum(String matchNum) {
		this.matchNum = matchNum;
	}

	public int getStatusValue() {
		return statusValue;
	}

	public void setStatusValue(int statusValue) {
		this.statusValue = statusValue;
	}

	public int getFetcherTypeCompare() {
		return fetcherTypeCompare;
	}

	public void setFetcherTypeCompare(int fetcherTypeCompare) {
		this.fetcherTypeCompare = fetcherTypeCompare;
	}

	public String getErrorStr() {
		if (errorStr == null) {
			return "";
		}
		return errorStr;
	}

	public void setErrorStr(String errorStr) {
		this.errorStr = errorStr;
	}

	public List<SfggRace> getCompareRaces() {
		return compareRaces;
	}

	public void setCompareRaces(List<SfggRace> compareRaces) {
		this.compareRaces = compareRaces;
	}

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public QueueTaskService getSmsQueueTaskService() {
		return smsQueueTaskService;
	}

	public void setSmsQueueTaskService(QueueTaskService smsQueueTaskService) {
		this.smsQueueTaskService = smsQueueTaskService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getWebroot() {
		return webroot;
	}

	public void setWebroot(String webroot) {
		this.webroot = webroot;
	}

	public AliasService getAliasService() {
		return aliasService;
	}

	public void setAliasService(AliasService aliasService) {
		this.aliasService = aliasService;
	}
}
