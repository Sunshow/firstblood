package web.domain.process;
import java.io.Serializable;

public class WorkProcess implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String processId;
	private String processName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
}
