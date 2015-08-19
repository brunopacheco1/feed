package org.ecad.captacao.dao;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.Robot;

@Stateless
public class RobotDAO extends AbstractDAO<Long, Robot> {
	
	public String getEntityName() {
		return "Robô";
	}
	
	public Boolean existsByUrl(Long id, String robotUrl) throws MandatoryFieldsException {
		if(robotUrl == null) {
			throw new MandatoryFieldsException("robotUrl é obrigatório");
		}
		
		String hql = "select count(r) from Robot r where r.robotUrl = :robotUrl";
		
		if(id != null) {
			hql += " and r.id != :id";
		}
		
		TypedQuery<Long> query = manager.createQuery(hql, Long.class);
		
		query.setParameter("robotUrl", robotUrl);
		
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
		orderOptions.add("name");
		orderOptions.add("robotUrl");
		orderOptions.add("robotGroup.id");
		orderOptions.add("robotGroup.name");
		
		return orderOptions;
	}
}