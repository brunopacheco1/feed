package org.ecad.captacao.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.ecad.captacao.model.Document;

@Singleton
@Startup
public class NormalizerExecutionControllerService {

	private Set<Long> runningThreads = new HashSet<>();
	private Set<Long> documentsToNormalize = new HashSet<>();
	private List<Document> normalizedDocuments = new ArrayList<>();

	public void addThread(Long id) {
		runningThreads.add(id);
	}
	
	public void removeThread(Long id) {
		runningThreads.remove(id);
	}
	
	public Boolean hasRunningThreads() {
		return !runningThreads.isEmpty();
	}
	
	public boolean canNormalize(Document document) {
		if (documentsToNormalize.contains(document.getId())) {
			return false;
		}

		return true;
	}
	
	public void add(Document document) {
		documentsToNormalize.add(document.getId());
	}

	public void remove(Document document) {
		documentsToNormalize.remove(document.getId());
		normalizedDocuments.add(document);
	}
	
	public Boolean hasDocuments() {
    	return !documentsToNormalize.isEmpty();
    }
	
	public void finish() {
		documentsToNormalize.clear();
		documentsToNormalize = new HashSet<>();
		normalizedDocuments.clear();
		normalizedDocuments = new ArrayList<>();
    }

	public List<Document> getDocuments() {
		return normalizedDocuments;
	}
}