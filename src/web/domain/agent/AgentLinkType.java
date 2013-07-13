package web.domain.agent;
import java.io.Serializable;


public class AgentLinkType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7523487116027920700L;
	private Long id;
	private String name;
	private String template;
	private boolean valid;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
