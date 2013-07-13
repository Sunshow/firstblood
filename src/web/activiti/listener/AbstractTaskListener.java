/**
 * 
 */
package web.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qatang
 *
 */
public abstract class AbstractTaskListener implements TaskListener {
	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void notify(DelegateTask delegateTask) {
		this.doNotify(delegateTask);
	}
	
	/**
	 * @param delegateTask
	 */
	abstract protected void doNotify(DelegateTask delegateTask);
}
