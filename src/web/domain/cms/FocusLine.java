package web.domain.cms;

import java.util.ArrayList;
import java.util.List;

public class FocusLine implements Comparable<FocusLine> {
	private Long id;
	private String lineTitle;
	private String link;
	private String openTarget;
	private Long lineSort;
	private List<FocusNews> focusNews = new ArrayList<FocusNews>();
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLineTitle() {
		return lineTitle;
	}
	public void setLineTitle(String lineTitle) {
		this.lineTitle = lineTitle;
	}
	public Long getLineSort() {
		return lineSort;
	}
	public void setLineSort(Long lineSort) {
		this.lineSort = lineSort;
	}
	public List<FocusNews> getFocusNews() {
		return focusNews;
	}
	public void setFocusNews(List<FocusNews> focusNews) {
		this.focusNews = focusNews;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getOpenTarget() {
		return openTarget;
	}
	public void setOpenTarget(String openTarget) {
		this.openTarget = openTarget;
	}

	@Override
	public int compareTo(FocusLine focusLine) {
		return lineSort.compareTo(focusLine.getLineSort());
	}
}
