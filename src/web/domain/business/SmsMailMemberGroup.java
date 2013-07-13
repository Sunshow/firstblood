package web.domain.business;

import java.io.Serializable;

public class SmsMailMemberGroup implements Serializable{
	private static final long serialVersionUID = 5219527163468935094L;
	
	private Long id;			//会员组编号
	private String name;		//会员组名称
	private boolean valid;		//是否有效
	private String memo;		//备注
	
	public SmsMailMemberGroup(){
		
	}

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

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
