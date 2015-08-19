package org.ecad.captacao.exception;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class ThrowableMapper implements ExceptionMapper<Throwable> {

	protected Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public Response toResponse(Throwable t) {
		Map<String, String> response = new HashMap<>();
		
		response.put("error", "Erro não esperado.");
		
		logger.error(t.getMessage(), t);
		
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON).build();
	}
}