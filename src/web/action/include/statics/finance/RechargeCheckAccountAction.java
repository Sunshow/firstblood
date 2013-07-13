package web.action.include.statics.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.entity.RechargeTask;
import com.lehecai.admin.web.activiti.service.RechargeTaskService;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreStringUtils;
import com.opensymphony.xwork2.Action;

public class RechargeCheckAccountAction extends BaseAction {
    private static final long serialVersionUID = 1936261232465388324L;
    private final Logger logger = LoggerFactory.getLogger(RechargeCheckAccountAction.class);

    private MemberService memberService;
    private RechargeTaskService rechargeTaskService;
    
    private String json;
    public String handle() {
    	String message = "";
    	int code = 1;
    	int total = 0;
    	logger.info("进入查询充值流水数据");
        if (StringUtils.isEmpty(json)) {
        	message = "未获取任何查询参数";
        }
        Date startDate = null;
        Date endDate = null;
        Double startAmount = null;
        Double endAmount = null;
        
        String account = "";
        Double amount = null;
        String realName = "";
        PageBean pageBean = super.getPageBean();
    
        try {
        	json = CoreStringUtils.unicodeToString(json);
        	JSONObject jsonObj = JSONObject.fromObject(json);
        	Integer page = jsonObj.getInt("page");
        	Integer pagesize = jsonObj.getInt("pagesize");
        	if (page != null && pagesize != null) {
        		pageBean.setPage(page);
        		pageBean.setPageSize(pagesize);
        	}
        	
        	JSONArray whereObj = jsonObj.getJSONArray("where");
        	if (whereObj != null && whereObj.size() > 0) {
        		for (int i=0; i<whereObj.size(); i++) {
        			JSONObject tempObject = whereObj.getJSONObject(i);
        			String key = tempObject.getString("key");
        			if (!tempObject.containsKey("val")) {
        				continue;
        			}
        			String val = tempObject.getString("val");
	        		if (StringUtils.isNotEmpty(val) && !"null".equals(val)) {
	        			if (key.equals("startDate")) {
	        				startDate = CoreDateUtils.parseDate(val, CoreDateUtils.DATETIME);
	        			} else if (key.equals("endDate")) {
	        				endDate = CoreDateUtils.parseDate(val, CoreDateUtils.DATETIME);
	        			} else if (key.equals("startAmount")) {
	        				startAmount = Double.valueOf(val);
	        			} else if (key.equals("endAmount")) {
	        				endAmount = Double.valueOf(val);
	        			} else if (key.equals("account")) {
	        				account = val;
	        			} else if (key.equals("amount")) {
	        				amount = Double.valueOf(val);
	        			} else if (key.equals("realName")) {
	        				realName = val;
	        			}
        			}
        		}
        	} else {
        		message = "未获取任何查询参数";
        	}
        } catch (Exception e) {
        	logger.error("参数解析异常，原因{}", e.getMessage());
        }
		List<RechargeTask> list = new ArrayList<RechargeTask>();
		try {
			list = rechargeTaskService.query(startDate, endDate, startAmount, endAmount, account, amount, realName, pageBean);
			code = 0;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		String result = dealResponseStr(message, code, total, list);
		writeRs(ServletActionContext.getResponse(), result);
        logger.info("查询充值流水数据结束");
        return Action.NONE;
    }
    
    private String dealResponseStr (String message, int code, int total, List<RechargeTask> list) {
    	JSONObject responseObj = new JSONObject();
    	responseObj.put("message", message);
    	responseObj.put("code", code);
    	responseObj.put("total", total);
    	JSONArray dataArray = new JSONArray();
    	if (list != null && list.size() > 0) {
    		for (RechargeTask task : list) {
    			JSONObject temp = new JSONObject();
    			temp.put("taskId", task.getId() + "");
    			temp.put("amount", task.getAmount());
    			temp.put("realName", task.getRealName());
    			temp.put("rechargeBankId", task.getRechargeBankId());
    			temp.put("rechargeCardNo", task.getRechargeCardNo());
    			temp.put("userName", task.getUsername());
    			temp.put("userCardNo", task.getUserCardNo());
    			temp.put("createdTime", CoreDateUtils.formatDateTime(task.getCreatedTime()));
    			temp.put("memo", task.getMemo());
    			dataArray.add(temp);
        	}
    		responseObj.put("total", list.size());
        	responseObj.put("data", dataArray);
    	} else {
    		responseObj.put("data", new JSONArray());
    	}
    	return responseObj.toString();
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

	public void setJson(String json) {
		this.json = json;
	}

	public String getJson() {
		return json;
	}

	public void setRechargeTaskService(RechargeTaskService rechargeTaskService) {
		this.rechargeTaskService = rechargeTaskService;
	}

	public RechargeTaskService getRechargeTaskService() {
		return rechargeTaskService;
	}
}
