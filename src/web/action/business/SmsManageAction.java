package web.action.business;

import java.io.Reader;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.ChuangShiManDaoSms;
import com.lehecai.admin.web.service.business.ChuangShiManDaoSmsService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreHttpUtils;

public class SmsManageAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String URL = "http://sdk2.entinfo.cn:8060/webservice.asmx";
	
	private static final String URL_GET_BALANCE = URL + "/GetBalance";
	private static final String URL_CHARGE_UP = URL + "/ChargUp";
	private static final String URL_RECEIVE_MESSAGE = URL + "/RECSMS_UTF8";
	
	private ChuangShiManDaoSmsService chuangShiManDaoSmsService;
	
	private List<ChuangShiManDaoSms> smsList;
	
	private String sender;
	private Date beginDate;
	private Date endDate;
	
	private String sn;
	private String pwd;
	private String cardno;//充值卡号
	private String cardpwd;//充值密码
	
	private String balance;//余额
	private String chargeResult;//充值结果信息
	private String receiveResult;//接收短信结果信息
	
	public String handle() {
		logger.info("进入查询创世漫道短信");
		List<String> list = null;
		try {
			list = CoreHttpUtils.getUrl(URL_GET_BALANCE, "sn="+sn+"&pwd="+pwd, CharsetConstant.CHARSET_UTF8, 5000);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (list == null || list.size() == 0) {
			logger.error("查询余额失败");
			super.setErrorMessage("查询余额失败");
			return "failure";
		}
		String str = StringUtils.join(list, "");
		
		SAXBuilder sb = new SAXBuilder();
		Reader reader = new StringReader(str);
		try {
			Document returnDoc = sb.build(reader);
			Element rootElement = returnDoc.getRootElement();
			balance = rootElement.getText().trim();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		smsList = chuangShiManDaoSmsService.list(sender, beginDate, endDate, super.getPageBean());
		PageBean pageBean = chuangShiManDaoSmsService.getPageBean(sender, beginDate, endDate, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		
		return "list";
	}
	public String charge() {
		logger.info("进入充值");
		List<String> list = null;
		try {
			list = CoreHttpUtils.getUrl(URL_CHARGE_UP, "sn="+sn+"&pwd="+pwd+"&cardno="+cardno+"&cardpwd="+cardpwd, CharsetConstant.CHARSET_UTF8, 5000);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (list == null || list.size() == 0) {
			logger.error("充值失败");
			super.setErrorMessage("充值失败");
			return "failure";
		}
		String str = StringUtils.join(list, "");
		
		SAXBuilder sb = new SAXBuilder();
		Reader reader = new StringReader(str);
		try {
			Document returnDoc = sb.build(reader);
			Element rootElement = returnDoc.getRootElement();
			chargeResult = rootElement.getTextTrim();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		super.setForwardUrl("/business/smsManage.do?chargeResult=" + chargeResult);
		return "forward";
	}
	
	@SuppressWarnings("unchecked")
	public String receiveMessage() {
		logger.info("进入接收短信");
		List<String> list = null;
		try {
			list = CoreHttpUtils.getUrl(URL_RECEIVE_MESSAGE, "sn="+sn+"&pwd="+pwd, CharsetConstant.CHARSET_UTF8, 5000);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (list == null || list.size() == 0) {
			logger.error("接收短信失败");
			super.setErrorMessage("接收短信失败");
			return "failure";
		}
		String str = StringUtils.join(list, "");
		
		//String str = "<ArrayOfMOBody> <MOBody> <total_num>2</total_num> <this_num>2</this_num> <recvtel>121818</recvtel> <sender>13810122553</sender> <content> %E7%88%B8%E7%88%B8%E4%B8%8D%E5%BF%AB%E6%8D%B7%E9%94%AE </content> <recdate>2011-6-7 17:42:33</recdate> </MOBody> <MOBody> <total_num>2</total_num> <this_num>2</this_num> <recvtel>121818</recvtel> <sender>18610116725</sender> <content> %6D%6B%6B%69%6C%6B%6E%6D%6D%6D%6E%6D%61%72%6B%E5%8E%95%E6%89%80%E5%AE%B6%E5%BA%AD </content> <recdate>2011-6-7 17:42:39</recdate> </MOBody> </ArrayOfMOBody>";
		//String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ArrayOfMOBody xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://tempuri.org/\">  <MOBody><total_num>2</total_num>    <this_num>2</this_num>    <recvtel>121818</recvtel>    <sender>13810122553</sender>    <content>%E6%96%A4%E6%96%A4%E8%AE%A1%E8%BE%83%E4%BA%86</content>    <recdate>2011-6-7 18:56:25</recdate>  </MOBody>  <MOBody>    <total_num>2</total_num>    <this_num>2</this_num>    <recvtel>121818</recvtel>    <sender>18610116725</sender>    <content>%E4%BD%A0%E7%B4%AF%E4%BD%A0%E5%A6%B9%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A</content>    <recdate>2011-6-7 18:56:31</recdate>  </MOBody></ArrayOfMOBody>";
		
		SAXBuilder sb = new SAXBuilder();
		Reader reader = new StringReader(str);
		try {
			Document returnDoc = sb.build(reader);
			Element rootElement = returnDoc.getRootElement();
			Namespace namespace = rootElement.getNamespace();
			List children = rootElement.getChildren();
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				Element total_num = element.getChild("total_num", namespace);
				Element recvtel = element.getChild("recvtel", namespace);
				Element sender = element.getChild("sender", namespace);
				Element content = element.getChild("content", namespace);
				Element recdate = element.getChild("recdate", namespace);
				if (receiveResult == null) {
					receiveResult = total_num == null ? "0" : total_num.getText();
				}
				if (receiveResult != null && "-1".equals(receiveResult)) {
					receiveResult = "0";
					continue;
				}
				if (sender == null || content == null) {
					continue;
				}
				ChuangShiManDaoSms sms = new ChuangShiManDaoSms();
				sms.setRecvtel(recvtel.getText());
				sms.setSender(sender.getText());
				sms.setContent(URLDecoder.decode(content.getText(), CharsetConstant.CHARSET_UTF8));
				sms.setRecdate(CoreDateUtils.parseLongDate(recdate.getText()));
				chuangShiManDaoSmsService.manage(sms);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		super.setForwardUrl("/business/smsManage.do?receiveResult=" + receiveResult);
		return "forward";
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getCardpwd() {
		return cardpwd;
	}

	public void setCardpwd(String cardpwd) {
		this.cardpwd = cardpwd;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getChargeResult() {
		return chargeResult;
	}

	public void setChargeResult(String chargeResult) {
		this.chargeResult = chargeResult;
	}
	public String getReceiveResult() {
		return receiveResult;
	}
	public void setReceiveResult(String receiveResult) {
		this.receiveResult = receiveResult;
	}
	public ChuangShiManDaoSmsService getChuangShiManDaoSmsService() {
		return chuangShiManDaoSmsService;
	}
	public void setChuangShiManDaoSmsService(
			ChuangShiManDaoSmsService chuangShiManDaoSmsService) {
		this.chuangShiManDaoSmsService = chuangShiManDaoSmsService;
	}
	public List<ChuangShiManDaoSms> getSmsList() {
		return smsList;
	}
	public void setSmsList(List<ChuangShiManDaoSms> smsList) {
		this.smsList = smsList;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
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
}
