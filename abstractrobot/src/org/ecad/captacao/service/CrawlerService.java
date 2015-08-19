package org.ecad.captacao.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
import javax.ws.rs.core.HttpHeaders;

import org.ecad.captacao.model.Document;
import org.ecad.captacao.model.Seed;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.google.gson.Gson;

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
    
    @EJB
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
            logger.error(e.getMessage(), e);
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
		
		Gson gson = new Gson();
		
		ClientRequest clientRequest = new ClientRequest(managerUrl);
		clientRequest.accept("application/json");
		
		clientRequest.header(HttpHeaders.AUTHORIZATION, key);
		clientRequest.header("Robot", "yes");
		
		clientRequest.body("application/json", gson.toJson(documentsToNormalize));
		
		ClientResponse<String> response = null;
		try {
			response = clientRequest.post(String.class);
			status = response.getStatus();
		} catch (Exception e) {
			status = 500;
			logger.error(e.getMessage(), e);
		}
		
		time = System.currentTimeMillis() - time;

		if(response == null || status != 200) {
			logger.info(String.format("Persisting documents status[%s] >> %sms", status, time));
		}
	}
}