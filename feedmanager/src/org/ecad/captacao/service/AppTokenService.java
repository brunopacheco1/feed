package org.ecad.captacao.service;

import java.util.Date;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.ecad.captacao.dao.AppTokenDAO;
import org.ecad.captacao.exception.EntityNotFoundException;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.exception.InvalidAccessException;
import org.ecad.captacao.persistence.AppToken;
import org.ecad.captacao.persistence.AppUser;
import org.ecad.captacao.persistence.AppUserType;
import org.ecad.captacao.persistence.Robot;
import org.joda.time.DateTime;

@Stateless
public class AppTokenService extends AbstractService {

	@Inject
	private AppTokenDAO tokenDAO;
	
	@Inject
	private AppUserService userService;
	
	@Inject
	private FeedManagerRobotService robotService;

	public AppToken generateToken(AppUser user) throws GenericException {
		userService.validateUser(user);
		
		AppToken token = new AppToken();

		token.setGenerateDate(new Date());
		token.setAppUser(user);
		token.setDuration(user.getUserType().getDuration());
		
		String tokenStr = null;
		
		while(tokenStr == null) {
			tokenStr = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
			
			if(tokenDAO.exists(tokenStr)) {
				token = null;
			}
		}
		
		token.setToken(tokenStr);
		
		tokenDAO.add(token);
		
		return token;
	}
	
	public AppToken generateToken(Robot robot) throws GenericException {
		robotService.validateRobot(robot);
		
		AppToken token = new AppToken();

		token.setGenerateDate(new Date());
		token.setRobot(robot);
		token.setDuration(120);
		String tokenStr = null;
		
		while(tokenStr == null) {
			tokenStr = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
			
			if(tokenDAO.exists(tokenStr)) {
				token = null;
			}
		}
		
		token.setToken(tokenStr);
		
		tokenDAO.add(token);
		
		return token;
	}

	public void validateToken(String tokenStr) throws GenericException {
		if (tokenStr == null || !tokenDAO.exists(tokenStr)) {
			throw new InvalidAccessException("Acesso negado, " + tokenDAO.getEntityName() + " inválido");
		}
		
		AppToken token = tokenDAO.findByToken(tokenStr);
		
		if(token == null || !token.getValidToken()) {
			throw new InvalidAccessException("Acesso negado, " + tokenDAO.getEntityName() + " inválido");
		}
	}
	
	public Boolean haveToDiscard(String tokenStr) throws GenericException {
		if (tokenStr == null || !tokenDAO.exists(tokenStr)) {
			throw new EntityNotFoundException(tokenDAO.getEntityName() + " não encontrado");
		}
		
		AppToken token = tokenDAO.findByToken(tokenStr);
		
		if(token.getAppUser() != null && token.getAppUser().getUserType().equals(AppUserType.SYSTEM)) {
			return false;
		}
		
		DateTime endDate = new DateTime(token.getGenerateDate());
		
		if(token.getDuration() != null) {
			endDate = endDate.plusMinutes(token.getDuration());
		}
		
		if(endDate.isBeforeNow()) {
			return true;
		}
		
		return false;
	}
	
	public void discardToken(String tokenStr) throws GenericException {
		if (tokenStr == null || !tokenDAO.exists(tokenStr)) {
			throw new EntityNotFoundException(tokenDAO.getEntityName() + " não encontrado");
		}
		
		AppToken token = tokenDAO.findByToken(tokenStr);
		
		if(token == null) {
			throw new EntityNotFoundException(tokenDAO.getEntityName() + " não encontrado");
		}
		
		token.setValidToken(false);
		
		tokenDAO.update(token);
	}
	
	public void validateTokens() throws GenericException {
		for(String token : tokenDAO.listTokens(true)) {
			if(haveToDiscard(token)) {
				discardToken(token);
			}
		}
	}
}