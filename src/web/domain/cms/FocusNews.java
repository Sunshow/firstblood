package web.domain.cms;

public class FocusNews implements Comparable<FocusNews> {
	
	private Long id;
	private String title;
	private String linkUrl;
	private String target;
	private String fontColor;
	private Long newsSort;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getFontColor() {
		return fontColor;
	}
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}
	public Long getNewsSort() {
		return newsSort;
	}
	public void setNewsSort(Long newsSort) {
		this.newsSort = newsSort;
	}
	
	@Override
	public int compareTo(FocusNews focusNews) {
		return newsSort.compareTo(focusNews.getNewsSort());
	}
}
