package org.ecad.captacao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Documents implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5130251407078850438L;
	
	private String key;
	private String managerUrl;
	private Long delay = 0l;
	private Integer connectionTimeout = 10000;
	private Map<String, String> fields = new HashMap<>();
	
	private List<Document> documents = new ArrayList<>();
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getManagerUrl() {
		return managerUrl;
	}

	public void setManagerUrl(String managerUrl) {
		this.managerUrl = managerUrl;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
}