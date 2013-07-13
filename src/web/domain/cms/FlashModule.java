package web.domain.cms;

public class FlashModule {
	/**
	 * 
	 */

	private String id;
	private String title;
	private Integer orderView;
	private String imgPath;
	private String link;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getOrderView() {
		return orderView;
	}
	public void setOrderView(Integer orderView) {
		this.orderView = orderView;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
}
