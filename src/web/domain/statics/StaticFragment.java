package web.domain.statics;

import java.io.Serializable;

public class StaticFragment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
    private String fragmentName;		//碎片名称
    private String memo;				//碎片描述
	private Integer orderValue;			//碎片排序值
	private String targetUrl;			//碎片生成位置
	private Long moduleId;				//模板编码
	
	public StaticFragment () {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFragmentName() {
		return fragmentName;
	}

	public void setFragmentName(String fragmentName) {
		this.fragmentName = fragmentName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getOrderValue() {
		return orderValue;
	}

	public void setOrderValue(Integer orderValue) {
		this.orderValue = orderValue;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
}
