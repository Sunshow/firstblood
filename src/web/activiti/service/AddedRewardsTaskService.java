/**
 * 
 */
package web.activiti.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.lehecai.admin.web.activiti.entity.AddedRewardsTask;

/**
 * @author qatang
 *
 */
public class AddedRewardsTaskService {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public AddedRewardsTask create(AddedRewardsTask addedRewardsTask) {
		entityManager.persist(addedRewardsTask);
		entityManager.flush();
		return addedRewardsTask;
	}
	@Transactional
	public void merge(AddedRewardsTask addedRewardsTask) {
		entityManager.merge(addedRewardsTask);
	}
	
	public AddedRewardsTask get(Long id) {
		return entityManager.find(AddedRewardsTask.class, id);
	}
	
	public AddedRewardsTask getByProcessId(String processId) {
		String sql = "select t from AddedRewardsTask t where t.processId=:processId";
		return (AddedRewardsTask)entityManager.createQuery(sql).setParameter("processId", processId).getSingleResult();
	}
	@SuppressWarnings("unchecked")
	public List<AddedRewardsTask> getByPlanId(String planId) {
		String sql = "select t from AddedRewardsTask t where t.planId=:planId";
		return (List<AddedRewardsTask>)entityManager.createQuery(sql).setParameter("planId", planId).getResultList();
	}
	
	@Transactional
	public void delete(AddedRewardsTask addedRewardsTask){
		AddedRewardsTask task = getByProcessId(addedRewardsTask.getProcessId());
		entityManager.remove(entityManager.merge(task));
	}
}
