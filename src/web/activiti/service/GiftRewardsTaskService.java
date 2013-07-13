/**
 * 
 */
package web.activiti.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.lehecai.admin.web.activiti.entity.GiftRewardsTask;

/**
 * @author chirowong
 *
 */
public class GiftRewardsTaskService {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public GiftRewardsTask create(GiftRewardsTask giftRewardsTask) {
		entityManager.persist(giftRewardsTask);
		entityManager.flush();
		return giftRewardsTask;
	}
	@Transactional
	public void merge(GiftRewardsTask giftRewardsTask) {
		entityManager.merge(giftRewardsTask);
	}
	
	public GiftRewardsTask get(Long id) {
		return entityManager.find(GiftRewardsTask.class, id);
	}
	
	public GiftRewardsTask getByProcessId(String processId) {
		String sql = "select t from GiftRewardsTask t where t.processId=:processId";
		return (GiftRewardsTask)entityManager.createQuery(sql).setParameter("processId", processId).getSingleResult();
	}
}
