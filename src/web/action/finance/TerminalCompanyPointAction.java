/**
 * 
 */
package web.action.finance;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalCompany;
import com.lehecai.admin.web.domain.finance.TerminalCompanyPoint;
import com.lehecai.admin.web.service.finance.TerminalCompanyPointService;
import com.lehecai.admin.web.service.finance.TerminalCompanyService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

/**
 * @author chirowong
 * 出票商管理
 */
public class TerminalCompanyPointAction extends BaseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4691731087137848274L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private TerminalCompanyService terminalCompanyService;
	private TerminalCompanyPointService terminalCompanyPointService;
	private TerminalCompany terminalCompany;
	private TerminalCompanyPoint terminalCompanyPoint;
	private List<TerminalCompany> terminalCompanys;
	private List<TerminalCompanyPoint> terminalCompanyPoints;
	private Integer lotteryId;
	
	public String handle(){
		logger.info("进入出票商点位列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		terminalCompanyPoints = terminalCompanyPointService.list(terminalCompanyPoint, super.getPageBean());
		PageBean pageBean = terminalCompanyPointService.getPageBean(terminalCompanyPoint,super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}
	
	public String input(){
		logger.info("添加出票商点位");
		TerminalCompany terminalCompany = new TerminalCompany();
		PageBean nopageBean = new PageBean();
		nopageBean.setPageFlag(false);
		terminalCompanys = terminalCompanyService.list(terminalCompany, super.getPageBean());
		if(terminalCompanyPoint != null && terminalCompanyPoint.getId() != null){
			terminalCompanyPoint = terminalCompanyPointService.get(terminalCompanyPoint.getId());
		}else{
			terminalCompanyPoint = new TerminalCompanyPoint();
		}
		return "input";
	}
	
	public String view(){
		logger.info("查看出票商信息");
		if(terminalCompanyPoint != null && terminalCompanyPoint.getId() != null){
			terminalCompanyPoint = terminalCompanyPointService.get(terminalCompanyPoint.getId());
		}else{
			logger.info("查看出票商出错，出票商编码为空");
			super.setErrorMessage("查看出票商出错，出票商编码为空");
			return "failure";
		}
		return "view";
	}
	
	public String del(){
		logger.info("删除出票商点位信息");
		if (terminalCompanyPoint != null && terminalCompanyPoint.getId() != null) {
			terminalCompanyPoint = terminalCompanyPointService.get(terminalCompanyPoint.getId());
			terminalCompanyPointService.del(terminalCompanyPoint);
		} else {
			logger.error("删除出票商点位，编码为空");
			super.setErrorMessage("删除出票商点位，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/finance/terminalCompanyPoint.do");
		logger.info("删除出票商点位结束");
		return "forward";
	}
	
	public String manage(){
		logger.info("进入更新出票商点位信息");
		if (terminalCompanyPoint != null) {
			if (terminalCompanyPoint.getCompanyId() == null || terminalCompanyPoint.getCompanyId() == 0) {
				logger.error("更新出票商点位信息，出票商名为空");
				super.setErrorMessage("出票商名不能为空");
				return "failure";
			}
			if (lotteryId == null) {
				logger.error("更新出票商点位信息，没有选择彩种");
				super.setErrorMessage("没有选择彩种");
				return "failure";
			}
			if (terminalCompanyPoint.getPoint() == null) {
				logger.error("点位为空");
				super.setErrorMessage("点位不能为空");
				return "failure";
			}
			terminalCompanyPoint.setLotteryType(LotteryType.getItem(lotteryId));
			if(terminalCompanyPoint.getId() == null){
				List<TerminalCompanyPoint> terminalCompanyPointList = terminalCompanyPointService.list(terminalCompanyPoint, null);
				if(terminalCompanyPointList != null && terminalCompanyPointList.size() > 0){
					logger.error("已存在出票商对应的彩种点位设置");
					super.setErrorMessage("已存在出票商对应的彩种点位设置");
					return "failure";
				}
				terminalCompanyPoint.setCreateTime(new Date());
			}else{
				TerminalCompanyPoint tcp = terminalCompanyPointService.get(terminalCompanyPoint.getId());
				if(tcp.getCompanyId().longValue() != terminalCompanyPoint.getCompanyId().longValue()){//修改出票商编码需检验重复
					List<TerminalCompanyPoint> terminalCompanyPointList = terminalCompanyPointService.list(terminalCompanyPoint, null);
					if(terminalCompanyPointList != null && terminalCompanyPointList.size() > 0){
						logger.error("已存在出票商对应的彩种点位设置");
						super.setErrorMessage("已存在出票商对应的彩种点位设置");
						return "failure";
					}
				}
				if(tcp.getLotteryType().getValue() != terminalCompanyPoint.getLotteryType().getValue()){//修改彩种需检验重复
					List<TerminalCompanyPoint> terminalCompanyPointList = terminalCompanyPointService.list(terminalCompanyPoint, null);
					if(terminalCompanyPointList != null && terminalCompanyPointList.size() > 0){
						logger.error("已存在出票商对应的彩种点位设置");
						super.setErrorMessage("已存在出票商对应的彩种点位设置");
						return "failure";
					}
				}
			}
			terminalCompanyPoint.setCompanyName(terminalCompanyService.get(terminalCompanyPoint.getCompanyId()).getName());
			terminalCompanyPointService.manage(terminalCompanyPoint);
			super.setForwardUrl("/finance/terminalCompanyPoint.do");
			logger.info("更新出票商点位信息结束");
			return "success";
		} else {
			logger.error("添加出票商点位错误，提交表单为空");
			super.setErrorMessage("添加出票商点位错误，提交表单不能为空");
			return "failure";
		}
	}
	
	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.get();
	}

	public TerminalCompanyService getTerminalCompanyService() {
		return terminalCompanyService;
	}
	
	public void setTerminalCompanyService(
			TerminalCompanyService terminalCompanyService) {
		this.terminalCompanyService = terminalCompanyService;
	}

	public TerminalCompany getTerminalCompany() {
		return terminalCompany;
	}

	public void setTerminalCompany(TerminalCompany terminalCompany) {
		this.terminalCompany = terminalCompany;
	}

	public List<TerminalCompany> getTerminalCompanys() {
		return terminalCompanys;
	}

	public void setTerminalCompanys(List<TerminalCompany> terminalCompanys) {
		this.terminalCompanys = terminalCompanys;
	}

	public TerminalCompanyPointService getTerminalCompanyPointService() {
		return terminalCompanyPointService;
	}

	public void setTerminalCompanyPointService(
			TerminalCompanyPointService terminalCompanyPointService) {
		this.terminalCompanyPointService = terminalCompanyPointService;
	}

	public TerminalCompanyPoint getTerminalCompanyPoint() {
		return terminalCompanyPoint;
	}

	public void setTerminalCompanyPoint(TerminalCompanyPoint terminalCompanyPoint) {
		this.terminalCompanyPoint = terminalCompanyPoint;
	}

	public List<TerminalCompanyPoint> getTerminalCompanyPoints() {
		return terminalCompanyPoints;
	}

	public void setTerminalCompanyPoints(
			List<TerminalCompanyPoint> terminalCompanyPoints) {
		this.terminalCompanyPoints = terminalCompanyPoints;
	}
	
	public List<LotteryType> getLotteryTypes(){
		return OnSaleLotteryList.get();
	}

	public Integer getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(Integer lotteryId) {
		this.lotteryId = lotteryId;
	}
}
