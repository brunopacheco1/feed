package org.ecad.captacao.robot;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.ecad.captacao.model.Document;
import org.ecad.captacao.service.NormalizerService;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Normalizer implements MessageListener {

protected Logger logger = Logger.getLogger(this.getClass());
	
	@EJB
	protected NormalizerService normalizerService;
	
	@Override
	public void onMessage(Message message) {
		normalizerService.addThread(Thread.currentThread().getId());
		
		ObjectMessage objMsg = (ObjectMessage) message;
		
		Document document = null; 
        try {
        	document = (Document) objMsg.getObject();
        } catch (JMSException e) {
            logger.error(e.getMessage(), e);
        }
        
        Long time = System.currentTimeMillis();
    	
    	runNormalizer(document);
    	
    	normalizerService.remove(document);
    	
    	normalizerService.removeThread(Thread.currentThread().getId());
    	
    	time = System.currentTimeMillis() - time;
		
		logger.info(String.format("Document[%s] normalized in %sms.", document.getUrl(), time));
    	
		if(document.getDelay() != null) {
	    	try {
				Thread.sleep(document.getDelay());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
        
        normalizerService.finish(document.getKey(), document.getManagerUrl());
	}
	
	protected void runNormalizer(Document document) {
		Connection connection = createBaseConnection(document.getUrl(), document.getConnectionTimeout());
		
		org.jsoup.nodes.Document body = null;
		
		try {
			body = connection.get();
		} catch (Exception e) {
			logger.error(String.format("Documento com problemas de acesso: %s", document.getUrl()));
			return;
		}
		
		for(String fieldKey : document.getFields().keySet()) {
			String fieldSelector = document.getFields().get(fieldKey);
			
			String fieldValue = clearText(body.select(fieldSelector).text());
			
			if(fieldValue != null && !fieldValue.equals("")) {
				document.addFieldValue(fieldKey, fieldValue);
			}
		}
	}
	
	protected String clearText(String txt) {
		if(txt == null) {
			return null;
		}
		
		return txt.replaceAll("\r", "").replaceAll("\n", "").trim();
	}
	
	protected Connection createBaseConnection(String url, Integer connectionTimeout) {
		Connection connection = Jsoup.connect(url);
		connection.followRedirects(true);
		connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		connection.header("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.6,en;q=0.4");
		connection.header("Cache-Control", "max-age=0");
		connection.header("Connection", "keep-alive");
		connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
		connection.timeout(connectionTimeout);
		
		return connection;
	}
}
