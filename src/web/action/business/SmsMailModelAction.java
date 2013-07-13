package web.action.business;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.SmsMailModel;
import com.lehecai.admin.web.enums.ModelType;
import com.lehecai.admin.web.enums.TextType;
import com.lehecai.admin.web.service.business.SmsMailModelService;
import com.lehecai.admin.web.utils.PageUtil;

public class SmsMailModelAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(SmsMailModelAction.class);
	
	private SmsMailModelService smsMailModelService;
	private SmsMailModel smsMailModel;
	
	private List<SmsMailModel> smsMailModelList;
	
	private String title;
	private String content;
	private Integer modelTypeValue;
	private Date createDateFrom;
	private Date createDateTo;
	private Date updateDateFrom;
	private Date updateDateTo;
	
	private Integer textTypeId;
	
	public String handle() {
		logger.info("进入查询短信邮件模板列表");
		return "list";
	}
	
	public String query() {
		logger.info("进入查询短信邮件模板列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		smsMailModelList = smsMailModelService.list(title,content, modelTypeValue, textTypeId, createDateFrom, createDateTo, updateDateFrom, updateDateTo, super.getPageBean());
		PageBean pageBean = smsMailModelService.getPageBean(title, content, modelTypeValue, textTypeId, createDateFrom, createDateTo, updateDateFrom, updateDateTo, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}

	public String manage() {
		logger.info("进入更新短信邮件模板信息");
		Date update = new Date();
		Date create = null;
		if (modelTypeValue != null && modelTypeValue != 0) {
			smsMailModel.setType(ModelType.getItem(modelTypeValue));
		} else {
			logger.error("模板类型错误");
			super.setErrorMessage("模板类型错误");
			return "failure";
		}
		
		if (textTypeId == null || textTypeId == 0) {
			logger.error("文本类型错误");
			super.setErrorMessage("文本类型错误");
			return "failure";
		} else {
			smsMailModel.setTextType(TextType.getItem(textTypeId));
		}
		
		if (textTypeId == TextType.HTMLTYPE.getValue() && modelTypeValue == ModelType.SMS.getValue()) {
			logger.error("手机短信暂不支持富文本");
			super.setErrorMessage("手机短信暂不支持富文本");
			return "failure";
		}
		
		if (smsMailModel.getTitle() == null || "".equals(smsMailModel.getTitle())) {
			logger.error("标题不能为空");
			super.setErrorMessage("标题不能为空");
			return "failure";
		}
		
		if (smsMailModel.getContent() == null || "".equals(smsMailModel.getContent())) {
			logger.error("内容不能为空");
			super.setErrorMessage("内容不能为空");
			return "failure";
		}
		
		if (smsMailModel.getId() == null || smsMailModel.getId() == 0) {
			create = new Date();
		}
		if (update != null) {
			smsMailModel.setUpdateTime(update);
		}
		if (create != null) {
			smsMailModel.setCreateTime(create);
		}
		smsMailModelService.merge(smsMailModel);
		super.setForwardUrl("/business/smsMailModel.do");
		logger.info("更新短信邮件模板信息结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入短信邮件模板信息");
		if (smsMailModel != null && smsMailModel.getId() != null) {
			smsMailModel = smsMailModelService.get(smsMailModel.getId());
		}
		return "inputForm";
	}
	
	public String del() {
		logger.info("进入删除短信邮件模板");
		if (smsMailModel.getId() == null || smsMailModel.getId() == 0) {
			logger.error("模板ID为空");
			super.setErrorMessage("模板ID不能为空");
			super.setForwardUrl("/business/smsMailModel.do");
			return "failure";
		}
		
		smsMailModel = smsMailModelService.get(smsMailModel.getId());		
		smsMailModelService.del(smsMailModel);
		
		super.setForwardUrl("/business/smsMailModel.do");
		logger.info("删除短信邮件模板结束");
		return "success";
	}
	
	public String view() {
		logger.info("进入查看短信邮件模板详细信息");
		if (smsMailModel.getId() == null || smsMailModel.getId() == 0) {
			logger.error("模板ID为空");
			super.setErrorMessage("模板ID不能为空");
			super.setForwardUrl("/business/smsMailModel.do");
			return "failure";
		}
		smsMailModel = smsMailModelService.get(smsMailModel.getId());
		logger.info("查看短信邮件模板详细信息结束");
		return "view";
	}
	
	public List<SmsMailModel> getSmsMailModelList() {
		return smsMailModelList;
	}

	public void setSmsMailModelList(List<SmsMailModel> smsMailModelList) {
		this.smsMailModelList = smsMailModelList;
	}

	public SmsMailModelService getSmsMailModelService() {
		return smsMailModelService;
	}

	public void setSmsMailModelService(SmsMailModelService smsMailModelService) {
		this.smsMailModelService = smsMailModelService;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getModelTypeValue() {
		return modelTypeValue;
	}

	public void setModelTypeValue(Integer modelTypeValue) {
		this.modelTypeValue = modelTypeValue;
	}

	public SmsMailModel getSmsMailModel() {
		return smsMailModel;
	}

	public void setSmsMailModel(SmsMailModel smsMailModel) {
		this.smsMailModel = smsMailModel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<ModelType> getModelTypes() {
		return ModelType.list;
	}

	public Date getCreateDateFrom() {
		return createDateFrom;
	}

	public void setCreateDateFrom(Date createDateFrom) {
		this.createDateFrom = createDateFrom;
	}

	public Date getCreateDateTo() {
		return createDateTo;
	}

	public void setCreateDateTo(Date createDateTo) {
		this.createDateTo = createDateTo;
	}

	public Date getUpdateDateFrom() {
		return updateDateFrom;
	}

	public void setUpdateDateFrom(Date updateDateFrom) {
		this.updateDateFrom = updateDateFrom;
	}

	public Date getUpdateDateTo() {
		return updateDateTo;
	}

	public void setUpdateDateTo(Date updateDateTo) {
		this.updateDateTo = updateDateTo;
	}
	
	public List<TextType> getTextTypes() {
		return TextType.list;
		
	}

	public Integer getTextTypeId() {
		return textTypeId;
	}

	public void setTextTypeId(Integer textTypeId) {
		this.textTypeId = textTypeId;
	}
	
	public TextType getHtmlTextType(){
		return TextType.HTMLTYPE;
	}
	
	public ModelType getSmsType(){
		return ModelType.SMS;
	}
	
	public TextType getPlainTextType(){
		return TextType.PLAINTYPE;
	}
	
}
