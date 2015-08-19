package org.ecad.captacao.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.ecad.captacao.dao.RobotGroupDAO;
import org.ecad.captacao.exception.EntityExistsException;
import org.ecad.captacao.exception.EntityNotFoundException;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.exception.InvalidValueException;
import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.Robot;
import org.ecad.captacao.persistence.RobotGroup;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;

@Stateless
public class RobotGroupService extends AbstractService {

	@Inject
	private RobotGroupDAO groupDAO;
	
	@Inject
	private FeedManagerRobotService robotService;
	
	@Inject
	private SchedulerService schedulerService;
	
	public RobotGroup add(RobotGroup group) throws GenericException, SchedulerException {
		group.setId(null);
		
		validateGroup(group);
		
		groupDAO.add(group);
		
		schedulerService.schedule(group);
		
		return group;
	}
	
	public RobotGroup update(Long id, RobotGroup group) throws GenericException, SchedulerException {
		group.setId(id);
		
		validateGroup(group);
		
		groupDAO.update(group);
		
		schedulerService.schedule(group);
		
		return group;
	}
	
	public RobotGroup get(Long id) throws GenericException {
		return groupDAO.findById(id);
	}
	
	public List<RobotGroup> list(Integer start, Integer limit, String order, String dir) throws GenericException {
		return groupDAO.list(start, limit, order, dir);
	}
	
	public Long getCount() {
		return groupDAO.getCount();
	}
	
	public void remove(Long id) throws GenericException {
		RobotGroup group = groupDAO.findById(id);
		groupDAO.remove(group);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> runNormalizer(Long id) throws GenericException {
		RobotGroup group = groupDAO.findById(id);

		List<Map<String, Object>> msgs = new ArrayList<>();

		for(Robot robot : group.getRobots()) {
			Map<String, Object> json = null;
			try {
				String jsonStr = robotService.runNormalizer(robot);
				
				json = gson.fromJson(jsonStr, HashMap.class);
			} catch (GenericException e) {
				json = new HashMap<>();
				json.put("message", e.getMessage());
			}

			json.put("robotId", robot.getId());
			
			msgs.add(json);
		}
		
		return msgs;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> runCrawler(Long id) throws GenericException {
		RobotGroup group = groupDAO.findById(id);

		List<Map<String, Object>> msgs = new ArrayList<>();

		for(Robot robot : group.getRobots()) {
			Map<String, Object> json = null;
			
			try {
				String jsonStr = robotService.runCrawler(robot);
				
				json = gson.fromJson(jsonStr, HashMap.class);
			} catch (GenericException e) {
				json = new HashMap<>();
				json.put("message", e.getMessage());
			}

			json.put("robotId", robot.getId());
			
			msgs.add(json);
		}
		
		return msgs;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> status(Long id) throws GenericException {
		RobotGroup group = groupDAO.findById(id);

		List<Map<String, Object>> msgs = new ArrayList<>();
		
		for(Robot robot : group.getRobots()) {
			String jsonStr = robotService.status(robot);

			Map<String, Object> json = gson.fromJson(jsonStr, HashMap.class);
			
			json.put("robotId", robot.getId());
			
			msgs.add(json);
		}
		
		return msgs;
	}
	
	public void validateGroup(RobotGroup group) throws GenericException {
		if(group == null || (group.getId() != null && !groupDAO.exists(group.getId()))) {
			throw new EntityNotFoundException("Grupo de Robôs não encontrado");
		}
		
		if(group.getCronPattern() == null || group.getName() == null) {
			throw new MandatoryFieldsException("cronPattern e name são campos obrigatórios");
		}
		
		if(groupDAO.existsByName(group.getId(), group.getName())) {
			throw new EntityExistsException("Já existe um " + groupDAO.getEntityName() + " cadastrado com esse nome");
		}
		
		if(!CronExpression.isValidExpression(group.getCronPattern())) {
			throw new InvalidValueException("A expressão CRON está num formato inválido");
		}
	}
}