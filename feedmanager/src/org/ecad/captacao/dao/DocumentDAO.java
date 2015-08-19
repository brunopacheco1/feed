package org.ecad.captacao.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.ecad.captacao.exception.InvalidValueException;
import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.Document;
import org.ecad.captacao.persistence.NormalizationStatus;
import org.ecad.captacao.persistence.Robot;

@Stateless
public class DocumentDAO extends AbstractDAO<Long, Document> {
	
	public String getEntityName() {
		return "Documento";
	}

	public List<Document> list(Robot robot, NormalizationStatus status, Integer start, Integer limit, String order, String dir) throws MandatoryFieldsException, InvalidValueException {
		if(robot == null || status == null || start == null || limit == null || order == null || dir == null) {
			throw new MandatoryFieldsException("robot, status, start, limit, order e dir são obrigatórios");
		}
		
		if(!orderOptions().contains(order) || !dirOptions().contains(dir)) {
			throw new InvalidValueException(String.format("Possíveis valores para order[%s] e dir[%s]", StringUtils.join(orderOptions(), ", "), StringUtils.join(dirOptions(), ", ")));
		}
		
		return manager.createQuery("select d from Document d where d.robot = :robot and d.status = :status order by d." + order + " " + dir, Document.class).setParameter("robot", robot).setParameter("status", status).setFirstResult(start).setMaxResults(limit).getResultList();
	}
	
	public Long getCount(Robot robot, NormalizationStatus status) throws MandatoryFieldsException {
		if(robot == null || status == null) {
			throw new MandatoryFieldsException("robot e status são obrigatórios");
		}
		
		return manager.createQuery("select count(d) from Document d where d.robot = :robot and d.status = :status", Long.class).setParameter("robot", robot).setParameter("status", status).getSingleResult();
	}
	
	public List<Document> list(NormalizationStatus status, Integer start, Integer limit, String order, String dir) throws MandatoryFieldsException, InvalidValueException {
		if(status == null || start == null || limit == null || order == null || dir == null) {
			throw new MandatoryFieldsException("robot, status, start, limit, order e dir são obrigatórios");
		}
		
		if(!orderOptions().contains(order) || !dirOptions().contains(dir)) {
			throw new InvalidValueException(String.format("Possíveis valores para order[%s] e dir[%s]", StringUtils.join(orderOptions(), ", "), StringUtils.join(dirOptions(), ", ")));
		}
		
		return manager.createQuery("select d from Document d order by d." + order + " " + dir, Document.class).setFirstResult(start).setMaxResults(limit).getResultList();
	}
	
	public Long getCount(NormalizationStatus status) throws MandatoryFieldsException {
		if(status == null) {
			throw new MandatoryFieldsException("status é obrigatório");
		}
		
		return manager.createQuery("select count(d) from Document d", Long.class).getSingleResult();
	}
	
	public Boolean existsByUrl(Long id, String url) throws MandatoryFieldsException {
		if(url == null) {
			throw new MandatoryFieldsException("url é obrigatório");
		}
		
		String hql = "select count(d) from Document d where d.url = :url";
		
		if(id != null) {
			hql += " and d.id != :id";
		}
		
		TypedQuery<Long> query = manager.createQuery(hql, Long.class);
		
		query.setParameter("url", url);
		
		if(id != null) {
			query.setParameter("id", id);
		}
		
		Long result = query.getSingleResult();
		
		return result > 0;
	}
	
	@Override
	public Set<String> orderOptions() {
		Set<String> orderOptions = new HashSet<>();
		
		orderOptions.add("id");
		orderOptions.add("captureDate");
		orderOptions.add("normalizeDate");
		orderOptions.add("url");
		orderOptions.add("status");
		orderOptions.add("robot.id");
		orderOptions.add("robot.name");
		orderOptions.add("robot.robotUrl");
		orderOptions.add("robot.robotGroup.id");
		orderOptions.add("robot.robotGroup.name");
		
		return orderOptions;
	}
}