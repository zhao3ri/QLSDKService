package com.qinglan.sdk.server.data.infrastructure.persistence.impl;

import com.qinglan.sdk.server.data.query.Page;
import com.qinglan.sdk.server.data.infrastructure.persistence.MybatisRepository;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisRepositoryImpl extends SqlSessionDaoSupport implements MybatisRepository {
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_PAGE_SIZE = "pageSize";
    private static final String SEPARATOR = ".";

    public MybatisRepositoryImpl() {
    }

    @Override
    public <T> int save(T entity) {
        return getSqlSession().insert(entity.getClass().getName() + SEPARATOR + STATEMENT_INSERT, entity);
    }

    @Override
    public <T> int delete(Class<T> entityClass, Serializable id) {
        return delete(entityClass, STATEMENT_DELETE, id);
    }

    @Override
    public <T> int delete(Class<T> entityClass, String statement, Object params) {
        return getSqlSession().delete(entityClass.getName() + SEPARATOR + statement, params);
    }

    @Override
    public <T> int deleteList(Class<T> entityClass, List<? extends Serializable> list) {
        return getSqlSession().delete(entityClass.getName() + SEPARATOR + STATEMENT_DELETE_LIST, list);
    }

    @Override
    public <T> int update(T entity) {
        return getSqlSession().update(entity.getClass().getName() + SEPARATOR + STATEMENT_UPDATE, entity);
    }

    @Override
    public <T> int update(Class<T> cls, Object params) {
        return update(cls, STATEMENT_UPDATE, params);
    }

    @Override
    public <T> int update(Class<T> entityClass, String statement, Object params) {
        return getSqlSession().update(entityClass.getName() + SEPARATOR + statement, params);
    }

    @Override
    public <T> T findById(Class<T> entityClass, Serializable id) {
        return getSqlSession().selectOne(entityClass.getName() + SEPARATOR + STATEMENT_FIND_ID, id);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return getSqlSession().selectList(entityClass.getName() + SEPARATOR + STATEMENT_FIND_ALL);
    }

    @Override
    public <E, T> Page<E> findPage(Class<T> entityClass, int pageNo, int pageSize) {
        Page<E> page = new Page(pageSize, pageNo);
        Map<String, Object> params = new HashMap();
        params.put(PARAM_OFFSET, page.getFirstResult());
        params.put(PARAM_PAGE_SIZE, page.getPageSize());
        List<E> pageList = getSqlSession().selectList(entityClass.getName() + SEPARATOR + STATEMENT_FIND_PAGE, params);
        long total = (Long) getSqlSession().selectOne(entityClass.getName() + SEPARATOR + STATEMENT_FIND_TOTAL);
        page.setResult(pageList);
        page.setTotalCount(total);
        return page;
    }

    @Override
    public <E, T> Page<E> findPage(Class<T> entityClass, String statement, Map<String, Object> params, int pageNo, int pageSize) {
        Page<E> page = new Page(pageSize, pageNo);
        params.put(PARAM_OFFSET, page.getFirstResult());
        params.put(PARAM_PAGE_SIZE, page.getPageSize());
        List<E> pageList = getSqlSession().selectList(entityClass.getName() + SEPARATOR + statement, params);
        long total = (Long) getSqlSession().selectOne(entityClass.getName() + SEPARATOR + statement + "Total", params);
        page.setResult(pageList);
        page.setTotalCount(total);
        return page;
    }

    @Override
    public <E, T> List<E> findList(Class<T> entityClass, String statement, Object params) {
        return getSqlSession().selectList(entityClass.getName() + SEPARATOR + statement, params);
    }

    @Override
    public <E, T> E findOne(Class<T> entityClass, String statement, Object params) {
        return getSqlSession().selectOne(entityClass.getName() + SEPARATOR + statement, params);
    }

    @Override
    public <E, T> E findOne(Class<T> cls, Object params) {
        return findOne(cls, STATEMENT_FIND_ONE, params);
    }

    @Override
    public <T> int isExist(Class<T> entityClass, String statement, Object params) {
        return (Integer) getSqlSession().selectOne(entityClass.getName() + SEPARATOR + statement, params);
    }

    @Override
    public <T> int insert(Class<T> entityClass, String statement, Object params) {
        return getSqlSession().insert(entityClass.getName() + SEPARATOR + statement, params);
    }

    @Override
    public <T> int insert(Class<T> cls, Object params) {
        return insert(cls, STATEMENT_INSERT, params);
    }
}