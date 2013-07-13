package web.action.cms;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.FocusLine;
import com.lehecai.admin.web.domain.cms.FocusNews;
import com.lehecai.admin.web.service.cms.FocusNewsService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.type.cooperator.Cooperator;

public class FocusNewsAction extends BaseAction {
	
	private static final long serialVersionUID = -9158988745806564338L;

	protected static String JSON_PATH = "focusnews/";
	
	private FocusNewsService focusNewsService;

	private List<FocusLine> focusLineList;

	private FocusLine focusLine;
	private FocusNews focusNews;
	
	private String codename;
	private String staticDir;
	
	private Cooperator cooperator;
	
	protected String getFocusNewsFilename() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String path = request.getSession().getServletContext().getRealPath("/")  + staticDir + JSON_PATH;
		String filename = codename;
		if (cooperator != null) {
			filename += "_" + StringUtils.uncapitalize(cooperator.getName());
		}
		filename += ".json";
		
		return path + filename;
	}
	
	protected String getFocusNewsForwardUrl() {
		return this.getContextURI() + "?codename=" + codename;
	}

	public String handle() {
		logger.info("进入查询焦点新闻列表");
		focusLineList = focusNewsService.focusLineList(this.getFocusNewsFilename());
		return "listCategories";
	}
	
	/**
	 * 添加修改行标题
	 * @return
	 */
	public String lineManage() {
		logger.info("进入更新行标题");
		if (focusLine != null) {
			if (focusLine.getLineTitle() == null || "".equals(focusLine.getLineTitle())) {
				logger.error("更新行标题，标题名称为空");
				super.setErrorMessage("更新行标题，标题名称不能为空");
				return "failure";
			}
			focusLineList = focusNewsService.focusLineList(this.getFocusNewsFilename());
			if (focusLine.getId() != null) {
				for(int i = 0;i<focusLineList.size();i++) {
					FocusLine fl = (FocusLine)focusLineList.get(i);
					if (focusLine.getId().equals(fl.getId())) {
						fl.setLineTitle(focusLine.getLineTitle());
						fl.setLink(focusLine.getLink());
						fl.setOpenTarget(focusLine.getOpenTarget());
						fl.setLineSort(focusLine.getLineSort());
						focusLineList.remove(i);
						focusLineList.add(i,fl);
						
						focusNewsService.lineManage(focusLineList, this.getFocusNewsFilename());
						super.setForwardUrl(this.getFocusNewsForwardUrl());
						return "success";
					}
				}
			}
			focusLine.setId(DateUtil.getTimtstamp());	//用时间戳生成ID
			focusLineList.add(focusLine);
			focusNewsService.lineManage(focusLineList, this.getFocusNewsFilename());
		}
		super.setForwardUrl(this.getFocusNewsForwardUrl());
		logger.info("更新行标题结束");
		return "success";
	}
	
	/**
	 * 添加修改新闻
	 * @return
	 */
	public String newsManage() {
		logger.info("进入更新行标题，新闻信息");
		if (focusNews != null) {
			if (focusNews.getTitle() == null || "".equals(focusNews.getTitle())) {
				logger.error("标题名称为空");
				super.setErrorMessage("标题名称不能为空");
				return "failure";
			}
			if (focusNews.getLinkUrl() == null || "".equals(focusNews.getLinkUrl())) {
				logger.error("链接为空");
				super.setErrorMessage("链接不能为空");
				return "failure";
			}
			focusLineList = focusNewsService.focusLineList(this.getFocusNewsFilename());
			for(int i = 0; i<focusLineList.size();i++) {
				FocusLine fl = (FocusLine)focusLineList.get(i);
				if (focusLine.getId().equals(fl.getId())) {
					if (focusNews.getId() != null) {
						for(int j = 0; j < fl.getFocusNews().size();j++) {
							FocusNews fn = (FocusNews)fl.getFocusNews().get(j);
							if (focusNews.getId().equals(fn.getId())) {
								fn = focusNews;
								
								focusLineList.get(i).getFocusNews().remove(j);
								focusLineList.get(i).getFocusNews().add(j,fn);
								
								try {//排序
									Collections.sort(focusLineList.get(i).getFocusNews());
								} catch (Exception e) {}
								focusNewsService.lineManage(focusLineList, this.getFocusNewsFilename());
								super.setForwardUrl(this.getFocusNewsForwardUrl());
								return "success";
							}
						}
					}
					focusNews.setId(DateUtil.getTimtstamp()); //用时间戳生成ID
					fl.getFocusNews().add(focusNews);
					focusLineList.remove(i);
					focusLineList.add(i,fl);
					focusNewsService.lineManage(focusLineList, this.getFocusNewsFilename());
				}
			}
		}
		super.setForwardUrl(this.getFocusNewsForwardUrl());
		logger.info("更新新闻信息结束");
		return "success";
	}
	
	/**
	 * 删除行标题
	 * @return
	 */
	public String lineDel() {
		logger.info("进入删除行标题");
		if (focusLine != null) {
			if (focusLine.getId() != null) {
				focusLineList = focusNewsService.focusLineList(this.getFocusNewsFilename());
				for(int i = 0;i<focusLineList.size();i++) {
					FocusLine fl = (FocusLine)focusLineList.get(i);
					if (focusLine.getId().equals(fl.getId())) {
						focusLineList.remove(i);
					}
				}
				focusNewsService.lineManage(focusLineList, this.getFocusNewsFilename());
			}
		}
		super.setForwardUrl(this.getFocusNewsForwardUrl());
		logger.info("删除行标题结束");
		return "success";
	}
	
	/**
	 * 删除新闻
	 * @return
	 */
	public String newsDel() {
		logger.info("进入删除新闻");
		if (focusNews.getId() != null) {
			focusLineList = focusNewsService.focusLineList(this.getFocusNewsFilename());
			for(int i = 0;i<focusLineList.size();i++) {
				FocusLine fl = (FocusLine)focusLineList.get(i);
				if (focusLine.getId().equals(fl.getId())) {
					for (int j = 0; j < fl.getFocusNews().size(); j++) {
						FocusNews fn = (FocusNews) fl.getFocusNews().get(j);
						if (focusNews.getId().equals(fn.getId())) {
							focusLineList.get(i).getFocusNews().remove(j);
						}
					}
				}
			}
			focusNewsService.lineManage(focusLineList, this.getFocusNewsFilename());
		}
		super.setForwardUrl(this.getFocusNewsForwardUrl());
		logger.info("删除新闻结束");
		return "success"; 
	}
	
	public String lineInput() {
		logger.info("进入输入行标题信息");
		if (focusLine != null) {
			if (focusLine.getId() != null) {
				focusLineList = focusNewsService.focusLineList(this.getFocusNewsFilename());
				for(int i = 0;i<focusLineList.size();i++) {
					FocusLine fl = (FocusLine)focusLineList.get(i);
					if (focusLine.getId().equals(fl.getId())) {
						focusLine = fl;
					}
				}
			}
		}
		return "lineInput";
	}
	
	public String newsInput() {
		logger.info("进入输入新闻信息");
		if (focusNews != null) {
			if (focusNews.getId() != null) {
				focusLineList = focusNewsService.focusLineList(this.getFocusNewsFilename());
				for(int i = 0;i<focusLineList.size();i++) {
					FocusLine fl = (FocusLine)focusLineList.get(i);
					if (focusLine.getId().equals(fl.getId())) {
						for(int j = 0;j<fl.getFocusNews().size();j++) {
							FocusNews fn = (FocusNews)fl.getFocusNews().get(j);
							if(focusNews.getId().equals(fn.getId())) {
								focusNews = fn;
							}
						}
					}
				}
			}
		}
		return "newsInput";
	}
	
	public FocusNewsService getFocusNewsService() {
		return focusNewsService;
	}
	
	public void setFocusNewsService(FocusNewsService focusNewsService) {
		this.focusNewsService = focusNewsService;
	}

	public List<FocusLine> getFocusLineList() {
		return focusLineList;
	}

	public void setFocusLineList(List<FocusLine> focusLineList) {
		this.focusLineList = focusLineList;
	}

	public FocusLine getFocusLine() {
		return focusLine;
	}

	public void setFocusLine(FocusLine focusLine) {
		this.focusLine = focusLine;
	}

	public FocusNews getFocusNews() {
		return focusNews;
	}

	public void setFocusNews(FocusNews focusNews) {
		this.focusNews = focusNews;
	}

	public String getCodename() {
		return codename;
	}

	public void setCodename(String codename) {
		this.codename = codename;
	}

	public String getStaticDir() {
		return staticDir;
	}

	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}

	public void setCooperatorId(Integer cooperatorId) {
		if (cooperatorId != null) {
			this.cooperator = Cooperator.getItem(cooperatorId);
		}
	}
}
