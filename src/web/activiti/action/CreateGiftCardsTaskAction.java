/**
 * 
 */
package web.activiti.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.entity.HistoryQueryCondition;
import com.lehecai.admin.web.activiti.entity.HistoryQueryObject;
import com.lehecai.admin.web.activiti.form.GiftCardsTaskForm;
import com.lehecai.admin.web.activiti.service.GiftCardsTaskService;
import com.lehecai.admin.web.activiti.service.WorkFlowHistoryService;
import com.lehecai.admin.web.activiti.task.giftcards.StartGiftCardsTask;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.export.GiftCardsExport;
import com.lehecai.admin.web.service.event.EventService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.process.CouponGenerateService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.event.Coupon;
import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.event.EventInfoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.CouponType;

/**
 * @author chirowong
 *
 */
public class CreateGiftCardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private StartGiftCardsTask startGiftCardsTask;
	@Autowired
	private EventService eventService;
	@Autowired
	private MemberService memberService;

	private WorkFlowHistoryService workFlowHistoryService;
	private CouponGenerateService couponGenerateService;
	
	@Autowired
	private GiftCardsTaskService giftCardsTaskService;
	
	private List<HistoryQueryObject> historyQueryObjectList;
	
	private GiftCardsTaskForm giftCardsTaskForm;
	private List<EventInfo> eventInfoList;
	private Date beginTime;//开始时间
	private Date endTime;//结束时间
	private String processDefinitionKey;
	private Integer finished;
	private String processId;//工作流id
	private InputStream inputStream;
	private String fileName;//导出文件名称
	
	private static boolean running = false;
	private static Object __lock__ = new Object();

	public String handle() {
		logger.info("工作流查询开始");
		HistoryQueryObject condition = new HistoryQueryObject();
		processDefinitionKey = HistoryQueryCondition.GIFTCARDSROCESS.getProcessName();
		condition.setProcessName(processDefinitionKey);
		condition.setProcessTable(HistoryQueryCondition.getTableName(processDefinitionKey));
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		User user = userSessionBean.getUser();
		condition.setInitiator(user.getUserName());
		Boolean finishFlag = null;
		if (finished != null) {
			if (finished == YesNoStatus.YES.getValue()) {
				finishFlag = true;
			} else if (finished == YesNoStatus.NO.getValue()) {
				finishFlag = false;
			}
		}
		setHistoryQueryObjectList(workFlowHistoryService.queryGiftCardsHistory(condition, super.getPageBean(), finishFlag, beginTime, endTime));
		PageBean pageBean = workFlowHistoryService.queryHistoryPageBean(condition, super.getPageBean(), finishFlag, beginTime, endTime);
		HttpServletRequest request = ServletActionContext.getRequest();
		super.setPageString(PageUtil.getPageString(request, pageBean));
		logger.info("工作流查询结束");
		return "list";
	}
	
	public String input() {
		logger.info("创建彩金赠送工单");
		return "inputForm";
	}
	
	@SuppressWarnings("unchecked")
	public String export() {
		logger.info("进入导出彩金卡");
        Map<String, Object> map = null;
        try {
            map = couponGenerateService.queryCouponList(processId, null);
        } catch (ApiRemoteCallFailedException e) {
            logger.error("导出彩金卡,api调用异常" + e.getMessage());
            super.setErrorMessage("导出彩金卡,api调用异常" + e.getMessage());
            return "failure";
        }
        if (map == null || map.size() == 0) {
            logger.error("API导出彩金卡信息为空");
            super.setErrorMessage("API导出彩金卡信息为空");
            return "failure";
        }
        try {
        	List<Coupon> list = (List<Coupon>) map.get(Global.API_MAP_KEY_LIST);
        	if (list == null || list.size() == 0) {
        		 logger.error("没有与流程编码对应的彩金卡数据,历史数据可能出现该情况");
                 super.setErrorMessage("没有与流程编码对应的彩金卡数据,历史数据可能出现该情况");
                 return "failure";
        	}
			Workbook workBook = GiftCardsExport.export(list);
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
        logger.info("导出彩金卡结束");
        return "export";
	}
	
	@SuppressWarnings("unchecked")
	public String enableAndExport () {
		logger.info("进入激活并导出彩金卡");
		if (running) {
			logger.info("激活程序正在执行，不能重复操作");
			super.setErrorMessage("激活程序正在执行，不能重复操作");
			return "failure";
		}
		synchronized (__lock__) {
			running = true;
		}
        Map<String, Object> map = null;
        try {
        	couponGenerateService.enableCouponByProcessId(processId);
        	map = couponGenerateService.queryCouponList(processId, null);
        	if (map == null || map.size() == 0) {
                logger.error("API导出彩金卡信息为空");
                super.setErrorMessage("API导出彩金卡信息为空");
                return "failure";
            }
        	List<Coupon> list = (List<Coupon>) map.get(Global.API_MAP_KEY_LIST);
        	if (list == null || list.size() == 0) {
        		 logger.error("没有与流程编码对应的彩金卡数据,历史数据可能出现该情况");
                 super.setErrorMessage("没有与流程编码对应的彩金卡数据,历史数据可能出现该情况");
                 return "failure";
        	}
			Workbook workBook = GiftCardsExport.export(list);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			workBook.write(os);
			inputStream  = new ByteArrayInputStream(os.toByteArray());
			this.fileName = "giftCard_"+(new Date()).getTime() + ".xls";
        } catch (IOException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("文件输出流写入错误");
			return "failure";
		} catch (ApiRemoteCallFailedException e) {
        	logger.error("激活并导出彩金卡,api调用异常" + e.getMessage());
            super.setErrorMessage("激活并导出彩金卡,api调用异常" + e.getMessage());
            return "failure";
        } catch (Exception e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("API返回数据有误，生成excel文件时错误");
			return "failure";
		} finally {
			synchronized (__lock__) {
				running = false;
			}
		}
        logger.info("激活并导出彩金卡结束");
        return "export";
	}
	
	
	public String manage() {
		logger.info("提交彩金赠送工单，启动彩金赠送工作流程");
		if (giftCardsTaskForm == null) {
			logger.error("提交彩金赠送工单失败，原因：GiftCardsTaskForm为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：GiftCardsTaskForm为空");
			return "inputForm";
		}
		if (giftCardsTaskForm.getGiftCardsTask().getCardMoney() == null || giftCardsTaskForm.getGiftCardsTask().getCardMoney() <= 0.00D) {
			logger.error("提交彩金赠送工单失败，原因：彩金卡面额cardMoney为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：彩金卡面额cardMoney为空");
			return "inputForm";
		}
		if (giftCardsTaskForm.getGiftCardsTask().getCardAmount() == null || giftCardsTaskForm.getGiftCardsTask().getCardAmount() <= 0L) {
			logger.error("提交彩金赠送工单失败，原因：彩金卡数量cardAmount为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：彩金卡数量cardAmount为空");
			return "inputForm";
		}
		if (giftCardsTaskForm.getGiftCardsTask().getCouponType() == CouponType.COUPON_TYPE_USER_BOUND.getValue()) {
			if(giftCardsTaskForm.getGiftCardsTask().getUserId() == null){
				logger.error("提交彩金赠送工单失败，原因：彩金卡类型为“绑定用户”时，用户编码为空");
				super.setErrorMessage("提交彩金赠送工单失败，原因：彩金卡类型为“绑定用户”时，用户编码为空");
				return "inputForm";
			}else {
				Member member = null;
				try {
					member = memberService.get(giftCardsTaskForm.getGiftCardsTask().getUserId());
				} catch (ApiRemoteCallFailedException e) {
					logger.error("提交彩金赠送工单失败，原因：获取用户信息失败");
					super.setErrorMessage("提交彩金赠送工单失败，原因：获取用户信息失败");
					return "inputForm";
				}
				if(member == null) {
					logger.error("提交彩金赠送工单失败，原因：用户编码无效");
					super.setErrorMessage("提交彩金赠送工单失败，原因：用户编码无效");
					return "inputForm";
				}
			}
		}
		if (StringUtils.isEmpty(giftCardsTaskForm.getGiftCardsTask().getLiveTime())) {
			logger.error("提交彩金赠送工单失败，原因：彩金卡使用期限liveTime为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：彩金卡使用期限liveTime为空");
			return "inputForm";
		}
		if (StringUtils.isEmpty(giftCardsTaskForm.getGiftCardsTask().getActivityContent())) {
			logger.error("提交彩金赠送工单失败，原因：活动内容activityContent为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：活动内容activityContent为空");
			return "inputForm";
		}
		if (StringUtils.isEmpty(giftCardsTaskForm.getGiftCardsTask().getReason())) {
			logger.error("提交彩金赠送工单失败，原因：赠予原因reason为空");
			super.setErrorMessage("提交彩金赠送工单失败，原因：赠予原因reason为空");
			return "inputForm";
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		giftCardsTaskForm.getGiftCardsTask().setInitiator(userSessionBean.getUser().getUserName());
		
		Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("giftCardsTaskForm", giftCardsTaskForm);
		
	    startGiftCardsTask.start(variables);
	    
	    super.setSuccessMessage("创建彩金卡申请工单成功！");
		return "success";
	}

	public GiftCardsTaskForm getGiftCardsTaskForm() {
		return giftCardsTaskForm;
	}

	public void setGiftCardsTaskForm(GiftCardsTaskForm giftCardsTaskForm) {
		this.giftCardsTaskForm = giftCardsTaskForm;
	}
	
	@SuppressWarnings("unchecked")
	public List<EventInfo> getEventInfoList() {
		Map<String, Object> map = null;
		try{
			map = eventService.findEventInfoListByCondition(null, null, null, 
					null, null, null, EventInfoStatus.OPEN, super.getPageBean());//条件查询所有活动
		}catch(ApiRemoteCallFailedException e){
			logger.error("查询抽奖活动，api调用异常，{}", e.getMessage());
		}
		if (map != null) {
			eventInfoList = (List<EventInfo>)map.get(Global.API_MAP_KEY_LIST);
		}
		return eventInfoList;
	}

	public void setEventInfoList(List<EventInfo> eventInfoList) {
		this.eventInfoList = eventInfoList;
	}

	public List<CouponType> getCouponTypes(){
		return CouponType.getItems();
	}
	
	public List<YesNoStatus> getFinishedList(){
		return YesNoStatus.getItemsForQuery();
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public void setHistoryQueryObjectList(List<HistoryQueryObject> historyQueryObjectList) {
		this.historyQueryObjectList = historyQueryObjectList;
	}

	public List<HistoryQueryObject> getHistoryQueryObjectList() {
		return historyQueryObjectList;
	}
	
	public WorkFlowHistoryService getWorkFlowHistoryService() {
		return workFlowHistoryService;
	}

	public void setWorkFlowHistoryService(
			WorkFlowHistoryService workFlowHistoryService) {
		this.workFlowHistoryService = workFlowHistoryService;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public void setFinished(Integer finished) {
		this.finished = finished;
	}

	public Integer getFinished() {
		return finished;
	}

	public void setGiftCardsTaskService(GiftCardsTaskService giftCardsTaskService) {
		this.giftCardsTaskService = giftCardsTaskService;
	}

	public GiftCardsTaskService getGiftCardsTaskService() {
		return giftCardsTaskService;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setCouponGenerateService(CouponGenerateService couponGenerateService) {
		this.couponGenerateService = couponGenerateService;
	}

	public CouponGenerateService getCouponGenerateService() {
		return couponGenerateService;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

}
