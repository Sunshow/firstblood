/**
 * 
 */
package web.action.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.finance.DrawLoss;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;
import com.lehecai.admin.web.service.finance.DrawLossService;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.ticket.TicketService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

/**
 * @author chirowong
 *
 */
public class DrawLossAction extends BaseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9040972164143781227L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private TicketService ticketService;
	private LotteryPlanService lotteryPlanService;
	private DrawLossService drawLossService;
	
	private DrawLoss drawLoss;
	//创建部分出票损失工单单参数
	private Integer lotteryTypeId;
	private Integer accountCheckType;
	private String phase;
	private Date beginDate;
	private String amountCheckDate;
	
	//查询部分出票损失工单单参数
	private Date queryBeginDate;
	private Date queryEndDate;
	List<DrawLoss> drawLossList;
	
	public String handle(){
		logger.info("进入部分出票损失工单列表开始");
		return "list";
	}
	
	public String query(){
		logger.info("查询部分出票损失工单开始");
		HttpServletRequest request = ServletActionContext.getRequest();
		if(drawLoss == null) drawLoss = new DrawLoss();
		if(lotteryTypeId != null && lotteryTypeId.intValue() != LotteryType.ALL.getValue()){
			drawLoss.setLotteryType(LotteryType.getItem(lotteryTypeId.intValue()));
		}
		List<DrawLoss> drawLossQueryList = drawLossService.list(drawLoss,queryBeginDate,queryEndDate, super.getPageBean());
		DrawLoss drawLossTotal = drawLossService.getTotal(drawLoss,queryBeginDate,queryEndDate, super.getPageBean());
		PageBean pageBean = drawLossService.getPageBean(drawLoss,queryBeginDate,queryEndDate, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		if(drawLossList == null) drawLossList = new ArrayList<DrawLoss>();
		drawLossTotal.setLotteryType(LotteryType.ALL);
		drawLossList.add(drawLossTotal);
		drawLossList.addAll(drawLossQueryList);
		logger.info("查询部分出票损失工单结束");
		return "list";
	}
	
	/**
	 * 选择创建部分出票损失工单单的条件
	 * @return
	 */
	public String preCreate(){
		logger.info("进入创建部分出票损失工单的查询条件");
		return "preCreate";
	}
	
	/**
	 * 创建部分出票损失工单单
	 * @return
	 */
	public String create(){
		logger.info("创建部分出票损失工单列表");
		
		if((lotteryTypeId == null) || (lotteryTypeId != null && lotteryTypeId.intValue() == -1)){
			logger.error("彩票类型不能为空");
			super.setErrorMessage("彩票类型不能为空");
			return preCreate();
		}
		
		if((accountCheckType == null) || (accountCheckType != null && accountCheckType.intValue() == -1)){
			logger.error("对账类型不能为空");
			super.setErrorMessage("对账类型不能为空");
			return preCreate();
		}
		
		if(accountCheckType.intValue() == 0){//期
			if((phase == null) || (phase != null && phase.equals("-1"))){
				logger.error("彩期不能为空");
				super.setErrorMessage("彩期不能为空");
				return preCreate();
			}
		}else{
			if((beginDate == null)){
				logger.error("日期不能为空");
				super.setErrorMessage("日期不能为空");
				return preCreate();
			}
		}
		
		if(accountCheckType.intValue() == 0){
			amountCheckDate = phase;
		}else{
			amountCheckDate = DateUtil.formatDate(beginDate);
		}
		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		if(drawLossService.getByLotteryTypeAmountCheckDate(lt, amountCheckDate)){
			logger.error("创建出票商损失工单错误，已存在该工单");
			super.setErrorMessage("创建部分出票损失工单错误，已存在该工单");
			return preCreate();
		}
		
		Map<String, Object> statisticsMap;
		if(accountCheckType.intValue() == 0){
			try {
				statisticsMap = lotteryPlanService.lotteryPlanStatistics(null,
						null, lt, null, phase, null, null,
						null, null, null, null,
						null, null, null, null, null, null,
						null, null, null, null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("统计方案，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
		}else{
			Date pbeginDate = DateUtil.parseDate(DateUtil.formatDate(beginDate)+" 00:00:00", DateUtil.DATETIME);
			Date pEndDate = DateUtil.parseDate(DateUtil.formatDate(beginDate)+" 23:59:59", DateUtil.DATETIME);
			try {
				statisticsMap = lotteryPlanService.lotteryPlanStatistics(null,
						null, lt, null, null, null, null,
						null, null, null, null,
						null, null, null, null, null, null,
						null, null, pbeginDate, pEndDate, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("统计方案，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
		}
		
		//计算出票方案金额
		double drawPlanMoney = 0;
		if (statisticsMap != null) {
			Object amountObj = statisticsMap.get(Global.API_MAP_KEY_AMOUNT);
			if (amountObj != null) {
				try {
					drawPlanMoney = Double.parseDouble(amountObj.toString());
				} catch (Exception e) {
					logger.error("方案总金额转换成double类型异常，{}", e);
				}
			} else {
				logger.error("方案总金额为空");
			}
		} else {
			logger.error("方案查询统计失败");
			super.setErrorMessage("方案查询统计失败");
			return "failure";
		}
		
		//计算出票金额
		double drawMoney = 0;
		TerminalAccountCheckItem taci = ticketService.getTerminalAccountCheckItem(null, lotteryTypeId, accountCheckType, amountCheckDate);
		if(taci != null){
			drawMoney = taci.getLehecaiDrawMoney();
		}
		
		//计算出票损失
		double drawLossMoney = 0; 
		drawLossMoney = drawMoney - drawPlanMoney;
		if(drawLoss == null) drawLoss = new DrawLoss();
		drawLoss.setAccountCheckType(accountCheckType);
		drawLoss.setLotteryType(LotteryType.getItem(lotteryTypeId));
		drawLoss.setAmountCheckDate(amountCheckDate);
		drawLoss.setDrawMoney(drawMoney);
		drawLoss.setDrawPlanMoney(drawPlanMoney);
		drawLoss.setDrawLossMoney(drawLossMoney);
		return "create";
	}
	
	/**
	 * 保存部分出票损失工单单
	 * @return
	 */
	public String save(){
		logger.info("更新部分出票损失工单开始");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		if (drawLoss != null) {
			if(drawLoss.getId() == null && drawLossService.getByLotteryTypeAmountCheckDate(drawLoss.getLotteryType(), drawLoss.getAmountCheckDate())){
				logger.error("更新部分出票损失工单错误，已存在该工单");
				super.setErrorMessage("更新部分出票损失工单错误，已存在该工单");
				return "failure";
			}else{
				Long userId = drawLoss.getUserId();
				if(userId == null)
					drawLoss.setUserId(userSessionBean.getUser().getId());
				Date createTime = drawLoss.getCreateTime();
				if(createTime == null)
					drawLoss.setCreateTime(new Date());
				drawLoss.setLotteryType(LotteryType.getItem(lotteryTypeId));
				drawLossService.manage(drawLoss);
				super.setForwardUrl("/finance/drawLoss.do");
				logger.info("更新部分出票损失工单结束");
				return "success";
			}
		}else{
			logger.error("更新部分出票损失工单错误，提交表单为空");
			super.setErrorMessage("更新部分出票损失工单错误，提交表单不能为空");
			return "failure";
		}
	}
	
	public String del(){
		logger.info("删除部分出票损失工单信息");
		if (drawLoss != null && drawLoss.getId() != null) {
			drawLoss = drawLossService.get(drawLoss.getId());
			drawLossService.del(drawLoss);
		} else {
			logger.error("删除部分出票损失工单，编码为空");
			super.setErrorMessage("删除部分出票损失工单单，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/finance/drawLoss.do"+groupQueryParam());
		logger.info("删除部分出票损失工单单结");
		return "forward";
	}
	
	public String update(){
		logger.info("修改部分出票损失工单开始");
		if(drawLoss != null && drawLoss.getId() != null){
			drawLoss = drawLossService.get(drawLoss.getId());
		}else{
			logger.info("修改部分出票损失工单，编码为空");
			super.setErrorMessage("修改部分出票损失工单，编码为空");
			return "failure";
		}
		logger.info("修改部分出票损失工单结束");
		return "create";
	}
	
	private String groupQueryParam(){
		StringBuffer sb = new StringBuffer();
		sb.append("?action=query");
		if(lotteryTypeId != null){
			sb.append("&lotteryTypeId=").append(lotteryTypeId);
		}
		if(queryBeginDate != null){
			sb.append("&queryBeginDate=").append(DateUtil.formatDate(queryBeginDate,DateUtil.DATETIME));
		}
		if(queryEndDate != null){
			sb.append("&queryEndDate=").append(DateUtil.formatDate(queryEndDate,DateUtil.DATETIME));
		}
		return sb.toString();
	}
	
	public List<LotteryType> getLotteryTypes() {
		return OnSaleLotteryList.getForQuery();
	}

	public TicketService getTicketService() {
		return ticketService;
	}

	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.getForQuery();
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public Integer getAccountCheckType() {
		return accountCheckType;
	}

	public void setAccountCheckType(Integer accountCheckType) {
		this.accountCheckType = accountCheckType;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	
	public String getAmountCheckDate() {
		return amountCheckDate;
	}

	public void setAmountCheckDate(String amountCheckDate) {
		this.amountCheckDate = amountCheckDate;
	}

	public DrawLoss getDrawLoss() {
		if(drawLoss == null) drawLoss = new DrawLoss();
		return drawLoss;
	}

	public void setDrawLoss(
			DrawLoss drawLoss) {
		this.drawLoss = drawLoss;
	}

	public DrawLossService getDrawLossService() {
		return drawLossService;
	}

	public void setDrawLossService(
			DrawLossService drawLossService) {
		this.drawLossService = drawLossService;
	}
	
	public Date getQueryBeginDate() {
		return queryBeginDate;
	}

	public void setQueryBeginDate(Date queryBeginDate) {
		this.queryBeginDate = queryBeginDate;
	}

	public Date getQueryEndDate() {
		return queryEndDate;
	}

	public void setQueryEndDate(Date queryEndDate) {
		this.queryEndDate = queryEndDate;
	}

	public List<DrawLoss> getDrawLossList() {
		return drawLossList;
	}

	public void setDrawLossList(
			List<DrawLoss> drawLossList) {
		this.drawLossList = drawLossList;
	}

	public LotteryType getAllType() {
		return LotteryType.ALL;
	}
	
	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}

	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}

	//按天处理的彩种
	public String getDateLotteryTypes(){
		List<String> dateLotteryTypes = new ArrayList<String>();
		dateLotteryTypes.add(String.valueOf(LotteryType.JCLQ_SF.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JCLQ_RFSF.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JCLQ_DXF.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JCLQ_SFC.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JCZQ_BF.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JCZQ_BQC.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JCZQ_JQS.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JCZQ_SPF.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.SD11X5.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JX11X5.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.GD11X5.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.GDKL10.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.CQSSC.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.SHSSL.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.JXSSC.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.BJKL8.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.SDQYH.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.GXKL10.getValue()));
		dateLotteryTypes.add(String.valueOf(LotteryType.HNKL10.getValue()));
		String str = StringUtils.join(dateLotteryTypes, ",");
		return str;
	}
}
