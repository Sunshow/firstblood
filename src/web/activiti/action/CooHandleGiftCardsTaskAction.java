/**
 * 
 */
package web.activiti.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.constant.ProcessStatusType;
import com.lehecai.admin.web.activiti.entity.GiftCardsTask;
import com.lehecai.admin.web.activiti.form.GiftCardsTaskForm;
import com.lehecai.admin.web.activiti.service.GiftCardsTaskService;
import com.lehecai.admin.web.activiti.task.giftcards.CooHandleGiftCardsTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.process.AmountSettingService;

/**
 * @author chirowong
 * 处理彩金派送流程
 */
public class CooHandleGiftCardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private CooHandleGiftCardsTask cooHandleGiftCardsTask;
	@Autowired
	private AmountSettingService amountSettingService;
	@Autowired
	private GiftCardsTaskService giftCardsTaskService;
	
	private List<GiftCardsTaskForm> giftCardsTaskFormList;
	private GiftCardsTaskForm giftCardsTaskForm;
	private String taskId;
	private String processId;
	private Double cardMoney;
	private Integer cardAmount;
	private Long id;
	private String memo;
	
	private static boolean running = false;
	private static Object __lock__ = new Object();
	
	public String handle() {
		logger.info("获取彩金卡申请任务列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		giftCardsTaskFormList = cooHandleGiftCardsTask.listByRoleOrUser(userSessionBean.getRole().getId() + "", userSessionBean.getUser().getId() + "");
		return "list";
	}
	
	public String claim() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		boolean flag = true;
		String message = "";
		if (giftCardsTaskFormList == null || giftCardsTaskFormList.size() == 0) {
			logger.error("认领彩金卡申请工单失败，原因：giftCardsTaskFormList为空");
			super.setErrorMessage("认领彩金卡申请工单失败，原因：giftCardsTaskFormList为空");
			flag = false;
		}
		if (flag) {
			try {
				for (GiftCardsTaskForm giftCardsTaskForm : giftCardsTaskFormList) {
					Task dbTask = cooHandleGiftCardsTask.queryTask(giftCardsTaskForm.getTaskId());
					if (dbTask != null && dbTask.getAssignee() == null) {
						cooHandleGiftCardsTask.claim(giftCardsTaskForm.getTaskId(), userSessionBean.getUser().getId() + "");
					} else {
						String code = giftCardsTaskForm.getCode() == null ? "" : giftCardsTaskForm.getCode();
						logger.error("认领彩金卡工单失败,原因：编码" + code + "已处理");
						if (!message.equals("")) {
							message += ",";
						}
						message += code;
					}
				}
				if (message.equals("")) {
					message = "批量认领成功，确定跳转至我的任务列表";
				} else {
					message = "认领彩金卡工单中编号" + message + "已认领，确定跳转至我的任务列表";
				}
			} catch(Exception e) {
				logger.error("批量认领失败，原因{}", e.getMessage());
				message = "批量认领失败，原因" + e.getMessage();
				flag = false;
			}
		}
		JSONObject object = new JSONObject();
		object.put("message", message);
		object.put("flag", flag);
		super.writeRs(response, object);
		return null;
	}
	
	public String listUserTask() {
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		giftCardsTaskFormList = cooHandleGiftCardsTask.listByAssignee(userSessionBean.getUser().getId() + "");
		return "userTaskList";
	}
	
	public String agreeTask(){
		logger.info("通过彩金卡申请任务");
		
		if (running) {
			return null;
		}
		synchronized (__lock__) {
			running = true;
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		try {
			logger.info("计算操作人员额度！");
			Task dbTask = cooHandleGiftCardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该彩金卡工单已处理");
				super.setErrorMessage("该彩金卡工单已处理");
				super.setForwardUrl("/process/cooHandleGiftCardsTask.do?action=listUserTask");
				return "failure";
			}
			
			Double rechargeAmount = amountSettingService.auditAmount(CooHandleGiftCardsTask.GIFT_CARDS_PROCESS, CooHandleGiftCardsTask.COO_HANDLE_GIFT_CARDS_TASK, userSessionBean.getUser().getId(),userSessionBean.getUser().getRoleID());
			rechargeAmount = rechargeAmount == null ? 0.0D : rechargeAmount;
			
			Double amount = 0.00D;
			boolean enough = false;
			amount = cardMoney * cardAmount;
			if (amount <= rechargeAmount) {
				enough = true;
			}
			if (enough) {
				GiftCardsTask giftCardsTask = giftCardsTaskService.get(id);
				giftCardsTask.setHandleUser(userSessionBean.getUser().getId());
				giftCardsTask.setStatus(ProcessStatusType.COMPLETE.getValue());
				giftCardsTaskService.merge(giftCardsTask);
				logger.info(userSessionBean.getUser().getName()+"通过彩金卡申请,额度充足！");
				super.setSuccessMessage("处理成功，彩金卡已生成！");
			} else {
				logger.info(userSessionBean.getUser().getName()+"通过彩金卡申请,但您的可用额度不够，系统将该工单自动提交给上一级主管！");
				super.setSuccessMessage("处理成功，但您可用额度不够，系统将该工单自动提交给上一级主管！");
			}
			cooHandleGiftCardsTask.agreeTask(enough, taskId);
			
		} catch (Exception e) {
			logger.error("通过彩金卡工单失败，原因{}", e.getMessage());
			super.setErrorMessage("通过彩金卡工单失败，原因" + e.getMessage());
			super.setForwardUrl("/process/cooHandleGiftCardsTask.do?action=listUserTask");
			return "failure";
		} finally {
			synchronized (__lock__) {
				running = false;
			}
		}
		return "success";
	}
	
	public String disagreeTask(){
		return "disagree";
	}
	
	public String disagreeTaskReason(){
		logger.info("拒绝彩金卡申请");
		try {
			Task dbTask = cooHandleGiftCardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该彩金卡工单已处理");
				super.setErrorMessage("该彩金卡工单已处理");
				super.setForwardUrl("/process/cooHandleGiftCardsTask.do?action=listUserTask");
				return "failure";
			}
			GiftCardsTask giftCardsTask = giftCardsTaskService.get(id);
			giftCardsTask.setStatus(ProcessStatusType.REJECT.getValue());
			giftCardsTask.setMemo(memo);
			giftCardsTaskService.merge(giftCardsTask);
			cooHandleGiftCardsTask.disAgreeTask(taskId);
			super.setSuccessMessage("拒绝彩金卡申请，原因："+memo);
		} catch (Exception e) {
			logger.error("拒绝彩金卡工单失败，原因{}", e.getMessage());
			super.setErrorMessage("拒绝彩金卡工单失败，原因" + e.getMessage());
			super.setForwardUrl("/process/cooHandleGiftCardsTask.do?action=listUserTask");
			return "failure";
		}
		return "success";
	}

	public CooHandleGiftCardsTask getCooHandleGiftCardsTask() {
		return cooHandleGiftCardsTask;
	}

	public void setCooHandleGiftCardsTask(
			CooHandleGiftCardsTask cooHandleGiftCardsTask) {
		this.cooHandleGiftCardsTask = cooHandleGiftCardsTask;
	}

	public List<GiftCardsTaskForm> getGiftCardsTaskFormList() {
		return giftCardsTaskFormList;
	}

	public void setGiftCardsTaskFormList(
			List<GiftCardsTaskForm> giftCardsTaskFormList) {
		this.giftCardsTaskFormList = giftCardsTaskFormList;
	}

	public GiftCardsTaskForm getGiftCardsTaskForm() {
		return giftCardsTaskForm;
	}

	public void setGiftCardsTaskForm(GiftCardsTaskForm giftCardsTaskForm) {
		this.giftCardsTaskForm = giftCardsTaskForm;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Double getCardMoney() {
		return cardMoney;
	}

	public void setCardMoney(Double cardMoney) {
		this.cardMoney = cardMoney;
	}

	public Integer getCardAmount() {
		return cardAmount;
	}

	public void setCardAmount(Integer cardAmount) {
		this.cardAmount = cardAmount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
}
