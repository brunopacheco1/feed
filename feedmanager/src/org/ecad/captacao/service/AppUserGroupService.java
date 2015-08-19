package org.ecad.captacao.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.ecad.captacao.dao.AppUserGroupDAO;
import org.ecad.captacao.exception.EntityExistsException;
import org.ecad.captacao.exception.EntityNotFoundException;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.AppUserGroup;

@Stateless
public class AppUserGroupService extends AbstractService {

	@Inject
	private AppUserGroupDAO groupDAO;
	
	
	public AppUserGroup add(AppUserGroup group) throws GenericException {
		group.setId(null);
		
		validateGroup(group);
		
		groupDAO.add(group);
		
		return group;
	}
	
	public AppUserGroup update(Long id, AppUserGroup group) throws GenericException {
		group.setId(id);
		
		validateGroup(group);
		
		groupDAO.update(group);
		
		return group;
	}
	
	public AppUserGroup get(Long id) throws GenericException {
		return groupDAO.findById(id);
	}
	
	public List<AppUserGroup> list(Integer start, Integer limit, String order, String dir) throws GenericException {
		return groupDAO.list(start, limit, order, dir);
	}
	
	public Long getCount() {
		return groupDAO.getCount();
	}
	
	public void remove(Long id) throws GenericException {
		AppUserGroup group = groupDAO.findById(id);
		groupDAO.remove(group);
	}
	
	public void validateGroup(AppUserGroup group) throws GenericException {
		if(group == null || (group.getId() != null && !groupDAO.exists(group.getId()))) {
			throw new EntityNotFoundException(groupDAO.getEntityName() + " não encontrado");
		}
		
		if(group.getName() == null) {
			throw new MandatoryFieldsException("name é um campo obrigatório");
		}
		
		if(groupDAO.existsByName(group.getId(), group.getName())) {
			throw new EntityExistsException("Já existe um " + groupDAO.getEntityName() + " cadastrado com esse nome");
		}
	}

	public boolean existsByName(Long id, String name) throws GenericException {
		return groupDAO.existsByName(id, name);
	}

	public AppUserGroup findByName(String groupName) throws GenericException {
		return groupDAO.findByName(groupName);
	}
}