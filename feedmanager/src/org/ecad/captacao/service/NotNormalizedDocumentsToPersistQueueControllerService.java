package org.ecad.captacao.service;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;

import org.ecad.captacao.persistence.Document;

@Singleton
public class NotNormalizedDocumentsToPersistQueueControllerService extends AbstractService {

	private Set<String> documents = new HashSet<>();
	
	public void add(Document document) {
		documents.add(document.getUrl());
	}
	
	public void remove(Document document) {
		documents.remove(document.getUrl());
	}
	
	public Boolean hasNotNormalizedDocumentsToPersist() {
		return !documents.isEmpty();
	}

	public void finish() {
		documents.clear();
		documents = new HashSet<>();
	}
}