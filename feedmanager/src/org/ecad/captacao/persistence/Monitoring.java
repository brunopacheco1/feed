package org.ecad.captacao.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.commons.io.IOUtils;

import com.google.gson.annotations.Expose;

@Entity
public class Monitoring implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2261046679495770848L;

	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Expose
	private Long id;

	@Expose
	private String ip;
	
	@Expose
	private String token;
	
	@Expose
	private String method;
	
	@Expose
	private String path;

	@Expose
	private Date startDate;
	
	@Expose
	private Date endDate;
	
	@Expose
	private Long executionTime;
	
	@Expose
	private String requestType;
	
	@Lob
	private byte[] requestBody;
	
	@Expose
	private Integer responseStatus;
	
	@Expose
	private String responseType;
	
	@Lob
	private byte[] responseBody;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public String getRequestBody() throws IOException {
		GZIPInputStream responseGzipInputStream = new GZIPInputStream(new ByteArrayInputStream(requestBody));
		String responseBodyStr = IOUtils.toString(responseGzipInputStream);
		
		IOUtils.closeQuietly(responseGzipInputStream);
		
		return responseBodyStr;
	}

	public void setRequestBody(String requestBody) throws IOException {
		ByteArrayOutputStream requestByteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream requestGzipOutputStream = new GZIPOutputStream(requestByteArrayOutputStream);
		requestGzipOutputStream.write(requestBody.getBytes());
		IOUtils.closeQuietly(requestGzipOutputStream);
		
		this.requestBody = requestByteArrayOutputStream.toByteArray();
	}

	public Integer getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(Integer responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getResponseBody() throws IOException {
		GZIPInputStream responseGzipInputStream = new GZIPInputStream(new ByteArrayInputStream(responseBody));
		String responseBodyStr = IOUtils.toString(responseGzipInputStream);
		
		IOUtils.closeQuietly(responseGzipInputStream);
		
		return responseBodyStr;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public void setResponseBody(String responseBody) throws IOException {
		ByteArrayOutputStream responseByteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream responseGzipOutputStream = new GZIPOutputStream(responseByteArrayOutputStream);
		responseGzipOutputStream.write(responseBody.getBytes());
		IOUtils.closeQuietly(responseGzipOutputStream);
		
		this.responseBody = responseByteArrayOutputStream.toByteArray();
	}
}