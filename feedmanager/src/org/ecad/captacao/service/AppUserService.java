package org.ecad.captacao.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.ecad.captacao.dao.AppUserDAO;
import org.ecad.captacao.exception.EntityExistsException;
import org.ecad.captacao.exception.EntityNotFoundException;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.exception.InvalidAccessException;
import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.AppToken;
import org.ecad.captacao.persistence.AppUser;
import org.ecad.captacao.persistence.AppUserGroup;

@Stateless
public class AppUserService extends AbstractService {

	@Inject
	private AppUserDAO userDAO;
	
	@Inject
	private AppUserGroupService groupService;

	@Inject
	private AppTokenService tokenService;

	public AppUser add(AppUser user) throws GenericException {
		user.setId(null);

		validateUser(user);
		
		AppUserGroup group = groupService.get(user.getUserGroup().getId());
		
		user.setPassword(hashPassword(user.getPassword()));
		user.setUserGroup(group);
		
		userDAO.add(user);
		
		return user;
	}

	public AppUser update(Long id, AppUser user) throws GenericException {
		user.setId(id);

		validateUser(user);

		AppUserGroup group = groupService.get(user.getUserGroup().getId());
		
		user.setUserGroup(group);
		
		userDAO.update(user);

		return user;
	}

	public AppUser get(Long id) throws GenericException {
		return userDAO.findById(id);
	}

	public List<AppUser> list(Integer start, Integer limit, String order, String dir) throws GenericException {
		return userDAO.list(start, limit, order, dir);
	}

	public Long getCount() {
		return userDAO.getCount();
	}

	public void remove(Long id) throws GenericException {
		AppUser user = userDAO.findById(id);
		userDAO.remove(user);
	}

	public AppToken login(AppUser userLogin) throws GenericException {
		if (userLogin == null || userLogin.getLogin() == null || userLogin.getPassword() == null) {
			throw new MandatoryFieldsException("login e password são campos obrigatórios");
		}

		AppUser user = userDAO.findByLogin(userLogin.getLogin());

		if (user == null) {
			throw new EntityNotFoundException(userDAO.getEntityName() + " não encontrado");
		}

		if (!hashPassword(userLogin.getPassword()).equals(user.getPassword())) {
			throw new InvalidAccessException("Senha informada não confere");
		}

		return tokenService.generateToken(user);
	}

	public void validateUser(AppUser user) throws GenericException {
		if (user == null || (user.getId() != null && !userDAO.exists(user.getId()))) {
			throw new EntityNotFoundException(userDAO.getEntityName() + " não encontrado");
		}

		if (user.getUserGroup() == null || user.getUserGroup().getId() == null || user.getLogin() == null || user.getName() == null || user.getPassword() == null || user.getUserType() == null) {
			throw new MandatoryFieldsException("userGroup.id, login, name, password e userType são campos obrigatórios");
		}

		if (userDAO.existsByLogin(user.getId(), user.getLogin())) {
			throw new EntityExistsException("Já existe um " + userDAO.getEntityName() + " cadastrado com esse login");
		}
	}

	private String hashPassword(String password) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
		
		if(md == null || password == null) {
			return password;
		}

		md.update(password.getBytes());

		byte[] mdbytes = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			String hex = Integer.toHexString(0xff & mdbytes[i]);
			
			if (hex.length() == 1) {
				hexString.append('0');
			}
			
			hexString.append(hex);
		}
		
		return hexString.toString();
	}

	public Boolean existsByLogin(Long id, String login) throws GenericException {
		return userDAO.existsByLogin(id, login);
	}

	public AppUser findByLogin(String login) throws GenericException {
		return userDAO.findByLogin(login);
	}
}