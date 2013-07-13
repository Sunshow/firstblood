package web.domain.weibo;

import java.io.Serializable;

import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.WeiboType;

public class WeiboLottery implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String uid;
	private WeiboType weiboType;
	private LotteryType lotteryType;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public WeiboType getWeiboType() {
		return weiboType;
	}
	public void setWeiboType(WeiboType weiboType) {
		this.weiboType = weiboType;
	}
	public LotteryType getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
}
