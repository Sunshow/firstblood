package web.action.business;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.SmsMailMember;
import com.lehecai.admin.web.domain.business.SmsMailMemberGroup;
import com.lehecai.admin.web.service.business.SmsMailMemberGroupService;
import com.lehecai.admin.web.service.business.SmsMailMemberService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class SmsMailMemberGroupAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(SmsMailMemberGroupAction.class);
	
	private SmsMailMemberGroupService smsMailMemberGroupService;
	private SmsMailMemberService smsMailMemberService;
	private MemberService memberService;

	private SmsMailMemberGroup smsMailMemberGroup;
	private SmsMailMember smsMailMember;
	
	private List<SmsMailMemberGroup> smsMailMemberGroupList;
	private List<SmsMailMember> smsMailMemberList;
	
	private String name;
	private String oldName;
	private String valid;
	private String checkValid;
	
	/**
	 * 多条件分页查询所有短信邮件会员组
	 * @return
	 */
	public String handle() {
		logger.info("进入查询短信邮件会员组列表");
		return "list";
	}
	
	/**
	 * 多条件分页查询所有短信邮件会员组
	 * @return
	 */
	public String query() {
		logger.info("进入查询短信邮件会员组列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		smsMailMemberGroupList = smsMailMemberGroupService.findSmsMailMemberGroupList(super.getPageBean(), name, valid);
		PageBean pageBean = smsMailMemberGroupService.getPageBean(super.getPageBean(), name,valid);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		logger.info("查询短信邮件会员组列表结束");
		
		return "list";
		
	}
	
	/**
	 * 转向添加/修改短信邮件会员组
	 */
	public String input() {
		logger.info("进入输入短信邮件会员组信息");
		if (smsMailMemberGroup != null && smsMailMemberGroup.getId() != null) {
			smsMailMemberGroup = smsMailMemberGroupService.get(smsMailMemberGroup.getId());
			oldName = smsMailMemberGroup.getName();
			if (smsMailMemberGroup.isValid()) {
				checkValid = "on";
			}
		}
		return "inputForm";
	}
	
	/**
	 * 添加/修改短信邮件会员组
	 * @return
	 */
	public String manage() {
		logger.info("进入更新短信邮件会员组信息");
		if (smsMailMemberGroup != null) {
			if (smsMailMemberGroup.getName() == null || "".equals(smsMailMemberGroup.getName().trim())) {
				logger.error("短信邮件会员组名称为空");
				super.setErrorMessage("短信邮件会员组名称不能为空");
				return "failure";
			} else {
				if (smsMailMemberGroup.getId() == null) {
					SmsMailMemberGroup tempSmsMailMemberGroup = smsMailMemberGroupService.findSmsMailMemberGroupByName(smsMailMemberGroup.getName().trim());
					if (tempSmsMailMemberGroup != null) {
						logger.error("{} 名称已经存在",tempSmsMailMemberGroup.getName());
						super.setErrorMessage(tempSmsMailMemberGroup.getName() + " 名称已经存在！");
						return "failure";
					}
				} else {
					if (!smsMailMemberGroup.getName().trim().equals(oldName)) {
						SmsMailMemberGroup tempSmsMailMemberGroup = smsMailMemberGroupService.findSmsMailMemberGroupByName(smsMailMemberGroup.getName().trim());
						if (tempSmsMailMemberGroup != null) {
							logger.error("{} 名称已经存在",tempSmsMailMemberGroup.getName());
							super.setErrorMessage(tempSmsMailMemberGroup.getName() + " 名称已经存在");
							return "failure";
						}
					}
				}
			}
			
			if (checkValid != null && "on".equals(checkValid)) {
				smsMailMemberGroup.setValid(true);
			}
			
			smsMailMemberGroupService.merge(smsMailMemberGroup);
		} else {
			logger.error("更新短信邮件会员组信息错误，提交表单为空");
			super.setErrorMessage("更新短信邮件会员组信息错误，提交表单为空！");
			return "failure";
		}
		super.setForwardUrl("/business/smsMailMemberGroup.do");
		logger.info("更新短信邮件会员组信息结束");
		return "success";
	}
	
	/**
	 * 查看短信邮件会员组详细信息
	 * @return
	 */
	public String view() {
		logger.info("进入查询短信邮件会员组详细信息");
		if (smsMailMemberGroup != null && smsMailMemberGroup.getId() != null) {
			smsMailMemberGroup = smsMailMemberGroupService.get(smsMailMemberGroup.getId());
		} else {
			logger.error("查询短信邮件会员组详细信息，编码为空");
			super.setErrorMessage("查询短信邮件会员组详细信息，编码为空");
			return "failure";
		}
		logger.info("查询短信邮件会员组详细信息结束");
		return "view";
	}
	
	/**
	 * 删除短信邮件会员组
	 * @return
	 */
	public String del() {
		logger.info("进入删除短信邮件会员组");
		if (smsMailMemberGroup != null && smsMailMemberGroup.getId() != null) {
			smsMailMemberGroup = smsMailMemberGroupService.get(smsMailMemberGroup.getId());
			smsMailMemberGroupService.del(smsMailMemberGroup);
		} else {
			logger.error("删除短信邮件会员组，编码为空");
			super.setErrorMessage("删除短信邮件会员组，编码为空");
			return "failure";
		}
		super.setForwardUrl("/business/smsMailMemberGroup.do");
		logger.info("删除短信邮件会员组结束");
		return "forward";
	}
	
	/**
	 * 查询短信邮件会员组对应会员
	 * @return
	 */
	public String listMember() {
		logger.info("进入查询短信邮件会员");
		if (smsMailMemberGroup == null || smsMailMemberGroup.getId() == null) {
			logger.error("查询短信邮件会员，短信邮件会员组编码为空");
			super.setErrorMessage("查询短信邮件会员，短信邮件会员组编码为空");
			return "failure";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		smsMailMemberList = smsMailMemberService.findSmsMailMemberList(super.getPageBean(), smsMailMemberGroup.getId());//根据组编号查询组会员
		PageBean pageBean = smsMailMemberService.getPageBean(super.getPageBean(), smsMailMemberGroup.getId());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		
		logger.info("查询短信邮件会员结束");
		return "memberList";
	}

	/**
	 * 添加短信邮件会员
	 * @return
	 */
	public String manageMember() {
		logger.info("进入添加短信邮件会员");
		Member member = null;
		if (smsMailMember != null) {
			if (smsMailMember.getGroupId() == null || smsMailMember.getGroupId() == 0L) {
				logger.error("短信邮件会员组编号为空");
				super.setErrorMessage("短信邮件会员组编号不能为空");
				return "failure";
			}
			if (smsMailMember.getUserName() == null || "".equals(smsMailMember.getUserName().trim())) {
				logger.error("用户名为空");
				super.setErrorMessage("用户名不能为空");
				return "failure";
			} else {
				try {
					member = memberService.get(smsMailMember.getUserName().trim());
					if (member == null) {
						logger.error("{} 对应的用户不存在",smsMailMember.getUserName().trim());
						super.setErrorMessage(smsMailMember.getUserName()+" 对应的用户不存在");
						return "failure";
					}
				} catch (ApiRemoteCallFailedException e) {
					logger.error("查询{}对应的用户名是否存在异常",smsMailMember.getUserName().trim());
					super.setErrorMessage("查询"+smsMailMember.getUserName()+"对应的用户名是否存在异常");
					return "failure";
				}
				SmsMailMember tempSmsMailMember = smsMailMemberService.findSmsMailMemberByUserName(smsMailMember.getUserName().trim(), smsMailMember.getGroupId());
				if (tempSmsMailMember != null) {
					SmsMailMemberGroup tempSmsMailMemberGroup = smsMailMemberGroupService.get(smsMailMember.getGroupId());
					logger.error(smsMailMember.getUserName().trim() + " 会员已经存在于 " + tempSmsMailMemberGroup.getName());
					super.setErrorMessage(smsMailMember.getUserName().trim() + " 会员已经存在于 " + tempSmsMailMemberGroup.getName());
					return "failure";
				}
				smsMailMember.setUid(member.getUid());
			}
		}
		smsMailMemberService.merge(smsMailMember);
		
		super.setForwardUrl("/business/smsMailMemberGroup.do?action=listMember&smsMailMemberGroup.id="+ smsMailMember.getGroupId());
		logger.info("添加短信邮件会员结束");
		return "forward";
	}
	
	/**
	 * 删除短信邮件会员
	 * @return
	 */
	public String delMember() {
		logger.info("进入删除短信邮件会员");
		if (smsMailMember == null || smsMailMember.getId() == null) {
			logger.error("短信邮件会员编号为空");
			super.setErrorMessage("短信邮件会员编号为空");
			return "failure";
		}
		smsMailMemberService.del(smsMailMemberService.get(smsMailMember.getId()));
		
		super.setForwardUrl("/business/smsMailMemberGroup.do?action=listMember&smsMailMemberGroup.id="+ smsMailMemberGroup.getId());
		logger.info("删除短信邮件会员结束");
		return "forward";
	}
	
	public String findMemberByGroup() {
		logger.info("进入查询对应组的所有会员");
		PrintWriter out = null;
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (smsMailMemberGroup.getId() == null || "".equals(smsMailMemberGroup.getId())) {
			logger.error("会员组编码为空");
			super.setErrorMessage("会员组编码为空");
			return "failure";
		}
		smsMailMemberList = smsMailMemberService.findSmsMailMemberList(null, smsMailMemberGroup.getId());
		
		if (smsMailMemberList == null || smsMailMemberList.size() == 0) {
			logger.warn("对应组没有会员");
		}
		
		JSONArray ja = new JSONArray();
		for (SmsMailMember member : smsMailMemberList) {
			ja.add(member.getUserName());
		}
		
		System.out.println("userNameJa.............."+ja.toString());
		
		JSONObject rs = new JSONObject();
		rs.put("userName", ja);
		
		out.println(rs.toString());
		out.flush();
		out.close();
		
		return null;
	}
	
	public SmsMailMemberGroupService getSmsMailMemberGroupService() {
		return smsMailMemberGroupService;
	}

	public void setSmsMailMemberGroupService(
			SmsMailMemberGroupService smsMailMemberGroupService) {
		this.smsMailMemberGroupService = smsMailMemberGroupService;
	}

	public SmsMailMemberService getSmsMailMemberService() {
		return smsMailMemberService;
	}

	public void setSmsMailMemberService(SmsMailMemberService smsMailMemberService) {
		this.smsMailMemberService = smsMailMemberService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public SmsMailMemberGroup getSmsMailMemberGroup() {
		return smsMailMemberGroup;
	}

	public void setSmsMailMemberGroup(SmsMailMemberGroup smsMailMemberGroup) {
		this.smsMailMemberGroup = smsMailMemberGroup;
	}

	public SmsMailMember getSmsMailMember() {
		return smsMailMember;
	}

	public void setSmsMailMember(SmsMailMember smsMailMember) {
		this.smsMailMember = smsMailMember;
	}

	public List<SmsMailMemberGroup> getSmsMailMemberGroupList() {
		return smsMailMemberGroupList;
	}

	public void setSmsMailMemberGroupList(
			List<SmsMailMemberGroup> smsMailMemberGroupList) {
		this.smsMailMemberGroupList = smsMailMemberGroupList;
	}

	public List<SmsMailMember> getSmsMailMemberList() {
		return smsMailMemberList;
	}

	public void setSmsMailMemberList(List<SmsMailMember> smsMailMemberList) {
		this.smsMailMemberList = smsMailMemberList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getCheckValid() {
		return checkValid;
	}

	public void setCheckValid(String checkValid) {
		this.checkValid = checkValid;
	}
}
