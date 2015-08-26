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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ecad.captacao.model.Document;
import org.ecad.captacao.model.Seed;

@Stateless
public class CrawlerService extends AbstractService {

	@Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
 
	@Resource(name="crawlerQueue")
	private String crawlerQueue;
	
	private Connection connection;
    private Session session;
    
    private MessageProducer crawlerQueueProducer;
    private Destination crawlerQueueDestination;
    
    @Inject
    private CrawlerExecutionControllerService executionService;
    
    @PostConstruct
    private void init() {
        try {
        	Context ctx = new InitialContext();
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            crawlerQueueDestination = (Destination) ctx.lookup(crawlerQueue);
            crawlerQueueProducer = session.createProducer(crawlerQueueDestination);
        } catch (JMSException | NamingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
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
    
    public void add(Seed seed) {
    	if(!executionService.canVisitSeed(seed)) {
    		return;
    	}
    	
        ObjectMessage message;
        try {
        	executionService.add(seed);
        	
            message = session.createObjectMessage(seed);
            crawlerQueueProducer.send(message);
        } catch (JMSException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
	public void remove(Seed seed) {
    	executionService.remove(seed);
    }
    
    public Boolean running() {
    	return executionService.hasSeeds() && executionService.hasRunningThreads();
    }
    
    public void addDocument(String documentUrl) {
    	executionService.add(documentUrl);
    }
    
    public void finish(String key, String managerUrl) {
    	if(running()) {
    		return;
    	}
    	
    	persistDocuments(key, managerUrl, executionService.getDocuments());
    	
    	executionService.finish();
    }
    
    private void persistDocuments(String key, String managerUrl, List<Document> documentsToNormalize) {
		if(documentsToNormalize.isEmpty()) {
			return;
		}
		
		Long time = System.currentTimeMillis();
		Integer status = 200;

		Client client = ClientBuilder.newClient();
		
		try {
			Response response = client.target(managerUrl).request().accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, key).header("Robot", "yes").post(Entity.entity(gson.toJson(documentsToNormalize), MediaType.APPLICATION_JSON_TYPE));
			status = response.getStatus();
		} catch (Exception e) {
			status = 500;
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		time = System.currentTimeMillis() - time;

		if(status != 200) {
			logger.info(String.format("Persisting documents status[%s] >> %sms", status, time));
		}
	}
}