package org.ecad.captacao.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.ecad.captacao.exception.EntityNotFoundException;
import org.ecad.captacao.exception.InvalidValueException;
import org.ecad.captacao.exception.MandatoryFieldsException;

public abstract class AbstractDAO<PK, T> {

	@PersistenceContext
	protected EntityManager manager;
	
	private Class<T> type;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        type = (Class<T>) pt.getActualTypeArguments()[1];
	}
	
	public abstract String getEntityName();

	public T findById(PK id) throws EntityNotFoundException, MandatoryFieldsException {
		if(!exists(id)) {
			throw new EntityNotFoundException(getEntityName() + " não encontrado");
		}
		
		T result = manager.find(type, id);
		
		return result;
	}

	public void remove(T entity) throws EntityNotFoundException {
		if(entity == null) {
			throw new EntityNotFoundException(getEntityName() + " não encontrado");
		}
		
		manager.remove(entity);
	}

	public void add(T entity) throws EntityNotFoundException {
		if(entity == null) {
			throw new EntityNotFoundException(getEntityName() + " não encontrado");
		}
		
		manager.persist(entity);
	}
	
	public void update(T entity) throws EntityNotFoundException {
		if(entity == null) {
			throw new EntityNotFoundException(getEntityName() + " não encontrado");
		}
		
		manager.merge(entity);
	}

	public List<T> list(Integer start, Integer limit, String order, String dir) throws MandatoryFieldsException, InvalidValueException {
		if(start == null || limit == null || order == null || dir == null) {
			throw new MandatoryFieldsException("start, limit, order e dir são obrigatórios");
		}
		
		if(!orderOptions().contains(order) || !dirOptions().contains(dir)) {
			throw new InvalidValueException(String.format("Possíveis valores para order[%s] e dir[%s]", StringUtils.join(orderOptions(), ", "), StringUtils.join(dirOptions(), ", ")));
		}
		
		return manager.createQuery("select e from " + type.getSimpleName() + " e order by e." + order + " " + dir, type).setFirstResult(start).setMaxResults(limit).getResultList();
	}
	
	public List<T> list() {
		return manager.createQuery("select e from " + type.getSimpleName() + " e", type).getResultList();
	}

	public Long getCount() {
		return manager.createQuery("select count(e) from " + type.getSimpleName() + " e", Long.class).getSingleResult();
	}
	
	public Boolean exists(PK id) throws MandatoryFieldsException {
		if(id == null) {
			throw new MandatoryFieldsException("id é obrigatório");
		}
		
		Long result = manager.createQuery("select count(e) from " + type.getSimpleName() + " e where e.id = :id", Long.class).setParameter("id", id).getSingleResult();
		
		return result > 0;
	}
	
	public abstract Set<String> orderOptions();
	
	public Set<String> dirOptions() {
		Set<String> dirOptions = new HashSet<>();
		
		dirOptions.add("asc");
		dirOptions.add("desc");
		
		return dirOptions;
	}
}