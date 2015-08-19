package org.ecad.captacao.dao;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.AppUserGroup;

@Stateless
public class AppUserGroupDAO extends AbstractDAO<Long, AppUserGroup> {
	
	public String getEntityName() {
		return "Grupo de Usuários do Sistema";
	}

	public Boolean existsByName(Long id, String name) throws MandatoryFieldsException {
		if(name == null) {
			throw new MandatoryFieldsException("name é obrigatório");
		}
		
		String hql = "select count(g) from AppUserGroup g where g.name = :name";
		
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
	
	public AppUserGroup findByName(String name) throws MandatoryFieldsException {
		if(name == null) {
			throw new MandatoryFieldsException("name é obrigatório");
		}
		
		return manager.createQuery("select g from AppUserGroup g where g.name = :name", AppUserGroup.class).setParameter("name", name).getSingleResult();
	}
	
	@Override
	public Set<String> orderOptions() {
		Set<String> orderOptions = new HashSet<>();
		
		orderOptions.add("id");
		orderOptions.add("name");
		
		return orderOptions;
	}
}