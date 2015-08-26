package org.ecad.captacao.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.locator.ServiceLocator;
import org.ecad.captacao.persistence.Monitoring;
import org.ecad.captacao.service.MonitoringService;
import org.joda.time.DateTime;

@Provider
@Priority(1)
public class MonitoringFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final String REQUEST_TIME = "REQUEST_TIME";
    private static final String REQUEST_BODY = "REQUEST_BODY";
    private static final String REQUEST_ADDRESS = "REQUEST_ADDRESS";
    
    private MonitoringService monitoringService = ServiceLocator.getInstance().lookup(MonitoringService.class);
    
    protected Logger logger = Logger.getLogger(this.getClass().getName());
    
    @Context
	private HttpServletRequest request;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String address = request.getRemoteAddr();
		String body = readBody(requestContext);
		Long time = new DateTime().getMillis();
		
		requestContext.setProperty(REQUEST_TIME, time);
		requestContext.setProperty(REQUEST_BODY, body);
		requestContext.setProperty(REQUEST_ADDRESS, address);
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		Monitoring monitoring = new Monitoring();
		
		String requestBody = (String) requestContext.getProperty(REQUEST_BODY);
		
		if(StringUtils.isEmpty(requestBody)) {
			requestBody = "";
		}

		Long requestTime = (Long) requestContext.getProperty(REQUEST_TIME);
		
		Long responseTime = (Long) new DateTime().getMillis();
		
		Long executionTime = 0l;
		
		if(requestTime != null) {
			executionTime = responseTime - requestTime;
		}
		
		Date startDate = new DateTime(requestTime).toDate();
		
		Date endDate = new DateTime(responseTime).toDate();
		
		MediaType requestType = requestContext.getMediaType();
		
        String ip = (String) requestContext.getProperty(REQUEST_ADDRESS);
        
        if(StringUtils.isEmpty(ip)) {
        	ip = "";
		}
		
		String method = requestContext.getMethod();
		
		String path = requestContext.getUriInfo().getPath();
		
		Integer responseStatus = responseContext.getStatus();
		
		MediaType responseType = responseContext.getMediaType();
		if (responseType != null) {
            String contentType = responseType.toString();
            if(contentType == null){
            	contentType = "application/json";
            }
            
            if (!contentType.contains("charset")) {
                contentType = contentType + ";charset=utf-8";
                responseContext.getHeaders().putSingle("Content-Type", contentType);
            }
        }
		
		String responseBody = "";
		if(responseContext.getEntity() != null) {
			responseBody = responseContext.getEntity() != null ? responseContext.getEntity().toString() : "";
		}
		
		String token = null;
		if(request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
			token = request.getHeader(HttpHeaders.AUTHORIZATION);
		}
		
		if(StringUtils.isEmpty(token)) {
			token = "Não informado";
		}
		
		monitoring.setEndDate(endDate);
		monitoring.setExecutionTime(executionTime);
		monitoring.setIp(ip);
		monitoring.setMethod(method);
		monitoring.setPath(path);
		monitoring.setRequestBody(requestBody);
		
		if(requestType != null) {
			monitoring.setRequestType(requestType.toString());
		}
		
		monitoring.setResponseBody(responseBody);
		monitoring.setResponseStatus(responseStatus);
		if(responseType != null) {
			monitoring.setResponseType(responseType.toString());
		}
		monitoring.setStartDate(startDate);
		monitoring.setToken(token);
		
		try {
			monitoringService.add(monitoring);
		} catch (GenericException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private String readBody(final ContainerRequestContext requestContext) throws IOException {
		String body = IOUtils.toString(requestContext.getEntityStream());
		requestContext.setEntityStream(new ByteArrayInputStream(body.getBytes()));
		
		return body;
	}
}
