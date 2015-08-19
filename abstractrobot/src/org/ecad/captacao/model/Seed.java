package org.ecad.captacao.model;

import java.io.Serializable;

public class Seed implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6193421118970045744L;
	private String key;
	private String managerUrl;
	private String seedUrl;
	private String documentRegex;
	private String seedRegex;
	private Integer depth = 1;
	private Integer endDepth = 1;
	private Integer connectionTimeout = 10000;
	private Long delay = 0l;
	
	public Seed(){}

	public Seed(String key, String managerUrl, String seedUrl, Integer depth, Integer endDepth, Integer connectionTimeout, String seedRegex, String documentRegex) {
		this.managerUrl = managerUrl;
		this.key = key;
		this.seedUrl = seedUrl;
		this.depth = depth;
		this.endDepth = endDepth;
		this.connectionTimeout = connectionTimeout;
		this.seedRegex = seedRegex;
		this.documentRegex = documentRegex;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getDocumentRegex() {
		return documentRegex;
	}

	public void setDocumentRegex(String documentRegex) {
		this.documentRegex = documentRegex;
	}

	public String getSeedRegex() {
		return seedRegex;
	}

	public void setSeedRegex(String seedRegex) {
		this.seedRegex = seedRegex;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public String getSeedUrl() {
		return seedUrl;
	}

	public void setSeedUrl(String seedUrl) {
		this.seedUrl = seedUrl;
	}

	public String getManagerUrl() {
		return managerUrl;
	}

	public void setManagerUrl(String managerUrl) {
		this.managerUrl = managerUrl;
	}

	public Integer getEndDepth() {
		return endDepth;
	}

	public void setEndDepth(Integer endDepth) {
		this.endDepth = endDepth;
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}