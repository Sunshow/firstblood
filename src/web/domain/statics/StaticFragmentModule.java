package web.domain.statics;

import java.io.Serializable;

public class StaticFragmentModule implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String moduleName;			//模板名称
	private String memo;				//模板描述
	
	public StaticFragmentModule () {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
