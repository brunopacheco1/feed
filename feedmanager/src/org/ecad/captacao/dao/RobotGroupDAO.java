package org.ecad.captacao.dao;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.RobotGroup;

@Stateless
public class RobotGroupDAO extends AbstractDAO<Long, RobotGroup> {
			
	public String getEntityName() {
		return "Grupo de Robôs";
	}
	
	public Boolean existsByName(Long id, String name) throws MandatoryFieldsException {
		if(name == null) {
			throw new MandatoryFieldsException("name é obrigatório");
		}
		
		String hql = "select count(g) from RobotGroup g where g.name = :name";
		
		if(id != null) {
			hql += " and g.id != :id";
		}
		
		TypedQuery<Long> query = manager.createQuery(hql, Long.class);
		
		query.setParameter("name", name);
		
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
		
		return orderOptions;
	}
}