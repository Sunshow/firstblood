package web.domain.cms;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.lehecai.admin.web.utils.UploadUtil;

public class Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7523487116027920700L;
	private Long id;
	private Long cateID;
	private String name;
	private String path;
	private Date createTime;
	private boolean valid;
	private String realPath;
	
	public Long getId() {
		return id;
	}
	public void setId(Long iD) {
		id = iD;
	}
	public Long getCateID() {
		return cateID;
	}
	public void setCateID(Long cateID) {
		this.cateID = cateID;
	}
	public Date getCreateTime() {
		if(createTime == null){
			createTime = new Date();
		}
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	public String getRealPath() {
		if (StringUtils.isNotEmpty(path)) {
			realPath = UploadUtil.replacePathForPreview(path);
		}
		return realPath;
	}
}
