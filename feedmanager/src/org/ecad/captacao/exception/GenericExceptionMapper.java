package org.ecad.captacao.exception;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<GenericException> {

	@Override
	public Response toResponse(GenericException t) {
		Map<String, String> response = new HashMap<>();
		
		response.put("error", t.getMessage());
		
		return Response.status(t.getStatus()).entity(response).type(MediaType.APPLICATION_JSON).build();
	}
}