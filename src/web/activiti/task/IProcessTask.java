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
public interface IProcessTask {
	/**
	 * 开始流程
	 * @param processKey
	 * @param variables
	 * @return ProcessInstance
	 */
	public ProcessInstance start(String processKey, Map<String, Object> variables);
	/**
	 * 认领任务
	 * @param taskId
	 * @param userId
	 */
	public void claim(String taskId, String userId);
	/**
	 * 完成任务
	 * @param taskId
	 * @param variables
	 */
	public void complete(String taskId, Map<String, Object> variables);
	/**
	 * 删除任务
	 * @param taskId
	 */
	public void delete(String taskId);
	/**
	 * 查询参数
	 * @param executionId
	 * @param key
	 * @return Object
	 */
	public Object getVariable(String executionId, String key);
	/**
	 * 设置参数
	 * @param executionId
	 * @param key
	 * @return Object
	 */
	public void setVariable(String executionId, String key, Object value);
	/**
	 * 根据任务编码查询任务
	 * @param taskId
	 * @return
	 */
	public Task queryTask(String taskId);
}
