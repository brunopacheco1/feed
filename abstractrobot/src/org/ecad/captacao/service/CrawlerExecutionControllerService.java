package org.ecad.captacao.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.ecad.captacao.model.Document;
import org.ecad.captacao.model.Seed;

@Singleton
@Startup
public class CrawlerExecutionControllerService extends AbstractService {

	private Set<Long> runningThreads = new HashSet<>();
	private Set<String> visitedSeeds = new HashSet<>();
	private Set<String> documentUrls = new HashSet<>();

	public void addThread(Long id) {
		runningThreads.add(id);
	}
	
	public void removeThread(Long id) {
		runningThreads.remove(id);
	}
	
	public Boolean hasRunningThreads() {
		return !runningThreads.isEmpty();
	}
	
	public boolean canVisitSeed(Seed seed) {
		if (visitedSeeds.contains(seed.getSeedUrl())) {
			return false;
		}

		return true;
	}
	
	public void add(Seed seed) {
		visitedSeeds.add(seed.getSeedUrl());
	}

	public void remove(Seed seed) {
		visitedSeeds.remove(seed.getSeedUrl());
	}

	public Boolean hasSeeds() {
		return !visitedSeeds.isEmpty();
	}

	public void finish() {
		visitedSeeds.clear();
		visitedSeeds = new HashSet<>();
		documentUrls.clear();
		documentUrls = new HashSet<>();
	}

	public void add(String documentUrl) {
		documentUrls.add(documentUrl);
	}
	
	public List<Document> getDocuments() {
		List<Document> documentsList = new ArrayList<>();
		
		for(String url : documentUrls) {
			Document document = new Document();
			document.setUrl(url);
			documentsList.add(document);
		}
		
		return documentsList;
	}
}