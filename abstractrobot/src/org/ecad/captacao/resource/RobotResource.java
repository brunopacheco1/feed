package org.ecad.captacao.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ecad.captacao.model.Documents;
import org.ecad.captacao.model.ExecutionResponse;
import org.ecad.captacao.model.Seed;
import org.ecad.captacao.service.RobotService;

@Stateless
@Path("/robot")
@Produces(MediaType.APPLICATION_JSON)
public class RobotResource {

	@Inject
	private RobotService robotService;
	
	@POST
	@Path("/normalizer/run")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response runNormalizer(Documents documents) {
		ExecutionResponse response = new ExecutionResponse();
		response.setStatus(robotService.runNormalizer(documents));
		return Response.status(Status.OK).entity(response).build();
	}
	
	@POST
	@Path("/crawler/run")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response runCrawler(Seed seed) {
		ExecutionResponse response = new ExecutionResponse();
		response.setStatus(robotService.runCrawler(seed));
		return Response.status(Status.OK).entity(response).build();
	}
	
	@GET
	@Path("/status")
	public Response status() throws JMSException {
		ExecutionResponse response = new ExecutionResponse();
		response.setStatus(robotService.status());
		return Response.status(Status.OK).entity(response).build();
	}
}