package web.domain.cms;
import java.io.Serializable;
import java.util.Date;

import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.util.CoreDateUtils;


public class News implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7523487116027920700L;
	private Long id;
	private Long cateID;
	private String name;
	private String title;
	private String shortContent;
	private String content;
	private String keyword;
	private String author;
	private String editor;
	private String fromPlace;
	private boolean headNews;
	private boolean currentNew;
	private Date createTime;
	private String createTimeStr;
	private Date updateTime;
	private String updateTimeStr;
	private ContentType contentType;
	private String link;
	private Integer orderView;
	private boolean valid;
	private String url;
	private String imageUrl;
	private Long click;
	// 是否禁用编辑器
	private YesNoStatus disableEditor;
	private Long userId;//创建用户编码
	private Long lastUpdateUserId;//最后修改用户编码
	
	private String contentStatic;
	
	//uv数据
	private Long uvData;
	
	//是否显示点击次数
	private Boolean clickFlag;
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortContent() {
		return shortContent;
	}
	public void setShortContent(String shortContent) {
		this.shortContent = shortContent;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public String getFromPlace() {
		return fromPlace;
	}
	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}
	public boolean isHeadNews() {
		return headNews;
	}
	public void setHeadNews(boolean headNews) {
		this.headNews = headNews;
	}
	public boolean isCurrentNew() {
		return currentNew;
	}
	public void setCurrentNew(boolean currentNew) {
		this.currentNew = currentNew;
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
		if(updateTime == null){
			updateTime = new Date();
		}
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public ContentType getContentType() {
		return contentType;
	}
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Integer getOrderView() {
		if(orderView == null){
			orderView = 0;
		}
		return orderView;
	}
	public void setOrderView(Integer orderView) {
		this.orderView = orderView;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getClick() {
		return click;
	}
	public void setClick(Long click) {
		this.click = click;
	}
	public String getCreateTimeStr() {
		if (createTimeStr == null && createTime != null) {
			createTimeStr = CoreDateUtils.formatDateTime(createTime);
		}
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public String getUpdateTimeStr() {
		if (updateTimeStr == null && updateTime != null){
			updateTimeStr = CoreDateUtils.formatDateTime(updateTime);
		}
		return updateTimeStr;
	}
	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public YesNoStatus getDisableEditor() {
		return disableEditor;
	}
	public void setDisableEditor(YesNoStatus disableEditor) {
		this.disableEditor = disableEditor;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getLastUpdateUserId() {
		return lastUpdateUserId;
	}
	public void setLastUpdateUserId(Long lastUpdateUserId) {
		this.lastUpdateUserId = lastUpdateUserId;
	}
	public void setContentStatic(String contentStatic) {
		this.contentStatic = contentStatic;
	}
	public String getContentStatic() {
		return contentStatic;
	}

	public void setClickFlag(Boolean clickFlag) {
		this.clickFlag = clickFlag;
	}
	public Boolean getClickFlag() {
		return clickFlag;
	}
	public void setUvData(Long uvData) {
		this.uvData = uvData;
	}
	public Long getUvData() {
		return uvData;
	}

}
