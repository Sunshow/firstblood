package web.domain.statics;

import java.io.Serializable;

public class DataConsolidation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String memo;
	
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
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
}
