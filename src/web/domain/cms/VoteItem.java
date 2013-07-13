package web.domain.cms;
import java.io.Serializable;


public class VoteItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7523487116027920700L;
	private Long id;
	private Long voteID;
	private String item;
	private Integer num;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVoteID() {
		return voteID;
	}
	public void setVoteID(Long voteID) {
		this.voteID = voteID;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
}
