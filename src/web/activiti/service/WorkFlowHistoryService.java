package web.activiti.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.lehecai.admin.web.activiti.constant.ProcessStatusType;
import com.lehecai.admin.web.activiti.entity.GiftCardsTask;
import com.lehecai.admin.web.activiti.entity.HistoryQueryObject;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.utils.DateUtil;

public class WorkFlowHistoryService {
	@PersistenceContext
	private EntityManager entityManager;
	
	public List<HistoryQueryObject> queryHistory(HistoryQueryObject hqo,PageBean pageBean,Boolean finished,Date beginTime,Date endTime){
		StringBuffer sb = new StringBuffer();
		sb.append("select a.START_TIME_,a.END_TIME_,b.initiator,b.CREATED_TIME,b.PROCESS_ID,b.HANDLE_USER from ACT_HI_PROCINST a join ").append(hqo.getProcessTable()).append(" b on a.PROC_INST_ID_ = b.PROCESS_ID ");
		sb.append("where a.PROC_DEF_ID_ like '%").append(hqo.getProcessName()).append("%' " );
		if (finished != null) {
			if(finished){
				sb.append("and a.END_TIME_ is not null ");
			}else{
				sb.append("and a.END_TIME_ is null ");
			}
		}
		String initiator = hqo.getInitiator();
		if(initiator != null && !"".equals(initiator)){
			sb.append("and b.initiator = \"").append(initiator).append("\" ");
		}
		if(beginTime != null){
			sb.append("and b.CREATED_TIME >= \"").append(DateUtil.formatDate(beginTime, DateUtil.DATETIME)).append("\" ");
		}
		if(endTime != null){
			sb.append("and b.CREATED_TIME <\"").append(DateUtil.formatDate(endTime, DateUtil.DATETIME)).append("\" ");
		}
		sb.append("order by b.CREATED_TIME desc");
		Query query = entityManager.createNativeQuery(sb.toString());
		if(pageBean != null && pageBean.isPageFlag()){
			if(pageBean.getPageSize() != 0){
				query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
				query.setMaxResults(pageBean.getPageSize());
			}
		}
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		List<HistoryQueryObject> historyQueryObjectList = null;
		if(resultList != null && resultList.size() > 0){
			historyQueryObjectList = new ArrayList<HistoryQueryObject>();
			for(Object[] arrObj : resultList){
				HistoryQueryObject _hqo = new HistoryQueryObject();
				_hqo.setTaskStartTime((Date)arrObj[0]);
				_hqo.setTaskEndTime((Date)arrObj[1]);
				_hqo.setInitiator((String)arrObj[2]);
				_hqo.setCreatedTime((Date)arrObj[3]);
				_hqo.setProcessId((String)arrObj[4]);
				_hqo.setHandleUser((Long)arrObj[5]);
				historyQueryObjectList.add(_hqo);
			}
		}
		return historyQueryObjectList;
	}
	
	public List<HistoryQueryObject> queryGiftCardsHistory(HistoryQueryObject hqo,PageBean pageBean,Boolean finished,Date beginTime,Date endTime){
		StringBuffer sb = new StringBuffer();
		sb.append("select a.START_TIME_,a.END_TIME_,b.initiator,b.CREATED_TIME,b.PROCESS_ID,b.HANDLE_USER,b.CARD_MONEY,b.CARD_AMOUNT,b.ACTIVITY_CONTENT,b.STATUS from ACT_HI_PROCINST a join ").append(hqo.getProcessTable()).append(" b on a.PROC_INST_ID_ = b.PROCESS_ID ");
		sb.append("where a.PROC_DEF_ID_ like '%").append(hqo.getProcessName()).append("%' " );
		if (finished != null) {
			if(finished){
				sb.append("and a.END_TIME_ is not null ");
			}else{
				sb.append("and a.END_TIME_ is null ");
			}
		}
		String initiator = hqo.getInitiator();
		if(initiator != null && !"".equals(initiator)){
			sb.append("and b.initiator = \"").append(initiator).append("\" ");
		}
		if(beginTime != null){
			sb.append("and b.CREATED_TIME >= \"").append(DateUtil.formatDate(beginTime, DateUtil.DATETIME)).append("\" ");
		}
		if(endTime != null){
			sb.append("and b.CREATED_TIME <\"").append(DateUtil.formatDate(endTime, DateUtil.DATETIME)).append("\" ");
		}
		sb.append("order by b.CREATED_TIME desc");
		Query query = entityManager.createNativeQuery(sb.toString());
		if(pageBean != null && pageBean.isPageFlag()){
			if(pageBean.getPageSize() != 0){
				query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
				query.setMaxResults(pageBean.getPageSize());
			}
		}
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		List<HistoryQueryObject> historyQueryObjectList = null;
		if(resultList != null && resultList.size() > 0){
			historyQueryObjectList = new ArrayList<HistoryQueryObject>();
			for(Object[] arrObj : resultList){
				HistoryQueryObject _hqo = new HistoryQueryObject();
				_hqo.setTaskStartTime((Date)arrObj[0]);
				_hqo.setTaskEndTime((Date)arrObj[1]);
				_hqo.setInitiator((String)arrObj[2]);
				_hqo.setCreatedTime((Date)arrObj[3]);
				_hqo.setProcessId((String)arrObj[4]);
				_hqo.setHandleUser((Long)arrObj[5]);
				GiftCardsTask giftCardsTask = new GiftCardsTask();
				giftCardsTask.setCardMoney((Double)arrObj[6]);
				giftCardsTask.setCardAmount((Integer)arrObj[7]);
				giftCardsTask.setActivityContent((String)arrObj[8]);
				Integer status = (Integer)arrObj[9];
				if (status != null && status == ProcessStatusType.COMPLETE.getValue()) {
					giftCardsTask.setCompleteFlag(true);
				} else {
					giftCardsTask.setCompleteFlag(false);
				}
				giftCardsTask.setStatus(status);
				_hqo.setGiftCardsTask(giftCardsTask);
				historyQueryObjectList.add(_hqo);
			}
		}
		return historyQueryObjectList;
	}
	
	public PageBean queryHistoryPageBean(HistoryQueryObject hqo,PageBean pageBean,Boolean finished,Date beginTime,Date endTime){
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from ACT_HI_PROCINST a join ").append(hqo.getProcessTable()).append(" b on a.PROC_INST_ID_ = b.PROCESS_ID ");
		sb.append("where a.PROC_DEF_ID_ like '%").append(hqo.getProcessName()).append("%' " );
		if (finished != null) {
			if(finished){
				sb.append("and a.END_TIME_ is not null ");
			}else{
				sb.append("and a.END_TIME_ is null ");
			}
		}
		String initiator = hqo.getInitiator();
		if(initiator != null && !"".equals(initiator)){
			sb.append("and b.initiator = \"").append(initiator).append("\" ");
		}
		if(beginTime != null){
			sb.append("and b.CREATED_TIME >= \"").append(DateUtil.formatDate(beginTime, DateUtil.DATETIME)).append("\" ");
		}
		if(endTime != null){
			sb.append("and b.CREATED_TIME <\"").append(DateUtil.formatDate(endTime, DateUtil.DATETIME)).append("\" ");
		}
		Query query = entityManager.createNativeQuery(sb.toString());
		if(pageBean != null && pageBean.isPageFlag()){
			int totalCount = ((Long)query.getSingleResult()).intValue();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if(pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if(totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		return pageBean;
	}
}
