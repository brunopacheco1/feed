package org.ecad.captacao.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.ecad.captacao.dao.MonitoringDAO;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.Monitoring;

@Stateless
public class MonitoringService extends AbstractService {

	@Inject
	private MonitoringDAO monitoringDAO;
	
	
	public Monitoring add(Monitoring monitoring) throws GenericException {
		monitoring.setId(null);
		
		validateMonitoring(monitoring);
		
		monitoringDAO.add(monitoring);
		
		return monitoring;
	}
	
	public Monitoring get(Long id) throws GenericException {
		return monitoringDAO.findById(id);
	}
	
	public List<Monitoring> list(Integer start, Integer limit, String order, String dir) throws GenericException {
		return monitoringDAO.list(start, limit, order, dir);
	}
	
	public Long getCount() {
		return monitoringDAO.getCount();
	}
	
	public void validateMonitoring(Monitoring monitoring) throws GenericException {
		if(monitoring == null) {
			throw new EntityNotFoundException(monitoringDAO.getEntityName() + " não encontrado");
		}
		
		if(monitoring.getEndDate() == null || monitoring.getExecutionTime() == null || monitoring.getIp() == null || monitoring.getMethod() == null || monitoring.getPath() == null || monitoring.getResponseStatus() == null || monitoring.getStartDate() == null) {
			throw new MandatoryFieldsException("endDate, executionTime, ip, method, path, responseStatus e startDate são campos obrigatórios");
		}
	}
}