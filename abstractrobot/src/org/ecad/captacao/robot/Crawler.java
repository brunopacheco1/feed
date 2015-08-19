package org.ecad.captacao.robot;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.ecad.captacao.model.Seed;
import org.ecad.captacao.service.CrawlerService;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Crawler implements MessageListener {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	@Inject
	protected CrawlerService crawlerService;
	
	@Override
    public void onMessage(Message message) {
		crawlerService.addThread(Thread.currentThread().getId());
		
		ObjectMessage objMsg = (ObjectMessage) message;
		
		Seed seed = null;
        try {
        	seed = (Seed) objMsg.getObject();
        } catch (JMSException e) {
            logger.error(e.getMessage(), e);
        }
        
        if(seed == null) {
        	return;
        }
        
        Long time = System.currentTimeMillis();
    	
		runCrawler(seed);
		
		crawlerService.remove(seed);
		
		crawlerService.removeThread(Thread.currentThread().getId());
    	
    	time = System.currentTimeMillis() - time;
		
		logger.info(String.format("Seed[%s - %s/%s] visited in %sms.", seed.getSeedUrl(), seed.getDepth(), seed.getEndDepth(), time));
    	
		if(seed.getDelay() != null) {
	    	try {
				Thread.sleep(seed.getDelay());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
    	
    	crawlerService.finish(seed.getKey(), seed.getManagerUrl());
    }
	
	protected void runCrawler(Seed seed) {
		Connection connection = createBaseConnection(seed.getSeedUrl(), seed.getConnectionTimeout());
		
		Document body = null;
		
		try {
			body = connection.get();
		} catch (Exception e) {
			logger.error(String.format("Seed com problemas de acesso: %s", seed.getSeedUrl()));
			return;
		}
		
		for(Element a : body.select("a")) {
			String link = a.attr("href");
			
			if(link.matches(seed.getDocumentRegex())) {
				crawlerService.addDocument(link);
			}
			
			if(seed.getDepth() < seed.getEndDepth() && (seed.getSeedRegex() == null || link.matches(seed.getSeedRegex()))) {
				Seed newSeed = new Seed(seed.getKey(), seed.getManagerUrl(), link, seed.getDepth() + 1, seed.getEndDepth(), seed.getConnectionTimeout(), seed.getSeedRegex(), seed.getDocumentRegex());
				crawlerService.add(newSeed);
			}
		}
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