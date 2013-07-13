package web.action.include.statics.lottery;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.statics.DataConsolidation;
import com.lehecai.admin.web.domain.statics.DataConsolidationItem;
import com.lehecai.admin.web.service.statics.DataConsolidationService;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreHttpUtils;
import com.opensymphony.xwork2.Action;

public class DataConsolidationStaticAction extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private DataConsolidationService dataConsolidationService;
	
	private String id;

	public String handle() {
		JSONObject json = new JSONObject();
		HttpServletResponse response = ServletActionContext.getResponse();
		if (StringUtils.isEmpty(id)) {
			json.put("code", 1);
			json.put("message", "数据整合编码不能为空");
			
			super.writeRs(response, json);
			return Action.NONE;
		}
		DataConsolidation dataConsolidation = null;
		try {
			dataConsolidation = dataConsolidationService.get(Long.valueOf(id));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", 1);
			json.put("message", "数据整合编码id=" + id + "查询错误");
			
			super.writeRs(response, json);
			return Action.NONE;
		}
		if (dataConsolidation == null) {
			json.put("code", 1);
			json.put("message", "未查询到数据整合编码id=" + id + "的数据");
			
			super.writeRs(response, json);
			return Action.NONE;
		}
		
		List<DataConsolidationItem> dataConsolidationItemList = dataConsolidationService.list(Long.valueOf(id));
		if (dataConsolidationItemList == null || dataConsolidationItemList.size() == 0) {
			json.put("code", 1);
			json.put("message", "未查询到数据整合编码id=" + id + "的数据整合项");
			
			super.writeRs(response, json);
			return Action.NONE;
		}
		JSONObject dataObj = new JSONObject();
		for (DataConsolidationItem item : dataConsolidationItemList) {
			if (StringUtils.isEmpty(item.getDataKey()) || StringUtils.isEmpty(item.getUrl())) {
				continue;
			}
			List<String> list = null;
			try {
				list = CoreHttpUtils.postUrl(item.getUrl(), "", CharsetConstant.CHARSET_UTF8, 10000);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			if (list == null || list.size() == 0) {
				continue;
			}
			String dataStr = list.get(0);
			JSONObject obj = null;
			try {
				obj = JSONObject.fromObject(dataStr);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (obj == null) {
				continue;
			}
			dataObj.put(item.getDataKey(), obj);
		}
		json.put("code", 0);
		json.put("message", "操作成功");
		json.put("data", dataObj);
		
		super.writeRs(response, json);
		return Action.NONE;
	}

	public DataConsolidationService getDataConsolidationService() {
		return dataConsolidationService;
	}

	public void setDataConsolidationService(
			DataConsolidationService dataConsolidationService) {
		this.dataConsolidationService = dataConsolidationService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
