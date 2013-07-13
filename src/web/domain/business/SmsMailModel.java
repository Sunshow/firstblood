package web.domain.business;

import java.io.Serializable;
import java.util.Date;

import com.lehecai.admin.web.enums.ModelType;
import com.lehecai.admin.web.enums.TextType;

public class SmsMailModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String title;
	private ModelType type;
	private TextType textType;
	private String content;
	private Date createTime;
	private Date updateTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ModelType getType() {
		return type;
	}
	public void setType(ModelType type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public TextType getTextType() {
		return textType;
	}
	public void setTextType(TextType textType) {
		this.textType = textType;
	}

}
