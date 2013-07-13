/**
 * 
 */
package web.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qatang
 *
 */
public abstract class AbstractExecutionListener implements ExecutionListener {
	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		this.doNotify(execution);
	}
	
	abstract void doNotify(DelegateExecution execution) throws Exception;
}
