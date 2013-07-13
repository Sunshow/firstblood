package web.action.statics;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.statics.DataConsolidation;
import com.lehecai.admin.web.domain.statics.DataConsolidationItem;
import com.lehecai.admin.web.service.statics.DataConsolidationService;

public class DataConsolidationAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	
	private DataConsolidationService dataConsolidationService;
	
	private DataConsolidation dataConsolidation;
	
	private List<DataConsolidation> dataConsolidationList;
	private List<DataConsolidationItem> dataConsolidationItemList;
	
	public String handle() {
		logger.info("进入查询json数据整合列表");
		dataConsolidationList = dataConsolidationService.list();
		logger.info("查询json数据整合列表结束");
		return "list";
	}
	
	/**
	 * 添加/修改数据整合
	 * @return
	 */
	public String manage() {
		logger.info("进入更新数据整合");
		if (dataConsolidation != null) {
			if (StringUtils.isEmpty(dataConsolidation.getName())) {
				logger.error("名称为空");
				super.setErrorMessage("名称不能为空");
				return "failure";
			}
			dataConsolidationService.manage(dataConsolidation, dataConsolidationItemList);
		} else {
			logger.error("更新数据整合，提交的表单为空");
			super.setErrorMessage("更新数据整合，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/statics/dataConsolidation.do");
		
		logger.info("更新dataConsolidation结束");
		return "success";
	}
	
	/**
	 * 转向添加/修改数据整合
	 */
	public String input() {
		logger.info("进入输入数据整合信息");
		if (dataConsolidation != null) {
			if (dataConsolidation.getId() != null) {//修改
				dataConsolidation = dataConsolidationService.get(dataConsolidation.getId());
				dataConsolidationItemList = dataConsolidationService.list(dataConsolidation.getId());
			}
		}
		return "inputForm";
	}

	public DataConsolidationService getDataConsolidationService() {
		return dataConsolidationService;
	}

	public void setDataConsolidationService(
			DataConsolidationService dataConsolidationService) {
		this.dataConsolidationService = dataConsolidationService;
	}

	public DataConsolidation getDataConsolidation() {
		return dataConsolidation;
	}

	public void setDataConsolidation(DataConsolidation dataConsolidation) {
		this.dataConsolidation = dataConsolidation;
	}

	public List<DataConsolidation> getDataConsolidationList() {
		return dataConsolidationList;
	}

	public void setDataConsolidationList(
			List<DataConsolidation> dataConsolidationList) {
		this.dataConsolidationList = dataConsolidationList;
	}

	public List<DataConsolidationItem> getDataConsolidationItemList() {
		return dataConsolidationItemList;
	}

	public void setDataConsolidationItemList(
			List<DataConsolidationItem> dataConsolidationItemList) {
		this.dataConsolidationItemList = dataConsolidationItemList;
	}
}
