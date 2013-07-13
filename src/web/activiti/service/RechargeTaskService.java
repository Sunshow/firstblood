/**
 * 
 */
package web.activiti.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.lehecai.admin.web.activiti.entity.HistoryQueryCondition;
import com.lehecai.admin.web.activiti.entity.RechargeTask;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.utils.DateUtil;

/**
 * @author qatang
 *
 */
public class RechargeTaskService {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public RechargeTask create(RechargeTask rechargeTask) {
		entityManager.persist(rechargeTask);
		entityManager.flush();
		return rechargeTask;
	}
	@Transactional
	public void merge(RechargeTask rechargeTask) {
		entityManager.merge(rechargeTask);
	}
	
	public RechargeTask get(Long id) {
		return entityManager.find(RechargeTask.class, id);
	}
	
	public RechargeTask getByProcessId(String processId) {
		String sql = "select t from RechargeTask t where t.processId=:processId";
		return (RechargeTask)entityManager.createQuery(sql).setParameter("processId", processId).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<RechargeTask> query(Date startDate, Date endDate, Double startAmount, Double endAmount,String account, Double amount, String realName, PageBean pageBean) {

		StringBuffer sql = new StringBuffer(300);
		sql.append("select b.REAL_NAME,b.ID,b.CREATED_TIME,b.AMOUNT,b.MEMO,b.initiator,b.USER_CARD_NO, a.START_TIME_,a.END_TIME_,b.CREATED_TIME,b.PROCESS_ID from ACT_HI_PROCINST a join ").append(HistoryQueryCondition.RECHARGEROCESS.getTableName()).append(" b on a.PROC_INST_ID_ = b.PROCESS_ID ");
		sql.append("where a.PROC_DEF_ID_ like '%").append(HistoryQueryCondition.RECHARGEROCESS.getProcessName()).append("%' " );
		sql.append("and a.END_TIME_ is not null ");

		if (startDate != null) {
			sql.append("and b.CREATED_TIME >= \"").append(DateUtil.formatDate(startDate, DateUtil.DATETIME)).append("\" ");
		}
		if (endDate != null) {
			sql.append("and b.CREATED_TIME <\"").append(DateUtil.formatDate(endDate, DateUtil.DATETIME)).append("\" ");
		}
		if (startAmount != null) {
			sql.append(" and b.AMOUNT >= ").append(startAmount).append(" ");
		}
		if (endAmount != null) {
			sql.append(" and b.AMOUNT <= ").append(endAmount).append(" ");
		}
		if (!StringUtils.isEmpty(account)) {
			sql.append(" and b.USER_CARD_NO = \"").append(account).append("\" ");
		}
		if (amount != null) {
			sql.append(" and b.AMOUNT = ").append(amount).append(" ");
		}
		if (!StringUtils.isEmpty(realName)) {
			sql.append(" and b.REAL_NAME = \"").append(realName).append("\" ");
		}
		
		Query query = entityManager.createNativeQuery(sql.toString());

		if (pageBean != null && pageBean.isPageFlag()) {
			if (pageBean.getPage() > 0) {
				query.setFirstResult((pageBean.getPage() - 1)
						* pageBean.getPageSize());
			}
			if (pageBean.getPageSize() > 0) {
				query.setMaxResults(pageBean.getPageSize());
			}
		}
		List<RechargeTask> list = null;
		List<Object[]> resultList = query.getResultList();
		if(resultList != null && resultList.size() > 0){
			list = new ArrayList<RechargeTask>();
			for(Object[] arrObj : resultList){
				RechargeTask task = new RechargeTask();
				task.setRealName((String)arrObj[0]);
				task.setId((Long)arrObj[1]);
				task.setCreatedTime((Date)arrObj[2]);
				task.setAmount((Double)arrObj[3]);
				task.setMemo((String)arrObj[4]);
				task.setInitiator((String)arrObj[5]);
				task.setUserCardNo((String)arrObj[6]);
				list.add(task);
			}
		}
		return list;
	}
}
