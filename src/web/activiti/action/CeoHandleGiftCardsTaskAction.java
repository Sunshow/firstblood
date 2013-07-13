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
import com.lehecai.admin.web.activiti.task.giftcards.CeoHandleGiftCardsTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;

/**
 * @author chirowong
 * 处理彩金卡申请流程
 */
public class CeoHandleGiftCardsTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private CeoHandleGiftCardsTask ceoHandleGiftCardsTask;
	@Autowired
	private GiftCardsTaskService giftCardsTaskService;
	
	private List<GiftCardsTaskForm> giftCardsTaskFormList;
	private GiftCardsTaskForm giftCardsTaskForm;
	
	private String memo;
	private String taskId;
	private Long id;
	
	private static boolean running = false;
	private static Object __lock__ = new Object();
	
	public String handle() {
		logger.info("获取彩金卡申请任务列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		giftCardsTaskFormList = ceoHandleGiftCardsTask.listByRoleOrUser(userSessionBean.getRole().getId() + "", userSessionBean.getUser().getId() + "");
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
			message = "认领彩金卡申请工单失败，原因：giftCardsTaskFormList为空";
			flag = false;
		}
		if (flag) {
			try {
				for (GiftCardsTaskForm giftCardsTaskForm : giftCardsTaskFormList) {
					
					Task dbTask = ceoHandleGiftCardsTask.queryTask(giftCardsTaskForm.getTaskId());
					if (dbTask != null && dbTask.getAssignee() == null) {
						ceoHandleGiftCardsTask.claim(giftCardsTaskForm.getTaskId(), userSessionBean.getUser().getId() + "");
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
		giftCardsTaskFormList = ceoHandleGiftCardsTask.listByAssignee(userSessionBean.getUser().getId() + "");
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
			Task dbTask = ceoHandleGiftCardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该彩金卡工单已处理");
				super.setErrorMessage("该彩金卡工单已处理");
				super.setForwardUrl("/process/ceoHandleGiftCardsTask.do?action=listUserTask");
				return "failure";
			}
			
			GiftCardsTask giftCardsTask = giftCardsTaskService.get(id);
			giftCardsTask.setHandleUser(userSessionBean.getUser().getId());
			giftCardsTask.setStatus(ProcessStatusType.COMPLETE.getValue());
			giftCardsTaskService.merge(giftCardsTask);
			ceoHandleGiftCardsTask.agreeTask(taskId);
			logger.info(userSessionBean.getUser().getName()+"通过彩金卡申请！");
			super.setSuccessMessage("处理成功，彩金卡已生成！");
		} catch (Exception e) {
			logger.error("通过彩金卡工单失败，原因{}", e.getMessage());
			super.setErrorMessage("通过彩金卡工单失败，原因" + e.getMessage());
			super.setForwardUrl("/process/ceoHandleGiftCardsTask.do?action=listUserTask");
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
			Task dbTask = ceoHandleGiftCardsTask.queryTask(taskId);
			if (dbTask == null) {
				logger.error("该彩金卡工单已处理");
				super.setErrorMessage("该彩金卡工单已处理");
				super.setForwardUrl("/process/ceoHandleGiftCardsTask.do?action=listUserTask");
				return "failure";
			}
			GiftCardsTask giftCardsTask = giftCardsTaskService.get(id);
			giftCardsTask.setMemo(memo);
			giftCardsTask.setStatus(ProcessStatusType.REJECT.getValue());
			giftCardsTaskService.merge(giftCardsTask);
			ceoHandleGiftCardsTask.disAgreeTask(taskId);
			super.setSuccessMessage("拒绝彩金卡申请，原因："+memo);
		} catch (Exception e) {
			logger.error("拒绝彩金卡工单处理失败，原因：{}", e.getMessage());
			super.setErrorMessage("拒绝彩金卡工单处理失败，原因：" + e.getMessage());
			super.setForwardUrl("/process/ceoHandleGiftCardsTask.do?action=listUserTask");
			return "failure";
		}
		return "success";
	}
	
	public CeoHandleGiftCardsTask getCeoHandleGiftCardsTask() {
		return ceoHandleGiftCardsTask;
	}

	public void setCeoHandleGiftCardsTask(
			CeoHandleGiftCardsTask ceoHandleGiftCardsTask) {
		this.ceoHandleGiftCardsTask = ceoHandleGiftCardsTask;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
