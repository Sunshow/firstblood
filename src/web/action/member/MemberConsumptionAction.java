package web.action.member;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberConsumptionService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.MemberConsumption;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class MemberConsumptionAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(MemberConsumptionAction.class);
	
	private MemberConsumptionService memberConsumptionService;
	
	private MemberConsumption memberConsumption;
	
	private List<MemberConsumption> memberConsumptions;
	
	private Integer lotteryTypeId;
	private Long uid;
	private String username;
	private Date beginDate;
	private Date endDate;
	
	private Integer seconds;//倒计时
	
	private String orderStr;
	private String orderView;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	public String handle() {
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String list() {
		logger.info("进入查询会员消费数据");

        if (beginDate == null || endDate == null) {
            logger.error("开始时间和结束时间必须指定");
            super.setErrorMessage("开始时间和结束时间必须指定!");
            return "failure";
        }

        if (!CoreDateUtils.formatDate(beginDate).equals(CoreDateUtils.formatDate(endDate))) {
            logger.error("开始时间和结束时间不为同一天");
            super.setErrorMessage("开始时间和结束时间暂时只支持同一天的数据查询!");
            return "failure";
        }

		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		Map<String, Object> map;
		try {
			map = memberConsumptionService.getResult(lt, uid, username, getBeginDate(), getEndDate(),
					getOrderStr(), getOrderView(), super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		if (map != null) {
			memberConsumptions = (List<MemberConsumption>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询会员消费数据结束");
		
		return "list";
	}
	
	public String getOrderStr() {
		if (orderStr == null && !"".equals(orderStr)){
			orderStr = MemberConsumption.ORDER_CONSUME_AMOUNT;
		}
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		if (orderView == null && !"".equals(orderView)){
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(MemberConsumption.ORDER_CONSUME_AMOUNT, "消费金额");
		orderStrMap.put(MemberConsumption.ORDER_PRIZE_AMOUNT, "中奖金额");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public List<LotteryType> getLotteryTypes(){
		return LotteryType.getItems();
	}
	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}
	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getBeginDate() {
		if (beginDate == null) {
			Calendar cd = Calendar.getInstance();
			cd.add(Calendar.DATE, -1);
			cd.set(Calendar.HOUR_OF_DAY, 0);
			cd.set(Calendar.MINUTE, 0);
			cd.set(Calendar.SECOND, 0);
			beginDate = cd.getTime();
		}
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		if (endDate == null) {
			Calendar cd = Calendar.getInstance();
			cd.add(Calendar.DATE, -1);
			cd.set(Calendar.HOUR_OF_DAY, 23);
			cd.set(Calendar.MINUTE, 59);
			cd.set(Calendar.SECOND, 59);
			endDate = cd.getTime();
		}
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public MemberConsumptionService getMemberConsumptionService() {
		return memberConsumptionService;
	}
	public void setMemberConsumptionService(
			MemberConsumptionService memberConsumptionService) {
		this.memberConsumptionService = memberConsumptionService;
	}
	public MemberConsumption getMemberConsumption() {
		return memberConsumption;
	}
	public void setMemberConsumption(MemberConsumption memberConsumption) {
		this.memberConsumption = memberConsumption;
	}
	public List<MemberConsumption> getMemberConsumptions() {
		return memberConsumptions;
	}
	public void setMemberConsumptions(List<MemberConsumption> memberConsumptions) {
		this.memberConsumptions = memberConsumptions;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public Integer getSeconds() {
		if (seconds == null) {
			seconds = 600;
		}
		return seconds;
	}
	public void setSeconds(Integer seconds) {
		this.seconds = seconds;
	}
}
