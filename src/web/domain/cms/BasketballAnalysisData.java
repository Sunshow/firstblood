package web.domain.cms;

import java.io.Serializable;

public class BasketballAnalysisData implements Serializable {
	private static final long serialVersionUID = 2931464764985366159L;
	
	private Long matchId;        //分析id
	private String aeHf;		 //平均欧赔-初赔胜
	private String aeAf;		 //平均欧赔-初赔负
	private String aeHl;		 //平均欧赔-终赔胜
	private String aeAl;		 //平均欧赔-终赔负

    public BasketballAnalysisData () {}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	public Long getMatchId() {
		return matchId;
	}

	public String getAeHf() {
		return aeHf;
	}

	public void setAeHf(String aeHf) {
		this.aeHf = aeHf;
	}

	public String getAeAf() {
		return aeAf;
	}

	public void setAeAf(String aeAf) {
		this.aeAf = aeAf;
	}

	public String getAeHl() {
		return aeHl;
	}

	public void setAeHl(String aeHl) {
		this.aeHl = aeHl;
	}

	public String getAeAl() {
		return aeAl;
	}

	public void setAeAl(String aeAl) {
		this.aeAl = aeAl;
	}



}
