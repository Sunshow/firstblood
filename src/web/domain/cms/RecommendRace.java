package web.domain.cms;

import java.io.Serializable;
import java.util.Date;

import com.lehecai.core.lottery.LotteryType;

public class RecommendRace implements Serializable {
	private static final long serialVersionUID = 4731464341853661340L;
	
	private Long id;				//编号
	private Long cooperatorId;		//合作商id
	private LotteryType lotteryType;	//彩种类型
	private String phase;			//彩期
	private Long dcId;				//足球单场id
    private Long matchNum;			//场次
    private String homeTeam;		//主队
    private String awayTeam;		//客队
    private String matchName;		//联赛
    private Date recommendDate;		//推荐日期
    private Date updateDate;		//更新日期
    private Integer recommendValue;	//推荐值
    
    public RecommendRace () {
    	
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCooperatorId() {
		return cooperatorId;
	}

	public void setCooperatorId(Long cooperatorId) {
		this.cooperatorId = cooperatorId;
	}

	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}
	
	public Long getDcId() {
		return dcId;
	}

	public void setDcId(Long dcId) {
		this.dcId = dcId;
	}

	public Long getMatchNum() {
		return matchNum;
	}

	public void setMatchNum(Long matchNum) {
		this.matchNum = matchNum;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getRecommendDate() {
		return recommendDate;
	}

	public void setRecommendDate(Date recommendDate) {
		this.recommendDate = recommendDate;
	}

	public Integer getRecommendValue() {
		return recommendValue;
	}

	public void setRecommendValue(Integer recommendValue) {
		this.recommendValue = recommendValue;
	}
}
