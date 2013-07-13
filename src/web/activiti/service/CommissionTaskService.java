package web.activiti.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.lehecai.admin.web.activiti.entity.CommissionTask;

public class CommissionTaskService {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public CommissionTask create(CommissionTask task) {
		entityManager.persist(task);
		entityManager.flush();
		return task;
	}
	@Transactional
	public void merge(CommissionTask task) {
		entityManager.merge(task);
	}
	
	public CommissionTask get(Long id) {
		return entityManager.find(CommissionTask.class, id);
	}
	
	public CommissionTask getByProcessId(String processId) {
		String sql = "select t from CommissionTask t where t.processId=:processId";
		return (CommissionTask)entityManager.createQuery(sql).setParameter("processId", processId).getSingleResult();
	}
}
