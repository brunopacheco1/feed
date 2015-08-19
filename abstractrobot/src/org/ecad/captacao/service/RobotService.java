package org.ecad.captacao.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.ecad.captacao.model.Document;
import org.ecad.captacao.model.Documents;
import org.ecad.captacao.model.ExecutionStatus;
import org.ecad.captacao.model.Seed;

@Stateless
public class RobotService {
	
	@Inject
	private NormalizerService normalizerService;
	
	@Inject
	private CrawlerService crawlerService;
	
	public ExecutionStatus runCrawler(Seed seed) {
		ExecutionStatus result = ExecutionStatus.CRAWLER_SCHEDULED;
		
		if(normalizerService.running()) {
			result = ExecutionStatus.NORMALIZING;
		} else if(crawlerService.running()){
			result = ExecutionStatus.CRAWLING;
		} else {
			crawlerService.add(seed);
		}
		
		return result;
	}
	
	public ExecutionStatus runNormalizer(Documents documents) {
		ExecutionStatus result = ExecutionStatus.NORMALIZER_SCHEDULED;
		
		if(normalizerService.running()) {
			result = ExecutionStatus.NORMALIZING;
		} else if(crawlerService.running()){
			result = ExecutionStatus.CRAWLING;
		} else {
			for(Document document : documents.getDocuments()) {
				document.setDelay(documents.getDelay());
				document.setConnectionTimeout(documents.getConnectionTimeout());
				document.setKey(documents.getKey());
				document.setManagerUrl(documents.getManagerUrl());
				document.setFields(documents.getFields());
				normalizerService.add(document);
			}
		}
		
		return result;
	}

	public ExecutionStatus status() {
		ExecutionStatus result = ExecutionStatus.NOT_RUNNING;
		if(crawlerService.running()){
			result = ExecutionStatus.CRAWLING;
		} else if(normalizerService.running()) {
			result = ExecutionStatus.NORMALIZING;
		}
		
		return result;
	}
}