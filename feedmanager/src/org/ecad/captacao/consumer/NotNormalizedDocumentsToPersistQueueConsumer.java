package org.ecad.captacao.consumer;

import java.util.Date;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.ecad.captacao.exception.EntityExistsException;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.persistence.Document;
import org.ecad.captacao.persistence.NormalizationStatus;
import org.ecad.captacao.service.DocumentService;
import org.ecad.captacao.service.NotNormalizedDocumentsToPersistQueueService;

public class NotNormalizedDocumentsToPersistQueueConsumer extends AbstractConsumer implements MessageListener {
	
	@Inject
	private DocumentService documentService;
	
	@Inject
	private NotNormalizedDocumentsToPersistQueueService service;
	
	@Override
    public void onMessage(Message message) {
        ObjectMessage objMsg = (ObjectMessage) message;
        
        Document document = null;
        try {
        	document = (Document) objMsg.getObject();
        } catch (JMSException e) {
        	logger.log(Level.SEVERE, e.getMessage(), e);
		}
        
        if(document == null) {
        	return;
        }
        
        document.setCaptureDate(new Date());
    	document.setStatus(NormalizationStatus.NOT_NORMALIZED);
    	
    	try {
			documentService.add(document);
		} catch (EntityExistsException e) {
			// Validacao de existencia, nesse caso, pode ser ignorada
        } catch (GenericException e) {
        	logger.log(Level.SEVERE, e.getMessage(), e);
		}
    	
    	service.remove(document);
    	
    	service.finish(document.getRobot().getId());
    }
}