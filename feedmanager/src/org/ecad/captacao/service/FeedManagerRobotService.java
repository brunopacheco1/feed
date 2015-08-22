package org.ecad.captacao.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ecad.captacao.dao.RobotDAO;
import org.ecad.captacao.exception.EntityExistsException;
import org.ecad.captacao.exception.EntityNotFoundException;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.exception.NothingToDoException;
import org.ecad.captacao.persistence.AppToken;
import org.ecad.captacao.persistence.Document;
import org.ecad.captacao.persistence.NormalizationStatus;
import org.ecad.captacao.persistence.Robot;
import org.ecad.captacao.persistence.RobotGroup;

@Stateless
public class FeedManagerRobotService extends AbstractService {

	@Inject
	private RobotDAO robotDAO;
	
	@Inject
	private RobotGroupService groupService;
	
	@Inject
	private NotNormalizedDocumentsToPersistQueueService documentsToNormalizeService;
	
	@Inject
	private NormalizedDocumentsToPersistQueueService normalizedDocumentsService;
	
	@Inject
	private DocumentService documentService;
	
	@Inject
	private AppTokenService tokenService;
	
	@Resource(name="app.address")
	private String appAddress;

	public Robot add(Robot robot) throws GenericException {
		robot.setId(null);
		
		validateRobot(robot);
		
		RobotGroup group = groupService.get(robot.getRobotGroup().getId());

		robot.setRobotGroup(group);

		robotDAO.add(robot);
		
		return robot;
	}

	public Robot update(Long id, Robot robot) throws GenericException {
		robot.setId(id);
		
		validateRobot(robot);
		
		RobotGroup group = groupService.get(robot.getRobotGroup().getId());

		robot.setRobotGroup(group);

		robotDAO.update(robot);
		
		return robot;
	}

	public Robot get(Long id) throws GenericException {
		return robotDAO.findById(id);
	}

	public List<Robot> list(Integer start, Integer limit, String order, String dir) throws GenericException {
		return robotDAO.list(start, limit, order, dir);
	}
	
	public Long getCount() {
		return robotDAO.getCount();
	}

	public void remove(Long id) throws GenericException {
		Robot robot = robotDAO.findById(id);
		robotDAO.remove(robot);
	}

	public String runCrawler(Long id) throws GenericException {
		Robot robot = robotDAO.findById(id);
		return runCrawler(robot);
	}
	
	public String runNormalizer(Long id) throws GenericException {
		Robot robot = robotDAO.findById(id);
		return runNormalizer(robot);
	}
	
	public String runNormalizer(Robot robot) throws GenericException {
		if(robot == null || !robotDAO.exists(robot.getId())) {
			throw new EntityNotFoundException(robotDAO.getEntityName() + " não encontrado");
		}
		
		if(documentService.getCount(robot, NormalizationStatus.NOT_NORMALIZED) == 0) {
			throw new NothingToDoException("Não existem documentos para normalizar");
		}
		
		List<Document> documentsToNormalize = documentService.list(robot, NormalizationStatus.NOT_NORMALIZED, 0, 100, "id", "asc");
		
		AppToken token = tokenService.generateToken(robot);
		
		Map<String, Object> data = new HashMap<>();
		
		data.put("documents", documentsToNormalize);
		data.put("delay", robot.getDelay());
		data.put("key", token.getToken());
		data.put("connectionTimeout", robot.getConnectionTimeout());
		data.put("managerUrl", appAddress + "/robot/" + robot.getId() + "/documents");
		data.put("fields", robot.getFields());
		
		Client client = ClientBuilder.newClient();
		String responseStr = null;
		
		try {
			Response response = client.target(robot.getRobotUrl() + "/robot/normalizer/run").request().post(Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON_TYPE));
			responseStr = response.readEntity(String.class);
		} catch (Exception e) {
			responseStr = e.getMessage();
			logger.error(e.getMessage(), e);
		}
		
		return responseStr;
	}

	public String runCrawler(Robot robot) throws GenericException {
		if(robot == null || !robotDAO.exists(robot.getId())) {
			throw new EntityNotFoundException(robotDAO.getEntityName() + " não encontrado");
		}
		
		AppToken token = tokenService.generateToken(robot);
		
		Map<String, Object> data = new HashMap<>();
		data.put("managerUrl", appAddress + "/robot/" + robot.getId() + "/documents");
		data.put("seedUrl", robot.getSeedUrl());
		data.put("documentRegex", robot.getDocumentRegex());
		data.put("key", token.getToken());
		data.put("endDepth", robot.getEndDepth());
		data.put("seedRegex", robot.getSeedRegex());
		data.put("connectionTimeout", robot.getConnectionTimeout());
		data.put("delay", robot.getDelay());
		
		Client client = ClientBuilder.newClient();
		String responseStr = null;
		
		try {
			Response response = client.target(robot.getRobotUrl() + "/robot/crawler/run").request().post(Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON_TYPE));
			responseStr = response.readEntity(String.class);
		} catch (Exception e) {
			responseStr = e.getMessage();
			logger.error(e.getMessage(), e);
		}

		return responseStr;
	}


	public String status(Long id) throws GenericException {
		Robot robot = robotDAO.findById(id);
		return status(robot);
	}
	
	public String status(Robot robot) throws GenericException {
		if(robot == null || !robotDAO.exists(robot.getId())) {
			throw new EntityNotFoundException(robotDAO.getEntityName() + " não encontrado");
		}
		
		Client client = ClientBuilder.newClient();
		String responseStr = null;
		
		try {
			Response response = client.target(robot.getRobotUrl() + "/robot/status").request().get();
			responseStr = response.readEntity(String.class);
		} catch (Exception e) {
			responseStr = e.getMessage();
			logger.error(e.getMessage(), e);
		}
		
		return responseStr;
	}
	
	public List<Document> listDocuments(Long id, NormalizationStatus status, Integer start, Integer limit, String order, String dir) throws GenericException {
		Robot robot = robotDAO.findById(id);
		
		return documentService.list(robot, status, start, limit, order, dir);
	}
	
	public Long getCountDocuments(Long id, NormalizationStatus status) throws GenericException {
		Robot robot = robotDAO.findById(id);
		
		return documentService.getCount(robot, status);
	}
	
	public void addDocuments(Long id, List<Document> documents) throws GenericException {
		Robot robot = robotDAO.findById(id);
		
		documentsToNormalizeService.add(robot, documents);
	}
	
	public void updateDocuments(Long id, List<Document> documents) throws GenericException {
		Robot robot = robotDAO.findById(id);
		
		normalizedDocumentsService.add(robot, documents);
	}
	
	public void validateRobot(Robot robot) throws GenericException {
		if(robot == null || (robot.getId() != null && !robotDAO.exists(robot.getId()))) {
			throw new EntityNotFoundException(robotDAO.getEntityName() + " não encontrado");
		}
		
		if(robotDAO.existsByUrl(robot.getId(), robot.getRobotUrl())) {
			throw new EntityExistsException("Já existe um " + robotDAO.getEntityName() + " com esse url");
		}
		
		if(robot.getRobotGroup() == null || robot.getRobotGroup().getId() == null || robot.getName() == null || robot.getRobotUrl() == null || robot.getSeedUrl() == null || robot.getDocumentRegex() == null || robot.getSeedRegex() == null) {
			throw new MandatoryFieldsException("robotGroup.id, name, robotUrl, seedUrl, seedRegex, documentRegex são campos obrigatórios");
		}
	}
}