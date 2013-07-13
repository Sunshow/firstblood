package web.domain.cms;

public class FlashCategory {
	/**
	 * 
	 */

	private String codename;
	private String name;
	private String path;
	private String memo;
	private Integer flashWidth;
	private Integer flashHeight;
	private Integer fontHeight;
	
	public String getCodename() {
		return codename;
	}
	public void setCodename(String codename) {
		this.codename = codename;
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

	public String getMemo() {
		return memo;
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public Integer getFlashWidth() {
		return flashWidth;
	}
	
	public void setFlashWidth(Integer flashWidth) {
		this.flashWidth = flashWidth;
	}
	
	public Integer getFlashHeight() {
		return flashHeight;
	}
	
	public void setFlashHeight(Integer flashHeight) {
		this.flashHeight = flashHeight;
	}
	
	public Integer getFontHeight() {
		return fontHeight;
	}
	
	public void setFontHeight(Integer fontHeight) {
		this.fontHeight = fontHeight;
	}
	
}
