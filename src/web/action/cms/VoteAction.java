package web.action.cms;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.Vote;
import com.lehecai.admin.web.enums.VoteType;
import com.lehecai.admin.web.service.cms.VoteService;
import com.lehecai.admin.web.utils.PageUtil;

public class VoteAction extends BaseAction {

	private static final long serialVersionUID = 2436161530465382824L;
	private VoteService voteService;
	
	private Vote vote;
	
	private List<Vote> voteList;
	
	private String title;
	private Integer voteTypeId;
	private String valid;
	private Date fromCreateDate;
	private Date toCreateDate;
	private Date fromBeginDate;
	private Date toBeginDate;
	private Date fromEndDate;
	private Date toEndDate;
	
	public String handle(){
		HttpServletRequest request = ServletActionContext.getRequest();
		voteList = voteService.list(title, voteTypeId, valid, fromCreateDate, toCreateDate, fromBeginDate, toBeginDate, fromEndDate, toEndDate, super.getPageBean());
		PageBean pageBean = voteService.getPageBean(title, voteTypeId, valid, fromCreateDate, toCreateDate, fromBeginDate, toBeginDate, fromEndDate, toEndDate, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}
	
	public String manage(){
		if(vote != null){
			if(vote.getTitle() == null || "".equals(vote.getTitle())){
				super.setErrorMessage("新闻名称不能为空！");
				return "failure";
			}
			vote.setVoteType(VoteType.getItem(voteTypeId));
			voteService.manage(vote);
		}else{
			super.setErrorMessage("添加新闻错误，提交表单为空！");
			return "failure";
		}
		super.setForwardUrl("/cms/vote.do");
		return "success";
	}
	
	public String audit(){
		Vote voteTmp = null;
		if(vote != null && vote.getId() != null){
			voteTmp = voteService.get(vote.getId());
			voteTmp.setValid(vote.isValid());
			voteService.manage(voteTmp);
		}else{
			super.setErrorMessage("添加新闻错误，提交表单为空！");
			return "failure";
		}
		super.setForwardUrl("/cms/vote.do");
		return "success";
	}
	
	public String input(){
		if(vote != null){
			if(vote.getId() != null){
				vote = voteService.get(vote.getId());
				voteTypeId = vote.getVoteType().getValue();
			}else{
				vote.setValid(true);
			}
		}	
		return "inputForm";
	}
	public String view(){
		if(vote != null && vote.getId() != null){
			vote = voteService.get(vote.getId());
		}else{
			return "failure";
		}
		return "view";
	}
	public String del(){
		if(vote != null && vote.getId() != null){
			vote = voteService.get(vote.getId());
			voteService.del(vote);
		}else{
			return "failure";
		}
		super.setForwardUrl("/cms/vote.do");
		return "forward";
	}
	public VoteService getVoteService() {
		return voteService;
	}

	public void setVoteService(VoteService voteService) {
		this.voteService = voteService;
	}

	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public List<Vote> getVoteList() {
		return voteList;
	}

	public void setVoteList(List<Vote> voteList) {
		this.voteList = voteList;
	}

	public List<VoteType> getVoteTypes(){
		return VoteType.list;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getVoteTypeId() {
		return voteTypeId;
	}

	public void setVoteTypeId(Integer voteTypeId) {
		this.voteTypeId = voteTypeId;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public Date getFromCreateDate() {
		return fromCreateDate;
	}

	public void setFromCreateDate(Date fromCreateDate) {
		this.fromCreateDate = fromCreateDate;
	}

	public Date getToCreateDate() {
		return toCreateDate;
	}

	public void setToCreateDate(Date toCreateDate) {
		this.toCreateDate = toCreateDate;
	}

	public Date getFromBeginDate() {
		return fromBeginDate;
	}

	public void setFromBeginDate(Date fromBeginDate) {
		this.fromBeginDate = fromBeginDate;
	}

	public Date getToBeginDate() {
		return toBeginDate;
	}

	public void setToBeginDate(Date toBeginDate) {
		this.toBeginDate = toBeginDate;
	}

	public Date getFromEndDate() {
		return fromEndDate;
	}

	public void setFromEndDate(Date fromEndDate) {
		this.fromEndDate = fromEndDate;
	}

	public Date getToEndDate() {
		return toEndDate;
	}

	public void setToEndDate(Date toEndDate) {
		this.toEndDate = toEndDate;
	}
}
