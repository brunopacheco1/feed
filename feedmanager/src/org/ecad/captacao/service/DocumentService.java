package org.ecad.captacao.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.ecad.captacao.dao.DocumentDAO;
import org.ecad.captacao.exception.EntityExistsException;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.Document;
import org.ecad.captacao.persistence.NormalizationStatus;
import org.ecad.captacao.persistence.Robot;

@Stateless
public class DocumentService extends AbstractService {

	@Inject
	private DocumentDAO documentDAO;
	
	@Inject
	private FeedManagerRobotService robotService;
	
	public Boolean exists(Long id) throws GenericException {
		return documentDAO.exists(id);
	}
	
	public void add(Document document) throws GenericException {
		document.setId(null);
		
		validateDocument(document);
		
		Robot robot = robotService.get(document.getRobot().getId());
		
		document.setRobot(robot);
		
		documentDAO.add(document);
	}
	
	public void update(Long id, Document document) throws GenericException {
		document.setId(id);
		
		validateDocument(document);
		
		Robot robot = robotService.get(document.getRobot().getId());
		
		document.setRobot(robot);
		
		documentDAO.update(document);
	}
	
	public Document get(Long id) throws GenericException {
		return documentDAO.findById(id);
	}
	
	public List<Document> list(NormalizationStatus status, Integer start, Integer limit, String order, String dir) throws GenericException {
		return documentDAO.list(status, start, limit, order, dir);
	}
	
	public Long getCount(NormalizationStatus status) throws GenericException {
		return documentDAO.getCount(status);
	}
	
	public List<Document> list(Robot robot, NormalizationStatus status, Integer start, Integer limit, String order, String dir) throws GenericException {
		return documentDAO.list(robot, status, start, limit, order, dir);
	}
	
	public Long getCount(Robot robot, NormalizationStatus status) throws GenericException {
		return documentDAO.getCount(robot, status);
	}
	
	public void remove(Long id) throws GenericException {
		Document document = documentDAO.findById(id);
		documentDAO.remove(document);
	}
	
	public void validateDocument(Document document) throws GenericException {
		if(document == null || (document.getId() != null && !documentDAO.exists(document.getId()))) {
			throw new EntityNotFoundException(documentDAO.getEntityName() + " não encontrado");
		}
		
		if(documentDAO.existsByUrl(document.getId(), document.getUrl())) {
			throw new EntityExistsException("Já existe um " + documentDAO.getEntityName() + " com esse url");
		}
		
		if(document.getRobot() == null || document.getRobot().getId() == null || document.getUrl() == null || document.getStatus() == null) {
			throw new MandatoryFieldsException("robot.id, url e status são campos obrigatórios");
		}
	}
}