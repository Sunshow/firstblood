package web.action.member;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.LotteryListService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.PrizeRank;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;

public class LotteryListAction extends BaseAction {

	private static final long serialVersionUID = 2436161531465382896L;
	private final Logger logger = LoggerFactory.getLogger(LotteryListAction.class);
	
	private String userName;
	private String lotteryAmount;
	private String channelSource;
	
	private String lotteryType;
	private Date ticketPrintTimeStart;
	private Date ticketPrintTimeEnd;  
	
	private LotteryListService lotteryListService;
	
	private PrizeRank lottery;
	private List<PrizeRank> lotteryrank = new ArrayList<PrizeRank>();
	
	public List<LotteryType> getLotteryTypes(){
		return LotteryType.getItems();
	}
	
	public String handle() {
		logger.info("进入查询中奖排行列表");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询中奖排行列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map;
		
		try {
			map = lotteryListService.fuzzyQueryResult(lotteryType,ticketPrintTimeStart,ticketPrintTimeEnd , super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			lotteryrank = (List<PrizeRank>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询中奖排行列表结束");
		return "list";
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLotteryAmount() {
		return lotteryAmount;
	}

	public void setLotteryAmount(String lotteryAmount) {
		this.lotteryAmount = lotteryAmount;
	}

	public String getChannelSource() {
		return channelSource;
	}

	public void setChannelSource(String channelSource) {
		this.channelSource = channelSource;
	}

	public LotteryListService getLotteryListService() {
		return lotteryListService;
	}

	public void setLotteryListService(LotteryListService lotteryListService) {
		this.lotteryListService = lotteryListService;
	}

	public PrizeRank getLottery() {
		return lottery;
	}

	public void setLottery(PrizeRank lottery) {
		this.lottery = lottery;
	}

	public String getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(String lotteryType) {
		this.lotteryType = lotteryType;
	}

	public Date getTicketPrintTimeStart() {
		return ticketPrintTimeStart;
	}

	public void setTicketPrintTimeStart(Date ticketPrintTimeStart) {
		this.ticketPrintTimeStart = ticketPrintTimeStart;
	}

	public Date getTicketPrintTimeEnd() {
		return ticketPrintTimeEnd;
	}

	public void setTicketPrintTimeEnd(Date ticketPrintTimeEnd) {
		this.ticketPrintTimeEnd = ticketPrintTimeEnd;
	}

	public List<PrizeRank> getLotteryrank() {
		return lotteryrank;
	}

	public void setLotteryrank(List<PrizeRank> lotteryrank) {
		this.lotteryrank = lotteryrank;
	}
	
}
