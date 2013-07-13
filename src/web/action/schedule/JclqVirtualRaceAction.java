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
import com.lehecai.admin.web.service.lottery.JclqRaceService;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.lottery.LotteryTicketConfigService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.FetchFailedException;
import com.lehecai.core.lottery.*;
import com.lehecai.core.lottery.cache.JclqLottery;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.jclq.IJclqScheduleFetcher;
import com.lehecai.core.lottery.fetcher.jclq.JclqScheduleItem;
import com.lehecai.core.lottery.fetcher.jclq.impl.CommonJclqScheduleFetcher;
import com.lehecai.core.queue.QueueConstant;
import com.lehecai.core.queue.QueueTaskService;
import com.lehecai.core.queue.sms.SmsQueueTask;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreFileUtils;
import com.lehecai.core.util.CoreStringUtils;
import com.lehecai.core.util.lottery.JclqUtil;
import com.lehecai.engine.entity.lottery.LotteryTicketConfig;
import com.lehecai.engine.entity.terminal.Terminal;
import com.lehecai.engine.entity.terminal.TerminalType;
import com.opensymphony.xwork2.Action;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class JclqVirtualRaceAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final int PLAN_COUNT_PER_QUERY = 200;
	private static final String JCLQ_RETURN_TICKET_TEMPLATE = "return_ticket_sms.html";
	private static final String PHASE_FORMAT_DATE = "yyyyMMdd";
	//private static final String JCLQ_VIRTUAL_NAME = "VirtualPengineAPI";

	private JclqRaceService jclqRaceService;

	private PhaseService phaseService;
	private MemberService memberService;
	private QueueTaskService smsQueueTaskService;
	private SmsService smsService;
	private AliasService aliasService;

	private LotteryPlanService lotteryPlanService;

	private LotteryTicketConfigService lotteryTicketConfigService;

	private TerminalService terminalService;

	private int fetcherType;

	private JclqRace race;
	private JclqRace aliasRace;
	private String path;
	private String webroot;

	private List<JclqRace> races;

//	private Date fetchTime;

	private int tag;

	private String callbackUrl;

	private Terminal terminal;

	private int terminalId;

	private List<Terminal> terminalList;

	private Integer staticDrawStatusValue;		//固定奖金开奖状态，用于表单提交
	private Integer dynamicDrawStatusValue;	//浮动奖金开奖状态，用于表单提交
	private Integer staticSaleSfStatusValue;	//固定奖金胜负玩法销售状态，用于表单提交
	private Integer dynamicSaleSfStatusValue;	//浮动奖金胜负玩法销售状态，用于表单提交
	private Integer staticSaleRfsfStatusValue;	//固定奖金让分胜负玩法销售状态，用于表单提交
	private Integer dynamicSaleRfsfStatusValue;//浮动奖金让分胜负玩法销售状态，用于表单提交
	private Integer staticSaleSfcStatusValue;	//固定奖金胜分差玩法销售状态，用于表单提交
	private Integer dynamicSaleSfcStatusValue;	//浮动奖金胜分差玩法销售状态，用于表单提交
	private Integer staticSaleDxfStatusValue;	//固定奖金大小分玩法销售状态，用于表单提交
	private Integer dynamicSaleDxfStatusValue;	//浮动奖金大小分玩法销售状态，用于表单提交

	private List<Integer> staticSaleSfStatusValues;	//固定奖金胜负玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleSfStatusValues;	//浮动奖金胜负玩法销售状态，用于表单提交
	private List<Integer> staticSaleRfsfStatusValues;	//固定奖金让分胜负玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleRfsfStatusValues;//浮动奖金让分胜负玩法销售状态，用于表单提交
	private List<Integer> staticSaleSfcStatusValues;	//固定奖金胜分差玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleSfcStatusValues;	//浮动奖金胜分差玩法销售状态，用于表单提交
	private List<Integer> staticSaleDxfStatusValues;	//固定奖金大小分玩法销售状态，用于表单提交
	private List<Integer> dynamicSaleDxfStatusValues;	//浮动奖金大小分玩法销售状态，用于表单提交

	private List<Integer> statusValues;
	private List<Integer> staticDrawStatusValues;
	private List<Integer> dynamicDrawStatusValues;
	private String phaseNo;

	public String handle() {
		logger.info("进入查询竞彩篮球对阵信息");
		try {
			terminalList = terminalService.list(terminal, super.getPageBean());
			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("查询竞彩篮球对阵信息结束");
		return "list";
	}

	private void convertRaces() {
		staticSaleSfStatusValues = new ArrayList<Integer>();
		dynamicSaleSfStatusValues = new ArrayList<Integer>();
		staticSaleRfsfStatusValues = new ArrayList<Integer>();
		dynamicSaleRfsfStatusValues = new ArrayList<Integer>();
		staticSaleSfcStatusValues = new ArrayList<Integer>();
		dynamicSaleSfcStatusValues = new ArrayList<Integer>();
		staticSaleDxfStatusValues = new ArrayList<Integer>();
		dynamicSaleDxfStatusValues = new ArrayList<Integer>();

		if (races != null && races.size() > 0) {
			for (JclqRace r : races) {
				staticSaleSfStatusValues.add(r.getStaticSaleSfStatus().getValue());
				dynamicSaleSfStatusValues.add(r.getDynamicSaleSfStatus().getValue());
				staticSaleRfsfStatusValues.add(r.getStaticSaleRfsfStatus().getValue());
				dynamicSaleRfsfStatusValues.add(r.getDynamicSaleRfsfStatus().getValue());
				staticSaleSfcStatusValues.add(r.getStaticSaleSfcStatus().getValue());
				dynamicSaleSfcStatusValues.add(r.getDynamicSaleSfcStatus().getValue());
				staticSaleDxfStatusValues.add(r.getStaticSaleDxfStatus().getValue());
				dynamicSaleDxfStatusValues.add(r.getDynamicSaleDxfStatus().getValue());
			}
		}
	}

	public String fetchVirtualRaceList(){
		try{
 			IJclqScheduleFetcher fetcher = new CommonJclqScheduleFetcher();
			List<JclqScheduleItem> scheduleApiItems = null;
			races = new ArrayList<JclqRace>();
			try {
				terminalList = terminalService.list(terminal, super.getPageBean());
//				Date fetchVirtualDate = this.getFetchTime();
//				Date phaseDate = this.getPhaseNo();
//				String fetchVirtualString = new SimpleDateFormat("yyyyMMdd").format(phaseDate);
				scheduleApiItems = fetcher.fetch(phaseNo, FetcherType.T_PENGINEAPI, String.valueOf(terminalId));
				@SuppressWarnings("unused")
				Phase phase = null;
				try {
					phase = phaseService.getCurrentPhase(PhaseType.getItem(LotteryType.JCLQ_SF.getValue()));
				} catch (ApiRemoteCallFailedException e) {
					logger.error("查询竞彩足球当前期时，api调用异常，{}", e);
					throw e;
				}
				for (JclqScheduleItem scheduleApiItem : scheduleApiItems) {
					JclqRace race  = new JclqRace();
					race.setMatchName(scheduleApiItem.getMatchName());
					race.setPhase(CoreDateUtils.formatDate(scheduleApiItem.getOfficialDate(), PHASE_FORMAT_DATE));
					race.setHomeTeam(scheduleApiItem.getHomeTeam());
					race.setAwayTeam(scheduleApiItem.getAwayTeam());
					race.setOfficialDate(scheduleApiItem.getOfficialDate());
					if (race.getOfficialDate() != null) {
						Calendar cd = Calendar.getInstance();
						cd.setTime(race.getOfficialDate());
						race.setOfficialWeekDay(JclqRace.WEEKSTR.get(cd.get(Calendar.DAY_OF_WEEK)));
					}
					race.setMatchNum(scheduleApiItem.getMatchNum());
					race.setMatchDate(scheduleApiItem.getMatchDate());
					race.setOfficialNum(scheduleApiItem.getOfficialNum());
					race.setStatus(scheduleApiItem.getStatus());
					race.setStaticHandicap(scheduleApiItem.getStaticHandicap());
					race.setStaticSaleSfStatus(scheduleApiItem.getStaticSaleSfStatus());
					race.setDynamicSaleSfStatus(scheduleApiItem.getDynamicSaleSfStatus());
					race.setStaticSaleRfsfStatus(scheduleApiItem.getStaticSaleRfsfStatus());
					race.setDynamicSaleRfsfStatus(scheduleApiItem.getDynamicSaleRfsfStatus());
					race.setStaticSaleSfcStatus(scheduleApiItem.getStaticSaleSfcStatus());
					race.setDynamicSaleSfcStatus(scheduleApiItem.getDynamicSaleSfcStatus());
					race.setStaticSaleDxfStatus(scheduleApiItem.getStaticSaleDxfStatus());
					race.setDynamicSaleDxfStatus(scheduleApiItem.getDynamicSaleDxfStatus());
					race.setDynamicHandicap(scheduleApiItem.getDynamicHandicap());
					race.setDynamicPresetScore(scheduleApiItem.getDynamicPresetScore());
					race.setStaticDrawStatus(JclqStaticDrawStatus.UNOPEN);
					race.setDynamicDrawStatus(JclqDynamicDrawStatus.UNOPEN);
					long endSaleForward = 0;
					LotteryTicketConfig config = lotteryTicketConfigService.get(LotteryType.JCLQ_SF);
					if (config != null && config.getEndSaleForward() != null) {
						endSaleForward = config.getEndSaleForward();
					}
					Date endSaleTime = JclqUtil.getEndSaleTimeByMatchDate(scheduleApiItem.getMatchDate(), endSaleForward);
					race.setEndSaleTime(endSaleTime);
					races.add(race);
				}
				convertRaces();
			} catch (FetchFailedException e) {
				logger.error("从出票商抓取赛程数据结果为空, fetchDate={}", phaseNo);
				throw new RuntimeException("从出票商抓取赛程数据结果为空");
			}
		}catch (Exception e) {
			logger.error("抓取赛程出错", e);
			super.setErrorMessage("抓取赛程出错：" + e.getMessage());
		}
//		try {
//			jclqRaceService.batchCreate(races);
//		} catch (ApiRemoteCallFailedException e) {
//			e.printStackTrace();
//		}
		return "fetchList";

	}

	public String createRace() {
		logger.info("进入创建赛程");
		terminalList = terminalService.list(terminal, super.getPageBean());
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
		String matchNum = race.getPhase() + LotteryConstant.JCLQ_MATCH_NUM_CODE_DEFAULT + race.getOfficialNum();
		try {
			JclqRace jclqRace = jclqRaceService.getRaceByMatchNum(matchNum);
			if (jclqRace != null && jclqRace.getMatchNum() != null) {
				super.setSuccessMessage(matchNum + "赛程已经存在,创建赛程失败");
				try {
					races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
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
		race.setStatus(JclqRaceStatus.UNOPEN);
		race.setStaticDrawStatus(JclqStaticDrawStatus.UNOPEN);
		race.setDynamicDrawStatus(JclqDynamicDrawStatus.UNOPEN);
		
		race.setStaticSaleSfStatus(JclqStaticSaleStatus.getItem(staticSaleSfStatusValue));
		race.setDynamicSaleSfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleSfStatusValue));
		race.setStaticSaleRfsfStatus(JclqStaticSaleStatus.getItem(staticSaleRfsfStatusValue));
		race.setDynamicSaleRfsfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleRfsfStatusValue));
		race.setStaticSaleSfcStatus(JclqStaticSaleStatus.getItem(staticSaleSfcStatusValue));
		race.setDynamicSaleSfcStatus(JclqDynamicSaleStatus.getItem(dynamicSaleSfcStatusValue));
		race.setStaticSaleDxfStatus(JclqStaticSaleStatus.getItem(staticSaleDxfStatusValue));
		race.setDynamicSaleDxfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleDxfStatusValue));
		
		if (jclqRaceService.saveRace(race)) {
			logger.info("创建赛程成功");
			super.setSuccessMessage("创建赛程成功");
		} else {
			logger.error("创建赛程失败");
			super.setSuccessMessage("创建赛程失败");
		}
		try {
			this.setPhaseNo(race.getPhase());
			races = jclqRaceService.getRaceListByDateAndStatus(race.getPhase(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("创建赛程结束");
		return "list";
	}

	public String updateRace() {
		logger.info("进入更新赛程");
		terminalList = terminalService.list(terminal, super.getPageBean());
		race.setStaticSaleSfStatus(JclqStaticSaleStatus.getItem(staticSaleSfStatusValue));
		race.setDynamicSaleSfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleSfStatusValue));
		race.setStaticSaleRfsfStatus(JclqStaticSaleStatus.getItem(staticSaleRfsfStatusValue));
		race.setDynamicSaleRfsfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleRfsfStatusValue));
		race.setStaticSaleSfcStatus(JclqStaticSaleStatus.getItem(staticSaleSfcStatusValue));
		race.setDynamicSaleSfcStatus(JclqDynamicSaleStatus.getItem(dynamicSaleSfcStatusValue));
		race.setStaticSaleDxfStatus(JclqStaticSaleStatus.getItem(staticSaleDxfStatusValue));
		race.setDynamicSaleDxfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleDxfStatusValue));
		
		boolean b =jclqRaceService.updateRace(race);

		JSONObject rs = new JSONObject();
		if (b) {
			rs.put("msg", "更新成功");
			logger.info("更新竞彩篮球数据，更新成功");
		} else {
			rs.put("msg", "更新失败请重试");
			logger.error("更新竞彩篮球数据，更新失败");
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更新赛程结束");
		return null;
	}

	// 重算截止
	public String resetMatch() {
		logger.info("进入重算合买");
		terminalList = terminalService.list(terminal, super.getPageBean());
		JSONObject json = new JSONObject();
		int rc = 0;
		String msg = "";
		if (race == null) {
			logger.error("[重算截止]race参数为空");
			super.setErrorMessage("[重算截止]race参数不能为空");
			rc = 1;
			msg = "[重算截止]race参数不能为空";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}
		
		String matchNum = race.getMatchNum();
		if (matchNum == null || "".equals(matchNum)) {
			logger.error("[重算截止]matchNum参数为空");
			super.setErrorMessage("[重算截止]matchNum参数不能为空");
			rc = 1;
			msg = "[重算截止]matchNum参数不能为空";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}
		
		logger.info("进入重算截止,matchNum:{}", matchNum);
		
		List<String> planIds = new ArrayList<String>();

		logger.info("[重算截止]方案状态为：招募中和未支付,待出票");
		List<PlanStatus> planStatusList = new ArrayList<PlanStatus>();
		planStatusList.add(PlanStatus.RECRUITING);
		planStatusList.add(PlanStatus.PRINT_WAITING);
		planStatusList.add(PlanStatus.PAID_NOT);

		PageBean pageBean = super.getPageBean();
		pageBean.setPageSize(PLAN_COUNT_PER_QUERY);// 每次查询条数

		int i = 1;
		while (true) {
			List<Plan> tmpPlanList = null;
			pageBean.setPage(i);
			try {
				tmpPlanList = lotteryPlanService.findByPhaseType(PhaseType.getItem(LotteryType.JCLQ_SF.getValue()),
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
					super.setErrorMessage("[重算截止]matchNums转换成jsonArray错误, matchNums"+ p.getMatchNums());
					rc = 1;
					msg = "[重算截止]matchNums转换成jsonArray错误, matchNums";
					json.put("code", rc);
					json.put("msg", msg);
					writeRs(ServletActionContext.getResponse(), json);
					return Action.NONE;
				}

				if (jsonArray != null && !jsonArray.isEmpty()) {
					for (Iterator<?> iterator = jsonArray.iterator(); iterator
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
//			try {
//				races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, CoreDateUtils.formatDate(this.getPhaseNo()).equals(CoreDateUtils.formatDate(new Date())));
//				convertRaces();
//			} catch (ApiRemoteCallFailedException e) {
//				logger.error("竞彩篮球查询数据库中赛程数据时，api访问出错", e);
//			}
//			return "list";
			rc = 1;
			msg = "没有方案包含matchNum:" + matchNum
					+ "，无需重新计算截止";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(ServletActionContext.getResponse(), json);
			logger.info("重算截止结束");
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
			super.setErrorMessage(e.getMessage());
			rc = 1;
			msg = e.getMessage();
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			rc = 1;
			msg = e.getMessage();
			json.put("code", rc);
			json.put("msg", msg);
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
		rc = 0;
		msg = "重算截止:已修改方案"
			+ changedList.size()
			+ "个,未修改方案"
			+ noChangedList.size()
			+ "个,失败方案"
			+ failureList.size()
			+ "个"
			+ (failureList.size() > 0 ? ",编码:"
					+ CoreStringUtils.join(failureList, ",") : "");
		json.put("code", rc);
		json.put("msg", msg);
		writeRs(ServletActionContext.getResponse(), json);
		return Action.NONE;
//		try {
//			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, CoreDateUtils.formatDate(this.getPhaseNo()).equals(CoreDateUtils.formatDate(new Date())));
//			convertRaces();
//		} catch (ApiRemoteCallFailedException e) {
//			logger.error("竞彩篮球查询数据库中赛程数据时，api访问出错", e);
//		}
//		return "list";
	}

	// 退票
	public String returnTicket() {
		logger.info("进入退票");
		terminalList = terminalService.list(terminal, super.getPageBean());
		JSONObject json = new JSONObject();
		int rc = 0;
		String msg = "";
		if (race == null) {
			logger.error("[退票]race参数为空");
			super.setErrorMessage("[退票]race参数不能为空");
			rc = 1;
			msg = "[退票]race参数不能为空";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(ServletActionContext.getResponse(), json);
			return Action.NONE;
		}
		
		String matchNum = race.getMatchNum();
		if (matchNum == null || "".equals(matchNum)) {
			logger.error("[退票]matchNum参数为空");
			super.setErrorMessage("[退票]matchNum参数不能为空");
			rc = 1;
			msg = "[退票]matchNum参数不能为空";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(ServletActionContext.getResponse(), json);
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
				tmpPlanList = lotteryPlanService.findByPhaseType(PhaseType.getItem(LotteryType.JCLQ_SF.getValue()),
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
		path = webroot + path + JCLQ_RETURN_TICKET_TEMPLATE;
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
//		try {
//			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, CoreDateUtils.formatDate(this.getPhaseNo()).equals(CoreDateUtils.formatDate(new Date())));
//			convertRaces();
//		} catch (ApiRemoteCallFailedException e) {
//			logger.error("竞彩篮球查询数据库中赛程数据时，api访问出错", e);
//		}
//		return "list";
	}

	public String updateRaces() {
		logger.info("进入更新赛程");
		terminalList = terminalService.list(terminal, super.getPageBean());
		ResultBean resultBean = null;
		
		if (races == null || races.size() == 0) {
			logger.error("更新赛程为空");
			super.setErrorMessage("更新赛程不能为空");
			try {
				races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
				convertRaces();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩篮球查询数据库中赛程数据时，api调用异常，{}", e);
			}
			logger.info("更新赛程结束");
			return "list";
		}
		try {
			if (races != null && races.size() > 0) {
				for (int i = 0 ; i < races.size(); i ++) {
					
					races.get(i).setStaticSaleSfStatus(JclqStaticSaleStatus.getItem(staticSaleSfStatusValues.get(i)));
					races.get(i).setDynamicSaleSfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleSfStatusValues.get(i)));
					races.get(i).setStaticSaleRfsfStatus(JclqStaticSaleStatus.getItem(staticSaleRfsfStatusValues.get(i)));
					races.get(i).setDynamicSaleRfsfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleRfsfStatusValues.get(i)));
					races.get(i).setStaticSaleSfcStatus(JclqStaticSaleStatus.getItem(staticSaleSfcStatusValues.get(i)));
					races.get(i).setDynamicSaleSfcStatus(JclqDynamicSaleStatus.getItem(dynamicSaleSfcStatusValues.get(i)));
					races.get(i).setStaticSaleDxfStatus(JclqStaticSaleStatus.getItem(staticSaleDxfStatusValues.get(i)));
					races.get(i).setDynamicSaleDxfStatus(JclqDynamicSaleStatus.getItem(dynamicSaleDxfStatusValues.get(i)));
					
					if (statusValues != null && statusValues.size() > 0) {
						races.get(i).setStatus(JclqRaceStatus.getItem(statusValues.get(i)));
					}
					if (staticDrawStatusValues != null && staticDrawStatusValues.size() > 0) {
						races.get(i).setStaticDrawStatus(JclqStaticDrawStatus.getItem(staticDrawStatusValues.get(i)));
					}
					if (dynamicDrawStatusValues != null && dynamicDrawStatusValues.size() > 0) {
						races.get(i).setDynamicDrawStatus(JclqDynamicDrawStatus.getItem(dynamicDrawStatusValues.get(i)));
					}
				}
			}
			
			resultBean = jclqRaceService.batchCreate(races);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("批量更新赛程数据时，api调用异常，{}", e);
		}
		if (resultBean == null) {
			super.setErrorMessage("更新竞彩篮球赛程失败，请联系管理员");
			try {
				races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
				convertRaces();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩篮球查询数据库中赛程数据时，api调用异常，{}", e);
			}
			return "list";
		}
		if (!resultBean.isResult()) {
			super.setErrorMessage(resultBean.getMessage());
			try {
				races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
				convertRaces();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩篮球查询数据库中赛程数据时，api调用异常，{}", e);
			}
			return "list";
		}
		try {
			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩篮球查询数据库中赛程数据时，api调用异常，{}", e);
		}
		super.setSuccessMessage(resultBean.getMessage());
		return "list";
	}

	public String updateStatus() {
		logger.info("进入更新状态");
		terminalList = terminalService.list(terminal, super.getPageBean());
		JclqRaceStatus status = JclqRaceStatus.getItem(tag);
		race.setStatus(status);
		boolean flag = jclqRaceService.updateRaceStatus(race);
//		try {
//			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, CoreDateUtils.formatDate(this.getPhaseNo()).equals(CoreDateUtils.formatDate(new Date())));
//			convertRaces();
//		} catch (ApiRemoteCallFailedException e) {
//			logger.error("竞彩篮球查询数据库中赛程数据时，api访问出错", e);
//		}
		JSONObject json = new JSONObject();
		int rc = 0;
		String msg = "";
		if (!flag){
			super.setErrorMessage("更新" + race.getMatchNum() + "失败");
			rc = 1;
			msg = "更新" + race.getMatchNum() + "失败";
			logger.error("更新状态，更新失败");
		} else {
			super.setSuccessMessage("更新" + race.getMatchNum() + "成功");
			rc = 0;
			msg = "更新" + race.getMatchNum() + "成功";
			logger.error("更新状态，更新成功");
		}
		json.put("code", rc);
		json.put("msg", msg);
		writeRs(ServletActionContext.getResponse(), json);
		logger.info("更新状态结束");
		return Action.NONE;
	}

	//全部比赛匹配短名
	public String matchAliasAll() {
		logger.info("进入查询竞彩足球对阵信息");
		terminalList = terminalService.list(terminal, super.getPageBean());
		try {
			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
			convertRaces();
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		if (races == null || races.size() == 0) {
			logger.error("赛程为空");
			super.setErrorMessage("赛程为空");
		} else {
			List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
			for (JclqRace jr : races) {
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
					matchs = aliasService.getAliasFromBasketballScheduleInfo(AliasDataProvider.PLOT, matchs);
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
		terminalList = terminalService.list(terminal, super.getPageBean());
		String msg = "";
		JSONObject rs = new JSONObject();
		if (aliasRace == null) {
			msg = "比赛信息为空";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		
		List<AliasMatchBean> matchs = new ArrayList<AliasMatchBean>();
		
		AliasMatchBean match = new AliasMatchBean();
		match.setLeagueLongName(aliasRace.getMatchName());
		match.setMatchTime(CoreDateUtils.formatDate(aliasRace.getMatchDate(), CoreDateUtils.DATETIME));
		match.setHomeTeamLongName(aliasRace.getHomeTeam());
		match.setAwayTeamLongName(aliasRace.getAwayTeam());
		match.setMatchId(aliasRace.getFxId());
		matchs.add(match);
		
		matchs = aliasService.getAliasFromBasketballScheduleInfo(AliasDataProvider.PLOT, matchs);
		
		if (matchs != null && matchs.size() > 0) {
			match = matchs.get(0);
			if (match.getMatchId() != null && match.getMatchId() != 0) {
				msg = "匹配成功";
				aliasRace.setFxId(match.getMatchId());
			} else {
				msg = "匹配失败";
				rs.put("message", msg);
				writeRs(ServletActionContext.getResponse(), rs);
				return Action.NONE;
			}
			msg = msg + "\n主队短名：" + match.getHomeTeamShortName() + "\n客队短名：" + match.getAwayTeamShortName()
					+ "\n联赛名短名：" + match.getLeagueShortName() + "\n分析ID：" + match.getMatchId();
			aliasRace.setAwayTeam(match.getAwayTeamShortName());
			aliasRace.setHomeTeam(match.getHomeTeamShortName());
			aliasRace.setMatchName(match.getLeagueShortName());
			rs.put("message", msg);
			rs.put("race", JSONObject.fromObject(aliasRace));
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		} else {
			msg = "匹配失败";
			rs.put("message", msg);
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
	}

	public int getFetcherType() {
		return fetcherType;
	}

	public void setFetcherType(int fetcherType) {
		this.fetcherType = fetcherType;
	}

	public JclqRaceService getJclqRaceService() {
		return jclqRaceService;
	}

	public void setJclqRaceService(JclqRaceService jclqRaceService) {
		this.jclqRaceService = jclqRaceService;
	}

	public List<JclqRace> getRaces() {
		return races;
	}

	public void setRaces(List<JclqRace> races) {
		this.races = races;
	}

//	public Date getFetchTime() {
//		if (fetchTime == null) {
//			fetchTime = CoreDateUtils.parseDate(CoreDateUtils.formatDate(new Date()));
//		}
//		return fetchTime;
//	}
//
//	public void setFetchTime(Date fetchTime) {
//		this.fetchTime = fetchTime;
//	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public JclqRace getRace() {
		return race;
	}

	public void setRace(JclqRace race) {
		this.race = race;
	}

	public List<TerminalType> getFetchers() {
		return TerminalType.getItems();
	}

	public List<LotteryType> getJclqLotteryList(){
		return JclqLottery.getList();
	}

	public JclqStaticSaleStatus getOpenJclqStaticSaleStatus() {
		return JclqStaticSaleStatus.SALE_OPEN;
	}

	public JclqRaceStatus getUnopenStatus() {
		return JclqRaceStatus.UNOPEN;
	}

	public JclqRaceStatus getOpenStatus() {
		return JclqRaceStatus.OPEN;
	}

	public JclqRaceStatus getCloseStatus() {
		return JclqRaceStatus.CLOSE;
	}

	public JclqRaceStatus getRewardStatus() {
		return JclqRaceStatus.REWARD;
	}

	public List<JclqStaticSaleStatus> getJclqStaticSaleStatus() {
		return JclqStaticSaleStatus.getItems();
	}

	public List<JclqDynamicSaleStatus> getJclqDynamicSaleStatus() {
		return JclqDynamicSaleStatus.getItems();
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

	public Integer getStaticSaleSfStatusValue() {
		return staticSaleSfStatusValue;
	}

	public void setStaticSaleSfStatusValue(Integer staticSaleSfStatusValue) {
		this.staticSaleSfStatusValue = staticSaleSfStatusValue;
	}

	public Integer getDynamicSaleSfStatusValue() {
		return dynamicSaleSfStatusValue;
	}

	public void setDynamicSaleSfStatusValue(Integer dynamicSaleSfStatusValue) {
		this.dynamicSaleSfStatusValue = dynamicSaleSfStatusValue;
	}

	public Integer getStaticSaleRfsfStatusValue() {
		return staticSaleRfsfStatusValue;
	}

	public void setStaticSaleRfsfStatusValue(Integer staticSaleRfsfStatusValue) {
		this.staticSaleRfsfStatusValue = staticSaleRfsfStatusValue;
	}

	public Integer getDynamicSaleRfsfStatusValue() {
		return dynamicSaleRfsfStatusValue;
	}

	public void setDynamicSaleRfsfStatusValue(Integer dynamicSaleRfsfStatusValue) {
		this.dynamicSaleRfsfStatusValue = dynamicSaleRfsfStatusValue;
	}

	public Integer getStaticSaleSfcStatusValue() {
		return staticSaleSfcStatusValue;
	}

	public void setStaticSaleSfcStatusValue(Integer staticSaleSfcStatusValue) {
		this.staticSaleSfcStatusValue = staticSaleSfcStatusValue;
	}

	public Integer getDynamicSaleSfcStatusValue() {
		return dynamicSaleSfcStatusValue;
	}

	public void setDynamicSaleSfcStatusValue(Integer dynamicSaleSfcStatusValue) {
		this.dynamicSaleSfcStatusValue = dynamicSaleSfcStatusValue;
	}

	public Integer getStaticSaleDxfStatusValue() {
		return staticSaleDxfStatusValue;
	}

	public void setStaticSaleDxfStatusValue(Integer staticSaleDxfStatusValue) {
		this.staticSaleDxfStatusValue = staticSaleDxfStatusValue;
	}

	public Integer getDynamicSaleDxfStatusValue() {
		return dynamicSaleDxfStatusValue;
	}

	public void setDynamicSaleDxfStatusValue(Integer dynamicSaleDxfStatusValue) {
		this.dynamicSaleDxfStatusValue = dynamicSaleDxfStatusValue;
	}

	public List<Integer> getStaticSaleSfStatusValues() {
		return staticSaleSfStatusValues;
	}

	public void setStaticSaleSfStatusValues(List<Integer> staticSaleSfStatusValues) {
		this.staticSaleSfStatusValues = staticSaleSfStatusValues;
	}

	public List<Integer> getDynamicSaleSfStatusValues() {
		return dynamicSaleSfStatusValues;
	}

	public void setDynamicSaleSfStatusValues(List<Integer> dynamicSaleSfStatusValues) {
		this.dynamicSaleSfStatusValues = dynamicSaleSfStatusValues;
	}

	public List<Integer> getStaticSaleRfsfStatusValues() {
		return staticSaleRfsfStatusValues;
	}

	public void setStaticSaleRfsfStatusValues(
			List<Integer> staticSaleRfsfStatusValues) {
		this.staticSaleRfsfStatusValues = staticSaleRfsfStatusValues;
	}

	public List<Integer> getDynamicSaleRfsfStatusValues() {
		return dynamicSaleRfsfStatusValues;
	}

	public void setDynamicSaleRfsfStatusValues(
			List<Integer> dynamicSaleRfsfStatusValues) {
		this.dynamicSaleRfsfStatusValues = dynamicSaleRfsfStatusValues;
	}

	public List<Integer> getStaticSaleSfcStatusValues() {
		return staticSaleSfcStatusValues;
	}

	public void setStaticSaleSfcStatusValues(List<Integer> staticSaleSfcStatusValues) {
		this.staticSaleSfcStatusValues = staticSaleSfcStatusValues;
	}

	public List<Integer> getDynamicSaleSfcStatusValues() {
		return dynamicSaleSfcStatusValues;
	}

	public void setDynamicSaleSfcStatusValues(
			List<Integer> dynamicSaleSfcStatusValues) {
		this.dynamicSaleSfcStatusValues = dynamicSaleSfcStatusValues;
	}

	public List<Integer> getStaticSaleDxfStatusValues() {
		return staticSaleDxfStatusValues;
	}

	public void setStaticSaleDxfStatusValues(List<Integer> staticSaleDxfStatusValues) {
		this.staticSaleDxfStatusValues = staticSaleDxfStatusValues;
	}

	public List<Integer> getDynamicSaleDxfStatusValues() {
		return dynamicSaleDxfStatusValues;
	}

	public void setDynamicSaleDxfStatusValues(
			List<Integer> dynamicSaleDxfStatusValues) {
		this.dynamicSaleDxfStatusValues = dynamicSaleDxfStatusValues;
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

	public QueueTaskService getSmsQueueTaskService() {
		return smsQueueTaskService;
	}

	public void setSmsQueueTaskService(QueueTaskService smsQueueTaskService) {
		this.smsQueueTaskService = smsQueueTaskService;
	}

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
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

	public void setAliasRace(JclqRace aliasRace) {
		this.aliasRace = aliasRace;
	}

	public JclqRace getAliasRace() {
		return aliasRace;
	}

	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public List<Terminal> getTerminalList() {
		return terminalList;
	}

	public void setTerminalList(List<Terminal> terminalList) {
		this.terminalList = terminalList;
	}

	public int getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(int terminalId) {
		this.terminalId = terminalId;
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
	
	public int getLotteryTypeValue(){
		return LotteryType.JCLQ_SF.getValue();
	}

}
