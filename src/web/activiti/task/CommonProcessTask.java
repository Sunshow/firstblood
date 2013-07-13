/**
 * 
 */
package web.activiti.task;

import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

/**
 * @author qatang
 *
 */
public class CommonProcessTask extends AbstractProcessTask {
	
	@Override
	public ProcessInstance start(String processKey, Map<String, Object> variables) {
		if (variables == null) {
			return runtimeService.startProcessInstanceByKey(processKey);
		}
		return runtimeService.startProcessInstanceByKey(processKey, variables);
	}
	
	@Override
	public void claim(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}

	@Override
	public void complete(String taskId, Map<String, Object> variables) {
		if (variables == null) {
			taskService.complete(taskId);
		} else {
			taskService.complete(taskId, variables);
		}
	}
	
	@Override
	public void delete(String processId){
		Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		taskService.deleteTask(task.getId());
	}

	@Override
	public Object getVariable(String executionId, String key) {
		return runtimeService.getVariable(executionId, key);
	}

	@Override
	public void setVariable(String executionId, String key, Object value) {
		runtimeService.setVariable(executionId, key, value);
	}
	
	@Override
	public Task queryTask(String taskId){
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		return task;
	}
}
