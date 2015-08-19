package org.ecad.captacao.exception;

import javax.ws.rs.core.Response.Status;


public class InvalidAccessException extends GenericException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6963057506068431542L;
	
	public InvalidAccessException(String msg) {
		super(msg, Status.UNAUTHORIZED.getStatusCode());
	}
}