package web.bean;

import java.io.Serializable;

public class AliasMatchBean  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer matchId;
	private String leagueShortName;
	private String leagueLongName;
	private String homeTeamShortName;
	private String homeTeamLongName;
	private String awayTeamShortName;
	private String awayTeamLongName;
	private String matchTime;
	public String getLeagueShortName() {
		return leagueShortName;
	}
	public void setLeagueShortName(String leagueShortName) {
		this.leagueShortName = leagueShortName;
	}
	public String getLeagueLongName() {
		return leagueLongName;
	}
	public void setLeagueLongName(String leagueLongName) {
		this.leagueLongName = leagueLongName;
	}
	public String getHomeTeamShortName() {
		return homeTeamShortName;
	}
	public void setHomeTeamShortName(String homeTeamShortName) {
		this.homeTeamShortName = homeTeamShortName;
	}
	public String getHomeTeamLongName() {
		return homeTeamLongName;
	}
	public void setHomeTeamLongName(String homeTeamLongName) {
		this.homeTeamLongName = homeTeamLongName;
	}
	public String getAwayTeamShortName() {
		return awayTeamShortName;
	}
	public void setAwayTeamShortName(String awayTeamShortName) {
		this.awayTeamShortName = awayTeamShortName;
	}
	public String getAwayTeamLongName() {
		return awayTeamLongName;
	}
	public void setAwayTeamLongName(String awayTeamLongName) {
		this.awayTeamLongName = awayTeamLongName;
	}
	public String getMatchTime() {
		return matchTime;
	}
	public void setMatchTime(String matchTime) {
		this.matchTime = matchTime;
	}
	public Integer getMatchId() {
		if (matchId == null) {
			matchId = 0;
		}
		return matchId;
	}
	public void setMatchId(Integer matchId) {
		this.matchId = matchId;
	}

}
