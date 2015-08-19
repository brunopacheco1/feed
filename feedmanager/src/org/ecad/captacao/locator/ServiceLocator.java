package org.ecad.captacao.locator;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;

public class ServiceLocator {

	protected Logger logger = Logger.getLogger(this.getClass());
	
	private ServiceLocator() {
		try {
			context = new InitialContext();
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
}