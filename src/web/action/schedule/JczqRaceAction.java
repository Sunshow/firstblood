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
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.lottery.LotteryTicketConfigService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.admin.web.service.ticket.TicketService;
import com.lehecai.core.EnabledStatus;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.FetchFailedException;
import com.lehecai.core.lottery.*;
import com.lehecai.core.lottery.cache.JczqLottery;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.jczq.IJczqScheduleFetcher;
import com.lehecai.core.lottery.fetcher.jczq.JczqScheduleItem;
import com.lehecai.core.lottery.fetcher.jczq.impl.CommonJczqScheduleFetcher;
import com.lehecai.core.queue.QueueConstant;
import com.lehecai.core.queue.QueueTaskService;
import com.lehecai.core.queue.sms.SmsQueueTask;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreFileUtils;
import com.lehecai.core.util.CoreStringUtils;
import com.lehecai.core.util.lottery.JczqUtil;
import com.lehecai.engine.entity.lottery.LotteryTicketConfig;
import com.lehecai.engine.entity.terminal.Terminal;
import com.opensymphony.xwork2.Action;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class JczqRaceAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final String PHASE_FORMAT_DATE = "yyyyMMdd";
	
	private static final int PLAN_COUNT_PER_QUERY = 200;
	private static final String JCZQ_RETURN_TICKET_TEMPLATE = "return_ticket_sms.html";
	
	private AliasService aliasService;

	private JczqRaceService jczqRaceService;
	private TicketService ticketService;
	
	private PhaseService phaseService;
	private MemberService memberService;
	private SmsService smsService;
	private QueueTaskService smsQueueTaskService;
	private LotteryPlanService lotteryPlanService;
	private LotteryTicketConfigService lotteryTicketConfigService;
	private TerminalService terminalService;
	
	private String callbackUrl;
	private String path;
	private String webroot;
	private String terminalId;
	private int fetcherType;
	private JczqRace race;
	private List<Terminal> terminalList;
	private List<JczqRace> races;
	
	//private Date fetchTime;
	
	private int tag;
	
	private Integer staticDrawStatusValue;		//固定奖金开奖状态，用于表单提交
	private Integer dynamicDrawStatusValue;	    //浮动奖金开奖状态，用于表单提交
	private Integer staticSaleSpfWrqStatusValue;//固定奖金胜平负玩法销售状态，用于表单提交
	private Integer dynamicSaleSpfWrqStatusValue;//浮动奖金胜平负玩法销售状态，用于表单提交
	private Integer staticSaleSpfStatusValue;	//固定奖金让球胜平负玩法销售状态，用于表单提交
	private Integer dynamicSaleSpfStatusValue;	//浮动奖金让球胜平负玩法销售状态，用于表单提交
	private Integer staticSaleBfStatusValue;	//固定奖金全场比分玩法销售状态，用于表单提交
	private Integer dynamicSaleBfStatusValue;   //浮动奖金全场比分玩法销售状态，用于表单提交
	private Integer staticSaleJqsStatusValue;	//固定奖金进球总数玩法销售状态，用于表单提交
	private Integer dynamicSaleJqsStatusValue;	//浮动奖金进球总数玩法销售状态，用于表单提交
	private Integer staticSaleBqcStatusValue;	//固定奖金半全场胜平负玩法销售状态，用于表单提交
	private Integer dynamicSaleBqcStatusValue;	//浮动奖金半全场胜平负玩法销售状态，用于表单提交
	
	private List<Integer> staticSaleSpfWrqStatusValues;	//固定奖金胜平负玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleSpfWrqStatusValues;//浮动奖金胜平负玩法销售状态，用于表单提交
	private List<Integer> staticSaleSpfStatusValues;	//固定奖金让球胜平负玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleSpfStatusValues;	//浮动奖金让球胜平负玩法销售状态，用于表单提交
	private List<Integer> staticSaleBfStatusValues;	    //固定奖金全场比分玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleBfStatusValues;    //浮动奖金全场比分玩法销售状态，用于表单提交
	private List<Integer> staticSaleJqsStatusValues;	//固定奖金进球总数玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleJqsStatusValues;	//浮动奖金进球总数玩法销售状态，用于表单提交
	private List<Integer> staticSaleBqcStatusValues;	//固定奖金半全场胜平负玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleBqcStatusValues;	//浮动奖金半全场胜平负玩法销售状态，用于表单提交
	
	private List<Integer> statusValues;
	private List<Integer> staticDrawStatusValues;
	private List<Integer> dynamicDrawStatusValues;
	
	private String phaseNo;
	
	public String handle() {
		logger.info("进入查询竞彩足球对阵信息");
		try {
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("查询竞彩足球对阵信息结束");
		return "list";
	}
	
	private void convertRaces() {
		staticSaleSpfWrqStatusValues = new ArrayList<Integer>();
		dynamicSaleSpfWrqStatusValues = new ArrayList<Integer>();
		staticSaleSpfStatusValues = new ArrayList<Integer>();
		dynamicSaleSpfStatusValues = new ArrayList<Integer>();
		staticSaleBfStatusValues = new ArrayList<Integer>();
		dynamicSaleBfStatusValues = new ArrayList<Integer>();
		staticSaleJqsStatusValues = new ArrayList<Integer>();
		dynamicSaleJqsStatusValues = new ArrayList<Integer>();
		staticSaleBqcStatusValues = new ArrayList<Integer>();
		dynamicSaleBqcStatusValues = new ArrayList<Integer>();
		
		if (races != null && races.size() > 0) {
			for (JczqRace r : races) {
				staticSaleSpfWrqStatusValues.add(r.getStaticSaleSpfWrqStatus() == null ? JczqStaticSaleStatus.SALE_UNOPEN.getValue() : r.getStaticSaleSpfWrqStatus().getValue());
				dynamicSaleSpfWrqStatusValues.add(r.getDynamicSaleSpfWrqStatus() == null ? JczqDynamicSaleStatus.SALE_UNOPEN.getValue() : r.getDynamicSaleSpfWrqStatus().getValue());
				staticSaleSpfStatusValues.add(r.getStaticSaleSpfStatus().getValue());
				dynamicSaleSpfStatusValues.add(r.getDynamicSaleSpfStatus().getValue());
				staticSaleBfStatusValues.add(r.getStaticSaleBfStatus().getValue());
				dynamicSaleBfStatusValues.add(r.getDynamicSaleBfStatus().getValue());
				staticSaleJqsStatusValues.add(r.getStaticSaleJqsStatus().getValue());
				dynamicSaleJqsStatusValues.add(r.getDynamicSaleJqsStatus().getValue());
				staticSaleBqcStatusValues.add(r.getStaticSaleBqcStatus().getValue());
				dynamicSaleBqcStatusValues.add(r.getDynamicSaleBqcStatus().getValue());
			}
		}
	}
	
	public String fetchRaceList() {
		logger.info("进入获取竞彩足球比赛数据");
		try {
			Date phaseDate = CoreDateUtils.parseDate(this.getPhaseNo(), PHASE_FORMAT_DATE);
			//if (CoreDateUtils.formatDate(phaseDate).equals(CoreDateUtils.formatDate(new Date()))) {
				IJczqScheduleFetcher fetcher = new CommonJczqScheduleFetcher();

				if (StringUtils.isEmpty(terminalId)) {
					terminalId = null;
				}
                // 获取所有开售赛程
				List<JczqScheduleItem> scheduleItems = fetcher.fetch(null, FetcherType.T_OFFICIAL, terminalId);
				
				if (scheduleItems == null || scheduleItems.isEmpty()) {
					logger.error("从官方抓取赛程数据结果为空, phaseNo={}",phaseNo);
					throw new RuntimeException("从官方抓取赛程数据结果为空");
				}
				
				Set<String> phaseNoSet = new HashSet<String>();
				for (JczqScheduleItem item : scheduleItems) {
					String phaseNo = CoreDateUtils.formatDate(item.getOfficialDate(), PHASE_FORMAT_DATE);
					phaseNoSet.add(phaseNo);
				}
				
				List<JczqScheduleItem> scheduleApiItems = null;
				try {
					scheduleApiItems = fetcher.fetch(null, FetcherType.T_PENGINEAPI, terminalId);
				} catch (FetchFailedException e) {
					logger.error("从出票商抓取赛程数据结果为空, 请联系出票商, 只显示官方赛程, phaseNo={}", phaseNo);
                    super.setErrorMessage("从出票商抓取赛程数据结果为空, 请联系出票商, 只显示官方赛程");
					//throw new RuntimeException("从出票商抓取赛程数据结果为空");
				}
				
				List<JczqRace> jczqRaces = new ArrayList<JczqRace>();
				try {
					for (String phaseNo : phaseNoSet) {
						List<JczqRace> fetchDateRaces = jczqRaceService.getRaceListByDateAndStatus(phaseNo, null, false);
						if (fetchDateRaces != null) {
							jczqRaces.addAll(fetchDateRaces);
						}
					}
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
					throw e;
				}
				
				List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
				races = new ArrayList<JczqRace>();

				for (JczqScheduleItem scheduleItem : scheduleItems) {
					if (scheduleItem.getMatchDate().before(phaseDate)) {
						// 忽略掉在抓取时间之前就已经开赛的比赛
						continue;
					}
					
					JczqRace race  = new JczqRace();
					AliasMatchBean match = new AliasMatchBean();
					race.setMatchNum(scheduleItem.getMatchNum());
					race.setPhase(CoreDateUtils.formatDate(scheduleItem.getOfficialDate(), PHASE_FORMAT_DATE));
					race.setOfficialNum(scheduleItem.getOfficialNum());
					race.setOfficialDate(scheduleItem.getOfficialDate());
					if (race.getOfficialDate() != null) {
						Calendar cd = Calendar.getInstance();
						cd.setTime(race.getOfficialDate());
						race.setOfficialWeekDay(JczqRace.WEEKSTR.get(cd.get(Calendar.DAY_OF_WEEK)));
					}
					race.setMatchName(scheduleItem.getMatchName());
					match.setLeagueLongName(scheduleItem.getMatchName());
					//languageName.add(scheduleItem.getMatchName());
					//race.setMatchName(aliasService.getAlias(AliasDataProvider.FOOTBALL310, AliasDataType.LEAGUE, AliasExtType.SHORT_NAME, scheduleItem.getMatchName()));
					race.setMatchDate(scheduleItem.getMatchDate());
					match.setMatchTime(CoreDateUtils.formatDate(scheduleItem.getMatchDate(), CoreDateUtils.DATETIME));
					long endSaleForward = 0;
					LotteryTicketConfig config = lotteryTicketConfigService.get(LotteryType.JCZQ_SPF);
					if (config != null && config.getEndSaleForward() != null) {
						endSaleForward = config.getEndSaleForward();
					}
					
					Date endSaleTime = JczqUtil.getEndSaleTimeByMatchDate(scheduleItem.getMatchDate(), endSaleForward);
					race.setEndSaleTime(endSaleTime);
					race.setStatus(JczqRaceStatus.UNOPEN);
					
					race.setHomeTeam(scheduleItem.getHomeTeam());
					match.setHomeTeamLongName(scheduleItem.getHomeTeam());
					//homeTeamName.add(scheduleItem.getHomeTeam());
					race.setAwayTeam(scheduleItem.getAwayTeam());
					match.setAwayTeamLongName(scheduleItem.getAwayTeam());
					//awayTeamName.add(scheduleItem.getAwayTeam());
					race.setStaticDrawStatus(JczqStaticDrawStatus.UNOPEN);
					race.setDynamicDrawStatus(JczqDynamicDrawStatus.UNOPEN);
					
					race.setHandicap(scheduleItem.getHandicap());
					race.setStaticSaleSpfWrqStatus(scheduleItem.getStaticSaleSpfWrqStatus());
					race.setDynamicSaleSpfWrqStatus(scheduleItem.getDynamicSaleSpfWrqStatus());
					race.setStaticSaleSpfStatus(scheduleItem.getStaticSaleSpfStatus());
					race.setDynamicSaleSpfStatus(scheduleItem.getDynamicSaleSpfStatus());
					race.setStaticSaleBfStatus(scheduleItem.getStaticSaleBfStatus());
					race.setDynamicSaleBfStatus(scheduleItem.getDynamicSaleBfStatus());
					race.setStaticSaleJqsStatus(scheduleItem.getStaticSaleJqsStatus());
					race.setDynamicSaleJqsStatus(scheduleItem.getDynamicSaleJqsStatus());
					race.setStaticSaleBqcStatus(scheduleItem.getStaticSaleBqcStatus());
					race.setDynamicSaleBqcStatus(scheduleItem.getDynamicSaleBqcStatus());
					
					if (jczqRaces != null && jczqRaces.size() > 0) {
						for (JczqRace r : jczqRaces) {
							if (scheduleItem.getMatchNum().equals(r.getMatchNum())) {
								race.setHandicap(r.getHandicap() == null || r.getHandicap().equals("") ? scheduleItem.getHandicap() : r.getHandicap());
								race.setStaticSaleSpfWrqStatus(r.getStaticSaleSpfWrqStatus() == null ? scheduleItem.getStaticSaleSpfWrqStatus() : r.getStaticSaleSpfWrqStatus());
								race.setDynamicSaleSpfWrqStatus(r.getDynamicSaleSpfWrqStatus() == null ? scheduleItem.getDynamicSaleSpfWrqStatus() : r.getDynamicSaleSpfWrqStatus());
								race.setStaticSaleSpfStatus(r.getStaticSaleSpfStatus() == null ? scheduleItem.getStaticSaleSpfStatus() : r.getStaticSaleSpfStatus());
								race.setDynamicSaleSpfStatus(r.getDynamicSaleSpfStatus() == null ? scheduleItem.getDynamicSaleSpfStatus() : r.getDynamicSaleSpfStatus());
								race.setStaticSaleBfStatus(r.getStaticSaleBfStatus() == null ? scheduleItem.getStaticSaleBfStatus() : r.getStaticSaleBfStatus());
								race.setDynamicSaleBfStatus(r.getDynamicSaleBfStatus() == null ? scheduleItem.getDynamicSaleBfStatus() : r.getDynamicSaleBfStatus());
								race.setStaticSaleJqsStatus(r.getStaticSaleJqsStatus() == null ? scheduleItem.getStaticSaleJqsStatus() : r.getStaticSaleJqsStatus());
								race.setDynamicSaleJqsStatus(r.getDynamicSaleJqsStatus() == null ? scheduleItem.getDynamicSaleJqsStatus() : r.getDynamicSaleJqsStatus());
								race.setStaticSaleBqcStatus(r.getStaticSaleBqcStatus() == null ? scheduleItem.getStaticSaleBqcStatus() : r.getStaticSaleBqcStatus());
								race.setDynamicSaleBqcStatus(r.getDynamicSaleBqcStatus() == null ? scheduleItem.getDynamicSaleBqcStatus() : r.getDynamicSaleBqcStatus());
								race.setFxId(r.getFxId());
								race.setPriority(r.getPriority());
								match.setMatchId(r.getFxId());
								break;
							}
						}
					}
					
					if (scheduleApiItems != null && scheduleApiItems.size() > 0) {
						for (JczqScheduleItem scheduleApiItem : scheduleApiItems) {
							if (scheduleItem.getMatchNum().equals(scheduleApiItem.getMatchNum())) {
								if (race.getHandicap() == null || race.getHandicap().equals("")) {
									race.setHandicap(scheduleApiItem.getHandicap() == null || scheduleApiItem.getHandicap().equals("") ? scheduleItem.getHandicap() : scheduleApiItem.getHandicap());
								}
								race.setStaticSaleSpfWrqStatus(scheduleApiItem.getStaticSaleSpfWrqStatus() == null ? scheduleItem.getStaticSaleSpfWrqStatus() : scheduleApiItem.getStaticSaleSpfWrqStatus());
								race.setDynamicSaleSpfWrqStatus(scheduleApiItem.getDynamicSaleSpfWrqStatus() == null ? scheduleItem.getDynamicSaleSpfWrqStatus() : scheduleApiItem.getDynamicSaleSpfWrqStatus());
								race.setStaticSaleSpfStatus(scheduleApiItem.getStaticSaleSpfStatus() == null ? scheduleItem.getStaticSaleSpfStatus() : scheduleApiItem.getStaticSaleSpfStatus());
								race.setDynamicSaleSpfStatus(scheduleApiItem.getDynamicSaleSpfStatus() == null ? scheduleItem.getDynamicSaleSpfStatus() : scheduleApiItem.getDynamicSaleSpfStatus());
								race.setStaticSaleBfStatus(scheduleApiItem.getStaticSaleBfStatus() == null ? scheduleItem.getStaticSaleBfStatus() : scheduleApiItem.getStaticSaleBfStatus());
								race.setDynamicSaleBfStatus(scheduleApiItem.getDynamicSaleBfStatus() == null ? scheduleItem.getDynamicSaleBfStatus() : scheduleApiItem.getDynamicSaleBfStatus());
								race.setStaticSaleJqsStatus(scheduleApiItem.getStaticSaleJqsStatus() == null ? scheduleItem.getStaticSaleJqsStatus() : scheduleApiItem.getStaticSaleJqsStatus());
								race.setDynamicSaleJqsStatus(scheduleApiItem.getDynamicSaleJqsStatus() == null ? scheduleItem.getDynamicSaleJqsStatus() : scheduleApiItem.getDynamicSaleJqsStatus());
								race.setStaticSaleBqcStatus(scheduleApiItem.getStaticSaleBqcStatus() == null ? scheduleItem.getStaticSaleBqcStatus() : scheduleApiItem.getStaticSaleBqcStatus());
								race.setDynamicSaleBqcStatus(scheduleApiItem.getDynamicSaleBqcStatus() == null ? scheduleItem.getDynamicSaleBqcStatus() : scheduleApiItem.getDynamicSaleBqcStatus());
								break;
							}
						}
					}
					races.add(race);
					matchs.add(match);
				}
				
				try {
					if (matchs != null && matchs.size() > 0) {
						matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
						for (int i = 0 ;i < races.size() ; i++) {
							if (matchs.get(i) != null){
								if(matchs.get(i).getAwayTeamShortName() != null && !"".equals(matchs.get(i).getAwayTeamShortName())) {
									races.get(i).setAwayTeam(matchs.get(i).getAwayTeamShortName());
								}
								if (matchs.get(i).getHomeTeamShortName() != null && !"".equals(matchs.get(i).getHomeTeamShortName())) {
									races.get(i).setHomeTeam(matchs.get(i).getHomeTeamShortName());
								}
								if (matchs.get(i).getLeagueShortName() != null && !"".equals(matchs.get(i).getLeagueShortName())) {
									races.get(i).setMatchName(matchs.get(i).getLeagueShortName());
								}
								if (matchs.get(i).getMatchId() != null && matchs.get(i).getMatchId() != 0) {
									races.get(i).setFxId(matchs.get(i).getMatchId());
								} else {
									races.get(i).setFxId(0);
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("别名服务失败" + aliasService.getClass() + "getAlias" + "参数" + AliasDataProvider.PLOT);
				}
				convertRaces();
			//} else {
				//logger.error("赛程为空");
				//super.setErrorMessage("未抓取到彩期<" + this.getPhaseNo() + ">的赛程");
			//}
		} catch (Exception e) {
			logger.error("抓取赛程出错", e);
			super.setErrorMessage("抓取赛程出错：" + e.getMessage());
		}
		logger.info("获取竞彩足球比赛数据结束");
		return "fetchList";
	}
	
	//全部比赛匹配短名
	public String matchAliasAll() {
		logger.info("进入查询竞彩足球对阵信息");
		try {
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		if (races == null || races.size() == 0) {
			logger.error("赛程为空");
			super.setErrorMessage("赛程为空");
		} else {
			List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
			for (JczqRace jr : races) {
				AliasMatchBean match = new AliasMatchBean();
				match.setAwayTeamLongName(jr.getAwayTeam());
				match.setHomeTeamLongName(jr.getHomeTeam());
				match.setLeagueLongName(jr.getMatchName());
				match.setMatchTime(CoreDateUtils.formatDate(jr.getMatchDate(), CoreDateUtils.DATETIME));
				match.setMatchId(jr.getFxId());
				matchs.add(match);
			}
			try {
				if (matchs != null && matchs.size() > 0) {
					matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
					for (int i = 0 ;i < races.size() ; i++) {
						if (matchs.get(i) != null){
							if(matchs.get(i).getAwayTeamShortName() != null && !"".equals(matchs.get(i).getAwayTeamShortName())) {
								races.get(i).setAwayTeam(matchs.get(i).getAwayTeamShortName());
							}
							if (matchs.get(i).getHomeTeamShortName() != null && !"".equals(matchs.get(i).getHomeTeamShortName())) {
								races.get(i).setHomeTeam(matchs.get(i).getHomeTeamShortName());
							}
							if (matchs.get(i).getLeagueShortName() != null && !"".equals(matchs.get(i).getLeagueShortName())) {
								races.get(i).setMatchName(matchs.get(i).getLeagueShortName());
							}
							if (matchs.get(i).getMatchId() != null && matchs.get(i).getMatchId() != 0) {
								races.get(i).setFxId(matchs.get(i).getMatchId());
							} else {
								races.get(i).setFxId(0);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("别名服务失败" + aliasService.getClass() + "getAlias" + "参数" + AliasDataProvider.PLOT);
			}
			convertRaces();
		}
		logger.info("查询竞彩足球对阵信息结束");
		return "list";
	}
	
	//个别比赛匹配短名
	public String matchAlias() {
		
		logger.info("进入个别场次别名匹配");
		String msg = "";
		JSONObject rs = new JSONObject();
		if (race == null) {
			msg = "比赛信息为空";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		
		List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
		
		AliasMatchBean match = new AliasMatchBean();
		match.setLeagueLongName(race.getMatchName());
		match.setMatchTime(CoreDateUtils.formatDate(race.getMatchDate(), CoreDateUtils.DATETIME));
		match.setHomeTeamLongName(race.getHomeTeam());
		match.setAwayTeamLongName(race.getAwayTeam());
		match.setMatchId(race.getFxId());
		matchs.add(match);
		
		matchs = aliasService.getAliasFromMatchInfo(AliasDataProvider.PLOT, matchs);
		
		if (matchs != null && matchs.size() > 0) {
			match = matchs.get(0);
			if (match.getMatchId() != null && match.getMatchId() != 0) {
				msg = "匹配成功";
				race.setFxId(match.getMatchId());
			} else {
				msg = "匹配失败";
				rs.put("message", msg);
				writeRs(ServletActionContext.getResponse(), rs);
				return Action.NONE;
			}
			msg = msg + "\n主队短名：" + match.getHomeTeamShortName() + "\n客队短名：" + match.getAwayTeamShortName()
					+ "\n联赛名短名：" + match.getLeagueShortName() + "\n分析ID：" + match.getMatchId();
			race.setAwayTeam(match.getAwayTeamShortName());
			race.setHomeTeam(match.getHomeTeamShortName());
			race.setMatchName(match.getLeagueShortName());
			rs.put("message", msg);
			rs.put("race", JczqRace.toJSON(race));
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		} else {
			msg = "匹配失败";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
	}
	
	public String createRace() {
		logger.info("进入创建赛程");

        if (StringUtils.isBlank(race.getPhase())) {
            logger.error("未指定彩期或彩期为空");
            super.setErrorMessage("创建赛程时，必须指定彩期");
            return "list";
        }
		if (race.getMatchDate() == null) {
			logger.error("比赛时间为空");
			super.setErrorMessage("创建赛程时，比赛时间不能为空");
			return "list";
		}
		if (race.getOfficialNum() == null) {
			logger.error("官方编码为空");
			super.setErrorMessage("创建赛程时，官方编码不能为空");
			return "list";
		}
		String matchNum = race.getPhase() + LotteryConstant.JCZQ_MATCH_NUM_CODE_DEFAULT + race.getOfficialNum();
		try {
			JczqRace jczqRace = jczqRaceService.getRaceByMatchNum(matchNum);
			if (jczqRace != null && jczqRace.getMatchNum() != null) {
				super.setSuccessMessage(matchNum + "赛程已经存在,创建赛程失败");
				try {
					races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
					convertRaces();
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
				}
				return "list";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		race.setMatchNum(matchNum);
		race.setOfficialDate(CoreDateUtils.parseDate(race.getPhase(), PHASE_FORMAT_DATE));
		race.setStatus(JczqRaceStatus.UNOPEN);
		race.setStaticDrawStatus(JczqStaticDrawStatus.UNOPEN);
		race.setDynamicDrawStatus(JczqDynamicDrawStatus.UNOPEN);
		
		race.setStaticSaleSpfWrqStatus(JczqStaticSaleStatus.getItem(staticSaleSpfWrqStatusValue));
		race.setDynamicSaleSpfWrqStatus(JczqDynamicSaleStatus.getItem(dynamicSaleSpfWrqStatusValue));
		race.setStaticSaleSpfStatus(JczqStaticSaleStatus.getItem(staticSaleSpfStatusValue));
		race.setDynamicSaleSpfStatus(JczqDynamicSaleStatus.getItem(dynamicSaleSpfStatusValue));
		race.setStaticSaleBfStatus(JczqStaticSaleStatus.getItem(staticSaleBfStatusValue));
		race.setDynamicSaleBfStatus(JczqDynamicSaleStatus.getItem(dynamicSaleBfStatusValue));
		race.setStaticSaleJqsStatus(JczqStaticSaleStatus.getItem(staticSaleJqsStatusValue));
		race.setDynamicSaleJqsStatus(JczqDynamicSaleStatus.getItem(dynamicSaleJqsStatusValue));
		race.setStaticSaleBqcStatus(JczqStaticSaleStatus.getItem(staticSaleBqcStatusValue));
		race.setDynamicSaleBqcStatus(JczqDynamicSaleStatus.getItem(dynamicSaleBqcStatusValue));
		
		if (jczqRaceService.saveRace(race)) {
			logger.info("创建赛程成功");
			super.setSuccessMessage("创建赛程成功");
		} else {
			logger.error("创建赛程失败");
			super.setSuccessMessage("创建赛程失败");
		}
		try {
			this.setPhaseNo(race.getPhase());
			races = jczqRaceService.getRaceListByDateAndStatus(race.getPhase(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("创建赛程结束");
		return "list";
	}
	
	public String updateRace() {
		logger.info("进入修改赛程");
		race.setStaticSaleSpfWrqStatus(JczqStaticSaleStatus.getItem(staticSaleSpfWrqStatusValue));
		race.setDynamicSaleSpfWrqStatus(JczqDynamicSaleStatus.getItem(dynamicSaleSpfWrqStatusValue));
		race.setStaticSaleSpfStatus(JczqStaticSaleStatus.getItem(staticSaleSpfStatusValue));
		race.setDynamicSaleSpfStatus(JczqDynamicSaleStatus.getItem(dynamicSaleSpfStatusValue));
		race.setStaticSaleBfStatus(JczqStaticSaleStatus.getItem(staticSaleBfStatusValue));
		race.setDynamicSaleBfStatus(JczqDynamicSaleStatus.getItem(dynamicSaleBfStatusValue));
		race.setStaticSaleJqsStatus(JczqStaticSaleStatus.getItem(staticSaleJqsStatusValue));
		race.setDynamicSaleJqsStatus(JczqDynamicSaleStatus.getItem(dynamicSaleJqsStatusValue));
		race.setStaticSaleBqcStatus(JczqStaticSaleStatus.getItem(staticSaleBqcStatusValue));
		race.setDynamicSaleBqcStatus(JczqDynamicSaleStatus.getItem(dynamicSaleBqcStatusValue));
		
		boolean b =jczqRaceService.updateRace(race);

		JSONObject rs = new JSONObject();
		if (b) {
			rs.put("msg", "更新成功");
			logger.error("更新竞彩足球数据成功");
		} else {
			rs.put("msg", "更新失败，请重试");
			logger.error("更新竞彩足球数据失败");
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("修改赛程结束");
		return null;
	}
	
	// 重算截止
	public String resetMatch() {
		logger.info("进入重算截止");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		int rc = 0;
		if (race == null) {
			logger.error("[重算截止]race参数为空");
			super.setErrorMessage("[重算截止]race参数不能为空");
			rc = 1;
			json.put("code", rc);
			json.put("msg", "[重算截止]race参数不能为空");
			super.writeRs(response, json);
			return Action.NONE;
		}

		String matchNum = race.getMatchNum();
		if (matchNum == null || "".equals(matchNum)) {
			logger.error("[重算截止]matchNum参数为空");
			super.setErrorMessage("[重算截止]matchNum参数不能为空");
			rc = 1;
			json.put("code", rc);
			json.put("msg", "[重算截止]matchNum参数不能为空");
			super.writeRs(response, json);
			return Action.NONE;
		}
		
		logger.info("进入重算截止,matchNum:{}", matchNum);
		
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
				tmpPlanList = lotteryPlanService.findByPhaseType(PhaseType.getItem(LotteryType.JCZQ_SPF.getValue()),
						null, planStatusList, pageBean);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
			}
			if (tmpPlanList == null || tmpPlanList.size() == 0) {
				logger.error("[重算截止]查询方案结果为空");
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
					super.setErrorMessage("[重算截止]matchNums转换成jsonArray错误, matchNums" + p.getMatchNums());
					rc = 1;
					json.put("code", rc);
					json.put("msg", "[重算截止]matchNums转换成jsonArray错误, matchNums"
							+ p.getMatchNums());
					super.writeRs(response, json);
					return Action.NONE;
				}

				if (jsonArray != null && !jsonArray.isEmpty()) {
					for (Iterator<?> iterator = jsonArray.iterator(); iterator
							.hasNext();) {
                        try {
                            String planMatchNum = (String) iterator.next();
                            if (planMatchNum.equals(matchNum)) {
                                logger.info("[重算截止]查询到匹配的matchNum,方案编码:{}", p.getId());
                                planIds.add(p.getId());
                                if (p.getPlanStatus() == PlanStatus.PRINTING) {
                                	planTicketIds.add(p.getId());
                                }
                                break;
                            }
                        } catch (Exception e) {
                            logger.error("处理场次判断出错, plan_id={}, match_nums={}", p.getId(), jsonArray.toString());
                            logger.error(e.getMessage(), e);
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
			super.setSuccessMessage("没有方案包含matchNum:" + matchNum + "，无需重新计算合买截止");
			
			try {
				races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
				convertRaces();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
			}
			rc = 0;
			json.put("code", rc);
			json.put("msg", "没有方案包含matchNum:" + matchNum + "，无需重新计算合买截止");
			super.writeRs(response, json);
			logger.info("重算截止结束");
			return Action.NONE;
		}

		List<String> changedList = new ArrayList<String>();
		List<String> noChangedList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		try {
			lotteryPlanService.resetMatchByPlanId(planIds, changedList, noChangedList,
					failureList);
			changedList.removeAll(noChangedList);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		
		String message = "";
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
		
		super.setSuccessMessage("重算截止:已修改方案"
				+ changedList.size()
				+ "个,未修改方案"
				+ noChangedList.size()
				+ "个,失败方案"
				+ failureList.size()
				+ "个"
				+ (failureList.size() > 0 ? ",编码:"
						+ CoreStringUtils.join(failureList, ",") : ""));
		logger.info("结束重算截止,matchNum:{}", matchNum);
		try {
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
		}
		rc = 0;
		json.put("code", rc);
		json.put("msg", "重算截止:已修改方案"
				+ changedList.size()
				+ "个,未修改方案"
				+ noChangedList.size()
				+ "个,失败方案"
				+ failureList.size()
				+ "个"
				+ (failureList.size() > 0 ? ",编码:"
						+ CoreStringUtils.join(failureList, ",") : ""));
		super.writeRs(response, json);
		return Action.NONE;
	}
	
	// 退票
	public String returnTicket() {
		logger.info("进入退票");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		int rc = 0;
		String msg= "";
		if (race == null) {
			logger.error("[退票]race参数为空");
			super.setErrorMessage("[退票]race参数不能为空");
			rc = 1;
			json.put("code", rc);
			json.put("msg", "[退票]race参数不能为空");
			super.writeRs(response, json);
			return Action.NONE;
		}

		String matchNum = race.getMatchNum();
		if (matchNum == null || "".equals(matchNum)) {
			logger.error("[退票]matchNum参数为空");
			super.setErrorMessage("[退票]matchNum参数不能为空");
			rc = 1;
			json.put("code", rc);
			json.put("msg", "[退票]matchNum参数不能为空");
			super.writeRs(response, json);
			return Action.NONE;
		}
		
		logger.info("进入退票,matchNum:{}", matchNum);
		
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
				tmpPlanList = lotteryPlanService.findByPhaseType(PhaseType.getItem(LotteryType.JCZQ_SPF.getValue()),
						null, planStatusList, pageBean);
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
		
		//发短信
		String template = "";
		path = webroot + path + JCZQ_RETURN_TICKET_TEMPLATE;
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
				json.put("msg", msg);
				writeRs(ServletActionContext.getResponse(), json);
				return Action.NONE;
			}
		} catch (Exception e) {
			logger.error("发送短信,读取短信模板失败path={}", path);
			logger.error(e.getMessage(), e);
			super.setErrorMessage("发送短信,读取短信模板失败path=" + path);
			msg = msg + "发送短信,读取短信模板失败path=" + path;
			json.put("code", rc);
			json.put("msg", msg);
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
		json.put("msg", msg);
		writeRs(ServletActionContext.getResponse(), json);
		return Action.NONE;
	}
	
	public String updateRaces() {
		logger.info("进入更新赛程数据");
		ResultBean resultBean = null;
		
		if (races == null || races.size() == 0) {
			logger.error("更新赛程为空");
			super.setErrorMessage("更新赛程不能为空");
			try {
				races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
				convertRaces();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
			}
			return "list";
		}
		try {
			if (races != null && races.size() > 0) {
				for (int i = 0 ; i < races.size(); i ++) {
					
					races.get(i).setStaticSaleSpfWrqStatus(JczqStaticSaleStatus.getItem(staticSaleSpfWrqStatusValues.get(i)));
					races.get(i).setDynamicSaleSpfWrqStatus(JczqDynamicSaleStatus.getItem(dynamicSaleSpfWrqStatusValues.get(i)));
					races.get(i).setStaticSaleSpfStatus(JczqStaticSaleStatus.getItem(staticSaleSpfStatusValues.get(i)));
					races.get(i).setDynamicSaleSpfStatus(JczqDynamicSaleStatus.getItem(dynamicSaleSpfStatusValues.get(i)));
					races.get(i).setStaticSaleBfStatus(JczqStaticSaleStatus.getItem(staticSaleBfStatusValues.get(i)));
					races.get(i).setDynamicSaleBfStatus(JczqDynamicSaleStatus.getItem(dynamicSaleBfStatusValues.get(i)));
					races.get(i).setStaticSaleJqsStatus(JczqStaticSaleStatus.getItem(staticSaleJqsStatusValues.get(i)));
					races.get(i).setDynamicSaleJqsStatus(JczqDynamicSaleStatus.getItem(dynamicSaleJqsStatusValues.get(i)));
					races.get(i).setStaticSaleBqcStatus(JczqStaticSaleStatus.getItem(staticSaleBqcStatusValues.get(i)));
					races.get(i).setDynamicSaleBqcStatus(JczqDynamicSaleStatus.getItem(dynamicSaleBqcStatusValues.get(i)));
					
					if (statusValues != null && statusValues.size() > 0) {
						races.get(i).setStatus(JczqRaceStatus.getItem(statusValues.get(i)));
					}
					if (staticDrawStatusValues != null && staticDrawStatusValues.size() > 0) {
						races.get(i).setStaticDrawStatus(JczqStaticDrawStatus.getItem(staticDrawStatusValues.get(i)));
					}
					if (dynamicDrawStatusValues != null && dynamicDrawStatusValues.size() > 0) {
						races.get(i).setDynamicDrawStatus(JczqDynamicDrawStatus.getItem(dynamicDrawStatusValues.get(i)));
					}
				}
			}
			
			resultBean = jczqRaceService.batchCreate(races);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("批量更新赛程数据时，api调用异常，{}", e);
		}
		if (resultBean == null) {
			logger.error("更新竞彩足球赛程，更新失败");
			super.setErrorMessage("更新竞彩足球赛程失败，请联系管理员");
			try {
				races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
				convertRaces();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
			}
			return "list";
		}
		if (!resultBean.isResult()) {
			super.setErrorMessage(resultBean.getMessage());
			try {
				races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
				convertRaces();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
			}
			return "list";
		}
		try {
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
		}
		super.setSuccessMessage(resultBean.getMessage());
		logger.info("更新赛程数据结束");
		return "list";
	}
	
	public String updateStatus() {
		logger.info("进入更新赛程状态");
		HttpServletResponse response = ServletActionContext.getResponse();
		Integer rc = 0;//0成功,1失败
		String message = "更新" + race.getMatchNum() + "成功";
		
		JczqRaceStatus status = JczqRaceStatus.getItem(tag);
		race.setStatus(status);
		boolean flag = jczqRaceService.updateRaceStatus(race);
		try {
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
		}
		if (!flag){
			super.setErrorMessage("更新" + race.getMatchNum() + "失败");
			rc = 1;//0成功,1失败
			message = "更新"  + race.getMatchNum() + "失败";
			logger.info("更新失败");
		} else {
			super.setSuccessMessage("更新" + race.getMatchNum() + "成功");
			logger.info("更新成功");
		}
		for (JczqRace r : races) {
			if (race.getMatchNum() != null && !"".equals(race.getMatchNum())) {
				if (race.getMatchNum().equals(r.getMatchNum())) {
					race = r;
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", JczqRace.toJSON(race));
		super.writeRs(response, json);
		logger.info("更新赛程状态结束");
		return Action.NONE;
	}
	
	public int getLotteryTypeValue(){
		return LotteryType.JCZQ_SPF.getValue();
	}

	public int getFetcherType() {
		return fetcherType;
	}

	public void setFetcherType(int fetcherType) {
		this.fetcherType = fetcherType;
	}

	public JczqRaceService getJczqRaceService() {
		return jczqRaceService;
	}

	public void setJczqRaceService(JczqRaceService jczqRaceService) {
		this.jczqRaceService = jczqRaceService;
	}

	public List<JczqRace> getRaces() {
		return races;
	}

	public void setRaces(List<JczqRace> races) {
		this.races = races;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public JczqRace getRace() {
		return race;
	}

	public void setRace(JczqRace race) {
		this.race = race;
	}

	public List<FetcherType> getFetchers() {
		return FetcherType.getItems();
	}
	
	public List<LotteryType> getJczqLotteryList(){
		return JczqLottery.getList();
	}
	
	public JczqStaticSaleStatus getOpenJczqStaticSaleStatus() {
		return JczqStaticSaleStatus.SALE_OPEN;
	}
	
	public JczqRaceStatus getUnopenStatus() {
		return JczqRaceStatus.UNOPEN;
	}
	
	public JczqRaceStatus getOpenStatus() {
		return JczqRaceStatus.OPEN;
	}
	
	public JczqRaceStatus getCloseStatus() {
		return JczqRaceStatus.CLOSE;
	}
	
	public JczqRaceStatus getRewardStatus() {
		return JczqRaceStatus.REWARD;
	}
	
	public List<JczqStaticSaleStatus> getJczqStaticSaleStatus() {
		return JczqStaticSaleStatus.getItems();
	}
	
	public List<JczqDynamicSaleStatus> getJczqDynamicSaleStatus() {
		return JczqDynamicSaleStatus.getItems();
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public Integer getStaticDrawStatusValue() {
		return staticDrawStatusValue;
	}

	public void setStaticDrawStatusValue(Integer staticDrawStatusValue) {
		this.staticDrawStatusValue = staticDrawStatusValue;
	}

	public Integer getDynamicDrawStatusValue() {
		return dynamicDrawStatusValue;
	}

	public void setDynamicDrawStatusValue(Integer dynamicDrawStatusValue) {
		this.dynamicDrawStatusValue = dynamicDrawStatusValue;
	}
	public Integer getStaticSaleSpfStatusValue() {
		return staticSaleSpfStatusValue;
	}
	public void setStaticSaleSpfStatusValue(Integer staticSaleSpfStatusValue) {
		this.staticSaleSpfStatusValue = staticSaleSpfStatusValue;
	}
	public Integer getDynamicSaleSpfStatusValue() {
		return dynamicSaleSpfStatusValue;
	}
	public void setDynamicSaleSpfStatusValue(Integer dynamicSaleSpfStatusValue) {
		this.dynamicSaleSpfStatusValue = dynamicSaleSpfStatusValue;
	}
	public Integer getStaticSaleBfStatusValue() {
		return staticSaleBfStatusValue;
	}
	public void setStaticSaleBfStatusValue(Integer staticSaleBfStatusValue) {
		this.staticSaleBfStatusValue = staticSaleBfStatusValue;
	}
	public Integer getDynamicSaleBfStatusValue() {
		return dynamicSaleBfStatusValue;
	}
	public void setDynamicSaleBfStatusValue(Integer dynamicSaleBfStatusValue) {
		this.dynamicSaleBfStatusValue = dynamicSaleBfStatusValue;
	}
	public Integer getStaticSaleJqsStatusValue() {
		return staticSaleJqsStatusValue;
	}
	public void setStaticSaleJqsStatusValue(Integer staticSaleJqsStatusValue) {
		this.staticSaleJqsStatusValue = staticSaleJqsStatusValue;
	}
	public Integer getDynamicSaleJqsStatusValue() {
		return dynamicSaleJqsStatusValue;
	}
	public void setDynamicSaleJqsStatusValue(Integer dynamicSaleJqsStatusValue) {
		this.dynamicSaleJqsStatusValue = dynamicSaleJqsStatusValue;
	}
	public Integer getStaticSaleBqcStatusValue() {
		return staticSaleBqcStatusValue;
	}
	public void setStaticSaleBqcStatusValue(Integer staticSaleBqcStatusValue) {
		this.staticSaleBqcStatusValue = staticSaleBqcStatusValue;
	}
	public Integer getDynamicSaleBqcStatusValue() {
		return dynamicSaleBqcStatusValue;
	}
	public void setDynamicSaleBqcStatusValue(Integer dynamicSaleBqcStatusValue) {
		this.dynamicSaleBqcStatusValue = dynamicSaleBqcStatusValue;
	}
	public List<Integer> getStaticSaleSpfStatusValues() {
		return staticSaleSpfStatusValues;
	}
	public void setStaticSaleSpfStatusValues(List<Integer> staticSaleSpfStatusValues) {
		this.staticSaleSpfStatusValues = staticSaleSpfStatusValues;
	}
	public List<Integer> getDynamicSaleSpfStatusValues() {
		return dynamicSaleSpfStatusValues;
	}
	public void setDynamicSaleSpfStatusValues(
			List<Integer> dynamicSaleSpfStatusValues) {
		this.dynamicSaleSpfStatusValues = dynamicSaleSpfStatusValues;
	}
	public List<Integer> getStaticSaleBfStatusValues() {
		return staticSaleBfStatusValues;
	}
	public void setStaticSaleBfStatusValues(List<Integer> staticSaleBfStatusValues) {
		this.staticSaleBfStatusValues = staticSaleBfStatusValues;
	}
	public List<Integer> getDynamicSaleBfStatusValues() {
		return dynamicSaleBfStatusValues;
	}
	public void setDynamicSaleBfStatusValues(List<Integer> dynamicSaleBfStatusValues) {
		this.dynamicSaleBfStatusValues = dynamicSaleBfStatusValues;
	}
	public List<Integer> getStaticSaleJqsStatusValues() {
		return staticSaleJqsStatusValues;
	}
	public void setStaticSaleJqsStatusValues(List<Integer> staticSaleJqsStatusValues) {
		this.staticSaleJqsStatusValues = staticSaleJqsStatusValues;
	}
	public List<Integer> getDynamicSaleJqsStatusValues() {
		return dynamicSaleJqsStatusValues;
	}
	public void setDynamicSaleJqsStatusValues(
			List<Integer> dynamicSaleJqsStatusValues) {
		this.dynamicSaleJqsStatusValues = dynamicSaleJqsStatusValues;
	}
	public List<Integer> getStaticSaleBqcStatusValues() {
		return staticSaleBqcStatusValues;
	}
	public void setStaticSaleBqcStatusValues(List<Integer> staticSaleBqcStatusValues) {
		this.staticSaleBqcStatusValues = staticSaleBqcStatusValues;
	}
	public List<Integer> getDynamicSaleBqcStatusValues() {
		return dynamicSaleBqcStatusValues;
	}
	public void setDynamicSaleBqcStatusValues(
			List<Integer> dynamicSaleBqcStatusValues) {
		this.dynamicSaleBqcStatusValues = dynamicSaleBqcStatusValues;
	}
	public List<Integer> getStatusValues() {
		return statusValues;
	}
	public void setStatusValues(List<Integer> statusValues) {
		this.statusValues = statusValues;
	}
	public List<Integer> getStaticDrawStatusValues() {
		return staticDrawStatusValues;
	}
	public void setStaticDrawStatusValues(List<Integer> staticDrawStatusValues) {
		this.staticDrawStatusValues = staticDrawStatusValues;
	}
	public List<Integer> getDynamicDrawStatusValues() {
		return dynamicDrawStatusValues;
	}
	public void setDynamicDrawStatusValues(List<Integer> dynamicDrawStatusValues) {
		this.dynamicDrawStatusValues = dynamicDrawStatusValues;
	}
	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}
	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}
	public LotteryTicketConfigService getLotteryTicketConfigService() {
		return lotteryTicketConfigService;
	}
	public void setLotteryTicketConfigService(
			LotteryTicketConfigService lotteryTicketConfigService) {
		this.lotteryTicketConfigService = lotteryTicketConfigService;
	}
	public AliasService getAliasService() {
		return aliasService;
	}
	public void setAliasService(AliasService aliasService) {
		this.aliasService = aliasService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public QueueTaskService getSmsQueueTaskService() {
		return smsQueueTaskService;
	}

	public void setSmsQueueTaskService(QueueTaskService smsQueueTaskService) {
		this.smsQueueTaskService = smsQueueTaskService;
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

	public String getPhaseNo() {
		if(StringUtils.isEmpty(phaseNo)){
			phaseNo = CoreDateUtils.formatDate(new Date(),PHASE_FORMAT_DATE);
		}
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}

	public void setDynamicSaleSpfWrqStatusValues(
			List<Integer> dynamicSaleSpfWrqStatusValues) {
		this.dynamicSaleSpfWrqStatusValues = dynamicSaleSpfWrqStatusValues;
	}

	public List<Integer> getDynamicSaleSpfWrqStatusValues() {
		return dynamicSaleSpfWrqStatusValues;
	}

	public void setStaticSaleSpfWrqStatusValues(
			List<Integer> staticSaleSpfWrqStatusValues) {
		this.staticSaleSpfWrqStatusValues = staticSaleSpfWrqStatusValues;
	}

	public List<Integer> getStaticSaleSpfWrqStatusValues() {
		return staticSaleSpfWrqStatusValues;
	}

	public void setStaticSaleSpfWrqStatusValue(
			Integer staticSaleSpfWrqStatusValue) {
		this.staticSaleSpfWrqStatusValue = staticSaleSpfWrqStatusValue;
	}

	public Integer getStaticSaleSpfWrqStatusValue() {
		return staticSaleSpfWrqStatusValue;
	}

	public void setDynamicSaleSpfWrqStatusValue(
			Integer dynamicSaleSpfWrqStatusValue) {
		this.dynamicSaleSpfWrqStatusValue = dynamicSaleSpfWrqStatusValue;
	}

	public Integer getDynamicSaleSpfWrqStatusValue() {
		return dynamicSaleSpfWrqStatusValue;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalList(List<Terminal> terminalList) {
		this.terminalList = terminalList;
	}

	public List<Terminal> getTerminalList() {
		Terminal terminal = new Terminal();
		terminal.setIsEnabled(EnabledStatus.ENABLED);
		PageBean page = super.getPageBean();
		page.setPageFlag(false);
		try{
			terminalList = terminalService.list(terminal, page);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return terminalList;
	}

	public TicketService getTicketService() {
		return ticketService;
	}

	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
	}
	
}
