package org.ecad.captacao.locator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ServiceLocator {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	private ServiceLocator() {
		try {
			context = new InitialContext();
		} catch (NamingException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private static ServiceLocator instance;
	
	static {
		instance = new ServiceLocator();
	}
	
	public static ServiceLocator getInstance() {
		return instance;
	}
	
	private InitialContext context;
	private String appContext;
	
	public void setAppContext(String appContext) {
		this.appContext = appContext;
	}
	
	public synchronized <T extends Object> T lookup(Class<T> type) {
		try {
			return type.cast(context.lookup("java:global/" + appContext + "/" + type.getSimpleName()));
		} catch (NamingException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return null;
	}
}