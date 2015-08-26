package org.ecad.captacao.service;

import java.util.List;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.exception.NothingToDoException;
import org.ecad.captacao.persistence.Document;
import org.ecad.captacao.persistence.Robot;

@Stateless
public class NotNormalizedDocumentsToPersistQueueService extends AbstractService {
	
	@Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
 
	@Resource(name="documentToNormalizeQueue")
	private String documentToNormalizeQueue;
	
	@Inject
	private NotNormalizedDocumentsToPersistQueueControllerService executionService;
	
	@Inject
	private FeedManagerRobotService robotService;
	
	private Connection connection;
    private Session session;
    private MessageProducer producer;
    
    @PostConstruct
    public void init() {
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Context ctx = new InitialContext();
            Destination destination = (Destination) ctx.lookup(documentToNormalizeQueue);
            producer = session.createProducer(destination);
        } catch (JMSException | NamingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    @PreDestroy
    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
            	logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
    
    public void add(Robot robot, List<Document> documents) {
    	for(Document document : documents) {
			add(robot, document);
		}
	}
    
	public void add(Robot robot, Document document) {
		document.setRobot(robot);
		
		add(document);
	}
	
	public void add(Document document) {
		ObjectMessage message;
        try {
        	executionService.add(document);
        	
            message = session.createObjectMessage(document);
            producer.send(message);
        } catch (JMSException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
	}
	
	public void remove(Document document) {
		executionService.remove(document);
	}
	
	public void finish(Long robotId) {
    	if (executionService.hasNotNormalizedDocumentsToPersist()) {
			return;
		}
    	
    	try {
			robotService.runNormalizer(robotId);
		} catch (NothingToDoException e) {
			//Ignorar mensagens de nada a ser feito
		} catch (GenericException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
    	
    	executionService.finish();
    }
}