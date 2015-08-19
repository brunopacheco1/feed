package org.ecad.captacao.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.persistence.Document;
import org.ecad.captacao.persistence.NormalizationStatus;
import org.ecad.captacao.persistence.Robot;
import org.ecad.captacao.service.FeedManagerRobotService;
import org.jboss.resteasy.annotations.GZIP;

@Path("robot")
@Stateless
public class FeedManagerRobotResource extends AbstractResource {

	@Inject
	private FeedManagerRobotService robotService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Robot robot) throws GenericException {
		robotService.add(robot);
		
		return Response.status(Status.OK).entity(gson.toJson(robot)).build();
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") Long id, Robot robot) throws GenericException {
		robotService.update(id, robot);
		
		return Response.status(Status.OK).entity(gson.toJson(robot)).build();
	}

	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") Long id) throws GenericException {
		Robot robot = robotService.get(id);
		return Response.status(Status.OK).entity(gson.toJson(robot)).build();
	}

	@GET
	@GZIP
	public Response list(@DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("100") @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("order") String order, @DefaultValue("asc") @QueryParam("dir") String dir) throws GenericException {
		Map<String, Object> result = new HashMap<>();
		
		List<Robot> robots = robotService.list(start, limit, order, dir);

		result.put("resultSize", robots.size());
		result.put("totalSize", robotService.getCount());
		result.put("result", robots);
		
		return Response.status(Status.OK).entity(gson.toJson(result)).build();
	}

	@DELETE
	@Path("/{id}")
	public Response remove(@PathParam("id") Long id) throws GenericException {
		robotService.remove(id);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("removed", true);
		
		return Response.status(Status.OK).entity(response).build();
	}

	@POST
	@Path("/{id}/run")
	public Response runCrawler(@PathParam("id") Long id) throws GenericException {
		String json = robotService.runCrawler(id);

		return Response.status(Status.OK).entity(json).build();
	}

	@GET
	@Path("/{id}/status")
	public Response status(@PathParam("id") Long id) throws GenericException {
		String json = robotService.status(id);

		return Response.status(Status.OK).entity(json).build();
	}
	
	@GET
	@Path("/{id}/documents")
	@GZIP
	public Response listDocuments(@PathParam("id") Long id, @DefaultValue("NOT_NORMALIZED") @QueryParam("status") NormalizationStatus status, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("100") @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("order") String order, @DefaultValue("asc") @QueryParam("dir") String dir) throws GenericException {
		Map<String, Object> result = new HashMap<>();

		List<Document> documents = robotService.listDocuments(id, status, start, limit, order, dir);
		
		result.put("resultSize", documents.size());
		result.put("totalSize", robotService.getCountDocuments(id, status));
		result.put("result", documents);
		
		return Response.status(Status.OK).entity(gson.toJson(result)).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/documents")
	public Response addDocuments(@PathParam("id") Long id, List<Document> documents) throws GenericException {
		robotService.addDocuments(id, documents);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("added", true);
		
		return Response.status(Status.OK).entity(response).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/documents")
	public Response updateDocuments(@PathParam("id") Long id, List<Document> documents) throws GenericException {
		robotService.updateDocuments(id, documents);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("updated", true);
		
		return Response.status(Status.OK).entity(response).build();
	}
}