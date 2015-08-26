package org.ecad.captacao.job;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.locator.ServiceLocator;
import org.ecad.captacao.service.RobotGroupService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RobotRunner implements Job {
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());

	private RobotGroupService groupService = ServiceLocator.getInstance().lookup(RobotGroupService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(groupService == null) {
			return;
		}
		
		Long groupId = context.getJobDetail().getJobDataMap().getLong("groupId");
		
		try {
			groupService.runCrawler(groupId);
		} catch (GenericException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}