package web.action.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.lottery.SfggRaceService;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.SfggRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;

/**
 * 胜负过关推荐赛程管理
 * @author Sunshow
 *
 */
public class SfggRecommendRaceAction extends AbstractRecommendRaceAction {

	private static final long serialVersionUID = -4714520661445780905L;
	
	private PhaseService phaseService;
	private SfggRaceService sfggRaceService;
	
	private Phase phase;
	private List<SfggRace> races;
	private List<Phase> phaseNoList;
	
	private Integer count = 10;
	private String phaseNo;
	
	private String pageResult;
	private String phaseStatusNo;

	@Override
	protected void initAvailableRaceList() throws Exception {
		if (phaseNo == null || phaseNo.isEmpty()) {
			phase = phaseService.getCurrentPhase(this.getPhaseType());	// 查询当前期
			if (phase != null) {	//当前期不为空
				phaseNo = phase.getPhase();
			} else {				//当前期为空
				phase = phaseService.getNearestPhase(this.getPhaseType(), new Date());// 获取离当前时间最近一期
				if (phase != null) {
					phaseNo = phase.getPhase();
				}
			}
			if (phaseNo == null) {
				phaseNo = "";
			}
		} else {
			phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(this.getPhaseType(), phaseNo);	//根据彩期类型和彩期号获得彩期
		}

		searchPhaseList();	//获得彩期列表
		
		if (phase != null) {
			phaseNo = phase.getPhase();
			List<SfggRace> allRaces = sfggRaceService.getSfggRaceListByPhase(phaseNo);		//查询单场对阵球队信息
			
			if (allRaces != null && !allRaces.isEmpty()) {
				if (this.getRecommendRaces() != null) {
					// 已推荐的场次
					Set<String> matchNumSet = new HashSet<String>();
					for (RecommendRace recommendRace : this.getRecommendRaces()) {
                        // 只将当前期次的作为判断
                        if (recommendRace.getPhase().equals(phaseNo)) {
						    matchNumSet.add(recommendRace.getMatchNum().toString());
                        }
					}
					
					races = new ArrayList<SfggRace>();
					for (SfggRace race : allRaces) {
						if (matchNumSet.contains(String.valueOf(race.getMatchNum()))) {
							continue;
						}
						races.add(race);
					}
				} else {
					races = allRaces;
				}
			} else {
				logger.info("{}期没有单场对阵球队信息", phaseNo);
			}
		}
	}

	
	/**
	 * 获得彩期列表
	 * @throws ApiRemoteCallFailedException
	 */
	private void searchPhaseList() throws ApiRemoteCallFailedException {
		phaseNoList = phaseService.getAppointPhaseList(this.getPhaseType(), phaseNo,
				count);
		if (phaseNoList != null) {
			removeRepeat(phaseNoList);// 指定彩期去重
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
			this.phase = this.phaseService.getPhaseByPhaseTypeAndPhaseNo(this.getPhaseType(), phaseNo);
			if (phase != null) {
				phaseStatusNo = String.valueOf(this.phase.getPhaseStatus()
						.getValue());
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
			return "failure";
		}
		races = sfggRaceService.getSfggRaceListByPhase(phaseNo);
		if (pageResult != null && !pageResult.trim().isEmpty()) {
			return pageResult;
		}
		logger.info("查询彩期信息结束");
		return "list";
	}
	
	
	@Override
	protected String getRecommendForwardUrl() {
		return super.getRecommendForwardUrl() + "?lotteryTypeValue=" + this.getLotteryTypeValue() + "&phaseNo=" + (this.getRecommendRace().getPhase() == null ? this.phaseNo : this.getRecommendRace().getPhase());
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public SfggRaceService getSfggRaceService() {
		return sfggRaceService;
	}


	public void setSfggRaceService(SfggRaceService sfggRaceService) {
		this.sfggRaceService = sfggRaceService;
	}


	public List<SfggRace> getRaces() {
		return races;
	}


	public void setRaces(List<SfggRace> races) {
		this.races = races;
	}


	public List<Phase> getPhaseNoList() {
		return phaseNoList;
	}

	public void setPhaseNoList(List<Phase> phaseNoList) {
		this.phaseNoList = phaseNoList;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}

	public PhaseType getPhaseType() {
		return PhaseType.getItem(LotteryType.DC_SFGG);
	}

	public String getPageResult() {
		return pageResult;
	}

	public void setPageResult(String pageResult) {
		this.pageResult = pageResult;
	}

	public String getPhaseStatusNo() {
		return phaseStatusNo;
	}

	public void setPhaseStatusNo(String phaseStatusNo) {
		this.phaseStatusNo = phaseStatusNo;
	}
}
