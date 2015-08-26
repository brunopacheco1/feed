package org.ecad.captacao.job;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.locator.ServiceLocator;
import org.ecad.captacao.service.AppTokenService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AppTokenChecker implements Job {
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());

	private AppTokenService tokenService = ServiceLocator.getInstance().lookup(AppTokenService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(tokenService == null) {
			return;
		}
		
		try {
			tokenService.validateTokens();
		} catch (GenericException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}