package web.dao.impl.multiconfirm;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.multiconfirm.MulticonfirmConfigDao;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfigType;

public class MulticonfirmConfigDaoImpl extends HibernateDaoSupport implements MulticonfirmConfigDao {

	@SuppressWarnings("unchecked")
	@Override
	public MulticonfirmConfig get(final String configKey) {
		List<MulticonfirmConfig> list = (List<MulticonfirmConfig>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from MulticonfirmConfig u where 1 = 1");
						if(configKey != null && !configKey.equals("")){
							hql.append(" and u.configKey = :configKey");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
								
						if(configKey != null && !configKey.equals("")){
								query.setParameter("configKey", configKey);
						}
						return query.list();
					}
				});
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public MulticonfirmConfig get(Long id) {
		return getHibernateTemplate().get(MulticonfirmConfig.class, id);
	}

	@Override
	public MulticonfirmConfig manageConfig(MulticonfirmConfig multiconfirmConfig) {
		return getHibernateTemplate().merge(multiconfirmConfig);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MulticonfirmConfig> getConfigList(final Long id, final String configKey,
			final String configName, final MulticonfirmConfigType mct, final Date createTimeFrom, final Date createTimeTo, final PageBean pageBean) {
		return (List<MulticonfirmConfig>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from MulticonfirmConfig u where 1 = 1");
						if (id != null && id != 0){
							hql.append(" and u.id = :id");
						}
						if (configKey != null && !configKey.equals("")){
							hql.append(" and u.configKey like '%").append(configKey).append("%'");
						}
						if (configName != null && !configName.equals("")){
							hql.append(" and u.configName like '%").append(configName).append("%'");
						}
						if (mct != null && mct.getValue() != MulticonfirmConfigType.ALL.getValue()) {
							hql.append(" and u.multiconfirmConfigType = :mct");
						}
						if (createTimeFrom != null) {
							hql.append(" and u.createTime >= :createTimeFrom");
						}
						if (createTimeTo != null) {
							hql.append(" and u.createTime < :createTimeTo");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
								
						if(id != null && id != 0){
							query.setParameter("id", id);
						}
						if(createTimeFrom != null) {
							query.setParameter("createTimeFrom", createTimeFrom);
						}
						if(createTimeTo != null) {
							query.setParameter("createTimeTo", createTimeTo);
						}
						if (mct != null && mct.getValue() != MulticonfirmConfigType.ALL.getValue()) {
							query.setParameter("mct", mct);
						}
						
						if(pageBean != null && pageBean.isPageFlag()){
							if(pageBean.getPageSize() != 0){
								query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
								query.setMaxResults(pageBean.getPageSize());
							}
						}
						return query.list();
					}
				});
	}

	@Override
	public void del(MulticonfirmConfig multiconfirmConfig) {
		getHibernateTemplate().delete(multiconfirmConfig);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getConfigPageBean(final Long id, final String configKey,
			final String configName, final MulticonfirmConfigType mct, final Date createTimeFrom, final Date createTimeTo, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from MulticonfirmConfig u where 1 = 1");
						
						if(id != null && id != 0){
							hql.append(" and u.id = :id");
						}
						if(configKey != null && !configKey.equals("")){
							hql.append(" and u.configKey like '%").append(configKey).append("%'");
						}
						if(configName != null && !configName.equals("")){
							hql.append(" and u.configName like '%").append(configName).append("%'");
						}
						if (mct != null && mct.getValue() != MulticonfirmConfigType.ALL.getValue()) {
							hql.append(" and u.multiconfirmConfigType = :mct");
						}
						if(createTimeFrom != null) {
							hql.append(" and u.createTime >= :createTimeFrom");
						}
						if(createTimeTo != null) {
							hql.append(" and u.createTime < :createTimeTo");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
								
						if(id != null && id != 0){
							query.setParameter("id", id);
						}
						if (mct != null && mct.getValue() != MulticonfirmConfigType.ALL.getValue()) {
							query.setParameter("mct", mct);
						}
						if(createTimeFrom != null) {
							query.setParameter("createTimeFrom", createTimeFrom);
						}
						if(createTimeTo != null) {
							query.setParameter("createTimeTo", createTimeTo);
						}
						
						if(pageBean.isPageFlag()){
							int totalCount = ((Long)query.iterate().next()).intValue();
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
				});
	}

}
