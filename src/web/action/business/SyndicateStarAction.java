package web.action.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.SyndicateStarService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.SyndicateStar;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 合买红人action
 * @author yanweijie
 *
 */
public class SyndicateStarAction extends BaseAction {
	private static final long serialVersionUID = -1902870954510759617L;
	private Logger logger = LoggerFactory.getLogger(SyndicateStarAction.class);
	
	private SyndicateStarService syndicateStarService;
	private MemberService memberService;
	
	private List<SyndicateStar> syndicateStars ;
	
	private SyndicateStar syndicateStar;
	
	private Long uid;
	private String userName;
	
	public String handle () {
		logger.info("进入查询合买红人");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query () {
		logger.info("进入查询合买红人");
		Map<String, Object> map = null;
		
		if (uid == null || uid == 0L) {
			if (userName != null && !userName.equals("")) {
				try {
					uid = memberService.getIdByUserName(userName);
				} catch (ApiRemoteCallFailedException e) {
					logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
				}
			}
		}
		try {
			map = syndicateStarService.findSyndicateStartList(uid, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询合买红人异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!");
			return "failure";
		}
		
		if (map != null) {
			syndicateStars = (List<SyndicateStar>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
		}
		
		logger.info("查询合买红人结束");
		return "list";
	}
	
	public String input () {
		logger.info("进入输入合买红人");
		return "inputForm";
	}
	
	@SuppressWarnings("unchecked")
	public String add () {
		logger.info("进入添加合买红人");
		if ((uid == null || uid == 0L) && (userName == null || userName.equals(""))) {
			logger.error("用户编码和用户名都为空");
			super.setErrorMessage("请填写用户编码或者用户名");
			return "failure";
		}
		
		Member member = null;
		if (uid != null && uid != 0L) {
			try {
				member = memberService.get(uid);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API根据会员编码查询会员异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!");
				return "failure";
			}
		} else if (userName != null && !userName.equals("")) {
			try {
				member = memberService.get(userName);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API根据会员用户名查询会员异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!");
				return "failure";
			}
		}

		if (member == null) {
			logger.error("添加会员不存在");
			super.setErrorMessage("添加会员不存在");
			return "failure";
		}
		
		uid = member.getUid();
		
		Map<String, Object> map = null;
		try {
			map = syndicateStarService.findSyndicateStartList(uid, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询合买红人异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!");
			return "failure";
		}
		
		if (map != null) {
			syndicateStars = (List<SyndicateStar>)map.get(Global.API_MAP_KEY_LIST);
			if (syndicateStars != null && syndicateStars.size() > 0) {
				logger.error("添加会员已经是合买红人");
				super.setErrorMessage("添加会员已经是合买红人");
				return "failure";
			}
		}
		
		boolean addResult = false;
		try {
			addResult = syndicateStarService.addSyndicateStar(uid);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API添加合买红人异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!");
			return "failure";
		}
		
		logger.info("添加合买红人结束");
		
		if (addResult) {
			logger.info("添加合买红人成功");
			super.setForwardUrl("/business/syndicateStar.do");
			return "success";
		} else {
			logger.error("添加合买红人失败");
			super.setErrorMessage("添加合买红人失败");
			return "failure";
		}
	}
	
	public String update () {
		logger.info("进入修改合买红人优先级");
		
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("msg", "API修改合买红人优先级成功");

		
		if (syndicateStar == null) {
			json.put("code", 1);
			json.put("msg", "合买红人信息为空");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		if (syndicateStar.getUid() == 0L) {
			json.put("code", 1);
			json.put("msg", "合买红人编码为空");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		
		List<String> uids = new ArrayList<String>();
		uids.add(syndicateStar.getUid() + "");
		
		boolean updateResult = false;
		try {
			updateResult = syndicateStarService.updateSyndicateStar(uids,syndicateStar.getPriority());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改合买红人优先级异常，{}", e.getMessage());
			json.put("code", 1);
			json.put("msg", "API调用异常，请联系技术人员!");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		
		if (!updateResult) {
			logger.error("修改合买红人优先级失败");
			json.put("code", 1);
			json.put("msg", "修改合买红人优先级失败");
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		
		super.writeRs(ServletActionContext.getResponse(), json);
		return null;
	}
	
	
	public String delete () {
		logger.info("进入删除合买红人");
		if (uid == null || uid == 0L) {
			logger.error("用户编码为空");
			super.setErrorMessage("用户编码不能为空");
			return "failure";
		}
		
		boolean deleteResult = false;
		try {
			deleteResult = syndicateStarService.deleteSyndicateStar(uid);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API删除合买红人异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!");
			return "failure";
		}
		
		logger.info("删除合买红人结束");
		
		if (deleteResult) {
			logger.info("删除合买红人成功");
			super.setForwardUrl("/business/syndicateStar.do");
			return "success";
		} else {
			logger.error("删除加合买红人失败");
			super.setErrorMessage("删除合买红人失败");
			return "failure";
		}
	}
	
	
	public SyndicateStarService getSyndicateStarService() {
		return syndicateStarService;
	}
	public void setSyndicateStarService(SyndicateStarService syndicateStarService) {
		this.syndicateStarService = syndicateStarService;
	}
	public MemberService getMemberService() {
		return memberService;
	}
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	public List<SyndicateStar> getSyndicateStars() {
		return syndicateStars;
	}
	public void setSyndicateStars(List<SyndicateStar> syndicateStars) {
		this.syndicateStars = syndicateStars;
	}
	public SyndicateStar getSyndicateStar() {
		return syndicateStar;
	}
	public void setSyndicateStar(SyndicateStar syndicateStar) {
		this.syndicateStar = syndicateStar;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
