package web.action.member;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.export.OperationExport;
import com.lehecai.admin.web.service.member.OperationService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.OperationLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.OperationStatus;
import com.lehecai.core.lottery.OperationType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(OperationAction.class);
	
	private OperationService operationService;
	
	private OperationLog operation;
	
	private List<OperationLog> operations;
	
	private Integer operationTypeId;
	private Integer operationStatusId;
	private String username;
	private Long uid;
	private Date beginDate;
	private Date endDate;
	private Long sourceId;
	private boolean distinctMember; 
	
	private String orderStr;
	private String orderView;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	private InputStream inputStream;
	private String fileName;
	
	public String handle(){
		logger.info("进入查询操作流水数据");
		if (beginDate == null) {
			beginDate = this.getLastDate();
		}
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入查询操作流水数据");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (beginDate == null) {
			beginDate = this.getLastDate();
		}
		
		if(beginDate != null && endDate != null){			
			if(!DateUtil.isSameMonth(beginDate, endDate)) {
				logger.error("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		OperationType ot = operationTypeId == null ? null : OperationType.getItem(operationTypeId);
		OperationStatus os = operationStatusId == null ? null : OperationStatus.getItem(operationStatusId);
		Map<String, Object> map;
		try {
			map = operationService.getResult(ot,
					os, username, uid, beginDate, endDate, sourceId,
					distinctMember, getOrderStr(), getOrderView(), super
							.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){
			operations = (List<OperationLog>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询操作流水数据异常");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String export() {
		logger.info("进入导出操作流水数据");
		if(beginDate != null && endDate != null){			
			if (!DateUtil.isSameMonth(beginDate, endDate)) {
				logger.info("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		PageBean pageBean = super.getPageBean();
		pageBean.setPageSize(10000);//max 10000 items
		
		OperationType ot = operationTypeId == null ? null : OperationType.getItem(operationTypeId);
		OperationStatus ops = operationStatusId == null ? null : OperationStatus.getItem(operationStatusId);
		
		Map<String, Object> map;
		try {
			map = operationService.getResult(ot,
					ops, username, uid, beginDate, endDate, sourceId,
					distinctMember, getOrderStr(), getOrderView(), pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){	
			operations = (List<OperationLog>)map.get(Global.API_MAP_KEY_LIST);
			try {
				Workbook workBook = OperationExport.export(operations, distinctMember);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				workBook.write(os);
				inputStream  = new ByteArrayInputStream(os.toByteArray());
				this.fileName = (new Date()).getTime() + ".xls";
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("文件输出流写入错误");
				return "failure";
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("生成excel文件时错误");
				return "failure";
			}
		}
		logger.info("导出操作流水数据结束");
		return "download";
	}
	
	public OperationService getOperationService() {
		return operationService;
	}
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}
	public String getOrderStr() {
		if(orderStr == null && !"".equals(orderStr)){
			orderStr = OperationLog.ORDER_TIMELINE;
		}
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		if(orderView == null && !"".equals(orderView)){
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(OperationLog.ORDER_LOG_ID, "流水号");
		orderStrMap.put(OperationLog.ORDER_TIMELINE, "操作时间");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public OperationLog getOperation() {
		return operation;
	}
	public void setOperation(OperationLog operation) {
		this.operation = operation;
	}
	public List<OperationLog> getOperations() {
		return operations;
	}
	public void setOperations(List<OperationLog> operations) {
		this.operations = operations;
	}
	public List<OperationType> getOperationTypes(){
		return OperationType.getItems();
	}
	public List<OperationStatus> getOperationStatuses(){
		return OperationStatus.getItems();
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getOperationTypeId() {
		return operationTypeId;
	}
	public void setOperationTypeId(Integer operationTypeId) {
		this.operationTypeId = operationTypeId;
	}
	public Integer getOperationStatusId() {
		return operationStatusId;
	}
	public void setOperationStatusId(Integer operationStatusId) {
		this.operationStatusId = operationStatusId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public boolean isDistinctMember() {
		return distinctMember;
	}
	public void setDistinctMember(boolean distinctMember) {
		this.distinctMember = distinctMember;
	}
}
