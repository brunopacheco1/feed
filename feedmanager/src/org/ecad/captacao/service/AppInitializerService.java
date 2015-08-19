package org.ecad.captacao.service;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.ecad.captacao.dao.AppTokenDAO;
import org.ecad.captacao.exception.EntityExistsException;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.locator.ServiceLocator;
import org.ecad.captacao.persistence.AppToken;
import org.ecad.captacao.persistence.AppUser;
import org.ecad.captacao.persistence.AppUserGroup;
import org.ecad.captacao.persistence.AppUserType;

@Singleton
@Startup
public class AppInitializerService extends AbstractService {

	@Inject
	private AppUserService userService;
	
	@Inject
	private AppUserGroupService groupService;
	
	@Inject
	private AppTokenDAO tokenDAO;
	
	@Resource(name="app.context")
	private String appContext;
	
	@PostConstruct
	private void initializeApp() throws GenericException {
		ServiceLocator.getInstance().setAppContext(appContext);
		
		createDefaultUser();
	}
	
	private void createDefaultUser() throws GenericException {
		String login = "admin";
		String groupName = "admin";
		
		AppUser appUser = null;
		
		AppUserGroup group = null;
		
		if(groupService.existsByName(null, groupName)) {
			group = groupService.findByName(groupName);
		} else {
			group = new AppUserGroup();
			group.setName(groupName);
			
			groupService.add(group);
		}
		
		if(userService.existsByLogin(null, login)) {
			appUser = userService.findByLogin(login);
		} else if(appUser == null) {
			appUser = new AppUser();
			appUser.setLogin(login);
			appUser.setName(login);
			appUser.setPassword(login);
			appUser.setUserType(AppUserType.SYSTEM);
			appUser.setUserGroup(group);
			
			try {
				appUser = userService.add(appUser);
			} catch (EntityExistsException e) {
				//Ignorar quando existir
			} catch (GenericException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		String tokenStr = "b1f650401989debdf1bd9455b9fdbada";
		
		if(tokenDAO.exists(tokenStr)) {
			return;
		}
		
		AppToken token = new AppToken();

		token.setGenerateDate(new Date());
		token.setAppUser(appUser);
		token.setDuration(null);
		token.setToken(tokenStr);
		
		tokenDAO.add(token);
	}
}