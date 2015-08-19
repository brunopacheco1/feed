package org.ecad.captacao.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.job.AppTokenChecker;
import org.ecad.captacao.job.RobotRunner;
import org.ecad.captacao.persistence.RobotGroup;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

@Singleton
@Startup
public class SchedulerService extends AbstractService {
	
	private Scheduler scheduler;
	
	@Inject
	private RobotGroupService groupService;
	
	@PostConstruct
	private void scheduleAllJobs() throws SchedulerException, GenericException {
		scheduler = new StdSchedulerFactory().getScheduler();
    	scheduler.start();
    	
    	Integer limit = 100;
    	Integer total = groupService.getCount().intValue();
    	Integer pages =  total / limit;
    	
    	if(total % limit != 0) {
    		pages++;
    	}
    	
    	for(Integer page = 0; page < pages; page++) {
	    	List<RobotGroup> groups = groupService.list(page * limit, limit, "id", "asc");
	    	
			for(RobotGroup group : groups) {
				schedule(group);
			}
    	}
    	
		scheduleTokenChecker();
	}
	
	public void schedule(RobotGroup group) throws SchedulerException, GenericException {
		groupService.validateGroup(group);
		
		JobDetail job = JobBuilder.newJob(RobotRunner.class).withIdentity(group.getName() + "-Job", group.getName() + "-Group").usingJobData("groupId", group.getId()).build();
		
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(group.getName() + "-Trigger", group.getName() + "-Group").withSchedule(CronScheduleBuilder.cronSchedule(group.getCronPattern())).build();
		
		if(scheduler.checkExists(trigger.getKey())) {
			scheduler.rescheduleJob(trigger.getKey(), trigger);
		} else {
			scheduler.scheduleJob(job, trigger);
		}
	}
	
	private void scheduleTokenChecker() throws SchedulerException {
		JobDetail job = JobBuilder.newJob(AppTokenChecker.class).withIdentity("Token-Job", "Token-Group").build();
		
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("Token-Trigger", "Token-Group").withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *")).build();
		
		if(scheduler.checkExists(trigger.getKey())) {
			scheduler.rescheduleJob(trigger.getKey(), trigger);
		} else {
			scheduler.scheduleJob(job, trigger);
		}
	}
	
	@PreDestroy
	private void finish() throws SchedulerException {
		scheduler.shutdown(true);
	}
}