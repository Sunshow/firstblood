package web.domain.cms;
import java.io.Serializable;
import java.util.Date;

import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.core.YesNoStatus;


public class NewsHistory implements Serializable {

	/**新闻历史版本实体
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long newsId;
	private Long cateID;
	private Long version;
	private String title;
	private String name;
	private String shortContent;
	private String keyword;
	private String author;
	private String editor;
	private String fromPlace;
	private String imageUrl;
	private boolean headNews;
	private boolean currentNew;
	private Integer orderView;
	private ContentType contentType;
	// 是否禁用编辑器
	private YesNoStatus disableEditor;
	private Date createTime;
	private String content;
	private String link;
	private Long lastUpdateUserId;
	public Long getLastUpdateUserId() {
		if (lastUpdateUserId == null) {
			lastUpdateUserId = 0L;
		}
		return lastUpdateUserId;
	}
	public void setLastUpdateUserId(Long lastUpdateUserId) {
		this.lastUpdateUserId = lastUpdateUserId;
	}
	public Long getCreateRecordUserId() {
		return createRecordUserId;
	}
	public void setCreateRecordUserId(Long createRecordUserId) {
		this.createRecordUserId = createRecordUserId;
	}
	private Long createRecordUserId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long iD) {
		id = iD;
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
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Long getNewsId() {
		return newsId;
	}
	public void setNewsId(Long newsId) {
		this.newsId = newsId;
	}
	public Long getCateID() {
		return cateID;
	}
	public void setCateID(Long cateID) {
		this.cateID = cateID;
	}
}
