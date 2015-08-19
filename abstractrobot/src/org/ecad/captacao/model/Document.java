package org.ecad.captacao.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Document implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6193421118970045744L;
	
	private Long id;
	private String url;
	private String key;
	private String managerUrl;
	private Long delay;
	private Integer connectionTimeout;
	private String status;
	private Date captureDate;
	private Date normalizeDate;
	private Map<String, String> fieldsValues;
	private Map<String, String> fields;
	
	public Document(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String documentUrl) {
		this.url = documentUrl;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCaptureDate() {
		return captureDate;
	}

	public void setCaptureDate(Date captureDate) {
		this.captureDate = captureDate;
	}

	public Date getNormalizeDate() {
		return normalizeDate;
	}

	public void setNormalizeDate(Date normalizeDate) {
		this.normalizeDate = normalizeDate;
	}

	public Map<String, String> getFieldsValues() {
		return fieldsValues;
	}

	public void setFieldsValues(Map<String, String> fieldsValues) {
		this.fieldsValues = fieldsValues;
	}
	
	public void addFieldValue(String fieldName, String fieldValue) {
		if(fieldsValues == null) {
			fieldsValues = new HashMap<String, String>();
		}
		this.fieldsValues.put(fieldName, fieldValue);
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
}