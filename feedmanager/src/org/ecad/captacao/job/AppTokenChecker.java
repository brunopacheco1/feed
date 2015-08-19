package org.ecad.captacao.job;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.locator.ServiceLocator;
import org.ecad.captacao.service.AppTokenService;
import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AppTokenChecker implements Job {
	
	protected Logger logger = Logger.getLogger(this.getClass());

	private AppTokenService tokenService = ServiceLocator.getInstance().lookup(AppTokenService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(tokenService == null) {
			return;
		}
		
		try {
			tokenService.validateTokens();
		} catch (GenericException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
