/**
 * 
 */
package web.activiti.task.recharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.activiti.form.RechargeTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;

/**
 * @author qatang
 * @author chirowong 增加分页功能
 */
public class ClaimRechargeTask extends CommonProcessTask {
	
	public Map<String, Object> listByRoleOrUser(String roleId, String userId,PageBean pageBean) {
		if (StringUtils.isEmpty(roleId)) {
			logger.error("处理汇款充值工单异常：按角色查询任务列表时，角色id为空");
			return null;
		}
		if (StringUtils.isEmpty(userId)) {
			logger.error("处理汇款充值工单异常：按用户查询任务列表时，用户id为空");
			return null;
		}
		logger.info("处理汇款充值工单：查询roleId={}角色的任务列表", roleId);
		int totalCount = 0;
		int currentPage = 0;
		int pageSize = 0;
		if (pageBean != null && pageBean.isPageFlag()){
			currentPage = pageBean.getPage();
			pageSize = pageBean.getPageSize();
		}
		List<Task> allTaskList = null;
		totalCount = (int)taskService.createTaskQuery().processDefinitionKey("rechargeProcess").taskCandidateGroup(roleId).count();
		if(totalCount == 0){
			logger.info("处理汇款充值工单：查询userID={}用户的任务列表", userId);
			totalCount = (int)taskService.createTaskQuery().processDefinitionKey("rechargeProcess").taskCandidateUser(userId).count();
			if(totalCount == 0){
				return null;
			}else{
				int beginRecord = 0;
				beginRecord = (currentPage-1)*pageSize;
				allTaskList = taskService.createTaskQuery().processDefinitionKey("rechargeProcess").taskCandidateUser(userId).orderByTaskCreateTime().desc().listPage(beginRecord,pageSize);
			}
		}else{
			logger.info("处理汇款充值工单：查询roleId={}角色的任务列表", roleId);
			int beginRecord = 0;
			beginRecord = (currentPage-1)*pageSize;
			allTaskList = taskService.createTaskQuery().processDefinitionKey("rechargeProcess").taskCandidateGroup(roleId).orderByTaskCreateTime().desc().listPage(beginRecord,pageSize);
		}
		Map<String, Object> map = null;
		if(allTaskList != null && allTaskList.size() > 0){
			List<RechargeTaskForm> rechargeTaskFormList = new ArrayList<RechargeTaskForm>();
			for (Task task : allTaskList) {
				RechargeTaskForm rechargeTaskForm = (RechargeTaskForm) this.getVariable(task.getProcessInstanceId(), "rechargeTaskForm");
				rechargeTaskForm.setTaskId(task.getId());
				rechargeTaskForm.setProcessId(task.getProcessInstanceId());
				rechargeTaskFormList.add(rechargeTaskForm);
			}
			
			if (pageBean != null && pageBean.isPageFlag()) {
				pageBean.setCount(totalCount);
				
				int pageCount = 0;//页数
				if ( pageBean.getPageSize() != 0 ) {
		            pageCount = totalCount / pageBean.getPageSize();
		            if (totalCount % pageBean.getPageSize() != 0) {
		                pageCount ++;
		            }
		        }
				pageBean.setPageCount(pageCount);
			}
			map = new HashMap<String, Object>();
			map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
			map.put(Global.API_MAP_KEY_LIST, rechargeTaskFormList);
		}
		return map;
	}
}
