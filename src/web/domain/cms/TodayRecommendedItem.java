package web.domain.cms;

/**
 * 今日推荐实体
 * @author yanweijie
 *
 */
public class TodayRecommendedItem implements Comparable<TodayRecommendedItem> {
	
	private String id;					//编号
	private String iconPath;			//图标路径
	private String title;				//主标题
	private String titleLink;			//主标题链接地址
	private String titleOpenTarget;		//主标题链接打开方式
	private String subTitle;			//副标题
	private String subTitleLink;		//副标题链接地址
	private String subTitleOpenTarget;	//副标题链接打开方式
	private String betImagePath;		//投注按钮图片路径
	private String betLink;				//投注按钮链接地址
	private String betOpenTarget;		//投注按钮链接打开方式
	private int order;					//排序
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitleLink() {
		return titleLink;
	}

	public void setTitleLink(String titleLink) {
		this.titleLink = titleLink;
	}

	public String getTitleOpenTarget() {
		return titleOpenTarget;
	}

	public void setTitleOpenTarget(String titleOpenTarget) {
		this.titleOpenTarget = titleOpenTarget;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getSubTitleLink() {
		return subTitleLink;
	}

	public void setSubTitleLink(String subTitleLink) {
		this.subTitleLink = subTitleLink;
	}

	public String getSubTitleOpenTarget() {
		return subTitleOpenTarget;
	}

	public void setSubTitleOpenTarget(String subTitleOpenTarget) {
		this.subTitleOpenTarget = subTitleOpenTarget;
	}

	public String getBetImagePath() {
		return betImagePath;
	}

	public void setBetImagePath(String betImagePath) {
		this.betImagePath = betImagePath;
	}

	public String getBetLink() {
		return betLink;
	}

	public void setBetLink(String betLink) {
		this.betLink = betLink;
	}

	public String getBetOpenTarget() {
		return betOpenTarget;
	}

	public void setBetOpenTarget(String betOpenTarget) {
		this.betOpenTarget = betOpenTarget;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int compareTo(TodayRecommendedItem todayRecommendedItem) {
		return Integer.valueOf(order).compareTo(Integer.valueOf(todayRecommendedItem.getOrder()));
	}
	
}
