package org.ecad.captacao.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ecad.captacao.model.Document;

import com.google.gson.Gson;

@Singleton
@Startup
public class NormalizerService extends AbstractService {

	@Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
 
	@Resource(name="normalizerQueue")
	private String normalizerQueue;
	
	private Connection connection;
    private Session session;
    
    private MessageProducer normalizerQueueProducer;
    private Destination normalizerQueueDestination;
    
    @Inject
    private NormalizerExecutionControllerService executionService;
    
    @PostConstruct
    private void init() {
        try {
        	Context ctx = new InitialContext();
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            normalizerQueueDestination = (Destination) ctx.lookup(normalizerQueue);
            normalizerQueueProducer = session.createProducer(normalizerQueueDestination);
        } catch (JMSException | NamingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    @PreDestroy
    private void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void addThread(Long id) {
    	executionService.addThread(id);
    }
    
    public void removeThread(Long id) {
    	executionService.removeThread(id);
    }
    
    public void add(Document document) {
    	if(!executionService.canNormalize(document)) {
    		return;
    	}
    	
        ObjectMessage message;
        try {
        	executionService.add(document);
        	
            message = session.createObjectMessage(document);
            normalizerQueueProducer.send(message);
        } catch (JMSException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public void remove(Document document) {
    	executionService.remove(document);
    }
    
    public Boolean running() {
    	return executionService.hasDocuments() && executionService.hasRunningThreads();
    }
    
    public void finish(String key, String managerUrl) {
    	if(running()) {
    		return;
    	}
    	
    	persistDocuments(key, managerUrl, executionService.getDocuments());
    	
    	executionService.finish();
    }
    
    private void persistDocuments(String key, String managerUrl, List<Document> normalizedDocuments) {
    	if(normalizedDocuments.isEmpty()) {
    		return;
    	}
    	
    	for(Document document : normalizedDocuments) {
    		document.setDelay(null);
    		document.setKey(null);
    		document.setConnectionTimeout(null);
    		document.setManagerUrl(null);
    		document.setFields(null);
    	}
    	
    	Long time = System.currentTimeMillis();
		Integer status = 200;
		Gson gson = new Gson();
		Client client = ClientBuilder.newClient();
		
		try {
			Response response = client.target(managerUrl).request().put(Entity.entity(gson.toJson(normalizedDocuments), MediaType.APPLICATION_JSON_TYPE));
			status = response.getStatus();
		} catch (Exception e) {
			status = 500;
			logger.error(e.getMessage(), e);
		}
		
		time = System.currentTimeMillis() - time;

		if(status != 200) {
			logger.info(String.format("Persisting documents status[%s] >> %sms", status, time));
		}
	}
}