/**
 * 
 */
package web.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qatang
 *
 */
public abstract class AbstractDelegate implements JavaDelegate {
	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		this.doExecution(execution);
	}
	
	abstract protected void doExecution(DelegateExecution execution) throws Exception;
}
