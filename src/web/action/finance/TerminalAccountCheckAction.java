/**
 * 
 */
package web.action.finance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.customconfig.CustomFunctionConfig;
import com.lehecai.admin.web.domain.customconfig.FunctionType;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;
import com.lehecai.admin.web.domain.finance.TerminalCompany;
import com.lehecai.admin.web.domain.finance.TerminalCompanyPoint;
import com.lehecai.admin.web.export.TerminalAccountCheckExport;
import com.lehecai.admin.web.service.customconfig.CustomFunctionConfigService;
import com.lehecai.admin.web.service.finance.TerminalAccountCheckItemService;
import com.lehecai.admin.web.service.finance.TerminalCompanyPointService;
import com.lehecai.admin.web.service.finance.TerminalCompanyService;
import com.lehecai.admin.web.service.ticket.TicketService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

/**
 * @author chirowong
 *
 */
public class TerminalAccountCheckAction extends BaseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4260678425602382212L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private TicketService ticketService;
	private TerminalAccountCheckItemService terminalAccountCheckItemService;
	private TerminalCompanyService terminalCompanyService;
	private TerminalCompanyPointService terminalCompanyPointService;
	private CustomFunctionConfigService customFunctionConfigService;
	
	private TerminalAccountCheckItem terminalAccountCheckItem;
	private List<TerminalCompany> terminalCompanyList;
	//创建出票商对账单参数
	private Long terminalCompanyId;
	private Integer lotteryTypeId;
	private Integer accountCheckType;
	private String phase;
	private Date beginDate;
	private String amountCheckDate;
	private Double point;//出票商点位
	
	//查询出票商对账单参数
	private Date queryBeginDate;
	private Date queryEndDate;
	List<TerminalAccountCheckItem> terminalAccountCheckItemList;
	
	//导出
	private InputStream inputStream;
	private String fileName;
	
	public String handle(){
		logger.info("进入出票商对账列表开始");
		terminalCompanyList = terminalCompanyService.list(new TerminalCompany(), super.getPageBean());
		logger.info("进入出票商对账列表结束");
		return "list";
	}
	
	public String query(){
		logger.info("查询出票商对账单开始");
		HttpServletRequest request = ServletActionContext.getRequest();
		terminalCompanyList = terminalCompanyService.list(new TerminalCompany(), super.getPageBean());
		List<TerminalAccountCheckItem> terminalAccountCheckItemQueryList = terminalAccountCheckItemService.list(terminalAccountCheckItem,queryBeginDate,queryEndDate, super.getPageBean());
		TerminalAccountCheckItem terminalAccountCheckItemTotal = terminalAccountCheckItemService.getTotal(terminalAccountCheckItem,queryBeginDate,queryEndDate, super.getPageBean());
		PageBean pageBean = terminalAccountCheckItemService.getPageBean(terminalAccountCheckItem,queryBeginDate,queryEndDate, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		if(terminalAccountCheckItemList == null) terminalAccountCheckItemList = new ArrayList<TerminalAccountCheckItem>();
		terminalAccountCheckItemTotal.setLotteryType(LotteryType.ALL);
		terminalAccountCheckItemTotal.setTerminalCompanyName("全部");
		terminalAccountCheckItemList.add(terminalAccountCheckItemTotal);
		terminalAccountCheckItemList.addAll(terminalAccountCheckItemQueryList);
		logger.info("查询出票商对账单结束");
		return "list";
	}
	
	/**
	 * 选择创建出票商对账单的条件
	 * @return
	 */
	public String preCreate(){
		logger.info("进入创建出票商对账单的查询条件");
		terminalCompanyList = terminalCompanyService.list(new TerminalCompany(), super.getPageBean());
		return "preCreate";
	}
	
	/**
	 * 创建出票商对账单
	 * @return
	 */
	public String create(){
		logger.info("创建出票商对账列表");
		if((terminalCompanyId == null) || (terminalCompanyId != null && terminalCompanyId.intValue() == -1)){
			logger.error("出票商不能为空");
			super.setErrorMessage("出票商不能为空");
			return preCreate();
		}
		
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
		TerminalCompany company = terminalCompanyService.get(terminalCompanyId);
		terminalAccountCheckItem = ticketService.getTerminalAccountCheckItem(company.getTerminalTypes(), lotteryTypeId, accountCheckType, amountCheckDate);
		terminalAccountCheckItem.setTerminalCompanyId(terminalCompanyId);
		TerminalCompany terminalCompany = terminalCompanyService.get(terminalCompanyId);
		TerminalCompanyPoint queryTcp = new TerminalCompanyPoint();
		queryTcp.setCompanyId(terminalCompany.getId());
		queryTcp.setLotteryType(LotteryType.getItem(lotteryTypeId));
		List<TerminalCompanyPoint> terminalCompanyPointList = terminalCompanyPointService.list(queryTcp, null);
		TerminalCompanyPoint terminalCompanyPoint = null;
		if(terminalCompanyPointList != null && terminalCompanyPointList.size() > 0){
			terminalCompanyPoint = terminalCompanyPointList.get(0);
		}
		if(terminalCompanyPoint != null){
			point = terminalCompanyPoint.getPoint();
		}else{
			logger.info("出票商点位为空，请先设置点位");
			super.setErrorMessage("出票商点位为空，请先设置点位");
			return "failure";
		}
		terminalAccountCheckItem.setTerminalCompanyName(terminalCompany.getName());
		terminalAccountCheckItem.setAccountCheckType(accountCheckType);
		terminalAccountCheckItem.setLotteryType(LotteryType.getItem(lotteryTypeId));
		terminalAccountCheckItem.setAmountCheckDate(amountCheckDate);
		return "create";
	}
	
	/**
	 * 保存出票商对账单
	 * @return
	 */
	public String saveAccountCheckBill(){
		logger.info("更新出票商对账单开始");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		if (terminalAccountCheckItem != null) {
			Long userId = terminalAccountCheckItem.getUserId();
			if(userId == null)
				terminalAccountCheckItem.setUserId(userSessionBean.getUser().getId());
			Date createTime = terminalAccountCheckItem.getCreateTime();
			if(createTime == null)
				terminalAccountCheckItem.setCreateTime(new Date());
			terminalAccountCheckItem.setLotteryType(LotteryType.getItem(lotteryTypeId));
			terminalAccountCheckItemService.manage(terminalAccountCheckItem);
			super.setForwardUrl("/finance/terminalAccountCheck.do"+groupQueryParam());
			logger.info("更新出票商对账单结束");
			return "success";
		}else{
			logger.error("更新出票商对账单错误，提交表单为空");
			super.setErrorMessage("更新出票商对账单错误，提交表单不能为空");
			return "failure";
		}
	}
	
	public String del(){
		logger.info("删除出票商对账单信息");
		if (terminalAccountCheckItem != null && terminalAccountCheckItem.getId() != null) {
			terminalAccountCheckItem = terminalAccountCheckItemService.get(terminalAccountCheckItem.getId());
			terminalAccountCheckItemService.del(terminalAccountCheckItem);
		} else {
			logger.error("删除出票商对账单，编码为空");
			super.setErrorMessage("删除出票商对账单，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/finance/terminalAccountCheck.do"+groupQueryParam());
		logger.info("删除出票商对账单结束");
		return "forward";
	}
	
	public String update(){
		logger.info("修改出票商对账单开始");
		if(terminalAccountCheckItem != null && terminalAccountCheckItem.getId() != null){
			terminalAccountCheckItem = terminalAccountCheckItemService.get(terminalAccountCheckItem.getId());
			TerminalCompany terminalCompany = terminalCompanyService.get(terminalAccountCheckItem.getTerminalCompanyId());
			TerminalCompanyPoint queryTcp = new TerminalCompanyPoint();
			queryTcp.setCompanyId(terminalCompany.getId());
			queryTcp.setLotteryType(terminalAccountCheckItem.getLotteryType());
			List<TerminalCompanyPoint> terminalCompanyPointList = terminalCompanyPointService.list(queryTcp, null);
			TerminalCompanyPoint terminalCompanyPoint = null;
			if(terminalCompanyPointList != null && terminalCompanyPointList.size() > 0){
				terminalCompanyPoint = terminalCompanyPointList.get(0);
			}
			if(terminalCompanyPoint != null){
				point = terminalCompanyPoint.getPoint();
			}else{
				logger.info("出票商点位为空，请先设置点位");
				super.setErrorMessage("出票商点位为空，请先设置点位");
				return "failure";
			}
		}else{
			logger.info("修改出票商对账单，编码为空");
			super.setErrorMessage("修改出票商对账单，编码为空");
			return "failure";
		}
		logger.info("修改出票商对账单结束");
		return "create";
	}
	
	/**
	 * 入账
	 * @return
	 */
	public String enterAccount(){
		logger.info("更新出票商对账单入账开始");
		if (terminalAccountCheckItem != null && terminalAccountCheckItem.getId() != null) {
			terminalAccountCheckItem = terminalAccountCheckItemService.get(terminalAccountCheckItem.getId());
			terminalAccountCheckItem.setEnterAccount(true);
			terminalAccountCheckItemService.manage(terminalAccountCheckItem);
			super.setForwardUrl("/finance/terminalAccountCheck.do"+groupQueryParam());
			logger.info("更新出票商对账单入账结束");
			return "success";
		}else{
			logger.error("更新出票商对账单错误，提交表单为空");
			super.setErrorMessage("更新出票商对账单错误，提交表单不能为空");
			return "failure";
		}
	}
	
	/**
	 * 导出报表
	 * @return
	 */
	public String export(){
		logger.info("导出出票商对账单开始");
		PageBean noPageBean = new PageBean();
		noPageBean.setPageFlag(false);
		List<TerminalAccountCheckItem> terminalAccountCheckItemQueryList = terminalAccountCheckItemService.list(terminalAccountCheckItem,queryBeginDate,queryEndDate, noPageBean);
		TerminalAccountCheckItem terminalAccountCheckItemTotal = terminalAccountCheckItemService.getTotal(terminalAccountCheckItem,queryBeginDate,queryEndDate, noPageBean);
		if(terminalAccountCheckItemList == null) terminalAccountCheckItemList = new ArrayList<TerminalAccountCheckItem>();
		terminalAccountCheckItemTotal.setLotteryType(LotteryType.ALL);
		terminalAccountCheckItemTotal.setTerminalCompanyName("全部");
		terminalAccountCheckItemList.add(terminalAccountCheckItemTotal);
		terminalAccountCheckItemList.addAll(terminalAccountCheckItemQueryList);
		
		try {
			Workbook workBook = TerminalAccountCheckExport.export(terminalAccountCheckItemList);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			workBook.write(os);
			inputStream  = new ByteArrayInputStream(os.toByteArray());
			this.fileName = (new Date()).getTime() + ".xls";
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("文件输出流写入错误");
			return "failure";
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("API返回数据有误，生成excel文件时错误");
			return "failure";
		}
		logger.info("导出出票商对账单结束");
		return "download";
	}
	
	private String groupQueryParam(){
		StringBuffer sb = new StringBuffer();
		sb.append("?action=query");
		if(terminalAccountCheckItem != null && terminalAccountCheckItem.getTerminalCompanyId() != null){
			sb.append("&terminalAccountCheckItem.terminalCompanyId=").append(terminalAccountCheckItem.getTerminalCompanyId());
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

	public Long getTerminalCompanyId() {
		return terminalCompanyId;
	}

	public void setTerminalCompanyId(Long terminalCompanyId) {
		this.terminalCompanyId = terminalCompanyId;
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

	public TerminalAccountCheckItem getTerminalAccountCheckItem() {
		if(terminalAccountCheckItem == null) terminalAccountCheckItem = new TerminalAccountCheckItem();
		return terminalAccountCheckItem;
	}

	public void setTerminalAccountCheckItem(
			TerminalAccountCheckItem terminalAccountCheckItem) {
		this.terminalAccountCheckItem = terminalAccountCheckItem;
	}

	public TerminalCompanyService getTerminalCompanyService() {
		return terminalCompanyService;
	}

	public void setTerminalCompanyService(
			TerminalCompanyService terminalCompanyService) {
		this.terminalCompanyService = terminalCompanyService;
	}

	public List<TerminalCompany> getTerminalCompanyList() {
		return terminalCompanyList;
	}

	public void setTerminalCompanyList(List<TerminalCompany> terminalCompanyList) {
		this.terminalCompanyList = terminalCompanyList;
	}

	public TerminalAccountCheckItemService getTerminalAccountCheckItemService() {
		return terminalAccountCheckItemService;
	}

	public void setTerminalAccountCheckItemService(
			TerminalAccountCheckItemService terminalAccountCheckItemService) {
		this.terminalAccountCheckItemService = terminalAccountCheckItemService;
	}

	public CustomFunctionConfigService getCustomFunctionConfigService() {
		return customFunctionConfigService;
	}

	public void setCustomFunctionConfigService(
			CustomFunctionConfigService customFunctionConfigService) {
		this.customFunctionConfigService = customFunctionConfigService;
	}

	public Double getPoint() {
		return point;
	}

	public void setPoint(Double point) {
		this.point = point;
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

	public List<TerminalAccountCheckItem> getTerminalAccountCheckItemList() {
		return terminalAccountCheckItemList;
	}

	public void setTerminalAccountCheckItemList(
			List<TerminalAccountCheckItem> terminalAccountCheckItemList) {
		this.terminalAccountCheckItemList = terminalAccountCheckItemList;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public LotteryType getAllType() {
		return LotteryType.ALL;
	}
	
	/**
	 * 是否可修改权限
	 * 财务主管/运营总监/商务总监/CEO/admin
	 * @return
	 */
	public boolean getAdminAuthority(){
		FunctionType functionType = FunctionType.TERMINALACCOUNTCHECK;
		CustomFunctionConfig config = new CustomFunctionConfig();
		config.setFunctionType(functionType);
		List<CustomFunctionConfig> customFunctionConfigList = customFunctionConfigService.list(config, null);
		if(customFunctionConfigList != null && customFunctionConfigList.size() > 0){
			config = customFunctionConfigList.get(0);
			HttpServletRequest request = ServletActionContext.getRequest();
			UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
			String roles = config.getRoles();
			if(roles != null && !"".equals(roles)){
				String[] arrRole = roles.split(",");
				for(String roleId : arrRole){
					if(userSessionBean.getRole().getId().longValue() == Long.parseLong(roleId.trim())){
						return true;
					}
				}
			}
		}
		return false;
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

	public TerminalCompanyPointService getTerminalCompanyPointService() {
		return terminalCompanyPointService;
	}

	public void setTerminalCompanyPointService(
			TerminalCompanyPointService terminalCompanyPointService) {
		this.terminalCompanyPointService = terminalCompanyPointService;
	}
}
