package web.domain.link;

import java.io.Serializable;

/**
 * 友情链接实体
 * @author yanweijie
 *
 */
public class FriendLink implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3562727477261723185L;

	private Long id;			//链接编号
	private String name;		//链接名称
	private String url;			//链接地址
	private int orderView;		//链接排序值
	private boolean onIndex;    //是否在首页显示
	private boolean highlight;  //是否高亮
	private boolean openNew;	//是否在新窗口中打开
	private boolean valid;		//是否有效
	private String memo;		//备注
	
	public FriendLink(){
		
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getOrderView() {
		return orderView;
	}

	public void setOrderView(int orderView) {
		this.orderView = orderView;
	}

	public boolean isOpenNew() {
		return openNew;
	}

	public void setOpenNew(boolean openNew) {
		this.openNew = openNew;
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

	public boolean isOnIndex() {
		return onIndex;
	}

	public void setOnIndex(boolean onIndex) {
		this.onIndex = onIndex;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}
	
}
