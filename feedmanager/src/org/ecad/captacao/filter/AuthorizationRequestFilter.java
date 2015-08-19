package org.ecad.captacao.filter;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.locator.ServiceLocator;
import org.ecad.captacao.service.AppTokenService;

@Provider
@Priority(2)
public class AuthorizationRequestFilter implements ContainerRequestFilter {

	private AppTokenService tokenService = ServiceLocator.getInstance().lookup(AppTokenService.class);
	
	@Override
	public void filter(ContainerRequestContext requestCtx) {
		String token = requestCtx.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		String robotToken = requestCtx.getHeaderString("Robot");
		
		try {
			if(!requestCtx.getUriInfo().getPath().matches("^\\/user\\/login$")) {
				tokenService.validateToken(token);
			}
		} catch (GenericException e) {
			throw new WebApplicationException(e.getMessage(), Response.Status.UNAUTHORIZED);
		}
		
		if(robotToken != null) {
			try {
				tokenService.discardToken(token);
			} catch (GenericException e) {
				throw new WebApplicationException(e.getMessage(), Response.Status.UNAUTHORIZED);
			}
		}
	}
}