package web.action.cms;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.cms.RecommendRaceService;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.type.cooperator.Cooperator;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 抽象的推荐赛程管理
 * @author Sunshow
 *
 */
public abstract class AbstractRecommendRaceAction extends BaseAction {

	private static final long serialVersionUID = -4611100779900870274L;

	private RecommendRaceService recommendRaceService;
	
	private List<RecommendRace> recommendRaces;
	
	private RecommendRace recommendRace;

	private LotteryType lotteryType;
	
	private Cooperator cooperator;
	
	protected static Map<LotteryType, String> lotteryActionMap = new HashMap<LotteryType, String>();
	
	/**
	 * 查询竞彩篮球对阵信息
	 */
	public String handle () {
		logger.info("进入查询竞彩篮球对阵信息");
		
		if (lotteryType == null || cooperator == null) {
			logger.error("必须指定彩种和合作商");
			super.setErrorMessage("必须指定彩种和合作商");
			return "failure";
		}
		
		recommendRaces = recommendRaceService.findList(cooperator, lotteryType);	//查询当前合作商已推荐的当前彩种场次
		
		try {
			this.initAvailableRaceList();	// 初始化备选场次列表
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("初始化备选场次列表失败，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		logger.info("查询竞彩篮球对阵信息结束");
		return "list";
	}
	
	abstract protected void initAvailableRaceList() throws Exception;
	
	/**
	 * 推荐赛程
	 * @return
	 */
	public String recommend () {
		logger.info("进入推荐赛程");
		if (recommendRace == null || recommendRace.getMatchNum() == null) {
			logger.error("推荐赛程场次为空");
			super.setErrorMessage("推荐赛程场次为空");
			return "failure";
		}

		recommendRace.setCooperatorId(Long.valueOf(cooperator.getValue()));
		recommendRace.setLotteryType(lotteryType);
		recommendRace.setRecommendDate(new Date());
		
		this.processRecommendRace(recommendRace);
		
		recommendRaceService.merge(recommendRace);
		
		super.setForwardUrl(this.getRecommendForwardUrl());
		return "forward";
	}
	
	protected void processRecommendRace(RecommendRace recommendRace) {
		
	}
	
	protected String getRecommendForwardUrl() {
		return this.getContextURI();
	}
	
	/**
	 * 更新赛程
	 * @return
	 */
	public String update () {
		if (recommendRace == null) {
			logger.error("赛程数据为空");
			super.setErrorMessage("赛程数据不能为空");
			return "failure";
		}

		recommendRace.setLotteryType(lotteryType);
		recommendRace.setUpdateDate(new Date());
		
		recommendRaceService.merge(recommendRace);
		
		JSONObject json = new JSONObject();
		json.put("msg", "编辑成功");
		json.put("updateDate", CoreDateUtils.formatDate(recommendRace.getUpdateDate(), CoreDateUtils.DATETIME));
		super.writeRs(ServletActionContext.getResponse(), json);
		
		return null;
	}

	/**
	 * 取消推荐
	 * @return
	 */
	public String cancelRecommend () {
		if (recommendRace == null || recommendRace.getId() == null) {
			logger.error("赛程编号为空");
			super.setErrorMessage("赛程编码不能为空");
			return "failure";
		}
		
		recommendRaceService.delete(recommendRace.getId());
		
		super.setForwardUrl(this.getRecommendForwardUrl());
		return "forward";
	}
	
	public List<FetcherType> getFetchers() {
		return FetcherType.getItems();
	}
	
	public String getActionName(Integer lotteryTypeValue) {
		LotteryType lotteryType = null;
		if (lotteryTypeValue != null) {
			lotteryType = LotteryType.getItem(lotteryTypeValue);
		}
		if (lotteryType == null) {
			lotteryType = this.lotteryType;
		}
		return String.format(lotteryActionMap.get(lotteryType), cooperator.getName());
	}

	public RecommendRaceService getRecommendRaceService() {
		return recommendRaceService;
	}

	public void setRecommendRaceService(RecommendRaceService recommendRaceService) {
		this.recommendRaceService = recommendRaceService;
	}
	
	public List<RecommendRace> getRecommendRaces() {
		return recommendRaces;
	}

	public void setRecommendRaces(List<RecommendRace> recommendRaces) {
		this.recommendRaces = recommendRaces;
	}

	public RecommendRace getRecommendRace() {
		return recommendRace;
	}

	public void setRecommendRace(RecommendRace recommendRace) {
		this.recommendRace = recommendRace;
	}

	public Integer getLotteryTypeValue() {
		if (this.lotteryType != null) {
			return this.lotteryType.getValue();
		}
		return null;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		if (lotteryTypeValue != null) {
			this.lotteryType = LotteryType.getItem(lotteryTypeValue);
		}
	}

	public void setCooperatorId(Integer cooperatorId) {
		if (cooperatorId != null) {
			this.cooperator = Cooperator.getItem(cooperatorId);
		}
	}
}
