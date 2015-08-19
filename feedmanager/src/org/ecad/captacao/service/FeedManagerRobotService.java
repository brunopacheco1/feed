package org.ecad.captacao.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;

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
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

@Stateless
public class FeedManagerRobotService extends AbstractService {

	@EJB
	private RobotDAO robotDAO;
	
	@EJB
	private RobotGroupService groupService;
	
	@EJB
	private NotNormalizedDocumentsToPersistQueueService documentsToNormalizeService;
	
	@EJB
	private NormalizedDocumentsToPersistQueueService normalizedDocumentsService;
	
	@EJB
	private DocumentService documentService;
	
	@EJB
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
		
		ClientRequest clientRequest = new ClientRequest(robot.getRobotUrl() + "/robot/normalizer/run");
		clientRequest.accept("application/json");
		
		clientRequest.body("application/json", gson.toJson(data));
		
		String responseStr = null;
		
		ClientResponse<String> response = null;
		try {
			response = clientRequest.post(String.class);
			responseStr = response.getEntity();
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

		ClientRequest clientRequest = new ClientRequest(robot.getRobotUrl() + "/robot/crawler/run");
		clientRequest.accept("application/json");
		
		clientRequest.body("application/json", gson.toJson(data));

		String responseStr = null;
		
		ClientResponse<String> response = null;
		try {
			response = clientRequest.post(String.class);
			responseStr = response.getEntity();
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
		
		ClientRequest clientRequest = new ClientRequest(robot.getRobotUrl() + "/robot/status");
		clientRequest.accept("application/json");
		
		String responseStr = null;
		
		ClientResponse<String> response = null;
		try {
			response = clientRequest.get(String.class);
			responseStr = response.getEntity();
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