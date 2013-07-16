package com.bjgl.web.dao.impl;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.dao.BaseDao;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;

/**
 * 基础CRUD实现
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午8:25
 */
public abstract class AbstractBaseDaoImpl<T> extends HibernateDaoSupport implements BaseDao<T> {

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public AbstractBaseDaoImpl() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.clazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    @Override
    public void save(T entity) {
        this.getHibernateTemplate().save(entity);
    }

    @Override
    public void update(T entity) {
        this.getHibernateTemplate().update(entity);
    }

    @Override
    public void merge(T entity) {
        this.getHibernateTemplate().merge(entity);
        this.getHibernateTemplate().flush();
    }

    @Override
    public void delete(Serializable id) {
        T instance = this.findById(id);
        if (instance != null) {
            this.getHibernateTemplate().delete(instance);
        }
    }

    @Override
    public T findById(Serializable id) {
        return this.getHibernateTemplate().get(this.clazz, id);
    }

    protected PageBean getPageBean(final Class<T> clazz, final Example example, final List<Criterion> criterionList, final PageBean pageBean) {
        return this.getHibernateTemplate().execute(new HibernateCallback<PageBean>() {
            @Override
            public PageBean doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(clazz);

                if (example != null) {
                    criteria.add(example);
                }

                if (criterionList != null && !criterionList.isEmpty()) {
                    for (Criterion criterion : criterionList) {
                        criteria.add(criterion);
                    }
                }

                criteria.setProjection(Projections.rowCount());

                int totalCount = ((Long) criteria.uniqueResult()).intValue();

                pageBean.setCount(totalCount);
                pageBean.setPageCount((int)Math.ceil((double)totalCount / pageBean.getPageSize()));

                return pageBean;
            }
        });
    }

    @Override
    public PageBean getPageBean(T example, PageBean pageBean) {
        return this.getPageBean(example, null, pageBean);
    }

    @SuppressWarnings("unchecked")
    protected List<T> findByExample(final Class<T> clazz, final Example example, final List<Criterion> criterionList, final PageBean pageBean, final Order... orders) {
        return this.getHibernateTemplate().executeFind(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(clazz);

                if (example != null) {
                    criteria.add(example);
                }

                if (criterionList != null && !criterionList.isEmpty()) {
                    for (Criterion criterion : criterionList) {
                        criteria.add(criterion);
                    }
                }

                if (orders != null) {
                    for (int i = 0; i < orders.length; i++) {
                        criteria.addOrder(orders[i]);
                    }
                }
                if (pageBean != null && pageBean.isPagging()) {
                    criteria.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize()).setMaxResults(pageBean.getPageSize());
                }
                return criteria.list();
            }
        });
    }

    @Override
    public List<T> findByExample(T example, PageBean pageBean, Order... orders) {
        return this.findByExample(example, null, pageBean, orders);
    }

    @Override
    public PageBean getPageBean(T example, List<Criterion> criterionList, PageBean pageBean) {
        return this.getPageBean(clazz, example == null ? null : Example.create(example), criterionList, pageBean);
    }

    @Override
    public List<T> findByExample(T example, List<Criterion> criterionList, PageBean pageBean, Order... orders) {
        return this.findByExample(clazz, example == null ? null : Example.create(example), criterionList, pageBean, orders);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByHQL(final String hql, final PageBean pageBean, final Object... params) {
        return this.getHibernateTemplate().executeFind(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(hql);
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        query.setParameter(i, params[i]);
                    }
                }
                if (pageBean != null && pageBean.isPagging()) {
                    query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize()).setMaxResults(pageBean.getPageSize());
                }
                return query.list();
            }
        });
    }
}
